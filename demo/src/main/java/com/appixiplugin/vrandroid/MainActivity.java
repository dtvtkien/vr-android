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
            String path = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4";
            intent.putExtra(VrPlayerActivity.INTENT_EXTRA_PATH, path);
            view.getContext().startActivity(intent);
        });
    }
}