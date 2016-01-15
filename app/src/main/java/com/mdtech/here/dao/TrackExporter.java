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

package com.mdtech.here.dao;

import android.location.Location;

import com.mdtech.geojson.Feature;
import com.mdtech.geojson.FeatureCollection;
import com.mdtech.geojson.LineString;
import com.mdtech.geojson.Point;
import com.mdtech.geojson.Position;
import com.mdtech.geojson.Properties;
import com.mdtech.here.geojson.TrackProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.mdtech.here.util.LogUtils.LOGE;
import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 1/4/2016.
 */
public class TrackExporter {
    private final static String TAG = makeLogTag(TrackExporter.class);

    protected String name;
    protected String description;
    protected Archiver archiver;

    public TrackExporter(Archiver archiver) {
        this.archiver = archiver;
    }

    public FeatureCollection execute() {
        FeatureCollection featureCollection = new FeatureCollection();

        Properties properties = new Properties();
        properties.stroke = "#FF26C6DA";
        properties.strokeWidth = 8;

        ArchiveMeta meta = this.archiver.getMeta();
        if(null != meta) {
            try {
                properties.setCusts(getTrackProperties(meta));
            } catch (IOException e) {
                LOGE(TAG, e.getMessage());
            }
        }

        Feature feature = makeLineString();
        if(null != feature) {
            feature.setProperties(properties);
            featureCollection.addFeature(feature);
        }

        featureCollection.setProperties(properties);
        return featureCollection;
    }

    /**
     * 设置track属性到geojson的cust properties中
     * @param meta
     */
    private TrackProperties getTrackProperties(ArchiveMeta meta) {
        TrackProperties properties = new TrackProperties();
        properties.id = meta.getName();
        properties.distance = meta.getDistance();
        properties.averageSpeed = meta.getAverageSpeed();
        return properties;
    }

    private Feature makeLineString() {
        List<Location> locations = archiver.fetchAll();
        if(locations.size() == 0) {
            return null;
        }
        if(locations.size() < 2) {
            // 一个点就显示为起点
            Location loc = locations.get(0);
            Point point = new Point(new Position(loc.getLongitude(), loc.getLatitude(), loc.getAltitude()));
            return new Feature(point);
        }
        Iterator<Location> iterator = locations.iterator();
        List<Position> points = new ArrayList<Position>(locations.size());
        Position position;

        while(iterator.hasNext()) {
            Location location = iterator.next();
            position = new Position(location.getLongitude(), location.getLatitude(), location.getAltitude());
            points.add(position);
        }
        LineString lineString = new LineString(points);
        Feature feature = new Feature(lineString);
        return feature;
    }
}
