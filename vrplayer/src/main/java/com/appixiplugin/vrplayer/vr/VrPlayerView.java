package com.appixiplugin.vrplayer.vr;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import com.asha.vrlib.MD360Director;
import com.asha.vrlib.MD360DirectorFactory;
import com.asha.vrlib.MDVRLibrary;
import com.asha.vrlib.model.MDPinchConfig;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

public class VrPlayerView extends FrameLayout implements VrMediaController.VRControl {
    private SurfaceView glSurfaceView;
    private MDVRLibrary vrLibrary;
    private SimpleExoPlayer exoPlayer;

    public VrPlayerView(@NonNull Context context) {
        super(context);
        init();
    }

    public VrPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VrPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VrPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setKeepScreenOn(true);
        glSurfaceView = new GLSurfaceView(getContext());
        addView(glSurfaceView);
    }

    public void prepare(String path) {
        exoPlayer = new SimpleExoPlayer.Builder(getContext()).build();
        MediaSource mediaSource = buildMediaSource(path);
        if (exoPlayer != null && mediaSource != null) {
            exoPlayer.prepare(mediaSource);
            prepareVrLibrary();
        }
    }

    private void prepareVrLibrary() {
        vrLibrary = MDVRLibrary.with(getContext())
                .displayMode(MDVRLibrary.DISPLAY_MODE_GLASS)
                .interactiveMode(MDVRLibrary.INTERACTIVE_MODE_CARDBORAD_MOTION)
                .projectionMode(MDVRLibrary.PROJECTION_MODE_SPHERE)
                .pinchConfig(new MDPinchConfig().setDefaultValue(0.7f).setMin(0.5f))
                .pinchEnabled(true)
                .directorFactory(new MD360DirectorFactory() {
                    @Override
                    public MD360Director createDirector(int index) {
                        return MD360Director.builder().build();
                    }
                })
                .asVideo(surface -> {
                    // IjkMediaPlayer || MediaPlayer || ExoPlayer
                    exoPlayer.setVideoSurface(surface);
                })
                .build(glSurfaceView);
        vrLibrary.setAntiDistortionEnabled(true);
    }

    @Override
    public void onInteractiveClick(int currentMode) {
        if (currentMode == MDVRLibrary.INTERACTIVE_MODE_CARDBORAD_MOTION) {
            vrLibrary.switchInteractiveMode(getContext(), MDVRLibrary.INTERACTIVE_MODE_TOUCH);
        } else {
            vrLibrary.switchInteractiveMode(getContext(), MDVRLibrary.INTERACTIVE_MODE_CARDBORAD_MOTION);
        }
    }

    @Override
    public void onDisplayClick(int currentMode) {
        if (currentMode == MDVRLibrary.DISPLAY_MODE_GLASS) {
            vrLibrary.switchDisplayMode(getContext(), MDVRLibrary.DISPLAY_MODE_NORMAL);
            vrLibrary.setAntiDistortionEnabled(false);
        } else {
            vrLibrary.switchDisplayMode(getContext(), MDVRLibrary.DISPLAY_MODE_GLASS);
            vrLibrary.setAntiDistortionEnabled(true);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (vrLibrary != null) {
            vrLibrary.handleTouchEvent(event);
        }
        return true;
    }

    public void onPause() {
        if (vrLibrary != null) {
            vrLibrary.onPause(getContext());
        }
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
        }
    }

    public void onResume() {
        if (vrLibrary != null) {
            vrLibrary.onResume(getContext());
        }
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
        }
    }

    public void onDestroy() {
        if (vrLibrary != null) {
            vrLibrary.onDestroy();
        }
        if (exoPlayer != null) {
            exoPlayer.setVideoSurface(null);
            exoPlayer.release();
        }
    }

    private MediaSource buildMediaSource(String path) {
        return buildMediaSource(Uri.parse(path));
    }

    private MediaSource buildMediaSource(Uri uri) {
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(getContext(), "VR-app");
        // Create a media source using the supplied URI
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }
}
