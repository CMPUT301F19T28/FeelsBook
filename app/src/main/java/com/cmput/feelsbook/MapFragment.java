package com.cmput.feelsbook;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.cmput.feelsbook.post.Mood;
import com.cmput.feelsbook.post.MoodType;
import com.cmput.feelsbook.post.Post;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import static android.content.ContentValues.TAG;

/**
 * Displays a map with standard map functions.
 * MapView mapView - window used to display the map
 * String MAPVIEW_BUNDLE_KEY - used to uniquely identify a map Bundle object
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, Filterable {

    private MapView mapView;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private Boolean locationPermissionGranted = false;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 15f;
    private ClusterManager clusterManager;
    private ClusterManagerRenderer clusterManagerRenderer;
    private ArrayList<ClusterMarker> clusterMarkers = new ArrayList<>();
    private User currentUser;
    private Boolean firstRun = false;
    private Boolean profile = false;
    private List<Post> feedListFiltered;
    private List<Post> feedList;
    private List<MoodType> moods;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUser = (User) getArguments().getSerializable("user");
            locationPermissionGranted = getArguments().getBoolean("locationPermission");
        }
        this.feedList = new ArrayList<>();
        this.feedListFiltered = new ArrayList<>();
        this.moods = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        mapView = (MapView) view.findViewById(R.id.map_view);

        initGoogleMap(savedInstanceState);
        getDeviceLocation();
        return view;

    }

    /**
     * Initializes the GoogleMap
     */
    private void initGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mapView.onCreate(mapViewBundle);
    }

    /**
     * Retrieves the DeviceLocation and sets the camera to it.
     */
    private void getDeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {
            if (locationPermissionGranted) {
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            try {
                                setCameraView(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                            } catch (NullPointerException n) {
                                Log.e(TAG, "getDeviceLocation: Null Pointer Exception: " + n.getMessage());
                            }
                        }
                        else {
                            Toast.makeText(getActivity(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    /**
     * Moves the camera to the given lat/lon and to the given zoom level.
     * @param latLng
     * @param zoom
     */
    private void setCameraView(LatLng latLng, float zoom) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    /**
     * Initializes clusterManager and clusterManagerRenderer
     */
    public void initClusterManager(){

        if(googleMap != null){

            if(clusterManager == null){
                clusterManager = new ClusterManager<ClusterMarker>(getActivity().getApplicationContext(), googleMap);

            }
            if(clusterManagerRenderer == null){
                clusterManagerRenderer = new ClusterManagerRenderer(
                        getActivity(),
                        googleMap,
                        clusterManager
                );
                clusterManager.setRenderer(clusterManagerRenderer);
                clusterManagerRenderer.setMinClusterSize(2);
                firstRun = true;

            }
            clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<ClusterItem>() {
                        @Override
                        public boolean onClusterClick(final Cluster<ClusterItem> cluster) {
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    cluster.getPosition(), (float) Math.floor(googleMap
                                            .getCameraPosition().zoom + 1)), 300,
                                    null);
                            clusterManager.cluster();
                            return true;
                        }
                    });
            googleMap.setOnMarkerClickListener(clusterManager);
            clusterManager.setOnClusterItemInfoWindowClickListener(
                    new ClusterManager.OnClusterItemInfoWindowClickListener<ClusterMarker>() {
                        @Override public void onClusterItemInfoWindowClick(ClusterMarker clusterMarker) {
                            Intent intent = new Intent(getActivity().getApplicationContext(), ViewMoodActivity.class);
                            Bundle userBundle = new Bundle();
                            userBundle.putSerializable("User", currentUser);
//                userBundle.putBoolean("editMood", true);
                            userBundle.putSerializable("Mood", clusterMarker.getMood());
                            intent.putExtras(userBundle);
                            startActivityForResult(intent, 1);
                        }
                    });
            googleMap.setOnInfoWindowClickListener(clusterManager);
        }
    }

    /**
     * Scans firebase for moods with a location and adds map markers based on Moods.
     */
    public void updateMapMarkers(){
        for (int i = 0; i < feedListFiltered.size(); i++) {
            Mood mood = (Mood) feedListFiltered.get(i);
            if(mood.hasLocation()) {
                LatLng position = new LatLng(mood.getLatitude(), mood.getLongitude());
                String title = mood.getUser();
                String snippet = getString(mood.getMoodType().getEmoticon()) + "@ " + mood.getDateTime().toString();
                Bitmap avatar = Bitmap.createScaledBitmap(mood.profilePicBitmap(), 80, 80, false);
                ClusterMarker clusterMarker = new ClusterMarker(position, title, snippet, avatar, mood);
                clusterManager.addItem(clusterMarker);
                clusterMarkers.add(clusterMarker);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        googleMap = map;
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        if (googleMap != null) {
            googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove() {
                    clusterManager.cluster();
                }
            });
        }

        if (firstRun) {
            googleMap.clear();
            clusterManager.clearItems();
            clusterMarkers.clear();
        }

        initClusterManager();
        updateMapMarkers();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                clusterManager.cluster();
            }
        }, 100);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public List<Post> getFeed() {
        return feedList;
    }

    public void setFeed(List<Post> feed) {
        this.feedList = feed;
    }

    public void addPost(Post post) {
        feedList.add((int) feedList.stream().filter(post1 -> post.getDateTime().compareTo(post1.getDateTime()) > 0).count(), post);
        getFilter().filter(null);

    }

    public void removePost(Post post) {
        feedList.remove(post);
        getFilter().filter(null);
    }

    public void clearMoods() {
        moods.clear();
        getFilter().filter(null);
    }

    public void toggleMoodFilter(MoodType moodType) {
        Optional<MoodType> mood = moods.stream().filter(moodType1 -> moodType1.equals(moodType)).findFirst();
        if(mood.isPresent())
            moods.remove(mood.get());
        else
            moods.add(moodType);

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                if(moods.isEmpty())
                    feedListFiltered = feedList;
                else {
                    List<Post> filtered = new ArrayList<>();
                    for(Post post : feedList) {
                        if(moods.contains(((Mood) post).getMoodType()))
                            filtered.add(post);
                    }
                    feedListFiltered = filtered;
                }
                FilterResults results = new FilterResults();
                results.values = feedListFiltered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                feedListFiltered = (ArrayList<Post>) filterResults.values;
                updateMap();

            }
        };
    }

    public void updateMap() {
        mapView.getMapAsync(this);
    }
    /**
     * Takes in a base64 string and converts it into a bitmap
     * @param photo
     *          photo to be converted in base64 String format format
     * @return
     *      returns bitmap of decoded photo returns null if base64 string was not passed in
     */
    private Bitmap getPhoto(String photo){
        try {
            @SuppressLint("NewApi") byte[] decoded = Base64.getDecoder()
                    .decode(photo);
            return BitmapFactory.decodeByteArray(decoded
                    , 0, decoded.length);
        }catch(Exception e){
            Log.d("-----CONVERT PHOTO-----",
                    "****NO PHOTO CONVERTED: " + e);
            return null;
        }
    }
}