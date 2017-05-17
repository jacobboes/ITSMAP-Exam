package com.grp16.itsmap.smapexam.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.grp16.itsmap.smapexam.model.POI;
import com.grp16.itsmap.smapexam.network.Database;
import com.grp16.itsmap.smapexam.util.appUtil;

import java.util.List;
import java.util.concurrent.ExecutionException;


public class NotificationServiceOld extends Service {

    private final IBinder INotificationBinder = new NotificationBinder();
    Database poiDatabase;
    GoogleApiHandler placesApi;
    GoogleApiParam apiParam;
    private final Context mContext;
    private Location location;
    private LocationManager locationManager;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 100; // meters
    private static final long MIN_TIME_BETWEEN_UPDATES = 1000*60*1; // 1 minute
    private List<POI> pointsOfInterestList;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;


    public NotificationServiceOld(Context context) {
        this.mContext = context;
        checkIfLocationAvailable();
    }

    private Location checkIfLocationAvailable() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                Toast.makeText(mContext, "No location Provider available", Toast.LENGTH_SHORT).show();
            } else {
                canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            // Convert to GSON object or whatever

                        }
                    }
                }
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES,locationListener);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            // Convert to GSON object or whatever
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Handle stuff
        }
        return location;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return INotificationBinder;
    }

    public class NotificationBinder extends Binder {
        public NotificationServiceOld getService() {
            return NotificationServiceOld.this;
        }
    }

    public void StopUsingLocation() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            pointsOfInterestList = getPointsOfInterestList();
            if (pointsOfInterestList != null) {
                Intent broadcastPOI = new Intent();
                broadcastPOI.setAction(appUtil.BROADCAST_LOCATION_CHANGED);
                sendBroadcast(broadcastPOI);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public List<POI> getPointsOfInterestList() {
        // Get places from googleApiHandler
        // Get places from database
        // Call async task with location parameters from getlocation
        location = checkIfLocationAvailable();
        appUtil.MY_LOCATION = "";
        appUtil.MY_RADIUS = "";
        appUtil.MY_TYPE = "";
        apiParam = new GoogleApiParam(location.getLatitude(), location.getLongitude(), 500, appUtil.MY_TYPE);

        try {
            pointsOfInterestList = placesApi.execute(apiParam).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }



    }
}
