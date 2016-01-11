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

package com.mdtech.social.api;

import com.mdtech.geojson.FeatureCollection;
import com.mdtech.social.api.model.Album;
import com.mdtech.social.api.model.Photo;

import java.math.BigInteger;
import java.util.List;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/11/2015.
 */
public interface AlbumOperations {
    /**
     *
     * @param id
     * @return
     */
    Album get(BigInteger id);

    /**
     * 创建专辑
     * @param album
     * @return
     */
    Album create(Album album);

    /**
     * 删除专辑
     * @param id
     * @return
     */
    void delete(BigInteger id);

    /**
     * 添加图片
     * @param id
     * @param photos
     * @return
     */
    Album addPhotos(BigInteger id, List<Photo> photos);

    /**
     * 删除图片
     * @param id
     * @param photos
     * @return
     */
    Album deletePhotos(BigInteger id, List<Photo> photos);

    /**
     * 向专辑中添加feature，feature有id的为更新
     * @param id
     * @param featureCollection
     * @return
     */
    Album addFeatures(BigInteger id, FeatureCollection featureCollection);
}
