package com.grp16.itsmap.smapexam.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;

public class NotificationService extends Service
        implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    private final IBinder INotificationBinder = new NotificationBinder();
    private static final String TAG = NotificationService.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private GoogleApiClient mGoogleApiClient;

    private Location mLastKnownLocation;

    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    public NotificationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Build the Play services client for use by the Fused Location Provider and the Places API
        // Use addApi() method to request Google Places Api and Fused Location Provider
        // Courtesy of https://developers.google.com/maps/documentation/android-api/current-place-tutorial

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return INotificationBinder;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // throw stuff
        // throw stuff
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastKnownLocation != null) {
            // Do stuff to DTO?
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(15000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    // Returns service to main activity
    public class NotificationBinder extends Binder {
        public NotificationService getService() {
            return NotificationService.this;
        }
    }
}
