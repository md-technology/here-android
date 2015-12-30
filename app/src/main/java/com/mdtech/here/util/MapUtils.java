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

package com.mdtech.here.util;

import android.media.ExifInterface;
import android.net.Uri;

import com.mdtech.social.api.model.Location;

import java.io.IOException;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/30/2015.
 */
public abstract class MapUtils {

    public static MapUtils singleObject;

    public interface OnGeoCodeListener {
        void onGeoCodeResult(Location location);
    }

    public interface OnDeGeoCodeListener {
        void onDeGeoCodeResult(Location location);
    }

    public static MapUtils newInstance() {
        if(null == singleObject) {
            singleObject = new MapBaiduUtils();
        }
        return singleObject;
    }

    public abstract void reverseGeoCode(final OnDeGeoCodeListener target, final Location location);

    /**
     * 获取图片文件Exif中的gps信息
     * @param file
     * @return
     */
    public static Location getImageLocation(String file) {
        try {
            ExifInterface exif = new ExifInterface(file);
            float[] position = new float[2];
            if(exif.getLatLong(position)) {
                return new Location(position[1], position[0]);
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }
}
