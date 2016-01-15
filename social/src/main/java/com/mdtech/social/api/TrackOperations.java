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

import com.mdtech.social.api.model.Track;

import java.math.BigInteger;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 1/15/2016.
 */
public interface TrackOperations {

    /**
     *
     * @param track
     * @return
     */
    Track create(Track track);

    /**
     *
     * @param id
     * @return
     */
    Track get(BigInteger id);

    /**
     *
     * @param id
     */
    void delete(BigInteger id);


}
