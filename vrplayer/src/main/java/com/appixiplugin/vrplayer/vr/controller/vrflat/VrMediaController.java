package com.appixiplugin.vrplayer.vr.controller.vrflat;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.appixiplugin.vrplayer.R;
import com.appixiplugin.vrplayer.databinding.VrMediaControllerBinding;
import com.appixiplugin.vrplayer.utils.DurationUtils;
import com.appixiplugin.vrplayer.vr.controller.IMediaController;
import com.appixiplugin.vrplayer.vr.plate.IMediaPlayer;
import com.appixiplugin.vrplayer.vr.plate.MediaConstants;

public class VrMediaController extends FrameLayout implements IMediaController {
    private static final String TAG = VrMediaController.class.getCanonicalName();
    // Binding view
    private VrMediaControllerBinding binding;
    private MediaConstants.DisplayMode currentDisplayMode;

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
        currentDisplayMode = MediaConstants.DisplayMode.MONO;
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.vr_media_controller, this, true);
        binding.seekBarProgressMedia.setMax(DEFAULT_MAX_VIDEO_PROGRESS);
        binding.seekBarProgressMedia.setProgress(0);
        binding.imgPlayPause.setImageResource(R.drawable.ic_play);
        binding.tvStartController.setText(DurationUtils.convertMilliseconds(0));
        binding.tvEndController.setText(DurationUtils.convertMilliseconds(0));
        // Animation to other layout if orientation is not portrait
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.motionLayoutControlBox.transitionToEnd();
        }
    }

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

    @Override
    public void changedPlayState(boolean isPlaying) {
        binding.imgPlayPause.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
    }

    @Override
    public void changedProgress(long currentPosition, long duration) {
        int percent = (int) ((currentPosition * 1.0 / duration) * DEFAULT_MAX_VIDEO_PROGRESS);
        String positionString = DurationUtils.convertMilliseconds(currentPosition);
        String durationString = DurationUtils.convertMilliseconds(duration);
        binding.tvStartController.setText(positionString);
        binding.tvEndController.setText(durationString);
        binding.seekBarProgressMedia.setProgress(percent);
    }

    @Override
    public void changedVisibility(boolean visible) {
        boolean couldShow = visible && currentDisplayMode == MediaConstants.DisplayMode.MONO;
        binding.motionLayoutControlBox.setVisibility(couldShow ? VISIBLE : GONE);
        binding.constraintVolume.setVisibility(couldShow ? VISIBLE : GONE);
        binding.imgSettings.setVisibility(visible && currentDisplayMode == MediaConstants.DisplayMode.STEREO ? VISIBLE : GONE);
        binding.imgClose.setVisibility(visible ? VISIBLE : GONE);
    }

    public void showHideCenterIcon(boolean needShow) {
        boolean couldShow = needShow && currentDisplayMode == MediaConstants.DisplayMode.STEREO;
        binding.view3dBlur.setVisibility(couldShow ? VISIBLE : GONE);
        boolean isFocusProgressbarShowing = binding.cpLeftEye.getVisibility() == VISIBLE;
        binding.viewCenterLeftEye.setVisibility(couldShow && !isFocusProgressbarShowing ? VISIBLE : GONE);
        binding.viewCenterRightEye.setVisibility(couldShow && !isFocusProgressbarShowing ? VISIBLE : GONE);
    }

    public void showHideFocusProgress(boolean needShow) {
        boolean couldShow = needShow && currentDisplayMode == MediaConstants.DisplayMode.STEREO;
        if (couldShow) {
            binding.cpLeftEye.setVisibility(VISIBLE);
            binding.cpRightEye.setVisibility(VISIBLE);
            binding.cpLeftEye.setProgressWithAnimation(100.0f);
            binding.cpRightEye.setProgressWithAnimation(100.0f);
            binding.viewCenterLeftEye.setVisibility(GONE);
            binding.viewCenterRightEye.setVisibility(GONE);
        } else {
            binding.cpLeftEye.setVisibility(GONE);
            binding.cpRightEye.setVisibility(GONE);
            binding.cpLeftEye.setProgress(0.0f);
            binding.cpRightEye.setProgress(0.0f);
            binding.viewCenterLeftEye.setVisibility(VISIBLE);
            binding.viewCenterRightEye.setVisibility(VISIBLE);
        }
    }

    public void changeMode(MediaConstants.DisplayMode displayMode) {
        currentDisplayMode = displayMode;
        switch (displayMode) {
            case MONO:
                binding.motionLayoutControlBox.setVisibility(VISIBLE);
                binding.constraintVolume.setVisibility(VISIBLE);
                binding.imgSettings.setVisibility(GONE);
                showHideCenterIcon(false);
                break;

            case STEREO:
                binding.motionLayoutControlBox.setVisibility(GONE);
                binding.constraintVolume.setVisibility(GONE);
                binding.imgSettings.setVisibility(GONE);
                break;
        }
    }

    public void setMediaPlayer(IMediaPlayer mediaPlayer) {
        if (binding != null && mediaPlayer != null) {
            binding.setMediaPlayer(mediaPlayer);
        }
    }
}
