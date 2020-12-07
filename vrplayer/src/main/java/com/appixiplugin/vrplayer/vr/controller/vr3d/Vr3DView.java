package com.appixiplugin.vrplayer.vr.controller.vr3d;

import android.content.Context;
import android.view.View;

import com.asha.vrlib.model.MDPosition;
import com.asha.vrlib.model.MDViewBuilder;
import com.asha.vrlib.plugins.hotspot.MDAbsHotspot;
import com.asha.vrlib.plugins.hotspot.MDView;

public class Vr3DView implements IRender3DView {
    private final String tag3D;
    private final MDPosition position;
    private final View view;
    private final float width3D;
    private final float height3D;
    private final float aspectFlatWith3D;

    public Vr3DView(String tag3D, MDPosition position, View view) {
        this(tag3D, position, view, DEFAULT_WIDTH_3D, DEFAULT_HEIGHT_3D, DEFAULT_ASPECT_FLAT_WITH_3D);
    }

    public Vr3DView(String tag3D, MDPosition position, View view, float width3D, float height3D) {
        this(tag3D, position, view, width3D, height3D, DEFAULT_ASPECT_FLAT_WITH_3D);
    }

    public Vr3DView(String tag3D, MDPosition position, View view, float width3D, float height3D, float aspectFlatWith3D) {
        assert tag3D != null;
        assert position != null;
        assert view != null;
        this.tag3D = tag3D;
        this.position = position;
        this.view = view;
        this.width3D = width3D;
        this.height3D = height3D;
        this.aspectFlatWith3D = aspectFlatWith3D;
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
    public float getWidth3D() {
        return width3D;
    }

    @Override
    public float getHeight3D() {
        return height3D;
    }

    @Override
    public MDAbsHotspot create3DView(Context context) {
        MDViewBuilder builder = MDViewBuilder.create()
                .provider(view, (int) (getWidth3D() * aspectFlatWith3D), (int) (getHeight3D() * aspectFlatWith3D))
                .size(getWidth3D(), getHeight3D())
                .position(getPosition())
                .tag(getTag3D())
                .title(getTag3D());
        return new MDView(builder);
    }
}
