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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdtech.geojson.FeatureCollection;
import com.mdtech.here.Config;
import com.mdtech.here.R;
import com.mdtech.here.geojson.IGeoJSONOverlay;
import com.mdtech.here.ui.BaseActivity;
import com.mdtech.here.util.SocialAsyncTask;
import com.mdtech.social.api.HereApi;
import com.mdtech.social.api.model.Album;
import com.mdtech.social.api.model.Photo;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Iterator;

import butterknife.Bind;

import static com.mdtech.here.util.LogUtils.LOGD;
import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/14/2015.
 */
public abstract class AlbumActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        IGeoJSONOverlay.Callback{
    private static final String TAG = makeLogTag(AlbumActivity.class);

    // Identifies a particular Loader being used in this component
    private static final int ALBUM_LOADER = 0;

    private BigInteger mAlbumId;
    protected Album mAlbum;

    private HereApi mApi;

    private MapInfoFragment mInfoFragment;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.ib_map_style)
    ImageButton mMapStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApi = getApi();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupAppbar(AlbumActivity.this);

        if (mInfoFragment == null) {
            mInfoFragment = MapInfoFragment.newInstace(this);
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_map_info, mInfoFragment, "mapsheet")
                    .commit();
        }

        mAlbumId = getIdFromBundle(savedInstanceState, Config.EXTRA_ALBUM_ID);

        setMapStyleChangeListener(mMapStyle);

        if(null != mAlbumId) {
            if(mAlbumId.equals(new BigInteger("0"))) {
//                new LocalGeoJSONTask().execute();
            }else {
                new AlbumTask(mAlbumId).execute();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mInfoFragment != null && mInfoFragment.isExpanded()) {
            mInfoFragment.minimize();
        } else {
            super.onBackPressed();
        }
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
//        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        mMapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(Config.EXTRA_ALBUM_ID, mAlbumId.toString());
    }

    protected class AlbumTask extends SocialAsyncTask<Void, Void, Album> {

        private BigInteger mId;

        AlbumTask(BigInteger id) {
            mId = id;
        }

        @Override
        protected Album request(Void... params) {
            return mApi.albumOperations().get(mId);
        }

        @Override
        protected void error(com.mdtech.social.api.Error error) {

        }

        @Override
        protected void onPostExecute(final Album album) {
            if(null != album ) {
                LOGD(TAG, "Loaded album " + album.getName());
                mAlbum = album;

                if(null != album.getFeatureCollection()) {
                    addGeoJSON(album.getFeatureCollection());
                }
                if(null != album.getPhotos()) {
                    Iterator<Photo> iterator = album.getPhotos().iterator();
                    while (iterator.hasNext()) {
                        addPhoto(iterator.next());
                    }
                }
                onPostDrawPhoto();
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

    /**
     * 把GeoJSON数据添加到map成overlay
     * @param geoJSON
     */
    abstract void addGeoJSON(FeatureCollection geoJSON);

    /**
     * 设置change map style按钮的监听响应程序
     * @param ib
     */
    abstract void setMapStyleChangeListener(ImageButton ib);

    /**
     * 往地图上添加图片标注
     * @param photo
     */
    abstract void addPhoto(Photo photo);

    protected class LocalGeoJSONTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            addLocalGeoJSON();
            return true;
        }
    }

    private void addLocalGeoJSON() {
        ObjectMapper deserializer = new ObjectMapper();
        try {
            InputStream inputStream = getAssets().open("aqis.geojson");
            FeatureCollection featureCollection = deserializer.readValue(inputStream, FeatureCollection.class);
            addGeoJSON(featureCollection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void onMapLoaded() {
        if(mAlbumId.equals(new BigInteger("0"))) {
                new LocalGeoJSONTask().execute();
        }
    }

    protected abstract void onPostDrawPhoto();

}
