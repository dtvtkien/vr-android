package com.asha.vrlib.strategy.projection;

import android.content.Context;

import com.asha.vrlib.MD360Director;
import com.asha.vrlib.MD360DirectorFactory;
import com.asha.vrlib.common.MDDirection;
import com.asha.vrlib.model.MDMainPluginBuilder;
import com.asha.vrlib.model.MDPosition;
import com.asha.vrlib.objects.MDAbsObject3D;
import com.asha.vrlib.objects.MDObject3DHelper;
import com.asha.vrlib.objects.MDStereoHemisphere3D;
import com.asha.vrlib.plugins.MDAbsPlugin;
import com.asha.vrlib.plugins.MDPanoramaPlugin;

public class StereoHemisphereProjection extends AbsProjectionStrategy {
    private static class FixedDirectorFactory extends MD360DirectorFactory {
        @Override
        public MD360Director createDirector(int index) {
            return MD360Director.builder().build();
        }
    }

    private MDDirection direction;

    private MDAbsObject3D object3D;

    public StereoHemisphereProjection(MDDirection direction) {
        this.direction = direction;
    }

    @Override
    public void turnOnInGL(Context context) {
        object3D = new MDStereoHemisphere3D(direction);
        MDObject3DHelper.loadObj(context, object3D);
    }

    @Override
    public void turnOffInGL(Context context) {

    }

    @Override
    public boolean isSupport(Context context) {
        return true;
    }

    @Override
    public MDAbsObject3D getObject3D() {
        return object3D;
    }

    @Override
    public MDPosition getModelPosition() {
        return MDPosition.getOriginalPosition();
    }

    @Override
    protected MD360DirectorFactory hijackDirectorFactory() {
        return new StereoHemisphereProjection.FixedDirectorFactory();
    }

    @Override
    public MDAbsPlugin buildMainPlugin(MDMainPluginBuilder builder) {
        return new MDPanoramaPlugin(builder);
    }
}
