package com.appixiplugin.vrplayer.vr.filter;

import com.asha.vrlib.MDVRLibrary;

public class VrStereoDirectorFilter implements MDVRLibrary.IDirectorFilter {
    private static final int DEFAULT_ANGLE = 60;
    private int filterAngle;

    public VrStereoDirectorFilter() {
        this.filterAngle = DEFAULT_ANGLE;
    }

    public VrStereoDirectorFilter(int filterAngle) {
        this.filterAngle = filterAngle;
    }

    @Override
    public float onFilterPitch(float input) {
        if (input > filterAngle + 10) {
            return filterAngle + 10;
        }
        if (input < -filterAngle - 10) {
            return -filterAngle - 10;
        }
        return input;
    }

    @Override
    public float onFilterRoll(float input) {
        if (input < -filterAngle) {
            return -filterAngle;
        }
        if (input > filterAngle) {
            return filterAngle;
        }
        return input;
    }

    @Override
    public float onFilterYaw(float input) {
        if (input < -filterAngle) {
            return -filterAngle;
        }
        if (input > filterAngle) {
            return filterAngle;
        }
        return input;
    }
}
