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

package com.mdtech.here.explore;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mdtech.here.R;
import com.mdtech.here.account.LoginActivity;
import com.mdtech.here.album.AlbumActivity;
import com.mdtech.here.ui.BaseActivity;
import com.mdtech.here.util.AccountUtils;

import java.math.BigInteger;

import static com.mdtech.here.util.LogUtils.LOGE;
import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/14/2015.
 */
public class ExploreActivity extends BaseActivity {

    private static final String TAG = makeLogTag(ExploreActivity.class);
    public static final String EXTRA_ALBUM_ID =
            "com.mdtech.here.EXTRA_ALBUM_ID";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_explore);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                     @Override
                                                     public void onRefresh() {
                                                         // Refresh items
                                                         onItemsLoadComplete();
                                                     }
                                                 }
        );

        mRecyclerView = (RecyclerView) findViewById(R.id.items_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        String[] mDataset = new String[] {
                "我们",
                "重点",
                "看看",
                "具体",
                "要",
                "怎么",
                "实现",
                "?"
        };

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(mDataset);
        mRecyclerView.setAdapter(mAdapter);

//        final Button button = (Button)findViewById(R.id.explore_ok_button);
//        button.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                CharSequence albumId = button.getText();
//                if (TextUtils.isEmpty(albumId)) {
//                    LOGE(TAG, "Album id is empty");
//                    return;
//                }
//                Intent intent = new Intent(v.getContext(), AlbumActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putCharSequence(EXTRA_ALBUM_ID, albumId);
//                intent.putExtras(bundle);
//                startActivity(intent);
////                finish();
//            }
//        });

//        Button login = (Button)findViewById(R.id.explore_login_button);
//        login.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if (AccountUtils.hasActiveAccount(ExploreActivity.this)) {
//                    Intent intent = new Intent(v.getContext(), AlbumActivity.class);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    Intent intent = new Intent(v.getContext(), LoginActivity.class);
//                    startActivity(intent);
////                    finish();
//                }
//
//            }
//        });

    }

    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed

        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
        Snackbar.make(mSwipeRefreshLayout, "刷新成功", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private String[] mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public CardView mView;
            public ViewHolder(CardView v) {
                super(v);
                mView = v;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(String[] myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_card, parent, false);
            // set the view's size, margins, paddings and layout parameters

            ViewHolder vh = new ViewHolder((CardView)v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            TextView view = (TextView)holder.mView.findViewById(R.id.info_text);
            view.setText(mDataset[position]);
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.length;
        }
    }
}
