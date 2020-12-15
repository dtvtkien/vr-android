package com.appixiplugin.vrplayer.utils;

import android.graphics.drawable.GradientDrawable;

public class DrawableUtils {
    public static GradientDrawable drawCircle(int backgroundColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setCornerRadii(new float[]{0, 0, 0, 0, 0, 0, 0, 0});
        shape.setColor(backgroundColor);
        return shape;
    }
}
