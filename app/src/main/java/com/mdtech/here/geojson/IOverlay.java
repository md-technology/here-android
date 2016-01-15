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

package com.mdtech.here.geojson;

import com.mdtech.geojson.Position;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 1/15/2016.
 */
public interface IOverlay {
    interface Callback {
        void onPostDrawOverlay(IGeoJSONOverlay overlay);
    }

    /**
     * 添加到一个map上
     *
     */
    void addTo();

    /**
     * 地图适应此overlay大小
     */
    void fitBounds();

    /**
     *
     * @param listener
     */
    void setCallbackListener(Callback listener);
}
