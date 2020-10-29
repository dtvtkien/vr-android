package com.appixiplugin.vrplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.appixiplugin.vrplayer.base.BaseActivity;
import com.appixiplugin.vrplayer.databinding.ActivityVrPlayerBinding;

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
        getBinding().setActivity(this);
        Intent intent = getIntent();
        String urlStr = intent.getStringExtra(INTENT_EXTRA_PATH);
        getBinding().player.prepare(urlStr);
    }

    public void onBackClick() {
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getBinding().player.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getBinding().player.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBinding().player.onResume();
    }
}