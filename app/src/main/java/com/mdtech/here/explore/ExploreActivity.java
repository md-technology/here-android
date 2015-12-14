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
import android.view.View;
import android.widget.Button;

import com.mdtech.here.R;
import com.mdtech.here.account.LoginActivity;
import com.mdtech.here.album.AlbumActivity;
import com.mdtech.here.ui.BaseActivity;
import com.mdtech.here.util.AccountUtils;

import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/14/2015.
 */
public class ExploreActivity extends BaseActivity {

    private static final String TAG = makeLogTag(ExploreActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_explore);

        Button button = (Button)findViewById(R.id.explore_ok_button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AlbumActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button login = (Button)findViewById(R.id.explore_login_button);
        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(AccountUtils.hasActiveAccount(ExploreActivity.this)) {
                    Intent intent = new Intent(v.getContext(), AlbumActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(v.getContext(), LoginActivity.class);
                    startActivity(intent);
//                    finish();
                }

            }
        });

    }
}
