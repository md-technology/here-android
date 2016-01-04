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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/16/2015.
 */
@JsonDeserialize(using = GeoJsonModule.MultiPolygonDeserializer.class)
public class MultiPolygon implements Geometry<Iterable<Iterable<Iterable<Iterable<Double>>>>> {

    private List<Polygon> coordinates = new ArrayList();

    public MultiPolygon(List<Polygon> polygons) {
        Assert.notNull(polygons, "Polygons for MultiPolygon must not be null!");
        this.coordinates.addAll(polygons);
    }

    @Override
    public String getType() {
        return "MultiPolygon";
    }

    @Override
    public Iterable<Iterable<Iterable<Iterable<Double>>>> getCoordinates() {
        ArrayList<Iterable<Iterable<Iterable<Double>>>> list =
                new ArrayList<Iterable<Iterable<Iterable<Double>>>>(this.coordinates.size());
        Iterator<Polygon> iterator = coordinates.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().getCoordinates());
        }
        return list;
    }

    @JsonIgnore
    public List<Polygon> getPolygons() {
        return coordinates;
    }
}
