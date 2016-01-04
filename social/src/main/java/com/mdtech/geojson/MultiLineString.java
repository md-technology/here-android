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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/16/2015.
 */
@JsonDeserialize(using = GeoJsonModule.MultiLineStringDeserializer.class)
public class MultiLineString implements Geometry<Iterable<Iterable<Iterable<Double>>>> {

    private List<LineString> coordinates = new ArrayList();

    public MultiLineString(List... lines) {
        Assert.notEmpty(lines, "Points for MultiLineString must not be null!");
        List[] var2 = lines;
        int var3 = lines.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            List line = var2[var4];
            this.coordinates.add(new LineString(line));
        }

    }

    public MultiLineString(List<LineString> lines) {
        Assert.notNull(lines, "Lines for MultiLineString must not be null!");
        this.coordinates.addAll(lines);
    }

    @Override
    public String getType() {
        return "MultiLineString";
    }

    @Override
    public Iterable<Iterable<Iterable<Double>>> getCoordinates() {
        ArrayList<Iterable<Iterable<Double>>> list = new ArrayList<Iterable<Iterable<Double>>>(this.coordinates.size());
        Iterator<LineString> iterator = coordinates.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().getCoordinates());
        }
        return list;
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.coordinates);
    }

    public boolean equals(Object obj) {
        return this == obj?true:(!(obj instanceof MultiLineString)?false:
                ObjectUtils.nullSafeEquals(this.coordinates, ((MultiLineString)obj).coordinates));
    }

    @JsonIgnore
    public List<LineString> getLineStrings() {
        return coordinates;
    }
}
