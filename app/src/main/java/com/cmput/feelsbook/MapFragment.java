package com.cmput.feelsbook;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cmput.feelsbook.post.Mood;
import com.cmput.feelsbook.post.MoodType;
import com.cmput.feelsbook.post.Post;
import com.cmput.feelsbook.post.SocialSituation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import static android.content.ContentValues.TAG;

/**
 * Displays a map with standard map functions.
 * MapView mapView - window used to display the map
 * String MAPVIEW_BUNDLE_KEY - used to uniquely identify a map Bundle object
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

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
    private CollectionReference cr;
    private FirebaseFirestore db;
    private Boolean firstRun = false;
    private List<Mood> feed;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUser = (User) getArguments().getSerializable("user");
            locationPermissionGranted = getArguments().getBoolean("locationPermission");
        }
        this.feed = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        cr = db.collection("users").document(currentUser.getUserName())
                .collection("Moods");
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
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
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
                            setCameraView(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
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
                    new ClusterManager.OnClusterItemInfoWindowClickListener<ClusterItem>() {
                        @Override public void onClusterItemInfoWindowClick(ClusterItem clusterItem) {
                            Toast.makeText(getContext(), "Clicked info window: make this goto view/edit activity" + firstRun.toString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
            googleMap.setOnInfoWindowClickListener(clusterManager);
        }
    }

    /**
     * Scans firebase for moods with a location and adds map markers based on Moods.
     */
    public void updateMapMarkers(){
        for (int i = 0; i < feed.size(); i++) {
            Mood mood = feed.get(i);
            if(mood.getLocation() != null) {
                LatLng position = new LatLng(mood.getLocation().getLatitude(), mood.getLocation().getLongitude());
                String title = "TODO: Put username here";
                String snippet = getString(mood.getMoodType().getEmoticon()) + "@ " + mood.getDateTime().toString();
                Bitmap avatar = mood.getPhoto();
                ClusterMarker clusterMarker = new ClusterMarker(position, title, snippet, avatar);
                clusterManager.addItem(clusterMarker);
                clusterMarkers.add(clusterMarker);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Toast.makeText(getActivity(), "Map is Ready", Toast.LENGTH_SHORT).show();
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

    public List<Mood> getFeed() {
        return feed;
    }

    public void addPost(Mood mood) {
        feed.add(mood);
    }
}