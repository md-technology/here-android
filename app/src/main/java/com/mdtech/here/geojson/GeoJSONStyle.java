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

package com.mdtech.here.geojson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/11/2015.
 */
public class GeoJSONStyle {

    private static final String TITLE          = "title";
    private static final String DESCRIPTION    = "description";
    private static final String MARKER_SIZE    = "marker-size";
    private static final String MARKER_SYMBOL  = "marker-symbol";
    private static final String MARKER_COLOR   = "marker-color";
    private static final String STROKE         = "stroke";
    private static final String STROKE_OPACITY = "stroke-opacity";
    private static final String STROKE_WIDTH   = "stroke-width";
    private static final String FILL           = "fill";
    private static final String FILL_OPACITY   = "fill-opacity";

    private Map<String, String> mProperties = new HashMap<String, String>();

    public GeoJSONStyle() {
    }

    public GeoJSONStyle(JSONObject properties) {
        super();
        setProperties(properties);
    }

    public GeoJSONStyle extend(JSONObject properties) {
        GeoJSONStyle geoJSONStyle = new GeoJSONStyle();
        geoJSONStyle.setProperties(this);
        geoJSONStyle.setProperties(properties);
        return geoJSONStyle;
    }

    // OPTIONAL: default ""
    // A title to show when this item is clicked or
    // hovered over
    private String title;

    // OPTIONAL: default ""
    // A description to show when this item is clicked or
    // hovered over
    private String description;

    // OPTIONAL: default "medium"
    // specify the size of the marker. sizes
    // can be different pixel sizes in different
    // implementations
    // Value must be one of
    // "small"
    // "medium"
    // "large"
    private String markerSize;

    // OPTIONAL: default ""
    // a symbol to position in the center of this icon
    // if not provided or "", no symbol is overlaid
    // and only the marker is shown
    // Allowed values include
    // - Icon ID from the Maki project at http://mapbox.com/maki/
    // - An integer 0 through 9
    // - A lowercase character "a" through "z"
    private String markerSymbol;

    // OPTIONAL: default "7e7e7e"
    // the marker's color
    //
    // value must follow COLOR RULES
    private String markerColor;

    // OPTIONAL: default "555555"
    // the color of a line as part of a polygon, polyline, or
    // multigeometry
    //
    // value must follow COLOR RULES
    private String stroke;

    // OPTIONAL: default 1.0
    // the opacity of the line component of a polygon, polyline, or
    // multigeometry
    //
    // value must be a floating point number greater than or equal to
    // zero and less or equal to than one
    private String strokeOpacity;

    // OPTIONAL: default 2
    // the width of the line component of a polygon, polyline, or
    // multigeometry
    //
    // value must be a floating point number greater than or equal to 0
    private String strokeWidth;

    // OPTIONAL: default "555555"
    // the color of the interior of a polygon
    //
    // value must follow COLOR RULES
    private String fill;

    // OPTIONAL: default 0.6
    // the opacity of the interior of a polygon. Implementations
    // may choose to set this to 0 for line features.
    //
    // value must be a floating point number greater than or equal to
    // zero and less or equal to than one
    private String fillOpacity;

    public void setProperties(GeoJSONStyle style) {
        mProperties.put(TITLE, style.getTitle());
        mProperties.put(DESCRIPTION, style.getDescription());
        mProperties.put(MARKER_SIZE, style.getMarkerSize());
        mProperties.put(MARKER_SYMBOL, style.getMarkerSymbol());
        mProperties.put(MARKER_COLOR, style.getMarkerColor());
        mProperties.put(STROKE, style.getStroke());
        mProperties.put(STROKE_OPACITY, style.getStrokeOpacity());
        mProperties.put(STROKE_WIDTH, style.getStrokeWidth());
        mProperties.put(FILL, style.getFill());
        mProperties.put(FILL_OPACITY, style.getFillOpacity());
    }

    public void setProperties(JSONObject properties) {
        try {
            mProperties.put(TITLE, properties.getString(TITLE));
        } catch (JSONException e) {
        }

        try {
            mProperties.put(DESCRIPTION, properties.getString(DESCRIPTION));
        } catch (JSONException e) {
        }

        try {
            mProperties.put(MARKER_SIZE, properties.getString(MARKER_SIZE));
        } catch (JSONException e) {
        }

        try {
            mProperties.put(MARKER_SYMBOL, properties.getString(MARKER_SYMBOL));
        } catch (JSONException e) {
        }

        try {
            mProperties.put(MARKER_COLOR, properties.getString(MARKER_COLOR));
        } catch (JSONException e) {
        }

        try {
            mProperties.put(STROKE, properties.getString(STROKE));
        } catch (JSONException e) {
        }

        try {
            mProperties.put(STROKE_OPACITY, properties.getString(STROKE_OPACITY));
        } catch (JSONException e) {
        }

        try {
            mProperties.put(STROKE_WIDTH, properties.getString(STROKE_WIDTH));
        } catch (JSONException e) {
        }

        try {
            mProperties.put(FILL, properties.getString(FILL));
        } catch (JSONException e) {
        }

        try {
            mProperties.put(FILL_OPACITY, properties.getString(FILL_OPACITY));
        } catch (JSONException e) {
        }
    }

    public String getTitle() {
        return mProperties.get(TITLE);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return mProperties.get(DESCRIPTION);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMarkerSize() {
        return mProperties.get(MARKER_SIZE);
    }

    public void setMarkerSize(String markerSize) {
        this.markerSize = markerSize;
    }

    public String getMarkerSymbol() {
        return mProperties.get(MARKER_SYMBOL);
    }

    public void setMarkerSymbol(String markerSymbol) {
        this.markerSymbol = markerSymbol;
    }

    public String getMarkerColor() {
        return mProperties.get(MARKER_COLOR);
    }

    public void setMarkerColor(String markerColor) {
        this.markerColor = markerColor;
    }

    public String getStroke() {
        return mProperties.get(STROKE);
    }

    public void setStroke(String stroke) {
        this.stroke = stroke;
    }

    public String getStrokeOpacity() {
        return mProperties.get(STROKE_OPACITY);
    }

    public void setStrokeOpacity(String strokeOpacity) {
        this.strokeOpacity = strokeOpacity;
    }

    public String getStrokeWidth() {
        return mProperties.get(STROKE_WIDTH);
    }

    public void setStrokeWidth(String strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public String getFill() {
        return mProperties.get(FILL);
    }

    public void setFill(String fill) {
        this.fill = fill;
    }

    public String getFillOpacity() {
        return mProperties.get(FILL_OPACITY);
    }

    public void setFillOpacity(String fillOpacity) {
        this.fillOpacity = fillOpacity;
    }
}
