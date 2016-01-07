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
 * GeoJSON 对象Feature或FeatureCollection常用属性对象，如果使用自定义属性请继承此class
 * Created by Tiven.wang on 1/4/2016.
 */
//@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Properties implements Serializable {

    public enum PointType {
        point, // 显示为点
        marker, // 显示为图标,图标的属性为markerSize markerSymbol markerColor定义
        photo // 显示为图片, 图片id为属性photoId定义
    }

    public enum BusinessType {
        event, // 代表事件
        poi, // 代表兴趣点
        geo, // 代表地理标记
        wiki // 代表维基标记
    }

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

    @JsonProperty("point-type")
    public PointType pointType = PointType.point;
    @JsonProperty("business-type")
    public BusinessType businessType = BusinessType.geo;

    @JsonProperty("photo-id")
    public String photoId;

}
