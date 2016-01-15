/*
 * Copyright (C) 2016 The Here Android Project
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.mdtech.geojson.FeatureCollection;
import com.mdtech.here.geojson.IGeoJSONOverlay;
import com.mdtech.here.geojson.baidu.TrackOverlay;
import com.mdtech.here.ui.MapFragment;

import static com.mdtech.here.util.LogUtils.LOGD;
import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 1/4/2016.
 */
public class MapBaiduFragment extends com.baidu.mapapi.map.MapFragment
        implements MapFragment, IGeoJSONOverlay.Callback {
    private static final String TAG = makeLogTag(MapBaiduFragment.class);

    private FeatureCollection mData;
    private TrackOverlay mTrackOverlay;

    MapView mMapView;
    BaiduMap mBaiduMap;

    public static MapBaiduFragment newInstance() {
        MapBaiduFragment mapBaiduFragment = new MapBaiduFragment();
        // TODO
        return mapBaiduFragment;
    }

    public static MapBaiduFragment newInstance(Bundle savedState) {
        MapBaiduFragment fragment = new MapBaiduFragment();
        fragment.setArguments(savedState);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        LOGD(TAG, "Saved state: " + outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the arguments and restore the highlighted room or displayed floor.
        Bundle data = getArguments();
        if(null != data) {
            mData = (FeatureCollection)data.getSerializable(ARG_GEOJSON);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMapView = (MapView)super.onCreateView(inflater, container, savedInstanceState);
//        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
//        ButterKnife.bind(this, mMapView);

        return mMapView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mBaiduMap = mMapView.getMap();
    }

    @Override
    public void addGeoJSON(FeatureCollection geoJSON) {
        mData = geoJSON;
        mTrackOverlay = new TrackOverlay(geoJSON);
        mTrackOverlay.setCallbackListener(this);
        mTrackOverlay.addTo(mMapView.getMap());
    }

    @Override
    public void clear() {
        if(null != mBaiduMap) {
            mBaiduMap.clear();
        }
    }

    @Override
    public void fitBounds() {
    }

    @Override
    public void onPostDrawOverlay(IGeoJSONOverlay overlay) {
        if(null != mBaiduMap && null != mTrackOverlay) {
            mTrackOverlay.fitBounds();
        }
    }

    @Override
    public void onPause() {
        mTrackOverlay = null;
        mBaiduMap = null;
        super.onPause();
    }

}
