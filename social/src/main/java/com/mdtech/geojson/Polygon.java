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
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/16/2015.
 */
@JsonDeserialize(using = GeoJsonModule.PolygonDeserializer.class)
public class Polygon implements Geometry<Iterable<Iterable<Iterable<Double>>>> {

    private List<Ring> coordinates = new ArrayList<Ring>();

    public Polygon(List... lines) {
        Assert.notEmpty(lines, "Points for Polygon must not be null!");
        List[] var2 = lines;
        int var3 = lines.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            List line = var2[var4];
            this.coordinates.add(new Ring(line));
        }

    }

    public Polygon(List<Ring> lines) {
        Assert.notNull(lines, "Lines for Polygon must not be null!");
        this.coordinates.addAll(lines);
    }

    @Override
    public String getType() {
        return "Polygon";
    }

    @Override
    public Iterable<Iterable<Iterable<Double>>> getCoordinates() {
        ArrayList<Iterable<Iterable<Double>>> list = new ArrayList<Iterable<Iterable<Double>>>(this.coordinates.size());
        Iterator<Ring> iterator = coordinates.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().getCoordinates());
        }
        return list;
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.coordinates);
    }

    public boolean equals(Object obj) {
        return this == obj?true:(!(obj instanceof Polygon)?false:
                ObjectUtils.nullSafeEquals(this.coordinates, ((Polygon)obj).coordinates));
    }

    @JsonIgnore
    public List<Ring> getRings() {
        return coordinates;
    }
}
