package com.grp16.itsmap.smapexam.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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

import com.grp16.itsmap.smapexam.R;
import com.grp16.itsmap.smapexam.model.POI;
import com.grp16.itsmap.smapexam.network.Authentication;
import com.grp16.itsmap.smapexam.network.Database;
import com.grp16.itsmap.smapexam.service.NotificationService;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TestServiceInteraction {
    private Authentication authentication;
    private Database database;

    private boolean isServiceBound;
    private ServiceConnection connection = getServiceConnection();
    private NotificationService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, NotificationService.class));

        initializeViews();
        startARCamera(true);
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
        Intent intent = new Intent(this, NotificationService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
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
        Fragment fragment = TestServiceFragment.newInstance(); //TODO Change fragment type to AR Camera instead of Test
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (isStartUp) {
            transaction.add(R.id.main_fragment_container, fragment);
        } else {
            transaction.replace(R.id.main_fragment_container, fragment);
        }
        transaction.commit();
    }

    private void startSettings() {
        Fragment fragment = SelectTypesFragment.newInstance(database);
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
                NotificationService.NotificationBinder binder = (NotificationService.NotificationBinder) service;
                MainActivity.this.service = binder.getService();
                isServiceBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                isServiceBound = false;
            }
        };
    }

    @Override
    public List<POI> getPois() {
        if (isServiceBound) {
            return service.getPointsOfInterestList("restaurant");
        }
        return Collections.emptyList();
    }
}
