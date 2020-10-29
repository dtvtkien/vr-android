package com.appixiplugin.vrplayer.vr;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import java.lang.reflect.Method;
import java.util.Formatter;
import java.util.Locale;

public class VrMediaController extends FrameLayout {
    private final static String TAG = VrMediaController.class.getCanonicalName();
    private static final int DEFAULT_TIME_OUT = 3000;

    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private static final int VR_INTERACTIVE_MODE_GYROSCOPE = 4;
    private static final int VR_INTERACTIVE_MODE_TOUCH = 3;
    private static final int VR_DISPLAY_MODE_NORMAL = 101;
    private static final int VR_DISPLAY_MODE_GLASS = 102;

    private int interactiveMode = VR_INTERACTIVE_MODE_GYROSCOPE;
    private int displayMode = VR_DISPLAY_MODE_GLASS;

    private MediaPlayerControl mediaPlayerControl;
    private final Context context;
    private View anchorView;
    private View rootView;
    private PopupWindow popupWindow;
    private ProgressBar progressBar;
    private AppCompatTextView tvEndTime, tvCurrentTime;
    private boolean showing;
    private boolean dragging;
    private boolean fromXml;
    StringBuilder formatBuilder;
    Formatter formatter;
    private AppCompatImageView imgControlButton;
    private AppCompatImageView imgVrInteractiveModeButton;
    private AppCompatImageView imgVrDisplayModeButton;

    private int animationStyle;

