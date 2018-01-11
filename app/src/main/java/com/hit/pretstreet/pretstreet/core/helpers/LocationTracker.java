package com.hit.pretstreet.pretstreet.core.helpers;

import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.hit.pretstreet.pretstreet.splashnlogin.interfaces.LocCallbackInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 10/01/2018.
 */

public class LocationTracker implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private boolean canGetLocation = false;
    private Location location; // location
    private double latitude = 0; // latitude
    private double longitude = 0; // longitude

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;

    AppCompatActivity activity;
    LocCallbackInterface loctionCallback;

    public LocationTracker(AppCompatActivity appCompatActivity){
        this.activity = appCompatActivity;
        this.loctionCallback = (LocCallbackInterface) appCompatActivity;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(appCompatActivity);
        setupLocationRequest();
    }

    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location mLocation : locationResult.getLocations()) {
                location = mLocation;
                latitude = mLocation.getLatitude();
                longitude = mLocation.getLongitude();
                loctionCallback.setLoc(mLocation);
                Log.i("MainActivity", "//Location: " + location.getLatitude() + " " +
                        location.getLongitude()+locationResult.getLocations().size()+"  ds");
            }
        }
    };

    public void removeUpdates(){
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    public void getMyLocation(){
        if (mGoogleApiClient != null && mFusedLocationClient != null) {
            requestLocationUpdates();
        } else {
            buildGoogleApiClient();
            // requestLocationUpdates();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void setupLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setInterval(30000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /*mLocationRequest.setInterval(10000); // two minute interval
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);*/
    }

    public void requestLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            canGetLocation = true;
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onConnected(Bundle bundle) {
        checkForLocationSettings();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //You can display a message here
    }

    public void checkLocationSettings(){
        try {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
            builder.addLocationRequest(mLocationRequest);
            SettingsClient settingsClient = LocationServices.getSettingsClient(activity);

            settingsClient.checkLocationSettings(builder.build())
                    .addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                            int permissionLocation = ActivityCompat.checkSelfPermission(activity,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION);
                            if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                canGetLocation = true;
                                getMyLocation();
                            } else
                                canGetLocation = false;
                        }
                    })
                    .addOnFailureListener(activity, new OnFailureListener() {
                        @RequiresApi(api = Build.VERSION_CODES.DONUT)
                        @Override
                        public void onFailure(@NonNull final Exception e) {
                            canGetLocation = false;
                        }
                    });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Check for location settings.
    public void checkForLocationSettings() {
        try {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
            builder.addLocationRequest(mLocationRequest);
            SettingsClient settingsClient = LocationServices.getSettingsClient(activity);

            settingsClient.checkLocationSettings(builder.build())
                    .addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                            checkPermissions();
                        }
                    })
                    .addOnFailureListener(activity, new OnFailureListener() {
                        @RequiresApi(api = Build.VERSION_CODES.DONUT)
                        @Override
                        public void onFailure(@NonNull final Exception e) {
                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    showSnackbar("Pretstreet needs permission to access your location", "Ok",
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    // Request permission
                                                    startLocationPermissionRequest(REQUEST_CHECK_SETTINGS_GPS, e);
                                                }
                                            });
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    Toast.makeText(activity, "Settings change is not available.Try in another device.",
                                            Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    showSnackbar("Pretstreet needs permission to access this feature.", "Ok",
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    // Request permission
                                                    startLocationPermissionRequest(REQUEST_CHECK_SETTINGS_GPS, e);
                                                }
                                            });
                                    break;
                            }

                        }
                    });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void checkPermissions(){
        int permissionLocation = ActivityCompat.checkSelfPermission(activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(activity,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                        REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            getMyLocation();
        }
    }

    private void showSnackbar(final String mainTextString, final String actionString,
                              View.OnClickListener listener) {
        Snackbar.make(activity.findViewById(android.R.id.content),
                mainTextString,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(actionString, listener).show();
    }

    private void startLocationPermissionRequest(int requestCode, Exception e) {
        try {
            ResolvableApiException rae = (ResolvableApiException) e;
            rae.startResolutionForResult(activity, requestCode);

        } catch (IntentSender.SendIntentException sie) {
            sie.printStackTrace();
        }
    }


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }


    /**check if location has been changed or not from the previous one
     * @param
     * @param lat1 previous lat
     * @param lng1 previous lng
     * @param lat2 current lat
     * @param lng2  current lng*/

    public static double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }
}
