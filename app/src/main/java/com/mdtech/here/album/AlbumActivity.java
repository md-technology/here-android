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

import android.os.Bundle;

import com.mapbox.mapboxsdk.annotations.SpriteFactory;
import com.mapbox.mapboxsdk.views.MapView;
import com.mdtech.here.R;
import com.mdtech.here.ui.BaseActivity;

import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/14/2015.
 */
public class AlbumActivity extends BaseActivity {
    private static final String TAG = makeLogTag(AlbumActivity.class);

    protected MapView mMapView = null;
    public SpriteFactory mSpriteFactory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_album);

        //map
        mMapView = (MapView) findViewById(R.id.mapview);
        mMapView.setStyleUrl("asset://styles/amap-satellite-v8.json");
        mMapView.onCreate(savedInstanceState);

        mSpriteFactory = mMapView.getSpriteFactory();
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
}
