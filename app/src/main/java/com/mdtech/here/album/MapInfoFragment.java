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

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 1/11/2016.
 */
public abstract class MapInfoFragment extends Fragment {

    @Nullable
    @Override
    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container,
                                      Bundle savedInstanceState);

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState, int layout) {
        View root = inflater.inflate(layout, container, false);

        return root;
    }

    /**
     * Creates a new instance depending of the form factor of the device.
     *
     */
    public static MapInfoFragment newInstace(Context c) {
        return SlideableInfoFragment.newInstance();
    }

    public abstract void hide();

    public abstract boolean isExpanded();

    public abstract void minimize();
}
