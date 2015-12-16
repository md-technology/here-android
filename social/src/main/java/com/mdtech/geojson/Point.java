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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Arrays;
import java.util.List;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/16/2015.
 */
@JsonDeserialize(using = GeoJsonModule.PointDeserializer.class)
public class Point implements Geometry<List<Double>> {

    private Position mPosition;

    public Point(double lng, double lat) {
        this.mPosition = new Position(lng, lat);
    }

    public Point(Position position) {
        this.mPosition = new Position(position);
    }

    @Override
    public String getType() {
        return "Point";
    }

    @Override
    public List<Double> getCoordinates() {
        return Arrays.asList(new Double[]{Double.valueOf(this.mPosition.getLongitude()), Double.valueOf(this.mPosition.getLatitude()), Double.valueOf(this.mPosition.getAltitude())});
    }

    public Position getPosition() {
        return mPosition;
    }

}
