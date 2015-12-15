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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.geo.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@JsonSerialize(using = Feature.FeatureSerializer.class)
@JsonDeserialize(using = Feature.FeatureDeserializer.class)
public class Feature {

    private String type = "Feature";

    private Map<String, String> properties = new HashMap<String, String>();

    public GeoJson getGeometry() {
        return null;
    }

    public static class Point extends Feature {
        private GeoJsonPoint geometry;

        @PersistenceConstructor
        public Point(GeoJsonPoint geometry) {
            this.geometry = geometry;
        }

        public GeoJsonPoint getGeometry() {
            return geometry;
        }

        public void setGeometry(GeoJsonPoint geometry) {
            this.geometry = geometry;
        }
    }

    public static class LineString extends Feature {
        private GeoJsonLineString geometry;

        @PersistenceConstructor
        public LineString(GeoJsonLineString geometry) {
            this.geometry = geometry;
        }

        public GeoJsonLineString getGeometry() {
            return geometry;
        }

        public void setGeometry(GeoJsonLineString geometry) {
            this.geometry = geometry;
        }
    }

    public static class Polygon extends Feature {
        private GeoJsonPolygon geometry;

        @PersistenceConstructor
        public Polygon(GeoJsonPolygon geometry) {
            this.geometry = geometry;
        }

        public GeoJsonPolygon getGeometry() {
            return geometry;
        }

        public void setGeometry(GeoJsonPolygon geometry) {
            this.geometry = geometry;
        }
    }

    public static class MultiPoint extends Feature {
        private GeoJsonMultiPoint geometry;

        @PersistenceConstructor
        public MultiPoint(GeoJsonMultiPoint geometry) {
            this.geometry = geometry;
        }

        public GeoJsonMultiPoint getGeometry() {
            return geometry;
        }

        public void setGeometry(GeoJsonMultiPoint geometry) {
            this.geometry = geometry;
        }
    }

    public static class MultiLineString extends Feature {
        private GeoJsonMultiLineString geometry;

        @PersistenceConstructor
        public MultiLineString(GeoJsonMultiLineString geometry) {
            this.geometry = geometry;
        }

        public GeoJsonMultiLineString getGeometry() {
            return geometry;
        }

        public void setGeometry(GeoJsonMultiLineString geometry) {
            this.geometry = geometry;
        }
    }

    public static class MultiPolygon extends Feature {
        private GeoJsonMultiPolygon geometry;

        @PersistenceConstructor
        public MultiPolygon(GeoJsonMultiPolygon geometry) {
            this.geometry = geometry;
        }

        public GeoJsonMultiPolygon getGeometry() {
            return geometry;
        }

        public void setGeometry(GeoJsonMultiPolygon geometry) {
            this.geometry = geometry;
        }
    }

    public static class FeatureSerializer extends JsonSerializer<Feature> {
        @Override
        public Class<Feature> handledType() {
            return Feature.class;
        }

        @Override
        public void serialize(Feature value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
            gen.writeStartObject();
            if(null != value.getId()) {
                gen.writeStringField("id", value.getId().toString());
            }
            gen.writeObjectField("properties", value.getProperties());
            gen.writeStringField("type", value.type);
            gen.writeObjectFieldStart("geometry");
            gen.writeStringField("type", value.getGeometry().getType());
            serialize(value.getGeometry(), gen, serializers);
            gen.writeEndObject();
            gen.writeEndObject();
        }

        public void serialize(GeoJson value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
                if(value instanceof GeoJsonPoint) {
                    serialize((GeoJsonPoint)value, gen, serializers);
                }else if(value instanceof GeoJsonPolygon) {
                    serialize((GeoJsonPolygon)value, gen, serializers);
                }else if(value instanceof GeoJsonLineString) {
                    serialize((GeoJsonMultiPoint)value, gen, serializers);
                }else if(value instanceof GeoJsonMultiLineString) {
                    serialize((GeoJsonMultiLineString)value, gen, serializers);
                }else if(value instanceof GeoJsonMultiPolygon) {
                    serialize((GeoJsonMultiPolygon)value, gen, serializers);
                }
        }

