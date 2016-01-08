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

package com.mdtech.here.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mdtech.here.Config;
import com.mdtech.here.R;
import com.mdtech.here.user.UserActivity;
import com.mdtech.here.util.CircleTransformation;
import com.mdtech.here.util.SocialAsyncTask;
import com.mdtech.social.api.*;
import com.mdtech.social.api.Error;
import com.mdtech.social.api.model.Comment;
import com.mdtech.social.api.model.User;
import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.mdtech.here.util.LogUtils.LOGE;
import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 1/8/2016.
 */
public class CommentListFragment extends AbstractListFragment {
    private static final String TAG = makeLogTag(CommentListFragment.class);

    private CommentOperations.CommentType mCommentType;

    @Bind(R.id.btn_comment)
    Button mBtnComment;
    @Bind(R.id.et_comment)
    EditText mCommentContent;

    public static Fragment newInstance(CommentOperations.CommentType type, BigInteger id){
        Bundle bundle = new Bundle();
        bundle.putSerializable(Config.ARG_ENTITY_TYPE, type);
        return newInstance(new CommentListFragment(), bundle, id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.comment_list_frag, container, false);

        mRecyclerView = (RecyclerView) root.findViewById(R.id.comment_list_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(null != savedInstanceState) {
            mCommentType = (CommentOperations.CommentType) savedInstanceState.getSerializable(Config.ARG_ENTITY_TYPE);
        }else {
            mCommentType = (CommentOperations.CommentType) getArguments().getSerializable(Config.ARG_ENTITY_TYPE);
        }

        // specify an adapter (see also next example)
        mAdapter = new CommentListAdapter(this.getActivity(), picasso);
        mRecyclerView.setAdapter(mAdapter);

        mBtnComment.setOnClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Config.ARG_ENTITY_TYPE, mCommentType);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_comment:
                sendComment();
        }
    }

    @Override
    protected int execLoadMoreTask() {
        List<Comment> comments = mApi.commentOperations().get(mCommentType, mId, pageSize, pageNo);
        mAdapter.addAll(comments);
        return comments.size();
    }

    private void sendComment() {
        if(validateComment()) {
            final String content = mCommentContent.getText().toString();

            new SocialAsyncTask<Void, Void, Comment>() {

                @Override
                protected Comment request(Void... params) {
                    return mApi.commentOperations().create(mCommentType, mId, content);
                }

                @Override
                protected void error(final Error error) {
                    String errorText = "social request error: ";
                    if(null != error) {
                        errorText = errorText + error.status + ", " + error.info;
                    }
                    LOGE(TAG, errorText);
                }

                @Override
                protected void onPostExecute(Comment comment) {
                    super.onPostExecute(comment);
                    if(null != comment) {
                        ((CommentListAdapter)mAdapter).insertTop(comment);
                        mAdapter.notifyDataSetChanged();
                        clearCommentContent();
                    }
                }
            }.execute();
        }
    }

    private boolean validateComment() {
        if (TextUtils.isEmpty(mCommentContent.getText())) {
            return false;
        }
        return true;
    }

    private void clearCommentContent() {
        mCommentContent.setText("");
    }

    public class CommentListAdapter extends AbstractListFragment.MyAdapter<Comment, CommentListAdapter.ViewHolder> {
        public CommentListAdapter(Context context, Picasso picasso) {
            super(context, picasso);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.list_title_time_format), Locale.CHINA);

        public class ViewHolder extends AbstractListFragment.MyAdapter.ViewHolder<Comment>{
            // each data item is just a string in this case

            @Bind(R.id.iv_user_profile)
            public ImageView mUserProfile;
            @Bind(R.id.tv_time)
            public TextView mTime;
            @Bind(R.id.tv_content)
            public TextView mContent;

            public ViewHolder(View v) {
                super(v);
                ButterKnife.bind(this, v);
                mUserProfile.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.iv_user_profile:
                        UserActivity.open(getActivity(), mEntity.getUser().getId());
                }

            }
        }

        // Create new views (invoked by the layout manager)
        @Override
        public CommentListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.comment_list_item, parent, false);
            // set the view's size, margins, paddings and layout parameters

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            Comment comment = mDataset.get(position);
            holder.mEntity = comment;

            holder.mTime.setText(dateFormat.format(comment.getCreateDate()));
            holder.mContent.setText(comment.getContent());
            if(null != comment.getUser().getAvatar()) {
                String uri = ((BaseActivity) getActivity()).getUrlFromOssKey(comment.getUser().getAvatar().getOssKey());
                picasso.load(uri)
                        .placeholder(R.mipmap.ic_profile_image)
                        .transform(new CircleTransformation(mContext.getResources().getColor(R.color.light_content_scrim), 1))
                        .into(holder.mUserProfile);
            }else {
                picasso.load(R.mipmap.ic_profile_image)
                        .transform(new CircleTransformation(mContext.getResources().getColor(R.color.light_content_scrim), 1))
                        .into(holder.mUserProfile);
            }
        }

        public void insertTop(Comment comment) {
            mDataset.add(0, comment);
        }

    }
}
