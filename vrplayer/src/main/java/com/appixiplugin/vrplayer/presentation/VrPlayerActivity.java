package com.appixiplugin.vrplayer.presentation;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.appixiplugin.vrplayer.R;
import com.appixiplugin.vrplayer.base.BaseActivity;
import com.appixiplugin.vrplayer.databinding.ActivityVrPlayerBinding;
import com.appixiplugin.vrplayer.model.VideoModel;
import com.appixiplugin.vrplayer.model.VideoSeries;
import com.appixiplugin.vrplayer.vr.callback.MediaControllerCallback;
import com.appixiplugin.vrplayer.vr.plate.MediaConstants;
import com.google.gson.Gson;

import io.reactivex.rxjava3.disposables.Disposable;

public class VrPlayerActivity extends BaseActivity<ActivityVrPlayerBinding> {
    public static final String INTENT_EXTRA_PATH = "path";
    private static final String TAG = VrPlayerActivity.class.getCanonicalName();

    // Handler for parsing JSON data from Flutter
    private Disposable videoSeriesDisposable;

    @Override
    public int getLayoutId() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_vr_player;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // Init UI
        getBinding().setActivity(this);
        getBinding().player.setMediaControllerCallback(new MediaControllerCallback() {
            @Override
            public void onClosePlayer() {
                finish();
            }

            @Override
            public void onChangedDisplayMode(MediaConstants.DisplayMode mode) {
                switch (mode) {
                    case MONO:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                        break;

                    case STEREO:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        break;
                }
            }
        });
        // Passed intent
        Intent intent = getIntent();
        String jsonSeries = intent.getStringExtra(INTENT_EXTRA_PATH);
        parseVideoSeries(jsonSeries);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getBinding().player.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getBinding().player.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getBinding().player.onDestroy();
        if (videoSeriesDisposable != null) {
            videoSeriesDisposable.dispose();
        }
    }

    // TODO: Move these logic into a ViewModel class
    private void parseVideoSeries(String jsonSeries) {
        Gson gson = new Gson();
        VideoSeries series = gson.fromJson(jsonSeries, VideoSeries.class);
        VideoModel firstVideo = series.getVideoData().get(0);
        getBinding().player.prepare(firstVideo.getUrl());
        // TODO: Using RxAndroid instead
//        videoSeriesDisposable = Observable
//                .fromCallable(() -> {
//                    Gson gson = new Gson();
//                    return gson.fromJson(jsonSeries, VideoSeries.class);
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(series -> {
//                    if (series.getVideoData() != null && !series.getVideoData().isEmpty()) {
//                        VideoModel firstVideo = series.getVideoData().get(0);
//                        getBinding().player.prepare(firstVideo.getUrl());
//                    }
//                });
    }
}