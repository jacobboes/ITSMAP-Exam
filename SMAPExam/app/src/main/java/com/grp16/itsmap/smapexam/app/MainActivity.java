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
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grp16.itsmap.smapexam.R;
import com.grp16.itsmap.smapexam.model.POI;
import com.grp16.itsmap.smapexam.network.Authentication;
import com.grp16.itsmap.smapexam.network.Database;
import com.grp16.itsmap.smapexam.network.DatabaseListener;
import com.grp16.itsmap.smapexam.service.LocationService;
import com.grp16.itsmap.smapexam.util.AppUtil;
import com.grp16.itsmap.smapexam.util.NotificationReceiver;
import com.grp16.itsmap.smapexam.util.PoiListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ARCameraInteraction {
    private Authentication authentication;
    private Database database;
    private boolean isServiceBound;
    private ServiceConnection connection = getServiceConnection();
    private LocationService service;
    private NotificationReceiver notificationReceiver;
    private ListView poiListView;
    private ArrayAdapter adapter;
    private List<POI> poiList  = new ArrayList<>();
    private FloatingActionButton createPoiFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, LocationService.class));
        setupNotificationReceiver();
        initializeViews();
        authentication = new Authentication();
        database = Database.getInstance();
        database.addListener(new DatabaseListener() {
            @Override
            public void dataReady() {
                refreshPoiList();
            }
        });
    }

    private void initializeViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupCreatePoiBtn();
        setupPoiListView();

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        notificationReceiver.addListener(new PoiListener() {
            @Override
            public void dataReady(List<POI> data) {
                refreshPoiList();
            }
        });

        setupLeftNavigationView();
    }

    private void refreshPoiList() {
        poiList.clear();
        poiList.addAll(getPoiList());
        adapter.notifyDataSetChanged();
    }

    private void setupPoiListView() {
        poiListView = (ListView) findViewById(R.id.poi_list_view);
        adapter = new ArrayAdapter<POI>(this, android.R.layout.simple_list_item_2, android.R.id.text1, poiList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(poiList.get(position).name);
                text2.setText(poiList.get(position).vicinity);

                return view;
            }
        };
        poiListView.setAdapter(adapter);
        poiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                startDetailsFragment(position);
                drawer.closeDrawer(GravityCompat.END);
            }
        });
    }

    private void startDetailsFragment(int listPosition) {
        final POI poi = poiList.get(listPosition);
        Fragment fragment = DetailsFragment.newInstance(poi);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.commit();
    }

    private void setupLeftNavigationView() {
        NavigationView leftNavigationView = (NavigationView) findViewById(R.id.left_nav_view);
        leftNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_camera) {
                    startARCamera();
                } else if (id == R.id.nav_settings) {
                    startSettings();
                }else if (id == R.id.nav_myPoi) {
                    startMyPoi();
                } else if (id == R.id.nav_logout) {
                    logout();
                } else if (id == R.id.nav_exit) {
                    finish();
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void logout() {
        authentication.logOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void setupCreatePoiBtn() {
        createPoiFab = (FloatingActionButton) findViewById(R.id.createPoi_fab);
        createPoiFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreatePoiDialog();
            }
        });
    }

    private void showCreatePoiDialog() {
        new CreatePoiDialog(this, this).show();
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
        requestPermissions();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            if (this.findViewById(R.id.activity_ar) == null)
                startARCamera();
        }
    }

    private void startARCamera() {
        Fragment fragment = ARCameraFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.commit();
    }

    private void startSettings() {
        Fragment fragment = SelectTypesFragment.newInstance(database);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void startMyPoi() {
        Fragment fragment = MyPoiFragment.newInstance(database);
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
                startARCamera();
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                isServiceBound = false;
            }
        };
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA}, 1);
        }
    }

    private void setupNotificationReceiver() {
        if (notificationReceiver == null) {
            notificationReceiver = new NotificationReceiver(this);
            registerReceiver(notificationReceiver, new IntentFilter(AppUtil.BROADCAST_LOCATION_CHANGED));
        }
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

    @Override
    public void addListener(PoiListener listener) {
        notificationReceiver.addListener(listener);
    }

    @Override
    public void removeListener(PoiListener listener) {
        notificationReceiver.removeListener(listener);
    }

    @Override
    public int getOrientation() {
        return getWindowManager().getDefaultDisplay().getRotation();
    }
}
