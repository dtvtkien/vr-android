package com.appixiplugin.vrplayer.vr.filter;

import com.asha.vrlib.MDVRLibrary;

public class VrMonoDirectorFilter implements MDVRLibrary.IDirectorFilter {
    private static final int DEFAULT_ANGLE = 40;
    private int filterAngle;

    public VrMonoDirectorFilter() {
        this.filterAngle = DEFAULT_ANGLE;
    }

    public VrMonoDirectorFilter(int filterAngle) {
        this.filterAngle = filterAngle;
    }

    @Override
    public float onFilterPitch(float input) {
        if (input > filterAngle) {
            return filterAngle;
        }
        if (input < -filterAngle) {
            return -filterAngle;
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
