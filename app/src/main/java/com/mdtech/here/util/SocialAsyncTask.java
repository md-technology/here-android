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

package com.mdtech.here.util;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdtech.social.api.*;
import com.mdtech.social.api.Error;

import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;

import static com.mdtech.here.util.LogUtils.LOGE;
import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 1/8/2016.
 */
public abstract class SocialAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private static final String TAG = makeLogTag(SocialAsyncTask.class);

    @Override
    protected Result doInBackground(Params... params) {
        try {
            return request(params);
        }catch (HttpServerErrorException exception) {
            String body = exception.getResponseBodyAsString();
            LOGE(TAG, body);
            Error error = null;
            if(!TextUtils.isEmpty(body)) {
                ObjectMapper deserializer = new ObjectMapper();
                try {
                    error = deserializer.readValue(body, Error.class);
                } catch (IOException e) {
                    LOGE(TAG, e.getMessage());
                }
            }
            error(error);
        }catch (Exception ex) {
            LOGE(TAG, ex.getMessage());
        }
        return null;
    }

    protected abstract Result request(Params... params);

    protected abstract void error(Error error);
}
