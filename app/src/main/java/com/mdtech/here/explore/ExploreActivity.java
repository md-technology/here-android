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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.mdtech.here.R;
import com.mdtech.here.album.TrackActivity;
import com.mdtech.here.ui.BaseActivity;

import butterknife.Bind;

import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/14/2015.
 */
public class ExploreActivity extends BaseActivity {

    private static final String TAG = makeLogTag(ExploreActivity.class);

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.btn_take)
    FloatingActionButton mBtnTake;
    @Bind(R.id.btn_pick)
    FloatingActionButton mBtnPick;
    @Bind(R.id.btn_track)
    FloatingActionButton mBtnTrack;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_explore);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        toolbar.setTitle(getString(R.string.app_name));
//        setSupportActionBar(toolbar);

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

        mFab.setOnClickListener(this);
        mBtnTake.setOnClickListener(this);
        mBtnPick.setOnClickListener(this);
        mBtnTrack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_take:
                CameraActivity.openTakePicture(ExploreActivity.this);
                animateButton();
                break;
            case R.id.btn_pick:
                CameraActivity.openPickImage(ExploreActivity.this);
                animateButton();
                break;
            case R.id.btn_track:
                TrackActivity.open(ExploreActivity.this);
                animateButton();
                break;
            case R.id.fab:
                animateButton();
                break;
        }
    }

    private boolean isExpand = false;
    private final long mShortAnimationDuration = 300;
    private void animateButton() {
        if(isExpand) {
            animateButton(mBtnTake, 3);
            animateButton(mBtnPick, 2);
            animateButton(mBtnTrack, 1);
            mFab.animate().rotation(0).setDuration(mShortAnimationDuration);
            isExpand = false;
        }else {
            animateButton(mBtnTake, 3);
            animateButton(mBtnPick, 2);
            animateButton(mBtnTrack, 1);
            mFab.animate().rotation(135).setDuration(mShortAnimationDuration);
            isExpand = true;
        }
    }

    private void animateButton(final FloatingActionButton btn, int value) {
        if(isExpand) {
            btn.animate().translationY(0).alpha(0)
                    .setInterpolator(new DecelerateInterpolator())
                    .setDuration(mShortAnimationDuration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            btn.setVisibility(View.GONE);
                        }
                    });
        }else {
            btn.animate().alphaBy(0)
                    .translationY(value * -200).alpha(1)
                    .setInterpolator(new AccelerateInterpolator())
                    .setDuration(mShortAnimationDuration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            btn.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            btn.setVisibility(View.VISIBLE);
                        }
                    });
        }

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

    /*public Dialog onGetPictureDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "New track", "Cancel" };
        builder.setTitle("Select")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (items[which].equals(items[0])) {
                            CameraActivity.openTakePicture(ExploreActivity.this);
                        } else if (items[which].equals(items[1])) {
                            CameraActivity.openPickImage(ExploreActivity.this);
                        } else if(items[which].equals(items[2])) {
                            TrackActivity.open(ExploreActivity.this);
                        } else if (items[which].equals(items[3])) {
                            dialog.dismiss();
                        }
                    }
                });
        return builder.create();
    }*/
}
