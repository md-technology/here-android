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

package com.mdtech.here;

import java.util.TimeZone;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/14/2015.
 */
public class Config {

    public static final TimeZone CONFERENCE_TIMEZONE =
            TimeZone.getTimeZone(null);

    public static final String EXTRA_USER_ID =
            "com.mdtech.here.EXTRA_USER_ID";

    public static final String EXTRA_ALBUM_ID =
            "com.mdtech.here.EXTRA_ALBUM_ID";
    public static final String EXTRA_USER = "com.mdtech.here.extra_user";

    public static final String OSS_STYLE_PREVIEW_LG = "@!photo-preview-lg";
    public static final String OSS_STYLE_PREVIEW_SM = "@!photo-preview-sm";
    public static final String OSS_STYLE_PREVIEW_SSM = "@!panor-lg";

    public static final String MARKER_PHOTO = "com.mdtech.here.photo";

    public static final String ARG_ENTITY_ID = "com.mdtech.here.entity_id";
    public static final String ARG_ENTITY_PHOTO = "com.mdtech.here.entity_photo";

}
