package com.appixiplugin.vrplayer.vr.controller;

public interface IMediaController {
    int DEFAULT_MAX_VIDEO_PROGRESS = 1000;

    void changedPlayState(boolean isPlaying);

    void changedProgress(long currentPosition, long duration);
}
