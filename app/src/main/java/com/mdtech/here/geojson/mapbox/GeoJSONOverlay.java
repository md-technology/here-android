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

package com.mdtech.here.geojson.mapbox;

import android.graphics.Color;
import android.os.AsyncTask;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;
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
import com.mdtech.geojson.Ring;
import com.mdtech.here.geojson.GeoJSONStyle;
import com.mdtech.here.geojson.IGeoJSONOverlay;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/11/2015.
 */
public class GeoJSONOverlay extends AsyncTask<Void, Void, GeoJSONObject> implements IGeoJSONOverlay {

    protected MapView map;
    private GeoJSONObject source;

    private List<MarkerOptions> markers = new ArrayList<MarkerOptions>();
    private List<PolygonOptions> polygons = new ArrayList<PolygonOptions>();
    private List<List<PolygonOptions>> multiPolygons = new ArrayList<List<PolygonOptions>>();
    private List<PolylineOptions> polylines = new ArrayList<PolylineOptions>();
    private List<List<PolylineOptions>> multiPolylines = new ArrayList<List<PolylineOptions>>();

    public GeoJSONOverlay() {
        super();
    }

    public GeoJSONOverlay(FeatureCollection featureCollection) {
        super();
        setSource(featureCollection);
    }

    public void addTo(MapView map) {
        this.map = map;
        this.execute();
    }

    @Override
    public void addTo() {
        addTo(map);
    }

    @Override
    public void setSource(String geoJSON) {

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
    public void addFeature(Feature feature, GeoJSONStyle geoJSONStyle) {
        Geometry geometry = feature.getGeometry();
        GeoJSONStyle style;
        if(null != geoJSONStyle) {
            style = geoJSONStyle.extend(feature.getProperties());
        }else {
            style = new GeoJSONStyle(feature.getProperties());
        }

        switch (geometry.getType()) {
            case Geometry.TYPE_POINT:
                addPoint((Point)geometry, style);
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

    @Override
    public void draw() {
        if(markers.size() > 0) {
            map.addMarkers(markers);
        }
        if(polygons.size() > 0) {
            map.addPolygons(polygons);
        }

        Iterator<List<PolygonOptions>> polygonIterator = multiPolygons.iterator();
        while (polygonIterator.hasNext()) {
            map.addPolygons(polygonIterator.next());
        }

        if(polylines.size() > 0) {
            map.addPolylines(polylines);
        }

        Iterator<List<PolylineOptions>> polylineIterator = multiPolylines.iterator();
        while (polylineIterator.hasNext()) {
            map.addPolylines(polylineIterator.next());
        }
    }

    private void addMarker(Position position, GeoJSONStyle style) {
        position.getLatitude();
        MarkerOptions options = new MarkerOptions()
                .position(new LatLng(position.getLatitude(), position.getLongitude()));
        options.title(style.getTitle());
        options.snippet(style.getDescription());
        markers.add(options);
    }

    public void addPoint(Point point, GeoJSONStyle style) {
        addMarker(point.getPosition(), style);
    }

    private void addLineStringTo(LineString lineString, List<PolylineOptions> polylines, GeoJSONStyle style) {
        List<Position> positions = lineString.getPositions();
        Iterator<Position> iterator = positions.iterator();
        ArrayList<LatLng> points = new ArrayList<LatLng>();
        addPositionsTo(positions, points);
        PolylineOptions options = new PolylineOptions().addAll(points);
        if(null != style.getStroke()) {
            options.color(Color.parseColor(style.getStroke()));
        }
        polylines.add(options); //TODO style
    }

    public void addLineString(LineString lineString, GeoJSONStyle style) {
        addLineStringTo(lineString, polylines, style);
    }

    public void addMultiLineString(MultiLineString multiLineString, GeoJSONStyle style) {
        List<LineString> lineStrings =  multiLineString.getLineStrings();
        Iterator<LineString> iterator = lineStrings.iterator();
        List<PolylineOptions> polylines;
        while (iterator.hasNext()) {
            polylines = new ArrayList<PolylineOptions>();
            addLineStringTo(iterator.next(), polylines, style);
            multiPolylines.add(polylines); // TODO style
        }
    }

    private void addPositionsTo(List<Position> positions, List<LatLng> latLngs) {
        Iterator<Position> iterator = positions.iterator();
        Position position;
        while (iterator.hasNext()) {
            position = iterator.next();
            latLngs.add(new LatLng(position.getLatitude(), position.getLongitude()));
        }
    }

    private void addPolygonTo(Polygon polygon, List<PolygonOptions> polygons, GeoJSONStyle style) {
        List<Ring> rings = polygon.getRings();
        Iterator<Ring> iterator = rings.iterator();
        ArrayList<LatLng> latLngs;
        while (iterator.hasNext()) {
            latLngs = new ArrayList<>();
            addPositionsTo(iterator.next().getPositions(), latLngs);
            PolygonOptions options = new PolygonOptions().addAll(latLngs);
            if(null != style.getFill()) {
                options.fillColor(Color.parseColor(style.getFill()));
            }
            if(null != style.getStroke()) {
                options.strokeColor(Color.parseColor(style.getStroke()));
            }
            if(null != style.getFillOpacity()) {
                options.alpha(Float.parseFloat(style.getFillOpacity()));
            }
            polygons.add(options);
        }
    }

    public void addPolygon(Polygon polygon, GeoJSONStyle style) {
        addPolygonTo(polygon, polygons, style);
    }

    public void addMultiPolygon(MultiPolygon multiPolygon, GeoJSONStyle style) {
        List<PolygonOptions> polygons;
        Iterator<Polygon> iterator = multiPolygon.getPolygons().iterator();
        while (iterator.hasNext()) {
            polygons = new ArrayList<PolygonOptions>();
            addPolygonTo(iterator.next(), polygons, style);
            multiPolygons.add(polygons);
        }
    }

    private GeoJSONStyle getDefaultStyle() {
        // default style
        GeoJSONStyle style = new GeoJSONStyle();
        style.setFill("blue");
        style.setFillOpacity("0.3");
        style.setStroke("blue");
        style.setStrokeOpacity("0.8");
        style.setStrokeWidth("2");
        return style;
    }

    public void addFeatureCollection(FeatureCollection featureCollection) {
        GeoJSONStyle style = getDefaultStyle().extend(featureCollection.getProperties());
        Iterator<Feature> featureIterator = featureCollection.getFeatures().iterator();
        while (featureIterator.hasNext()) {
            addFeature(featureIterator.next(), style);
        }
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
    }
}
