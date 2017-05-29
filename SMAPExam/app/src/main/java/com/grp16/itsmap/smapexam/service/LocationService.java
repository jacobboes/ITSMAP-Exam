package com.grp16.itsmap.smapexam.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.grp16.itsmap.smapexam.model.POI;
import com.grp16.itsmap.smapexam.network.Database;
import com.grp16.itsmap.smapexam.util.AppUtil;
import com.grp16.itsmap.smapexam.util.Notification;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class LocationService extends Service {

    private final IBinder INotificationBinder = new NotificationBinder();
    Database poiDatabase;
    LocationParam locationParam;
    Notification notification;
    private Location location;
    private LocationManager locationManager;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 100; // meters
    private static final long MIN_TIME_BETWEEN_UPDATES = 1; // 1 minute //TODO set update time
    private List<POI> pointsOfInterestList;

    boolean isGPSEnabled = false;
    boolean canGetLocation = false;


    public LocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        poiDatabase = Database.getInstance();
        notification = new Notification(this);
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!isGPSEnabled) {
                Toast.makeText(this, "No location provider available", Toast.LENGTH_SHORT).show();
            } else {
                if (locationManager != null) {
                    canGetLocation = true;
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                }
            }
        } catch (Exception e) {
            Log.e("Placeholder", "Error while initializing LocationUpdates", e);
        }
    }


    private Location checkIfLocationAvailable() {
        try {
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    return location;
                }
            }
        } catch (Exception e) {
            Log.e("Placeholder", "Error while receiving last known location", e);
        }
        return null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return INotificationBinder;
    }

    public class NotificationBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    public void StopGettingLocationUpdates() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    public Location getLocation() {
        return checkIfLocationAvailable();
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (pointsOfInterestList != null) {
                notification.Send("Location changed", "New points of interest available");
                Intent broadcastPOI = new Intent();
                broadcastPOI.setAction(AppUtil.BROADCAST_LOCATION_CHANGED);
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

    public List<POI> getPointsOfInterestList(String type) {
        location = checkIfLocationAvailable();
        pointsOfInterestList = new ArrayList<>();
        if (location != null) {
            locationParam = new LocationParam(location.getLatitude(), location.getLongitude(), AppUtil.MY_RADIUS, type);

            try {
                pointsOfInterestList = new GoogleApiHandler().execute(locationParam).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            List<POI> tempList;
            tempList = poiDatabase.getPOI(locationParam);

            pointsOfInterestList.addAll(tempList);

            return pointsOfInterestList;
        }
        return pointsOfInterestList;
    }

    private void startupBroadcast() {
        Intent broadcastPOI = new Intent();
        broadcastPOI.setAction(AppUtil.BROADCAST_LOCATION_CHANGED);
        sendBroadcast(broadcastPOI);
    }
}