    private Handler progressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            long pos;
            switch (msg.what) {
                case FADE_OUT:
                    hide();
                    break;
                case SHOW_PROGRESS:
                    pos = setProgress();
                    if (!dragging && showing) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                        updatePausePlay();
                    }
                    break;
            }
        }
    };

    public VrMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        rootView = this;
        this.context = context;
        fromXml = true;
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        if (rootView != null)
            initControllerView(rootView);
    }

    public VrMediaController(Context context, boolean useFastForward) {
        super(context);
        this.context = context;
        initFloatingWindow();
    }

    public VrMediaController(Context context) {
        this(context, true);
    }

    private void initFloatingWindow() {
        popupWindow = new PopupWindow(context);
        popupWindow.setFocusable(false);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setOutsideTouchable(true);
        animationStyle = android.R.style.Animation;
        requestFocus();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setWindowLayoutType() {
        try {
            anchorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            Method setWindowLayoutType = PopupWindow.class.getMethod("setWindowLayoutType", int.class);
            setWindowLayoutType.invoke(popupWindow, WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final OnTouchListener mTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (showing) {
                    hide();
                }
            }
            return false;
        }
    };

    public void setMediaPlayer(MediaPlayerControl player) {
        mediaPlayerControl = player;
        updatePausePlay();
    }

    /**
     * Set the view that acts as the anchor for the control view.
     * This can for example be a VideoView, or your Activity's main view.
     * When VideoView calls this method, it will use the VideoView's parent
     * as the anchor.
     *
     * @param view The view to which to anchor the controller when it is visible.
     */
    public void setAnchorView(View view) {
        anchorView = view;
        if (!fromXml) {
            removeAllViews();
            rootView = makeControllerView();
            popupWindow.setContentView(rootView);
            popupWindow.setWidth(LayoutParams.MATCH_PARENT);
            popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
        }
        initControllerView(rootView);
    }

    /**
     * Create the view that holds the widgets that control playback.
     * Derived classes can override this to create their own.
     *
     * @return The controller view.
     * @hide This doesn't work as advertised
     */
    protected View makeControllerView() {
        return ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(getResources().getIdentifier("media_controller_vr", "layout", context.getPackageName()), this);
    }

    private void initControllerView(View v) {
        imgControlButton = v.findViewById(getResources().getIdentifier("img_control_media", "id", context.getPackageName()));
        if (imgControlButton != null) {
            imgControlButton.requestFocus();
            imgControlButton.setOnClickListener(mPauseListener);
        }

        imgVrInteractiveModeButton = v.findViewById(getResources().getIdentifier("img_interactive", "id", context.getPackageName()));
        if (imgVrInteractiveModeButton != null) {
            imgVrInteractiveModeButton.requestFocus();
            imgVrInteractiveModeButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    vrControl.onInteractiveClick(interactiveMode);
                    updateInteractive();
                }
            });
        }
        imgVrDisplayModeButton = v.findViewById(getResources().getIdentifier("img_display", "id", context.getPackageName()));
        if (imgVrDisplayModeButton != null) {
            imgVrDisplayModeButton.requestFocus();
            imgVrDisplayModeButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    vrControl.onDisplayClick(displayMode);
                    updateDisplay();
                }
            });
        }

        progressBar = (SeekBar) v.findViewById(getResources().getIdentifier("seek_bar_media", "id", context.getPackageName()));
        if (progressBar != null) {
            if (progressBar instanceof SeekBar) {
                SeekBar seeker = (SeekBar) progressBar;
                seeker.setOnSeekBarChangeListener(mSeekListener);
            }
            progressBar.setMax(1000);
        }

        tvEndTime = v.findViewById(getResources().getIdentifier("tv_total_time", "id", context.getPackageName()));
        tvCurrentTime = v.findViewById(getResources().getIdentifier("tv_current_time", "id", context.getPackageName()));

        formatBuilder = new StringBuilder();
        formatter = new Formatter(formatBuilder, Locale.getDefault());
    }

    /**
     * Show the controller on screen. It will go away
     * automatically after 3 seconds of inactivity.
     */
    public void show() {
        show(DEFAULT_TIME_OUT);
    }

    /**
     * Show the controller on screen. It will go away
     * automatically after 'timeout' milliseconds of inactivity.
     *
     * @param timeout The timeout in milliseconds. Use 0 to show
     *                the controller until hide() is called.
     */
    public void show(int timeout) {
        if (!showing && anchorView != null) {
            setProgress();
            if (imgControlButton != null) {
                imgControlButton.requestFocus();
            }
            //disableUnsupportedButtons();
            //updateFloatingWindowLayout();
//            mWindowManager.addView(mDecor, mDecorLayoutParams);
            if (fromXml) {
                setVisibility(View.VISIBLE);
            } else {
                int[] location = new int[2];

                anchorView.getLocationOnScreen(location);
                Rect anchorRect = new Rect(location[0], location[1], location[0] + anchorView.getWidth(), location[1]);

                popupWindow.setAnimationStyle(animationStyle);
//        setWindowLayoutType();
//        mWindow.showAtLocation(mAnchor, Gravity.TOP, anchorRect.left, anchorRect.bottom);
                popupWindow.showAsDropDown(anchorView, 0, -getResources().getDimensionPixelOffset(getResources().getIdentifier("media_control_height", "dimen", context.getPackageName())));
            }
            if (titleView != null)
                titleView.setVisibility(View.VISIBLE);
            showing = true;
        }

        updatePausePlay();

        // cause the progress bar to be updated even if mShowing
        // was already true.  This happens, for example, if we're
        // paused with the progress bar showing the user hits play.
//        post(mShowProgress);
        progressHandler.sendEmptyMessage(SHOW_PROGRESS);
        if (timeout != 0) {
//            removeCallbacks(mFadeOut);
//            postDelayed(mFadeOut, timeout);
            progressHandler.removeMessages(FADE_OUT);
            progressHandler.sendMessageDelayed(progressHandler.obtainMessage(FADE_OUT), timeout);
        }
    }

    public boolean isShowing() {
        return showing;
    }

    /**
     * Remove the controller from the screen.
     */
    public void hide() {
        if (anchorView == null)
            return;

        if (showing) {
            try {
                if (fromXml) {
//                    removeCallbacks(mShowProgress);
                    progressHandler.removeMessages(SHOW_PROGRESS);
                    setVisibility(View.GONE);
                } else {
                    popupWindow.dismiss();
                    if (titleView != null)
                        titleView.setVisibility(View.GONE);
                }
            } catch (IllegalArgumentException ex) {
                Log.w("MediaController", "already removed");
            }
            Log.d(TAG, "hide");
            showing = false;
        }
    }

    /**
     * Time Format
     *
     * @param timeMs
     * @return
     */
    private String stringForTime(long timeMs) {
        long totalSeconds = timeMs / 1000;

        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;

        formatBuilder.setLength(0);
        if (hours > 0) {
            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return formatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private long setProgress() {
        if (mediaPlayerControl == null || dragging) {
            return 0;
        }
        long position = mediaPlayerControl.getCurrentPosition();
        long duration = mediaPlayerControl.getDuration();
        if (progressBar != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                progressBar.setProgress((int) pos);
            }
            int percent = mediaPlayerControl.getBufferPercentage();
            progressBar.setSecondaryProgress(percent * 10);
        }

        if (tvEndTime != null)
            tvEndTime.setText(stringForTime(duration));
        if (tvCurrentTime != null)
            tvCurrentTime.setText(stringForTime(position));

        return position;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                show(0); // showe until hide is called
                break;
            case MotionEvent.ACTION_UP:
                show(DEFAULT_TIME_OUT); // start timeout
                break;
            case MotionEvent.ACTION_CANCEL:
                hide();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        show(DEFAULT_TIME_OUT);
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        final boolean uniqueDown = event.getRepeatCount() == 0
                && event.getAction() == KeyEvent.ACTION_DOWN;
        if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                || keyCode == KeyEvent.KEYCODE_SPACE) {
            if (uniqueDown) {
                doPauseResume();
                show(DEFAULT_TIME_OUT);
                if (imgControlButton != null) {
                    imgControlButton.requestFocus();
                }
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
            if (uniqueDown && !mediaPlayerControl.isPlaying()) {
                mediaPlayerControl.start();
                updatePausePlay();
                show(DEFAULT_TIME_OUT);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
            if (uniqueDown && mediaPlayerControl.isPlaying()) {
                mediaPlayerControl.pause();
                updatePausePlay();
                show(DEFAULT_TIME_OUT);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                || keyCode == KeyEvent.KEYCODE_VOLUME_UP
                || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE
                || keyCode == KeyEvent.KEYCODE_CAMERA) {
            // don't show the controls for volume adjustment
            return super.dispatchKeyEvent(event);
        } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
            if (uniqueDown) {
                hide();
            }
            return true;
        }

        show(DEFAULT_TIME_OUT);
        return super.dispatchKeyEvent(event);
    }

    private final OnClickListener mPauseListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            doPauseResume();
            show(DEFAULT_TIME_OUT);
        }
    };

    private void updatePausePlay() {
        if (rootView == null || imgControlButton == null)
            return;
        if (mediaPlayerControl.isPlaying())
            imgControlButton.setImageResource(getResources().getIdentifier("media_controller_pause", "drawable", context.getPackageName()));
        else
            imgControlButton.setImageResource(getResources().getIdentifier("media_controller_play", "drawable", context.getPackageName()));
    }


    private void doPauseResume() {
        if (mediaPlayerControl.isPlaying()) {
            mediaPlayerControl.pause();
        } else {
            mediaPlayerControl.start();
        }
        updatePausePlay();
    }

    // There are two scenarios that can trigger the seek bar listener to trigger:
    //
    // The first is the user using the touch pad to adjust the position of the
    // seek bar's thumb. In this case onStartTrackingTouch is called followed by
    // a number of onProgressChanged notifications, concluded by onStopTrackingTouch.
    // We're setting the field "mDragging" to true for the duration of the dragging
    // session to avoid jumps in the position in case of ongoing playback.
    //
    // The second scenario involves the user operating the scroll ball, in this
    // case there WON'T BE onStartTrackingTouch/onStopTrackingTouch notifications,
    // we will simply apply the updated position without suspending regular updates.
    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStartTrackingTouch(SeekBar bar) {
            show(3600000);
            dragging = true;
            // By removing these pending progress messages we make sure
            // that a) we won't update the progress while the user adjusts
            // the seekbar and b) once the user is done dragging the thumb
            // we will post one of these messages to the queue again and
            // this ensures that there will be exactly one message queued up.

//            removeCallbacks(mShowProgress);
            progressHandler.removeMessages(SHOW_PROGRESS);
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser) {
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return;
            }

            long duration = mediaPlayerControl.getDuration();
            long newPosition = (duration * progress) / 1000L;
            mediaPlayerControl.seekTo((int) newPosition);
            if (tvCurrentTime != null)
                tvCurrentTime.setText(stringForTime((int) newPosition));
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
            dragging = false;
            setProgress();
            updatePausePlay();
            show(DEFAULT_TIME_OUT);

            // Ensure that progress is properly updated in the future,
            // the call to show() does not guarantee this because it is a
            // no-op if we are already showing.
