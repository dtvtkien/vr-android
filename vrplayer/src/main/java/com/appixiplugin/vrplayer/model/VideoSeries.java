package com.appixiplugin.vrplayer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoSeries {
    @SerializedName("video_data")
    @Expose
    private List<VideoModel> videoData = null;

    public List<VideoModel> getVideoData() {
        return videoData;
    }

    public void setVideoData(List<VideoModel> videoData) {
        this.videoData = videoData;
    }
}
