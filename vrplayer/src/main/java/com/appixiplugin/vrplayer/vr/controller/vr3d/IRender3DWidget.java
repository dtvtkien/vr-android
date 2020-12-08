package com.appixiplugin.vrplayer.vr.controller.vr3d;

import android.content.Context;

import com.asha.vrlib.model.MDHotspotBuilder;
import com.asha.vrlib.plugins.MDWidgetPlugin;
import com.asha.vrlib.plugins.hotspot.MDAbsHotspot;

public interface IRender3DWidget extends IRender3DView {
    int DEFAULT_KEY_NORMAL_STATUS = 0;
    int DEFAULT_KEY_FOCUSED_STATUS = 1;

    int getAssetStateNormal();

    int getAssetStateFocused();

    default MDHotspotBuilder create3DViewBuilder(Context context) {
        return MDHotspotBuilder.create(new Vr3DImageProvider(context))
                .size(getWidth3D(), getHeight3D())
                .provider(DEFAULT_KEY_NORMAL_STATUS, context, getAssetStateNormal())
                .provider(DEFAULT_KEY_FOCUSED_STATUS, context, getAssetStateFocused())
                .tag(getTag3D())
                .title(getTag3D())
                .position(getPosition())
                .status(DEFAULT_KEY_NORMAL_STATUS, DEFAULT_KEY_FOCUSED_STATUS);
    }

    default MDAbsHotspot create3DView(Context context) {
        return new MDWidgetPlugin(create3DViewBuilder(context));
    }
}
