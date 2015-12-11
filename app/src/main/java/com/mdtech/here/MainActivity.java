package com.mdtech.here;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.annotations.Sprite;
import com.mapbox.mapboxsdk.annotations.SpriteFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngZoom;
import com.mapbox.mapboxsdk.views.MapView;
import com.mdtech.here.geojson.mapbox.GeoJSONOverlay;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private MapView mapView = null;
    public SpriteFactory spriteFactory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Log.d(LOG_TAG, "mapView location:");
                Log.d(LOG_TAG, mapView.fromScreenLocation(new PointF(0, mapView.getRootView().getHeight())).toString());
                Log.d(LOG_TAG, mapView.fromScreenLocation(new PointF(mapView.getRootView().getMeasuredWidth(), 0)).toString());

                // set user's location
                mapView.setCenterCoordinate(new LatLng(mapView.getMyLocation()));

                // add a demo marker
                mapView.addMarker(new MarkerOptions()
                        .position(new LatLng(32.3, 121))
                        .title("Test add marker")
                        .icon(spriteFactory.fromResource(R.drawable.dic_launcher)));

                // draw a GeoJSON overlay
//                new DrawGeoJSON().execute();

                // Load GeoJSON file
                try {
                    InputStream inputStream = getAssets().open("countries.geo.json");
                    GeoJSONOverlay geoJSONOverlay = new GeoJSONOverlay(inputStream);
                    geoJSONOverlay.addTo(mapView);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //map
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setStyleUrl("asset://styles/amap-satellite-v8.json");
//        mapView.setStyleUrl("asset://styles/osm-water.json");
//        mapView.getSpriteFactory()
//        mapView.setStyleUrl(Style.MAPBOX_STREETS);
        mapView.setCenterCoordinate(new LatLng(40.73581, -1.99155));
        mapView.setZoomLevel(5);
        mapView.onCreate(savedInstanceState);

        spriteFactory = mapView.getSpriteFactory();


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_map_style_amap_satellite:
                mapView.setStyleUrl("asset://styles/amap-satellite-v8.json");
                return true;
            case R.id.action_map_style_osm_water:
                mapView.setStyleUrl("asset://styles/osm-water.json");
                return true;
            case R.id.action_map_style_dark:
                mapView.setStyle(Style.DARK);
                return true;
            case R.id.action_map_style_emerald:
                mapView.setStyle(Style.EMERALD);
                return true;
            case R.id.action_map_style_light:
                mapView.setStyle(Style.LIGHT);
                return true;
            case R.id.action_map_style_mapbox_streets:
                mapView.setStyle(Style.MAPBOX_STREETS);
                return true;
            case R.id.action_map_style_satellite:
                mapView.setStyle(Style.SATELLITE);
                return true;
            case R.id.action_map_style_satellite_streets:
                mapView.setStyle(Style.SATELLITE_STREETS);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            mapView.removeAllAnnotations();

        } else if (id == R.id.nav_share) {
            try {
                InputStream inputStream = getAssets().open("china.geo.json");
                GeoJSONOverlay geoJSONOverlay = new GeoJSONOverlay(inputStream);
                geoJSONOverlay.addTo(mapView);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_send) {
            try {
                InputStream inputStream = getAssets().open("countries.geo.json");
                GeoJSONOverlay geoJSONOverlay = new GeoJSONOverlay(inputStream);
                geoJSONOverlay.addTo(mapView);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause()  {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

}
