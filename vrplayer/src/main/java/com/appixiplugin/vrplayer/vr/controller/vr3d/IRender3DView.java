package com.appixiplugin.vrplayer.vr.controller.vr3d;

import android.content.Context;

import com.asha.vrlib.model.MDPosition;
import com.asha.vrlib.plugins.hotspot.MDAbsHotspot;

public interface IRender3DView {
    float DEFAULT_WIDTH_3D = 1.0f;
    float DEFAULT_HEIGHT_3D = 1.0f;
    float DEFAULT_ASPECT_FLAT_WITH_3D = 100.0f;

    String getTag3D();

    default MDPosition getPosition() {
        return MDPosition.newInstance();
    }

    default float getWidth3D() {
        return DEFAULT_WIDTH_3D;
    }

    default float getHeight3D() {
        return DEFAULT_HEIGHT_3D;
    }

    MDAbsHotspot create3DView(Context context);
}
