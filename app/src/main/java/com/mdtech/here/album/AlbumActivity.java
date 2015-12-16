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

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.mapbox.mapboxsdk.annotations.SpriteFactory;
import com.mapbox.mapboxsdk.views.MapView;
import com.mdtech.here.R;
import com.mdtech.here.geojson.mapbox.GeoJSONOverlay;
import com.mdtech.here.ui.BaseActivity;
import com.mdtech.social.api.Ponmap;
import com.mdtech.social.api.json.Album;

import java.math.BigInteger;

import static com.mdtech.here.util.LogUtils.LOGI;
import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/14/2015.
 */
public class AlbumActivity extends BaseActivity {
    private static final String TAG = makeLogTag(AlbumActivity.class);

    protected MapView mMapView = null;
    public SpriteFactory mSpriteFactory = null;

    public static final String EXTRA_ALBUM_ID =
            "com.mdtech.here.EXTRA_ALBUM_ID";

    // Identifies a particular Loader being used in this component
    private static final int ALBUM_LOADER = 0;

    private BigInteger mAlbumId;

    private Ponmap mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_album);

        // map
        mMapView = (MapView) findViewById(R.id.mapview);
        mMapView.setStyleUrl("asset://styles/amap-satellite-v8.json");
        mMapView.onCreate(savedInstanceState);

        mSpriteFactory = mMapView.getSpriteFactory();

        if (savedInstanceState != null) {
            CharSequence id = savedInstanceState.getCharSequence(EXTRA_ALBUM_ID);
            if(!TextUtils.isEmpty(id)) {
                mAlbumId = new BigInteger(id.toString());
            }

        } else if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            CharSequence id = bundle.getCharSequence(EXTRA_ALBUM_ID);
            if(!TextUtils.isEmpty(id)) {
                mAlbumId = new BigInteger(id.toString());
            }
//            mCurrentUri = getIntent().getData();
        }

        mApi = getApi();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(null != mAlbumId) {
            new AlbumTask(mAlbumId).execute();
        }

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
