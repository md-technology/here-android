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

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.mdtech.here.R;
import com.mdtech.here.util.AccountUtils;

import java.util.List;

import static com.mdtech.here.util.LogUtils.LOGD;
import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/14/2015.
 */
public class AccountFragment extends WelcomeFragment
        implements WelcomeActivity.WelcomeActivityContent, RadioGroup.OnCheckedChangeListener {
    private static final String TAG = makeLogTag(AccountFragment.class);

    private List<Account> mAccounts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.welcome_account_fragment, container, false);
        if (mAccounts == null) {
            LOGD(TAG, "No accounts to display.");
            return null;
        }
        return layout;
    }
        @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }

    @Override
    public boolean shouldDisplay(Context context) {
        Account account = AccountUtils.getActiveAccount(context);
        if (account == null) {
            return true;
        }
        return false;
    }

    @Override
    protected String getPositiveText() {
        return null;
    }

    @Override
    protected String getNegativeText() {
        return null;
    }

    @Override
    protected View.OnClickListener getPositiveListener() {
        return null;
    }

    @Override
    protected View.OnClickListener getNegativeListener() {
        return null;
    }
}
