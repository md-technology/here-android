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

package com.mdtech.here.geojson.baidu;

import android.text.TextUtils;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.mdtech.geojson.Feature;
import com.mdtech.geojson.FeatureCollection;
import com.mdtech.geojson.Geometry;
import com.mdtech.geojson.LineString;
import com.mdtech.geojson.MultiLineString;
import com.mdtech.geojson.MultiPolygon;
import com.mdtech.geojson.Polygon;
import com.mdtech.geojson.Position;
import com.mdtech.geojson.Properties;
import com.mdtech.here.geojson.AbstractGeoJSONOverlay;
import com.mdtech.here.geojson.TrackProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.mdtech.here.util.LogUtils.LOGE;
import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 1/15/2016.
 */
public class TrackOverlay extends AbstractBaiduOverlay {
    private static final String TAG = makeLogTag(TrackOverlay.class);

    private Map<String, PolylineOptions> tracks = new HashMap<String, PolylineOptions>();

    public TrackOverlay(FeatureCollection fc) {
        setSource(fc);
    }

    @Override
    public void addFeature(Feature feature, Properties properties) {
        TrackProperties trackProperties = null;
        if(feature.getProperties() != null) {
            try {
                trackProperties = feature.getProperties().getCusts(TrackProperties.class);
            } catch (IOException e) {
                LOGE(TAG, e.getMessage());
            }
        }

        if(trackProperties == null || TextUtils.isEmpty(trackProperties.id)) {
            LOGE(TAG, "Track's id unknown");
            return;
        }

        if(Geometry.TYPE_LINE_STRING.equals(feature.getGeometry().getType())) {
            PolylineOptions polylineOptions = tracks.get(trackProperties.id);
            List<Position> positions = ((LineString)feature.getGeometry()).getPositions();
            ArrayList<LatLng> points = new ArrayList<LatLng>();
            addPositionsTo(positions, points);
            if(null == polylineOptions) {
                polylineOptions = new PolylineOptions().points(points);
                if(null != properties.stroke) {
                    polylineOptions.color(color(properties.stroke, properties.strokeOpacity));
                }
                if(null != properties.strokeWidth) {
                    polylineOptions.width(properties.strokeWidth);
                }
                tracks.put(trackProperties.id, polylineOptions);
            }else {
                polylineOptions.points(points);
            }
            map.addOverlay(polylineOptions);
            addToBounds(points);
        }
    }

    @Override
    protected void addLineString(LineString lineString, Properties style) {

    }

    @Override
    protected void addMultiLineString(MultiLineString multiLineString, Properties style) {

    }

    @Override
    protected void addPolygon(Polygon polygon, Properties style) {

    }

    @Override
    protected void addMultiPolygon(MultiPolygon multiPolygon, Properties style) {

    }

    @Override
    protected void addMarker(Position position, Properties style) {

    }

    @Override
    protected void addPoint(Position position, Properties style) {

    }

    @Override
    public void draw() {

    }
}
