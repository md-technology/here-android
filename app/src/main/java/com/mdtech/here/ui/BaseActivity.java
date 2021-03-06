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
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.mdtech.here.AppApplication;
import com.mdtech.here.Config;
import com.mdtech.here.R;
import com.mdtech.here.account.LoginActivity;
import com.mdtech.here.account.SignupActivity;
import com.mdtech.here.album.AlbumBaiduActivity;
import com.mdtech.here.settings.SettingsActivity;
import com.mdtech.here.user.UserActivity;
import com.mdtech.here.util.AccountUtils;
import com.mdtech.here.util.CircleTransformation;
import com.mdtech.here.util.LoginAndAuthHelper;
import com.mdtech.here.welcome.WelcomeActivity;
import com.mdtech.social.api.HereApi;
import com.mdtech.social.connect.HereConnectionFactory;
import com.squareup.picasso.Picasso;

import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.NotConnectedException;

import java.math.BigInteger;

import butterknife.ButterKnife;

import static com.mdtech.here.util.LogUtils.LOGE;
import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/14/2015.
 */
public abstract class BaseActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener {

    private static final String TAG = makeLogTag(BaseActivity.class);

    // the LoginAndAuthHelper handles signing in to Google Play Services and OAuth
    private LoginAndAuthHelper mLoginAndAuthHelper;

    // Primary toolbar and drawer toggle
    private Toolbar mActionBarToolbar;

    // Navigation drawer:
    private DrawerLayout mDrawerLayout;

    private int mThemedStatusBarColor;

    private int mNormalStatusBarColor;

    protected ConnectionRepository mConnectionRepository;
    protected HereConnectionFactory mConnectionFactory;

    protected Picasso picasso;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        bindViews();
    }

    protected void bindViews() {
        ButterKnife.bind(this);
//        setupToolbar();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//                RecentTasksStyler.styleRecentTasksEntry(this);

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());

        // Check if the EULA has been accepted; if not, show it.
        // TODO del false
        if (false && WelcomeActivity.shouldDisplay(this)) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        Account chosenAccount = AccountUtils.getActiveAccount(this);
        if(null == chosenAccount && !this.getClass().equals(LoginActivity.class)
                && !this.getClass().equals(SignupActivity.class)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);

