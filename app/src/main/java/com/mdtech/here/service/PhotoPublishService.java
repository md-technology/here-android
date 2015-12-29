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

package com.mdtech.here.service;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.NotificationCompat;

import com.mdtech.here.util.FileUtils;
import com.mdtech.social.api.HereApi;
import com.mdtech.social.api.PhotoOperations;
import com.mdtech.social.api.model.Image;
import com.mdtech.social.api.model.Location;
import com.mdtech.social.api.model.Photo;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;

import static com.mdtech.here.util.LogUtils.LOGD;
import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/29/2015.
 */
public class PhotoPublishService extends AbstractService {

    private static final String TAG = makeLogTag(PhotoPublishService.class);

    private static String SERVICE_NAME = "photo_publish_service";
    private static String ARG_IMAGE = "arg_photo_publish_image";
    private static String ARG_LOCATION = "arg_photo_publish_location";
    private static String ARG_TAGS = "arg_photo_publish_tags";
    private static String ARG_ALBUM = "arg_photo_publish_album";
    private static String ARG_IS360 = "arg_photo_publish_is360";
    public static String ARG_PHOTO = "arg_photo";
    public static String ARG_RESULT = "arg_result";
    public static final String NOTIFICATION = "com.mdtech.here.service.receiver";

    private PhotoOperations mPhotoOperations;

    int id = 1;
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;

    public PhotoPublishService() {
        super(SERVICE_NAME);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public PhotoPublishService(String name) {
        super(name);
    }

    public static void startService(Activity activity, Image image, Location location, HashSet<String> tags, String album, boolean is360, Uri file) {
        Intent mServiceIntent = new Intent(activity, PhotoPublishService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_IMAGE, image);
        bundle.putSerializable(ARG_LOCATION, location);
        bundle.putSerializable(ARG_TAGS, tags);
        bundle.putString(ARG_ALBUM, album);
        bundle.putSerializable(ARG_IS360, is360);

        mServiceIntent.setData(file);
        mServiceIntent.putExtras(bundle);

        activity.startService(mServiceIntent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // init notify manager
        mNotifyManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);

        mPhotoOperations = getPhotoOperations();
        // Gets data from the incoming Intent
        final Uri file = intent.getData();
        Bundle bundle = intent.getExtras();
        Image image = (Image) bundle.getSerializable(ARG_IMAGE);
        Location location = (Location) bundle.getSerializable(ARG_LOCATION);
        HashSet<String> tags = (HashSet<String>) bundle.getSerializable(ARG_TAGS);
        String album = (String) bundle.getString(ARG_ALBUM);
        boolean is360 = bundle.getBoolean(ARG_IS360);

        LOGD(TAG, file.getPath());
        LOGD(TAG, file.getEncodedPath());
        Resource resource = new FileSystemResource(FileUtils.getPath(this, file)) {
            @Override
            public String getFilename() throws IllegalStateException {
                return file.getLastPathSegment();
            }
        };

        mBuilder.setContentText("Uploading photo");
        // Sets an activity indicator for an operation of indeterminate length
        mBuilder.setProgress(0, 0, true);
        // Issues the notification
        mNotifyManager.notify(id, mBuilder.build());
        Photo photo = mPhotoOperations.upload(image, location, tags, album, is360, resource);
        publishResults(photo, Activity.RESULT_OK);

        // When the loop is finished, updates the notification
        mBuilder.setContentText("Uploading complete")
                // Removes the progress bar
                .setProgress(0, 0, false);
        mNotifyManager.notify(id, mBuilder.build());
    }

    private void publishResults(Photo photo, int result) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(ARG_PHOTO, photo);
        intent.putExtra(ARG_RESULT, result);
        sendBroadcast(intent);
    }

    private PhotoOperations getPhotoOperations() {
        getAppApplication().checkConnection();
        return getAppApplication().getConnectionRepository().getPrimaryConnection(HereApi.class).getApi().photoOperations();
    }
}
