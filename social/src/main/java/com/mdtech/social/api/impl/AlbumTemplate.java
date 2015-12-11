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

package com.mdtech.social.api.impl;

import com.mdtech.social.api.AlbumOperations;
import com.mdtech.social.api.json.Album;
import com.mdtech.social.api.json.TravelResponse;

import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/11/2015.
 */
public class AlbumTemplate extends AbstractPonmapOperations implements AlbumOperations {

    private final RestTemplate restTemplate;

    public AlbumTemplate(RestTemplate restTemplate, boolean isAuthorized) {
        super(isAuthorized);
        this.restTemplate = restTemplate;
    }

    @Override
    public Album get(BigInteger id) {
        return this.restTemplate.getForObject(BASE_API_URL + "/album/{id}", Album.class, id);
    }
}
