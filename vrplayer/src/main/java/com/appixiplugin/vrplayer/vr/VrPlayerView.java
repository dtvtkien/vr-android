package com.appixiplugin.vrplayer.vr;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
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

import com.appixiplugin.vrplayer.vr.callback.MediaControllerCallback;
import com.appixiplugin.vrplayer.vr.callback.MediaPlayerStateChanged;
import com.appixiplugin.vrplayer.vr.plate.MediaConstants;
import com.asha.vrlib.MD360Director;
import com.asha.vrlib.MD360DirectorFactory;
import com.asha.vrlib.MDVRLibrary;
import com.asha.vrlib.model.BarrelDistortionConfig;
import com.asha.vrlib.model.MDPinchConfig;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class VrPlayerView extends FrameLayout {
    // Media renderer -> VR renderer
    private MDVRLibrary vrLibrary;

    // Custom media controller & video surface view
    private SurfaceView glSurfaceView;
    private VrMediaController vrMediaController;

    // Media player -> Using ExoPlayer2 with an adapter to adapt with [IMediaController]
    private SimpleExoPlayer exoPlayer;
    private ExoMediaPlayerAdapter vrPlayerAdapter;

    // RxAndroid objects
    private CompositeDisposable vrViewCompositeDisposable;
    private Disposable vrControllerDisposable;

    // Callback for Activity
    private MediaControllerCallback mediaControllerCallback;

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
        vrViewCompositeDisposable = new CompositeDisposable();
        glSurfaceView = new GLSurfaceView(getContext());
        addView(glSurfaceView);
        vrMediaController = new VrMediaController(getContext());
        addView(vrMediaController);
//        autoHideController();
    }

    public void setMediaControllerCallback(MediaControllerCallback callback) {
        this.mediaControllerCallback = callback;
    }

    public void onPause() {
        if (vrPlayerAdapter != null) {
            vrPlayerAdapter.pause();
        }
    }

    public void onResume() {
        if (vrPlayerAdapter != null) {
            vrPlayerAdapter.play();
        }
    }

    public void onDestroy() {
        if (vrPlayerAdapter != null) {
            vrPlayerAdapter.release();
        }
        if (vrViewCompositeDisposable != null) {
            vrViewCompositeDisposable.dispose();
        }
    }

    public void prepare(String path) {
        exoPlayer = new SimpleExoPlayer.Builder(getContext()).build();
        prepareVrLibrary();
        vrPlayerAdapter = new ExoMediaPlayerAdapter(getContext(), path, vrLibrary, exoPlayer,
                new MediaPlayerStateChanged() {
                    @Override
                    public void onMediaClosed() {
                        if (mediaControllerCallback != null) {
                            mediaControllerCallback.onClosePlayer();
                        }
                    }

                    @Override
                    public void onDisplayModeChanged(MediaConstants.DisplayMode mode) {
                        vrMediaController.changeMode(mode);
                        if (mediaControllerCallback != null) {
                            mediaControllerCallback.onChangedDisplayMode(mode);
                        }
                    }

                    @Override
                    public void onPlayControlChanged(boolean isPlaying) {
                        vrMediaController.changedPlayState(isPlaying);
                    }

                    @Override
                    public void onProgressChanged(long currentPosition, long duration) {
                        vrMediaController.changedProgress(currentPosition, duration);
                    }
                });
        vrMediaController.setMediaPlayer(vrPlayerAdapter);
    }

    private void autoHideController() {
        vrMediaController.show();
        if (vrControllerDisposable != null) {
            vrViewCompositeDisposable.remove(vrControllerDisposable);
        }
        vrControllerDisposable = Observable.timer(3000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(delay -> vrMediaController.hide());
        vrViewCompositeDisposable.add(vrControllerDisposable);
    }

    private void prepareVrLibrary() {
        vrLibrary = MDVRLibrary.with(getContext())
                .projectionMode(MDVRLibrary.PROJECTION_MODE_SPHERE)
                .pinchConfig(new MDPinchConfig().setDefaultValue(0.7f).setMin(0.5f))
                .pinchEnabled(true)
                .barrelDistortionConfig(new BarrelDistortionConfig().setParamA(-0.036).setParamB(0.36))
                .directorFactory(new MD360DirectorFactory() {
                    @Override
                    public MD360Director createDirector(int index) {
                        return MD360Director.builder().build();
                    }
                })
                .asVideo(surface -> {
                    // IjkMediaPlayer || MediaPlayer || ExoPlayer
                    vrViewCompositeDisposable.add(Observable.just(surface)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(surface1 -> exoPlayer.setVideoSurface(surface1)));
                })
                .build(glSurfaceView);
    }
}
