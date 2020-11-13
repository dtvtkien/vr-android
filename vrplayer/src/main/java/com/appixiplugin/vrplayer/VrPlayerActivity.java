package com.appixiplugin.vrplayer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.appixiplugin.vrplayer.base.BaseActivity;
import com.appixiplugin.vrplayer.databinding.ActivityVrPlayerBinding;
import com.appixiplugin.vrplayer.vr.callback.MediaControllerCallback;
import com.appixiplugin.vrplayer.vr.plate.MediaConstants;

public class VrPlayerActivity extends BaseActivity<ActivityVrPlayerBinding> {
    public static final String INTENT_EXTRA_PATH = "path";

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
        String urlStr = intent.getStringExtra(INTENT_EXTRA_PATH);
        getBinding().player.prepare(urlStr);
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
    }
}