//            post(mShowProgress);
            progressHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
        }
    };

    @Override
    public void setEnabled(boolean enabled) {
        if (imgControlButton != null) {
            imgControlButton.setEnabled(enabled);

            if (progressBar != null) {
                progressBar.setEnabled(enabled);
            }
            //disableUnsupportedButtons();
            super.setEnabled(enabled);
        }

    }

    public interface MediaPlayerControl {
        void start();

        void pause();

        long getDuration();

        long getCurrentPosition();

        void seekTo(long pos);

        boolean isPlaying();

        int getBufferPercentage();
    }

    /*----------------------------------------Extend VR ------------------------------------------*/

    private VRControl vrControl;

    public interface VRControl {
        void onInteractiveClick(int currentMode);

        void onDisplayClick(int currentMode);
    }

    public void setOnVRControlListener(VRControl vrControl) {
        this.vrControl = vrControl;
    }

    private void updateInteractive() {
        if (rootView == null || imgVrInteractiveModeButton == null)
            return;
        if (interactiveMode == VR_INTERACTIVE_MODE_GYROSCOPE) {
            interactiveMode = VR_INTERACTIVE_MODE_TOUCH;
            imgVrInteractiveModeButton.setImageResource(getResources().getIdentifier("ic_gyroscope", "drawable", context.getPackageName()));
        } else {
            interactiveMode = VR_INTERACTIVE_MODE_GYROSCOPE;
            imgVrInteractiveModeButton.setImageResource(getResources().getIdentifier("ic_touch_mode", "drawable", context.getPackageName()));
        }
    }

    private void updateDisplay() {
        if (rootView == null || imgVrDisplayModeButton == null)
            return;
        if (displayMode == VR_DISPLAY_MODE_GLASS) {
            displayMode = VR_DISPLAY_MODE_NORMAL;
            imgVrDisplayModeButton.setImageResource(getResources().getIdentifier("ic_vr_mode", "drawable", context.getPackageName()));
        } else {
            displayMode = VR_DISPLAY_MODE_GLASS;
            imgVrDisplayModeButton.setImageResource(getResources().getIdentifier("ic_eye_mode", "drawable", context.getPackageName()));
        }
    }

    /*----------------------------------------Extend Title ------------------------------------------*/

    private View titleView;

    public void setTitleView(View v) {
        titleView = v;
    }
}
