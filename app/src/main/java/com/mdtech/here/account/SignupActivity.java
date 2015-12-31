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

package com.mdtech.here.account;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mdtech.here.Config;
import com.mdtech.here.R;
import com.mdtech.here.ui.BaseActivity;
import com.mdtech.social.api.HereApi;
import com.mdtech.social.api.model.SignupRequest;
import com.mdtech.social.api.model.User;

import butterknife.Bind;

import static com.mdtech.here.util.LogUtils.LOGD;
import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/14/2015.
 */
public class SignupActivity extends BaseActivity {

    private static final String TAG = makeLogTag(SignupActivity.class);

    private HereApi mApi;

    @Bind(R.id.input_name)
    EditText mNameText;
    @Bind(R.id.input_email)
    EditText mEmailText;
    @Bind(R.id.input_password)
    EditText mPasswordText;
    @Bind(R.id.btn_signup)
    Button mSignupButton;
    @Bind(R.id.link_login)
    TextView mLoginLink;

    public static void open(Activity activity) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        mLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });

    }

    public void signup() {
        LOGD(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        mSignupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final String name = mNameText.getText().toString();
        final String email = mEmailText.getText().toString();
        final String password = mPasswordText.getText().toString();

        new AsyncTask<Void, Void, User>() {
            @Override
            protected User doInBackground(Void... params) {

                try {
                    return getApplicationContext().authenticateClient().getApi().signupOperations()
                            .signup(new SignupRequest(name, email, password));
                }catch (Exception ex) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(User user) {
                super.onPostExecute(user);
                if(null != user) {
                    // On complete call either onSignupSuccess or onSignupFailed
                    // depending on success
                    onSignupSuccess(user);
                    // onSignupFailed();
                    progressDialog.dismiss();
                }else {
                    onSignupFailed();
                    progressDialog.dismiss();
                }
            }
        }.execute();
    }

    public void onSignupSuccess(User user) {
        mSignupButton.setEnabled(true);
        Intent intent = new Intent();
        intent.putExtra(Config.EXTRA_USER, user);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_LONG).show();
        mSignupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = mNameText.getText().toString();
        String email = mEmailText.getText().toString();
        String password = mPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            mNameText.setError("at least 3 characters");
            valid = false;
        } else {
            mNameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailText.setError("enter a valid email address");
            valid = false;
        } else {
            mEmailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            mPasswordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            mPasswordText.setError(null);
        }

        return valid;
    }
}
