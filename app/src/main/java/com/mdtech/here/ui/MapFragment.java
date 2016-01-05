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

package com.mdtech.here.ui;

import com.mdtech.geojson.FeatureCollection;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 1/4/2016.
 */
public interface MapFragment {

    String ARG_GEOJSON = "arg_geojson";

    interface Callback {

    }

    /**
     * 添加一个GeoJSON到地图上
     * @param geoJSON
     */
    void addGeoJSON(FeatureCollection geoJSON);

    /**
     * 清除地图上所有的覆盖物overlays
     */
    void clear();

    /**
     * 地图适应覆盖物大小
     */
    void fitBounds();
}
