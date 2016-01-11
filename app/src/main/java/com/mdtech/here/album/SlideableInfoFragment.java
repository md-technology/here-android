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

package com.mdtech.here.album;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mdtech.here.R;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 1/11/2016.
 */
public class SlideableInfoFragment extends MapInfoFragment {

    protected static SlideableInfoFragment newInstance() {
        return new SlideableInfoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super
                .onCreateView(inflater, container, savedInstanceState, R.layout.map_info_bottom);

        return root;
    }

    @Override
    public void hide() {

    }

    @Override
    public boolean isExpanded() {
        return false;
    }

    @Override
    public void minimize() {

    }
}
