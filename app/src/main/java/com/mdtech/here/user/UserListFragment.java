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
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdtech.here.Config;
import com.mdtech.here.R;
import com.mdtech.here.ui.AbstractListFragment;
import com.mdtech.here.ui.BaseActivity;
import com.mdtech.social.api.HereApi;
import com.mdtech.social.api.model.Cover;
import com.mdtech.social.api.model.User;
import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.util.List;

import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/25/2015.
 */
public class UserListFragment extends AbstractListFragment {
    private static final String TAG = makeLogTag(UserListFragment.class);



    public static Fragment newInstance(HereApi api, BigInteger id){
        return newInstance(new UserListFragment(), api, id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.user_list_frag, container, false);

        mRecyclerView = (RecyclerView) root.findViewById(R.id.user_list_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // specify an adapter (see also next example)
        mAdapter = new UserListAdapter(this.getActivity(), picasso);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_load_more:
                break;
        }
    }

    @Override
    protected int execLoadMoreTask() {
        List<User> users = mApi.userOperations().getGroups(mId, pageSize, pageNo);
        mAdapter.addAll(users);

        return users.size();
    }

    public static class UserListAdapter extends AbstractListFragment.MyAdapter<User, UserListAdapter.ViewHolder> {
        public UserListAdapter(Context context, Picasso picasso) {
            super(context, picasso);
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends AbstractListFragment.MyAdapter.ViewHolder<User> {
            public ViewHolder(View itemView) {
                super(itemView);
            }

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), UserActivity.class);
                Bundle bundle = new Bundle();
                bundle.putCharSequence(Config.EXTRA_USER_ID, mEntity.getId().toString());
                intent.putExtras(bundle);
                mContext.startActivity(intent);
                ((Activity)mContext).finish();
            }
        }

        // Create new views (invoked by the layout manager)
        @Override
        public UserListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_user_view, parent, false);
            // set the view's size, margins, paddings and layout parameters

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            User user = mDataset.get(position);
            Cover cover = user.getAvatar();
            if(null != cover) {
                ImageView view = (ImageView) holder.mView.findViewById(R.id.iv_user_profile);
                picasso.load(((BaseActivity)mContext).getUrlFromOssKey(
                        cover.getOssKey()))
                        .into(view);
            }

            TextView username = (TextView) holder.mView.findViewById(R.id.tv_user_name);
            TextView desc = (TextView) holder.mView.findViewById(R.id.tv_user_desc);

            username.setText(user.getName());
            desc.setText(user.getDescription());
            holder.mEntity = user;
        }
    }

}
