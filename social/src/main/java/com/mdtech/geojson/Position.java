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
import java.util.Arrays;
import java.util.Locale;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/16/2015.
 */
public class Position implements Serializable {

    private final double lat;
    private final double lng;
    private final double alt;

    public Position(double lng, double lat) {
        this.lat = lat;
        this.lng = lng;
        this.alt = 0;
    }

    public Position(double lng, double lat, double alt) {
        this.lat = lat;
        this.lng = lng;
        this.alt = alt;
    }

    public Position(Position position) {
        this.lat = position.getLatitude();
        this.lng = position.getLongitude();
        this.alt = position.getAltitude();
    }

    public int hashCode() {
        byte result = 1;
        long temp = Double.doubleToLongBits(this.lat);
        int result1 = 31 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.lng);
        result1 = 31 * result1 + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.alt);
        result1 = 31 * result1 + (int)(temp ^ temp >>> 32);
        return result1;
    }

    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        } else if(!(obj instanceof Position)) {
            return false;
        } else {
            Position other = (Position)obj;
            return Double.doubleToLongBits(this.lat) != Double.doubleToLongBits(other.lat) ?
                    false : Double.doubleToLongBits(this.lng) == Double.doubleToLongBits(other.lng) ?
                    false : Double.doubleToLongBits(this.alt) == Double.doubleToLongBits(other.alt);
        }
    }

    public String toString() {
        return String.format(Locale.ENGLISH, "Position [lng=%f, lat=%f, alt=%f]", new Object[]{Double.valueOf(this.lng), Double.valueOf(this.lat), Double.valueOf(this.alt)});
    }

    public double getAltitude() {
        return alt;
    }

    public double getLatitude() {
        return lat;
    }

    public double getLongitude() {
        return lng;
    }

    public Iterable<Double> getCoordinates() {
        return Arrays.asList(new Double[]{Double.valueOf(this.getLongitude()), Double.valueOf(this.getLatitude()), Double.valueOf(this.getAltitude())});
    }
}
