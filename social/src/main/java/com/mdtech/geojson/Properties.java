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

package com.mdtech.geojson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 1/4/2016.
 */
//@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Properties implements Serializable {

    public String title;
    public String description;

    @JsonProperty("marker-size")
    public Integer markerSize;
    @JsonProperty("marker-symbol")
    public String markerSymbol;
    @JsonProperty("marker-color")
    public String markerColor;

    public String stroke;
    @JsonProperty("stroke-opacity")
    public Float strokeOpacity;
    @JsonProperty("stroke-width")
    public Integer strokeWidth;

    public String fill;
    @JsonProperty("fill-opacity")
    public Float fillOpacity;


}
