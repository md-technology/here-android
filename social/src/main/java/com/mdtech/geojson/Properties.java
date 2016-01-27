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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.io.Serializable;

/**
 * GeoJSON 对象Feature或FeatureCollection常用属性对象，如果使用自定义属性请继承此class
 * Created by Tiven.wang on 1/4/2016.
 */
//@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
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

    public enum CoordinateSystem {
        WGS84, // 标准
        GCJ02, // 火星
        BD09 // 百度
    }

    public String title;
    public String description;

    // 标记大小
    @JsonProperty("marker-size")
    public Integer markerSize;
    // 标记图标
    @JsonProperty("marker-symbol")
    public String markerSymbol;
    // 标记颜色
    @JsonProperty("marker-color")
    public String markerColor;

    // 线颜色
    public String stroke;
    // 线透明度
    @JsonProperty("stroke-opacity")
    public Float strokeOpacity;
    // 线粗
    @JsonProperty("stroke-width")
    public Integer strokeWidth;
    // 填充颜色
    public String fill;
    // 填充透明度
    @JsonProperty("fill-opacity")
    public Float fillOpacity;

    // 点类型
    @JsonProperty("point-type")
    public PointType pointType = PointType.point;
    // 商业信息类型
    @JsonProperty("business-type")
    public BusinessType businessType = BusinessType.geo;
    // 坐标系统
    @JsonProperty("coordinate-system")
    public CoordinateSystem coordinateSystem = CoordinateSystem.WGS84;

    // 自定义属性
    public JsonNode custs;

    @JsonIgnore
    public void setCusts(Object custs) throws IOException {
        if(custs == null) {
            this.custs = null;
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        // Properties object -> String
        String ps = objectMapper.writeValueAsString(custs);
        // String -> JsonNode
        JsonNode jsonNode = objectMapper.readValue(ps, JsonNode.class);
        // set JsonNode to GeoJSON properties
        this.custs = jsonNode;
    }

    @JsonIgnore
    public <T> T getCusts(Class<T> c) throws IOException {
        if(null == this.custs) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(this.custs.toString(), c);
    }
}
