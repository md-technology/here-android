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

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.mdtech.geojson.FeatureCollection;
import com.mdtech.here.Config;
import com.mdtech.here.R;
import com.mdtech.here.geojson.baidu.GeoJSONOverlay;
import com.mdtech.here.util.CircleTransformation;
import com.mdtech.here.util.SquareTransformation;
import com.mdtech.social.api.model.Photo;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.Serializable;

import uk.co.senab.photoview.PhotoView;

import static com.baidu.mapapi.utils.CoordinateConverter.*;
import static com.mdtech.here.util.LogUtils.LOGD;
import static com.mdtech.here.util.LogUtils.LOGE;
import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/23/2015.
 */
public class AlbumBaiduActivity extends AlbumActivity implements BaiduMap.OnMapLoadedCallback, BaiduMap.OnMarkerClickListener {
    private static final String TAG = makeLogTag(AlbumBaiduActivity.class);

    protected MapView mMapView = null;

    // baidu map
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    BaiduMap mBaiduMap;
    private int mMapType = BaiduMap.MAP_TYPE_NORMAL;
    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    boolean isFirstLoc = true;// 是否首次定位

    // 将GPS设备采集的原始GPS坐标转换成百度坐标
    CoordinateConverter converter  = new CoordinateConverter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_album);

        // map
        mMapView = (MapView) findViewById(R.id.mapview);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setOnMapLoadedCallback(this);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();

        // 设置坐标类型
        converter.from(CoordType.GPS);

        this.mBaiduMap.setOnMarkerClickListener(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mCurrentMode) {
                    case NORMAL:
                        mFab.setImageResource(R.drawable.ic_my_location_white_24dp);
                        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                        mBaiduMap
                                .setMyLocationConfigeration(new MyLocationConfiguration(
                                        mCurrentMode, true, mCurrentMarker));
                        break;
                    case COMPASS:
                        mFab.setImageResource(R.drawable.ic_location_searching_white_24dp);
                        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
                        mBaiduMap
                                .setMyLocationConfigeration(new MyLocationConfiguration(
                                        mCurrentMode, true, mCurrentMarker));

                        break;
                    case FOLLOWING:
                        mFab.setImageResource(R.drawable.ic_near_me_white_24dp);
                        mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
                        mBaiduMap
                                .setMyLocationConfigeration(new MyLocationConfiguration(
                                        mCurrentMode, true, mCurrentMarker));
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    void addGeoJSON(FeatureCollection geoJSON) {
        GeoJSONOverlay geoJSONOverlay = new GeoJSONOverlay(geoJSON);
        geoJSONOverlay.addTo(mMapView.getMap());
    }

    @Override
    void setMapStyleChangeListener(final ImageButton ib) {
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMapType == BaiduMap.MAP_TYPE_NORMAL) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                    ib.setImageResource(R.drawable.ic_satellite_black_24dp);
                    mMapType = BaiduMap.MAP_TYPE_SATELLITE;
                }else {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    ib.setImageResource(R.drawable.ic_map_black_24dp);
                    mMapType = BaiduMap.MAP_TYPE_NORMAL;
                }
            }
        });

    }

    @Override
    void addPhoto(Photo photo) {
        if(null != photo.getLocation()) {
            double[] position = photo.getLocation().getPosition();
            MarkerOptions markerOption = new MarkerOptions();
            // sourceLatLng待转换坐标
            converter.coord(new LatLng(position[1], position[0]));
            markerOption.position(converter.convert());
            markerOption.title(photo.getTitle());
            markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location_black_24dp));

            Marker marker = (Marker)this.mBaiduMap.addOverlay(markerOption);

            PhotoMarker pMarker = new PhotoMarker(marker, photo);
            Bundle extraInfo = new Bundle();
            extraInfo.putSerializable(Config.MARKER_PHOTO, pMarker);
            marker.setExtraInfo(extraInfo);

            // Trigger the download of the URL asynchronously into the image view.
            picasso.load(getUrlFromOssKey(photo.getOssKey(), Config.OSS_STYLE_PREVIEW_SSM)) //
                    .placeholder(R.drawable.ic_my_location_white_24dp) //
                    .resize(getResources().getInteger(R.integer.map_photo_marker_width),
                            getResources().getInteger(R.integer.map_photo_marker_width))
                    .transform(new CircleTransformation())
                    .into(pMarker);
        }


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
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    public void onMapLoaded() {
        super.onMapLoaded();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Bundle bundle = marker.getExtraInfo();
        if(null != bundle) {
            PhotoMarker photoMarker = (PhotoMarker)bundle.getSerializable(Config.MARKER_PHOTO);
            if(null != photoMarker) {
                PhotoViewActivity.openWithPhoto(this, photoMarker.getPhoto().getId());
                return true;
            }
        }

        return false;
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }
        }
    }

    public class PhotoMarker implements Target, Serializable {
        Marker marker;
        Photo photo;

        public PhotoMarker(Marker marker, Photo photo) {
            this.marker = marker;
            this.photo = photo;
        }
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            LOGE(TAG, "Picasso Bitmap failed, photo id is " + getPhoto().getId());
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            LOGD(TAG, "Picasso onPrepareLoad, photo id is " + getPhoto().getId());
        }

        public Photo getPhoto() {
            return photo;
        }
    }
}
