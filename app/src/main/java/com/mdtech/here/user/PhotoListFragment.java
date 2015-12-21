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

import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdtech.here.Config;
import com.mdtech.here.R;
import com.mdtech.here.ui.BaseActivity;
import com.mdtech.here.ui.EndlessRecyclerOnScrollListener;
import com.mdtech.here.ui.Pagable;
import com.mdtech.social.api.HereApi;
import com.mdtech.social.api.model.Album;
import com.mdtech.social.api.model.Photo;
import com.mdtech.social.api.model.User;
import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.mdtech.here.util.LogUtils.LOGD;
import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/18/2015.
 */
public class PhotoListFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = makeLogTag(PhotoListFragment.class);

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private BigInteger mUserId;
    private HereApi mApi;

    private Picasso picasso;

    private Integer pageSize = 10;
    private Integer pageNo = 0;
    private boolean pageEnd = false;

    private UserPhotosTask mUserPhotosTask;

    public PhotoListFragment(HereApi api, BigInteger id) {
        mApi = api;
        mUserId = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.photo_list_frag, container, false);

        picasso = new Picasso.Builder(getActivity()).build();

        mRecyclerView = (RecyclerView) root.findViewById(R.id.photo_list_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(this.getActivity(), picasso);
        mRecyclerView.setAdapter(mAdapter);

//        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
//            @Override
//            public void onLoadMore(int current_page) {
//                // do something...
//                loadMorePhotos();
//                // Notify the adapter that the data has changed
//                mAdapter.notifyDataSetChanged();
//            }
//        });

        Button button = (Button)root.findViewById(R.id.btn_load_more);
        button.setOnClickListener(this);

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        loadMorePhotos();
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(null != mUserId) {
            mUserPhotosTask = new UserPhotosTask(mUserId);
        }
    }

    public void loadMorePhotos() {
        if(pageEnd) {
            return;
        }

        new UserPhotosTask(mUserId).execute();

//        List<Photo> photos = mApi.userOperations().getPhotos(mUserId, pageSize, pageNo);
//        if(photos.size() < pageSize) {
//            pageEnd = true;
//        }else {
//            pageNo++;
//        }
//
//        mAdapter.addPhotos(photos);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_load_more:
                loadMorePhotos();
                break;
        }

    }

    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<Photo> mDataset = new ArrayList<Photo>();
        private Context mContext;
        private Picasso picasso;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public ImageView mView;
            public ViewHolder(ImageView v) {
                super(v);
                mView = v;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(Context context, Picasso picasso) {
            mContext = context;
            this.picasso = picasso;
        }

        public void addPhotos(Collection<Photo> photos) {
            mDataset.addAll(photos);
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
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
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            picasso.load(((BaseActivity)mContext).getUrlFromOssKey(
                    mDataset.get(position).getOssKey(), Config.OSS_STYLE_PREVIEW_SM))
                    .into(holder.mView);
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

    protected class UserPhotosTask extends AsyncTask<Void, Void, List<Photo>> implements Pagable {

        private BigInteger mId;


        public UserPhotosTask(BigInteger id) {
            mId = id;
        }

        @Override
        protected List<Photo> doInBackground(Void... params) {
            LOGD(TAG, "load more photos, pageNo = " + pageNo );
            return mApi.userOperations().getPhotos(mId, pageSize, pageNo);
        }

        @Override
        protected void onPostExecute(final List<Photo> photos) {
            if(photos.size() < pageSize) {
                pageEnd = true;
                if(photos.size() == 0) {
                    return;
                }
            }else {
                pageDown();
            }

            mAdapter.addPhotos(photos);

            mAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onCancelled() {
        }

        public Integer pageDown() {
            return ++pageNo;
        }

        public Integer pageUp() {
            return --pageNo;
        }
    }
}
