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

package com.mdtech.here.album;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.mdtech.here.Config;
import com.mdtech.here.R;
import com.mdtech.here.ui.BaseActivity;
import com.mdtech.social.api.HereApi;
import com.mdtech.social.api.model.Photo;
import com.squareup.picasso.Callback;

import java.math.BigInteger;

import butterknife.Bind;
import uk.co.senab.photoview.PhotoViewAttacher;

import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/31/2015.
 */
public class PhotoViewActivity extends BaseActivity {
    private static final String TAG = makeLogTag(PhotoViewActivity.class);

    // entity
    private BigInteger mPhotoId;
    private Photo mPhoto;

    // API
    private HereApi mApi;

    // UI elements
    @Bind(R.id.iv_photo)
    ImageView mImageView;
    private PhotoViewAttacher mAttacher;

    public static void openWithPhoto(Activity openingActivity, BigInteger id) {
        Intent intent = new Intent(openingActivity, PhotoViewActivity.class);
        intent.putExtra(Config.ARG_ENTITY_ID, id.toString());
        openingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photoview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateUpOrBack(PhotoViewActivity.this, null);
            }
        });

        // The MAGIC happens here!
        mAttacher = new PhotoViewAttacher(mImageView);

        if(null == savedInstanceState) {
            mPhotoId = new BigInteger(getIntent().getStringExtra(Config.ARG_ENTITY_ID));
        }else {
            mPhoto = (Photo) savedInstanceState.get(Config.ARG_ENTITY_PHOTO);
        }
        mApi = getApi();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(null == mPhoto && null != mPhotoId) {
            new AsyncTask<Void, Void, Photo>() {
                @Override
                protected Photo doInBackground(Void... params) {
                    return mApi.photoOperations().get(mPhotoId);
                }

                @Override
                protected void onPostExecute(Photo photo) {
                    super.onPostExecute(photo);
                    setPhoto(photo);
                }
            }.execute();
        }else if(null != mPhoto) {
            setPhoto(mPhoto);
        }
    }

    /**
     * 设置图片加载小图
     * @param photo
     */
    private void setPhoto(final Photo photo) {
        mPhoto = photo;
//        picasso.load(getUrlFromOssKey(photo.getOssKey(), Config.OSS_STYLE_PREVIEW_SM))
//                .into(mImageView);
        loadLGPhoto(photo);
    }

    /**
     * 加载大图
     * @param photo
     */
    private void loadLGPhoto(Photo photo) {
        picasso.load(getUrlFromOssKey(photo.getOssKey(), Config.OSS_STYLE_PREVIEW_LG))
                .into(mImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        mImageView.animate()
                                .alphaBy(0.5f)
                                .alpha(1.f)
                                .setInterpolator(new OvershootInterpolator())
                                .setDuration(400)
                                .start();
                    }

                    @Override
                    public void onError() {

                    }
                });
    }
}
