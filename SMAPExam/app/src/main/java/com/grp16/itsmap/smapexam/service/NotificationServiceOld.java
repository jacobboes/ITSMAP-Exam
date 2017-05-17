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
    LocationParam locationParam;
    private final Context mContext;
    private Location location;
    private LocationManager locationManager;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 100; // meters
    private static final long MIN_TIME_BETWEEN_UPDATES = 1000*60*1; // 1 minute
    private List<POI> pointsOfInterestList;

    boolean isGPSEnabled = false;
    boolean canGetLocation = false;


    public NotificationServiceOld(Context context) {
        this.mContext = context;
        checkIfLocationAvailable();
    }

    private Location checkIfLocationAvailable() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!isGPSEnabled) {
                Toast.makeText(mContext, "No location Provider available", Toast.LENGTH_SHORT).show();
            } else {
                canGetLocation = true;
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES,locationListener);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            return location;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Handle stuff
        }
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

    public Location GetLocation() {
        return checkIfLocationAvailable();
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

    public List<POI> getPointsOfInterestList(String type) {
        location = checkIfLocationAvailable();
        locationParam = new LocationParam(location.getLatitude(), location.getLongitude(), appUtil.MY_RADIUS, type);

        try {
            pointsOfInterestList = placesApi.execute(locationParam).get();
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
}
