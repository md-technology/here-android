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

package com.mdtech.here.geojson.baidu;

import android.graphics.Color;
import android.os.AsyncTask;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
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
import com.mdtech.here.R;
import com.mdtech.here.geojson.GeoJSONStyle;
import com.mdtech.here.geojson.IGeoJSONOverlay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/11/2015.
 */
public class GeoJSONOverlay extends AsyncTask<Void, Void, GeoJSONObject> implements IGeoJSONOverlay {

    protected BaiduMap map;
    private GeoJSONObject source;

    private List<MarkerOptions> markers = new ArrayList<MarkerOptions>();
    private List<PolygonOptions> polygons = new ArrayList<PolygonOptions>();
    private List<List<PolygonOptions>> multiPolygons = new ArrayList<List<PolygonOptions>>();
    private List<PolylineOptions> polylines = new ArrayList<PolylineOptions>();
    private List<List<PolylineOptions>> multiPolylines = new ArrayList<List<PolylineOptions>>();

    // 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor bdA = BitmapDescriptorFactory
            .fromResource(R.drawable.ic_add_black_24dp);

    // 将GPS设备采集的原始GPS坐标转换成百度坐标
    CoordinateConverter converter  = new CoordinateConverter();

    public GeoJSONOverlay() {
        super();
        // 设置坐标类型
        converter.from(CoordinateConverter.CoordType.GPS);
    }

    public GeoJSONOverlay(FeatureCollection featureCollection) {
        this();
        setSource(featureCollection);
    }

    public void addTo(BaiduMap map) {
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
            addOverlays(markers);
        }
        if(polygons.size() > 0) {
            addOverlays(polygons);
        }

        Iterator<List<PolygonOptions>> polygonIterator = multiPolygons.iterator();
        while (polygonIterator.hasNext()) {
            addOverlays(polygonIterator.next());
        }

        if(polylines.size() > 0) {
            addOverlays(polylines);
        }

        Iterator<List<PolylineOptions>> polylineIterator = multiPolylines.iterator();
        while (polylineIterator.hasNext()) {
            addOverlays(polylineIterator.next());
        }
    }

    private void addOverlays(List list) {
        Iterator<OverlayOptions> iterator = list.iterator();
        while (iterator.hasNext()) {
            map.addOverlay(iterator.next());
        }
    }

    private void addMarker(Position position, GeoJSONStyle style) {
        MarkerOptions options = new MarkerOptions()
                .position(covert(position))
                .icon(bdA);
        options.title(style.getTitle());
//        options.snippet(style.getDescription());
        markers.add(options);
    }

    /**
     * 点转化成坐标对象
     * @param position
     * @return
     */
    private LatLng covert(Position position) {
        return converter.coord(new LatLng(position.getLatitude(), position.getLongitude()))
                .convert();
    }

    public void addPoint(Point point, GeoJSONStyle style) {
        addMarker(point.getPosition(), style);
    }

    private void addLineStringTo(LineString lineString, List<PolylineOptions> polylines, GeoJSONStyle style) {
        List<Position> positions = lineString.getPositions();
        ArrayList<LatLng> points = new ArrayList<LatLng>();
        addPositionsTo(positions, points);
        PolylineOptions options = new PolylineOptions().points(points);
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
            latLngs.add(covert(position));
        }
    }

    private void addPolygonTo(Polygon polygon, List<PolygonOptions> polygons, GeoJSONStyle style) {
        List<Ring> rings = polygon.getRings();
        Iterator<Ring> iterator = rings.iterator();
        ArrayList<LatLng> latLngs;
        while (iterator.hasNext()) {
            latLngs = new ArrayList<>();
            addPositionsTo(iterator.next().getPositions(), latLngs);
            PolygonOptions options = new PolygonOptions().points(latLngs);
            if(null != style.getFill()) {
                options.fillColor(color(style.getFill(), style.getFillOpacity()));
            }
            if (null != style.getStroke()) {
                options.stroke(new Stroke(Integer.parseInt(style.getStrokeWidth()), color(style.getStroke(), style.getStrokeOpacity())));
            }

            polygons.add(options);
        }
    }

    private int color(String color, String opacity) {
        int c = Color.parseColor(color);
        int alpha = 0;
        if(null != opacity) {
            alpha = Math.round(Float.parseFloat(opacity)*255);
        }
        return Color.argb(alpha, Color.red(c), Color.green(c), Color.blue(c));
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
