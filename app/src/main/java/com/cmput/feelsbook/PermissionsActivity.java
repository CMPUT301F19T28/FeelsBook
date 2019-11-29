package com.cmput.feelsbook;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import static android.content.ContentValues.TAG;

/**
 * activity that confirms permission access for Location used in conjunction with posts
 */

public class PermissionsActivity extends AppCompatActivity {
    //Location permission vars

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    private Boolean locationPermissionGranted = false;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentUser = (User) bundle.get("User");
        }

    }

    /**
     * Prepare the MainActivity args and start it.
     */
    private void finishPermissions() {
        finish();
        Intent intent = new Intent(PermissionsActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("User", currentUser);
        bundle.putBoolean("locationPermission", locationPermissionGranted);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (checkMapServices()) {
            if (locationPermissionGranted) {
                finishPermissions();
            }
            else {
                getLocationPermission();
            }
        }
    }

    /**
     * Checks if Google Services and if GPS is enabled.
     * @return
     * Returns true if Google Services and GPS is enabled, false otherwise
     */
    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    /**
     * Prompt the user with a dialogue to enable GPS.
     */
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Check if GPS is enabled on the device. If it isn't display a prompt helping the user enable it.
     * @return
     * Returns true if GPS is enabled and false if it is not
     */
    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    /**
     * Explicitly requests location permission.
     */
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            finishPermissions();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Determine whether or not the device is able to use Google Services
     * @return
     * Returns true if services is available false if not
     */
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(PermissionsActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(PermissionsActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     * On result of permissions request, if result is permission granted change locationPermissionGranted to true, else finish the PermissionsActivity
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                } else {
                    finishPermissions();
                }
            }
        }
    }

    /**
     * On result of GPS request, if GPS is enabled and location permission is granted finish PermissionsActivity.
     * Else get location permission.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(locationPermissionGranted){
                    finishPermissions();
                }
                else{
                    getLocationPermission();
                }
            }
        }
    }
}
