/*
 * Copyright (C) 2013 AChep@xda <artemchep@gmail.com>
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

package com.carrey.e_bus_home.anim;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

public class FadingHeadViewHelper {


    private int mAlpha = 255;
    private Drawable mDrawable;
    private boolean isAlphaLocked;

    private final View mHeadView;
    private ImageView scanner , message;

    public FadingHeadViewHelper(final View actionBar) {
        mHeadView = actionBar;
    }

    public FadingHeadViewHelper(final View headView, final Drawable drawable) {
        mHeadView = headView;
        setHeadViewBackgroundDrawable(drawable);
    }

    public void setHeadViewBackgroundDrawable(Drawable drawable) {
        setHeadViewBackgroundDrawable(drawable, true);
    }

    public void setHeadViewBackgroundDrawable(Drawable drawable, boolean mutate) {
        mDrawable = mutate ? drawable.mutate() : drawable;
        mHeadView.setBackgroundDrawable(mDrawable);

        if (mAlpha == 255) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                mAlpha = mDrawable.getAlpha();
        } else {
            setHeadViewAlpha(mAlpha);
        }
    }

    public void setHeadViewAlpha(int alpha) {
        if (mDrawable != null) {
            if (!isAlphaLocked) {
                mDrawable.setAlpha(alpha);
            }
            mAlpha = alpha;
        }
    }
}
