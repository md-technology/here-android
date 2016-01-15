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

package com.mdtech.here.geojson;

import com.mdtech.geojson.Feature;
import com.mdtech.geojson.FeatureCollection;
import com.mdtech.geojson.GeoJSONObject;
import com.mdtech.geojson.LineString;
import com.mdtech.geojson.Point;
import com.mdtech.geojson.Position;
import com.mdtech.geojson.Properties;

import java.io.InputStream;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/11/2015.
 */
public interface IGeoJSONOverlay extends IOverlay {

    /**
     *
     * @param featureCollection
     */
    void setSource(FeatureCollection featureCollection);

    /**
     *
     * @param feature
     */
    void addFeature(Feature feature);

    /**
     *
     * @param feature
     * @param style FeatureCollection style
     */
    void addFeature(Feature feature, Properties style);



}
