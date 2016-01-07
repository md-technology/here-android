/*
 * Copyright (C) 2016 The Here Android Project
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

import android.support.v4.app.Fragment;

import com.mdtech.social.api.HereApi;
import com.mdtech.social.api.model.User;

import java.math.BigInteger;
import java.util.List;

import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 1/7/2016.
 */
public class GroupListFragment extends UserListFragment {

    private static final String TAG = makeLogTag(GroupListFragment.class);

    public static Fragment newInstance(BigInteger id){
        return newInstance(new GroupListFragment(), id);
    }

    @Override
    protected int execLoadMoreTask() {
        List<User> users = mApi.userOperations().getGroups(mId, pageSize, pageNo);
        mAdapter.addAll(users);
        return users.size();
    }

}
