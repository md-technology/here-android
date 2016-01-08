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

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mdtech.here.Config;
import com.mdtech.here.R;
import com.mdtech.social.api.CommentOperations;
import com.mdtech.social.api.HereApi;

import java.math.BigInteger;

import butterknife.Bind;

import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 1/8/2016.
 */
public class CommentsActivity extends BaseActivity {
    private static final String TAG = makeLogTag(CommentsActivity.class);

    private HereApi mApi;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private CommentOperations.CommentType mCommentType;
    private BigInteger mEntityId;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.fl_container_comment_list)
    FrameLayout mCommentListContainer;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_act);

        setupAppbar(this);

        if (savedInstanceState == null) {
            mCommentType = (CommentOperations.CommentType)getIntent().getSerializableExtra(Config.ARG_ENTITY_TYPE);
            mEntityId = (BigInteger)getIntent().getSerializableExtra(Config.ARG_ENTITY_ID);
        } else {
            mCommentType = (CommentOperations.CommentType)savedInstanceState.getSerializable(Config.ARG_ENTITY_TYPE);
            mEntityId = (BigInteger)savedInstanceState.getSerializable(Config.ARG_ENTITY_ID);
        }

        mApi = getApi();

        setupCommentList();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    private void setupCommentList() {
        getSupportFragmentManager().beginTransaction().add(R.id.fl_container_comment_list,
                CommentListFragment.newInstance(mCommentType, mEntityId))
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Config.ARG_ENTITY_TYPE, mCommentType);
        outState.putSerializable(Config.ARG_ENTITY_ID, mEntityId);
    }
}
