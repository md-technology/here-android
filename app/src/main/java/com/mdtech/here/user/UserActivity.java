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

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdtech.here.Config;
import com.mdtech.here.R;
import com.mdtech.here.ui.BaseActivity;
import com.mdtech.here.util.CircleTransformation;
import com.mdtech.social.api.HereApi;
import com.mdtech.social.api.model.User;
import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/17/2015.
 */
public class UserActivity extends BaseActivity {
    private static final String TAG = makeLogTag(UserActivity.class);

    private BigInteger mUserId;
    private User mUser;

    private HereApi mApi;

    private Picasso picasso;

    private TextView mUserEmail;
    private TextView mUserDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Add the back button to the toolbar.
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateUpOrBack(UserActivity.this, null);
            }
        });

        mUserId = getIdFromBundle(savedInstanceState, Config.EXTRA_USER_ID);

        mApi = getApi();

        picasso = new Picasso.Builder(this).build();

        mUserEmail = (TextView) findViewById(R.id.vUserEmail);
        mUserDesc = (TextView) findViewById(R.id.vUserDesc);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get user then set user
        if(null != mUserId) {
            new UserTask(mUserId).execute();
        }
    }

    private void setupUser(User user) {
        mUser = user;

        if(null != mUser.getProfileCover()) {
            ImageView profileCover = (ImageView) findViewById(R.id.profile_cover_image);
            picasso.load(getUrlFromOssKey(mUser.getProfileCover().getOssKey()))
                    .into(profileCover);
            profileCover.setColorFilter(getResources().getColor(R.color.light_content_scrim));
        }

        if(null != mUser.getAvatar()) {
            ImageView avatarImage = (ImageView) findViewById(R.id.ivUserProfilePhoto);
            picasso.load(getUrlFromOssKey(mUser.getAvatar().getOssKey()))
                    .transform(new CircleTransformation())
                    .into(avatarImage);
        }

        mUserEmail.setText(mUser.getEmail());
        mUserDesc.setText(mUser.getDescription());

        setTitle(mUser.getName());
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPager.setOffscreenPageLimit(3);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(PhotoListFragment.newInstance(mApi, mUserId), "图片");
        adapter.addFrag(AlbumListFragment.newInstance(mApi, mUserId), "专辑");
        adapter.addFrag(UserListFragment.newInstance(mApi, mUserId), "组");
        adapter.addFrag(new DummyFragment(), "Map");
        viewPager.setAdapter(adapter);
    }

    class UserTask extends AsyncTask<Void, Void, User> {

        private BigInteger mId;

        public UserTask(BigInteger id) {
            mId = id;
        }

        @Override
        protected User doInBackground(Void... params) {
            return mApi.userOperations().get(mId);
        }

        @Override
        protected void onPostExecute(final User user) {
            setupUser(user);
        }

        @Override
        protected void onCancelled() {
        }
    }

    static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }
        @Override
        public int getCount() {
            return mFragmentList.size();
        }
        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    static class DummyFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.message_card, container, false);
            return view;
        }
    }
}
