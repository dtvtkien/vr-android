package com.appixiplugin.vrplayer.vr.callback;

import com.appixiplugin.vrplayer.vr.plate.MediaConstants;

public interface MediaPlayerStateChanged {
    void onMediaClosed();

    void onDisplayModeChanged(MediaConstants.DisplayMode mode);

    void onPlayControlChanged(boolean isPlaying);

    void onProgressChanged(long currentPosition, long duration);
}
