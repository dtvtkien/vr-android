package com.appixiplugin.vrplayer.vr.controller.vr3d;

import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;

import com.appixiplugin.vrplayer.R;
import com.appixiplugin.vrplayer.utils.DurationUtils;
import com.appixiplugin.vrplayer.vr.controller.IMediaController;
import com.asha.vrlib.MDVRLibrary;
import com.asha.vrlib.model.MDHitEvent;
import com.asha.vrlib.model.MDPosition;
import com.asha.vrlib.plugins.MDWidgetPlugin;
import com.asha.vrlib.plugins.hotspot.IMDHotspot;
import com.asha.vrlib.plugins.hotspot.MDAbsView;

import java.util.HashMap;
import java.util.Map;

public class Vr3DMediaController implements IMediaController {
    private static final String TAG = Vr3DMediaController.class.getCanonicalName();
    private static final float CONTROLLER_YAW = -90.0f; // Flat on the floor
    private static final float CONTROLLER_DISTANCE_TO_EYE = -12.0f; // Distance from controller to eyes

    private MDVRLibrary mdvrLibrary;
    /**
     * All VR 3D views
     */
    private Map<String, IRender3DView> components;

    public Vr3DMediaController(MDVRLibrary mdvrLibrary) {
        assert mdvrLibrary != null : "Need initialize MDVRLibrary first";
        this.mdvrLibrary = mdvrLibrary;
        this.components = new HashMap<>();
        setupListener();
    }

    public void initializeController(Context context) {
        components.clear();
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
                    controlView.setImageResource(R.drawable.ic_vr_play);
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
                    durationSeekBar.setMax(DEFAULT_MAX_VIDEO_PROGRESS);
                    durationSeekBar.setProgress(0);
                    durationSeekBar.getProgressDrawable().setColorFilter(Color.parseColor("#E50000"), PorterDuff.Mode.SRC_IN);
                    durationSeekBar.getThumb().setColorFilter(Color.parseColor("#E50000"), PorterDuff.Mode.SRC_IN);
                    view3D = new Vr3DView(tag, position, durationSeekBar, width, height);
                    break;
                case leftDuration:
                    AppCompatTextView leftDurationTextView = new AppCompatTextView(context);
                    leftDurationTextView.setText("00:06:02");
                    leftDurationTextView.setTextColor(Color.WHITE);
                    view3D = new Vr3DView(tag, position, leftDurationTextView, width, height);
                    break;
                case rightDuration:
                    AppCompatTextView rightDurationTextView = new AppCompatTextView(context);
                    rightDurationTextView.setText("/ 00:20:08");
                    rightDurationTextView.setTextColor(Color.WHITE);
                    view3D = new Vr3DView(tag, position, rightDurationTextView, width, height);
                    break;
                case focus:
                    view3D = new Vr3DWidget(tag, position,
                            R.drawable.ic_vr_focus, R.drawable.ic_vr_focus, width, height);
                    break;
            }
            if (view3D != null) {
                components.put(tag, view3D);
                mdvrLibrary.addPlugin(view3D.create3DView(context));
            }
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

    private void setupListener() {
        mdvrLibrary.setEyePickChangedListener(hitEvent -> {
            IMDHotspot hitHotspot = hitEvent.getHotspot();
            if (hitHotspot instanceof MDWidgetPlugin) {
                MDWidgetPlugin widgetPlugin = (MDWidgetPlugin) hitHotspot;
                Log.d(TAG, "[setEyePickChangedListener]: HIT to the plugin " + widgetPlugin.getTag());
                widgetPlugin.setChecked(!widgetPlugin.getChecked());
            }
        });
        mdvrLibrary.setTouchPickListener(hitEvent -> {
            IMDHotspot hitHotspot = hitEvent.getHotspot();
            if (hitHotspot instanceof MDWidgetPlugin) {
                MDWidgetPlugin widgetPlugin = (MDWidgetPlugin) hitHotspot;
                Log.d(TAG, "[setTouchPickListener]: HIT to the plugin " + widgetPlugin.getTag());
                widgetPlugin.setChecked(!widgetPlugin.getChecked());
            }
        });
    }

    private enum Vr3DViewType {
        titleVideo("md-tag-title-video", 0.0f, CONTROLLER_DISTANCE_TO_EYE, -7.6f, 9.0f, 1.6f),
        skipPrevious("md-tag-skip-previous", -2.2f, CONTROLLER_DISTANCE_TO_EYE, -6.0f, 0.6f, 0.6f),
        rewind30s("md-tag-rewind-30s", -1.2f, CONTROLLER_DISTANCE_TO_EYE, -6.0f, 1.0f, 1.0f),
        play("md-tag-play", 0.0f, CONTROLLER_DISTANCE_TO_EYE, -6.0f, 1.0f, 1.0f),
        fastForward30s("md-tag-fast-forward-30s", 1.2f, CONTROLLER_DISTANCE_TO_EYE, -6.0f, 1.0f, 1.0f),
        skipNext("md-tag-skip-next", 2.2f, CONTROLLER_DISTANCE_TO_EYE, -6.0f, 0.6f, 0.6f),
        seekBar("md-tag-seek-bar", 0.0f, CONTROLLER_DISTANCE_TO_EYE, -5.0f, 9.0f, 1.0f),
        leftDuration("md-tag-left-duration", -3.0f, CONTROLLER_DISTANCE_TO_EYE, -4.0f, 2.2f, 1.0f),
        rightDuration("md-tag-right-duration", -0.8f, CONTROLLER_DISTANCE_TO_EYE, -4.0f, 2.3f, 1.0f),
        focus("md-tag-focus", 3.6f, CONTROLLER_DISTANCE_TO_EYE, -4.2f, 0.6f, 0.6f);

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
