package com.appixiplugin.vrplayer.vr.binding;

import android.view.View;
import android.widget.SeekBar;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.databinding.BindingAdapter;

import com.appixiplugin.vrplayer.R;
import com.appixiplugin.vrplayer.vr.plate.IMediaPlayer;
import com.jakewharton.rxbinding4.view.RxView;

import java.util.concurrent.TimeUnit;

public class VrMediaBindingAdapter {
    private static final int DEFAULT_THROTTLE_IN_MILLIS = 300;

    @BindingAdapter("android:onClick")
    public static void bindListener(final View view, final View.OnClickListener listener) {
        RxView.clicks(view)
                .throttleFirst(DEFAULT_THROTTLE_IN_MILLIS, TimeUnit.MILLISECONDS)
                .subscribe(
                        ignore -> listener.onClick(view)
                );
    }

    @BindingAdapter("onSeekBarChanged")
    public static void bindSeekBarChangedPosition(AppCompatSeekBar seekBar,
                                                  IMediaPlayer iMediaPlayer) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (iMediaPlayer != null && fromUser) {
                    float percent = progress * 1.0f / seekBar.getMax();
                    iMediaPlayer.seekTo(percent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (iMediaPlayer != null) {
                    iMediaPlayer.seekEnded();
                }
            }
        });
    }

    @BindingAdapter("toggleControlSrc")
    public static void bindToggleControlSrc(AppCompatImageView v, boolean isPlaying) {
        int drawableRes = isPlaying ? R.drawable.ic_pause : R.drawable.ic_play;
        v.setImageResource(drawableRes);
    }
}
