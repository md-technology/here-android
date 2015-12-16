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

package com.mdtech.here.ui;

import android.accounts.Account;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdtech.here.AppApplication;
import com.mdtech.here.R;
import com.mdtech.here.util.AccountUtils;
import com.mdtech.here.util.ImageLoader;
import com.mdtech.here.util.LoginAndAuthHelper;
import com.mdtech.here.welcome.WelcomeActivity;
import com.mdtech.social.api.Ponmap;
import com.mdtech.social.connect.PonmapConnectionFactory;

import org.springframework.social.connect.ConnectionRepository;

import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/14/2015.
 */
public abstract class BaseActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = makeLogTag(BaseActivity.class);

    // the LoginAndAuthHelper handles signing in to Google Play Services and OAuth
    private LoginAndAuthHelper mLoginAndAuthHelper;

    private int mThemedStatusBarColor;

    private int mNormalStatusBarColor;

    private ImageLoader mImageLoader;

    protected ConnectionRepository mConnectionRepository;
    protected PonmapConnectionFactory mConnectionFactory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//                RecentTasksStyler.styleRecentTasksEntry(this);

        // Check if the EULA has been accepted; if not, show it.
        // TODO del false
        if (false && WelcomeActivity.shouldDisplay(this)) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        mImageLoader = new ImageLoader(this);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        mThemedStatusBarColor = getResources().getColor(R.color.theme_primary_dark);
        mNormalStatusBarColor = mThemedStatusBarColor;

        mConnectionRepository = getApplicationContext().getConnectionRepository();
        mConnectionFactory = getApplicationContext().getConnectionFactory();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();
        setupAccountBox();

//        trySetupSwipeRefresh();
//        updateSwipeRefreshProgressBarTop();

//        View mainContent = findViewById(R.id.main_content);
//        if (mainContent != null) {
//            mainContent.setAlpha(0);
//            mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
//        } else {
//            LOGW(TAG, "No view with ID main_content to fade in.");
//        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if(null != navigationView) {
            navigationView.setNavigationItemSelectedListener(this);
        }
    }

    /**
     * Sets up the navigation drawer as appropriate. Note that the nav drawer will be
     * different depending on whether the attendee indicated that they are attending the
     * event on-site vs. attending remotely.
     */
    private void setupNavDrawer() {
        populateNavDrawer();
    }

    /**
     * Defines the Navigation Drawer items to display by updating {@code mNavDrawerItems} then
     * forces the Navigation Drawer to redraw itself.
     */
    private void populateNavDrawer() {

    }

    /**
     * Sets up the account box. The account box is the area at the top of the nav drawer that
     * shows which account the user is logged in as, and lets them switch accounts. It also
     * shows the user's Google+ cover photo as background.
     */
    private void setupAccountBox() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if(null == navigationView) {
            return;
        }
        View header = navigationView.getHeaderView(0);
        final View chosenAccountView = header.findViewById(R.id.chosen_account_view);
        Account chosenAccount = AccountUtils.getActiveAccount(this);
        if(null == chosenAccount) {
            return;
        }

        ImageView coverImageView = (ImageView) chosenAccountView.findViewById(R.id.profile_cover_image);
        ImageView profileImageView = (ImageView) chosenAccountView.findViewById(R.id.profile_image);
        TextView nameTextView = (TextView) chosenAccountView.findViewById(R.id.profile_name_text);
        TextView email = (TextView) chosenAccountView.findViewById(R.id.profile_email_text);

        String name = AccountUtils.getHereName(this);
        if (name == null) {
            nameTextView.setVisibility(View.GONE);
        } else {
            nameTextView.setVisibility(View.VISIBLE);
            nameTextView.setText(name);
        }

        String imageUrl = AccountUtils.getHereImageUrl(this);
        if (imageUrl != null) {
            mImageLoader.loadImage(imageUrl, profileImageView);
        }

        String coverImageUrl = AccountUtils.getHereCoverUrl(this);
        // TODO cover profile image
        if (coverImageUrl != null) {
            chosenAccountView.findViewById(R.id.profile_cover_image_placeholder).setVisibility(View.GONE);
            coverImageView.setVisibility(View.VISIBLE);
//            coverImageView.setContentDescription(getResources().getString(
//                    R.string.navview_header_user_image_content_description));
            mImageLoader.loadImage(coverImageUrl, coverImageView);
            coverImageView.setColorFilter(getResources().getColor(R.color.light_content_scrim));
        }

        email.setText(chosenAccount.name);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    private void signInOrCreateAnAccount() {

    }

    private void startLoginProcess() {

    }

    // ***************************************
    // Activity methods
    // ***************************************
    @Override
    public AppApplication getApplicationContext() {
        return (AppApplication) super.getApplicationContext();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_send) {
            new LoginAndAuthHelper(this).logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected Ponmap getApi() {
        if(null == mConnectionRepository) {
            mConnectionRepository = getApplicationContext().getConnectionRepository();
        }
        return mConnectionRepository.getPrimaryConnection(Ponmap.class).getApi();
    }
}