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
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
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
import com.mdtech.geojson.Properties;
import com.mdtech.geojson.Ring;
import com.mdtech.here.R;
import com.mdtech.here.geojson.AbstractGeoJSONOverlay;
import com.mdtech.here.geojson.IGeoJSONOverlay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/11/2015.
 */
public class GeoJSONOverlay extends AbstractGeoJSONOverlay {
    private static final String TAG = makeLogTag(GeoJSONOverlay.class);

    protected BaiduMap map;

    private List<DotOptions> dots = new ArrayList<DotOptions>();
    private List<MarkerOptions> markers = new ArrayList<MarkerOptions>();
    private List<PolygonOptions> polygons = new ArrayList<PolygonOptions>();
    private List<List<PolygonOptions>> multiPolygons = new ArrayList<List<PolygonOptions>>();
    private List<PolylineOptions> polylines = new ArrayList<PolylineOptions>();
    private List<List<PolylineOptions>> multiPolylines = new ArrayList<List<PolylineOptions>>();

    // bounds
    private LatLngBounds.Builder boundsBuilder;

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

    public GeoJSONOverlay(FeatureCollection geoJSON) {
        super(geoJSON);
        // 设置坐标类型
        converter.from(CoordinateConverter.CoordType.GPS);
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
    public void draw() {
        if(dots.size() > 0) {
            addOverlays(dots);
        }
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

    @Override
    public void fitBounds() {
        if(null != boundsBuilder && null != map) {
            map.animateMapStatus(MapStatusUpdateFactory.newLatLngBounds(boundsBuilder.build()));
        }
    }

    @Override
    public void fitBounds(Position sw, Position ne) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(sw.getLatitude(), sw.getLongitude()));
        builder.include(new LatLng(ne.getLatitude(), ne.getLongitude()));

        map.animateMapStatus(MapStatusUpdateFactory.newLatLngBounds(builder.build()));
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

    private void addOverlays(List list) {
        Iterator<OverlayOptions> iterator = list.iterator();
        while (iterator.hasNext()) {
            map.addOverlay(iterator.next());
        }
    }

    @Override
    protected void addMarker(Position position, Properties style) {
        MarkerOptions options = new MarkerOptions()
                .position(covert(position))
                .icon(bdA);
        options.title(style.title);
//        options.snippet(style.getDescription());
        markers.add(options);

        addToBounds(covert(position));
    }

    @Override
    protected void addPoint(Position position, Properties style) {
        DotOptions dotOptions = new DotOptions()
                .center(covert(position)).color(color(style.stroke, style.strokeOpacity))
                .radius(style.strokeWidth);

        dots.add(dotOptions);

        addToBounds(covert(position));
    }

    private void addLineStringTo(LineString lineString, List<PolylineOptions> polylines, Properties style) {
        List<Position> positions = lineString.getPositions();
        ArrayList<LatLng> points = new ArrayList<LatLng>();
        addPositionsTo(positions, points);
        PolylineOptions options = new PolylineOptions().points(points);
        if(null != style.stroke) {
            options.color(color(style.stroke, style.strokeOpacity));
        }
        if(null != style.strokeWidth) {
            options.width(style.strokeWidth);
        }

        polylines.add(options);

        addToBounds(points);
    }

    @Override
    public void addLineString(LineString lineString, Properties style) {
        addLineStringTo(lineString, polylines, style);
    }

    @Override
    public void addMultiLineString(MultiLineString multiLineString, Properties style) {
        List<LineString> lineStrings =  multiLineString.getLineStrings();
        Iterator<LineString> iterator = lineStrings.iterator();
        List<PolylineOptions> polylines;
        while (iterator.hasNext()) {
            polylines = new ArrayList<PolylineOptions>();
            addLineStringTo(iterator.next(), polylines, style);
            multiPolylines.add(polylines);
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

    private void addPolygonTo(Polygon polygon, List<PolygonOptions> polygons, Properties style) {
        List<Ring> rings = polygon.getRings();
        Iterator<Ring> iterator = rings.iterator();
        ArrayList<LatLng> latLngs;
        while (iterator.hasNext()) {
            latLngs = new ArrayList<>();
            addPositionsTo(iterator.next().getPositions(), latLngs);
            PolygonOptions options = new PolygonOptions().points(latLngs);
            if(null != style.fill) {
                options.fillColor(color(style.fill, style.fillOpacity));
            }
            if (null != style.stroke) {
                options.stroke(new Stroke(style.strokeWidth, color(style.stroke, style.strokeOpacity)));
            }

            polygons.add(options);

            addToBounds(latLngs);
        }
    }

    private int color(String color, Float opacity) {
        int c = Color.parseColor(color);
        int alpha = 0;
        if(null != opacity) {
            alpha = Math.round(opacity*255);
        }
        return Color.argb(alpha, Color.red(c), Color.green(c), Color.blue(c));
    }

    public void addPolygon(Polygon polygon, Properties style) {
        addPolygonTo(polygon, polygons, style);
    }

    public void addMultiPolygon(MultiPolygon multiPolygon, Properties style) {
        List<PolygonOptions> polygons;
        Iterator<Polygon> iterator = multiPolygon.getPolygons().iterator();
        while (iterator.hasNext()) {
            polygons = new ArrayList<PolygonOptions>();
            addPolygonTo(iterator.next(), polygons, style);
            multiPolygons.add(polygons);
        }
    }

    private void addToBounds(LatLng latLng) {
        if(null == boundsBuilder) {
            boundsBuilder = new LatLngBounds.Builder();
        }
        boundsBuilder.include(latLng);
    }

    private void addToBounds(List<LatLng> latLngs) {
        Iterator<LatLng> iterator = latLngs.iterator();
        while (iterator.hasNext()) {
            addToBounds(iterator.next());
        }
    }

    public LatLngBounds.Builder getBoundsBuilder() {
        return boundsBuilder;
    }

    public void setBoundsBuilder(LatLngBounds.Builder boundsBuilder) {
        this.boundsBuilder = boundsBuilder;
    }
}
