package com.appixiplugin.vrplayer.vr.controller;

public interface IMediaController {
    void changedPlayState(boolean isPlaying);

    void changedProgress(long currentPosition, long duration);
}
