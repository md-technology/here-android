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

package com.mdtech.here.user;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.mdtech.here.Config;
import com.mdtech.here.R;
import com.mdtech.here.album.PhotoViewActivity;
import com.mdtech.here.explore.CameraActivity;
import com.mdtech.here.ui.AbstractListFragment;
import com.mdtech.here.ui.BaseActivity;
import com.mdtech.here.ui.OnRcvScrollListener;
import com.mdtech.social.api.HereApi;
import com.mdtech.social.api.model.Photo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.util.List;

import butterknife.Bind;

import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/18/2015.
 */
public class PhotoListFragment extends AbstractListFragment {
    private static final String TAG = makeLogTag(PhotoListFragment.class);

    public static Fragment newInstance(HereApi api, BigInteger id){
        return newInstance(new PhotoListFragment(), id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.photo_list_frag, container, false);

        mRecyclerView = (RecyclerView) root.findViewById(R.id.photo_list_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // specify an adapter (see also next example)
        mAdapter = new PhotoListAdapter(this.getActivity(), picasso);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.fab:
                CameraActivity.openTakePicture(getActivity());
                getActivity().finish();
                break;
        }
    }

    @Override
    protected int execLoadMoreTask() {
        List<Photo> photos = mApi.userOperations().getPhotos(mId, pageSize, pageNo);
        mAdapter.addAll(photos);
        return photos.size();
    }

    public class PhotoListAdapter extends AbstractListFragment.MyAdapter<Photo, PhotoListAdapter.ViewHolder> {
        public PhotoListAdapter(Context context, Picasso picasso) {
            super(context, picasso);
        }

        public class ViewHolder extends AbstractListFragment.MyAdapter.ViewHolder<Photo>{
            // each data item is just a string in this case
            public ImageView mView;
            public ViewHolder(ImageView v) {
                super(v);
                mView = v;
            }

            @Override
            public void onClick(View v) {
                PhotoViewActivity.openWithPhoto((Activity)mContext, mEntity.getId());
            }
        }

        // Create new views (invoked by the layout manager)
        @Override
        public PhotoListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.photo_image_view, parent, false);
            // set the view's size, margins, paddings and layout parameters

            ViewHolder vh = new ViewHolder((ImageView)v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            Photo photo = mDataset.get(position);
            holder.mEntity = photo;
//            loadPhoto(photo, holder.mView);
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            picasso.load(((BaseActivity)mContext).getUrlFromOssKey(
                    photo.getOssKey(), Config.OSS_STYLE_PREVIEW_SM))
                    .into(holder.mView, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.mView.animate()
                                    .alphaBy(0.5f)
                                    .alpha(1.f)
                                    .scaleXBy(0.8f).scaleYBy(0.8f)
                                    .scaleX(1.f).scaleY(1.f)
                                    .setInterpolator(new OvershootInterpolator())
                                    .setDuration(400)
                                    .setStartDelay(200)
                                    .start();
//                            mAdapter.notifyItemChanged(position);
//                            uiHandler.dispatchMessage(uiHandler.obtainMessage(MESSAGE_UPDATE, position));
                        }

                        @Override
                        public void onError() {
                        }
                    });
        }

    }
//
//    private void loadPhoto(Photo photo, final ImageView view) {
//        picasso.load(((BaseActivity)this.getActivity()).getUrlFromOssKey(
//                photo.getOssKey(), Config.OSS_STYLE_PREVIEW_SM))
//                .into(view, new Callback() {
//                    @Override
//                    public void onSuccess() {
//                        view.animate()
//                                .alphaBy(0.5f)
//                                .alpha(1.f)
//                                .scaleXBy(0.8f).scaleYBy(0.8f)
//                                .scaleX(1.f).scaleY(1.f)
//                                .setInterpolator(new OvershootInterpolator())
//                                .setDuration(400)
//                                .setStartDelay(200)
//                                .start();
//                        (new AsyncTask() {
//                            @Override
//                            protected Object doInBackground(Object[] params) {
//                                mAdapter.notifyDataSetChanged();
//                                return null;
//                            }
//                        }).execute();
//
//                    }
//
//                    @Override
//                    public void onError() {
//                    }
//                });
//    }
}
