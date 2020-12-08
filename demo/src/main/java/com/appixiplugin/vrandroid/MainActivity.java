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
            String url = "https://stage-vr-test.s3-ap-southeast-1.amazonaws.com/videos/2VRひとみ座_9月0日稽古風景2-2K.mp4";
//            String url = "https://stage-vr-test.s3-ap-southeast-1.amazonaws.com/videos/2VR%E3%81%B2%E3%81%A8%E3%81%BF%E5%BA%A7_9%E6%9C%880%E6%97%A5%E7%A8%BD%E5%8F%A4%E9%A2%A8%E6%99%AF2-4K.mp4";
//            String url = "https://stagevr-source.s3-ap-northeast-1.amazonaws.com/video_convert/484/MP4/484_video.mp4";
            String jsonSeries = "{\n" +
                    "  \"video_data\": [\n" +
                    "    {\n" +
                    "      \"id\": 1,\n" +
                    "      \"title\": \"1.1.舞台deVRバーチャルコンサート/体験動画\",\n" +
                    "      \"duration\": \"\",\n" +
                    "      \"url\": \"" + url + "\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
            intent.putExtra(VrPlayerActivity.INTENT_EXTRA_PATH, jsonSeries);
            view.getContext().startActivity(intent);
        });
    }
}