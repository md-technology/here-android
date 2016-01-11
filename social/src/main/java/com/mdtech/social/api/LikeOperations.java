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

import com.mdtech.social.api.model.EntityType;
import com.mdtech.social.api.model.Like;

import java.math.BigInteger;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 1/11/2016.
 */
public interface LikeOperations {
    /**
     * 加星
     * @param type
     * @param id
     * @return
     */
    Like create(EntityType type, BigInteger id);

    /**
     * 查询本人是否加星
     * @param type
     * @param id
     * @return
     */
    Like get(EntityType type, BigInteger id);

    /**
     * 取消加星
     * @param type
     * @param id
     */
    void delete(EntityType type, BigInteger id);

}
