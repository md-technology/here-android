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
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
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

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    // number of images to select
    private static final int PICK_IMAGE = 1;

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

        FloatingActionButton fabBtn = (FloatingActionButton)findViewById(R.id.fab);
        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
            }
        });
    }

    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed

        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
        Snackbar.make(mSwipeRefreshLayout, "刷新成功", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    void selectImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK
                && null != data) {
            Uri selectedImage = data.getData();
            CameraActivity.openWithPhotoUri(this, selectedImage);

//            String[] filePathColumn = { MediaStore.Images.Media.DATA };
//
//            Cursor cursor = getContentResolver().query(selectedImage,
//                    filePathColumn, null, null, null);
//            cursor.moveToFirst();
//
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = cursor.getString(columnIndex);
//            cursor.close();
//
//            decodeFile(picturePath);

        }
    }

//    public void decodeFile(String filePath) {
//        // Decode image size
//        BitmapFactory.Options o = new BitmapFactory.Options();
//        o.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(filePath, o);
//
//        // The new size we want to scale to
//        final int REQUIRED_SIZE = 1024;
//
//        // Find the correct scale value. It should be the power of 2.
//        int width_tmp = o.outWidth, height_tmp = o.outHeight;
//        int scale = 1;
//        while (true) {
//            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
//                break;
//            width_tmp /= 2;
//            height_tmp /= 2;
//            scale *= 2;
//        }
//
//        // Decode with inSampleSize
//        BitmapFactory.Options o2 = new BitmapFactory.Options();
//        o2.inSampleSize = scale;
//        bitmap = BitmapFactory.decodeFile(filePath, o2);
//
//        image.setImageBitmap(bitmap);
//    }

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
