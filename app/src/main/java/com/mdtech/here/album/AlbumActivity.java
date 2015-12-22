/*
 * Copyright (C) 2015 The Here Android Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mdtech.here.album;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.mapbox.mapboxsdk.annotations.SpriteFactory;
import com.mapbox.mapboxsdk.views.MapView;
import com.mdtech.here.Config;
import com.mdtech.here.R;
import com.mdtech.here.geojson.mapbox.GeoJSONOverlay;
import com.mdtech.here.ui.BaseActivity;
import com.mdtech.social.api.HereApi;
import com.mdtech.social.api.model.Album;

import java.math.BigInteger;

import static com.mdtech.here.util.LogUtils.LOGI;
import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/14/2015.
 */
public class AlbumActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = makeLogTag(AlbumActivity.class);

    protected MapView mMapView = null;
    public SpriteFactory mSpriteFactory = null;

    // Identifies a particular Loader being used in this component
    private static final int ALBUM_LOADER = 0;

    private BigInteger mAlbumId;

    private HereApi mApi;

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_album);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Add the back button to the toolbar.
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateUpOrBack(AlbumActivity.this, null);
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // map
        mMapView = (MapView) findViewById(R.id.mapview);
        mMapView.setStyleUrl("asset://styles/amap-satellite-v8.json");
        mMapView.onCreate(savedInstanceState);

        mSpriteFactory = mMapView.getSpriteFactory();

        mAlbumId = getIdFromBundle(savedInstanceState, Config.EXTRA_ALBUM_ID);

        mApi = getApi();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(null != mAlbumId) {
            new AlbumTask(mAlbumId).execute();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause()  {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    protected class AlbumTask extends AsyncTask<Void, Void, Album> {

        private BigInteger mId;

        AlbumTask(BigInteger id) {
            mId = id;
        }

        @Override
        protected Album doInBackground(Void... params) {
            try {
                return mApi.albumOperations().get(mId);
            }catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final Album album) {
            if(null != album ) {
                LOGI(TAG, "Loaded album");
                LOGI(TAG, album.getName());

                if(null != album.getFeatureCollection()) {
                    GeoJSONOverlay geoJSONOverlay = new GeoJSONOverlay(album.getFeatureCollection());
                    geoJSONOverlay.addTo(mMapView);
                }
            }
        }

        @Override
        protected void onCancelled() {
        }
    }
}
