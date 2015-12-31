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

package com.mdtech.here.util;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;

import com.squareup.picasso.Transformation;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/31/2015.
 */
public class SquareTransformation implements Transformation {

    private float strokeWidth = 3;
    private int color = Color.WHITE;
    private Paint.Style style = Paint.Style.STROKE;

    public SquareTransformation() {}

    public SquareTransformation(float width) {
        this.strokeWidth = width;
    }

    public SquareTransformation(@ColorInt int color, Paint.Style style, float width) {
        this.color = color;
        this.style = style;
        this.strokeWidth = width;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);

        Paint avatarPaint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        avatarPaint.setShader(shader);

        Paint outlinePaint = new Paint();
        outlinePaint.setColor(this.color);
        outlinePaint.setStyle(this.style);
        outlinePaint.setStrokeWidth(this.strokeWidth);
        outlinePaint.setAntiAlias(true);

        float r = size / 2f + this.strokeWidth;
        canvas.drawRect(r, r, r, r, outlinePaint);
        r = r - this.strokeWidth;
        canvas.drawRect(r, r, r, r, avatarPaint);
        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "squareTransformation()";
    }
}
