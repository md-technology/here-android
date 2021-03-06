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

import java.io.Serializable;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/16/2015.
 */
public interface Geometry<T extends Iterable<?>> extends Serializable {

    String TYPE_POINT = "Point";

    String TYPE_MULTI_POINT = "MultiPoint";

    String TYPE_LINE_STRING = "LineString";

    String TYPE_MULTI_LINE_STRING = "MultiLineString";

    String TYPE_POLYGON = "Polygon";

    String TYPE_MULTI_POLYGON = "MultiPolygon";

    String TYPE_GEOMETRY_COLLECTION = "GeometryCollection";

    String getType();

    T getCoordinates();

}
