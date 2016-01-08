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

package com.mdtech.here.geojson;

import android.os.AsyncTask;

import com.baidu.mapapi.map.PolygonOptions;
import com.mdtech.geojson.Feature;
import com.mdtech.geojson.FeatureCollection;
import com.mdtech.geojson.GeoJSONObject;
import com.mdtech.geojson.Geometry;
import com.mdtech.geojson.LineString;
import com.mdtech.geojson.MultiLineString;
import com.mdtech.geojson.MultiPolygon;
import com.mdtech.geojson.Point;
import com.mdtech.geojson.Polygon;
import com.mdtech.geojson.Position;
import com.mdtech.geojson.Properties;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static com.mdtech.here.util.LogUtils.LOGE;
import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 1/7/2016.
 */
public abstract class AbstractGeoJSONOverlay extends AsyncTask<Void, Void, GeoJSONObject> implements IGeoJSONOverlay {
    private static final String TAG = makeLogTag(AbstractGeoJSONOverlay.class);

    protected Callback listener;
    protected GeoJSONObject source;
    protected Class<? extends Properties> propertiesClass = Properties.class;

    public AbstractGeoJSONOverlay() {
    }

    public AbstractGeoJSONOverlay(FeatureCollection featureCollection) {
        this();
        setSource(featureCollection);
    }

    @Override
    public void setSource(GeoJSONObject geoJSONObject) {
        this.source = geoJSONObject;
    }

    @Override
    public void addFeature(Feature feature) {
        addFeature(feature, null);
    }

    @Override
    public void addFeature(Feature feature, Properties properties) {
        Geometry geometry = feature.getGeometry();
        Properties style = null;
        try {
            style = feature.getProperties(propertiesClass);
        } catch (IOException e) {
            LOGE(TAG, e.getMessage());
        }
        if(null == style) {
            style = new Properties();
        }
        if(null != properties) {
            style = extendPropperties(properties, style);
        }

        switch (geometry.getType()) {
            case Geometry.TYPE_POINT:
                addPointFeature((Point)geometry, style);
                break;
            case Geometry.TYPE_MULTI_POINT:
                break;
            case Geometry.TYPE_LINE_STRING:
                addLineString((LineString)geometry, style);
                break;
            case Geometry.TYPE_MULTI_LINE_STRING:
                addMultiLineString((MultiLineString) geometry, style);
                break;
            case Geometry.TYPE_POLYGON:
                addPolygon((Polygon) geometry, style);
                break;
            case Geometry.TYPE_MULTI_POLYGON:
                addMultiPolygon((MultiPolygon) geometry, style);
                break;
        }
    }
    protected abstract void addLineString(LineString lineString, Properties style);
    protected abstract void addMultiLineString(MultiLineString multiLineString, Properties style);
    protected abstract void addPolygon(Polygon polygon, Properties style);
    protected abstract void addMultiPolygon(MultiPolygon multiPolygon, Properties style);
    protected abstract void addMarker(Position position, Properties style);
    protected abstract void addPoint(Position position, Properties style);

    public void addPointFeature(Point point, Properties style) {
        if(style.pointType.equals(Properties.PointType.marker)) {
            addMarker(point.getPosition(), style);
        }else {
            addPoint(point.getPosition(), style);
        }

    }

    public void addFeatureCollection(FeatureCollection featureCollection) {
        Properties properties = null;
        try {
            properties = featureCollection.getProperties(propertiesClass);
        } catch (IOException e) {
            LOGE(TAG, e.getMessage());
        }
        if(properties == null) {
            properties = new Properties();
        }
        properties = extendPropperties(getDefaultStyle(), properties);

        Iterator<Feature> featureIterator = featureCollection.getFeatures().iterator();
        while (featureIterator.hasNext()) {
            addFeature(featureIterator.next(), properties);
        }
    }

    @Override
    public void setCallbackListener(Callback listener) {
        this.listener = listener;
    }

    protected Properties getDefaultStyle() {
        // default style
        Properties style = new Properties();
        style.fill = "blue";
        style.fillOpacity = 0.3f;
        style.stroke = "blue";
        style.strokeOpacity = 0.8f;
        style.strokeWidth = 8;
        return style;
    }

    @Override
    protected GeoJSONObject doInBackground(Void... params) {
        switch (source.getType()) {
            case GeoJSONObject.TYPE_FEATURE_COLLECTION:
                addFeatureCollection((FeatureCollection) source);
                break;
            case GeoJSONObject.TYPE_FEATURE:
                addFeature((Feature) source);
                break;
            default:
        }
        return source;
    }

    @Override
    protected void onPostExecute(GeoJSONObject geoJSONObject) {
        draw();
        if(null != this.listener) {
            this.listener.onPostDrawOverlay(this);
        }
    }

    public Properties extendPropperties(Properties parent, Properties sub) {

        if(null == sub.title) {
            sub.title = parent.title;
        }
        if(null == sub.description) {
            sub.description = parent.description;
        }
        if(null == sub.markerSize) {
            sub.markerSize = parent.markerSize;
        }
        if(null == sub.markerSymbol) {
            sub.markerSymbol = parent.markerSymbol;
        }
        if(null == sub.markerColor) {
            sub.markerColor = parent.markerColor;
        }
        if(null == sub.stroke) {
            sub.stroke = parent.stroke;
        }
        if(null == sub.strokeOpacity) {
            sub.strokeOpacity = parent.strokeOpacity;
        }
        if(null == sub.strokeWidth) {
            sub.strokeWidth = parent.strokeWidth;
        }
        if(null == sub.fill) {
            sub.fill = parent.fill;
        }
        if(null == sub.fillOpacity) {
            sub.fillOpacity = parent.fillOpacity;
        }

        return sub;
    }

    public Class<? extends Properties> getPropertiesClass() {
        return propertiesClass;
    }

    public void setPropertiesClass(Class<? extends Properties> propertiesClass) {
        this.propertiesClass = propertiesClass;
    }
}