        public void serialize(GeoJsonPoint value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
            gen.writeObjectField("coordinates", value.getCoordinates());
        }

        public void serialize(GeoJsonMultiPoint value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
            gen.writeArrayFieldStart("coordinates");
            for(org.springframework.data.geo.Point point : value.getCoordinates()) {
                gen.writeStartArray();
                gen.writeNumber(point.getX());
                gen.writeNumber(point.getY());
                gen.writeEndArray();
            }
            gen.writeEndArray();
        }

        public void serialize(GeoJsonPolygon value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
            gen.writeArrayFieldStart("coordinates");
            for(GeoJsonLineString ls : value.getCoordinates()) {
                convert(ls, gen, serializers);
            }
            gen.writeEndArray();
        }

        public void serialize(GeoJsonMultiLineString value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
            gen.writeArrayFieldStart("coordinates");
            for(GeoJsonLineString ls : value.getCoordinates()) {
                convert(ls, gen, serializers);
            }
            gen.writeEndArray();
        }

        public void serialize(GeoJsonMultiPolygon value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
            gen.writeArrayFieldStart("coordinates");
            for(GeoJsonPolygon gp : value.getCoordinates()) {
                convert(gp, gen, serializers);
            }
            gen.writeEndArray();
        }

        public void convert(GeoJsonLineString value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
            gen.writeStartArray();
            for(org.springframework.data.geo.Point point : value.getCoordinates()) {
                gen.writeStartArray();
                gen.writeNumber(point.getX());
                gen.writeNumber(point.getY());
                gen.writeEndArray();
            }
            gen.writeEndArray();
        }

        public void convert(GeoJsonPolygon value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
            gen.writeStartArray();
            for(GeoJsonLineString ls : value.getCoordinates()) {
                convert(ls, gen, serializers);
            }
            gen.writeEndArray();
        }
    }

    public static class FeatureDeserializer extends JsonDeserializer<Feature> {

        @Override
        public Feature deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            ObjectMapper deserializer = new ObjectMapper();
            GeoJsonModule module = new GeoJsonModule();
            deserializer.registerModule(module);

            Feature feature = null;
            JsonNode node = p.readValueAsTree();
            JsonNode properties = node.get("properties");
            Map<String, String> props = deserializer.readValue(properties.toString(), Map.class);
            JsonNode geometry = node.get("geometry");
            String type = geometry.get("type").asText();
            if(type.equals("Point")) {
                GeoJsonPoint point = deserializer.readValue(geometry.toString(), GeoJsonPoint.class);
                feature = new Point(point);
            }else if(type.equals("Polygon")) {
                GeoJsonPolygon polygon = deserializer.readValue(geometry.toString(), GeoJsonPolygon.class);
                feature = new Polygon(polygon);
            }else if(type.equals("LineString")) {
                GeoJsonLineString ls = deserializer.readValue(geometry.toString(), GeoJsonLineString.class);
                feature = new LineString(ls);
            }else if(type.equals("MultiPoint")) {
                GeoJsonMultiPoint mp = deserializer.readValue(geometry.toString(), GeoJsonMultiPoint.class);
                feature = new MultiPoint(mp);
            }else if(type.equals("MultiLineString")) {
                GeoJsonMultiLineString ms = deserializer.readValue(geometry.toString(), GeoJsonMultiLineString.class);
                feature = new MultiLineString(ms);
            }else if(type.equals("MultiPolygon")) {
                GeoJsonMultiPolygon mp = deserializer.readValue(geometry.toString(), GeoJsonMultiPolygon.class);
                feature = new MultiPolygon(mp);
            }

            JsonNode id = node.get("id");
            if(null != id) {
                feature.setId(new BigInteger(id.asText()));
            }
            feature.setProperties(props);
            return feature;
        }

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public void putProperty(String key, String value) {
        this.properties.put(key, value);
    }

    public void removeProperty(String key, String value) {
        this.properties.remove(key);
    }
}
