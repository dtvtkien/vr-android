package com.appixiplugin.vrplayer.vr.callback;

import com.appixiplugin.vrplayer.vr.plate.MediaConstants;

public interface MediaControllerCallback {
    void onClosePlayer();

    void onChangedDisplayMode(MediaConstants.DisplayMode mode);
}
