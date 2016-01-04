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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/16/2015.
 */
@JsonDeserialize(using = GeoJsonModule.MultiPointDeserializer.class)
public class MultiPoint implements Geometry<Iterable<Iterable<Double>>> {

    private final List<Position> points;

    public MultiPoint(List<Position> points) {
        Assert.notNull(points, "Points must not be null.");
        Assert.isTrue(points.size() >= 2, "Minimum of 2 Points required.");
        this.points = new ArrayList(points);
    }

    public MultiPoint(Position first, Position second, Position... others) {
        Assert.notNull(first, "First point must not be null!");
        Assert.notNull(second, "Second point must not be null!");
        Assert.notNull(others, "Additional points must not be null!");
        this.points = new ArrayList();
        this.points.add(first);
        this.points.add(second);
        this.points.addAll(Arrays.asList(others));
    }

    @Override
    public String getType() {
        return "MultiPoint";
    }

    @Override
    public Iterable<Iterable<Double>> getCoordinates() {
        List<Iterable<Double>> coordinates = new ArrayList<Iterable<Double>>(points.size());
        Iterator<Position> iterator =  points.iterator();
        while (iterator.hasNext()) {
            coordinates.add(iterator.next().getCoordinates());
        }
        return coordinates;
    }

    @JsonIgnore
    public List<Position> getPositions() {
        return points;
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.points);
    }

    public boolean equals(Object obj) {
        return this == obj?true:(!(obj instanceof MultiPoint)?false:
                ObjectUtils.nullSafeEquals(this.points, ((MultiPoint)obj).points));
    }
}
