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

import com.mdtech.social.api.CommentOperations;
import com.mdtech.social.api.model.Comment;
import com.mdtech.social.api.model.CommentList;
import com.mdtech.social.api.model.UserList;

import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.List;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 1/8/2016.
 */
public class CommentTemplate extends AbstractPonmapOperations implements CommentOperations {

    private static final String PATH = "/comment";

    private final RestTemplate restTemplate;

    public CommentTemplate(RestTemplate restTemplate, boolean isAuthorized) {
        super(isAuthorized);
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Comment> get(CommentType type, BigInteger id, int pageSize, int pageNo) {
        MultiValueMap<String, String> params = getPagableParams(pageSize, pageNo);
        params.add("type", type.name());
        params.add("id", id.toString());
        return restTemplate.getForObject(buildUri(PATH, params), CommentList.class);
    }

    @Override
    public Comment create(CommentType type, BigInteger id, String content) {
        Comment comment = new Comment();
        comment.setType(type.name());
        comment.setId(id);
        comment.setContent(content);
        return restTemplate.postForObject(buildUri(PATH), comment, Comment.class);
    }
}
