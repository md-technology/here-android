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

package com.mdtech.geojson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * TODO insert class's header comments
 *
 * Created by Tiven.wang on 12/16/2015.
 */
public class FeatureCollectionTest {

    protected transient final Log log = LogFactory.getLog(getClass());

    @Test
    public void testSerializer() throws JsonProcessingException {
        ObjectMapper serializer = new ObjectMapper();

        Geometry geometry = new Point(12, 13);

        MultiPoint multiPoint = new MultiPoint(new Position(12, 13), new Position(3, 4), new Position(7, 8));

        Feature feature = new Feature(geometry);

        FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.addFeature(feature);

        feature = new Feature(multiPoint);
        featureCollection.addFeature(feature);

        String out = serializer.writeValueAsString(feature);
        log.info(out);

        out = serializer.writeValueAsString(featureCollection);
        log.info(out);
    }

    @Test
    public void testDeserializer() throws IOException {
        ObjectMapper deserializer = new ObjectMapper();
//        GeoJsonModule module = new GeoJsonModule();
//        deserializer.registerModule(module);

        String result = "{\"type\":\"Point\",\"coordinates\":[13.0,12.0,0.0]}";

        Point point = deserializer.readValue(result, Point.class);

        log.info(point.getCoordinates().get(0));
    }

    @Test
    public void testFeatureDeserializer() throws IOException {
        ObjectMapper deserializer = new ObjectMapper();
        String result = "{\"type\":\"Feature\",\"properties\":{\"name\":\"test name\", \"style\":{\"color\":\"green\"}},\"geometry\":{\"type\":\"Point\",\"coordinates\":[13.0,12.0,0.0]}}";

        Feature feature = deserializer.readValue(result, Feature.class);

        log.info(feature.getType());
        log.info(feature.getGeometry().getType());
        log.info(feature.getGeometry().getCoordinates());
        log.info(feature.getProperties().get("name").asText());
        log.info(feature.getProperties().get("style").get("color").asText());
    }

    @Test
    public void testFeatureCollectionDeserializer() throws IOException {
        ObjectMapper deserializer = new ObjectMapper();

        String result = "{\"type\":\"FeatureCollection\", \"features\":[{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[13.0,12.0,0.0]}}]}";

        FeatureCollection featureCollection = deserializer.readValue(result, FeatureCollection.class);

        Assert.assertEquals("something is wrong", "FeatureCollection", featureCollection.getType());
        log.info(featureCollection.getType());
        log.info(featureCollection.getFeatures().get(0).getGeometry().getCoordinates());
    }

    @Test
    public void testMultiPointDeserializer() throws IOException {
        ObjectMapper deserializer = new ObjectMapper();

        String result = "{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"MultiPoint\",\"coordinates\":[[12.0,13.0,0.0],[3.0,4.0,0.0],[7.0,8.0,0.0]]}}";

        Feature feature = deserializer.readValue(result, Feature.class);

        log.info(feature.getType());
        log.info(feature.getGeometry().getCoordinates());
    }

    @Test
    public void testLineStringDeserializer() throws IOException {
        ObjectMapper deserializer = new ObjectMapper();

        String result = "{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[12.0,13.0,0.0],[3.0,4.0,0.0],[7.0,8.0,0.0]]}}";

        Feature feature = deserializer.readValue(result, Feature.class);

        Assert.assertEquals("something is wrong", "LineString", feature.getGeometry().getType());

        log.info(feature.getType());
        log.info(feature.getGeometry().getCoordinates());
    }

    @Test
    public void testMultiLineStringDeserializer() throws IOException {
        ObjectMapper deserializer = new ObjectMapper();

        String result = "{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"MultiLineString\",\"coordinates\":[[[12.0,13.0,0.0],[3.0,4.0,0.0],[7.0,8.0,0.0]],[[1.0,1.0,0.0],[2,2,0.0],[3,3,4]]]}}";

        Feature feature = deserializer.readValue(result, Feature.class);

        Assert.assertEquals("something is wrong", "MultiLineString", feature.getGeometry().getType());

        log.info(feature.getType());
        log.info(feature.getGeometry().getCoordinates());

        ObjectMapper serializer = new ObjectMapper();

        String out = serializer.writeValueAsString(feature);
        log.info(out);
    }

