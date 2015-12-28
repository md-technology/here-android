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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mdtech.geojson.Position;
import com.mdtech.here.R;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/28/2015.
 */
public abstract class MapLocateFragment extends Fragment {

    protected static final String ARG_LOCATION = "location";

    protected static MapLocateFragment newInstance(MapLocateFragment fragment, Position position) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_LOCATION, position);
        fragment.setArguments(args);
        return fragment;
    }


}
