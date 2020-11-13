package com.appixiplugin.vrplayer.vr;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.appixiplugin.vrplayer.R;
import com.appixiplugin.vrplayer.databinding.VrMediaControllerBinding;
import com.appixiplugin.vrplayer.utils.DurationUtils;
import com.appixiplugin.vrplayer.vr.plate.IMediaPlayer;
import com.appixiplugin.vrplayer.vr.plate.MediaConstants;

class VrMediaController extends FrameLayout {
    private static final String TAG = VrMediaController.class.getCanonicalName();
    private static final int DEFAULT_MAX_VIDEO_PROGRESS = 1000;
    // Binding view
    private VrMediaControllerBinding binding;

    public VrMediaController(@NonNull Context context) {
        super(context);
        init();
    }

    public VrMediaController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VrMediaController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public VrMediaController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.vr_media_controller, this, true);
        binding.seekBarProgressMedia.setMax(DEFAULT_MAX_VIDEO_PROGRESS);
        binding.seekBarProgressMedia.setProgress(0);
        binding.imgPlayPause.setImageResource(R.drawable.ic_play);
        binding.tvStartController.setText(DurationUtils.convertMilliseconds(0));
        binding.tvEndController.setText(DurationUtils.convertMilliseconds(0));
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                show();
//                break;
//            case MotionEvent.ACTION_CANCEL:
//                hide();
//                break;
//            default:
//                break;
//        }
//        return true;
//    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                binding.motionLayoutControlBox.transitionToEnd();
                break;

            case Configuration.ORIENTATION_PORTRAIT:
                binding.motionLayoutControlBox.transitionToStart();
                break;

            default:
                break;
        }
    }

    public void show() {
        binding.motionLayoutMediaController.setVisibility(VISIBLE);
    }

    public void hide() {
        binding.motionLayoutMediaController.setVisibility(GONE);
    }

    public void changeMode(MediaConstants.DisplayMode displayMode) {
        switch (displayMode) {
            case MONO:
                binding.motionLayoutControlBox.setVisibility(VISIBLE);
                binding.constraintVolume.setVisibility(VISIBLE);
                binding.imgSettings.setVisibility(GONE);
                break;

            case STEREO:
                binding.motionLayoutControlBox.setVisibility(GONE);
                binding.constraintVolume.setVisibility(GONE);
                binding.imgSettings.setVisibility(VISIBLE);
                break;
        }
    }

    public void setMediaPlayer(IMediaPlayer mediaPlayer) {
        if (binding != null && mediaPlayer != null) {
            binding.setMediaPlayer(mediaPlayer);
        }
    }

    public void changedPlayState(boolean isPlaying) {
        binding.imgPlayPause.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
    }

    public void changedProgress(long currentPosition, long duration) {
        int percent = (int) ((currentPosition * 1.0 / duration) * DEFAULT_MAX_VIDEO_PROGRESS);
        String positionString = DurationUtils.convertMilliseconds(currentPosition);
        String durationString = DurationUtils.convertMilliseconds(duration);
        binding.tvStartController.setText(positionString);
        binding.tvEndController.setText(durationString);
        binding.seekBarProgressMedia.setProgress(percent);
    }
}
