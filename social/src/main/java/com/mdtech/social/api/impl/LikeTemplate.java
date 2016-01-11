/*
 * Copyright (C) 2016 The Here Android Project
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

import com.mdtech.social.api.LikeOperations;
import com.mdtech.social.api.model.Comment;
import com.mdtech.social.api.model.EntityType;
import com.mdtech.social.api.model.Like;

import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 1/11/2016.
 */
public class LikeTemplate extends AbstractPonmapOperations implements LikeOperations {
    private static final String PATH = "/like";

    private final RestTemplate restTemplate;

    public LikeTemplate(RestTemplate restTemplate, boolean isAuthorized) {
        super(isAuthorized);
        this.restTemplate = restTemplate;
    }

    @Override
    public Like create(EntityType type, BigInteger id) {
        Like like = new Like();
        like.setId(id);
        like.setType(type);
        return restTemplate.postForObject(buildUri(PATH), like, Like.class);
    }

    @Override
    public Like get(EntityType type, BigInteger id) {
        return restTemplate.getForObject(buildUri(PATH, getEntityParams(type, id)), Like.class);
    }

    @Override
    public void delete(EntityType type, BigInteger id) {
        restTemplate.delete(buildUri(PATH, getEntityParams(type, id)));
    }


}
