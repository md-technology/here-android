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

package com.mdtech.here;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;

import com.mdtech.here.settings.SettingsUtils;
import com.mdtech.social.connect.PonmapConnectionFactory;
import com.mdtech.social.connect.PonmapServiceProvider;

import org.springframework.security.crypto.encrypt.AndroidEncryptors;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.sqlite.SQLiteConnectionRepository;
import org.springframework.social.connect.sqlite.support.SQLiteConnectionRepositoryHelper;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;

import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * {@link android.app.Application} used to initialize Analytics. Code initialized in
 * Application classes is rare since this code will be run any time a ContentProvider, Activity,
 * or Service is used by the user or system. Analytics, dependency injection, and multi-dex
 * frameworks are in this very small set of use cases.
 * Created by Tiven.wang on 12/14/2015.
 */
public class AppApplication extends Application {

    private static final String TAG = makeLogTag(AppApplication.class);
    private ConnectionFactoryRegistry connectionFactoryRegistry;
    private ConnectionRepository connectionRepository;
    private SQLiteOpenHelper repositoryHelper;

    @Override
    public void onCreate() {
        super.onCreate();
//        AnalyticsHelper.prepareAnalytics(getApplicationContext());
        SettingsUtils.markDeclinedWifiSetup(getApplicationContext(), false);

        // create a new ConnectionFactoryLocator and populate it with Facebook ConnectionFactory
        this.connectionFactoryRegistry = new ConnectionFactoryRegistry();
        this.connectionFactoryRegistry.addConnectionFactory(new PonmapConnectionFactory(getHereAppId(),
                getHereAppSecret()));

        // set up the database and encryption
        this.repositoryHelper = new SQLiteConnectionRepositoryHelper(this);
        this.connectionRepository = new SQLiteConnectionRepository(this.repositoryHelper,
                this.connectionFactoryRegistry, AndroidEncryptors.text("password", "5c0744940b5c369b"));
    }

    // ***************************************
    // Private methods
    // ***************************************
    private String getHereAppId() {
        return getString(R.string.here_app_id);
    }

    private String getHereAppSecret() {
        return getString(R.string.here_app_secret);
    }

    // ***************************************
    // Public methods
    // ***************************************
    public ConnectionRepository getConnectionRepository() {
        return this.connectionRepository;
    }

    public PonmapConnectionFactory getConnectionFactory() {
        return (PonmapConnectionFactory) this.connectionFactoryRegistry.getConnectionFactory(PonmapServiceProvider.PROVIDER_ID);
    }

}
