package com.appixiplugin.vrplayer.utils;

import android.annotation.SuppressLint;

public final class DurationUtils {
    @SuppressLint("DefaultLocale")
    public static String convertMilliseconds(long duration) {
        long durationInSeconds = duration / 1000;
        long s = durationInSeconds % 60;
        long m = (durationInSeconds / 60) % 60;
        long h = (durationInSeconds / (60 * 60)) % 24;
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}
