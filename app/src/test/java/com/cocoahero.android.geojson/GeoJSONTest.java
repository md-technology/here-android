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

package com.cocoahero.android.geojson;

import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/10/2015.
 */
public class GeoJSONTest {

    @Test
    public void parse_isCorrect() throws Exception {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("countries.geo.json");

        GeoJSONObject geoJSONObject = GeoJSON.parse(is);

        Assert.assertNotNull(geoJSONObject);
    }
}
