package com.appixiplugin.vrplayer.vr;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.appixiplugin.vrplayer.vr.callback.MediaPlayerStateChanged;
import com.appixiplugin.vrplayer.vr.filter.VrMonoDirectorFilter;
import com.appixiplugin.vrplayer.vr.filter.VrStereoDirectorFilter;
import com.appixiplugin.vrplayer.vr.plate.IMediaPlayer;
import com.appixiplugin.vrplayer.vr.plate.MediaConstants;
import com.asha.vrlib.MDVRLibrary;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class ExoMediaPlayerAdapter implements IMediaPlayer {
    private static final String TAG = ExoMediaPlayerAdapter.class.getCanonicalName();

    // Context
    private final Context context;

    // Video link
    private final String videoLink;

    // EXO interface player & media renderer
    private final MDVRLibrary mdVrLibrary;
    private final SimpleExoPlayer exoPlayer;

    // Callback
    private final MediaPlayerStateChanged callback;

    // Media information
    private boolean livePlaying;
    private long liveCurrentPosition, liveDuration;

    // Timer for video's progress changed
    private CompositeDisposable mediaCompositeDisposable;

    // Handle events change modes
    private BehaviorSubject<MediaConstants.DisplayMode> displayModeSubject;
    private BehaviorSubject<MediaConstants.InteractiveMode> interactiveModeSubject;

    public ExoMediaPlayerAdapter(Context context,
                                 String videoLink,
                                 MDVRLibrary mdVrLibrary,
                                 SimpleExoPlayer exoPlayer,
                                 MediaPlayerStateChanged callback) {
        assert mdVrLibrary != null : "MD360 Renderer VR library mus be non null!!!";
        assert exoPlayer != null : "ExoPlayer must be non null!!!";
        this.context = context;
        this.videoLink = videoLink;
        this.mdVrLibrary = mdVrLibrary;
        this.exoPlayer = exoPlayer;
        this.callback = callback;
        initMediaInformation();
        setupExoPlayer();
    }

    @Override
    public void play() {
        changeControlState(true);
    }

    @Override
    public void pause() {
        changeControlState(false);
    }

    @Override
    public void replay() {

    }

    @Override
    public boolean isPlaying() {
        return livePlaying;
    }

    @Override
    public void fastForward(long milliseconds) {
        long newPosition = liveCurrentPosition + milliseconds;
        seekTo(Math.min(newPosition, liveDuration));
    }

    @Override
    public void rewind(long milliseconds) {
        long newPosition = liveCurrentPosition - milliseconds;
        seekTo(Math.max(newPosition, 0));
    }

    @Override
    public void seekTo(long position) {
        exoPlayer.seekTo(position);
    }

    @Override
    public void seekTo(float progressPercent) {
        long seekToPosition = (long) (progressPercent * liveDuration);
        exoPlayer.seekTo(seekToPosition);
        if (callback != null) {
            callback.onUserInteracting();
        }
    }

    @Override
    public void seekEnded() {
        if (callback != null) {
            callback.onInteractiveEnded();
        }
    }

    @Override
    public void changeMode() {
        MediaConstants.DisplayMode currentMode = displayModeSubject.getValue();
        MediaConstants.DisplayMode newMode = MediaConstants.DisplayMode.MONO;
        switch (currentMode) {
            case MONO:
                newMode = MediaConstants.DisplayMode.STEREO;
                break;
            case STEREO:
                newMode = MediaConstants.DisplayMode.MONO;
                break;
        }
        changeMode(newMode);
    }

    @Override
    public void changeMode(MediaConstants.DisplayMode newMode) {
        displayModeSubject.onNext(newMode);
        if (callback != null) {
            callback.onDisplayModeChanged(newMode);
        }
    }

    @Override
    public void changeVolume(double newVolume) {

    }

    @Override
    public void close() {
        // Mode Mono: Pop to previous screen
        // Mode Stereo: Back to mode Mono
        if (displayModeSubject.getValue() == MediaConstants.DisplayMode.STEREO) {
            changeMode(MediaConstants.DisplayMode.MONO);
            return;
        }
        if (callback != null) {
            callback.onMediaClosed();
        }
//        release();
    }

    @Override
    public void release() {
        mediaCompositeDisposable.dispose();
        mdVrLibrary.onDestroy();
        exoPlayer.setVideoSurface(null);
        exoPlayer.release();
    }

    @Override
    public long currentPosition() {
        return liveCurrentPosition;
    }

    @Override
    public long duration() {
        return liveDuration;
    }

    private void setupExoPlayer() {
        exoPlayer.addListener(new ExoPlayerListener());
        MediaSource mediaSource = buildMediaSource(videoLink);
        if (mediaSource != null) {
            exoPlayer.prepare(mediaSource);
        }
    }

    private void initMediaInformation() {
        // Init media variables
        livePlaying = false;
        liveCurrentPosition = 0L;
        liveDuration = 0L;
        // Init Rx Objects
        mediaCompositeDisposable = new CompositeDisposable();
        // For DisplayMode
        displayModeSubject = BehaviorSubject.create();
        displayModeSubject.onNext(MediaConstants.DisplayMode.MONO);
        mediaCompositeDisposable.add(displayModeSubject.subscribe(displayMode -> {
            int mdVrProjectionMode = 0;
            int mdVrDisplayMode = 0;
            int mdVrInteractiveMode = 0;
            MDVRLibrary.IDirectorFilter directorFilter = null;
            boolean needAntiDistort = false;
            switch (displayMode) {
                case MONO:
                    mdVrProjectionMode = MDVRLibrary.PROJECTION_MODE_HEMISPHERE;
                    mdVrDisplayMode = MDVRLibrary.DISPLAY_MODE_NORMAL;
                    mdVrInteractiveMode = MDVRLibrary.INTERACTIVE_MODE_TOUCH;
                    directorFilter = new VrMonoDirectorFilter();
                    break;
                case STEREO:
                    mdVrProjectionMode = MDVRLibrary.PROJECTION_MODE_STEREO_HEMISPHERE;
                    mdVrDisplayMode = MDVRLibrary.DISPLAY_MODE_GLASS;
                    mdVrInteractiveMode = MDVRLibrary.INTERACTIVE_MODE_CARDBORAD_MOTION;
                    directorFilter = new VrStereoDirectorFilter();
                    needAntiDistort = true;
                    break;
            }
            mdVrLibrary.switchProjectionMode(context, mdVrProjectionMode);
            mdVrLibrary.switchDisplayMode(context, mdVrDisplayMode);
            mdVrLibrary.switchInteractiveMode(context, mdVrInteractiveMode);
            mdVrLibrary.setDirectorFilter(directorFilter);
            mdVrLibrary.setAntiDistortionEnabled(needAntiDistort);
            mdVrLibrary.onResume(context);
        }));
        // For InteractiveMode
        interactiveModeSubject = BehaviorSubject.create();
        interactiveModeSubject.onNext(MediaConstants.InteractiveMode.TOUCH);
        mediaCompositeDisposable.add(interactiveModeSubject.subscribe(interactiveMode -> {
            int mdVrInteractiveMode = 0;
            switch (interactiveMode) {
                case TOUCH:
                    mdVrInteractiveMode = MDVRLibrary.INTERACTIVE_MODE_TOUCH;
                    break;
                case MOTION:
                    mdVrInteractiveMode = MDVRLibrary.INTERACTIVE_MODE_CARDBORAD_MOTION;
                    break;
            }
            mdVrLibrary.switchInteractiveMode(context, mdVrInteractiveMode);
        }));
    }

    private void setupTimer() {
        Observable<Long> playbackProgressObservable = Observable.interval(1, TimeUnit.SECONDS);
        mediaCompositeDisposable.add(playbackProgressObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(period -> updateUIOnProgress()));
    }

    private void updateUIOnProgress() {
        liveCurrentPosition = exoPlayer.getCurrentPosition();
        liveDuration = exoPlayer.getDuration();
        if (callback != null) {
            callback.onProgressChanged(liveCurrentPosition, liveDuration);
        }
    }

    private void changeControlState(boolean wantToPlay) {
        // Handle Player
        exoPlayer.setPlayWhenReady(wantToPlay);
        // Callback
        livePlaying = wantToPlay;
        if (callback != null) {
            callback.onPlayControlChanged(wantToPlay);
        }
    }

    private MediaSource buildMediaSource(String path) {
        return buildMediaSource(Uri.parse(path));
    }

    private MediaSource buildMediaSource(Uri uri) {
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context, "VR-app");
        // Create a media source using the supplied URI
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    private class ExoPlayerListener implements Player.EventListener {
        @Override
        public void onTimelineChanged(Timeline timeline, int reason) {
            Log.d(TAG, "onTimelineChanged: " + timeline);
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            Log.d(TAG, "onPlayerStateChanged: " + playbackState);
            switch (playbackState) {
                case Player.STATE_READY:
                    setupTimer();
                    break;

                case Player.STATE_IDLE:
                    break;

                case Player.STATE_BUFFERING:
                    break;

                case Player.STATE_ENDED:
                    break;
            }
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            Log.d(TAG, "onIsPlayingChanged: " + isPlaying);
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            Log.d(TAG, "onPlayerError: " + error);
        }

        @Override
        public void onSeekProcessed() {
            Log.d(TAG, "onSeekProcessed: ");
        }
    }
}