    @Test
    public void testPolygonDeserializer() throws IOException {
        ObjectMapper deserializer = new ObjectMapper();

        String result = "{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[12.0,13.0,0.0],[3.0,4.0,0.0],[7.0,8.0,0.0]],[[1.0,1.0,0.0],[2,2,0.0],[3,3,4]]]}}";

        Feature feature = deserializer.readValue(result, Feature.class);

        Assert.assertEquals("something is wrong", "Polygon", feature.getGeometry().getType());

        log.info(feature.getType());
        log.info(feature.getGeometry().getCoordinates());

        ObjectMapper serializer = new ObjectMapper();

        String out = serializer.writeValueAsString(feature);
        log.info(out);
    }

    @Test
    public void testMultiPolygonDeserializer() throws IOException {
        ObjectMapper deserializer = new ObjectMapper();

        String result = "{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"MultiPolygon\",\"coordinates\":[[[[12.0,13.0,0.0],[3.0,4.0,0.0],[7.0,8.0,0.0]],[[1.0,1.0,0.0],[2,2,0.0],[3,3,4]]],[[[12.0,13.0,0.0],[100,101,0.0],[7.0,8.0,0.0]],[[1.0,1.0,0.0],[90,90,0.0],[3,3,4]]]]}}";

        Feature feature = deserializer.readValue(result, Feature.class);

        Assert.assertEquals("something is wrong", "MultiPolygon", feature.getGeometry().getType());

        log.info(feature.getType());
        log.info(feature.getGeometry().getCoordinates());

        ObjectMapper serializer = new ObjectMapper();

        String out = serializer.writeValueAsString(feature);
        log.info(out);
    }

    @Test
    public void testPropertiesDeserializer() throws IOException {
        ObjectMapper deserializer = new ObjectMapper();
        String result = "{\"type\":\"Feature\",\"properties\":{\"title\":\"test name\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[13.0,12.0,0.0]}}";

        // String -> GeoJSON object
        Feature feature = deserializer.readValue(result, Feature.class);

        // JsonNode -> Properties object
        Properties properties = deserializer.readValue(feature.getProperties().toString(), Properties.class);
        log.info(properties.title);
//
        properties.title = "red";
        // Properties object -> String
        String ps = deserializer.writeValueAsString(properties);
        // String -> JsonNode
        JsonNode jsonNode = deserializer.readValue(ps, JsonNode.class);
        // set back JsonNode to GeoJSON object
        feature.setProperties(jsonNode);
        String fs = deserializer.writeValueAsString(feature);

        log.info(fs);
    }

    @Test
    public void testProperties() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Point p = new Point(11, 12);
        Feature feature = new Feature(p);
        com.mdtech.geojson.Properties properties = new com.mdtech.geojson.Properties();
        properties.title = "test properties title";
        properties.fill = "#FFFFFF";

        feature.setProperties(properties);

        String fs = objectMapper.writeValueAsString(feature);

        log.info(fs);
    }

    @Test
    public void testExtendsProperties() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        TrackProperties properties = new TrackProperties();

        properties.title = "test extends properties";
        properties.distance = 1001L;

        Point p = new Point(11, 12);
        Feature feature = new Feature(p);
        feature.setProperties(properties);

        String fs = objectMapper.writeValueAsString(feature);

        log.info(fs);

        Feature f2 = objectMapper.readValue(fs, Feature.class);
        TrackProperties tp2 = objectMapper.readValue(f2.getProperties().toString(), TrackProperties.class);
        Assert.assertEquals("something is error", properties.distance, tp2.distance);
    }

    public static class TrackProperties extends Properties {
        public Long distance;
    }
}
