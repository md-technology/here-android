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

package com.mdtech.social.api;

import com.mdtech.geojson.FeatureCollection;
import com.mdtech.social.api.model.Album;
import com.mdtech.social.api.model.Comment;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/11/2015.
 */
public interface CommentOperations {

    enum CommentType implements Serializable {
        photo,
        album
    }

    /**
     * 查询评论
     * @param type
     * @param id
     * @param pageSize
     * @param pageNo
     * @return
     */
    List<Comment> get(CommentType type, BigInteger id, int pageSize, int pageNo);

    /**
     * 创建一条评论
     * @param type
     * @param id
     * @param content
     * @return
     */
    Comment create(CommentType type, BigInteger id, String content);
}
