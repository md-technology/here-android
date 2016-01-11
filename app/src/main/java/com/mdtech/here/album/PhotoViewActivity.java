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

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdtech.here.Config;
import com.mdtech.here.R;
import com.mdtech.here.explore.AlbumSearchActivity;
import com.mdtech.here.ui.BaseActivity;
import com.mdtech.here.ui.CommentsActivity;
import com.mdtech.here.util.SocialAsyncTask;
import com.mdtech.social.api.*;
import com.mdtech.social.api.Error;
import com.mdtech.social.api.model.Album;
import com.mdtech.social.api.model.EntityType;
import com.mdtech.social.api.model.Like;
import com.mdtech.social.api.model.Photo;
import com.squareup.picasso.Callback;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import uk.co.senab.photoview.PhotoViewAttacher;

import static com.mdtech.here.util.LogUtils.LOGE;
import static com.mdtech.here.util.LogUtils.LOGI;
import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/31/2015.
 */
public class PhotoViewActivity extends BaseActivity implements PhotoViewAttacher.OnViewTapListener {
    private static final String TAG = makeLogTag(PhotoViewActivity.class);

    // entity
    private BigInteger mPhotoId;
    private Photo mPhoto;

    // API
    private HereApi mApi;

    // UI elements
    @Bind(R.id.appbar)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.iv_photo)
    ImageView mImageView;
    @Bind(R.id.ll_photo_container)
    View mPhotoView;
    @Bind(R.id.tv_description)
    TextView mDescription;
    @Bind(R.id.tv_address)
    TextView mAddress;
    @Bind(R.id.ll_photo_actions)
    View mViewActions;
    @Bind(R.id.btn_like)
    Button mBtnLike;
    @Bind(R.id.btn_comment)
    Button mBtnComment;
    @Bind(R.id.fab)
    FloatingActionButton mFab;

    private PhotoViewAttacher mAttacher;

    static final int REQUEST_PICK_ALBUM = 1;

    public static void openWithPhoto(Activity openingActivity, BigInteger id) {
        Intent intent = new Intent(openingActivity, PhotoViewActivity.class);
        intent.putExtra(Config.ARG_ENTITY_ID, id.toString());
        openingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photoview);

        setupAppbar(this);

        // The MAGIC happens here!
        mAttacher = new PhotoViewAttacher(mImageView);
        mAttacher.setOnViewTapListener(this);

        if(null == savedInstanceState) {
            mPhotoId = new BigInteger(getIntent().getStringExtra(Config.ARG_ENTITY_ID));
        }else {
            mPhoto = (Photo) savedInstanceState.get(Config.ARG_ENTITY_PHOTO);
        }
        mApi = getApi();

        mPhotoView.setOnClickListener(this);
        mBtnLike.setOnClickListener(this);
        mBtnComment.setOnClickListener(this);
        mFab.setOnClickListener(this);
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
                    setupPhoto(photo);
                }
            }.execute();
        }else if(null != mPhoto) {
            setupPhoto(mPhoto);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_like:
                like();
                break;
            case R.id.btn_comment:
                final Intent intent = new Intent(this, CommentsActivity.class);
                intent.putExtra(Config.ARG_ENTITY_TYPE, EntityType.photo);
                intent.putExtra(Config.ARG_ENTITY_ID, mPhotoId);
                startActivity(intent);
                break;
            case R.id.fab:
                startActivityForResult(new Intent(this, AlbumSearchActivity.class), REQUEST_PICK_ALBUM);
                break;
        }
    }

    private void showHideBarInfo() {
        if(mAppBarLayout.getVisibility() == View.GONE) {
            mAppBarLayout.setVisibility(View.VISIBLE);
            mViewActions.setVisibility(View.VISIBLE);
        }else {
            mAppBarLayout.setVisibility(View.GONE);
            mViewActions.setVisibility(View.GONE);
        }
    }

    private void like() {
        mBtnLike.setClickable(false);
        new SocialAsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean request(Void... params) {
                if(mPhoto.isLike()) {
                    mApi.likeOperation().delete(EntityType.photo, mPhotoId);
                }else {
                    mApi.likeOperation().create(EntityType.photo, mPhotoId);
                }
                return true;
            }

            @Override
            protected void error(com.mdtech.social.api.Error error) {
                mBtnLike.setClickable(true);
                LOGE(TAG, error.info);
            }

            @Override
            protected void onPostExecute(Boolean res) {
                super.onPostExecute(res);
                if(null != res) {
                    mPhoto.setLike(!mPhoto.isLike());
                    if(mPhoto.isLike()) {
                        mPhoto.setLikeCount(mPhoto.getLikeCount()+1);
                    }else {
                        mPhoto.setLikeCount(mPhoto.getLikeCount()-1);
                    }
                    mBtnLike.setClickable(true);
                    setPhotoInfo(mPhoto);
                }
            }
        }.execute();
    }

    private void getLike(final Photo photo) {
        new SocialAsyncTask<Void, Void, Like>() {

            @Override
            protected Like request(Void... params) {
                return mApi.likeOperation().get(EntityType.photo, photo.getId());
            }

            @Override
            protected void error(Error error) {

            }

            protected void onPostExecute(Like res) {
                super.onPostExecute(res);

                if(res != null) {
                    mPhoto.setLike(true);
                    setPhotoInfo(mPhoto);
                }
            }
        }.execute();
    }

    /**
     * 设置图片加载小图
     * @param photo
     */
    private void setupPhoto(final Photo photo) {
        mPhoto = photo;
        setPhotoInfo(photo);
        loadLGPhoto(photo);
        getLike(mPhoto);
    }

    private void setPhotoInfo(Photo photo) {
        if(null != photo.getLocation()) {
            mAddress.setText(photo.getLocation().getAddress());
        }else {
            mAddress.setText("");
        }
        mDescription.setText(photo.getTitle());
        mBtnComment.setText(String.valueOf(photo.getCommentCount()));
        mBtnLike.setText(String.valueOf(photo.getLikeCount()));
        if(photo.isLike()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBtnLike.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.mipmap.ic_favorite_white_24dp), null, null, null);
            }else {
                mBtnLike.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_favorite_white_24dp), null, null, null);
            }
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBtnLike.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.ic_favorite_border_white_24dp), null, null, null);
            }else {
                mBtnLike.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_favorite_border_white_24dp), null, null, null);
            }
        }
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

    @Override
    public void onViewTap(View view, float x, float y) {
        showHideBarInfo();
    }

    private void addToAlbum(final BigInteger id) {

        final List<Photo> photos = new ArrayList<Photo>(1);
        photos.add(mPhoto);

        new SocialAsyncTask<Void, Void, Album>() {

            @Override
            protected Album request(Void... params) {
                return mApi.albumOperations().addPhotos(id, photos);
            }

            @Override
            protected void error(Error error) {

            }

            protected void onPostExecute(Album album) {
                super.onPostExecute(album);
                if(null != album) {
                    LOGI(TAG, "Added photo");
                    showSnackbarInfo(mFab, R.string.info_photo_collected, Snackbar.LENGTH_SHORT);
                    mFab.setVisibility(View.GONE);
                }
            }
        }.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICK_ALBUM && null != data) {
                String id = data.getStringExtra(AlbumSearchActivity.ARG_ALBUM_ID);
                addToAlbum(new BigInteger(id));
            }
        }
    }
}
