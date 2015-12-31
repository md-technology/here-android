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

import com.mdtech.social.api.SignupOperations;
import com.mdtech.social.api.model.Album;
import com.mdtech.social.api.model.SignupRequest;
import com.mdtech.social.api.model.User;

import org.springframework.web.client.RestTemplate;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/31/2015.
 */
public class SignupTemplate extends AbstractPonmapOperations implements SignupOperations {

    private final RestTemplate restTemplate;

    public SignupTemplate(RestTemplate restTemplate, boolean isAuthorized) {
        super(isAuthorized);
        this.restTemplate = restTemplate;
    }

    @Override
    public User signup(SignupRequest request) {
        return this.restTemplate.postForObject(BASE_API_URL + "/signup", request, User.class);
    }
}
