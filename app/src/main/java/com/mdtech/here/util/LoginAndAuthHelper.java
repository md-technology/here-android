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

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.mdtech.here.R;
import com.mdtech.social.api.UserProfile;
import com.mdtech.social.api.model.User;

import java.lang.ref.WeakReference;

import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/14/2015.
 */
public class LoginAndAuthHelper {

    private static final String TAG = makeLogTag(LoginAndAuthHelper.class);

    // The Activity this object is bound to (we use a weak ref to avoid context leaks)
    WeakReference<Activity> mActivityRef;

    Context mAppContext;

    public LoginAndAuthHelper(Activity activity) {
        mActivityRef = new WeakReference<Activity>(activity);
        mAppContext = activity.getApplicationContext();
    }

    public void onLogin(UserProfile currentUser) {

        AccountUtils.setActiveAccount(mAppContext, currentUser.getUsername());

        // Record profile ID, image URL and name
        Log.d(TAG, "Saving ponmap profile ID: " + currentUser.getId());
        AccountUtils.setHereProfileId(mAppContext, currentUser.getUsername(), currentUser.getId());
        String imageUrl = currentUser.getImage();
        Log.d(TAG, "Saving plus image URL: " + imageUrl);
        AccountUtils.setHereImageUrl(mAppContext, currentUser.getUsername(), imageUrl);
        Log.d(TAG, "Saving plus display name: " + currentUser.getName());
        AccountUtils.setHereName(mAppContext, currentUser.getUsername(), currentUser.getName());

        imageUrl = currentUser.getCoverImage();
        if(null != imageUrl) {
            Log.d(TAG, "Saving plus cover URL: " + imageUrl);
            AccountUtils.setHereCoverUrl(mAppContext, currentUser.getUsername(), imageUrl);
        } else {
            Log.d(TAG, "Profile has no cover.");
        }
    }

    public boolean logout() {
        AccountUtils.setActiveAccount(mAppContext, null);
        return true;
    }

}
