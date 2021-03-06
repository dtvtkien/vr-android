package com.appixiplugin.vrplayer.vr;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
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
import com.appixiplugin.vrplayer.vr.controller.vr3d.Vr3DMediaController;
import com.appixiplugin.vrplayer.vr.controller.vrflat.VrMediaController;
import com.appixiplugin.vrplayer.vr.plate.MediaConstants;
import com.asha.vrlib.MD360Director;
import com.asha.vrlib.MD360DirectorFactory;
import com.asha.vrlib.MDVRLibrary;
import com.asha.vrlib.model.BarrelDistortionConfig;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class VrPlayerView extends FrameLayout {
    private static final String TAG = VrPlayerView.class.getCanonicalName();
    private static final float THRESHOLD_MOVE_ACTION = 50f;

    // Media renderer -> VR renderer
    private MDVRLibrary vrLibrary;

    // Custom media controller & video surface view
    private SurfaceView glSurfaceView;
    private VrMediaController vrMediaController;
    private Vr3DMediaController vr3DMediaController;

    // Media player -> Using ExoPlayer2 with an adapter to adapt with [IMediaController]
    private SimpleExoPlayer exoPlayer;
    private ExoMediaPlayerAdapter vrPlayerAdapter;

    // RxAndroid objects
    private CompositeDisposable vrViewCompositeDisposable;
    private Disposable vrControllerDisposable;

    // Callback for Activity
    private MediaControllerCallback mediaControllerCallback;

    // Check if touch action MOVE is followed by action UP or not
    private boolean touchUpAfterMove;
    private float touchDownX;
    private float touchDownY;

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
        autoHideController();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDownX = ev.getX();
                touchDownY = ev.getY();
                touchUpAfterMove = false;
                break;

            case MotionEvent.ACTION_MOVE:
                float moveX = ev.getX();
                float moveY = ev.getY();
                float movedDistance = (float) Math.sqrt(Math.pow(moveX - touchDownX, 2) + Math.pow(moveY - touchDownY, 2));
                if (movedDistance > THRESHOLD_MOVE_ACTION) {
                    touchUpAfterMove = true;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // Just touch down then touch up without move
                if (!touchUpAfterMove) {
                    autoHideController();
                }
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
            case Configuration.ORIENTATION_PORTRAIT:
                autoHideController();
                break;
        }
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
        prepareMediaPlayer(path);
        prepareFlatMediaController();
        prepare3DMediaController();
    }

    private void autoHideController() {
        vrMediaController.changedVisibility(true);
        if (vrControllerDisposable != null) {
            vrViewCompositeDisposable.remove(vrControllerDisposable);
        }
        vrControllerDisposable = Observable.timer(3000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(delay -> vrMediaController.changedVisibility(false));
        vrViewCompositeDisposable.add(vrControllerDisposable);
    }

    private void prepareVrLibrary() {
        vrLibrary = MDVRLibrary.with(getContext())
                .pinchEnabled(false)
                .eyePickEnabled(true)
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

    private void prepareMediaPlayer(String path) {
        final MediaPlayerStateChanged mediaPlayerStateChanged = new MediaPlayerStateChanged() {
            @Override
            public void onMediaClosed() {
                if (mediaControllerCallback != null) {
                    mediaControllerCallback.onClosePlayer();
                }
            }

            @Override
            public void onUserInteracting() {
                vrMediaController.changedPlayState(true);
            }

            @Override
            public void onInteractiveEnded() {
                autoHideController();
            }

            @Override
            public void onDisplayModeChanged(MediaConstants.DisplayMode mode) {
                vrMediaController.changeMode(mode);
                vr3DMediaController.changedVisibility(false);
                if (mediaControllerCallback != null) {
                    mediaControllerCallback.onChangedDisplayMode(mode);
                }
            }

            @Override
            public void onPlayControlChanged(boolean isPlaying) {
                vrMediaController.changedPlayState(isPlaying);
                vr3DMediaController.changedPlayState(isPlaying);
            }

            @Override
            public void onProgressChanged(long currentPosition, long duration) {
                float currentPitch = vrLibrary.getDirectorBrief().getPitch();
                vrMediaController.changedProgress(currentPosition, duration);
                vr3DMediaController.changedProgress(currentPosition, duration);
                if (vrLibrary.getDisplayMode() != MDVRLibrary.DISPLAY_MODE_GLASS) {
                    return;
                }
                if (currentPitch < -30.0f) {
                    vrMediaController.showHideCenterIcon(true);
                    vr3DMediaController.changedVisibility(true);
                } else if (currentPitch > -16.0f) {
                    vrMediaController.showHideCenterIcon(false);
                    vr3DMediaController.changedVisibility(false);
                }
            }

            @Override
            public void onFocusChanged(boolean needShow) {
                vrMediaController.showHideFocusProgress(needShow);
            }
        };
        vrPlayerAdapter = new ExoMediaPlayerAdapter(getContext(), path, vrLibrary, exoPlayer, mediaPlayerStateChanged);
    }

    private void prepareFlatMediaController() {
        vrMediaController.setMediaPlayer(vrPlayerAdapter);
    }

    private void prepare3DMediaController() {
        vr3DMediaController = new Vr3DMediaController(getContext(), vrLibrary);
        vr3DMediaController.setMediaPlayer(vrPlayerAdapter);
    }
}