//        ActionBar ab = getSupportActionBar();
//        if (ab != null) {
//            ab.setDisplayHomeAsUpEnabled(true);
//        }

        mThemedStatusBarColor = getResources().getColor(R.color.theme_primary_dark);
        mNormalStatusBarColor = mThemedStatusBarColor;

        mConnectionRepository = getApplicationContext().getConnectionRepository();
        mConnectionFactory = getApplicationContext().getConnectionFactory();

        // check the
        getApplicationContext().checkConnection();

        picasso = new Picasso.Builder(this).build();
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

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null) {
            return;
        }
        mDrawerLayout.setStatusBarBackgroundColor(
                getResources().getColor(R.color.theme_primary_dark));

        if (mActionBarToolbar != null) {
            mActionBarToolbar.setNavigationIcon(R.drawable.side_nav_bar);
            mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

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
            picasso.load(getUrlFromOssKey(imageUrl))
                    .transform(new CircleTransformation(2))
                    .into(profileImageView);
//            mImageLoader.loadImage(imageUrl, profileImageView);
        }

        String coverImageUrl = AccountUtils.getHereCoverUrl(this);
        // TODO cover profile image
        if (coverImageUrl != null) {
            chosenAccountView.findViewById(R.id.profile_cover_image_placeholder).setVisibility(View.GONE);
            coverImageView.setVisibility(View.VISIBLE);
//            coverImageView.setContentDescription(getResources().getString(
//                    R.string.navview_header_user_image_content_description));
            picasso.load(getUrlFromOssKey(coverImageUrl, Config.OSS_STYLE_PREVIEW_SM)).into(coverImageView);
//            mImageLoader.loadImage(coverImageUrl, coverImageView);
            coverImageView.setColorFilter(getResources().getColor(R.color.light_content_scrim));
        }

        email.setText(chosenAccount.name);

        profileImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_image:
                UserActivity.open(this, new BigInteger(AccountUtils.getHereProfileId(getApplicationContext())));
                break;
            default:
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * This utility method handles Up navigation intents by searching for a parent activity and
     * navigating there if defined. When using this for an activity make sure to define both the
     * native parentActivity as well as the AppCompat one when supporting API levels less than 16.
     * when the activity has a single parent activity. If the activity doesn't have a single parent
     * activity then don't define one and this method will use back button functionality. If "Up"
     * functionality is still desired for activities without parents then use
     * {@code syntheticParentActivity} to define one dynamically.
     *
     * Note: Up navigation intents are represented by a back arrow in the top left of the Toolbar
     *       in Material Design guidelines.
     *
     * @param currentActivity Activity in use when navigate Up action occurred.
     * @param syntheticParentActivity Parent activity to use when one is not already configured.
     */
    public static void navigateUpOrBack(Activity currentActivity,
                                        Class<? extends Activity> syntheticParentActivity) {
        // Retrieve parent activity from AndroidManifest.
        Intent intent = NavUtils.getParentActivityIntent(currentActivity);

        // Synthesize the parent activity when a natural one doesn't exist.
        if (intent == null && syntheticParentActivity != null) {
            try {
                intent = NavUtils.getParentActivityIntent(currentActivity, syntheticParentActivity);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (intent == null) {
            // No parent defined in manifest. This indicates the activity may be used by
            // in multiple flows throughout the app and doesn't have a strict parent. In
            // this case the navigation up button should act in the same manner as the
            // back button. This will result in users being forwarded back to other
            // applications if currentActivity was invoked from another application.
            currentActivity.onBackPressed();
        } else {
            if (NavUtils.shouldUpRecreateTask(currentActivity, intent)) {
                // Need to synthesize a backstack since currentActivity was probably invoked by a
                // different app. The preserves the "Up" functionality within the app according to
                // the activity hierarchy defined in AndroidManifest.xml via parentActivity
                // attributes.
                TaskStackBuilder builder = TaskStackBuilder.create(currentActivity);
                builder.addNextIntentWithParentStack(intent);
                builder.startActivities();
            } else {
                // Navigate normally to the manifest defined "Up" activity.
                NavUtils.navigateUpTo(currentActivity, intent);
            }
        }
    }

    private void signInOrCreateAnAccount() {

    }

    private void startLoginProcess() {

    }

    protected BigInteger getIdFromBundle(Bundle savedInstanceState, String identifier) {
        if (savedInstanceState != null) {
            CharSequence id = savedInstanceState.getCharSequence(identifier);
            if(!TextUtils.isEmpty(id)) {
                return new BigInteger(id.toString());
            }

        } else if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if(null != bundle) {
                CharSequence id = bundle.getCharSequence(identifier);
                if(!TextUtils.isEmpty(id)) {
                    return new BigInteger(id.toString());
                }
            }
        }
        return null;
    }

    /**
     * 根据OSS key 获取链接
     * @param ossKey
     * @return
     */
    public String getUrlFromOssKey(String ossKey) {
        return getUrlFromOssKey(ossKey, "");
    }

    /**
     * 根据OSS key和OSS style获取链接
     * @param ossKey
     * @param ossStyle
     * @return
     */
    public String getUrlFromOssKey(String ossKey, String ossStyle) {
        return getResources().getString(R.string.here_photo_base_url)+"/"+ossKey + ossStyle;
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

        switch (id) {
            case R.id.nav_camara:
                Intent intent = new Intent(this, AlbumBaiduActivity.class);
                Bundle bundle = new Bundle();
                bundle.putCharSequence(Config.EXTRA_ALBUM_ID, "0");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_exit:
                logout();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 获取服务器API
     * @return
     */
    protected HereApi getApi() {
        if(null == mConnectionRepository) {
            mConnectionRepository = getApplicationContext().getConnectionRepository();
        }

        try {
            return mConnectionRepository.getPrimaryConnection(HereApi.class).getApi();
        }catch (NotConnectedException ex) {
            LOGE(TAG, ex.getMessage());
            logout();
        }
        return null;
    }

    /**
     * 退出账号
     */
    protected void logout() {
        new LoginAndAuthHelper(this).logout();
        getApplicationContext().clearConnections();
        finish();
    }

    protected void setupAppbar(final Activity currentActivity) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateUpOrBack(currentActivity, null);
            }
        });
    }

    protected void showSnackbarInfo(View view, String text) {
        showSnackbarInfo(view, text, Snackbar.LENGTH_LONG);
    }

    protected void showSnackbarInfo(View view, String text, @Snackbar.Duration int duration) {
        Snackbar.make(view, text, duration)
                .setAction("Action", null).show();
    }

    protected void showSnackbarInfo(View view, @StringRes int resId) {
        showSnackbarInfo(view, getString(resId));
    }

    protected void showSnackbarInfo(View view, @StringRes int resId, @Snackbar.Duration int duration) {
        showSnackbarInfo(view, getString(resId), duration);
    }
}
