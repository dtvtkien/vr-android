package com.appixiplugin.vrandroid;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.appixiplugin.vrplayer.presentation.VrPlayerActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), VrPlayerActivity.class);
            String jsonSeries = "{\n" +
                    "  \"video_data\": [\n" +
                    "    {\n" +
                    "      \"id\": 1,\n" +
                    "      \"title\": \"1.1.舞台deVRバーチャルコンサート/体験動画\",\n" +
                    "      \"duration\": \"\",\n" +
                    "      \"url\": \"https://stage-vr-test.s3-ap-southeast-1.amazonaws.com/videos/2VRひとみ座_9月0日稽古風景2-2K.mp4\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
            intent.putExtra(VrPlayerActivity.INTENT_EXTRA_PATH, jsonSeries);
            view.getContext().startActivity(intent);
        });
    }
}