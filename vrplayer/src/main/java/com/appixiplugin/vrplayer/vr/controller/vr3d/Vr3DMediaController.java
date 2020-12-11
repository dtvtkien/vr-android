package com.appixiplugin.vrplayer.vr.controller.vr3d;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;

import com.appixiplugin.vrplayer.R;
import com.appixiplugin.vrplayer.utils.DurationUtils;
import com.appixiplugin.vrplayer.vr.controller.IMediaController;
import com.appixiplugin.vrplayer.vr.plate.IMediaPlayer;
import com.asha.vrlib.MDVRLibrary;
import com.asha.vrlib.model.MDHitPoint;
import com.asha.vrlib.model.MDPosition;
import com.asha.vrlib.model.MDViewBuilder;
import com.asha.vrlib.plugins.hotspot.IMDHotspot;
import com.asha.vrlib.plugins.hotspot.MDAbsHotspot;
import com.asha.vrlib.plugins.hotspot.MDAbsView;
import com.asha.vrlib.plugins.hotspot.MDView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Vr3DMediaController implements IMediaController {
    private static final String TAG = Vr3DMediaController.class.getCanonicalName();
    private static final float CONTROLLER_YAW = -90.0f; // Flat on the floor
    private static final float CONTROLLER_DISTANCE_TO_EYE = -14.0f; // Distance from controller to eyes
    private static final int FOCUSED_CHECK_IN_SECONDS = 3;

    private MDVRLibrary mdvrLibrary;
    private Context context;
    private IMediaPlayer iMediaPlayer;

    /**
     * Stored last hit point on 3D object (u,v,t) (got by ray hit test)
     */
    private MDHitPoint currentHitPoint;

    /**
     * All VR 3D views
     */
    private Map<String, IRender3DView> components;

    /**
     * Stored last focused tag on VR mode before started [FOCUSED_CHECK_IN_SECONDS] checking
     */
    private String lastFocusedTag;

    private String currentTag;

    /**
     * Rx disposable for handling subscription of [lastFocusedTag]
     */
    private Disposable checkFocusDisposable;

    public Vr3DMediaController(Context context, MDVRLibrary mdvrLibrary) {
        assert mdvrLibrary != null : "Need initialize MDVRLibrary first";
        this.mdvrLibrary = mdvrLibrary;
        this.context = context;
        this.components = new HashMap<>();
        setupListener();
    }

    public void setMediaPlayer(IMediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            iMediaPlayer = mediaPlayer;
        }
    }

    @Override
    public void changedPlayState(boolean isPlaying) {
        MDAbsView mdView = mdvrLibrary.findViewByTag(Vr3DViewType.play.tag);
        if (mdView != null) {
            AppCompatImageView controlView = mdView.castAttachedView(AppCompatImageView.class);
            controlView.setImageResource(isPlaying ? R.drawable.ic_vr_pause : R.drawable.ic_vr_play);
            mdView.invalidate();
        }
    }

    @Override
    public void changedProgress(long currentPosition, long duration) {
        // Convert current position into duration
        int percent = (int) ((currentPosition * 1.0 / duration) * DEFAULT_MAX_VIDEO_PROGRESS);
        String positionString = DurationUtils.convertMilliseconds(currentPosition);
        String durationString = DurationUtils.convertMilliseconds(duration);

        // Fill in views
        MDAbsView leftDurationView = mdvrLibrary.findViewByTag(Vr3DViewType.leftDuration.tag);
        if (leftDurationView != null) {
            AppCompatTextView textView = leftDurationView.castAttachedView(AppCompatTextView.class);
            textView.setText(positionString);
            leftDurationView.invalidate();
        }
        MDAbsView rightDurationView = mdvrLibrary.findViewByTag(Vr3DViewType.rightDuration.tag);
        if (rightDurationView != null) {
            AppCompatTextView textView = rightDurationView.castAttachedView(AppCompatTextView.class);
            textView.setText(durationString);
            rightDurationView.invalidate();
        }
        MDAbsView durationSeekBar = mdvrLibrary.findViewByTag(Vr3DViewType.seekBar.tag);
        if (durationSeekBar != null) {
            AppCompatSeekBar seekBar = durationSeekBar.castAttachedView(AppCompatSeekBar.class);
            seekBar.setProgress(percent);
            durationSeekBar.invalidate();
        }
    }

    @Override
    public void changedVisibility(boolean visible) {
        assert iMediaPlayer != null : "Need initialize [MediaPlayer] interface first!";
        if ((!components.isEmpty() && visible) || (components.isEmpty() && !visible)) {
            // No need to create or remove 3D views again
            return;
        }
        components.clear();
        if (visible) {
            long currentPosition = iMediaPlayer.currentPosition();
            long duration = iMediaPlayer.duration();
            int percent = (int) ((currentPosition * 1.0 / duration) * DEFAULT_MAX_VIDEO_PROGRESS);
            String positionString = DurationUtils.convertMilliseconds(currentPosition);
            String durationString = DurationUtils.convertMilliseconds(duration);
            for (Vr3DViewType viewType : Vr3DViewType.values()) {
                String tag = viewType.getTag();
                MDPosition position = MDPosition.newInstance()
                        .setX(viewType.getX())
                        .setY(viewType.getY())
                        .setZ(viewType.getZ())
                        .setYaw(CONTROLLER_YAW);
                IRender3DView view3D = null;
                float width = viewType.getWidth();
                float height = viewType.getHeight();
                switch (viewType) {
                    case container:
                        View containerView = new View(context);
//                        containerView.setBackgroundColor(0x88908894);
                        containerView.setBackgroundColor(0x00FFFFFF);
                        view3D = new Vr3DView(tag, position, containerView, width, height);
                        break;
                    case titleVideo:
                        AppCompatTextView titleVideoTextView = new AppCompatTextView(context);
                        titleVideoTextView.setText("1.1.舞台deVRバーチャルコンサート/体験動画");
                        titleVideoTextView.setTextColor(Color.WHITE);
                        view3D = new Vr3DView(tag, position, titleVideoTextView, width, height);
                        break;
                    case skipPrevious:
                        view3D = new Vr3DWidget(tag, position,
                                R.drawable.ic_vr_skip_previous, R.drawable.ic_vr_skip_previous, width, height);
                        break;
                    case rewind30s:
                        view3D = new Vr3DWidget(tag, position,
                                R.drawable.ic_vr_rewind_30, R.drawable.ic_vr_rewind_30, width, height);
                        break;
                    case play:
                        AppCompatImageView controlView = new AppCompatImageView(context);
                        controlView.setImageResource(iMediaPlayer.isPlaying() ? R.drawable.ic_vr_pause : R.drawable.ic_vr_play);
                        view3D = new Vr3DView(tag, position, controlView, width, height);
                        break;
                    case fastForward30s:
                        view3D = new Vr3DWidget(tag, position,
                                R.drawable.ic_vr_fast_forward_30, R.drawable.ic_vr_fast_forward_30, width, height);
                        break;
                    case skipNext:
                        view3D = new Vr3DWidget(tag, position,
                                R.drawable.ic_vr_skip_next, R.drawable.ic_vr_skip_next, width, height);
                        break;
                    case seekBar:
                        AppCompatSeekBar durationSeekBar = new AppCompatSeekBar(context);
                        durationSeekBar.setPadding(0, 0, 0, 0);
                        durationSeekBar.setMax(DEFAULT_MAX_VIDEO_PROGRESS);
                        durationSeekBar.setProgress(percent);
                        durationSeekBar.getProgressDrawable().setColorFilter(Color.parseColor("#E50000"), PorterDuff.Mode.SRC_IN);
                        durationSeekBar.getThumb().setColorFilter(Color.parseColor("#E50000"), PorterDuff.Mode.SRC_IN);
                        view3D = new Vr3DView(tag, position, durationSeekBar, width, height);
                        break;
                    case leftDuration:
                        AppCompatTextView leftDurationTextView = new AppCompatTextView(context);
                        leftDurationTextView.setText(positionString);
                        leftDurationTextView.setTextColor(Color.WHITE);
                        leftDurationTextView.setTextSize(12.0f);
                        view3D = new Vr3DView(tag, position, leftDurationTextView, width, height);
                        break;
                    case rightDuration:
                        AppCompatTextView rightDurationTextView = new AppCompatTextView(context);
                        rightDurationTextView.setText(durationString);
                        rightDurationTextView.setTextColor(Color.WHITE);
                        rightDurationTextView.setTextSize(12.0f);
                        view3D = new Vr3DView(tag, position, rightDurationTextView, width, height);
                        break;
                }
                if (view3D != null) {
                    components.put(tag, view3D);
                    mdvrLibrary.addPlugin(view3D.create3DView(context));
                }
            }
        } else {
            mdvrLibrary.removePlugins();
        }
    }

    private void setupListener() {
        mdvrLibrary.setEyePickChangedListener(hitEvent -> {
            currentHitPoint = hitEvent.getHitPoint();
            IMDHotspot hitHotspot = hitEvent.getHotspot();
            if (hitHotspot instanceof MDAbsHotspot) {
                MDAbsHotspot widgetPlugin = (MDAbsHotspot) hitHotspot;
                checkFocusTag(widgetPlugin.getTag());
            } else {
                checkFocusTag(null);
            }
        });
    }

    private void trigger3DAction() {
        Vr3DViewType triggeredView = Vr3DViewType.getTypeByTag(lastFocusedTag);
        switch (triggeredView) {
            case play:
                iMediaPlayer.toggleControl();
                break;
            case seekBar:
                if (currentHitPoint != null) {
                    float currentPercentDuration = currentHitPoint.getU();
                    iMediaPlayer.seekTo(currentPercentDuration);
                }
                break;
        }
    }

    private void checkFocusTag(String tag) {
        currentTag = tag;
        if (lastFocusedTag != null && !lastFocusedTag.equalsIgnoreCase(tag)
                && !Vr3DViewType.container.tag.equalsIgnoreCase(tag)) {
            iMediaPlayer.focusChanged(false);
            lastFocusedTag = tag;
            checkFocusDisposable.dispose();
            return;
        }
        if (checkFocusDisposable != null && !checkFocusDisposable.isDisposed()) {
            return;
        }
        if (!couldFocusTag(tag)) {
            // No need to store if focused nothing
            return;
        }
        iMediaPlayer.focusChanged(true);
        lastFocusedTag = tag;
        checkFocusDisposable = Observable.interval(FOCUSED_CHECK_IN_SECONDS, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(period -> {
                    iMediaPlayer.focusChanged(false);
                    checkFocusDisposable.dispose();
                    if (!lastFocusedTag.equalsIgnoreCase(currentTag)) {
                        return;
                    }
                    trigger3DAction();
                });
    }

    private boolean couldFocusTag(String tag) {
        return Vr3DViewType.play.tag.equalsIgnoreCase(tag)
                || Vr3DViewType.skipPrevious.tag.equalsIgnoreCase(tag)
                || Vr3DViewType.skipNext.tag.equalsIgnoreCase(tag)
                || Vr3DViewType.rewind30s.tag.equalsIgnoreCase(tag)
                || Vr3DViewType.fastForward30s.tag.equalsIgnoreCase(tag)
                || Vr3DViewType.seekBar.tag.equalsIgnoreCase(tag);
    }

    private enum Vr3DViewType {
        container("md-tag-container", 0.0f, CONTROLLER_DISTANCE_TO_EYE + 0.2f, -6.4f, 9.0f, 4.8f),
        titleVideo("md-tag-title-video", 0.0f, CONTROLLER_DISTANCE_TO_EYE, -7.6f, 7.6f, 1.6f),
        skipPrevious("md-tag-skip-previous", -2.2f, CONTROLLER_DISTANCE_TO_EYE, -6.0f, 0.4f, 0.4f),
        rewind30s("md-tag-rewind-30s", -1.2f, CONTROLLER_DISTANCE_TO_EYE, -6.0f, 0.7f, 0.7f),
        play("md-tag-play", 0.0f, CONTROLLER_DISTANCE_TO_EYE, -6.0f, 1.2f, 1.2f),
        fastForward30s("md-tag-fast-forward-30s", 1.2f, CONTROLLER_DISTANCE_TO_EYE, -6.0f, 0.7f, 0.7f),
        skipNext("md-tag-skip-next", 2.2f, CONTROLLER_DISTANCE_TO_EYE, -6.0f, 0.4f, 0.4f),
        seekBar("md-tag-seek-bar", 0.0f, CONTROLLER_DISTANCE_TO_EYE, -5.0f, 9.0f, 1.0f),
        leftDuration("md-tag-left-duration", -3.3f, CONTROLLER_DISTANCE_TO_EYE, -4.4f, 2.2f, 1.0f),
        rightDuration("md-tag-right-duration", 4.0f, CONTROLLER_DISTANCE_TO_EYE, -4.4f, 2.3f, 1.0f);

        static Vr3DViewType getTypeByTag(@NonNull String tag) {
            for (Vr3DViewType type : Vr3DViewType.values()) {
                if (type.getTag().equalsIgnoreCase(tag)) {
                    return type;
                }
            }
            return null;
        }

        private final String tag;
        private final float x;
        private final float y;
        private final float z;
        private final float width;
        private final float height;

        Vr3DViewType(String tag, float x, float y, float z, float width, float height) {
            this.tag = tag;
            this.x = x;
            this.y = y;
            this.z = z;
            this.width = width;
            this.height = height;
        }

        public String getTag() {
            return tag;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getZ() {
            return z;
        }

        public float getWidth() {
            return width;
        }

        public float getHeight() {
            return height;
        }
    }
}
