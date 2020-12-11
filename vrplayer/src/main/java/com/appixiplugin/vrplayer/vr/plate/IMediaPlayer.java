package com.appixiplugin.vrplayer.vr.plate;

public interface IMediaPlayer {
    void play();

    void pause();

    void replay();

    boolean isPlaying();

    default void toggleControl() {
        if (isPlaying()) {
            pause();
        } else {
            play();
        }
    }

    default void fastForward() {
        fastForward(MediaConstants.DEFAULT_FAST_FORWARD_SECONDS * 1000);
    }

    default void rewind() {
        rewind(MediaConstants.DEFAULT_REWIND_SECONDS * 1000);
    }

    void fastForward(long milliseconds);

    void rewind(long milliseconds);

    void seekTo(long position);

    void seekTo(float progressPercent);

    void seekEnded();

    /**
     * If we just have 2 mode VR, then this function will use as a trigger,
     * switch between those 2 modes
     */
    default void changeMode() {
        changeMode(MediaConstants.DisplayMode.MONO);
    }

    void changeMode(MediaConstants.DisplayMode newMode);

    void changeVolume(double newVolume);

    void close();

    /**
     * Release all source for viewing this video
     */
    void release();

    long currentPosition();

    long duration();

    /**
     * Handle focus state changed in VR 3D mode
     */
    void focusChanged(boolean needShow);
}
