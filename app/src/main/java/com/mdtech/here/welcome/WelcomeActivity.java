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

package com.mdtech.here.welcome;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.mdtech.here.BuildConfig;
import com.mdtech.here.Config;
import com.mdtech.here.R;
import com.mdtech.here.album.AlbumActivity;
import com.mdtech.here.explore.ExploreActivity;
import com.mdtech.here.settings.SettingsUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mdtech.here.util.LogUtils.LOGD;
import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/14/2015.
 */
public class WelcomeActivity extends AppCompatActivity implements WelcomeFragment.WelcomeFragmentContainer {

    private static final String TAG = makeLogTag(WelcomeActivity.class);
    WelcomeActivityContent mContentFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        mContentFragment = getCurrentFragment(this);

        // If there's no fragment to use, we're done here.
        if (mContentFragment == null) {
            finish();
        }

        // Wire up the fragment
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.welcome_content, (Fragment) mContentFragment);
        fragmentTransaction.commit();

        LOGD(TAG, "Inside Create View.");

        setupAnimation();

        Button button = (Button)findViewById(R.id.welcome_btn_ok);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ExploreActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void setupAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ImageView iv = (ImageView) findViewById(R.id.logo);
            AnimatedVectorDrawable logoAnim = (AnimatedVectorDrawable) getDrawable(R.drawable.io_logo_white_anim);
            iv.setImageDrawable(logoAnim);
            logoAnim.start();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Show the debug warning if debug tools are enabled and it hasn't been shown yet.
        if (BuildConfig.ENABLE_DEBUG_TOOLS && !SettingsUtils.wasDebugWarningShown(this)) {
            displayDogfoodWarningDialog();
        }
    }

    /**
     * Display dogfood build warning and mark that it was shown.
     */
    private void displayDogfoodWarningDialog() {
        new AlertDialog.Builder(this)
                .setTitle("TODO")//Config.DOGFOOD_BUILD_WARNING_TITLE)
                .setMessage("TODO")//Config.DOGFOOD_BUILD_WARNING_TEXT)
                .setPositiveButton(android.R.string.ok, null).show();
        SettingsUtils.markDebugWarningShown(this);
    }

    @Override
    public Button getPositiveButton() {
        return null;
    }

    @Override
    public void setPositiveButtonEnabled(Boolean enabled) {

    }

    @Override
    public Button getNegativeButton() {
        return null;
    }

    @Override
    public void setNegativeButtonEnabled(Boolean enabled) {

    }

    /**
     * Get the current fragment to display.
     *
     * This is the first fragment in the list that WelcomeActivityContent.shouldDisplay().
     *
     * @param context the application context.
     * @return the WelcomeActivityContent to display or null if there's none.
     */
    private static WelcomeActivityContent getCurrentFragment(Context context) {
        List<WelcomeActivityContent> welcomeActivityContents = getWelcomeFragments();

        for (WelcomeActivityContent fragment : welcomeActivityContents) {
            if (fragment.shouldDisplay(context)) {
                return fragment;
            }
        }

        return null;
    }

    /**
     * Whether to display the WelcomeActivity.
     *
     * Decided whether any of the fragments need to be displayed.
     *
     * @param context the application context.
     * @return true if the activity should be displayed.
     */
    public static boolean shouldDisplay(Context context) {
        WelcomeActivityContent fragment = getCurrentFragment(context);
        if (fragment == null) {
            return false;
        }
        return true;
    }

    /**
     * Get all WelcomeFragments for the WelcomeActivity.
     *
     * @return the List of WelcomeFragments.
     */
    private static List<WelcomeActivityContent> getWelcomeFragments() {
        return new ArrayList<WelcomeActivityContent>(Arrays.asList(
            new AccountFragment()
        ));
    }

    /**
     * The definition of a Fragment for a use in the WelcomeActivity.
     */
    interface WelcomeActivityContent {
        /**
         * Whether the fragment should be displayed.
         *
         * @param context the application context.
         * @return true if the WelcomeActivityContent should be displayed.
         */
        public boolean shouldDisplay(Context context);
    }
}
