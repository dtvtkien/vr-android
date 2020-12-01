package com.asha.vrlib.type;

import android.content.Context;

import com.asha.vrlib.MD360Director;

public abstract class MDVideoRender {
    abstract public void setup(Context context);
    abstract public void renderer(int index, int itemWidth, int itemHeight, MD360Director director);
}
