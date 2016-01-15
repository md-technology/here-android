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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/16/2015.
 */
public class GeoJsonModule extends SimpleModule {

    public GeoJsonModule() {
        this.addDeserializer(Point.class, new GeoJsonModule.PointDeserializer());
    }

    public static class PointDeserializer extends GeoJsonModule.GeoJsonDeserializer<Point> {
        private PointDeserializer() {
            super();
        }

        protected Point doDeserialize(ArrayNode coordinates) {
            return this.toPoint(coordinates);
        }
    }

    public static class MultiPointDeserializer extends GeoJsonModule.GeoJsonDeserializer<MultiPoint> {
        private MultiPointDeserializer() {
            super();
        }

        protected MultiPoint doDeserialize(ArrayNode coordinates) {
            return new MultiPoint(this.toPositions(coordinates));
        }
    }

    public static class LineStringDeserializer extends GeoJsonModule.GeoJsonDeserializer<LineString> {
        private LineStringDeserializer() {
            super();
        }

        protected LineString doDeserialize(ArrayNode coordinates) {
            return new LineString(this.toPositions(coordinates));
        }
    }

    public static class MultiLineStringDeserializer extends GeoJsonModule.GeoJsonDeserializer<MultiLineString> {
        private MultiLineStringDeserializer() {
            super();
        }

        protected MultiLineString doDeserialize(ArrayNode coordinates) {
            ArrayList lines = new ArrayList(coordinates.size());
            Iterator var3 = coordinates.iterator();

            while (var3.hasNext()) {
                JsonNode lineString = (JsonNode) var3.next();
                if (lineString.isArray()) {
                    lines.add(this.toLineString((ArrayNode) lineString));
                }
            }

            return new MultiLineString(lines);
        }
    }

    public static class PolygonDeserializer extends GeoJsonModule.GeoJsonDeserializer<Polygon> {
        private PolygonDeserializer() {
            super();
        }

        protected Polygon doDeserialize(ArrayNode coordinates) {
            return this.toPolygon(coordinates);
        }
    }

    public static class MultiPolygonDeserializer extends GeoJsonModule.GeoJsonDeserializer<MultiPolygon> {
        private MultiPolygonDeserializer() {
            super();
        }

        protected MultiPolygon doDeserialize(ArrayNode coordinates) {
            ArrayList polygones = new ArrayList(coordinates.size());
            Iterator var3 = coordinates.iterator();

            while (var3.hasNext()) {
                JsonNode polygon = (JsonNode) var3.next();
                Iterator var5 = ((ArrayNode) polygon).iterator();

                polygones.add(this.toPolygon((ArrayNode) polygon));
            }

            return new MultiPolygon(polygones);
        }
    }

    public static class FeatureDeserializer extends JsonDeserializer<Feature> {

        @Override
        public Feature deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            ObjectMapper mapper = (ObjectMapper) p.getCodec();

            Feature feature = null;
            JsonNode node = p.readValueAsTree();
            JsonNode properties = node.get("properties");
            Properties props = mapper.readValue(properties.toString(), Properties.class);
            JsonNode geometry = node.get("geometry");
            String type = geometry.get("type").asText();

            switch (type) {
                case "Point":
                    Point point = mapper.readValue(geometry.toString(), Point.class);
                    feature = new Feature(point);
                    break;
                case "MultiPoint":
                    MultiPoint multiPoint = mapper.readValue(geometry.toString(), MultiPoint.class);
                    feature = new Feature(multiPoint);
                    break;
                case "LineString":
                    LineString lineString = mapper.readValue(geometry.toString(), LineString.class);
                    feature = new Feature(lineString);
                    break;
                case "MultiLineString":
                    MultiLineString multiLineString = mapper.readValue(geometry.toString(), MultiLineString.class);
                    feature = new Feature(multiLineString);
                    break;
                case "Polygon":
                    Polygon polygon = mapper.readValue(geometry.toString(), Polygon.class);
                    feature = new Feature(polygon);
                    break;
                case "MultiPolygon":
                    MultiPolygon multiPolygon = mapper.readValue(geometry.toString(), MultiPolygon.class);
                    feature = new Feature(multiPolygon);
                    break;
                default:
//                    feature = new Feature();
            }
            JsonNode id = node.get("id");
            if (null != id) {
                feature.setId(new BigInteger(id.asText()));
            }
            feature.setProperties(props);
            return feature;
        }
    }

    private abstract static class GeoJsonDeserializer<T extends Geometry<?>> extends JsonDeserializer<T> {
        private GeoJsonDeserializer() {
        }

        public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = (JsonNode) jp.readValueAsTree();
            JsonNode coordinates = node.get("coordinates");
            return coordinates != null && coordinates.isArray() ? this.doDeserialize((ArrayNode) coordinates) : null;
        }

        protected abstract T doDeserialize(ArrayNode var1);

        protected Point toPoint(ArrayNode node) {
            return node == null ? null : new Point(node.get(0).asDouble(), node.get(1).asDouble());
        }

        protected Position toPosition(ArrayNode node) {
            if (node.size() > 2) {
                return node == null ? null : new Position(node.get(0).asDouble(), node.get(1).asDouble(), node.get(2).asDouble());
            } else {
                return node == null ? null : new Position(node.get(0).asDouble(), node.get(1).asDouble());
            }
        }

        protected List<Position> toPositions(ArrayNode node) {
            if (node == null) {
                return Collections.emptyList();
            } else {
                ArrayList<Position> points = new ArrayList<Position>(node.size());
                Iterator var3 = node.iterator();

                while (var3.hasNext()) {
                    JsonNode coordinatePair = (JsonNode) var3.next();
                    if (coordinatePair.isArray()) {
                        points.add(this.toPosition((ArrayNode) coordinatePair));
                    }
                }

                return points;
            }
        }

        protected LineString toLineString(ArrayNode node) {
            return new LineString(this.toPositions(node));
        }

        protected Ring toRing(ArrayNode node) {
            return new Ring(this.toPositions(node));
        }

        protected Polygon toPolygon(ArrayNode node) {
            ArrayList lines = new ArrayList(node.size());
            Iterator var3 = node.iterator();

            while (var3.hasNext()) {
                JsonNode lineString = (JsonNode) var3.next();
                if (lineString.isArray()) {
                    lines.add(this.toRing((ArrayNode) lineString));
                }
            }

            return new Polygon(lines);
        }
    }
}
