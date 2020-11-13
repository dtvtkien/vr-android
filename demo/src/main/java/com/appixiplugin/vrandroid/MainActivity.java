package com.appixiplugin.vrandroid;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.appixiplugin.vrplayer.VrPlayerActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), VrPlayerActivity.class);
            String path = "https://stage-vr-test.s3-ap-southeast-1.amazonaws.com/videos/2VRひとみ座_9月0日稽古風景2-2K.mp4";
            intent.putExtra(VrPlayerActivity.INTENT_EXTRA_PATH, path);
            view.getContext().startActivity(intent);
        });
    }
}