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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/16/2015.
 */
//@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class GeoJSONObject implements Serializable {

    // ------------------------------------------------------------------------
    // Public Constants
    // ------------------------------------------------------------------------

    public static final String JSON_TYPE = "type";

    public static final String TYPE_FEATURE = "Feature";

    public static final String TYPE_FEATURE_COLLECTION = "FeatureCollection";

    public BigInteger id;

    private Properties properties;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    public GeoJSONObject() {
        // Default Constructor
    }

    public GeoJSONObject(JsonNode json) {

    }

    // ------------------------------------------------------------------------
    // Public Methods
    // ------------------------------------------------------------------------

    public abstract String getType();

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getId() {
        return this.id;
    }

    @JsonProperty("properties")
    @JsonSerialize
    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
