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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdtech.here.Config;
import com.mdtech.here.R;
import com.mdtech.here.album.AlbumActivity;
import com.mdtech.here.ui.BaseActivity;
import com.mdtech.here.ui.Pagable;
import com.mdtech.social.api.HereApi;
import com.mdtech.social.api.model.Album;
import com.mdtech.social.api.model.Photo;
import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.mdtech.here.util.LogUtils.LOGD;
import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/17/2015.
 */
public class AlbumListFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = makeLogTag(AlbumListFragment.class);

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private BigInteger mUserId;

    private Picasso picasso;
    private HereApi mApi;

    private Integer pageSize = 10;
    private Integer pageNo = 0;
    private boolean pageEnd = false;

    public AlbumListFragment(HereApi api, BigInteger id) {
        mApi = api;
        mUserId = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.user_album_frag, container, false);

        picasso = new Picasso.Builder(getActivity()).build();

        mRecyclerView = (RecyclerView) root.findViewById(R.id.album_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(this.getActivity(), picasso);
        mRecyclerView.setAdapter(mAdapter);

        Button button = (Button)root.findViewById(R.id.btn_load_more);
        button.setOnClickListener(this);

        return root;
    }

    public void loadMorePhotos() {
        if (pageEnd) {
            return;
        }

        new UserAlbumsTask(mUserId).execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_load_more:
                loadMorePhotos();
                break;
        }
    }

    /*public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }*/

    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<Album> mDataset = new ArrayList<Album>();
        private Context mContext;
        private Picasso picasso;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            // each data item is just a string in this case
            public View mView;
            public Album mAlbum;

            public ViewHolder(View v) {
                super(v);
                mView = v;
                mView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AlbumActivity.class);
                Bundle bundle = new Bundle();
                bundle.putCharSequence(Config.EXTRA_ALBUM_ID, mAlbum.getId().toString());
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(Context context, Picasso picasso) {
            mContext = context;
            this.picasso = picasso;
        }

        public void addAlbums(Collection<Album> albums) {
            mDataset.addAll(albums);
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.album_view, parent, false);
            // set the view's size, margins, paddings and layout parameters

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Album album = mDataset.get(position);
            Photo cover = album.getCover();
            if(null != cover) {
                ImageView view = (ImageView) holder.mView.findViewById(R.id.iv_album_cover);
                picasso.load(((BaseActivity)mContext).getUrlFromOssKey(
                        cover.getOssKey(), Config.OSS_STYLE_PREVIEW_SM))
                        .into(view);
            }

            TextView title = (TextView) holder.mView.findViewById(R.id.tv_album_title);
            TextView desc = (TextView) holder.mView.findViewById(R.id.tv_album_description);

            title.setText(album.getTitle());
            desc.setText(album.getDescription());
            holder.mAlbum = album;
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

    protected class UserAlbumsTask extends AsyncTask<Void, Void, List<Album>> implements Pagable {

        private BigInteger mId;


        public UserAlbumsTask(BigInteger id) {
            mId = id;
        }

        @Override
        protected List<Album> doInBackground(Void... params) {
            LOGD(TAG, "load more photos, pageNo = " + pageNo );
            return mApi.userOperations().getAlbums(mId, pageSize, pageNo);
        }

        @Override
        protected void onPostExecute(final List<Album> albums) {
            if(albums.size() < pageSize) {
                pageEnd = true;
                if(albums.size() == 0) {
                    return;
                }
            }else {
                pageDown();
            }

            mAdapter.addAlbums(albums);

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
