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

import com.mdtech.geojson.FeatureCollection;
import com.mdtech.social.api.AlbumOperations;
import com.mdtech.social.api.model.Album;
import com.mdtech.social.api.model.Photo;

import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/11/2015.
 */
public class AlbumTemplate extends AbstractHereOperations implements AlbumOperations {
    private static final String PATH = "/album";
    private final RestTemplate restTemplate;

    public AlbumTemplate(RestTemplate restTemplate, boolean isAuthorized) {
        super(isAuthorized);
        this.restTemplate = restTemplate;
    }

    @Override
    public Album get(BigInteger id) {
        return this.restTemplate.getForObject(BASE_API_URL + "/album/{id}", Album.class, id);
    }

    @Override
    public Album create(Album album) {
        return this.restTemplate.postForObject(BASE_API_URL + "/album", album, Album.class);
    }

    @Override
    public void delete(BigInteger id) {
        this.restTemplate.delete(BASE_API_URL + "/album/{id}", id);
    }

    @Override
    public Album addPhotos(BigInteger id, List<Photo> photos) {
        List<String> photoIds = new ArrayList<String>(photos.size());
        Iterator<Photo> iterator = photos.iterator();
        while (iterator.hasNext()) {
            photoIds.add(iterator.next().getId().toString());
        }
        return this.restTemplate.postForObject(BASE_API_URL + PATH + "/{id}/add", photoIds, Album.class, id);
    }

    @Override
    public Album deletePhotos(BigInteger id, List<Photo> photos) {
        List<String> photoIds = new ArrayList<String>(photos.size());
        Iterator<Photo> iterator = photos.iterator();
        while (iterator.hasNext()) {
            photoIds.add(iterator.next().getId().toString());
        }
        return this.restTemplate.postForObject(BASE_API_URL + PATH + "/{id}/remove", photoIds, Album.class, id);
    }

    @Override
    public Album addFeatures(BigInteger id, FeatureCollection featureCollection) {
        return this.restTemplate.postForObject(BASE_API_URL + PATH + "/{id}/fc", featureCollection, Album.class, id);
    }
}
