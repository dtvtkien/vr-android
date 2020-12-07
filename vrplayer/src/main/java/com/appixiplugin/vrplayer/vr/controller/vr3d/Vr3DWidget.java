package com.appixiplugin.vrplayer.vr.controller.vr3d;

import androidx.annotation.LayoutRes;

import com.appixiplugin.vrplayer.BuildConfig;
import com.asha.vrlib.model.MDPosition;

public class Vr3DWidget implements IRender3DWidget {
    private final String tag3D;
    private final MDPosition position;
    @LayoutRes
    private final int normalStateAsset;
    @LayoutRes
    private final int focusedStateAsset;
    private final float width3D;
    private final float height3D;

    public Vr3DWidget(String tag3D, MDPosition position, int normalStateAsset, int focusedStateAsset) {
        this(tag3D, position, normalStateAsset, focusedStateAsset, DEFAULT_WIDTH_3D, DEFAULT_HEIGHT_3D);
    }

    public Vr3DWidget(String tag3D,
                      MDPosition position,
                      int normalStateAsset,
                      int focusedStateAsset,
                      float width3D,
                      float height3D) {
        assert tag3D != null;
        assert position != null;
        if (BuildConfig.DEBUG && normalStateAsset <= 0) {
            throw new AssertionError("Asset image for normal state of " + tag3D + " is NULL");
        }
        if (BuildConfig.DEBUG && focusedStateAsset <= 0) {
            throw new AssertionError("Asset image for focused state of " + tag3D + " is NULL");
        }
        this.tag3D = tag3D;
        this.position = position;
        this.normalStateAsset = normalStateAsset;
        this.focusedStateAsset = focusedStateAsset;
        this.width3D = width3D;
        this.height3D = height3D;
    }

    @Override
    public String getTag3D() {
        return tag3D;
    }

    @Override
    public MDPosition getPosition() {
        return position;
    }

    @Override
    public int getAssetStateNormal() {
        return normalStateAsset;
    }

    @Override
    public int getAssetStateFocused() {
        return focusedStateAsset;
    }

    @Override
    public float getWidth3D() {
        return width3D;
    }

    @Override
    public float getHeight3D() {
        return height3D;
    }
}
