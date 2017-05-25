package com.grp16.itsmap.smapexam.app;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.grp16.itsmap.smapexam.R;
import com.grp16.itsmap.smapexam.model.POI;
import com.grp16.itsmap.smapexam.network.Authentication;
import com.grp16.itsmap.smapexam.network.Database;
import com.grp16.itsmap.smapexam.service.LocationService;
import com.grp16.itsmap.smapexam.util.AppUtil;
import com.grp16.itsmap.smapexam.util.NotificationReceiver;
import com.grp16.itsmap.smapexam.util.PoiListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ARCameraInteraction, SelectTypesInteraction, PoiListener {
    private Authentication authentication;
    private Database database;

    private boolean isServiceBound;
    private ServiceConnection connection = getServiceConnection();
    private LocationService service;
    private NotificationReceiver notificationReceiver;

    private ListView testList;
    private ArrayAdapter<String> adapter;
    private List<String> places = new ArrayList<>();
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, LocationService.class));

        initializeViews();
        //startARCamera(true);
        setupNotificationReceiver();
        authentication = new Authentication();
        database = Database.getInstance();
    }

    private void initializeViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isServiceBound) {
            unbindService(connection);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isServiceBound) {
            Intent intent = new Intent(this, LocationService.class);
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
        requestLocationPermission();
        requestCameraPermission();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            startARCamera(false);
        } else if (id == R.id.nav_settings) {
            startSettings();
        } else if (id == R.id.nav_logout) {
            authentication.logOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else if (id == R.id.nav_exit) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startARCamera(boolean isStartUp) {
        Fragment fragment = ARCameraFragment.newInstance(); //TODO Change fragment type to AR Camera instead of Test
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (isStartUp) {
            transaction.add(R.id.main_fragment_container, fragment);
        } else {
            transaction.replace(R.id.main_fragment_container, fragment);
        }
        transaction.commit();
    }

    private void startSettings() {
        Fragment fragment = SelectTypesFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @NonNull
    private ServiceConnection getServiceConnection() {
        return new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                LocationService.NotificationBinder binder = (LocationService.NotificationBinder) service;
                MainActivity.this.service = binder.getService();
                isServiceBound = true;
                startARCamera(true);
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                isServiceBound = false;
            }
        };
    }

    private void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }

    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void setupNotificationReceiver() {
        notificationReceiver = new NotificationReceiver(this);
        registerReceiver(notificationReceiver, new IntentFilter(AppUtil.BROADCAST_LOCATION_CHANGED));

        // Testing broadcast listeners
        testAddListener();
    }

    // Exposes List from Service to other activities
    @Override
    public List<POI> getPoiList() {
        if (isServiceBound) {
            List<String> types = database.getUserSelectedTypes();
            List<POI> returnList = new ArrayList<>();
            for (String type : types) {
                returnList.addAll(service.getPointsOfInterestList(type));
            }
            return returnList;
        } else {
            return Collections.emptyList();
        }
    }

    // Test to expose location to Fragment
    @Override
    public Location getLocation() {
        if (isServiceBound) {
            return service.getLocation();
        }
        return null;
    }

    // Testing broadcast
    @Override
    public void dataReady(List<POI> data) {
        Toast.makeText(this, "bla bla", Toast.LENGTH_SHORT).show();
        //TODO Do stuff to update View with new items from list
    }

    // Testing broadcast
    private void testAddListener() {
        notificationReceiver.addListener(this);
    }

    @Override
    public void addListener(PoiListener listener) {
        notificationReceiver.addListener(listener);
    }

    @Override
    public void removeListener(PoiListener listener) {
        notificationReceiver.removeListener(listener);
    }
}
