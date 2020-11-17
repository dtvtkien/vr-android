package com.appixiplugin.vrplayer.databinding;
import com.appixiplugin.vrplayer.R;
import com.appixiplugin.vrplayer.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class VrMediaControllerBindingImpl extends VrMediaControllerBinding implements com.appixiplugin.vrplayer.generated.callback.OnClickListener.Listener {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.constraint_layout_media_controller, 7);
        sViewsWithIds.put(R.id.motion_layout_control_box, 8);
        sViewsWithIds.put(R.id.tv_start_controller, 9);
        sViewsWithIds.put(R.id.tv_end_controller, 10);
        sViewsWithIds.put(R.id.constraint_control, 11);
        sViewsWithIds.put(R.id.constraint_volume, 12);
        sViewsWithIds.put(R.id.img_volume, 13);
        sViewsWithIds.put(R.id.img_settings, 14);
        sViewsWithIds.put(R.id.guideline, 15);
    }
    // views
    // variables
    @Nullable
    private final android.view.View.OnClickListener mCallback5;
    @Nullable
    private final android.view.View.OnClickListener mCallback3;
    @Nullable
    private final android.view.View.OnClickListener mCallback4;
    @Nullable
    private final android.view.View.OnClickListener mCallback1;
    @Nullable
    private final android.view.View.OnClickListener mCallback2;
    // values
    // listeners
    // Inverse Binding Event Handlers

    public VrMediaControllerBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View[] root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 16, sIncludes, sViewsWithIds));
    }
    private VrMediaControllerBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View[] root, Object[] bindings) {
        super(bindingComponent, root[0], 0
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[11]
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[7]
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[12]
            , (androidx.constraintlayout.widget.Guideline) bindings[15]
            , (androidx.appcompat.widget.AppCompatImageView) bindings[1]
            , (androidx.appcompat.widget.AppCompatImageView) bindings[6]
            , (androidx.appcompat.widget.AppCompatImageView) bindings[5]
            , (androidx.appcompat.widget.AppCompatImageView) bindings[4]
            , (androidx.appcompat.widget.AppCompatImageView) bindings[14]
            , (androidx.appcompat.widget.AppCompatImageView) bindings[13]
            , (androidx.appcompat.widget.AppCompatImageView) bindings[3]
            , (androidx.constraintlayout.motion.widget.MotionLayout) bindings[8]
            , (androidx.constraintlayout.motion.widget.MotionLayout) bindings[0]
            , (androidx.appcompat.widget.AppCompatSeekBar) bindings[2]
            , (androidx.appcompat.widget.AppCompatTextView) bindings[10]
            , (androidx.appcompat.widget.AppCompatTextView) bindings[9]
            );
        this.imgClose.setTag(null);
        this.imgFastForward.setTag(null);
        this.imgPlayPause.setTag(null);
        this.imgRewind.setTag(null);
        this.imgVrMode.setTag(null);
        this.motionLayoutMediaController.setTag(null);
        this.seekBarProgressMedia.setTag(null);
        setRootTag(root);
        // listeners
        mCallback5 = new com.appixiplugin.vrplayer.generated.callback.OnClickListener(this, 5);
        mCallback3 = new com.appixiplugin.vrplayer.generated.callback.OnClickListener(this, 3);
        mCallback4 = new com.appixiplugin.vrplayer.generated.callback.OnClickListener(this, 4);
        mCallback1 = new com.appixiplugin.vrplayer.generated.callback.OnClickListener(this, 1);
        mCallback2 = new com.appixiplugin.vrplayer.generated.callback.OnClickListener(this, 2);
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x2L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
        if (BR.mediaPlayer == variableId) {
            setMediaPlayer((com.appixiplugin.vrplayer.vr.plate.IMediaPlayer) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setMediaPlayer(@Nullable com.appixiplugin.vrplayer.vr.plate.IMediaPlayer MediaPlayer) {
        this.mMediaPlayer = MediaPlayer;
        synchronized(this) {
            mDirtyFlags |= 0x1L;
        }
        notifyPropertyChanged(BR.mediaPlayer);
        super.requestRebind();
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        com.appixiplugin.vrplayer.vr.plate.IMediaPlayer mediaPlayer = mMediaPlayer;

        if ((dirtyFlags & 0x3L) != 0) {
        }
        // batch finished
        if ((dirtyFlags & 0x2L) != 0) {
            // api target 1

            com.appixiplugin.vrplayer.vr.binding.VrMediaBindingAdapter.bindListener(this.imgClose, mCallback1);
            com.appixiplugin.vrplayer.vr.binding.VrMediaBindingAdapter.bindListener(this.imgFastForward, mCallback5);
            com.appixiplugin.vrplayer.vr.binding.VrMediaBindingAdapter.bindListener(this.imgPlayPause, mCallback4);
            com.appixiplugin.vrplayer.vr.binding.VrMediaBindingAdapter.bindListener(this.imgRewind, mCallback3);
            com.appixiplugin.vrplayer.vr.binding.VrMediaBindingAdapter.bindListener(this.imgVrMode, mCallback2);
        }
        if ((dirtyFlags & 0x3L) != 0) {
            // api target 1

            com.appixiplugin.vrplayer.vr.binding.VrMediaBindingAdapter.bindSeekBarChangedPosition(this.seekBarProgressMedia, mediaPlayer);
        }
    }
    // Listener Stub Implementations
    // callback impls
    public final void _internalCallbackOnClick(int sourceId , android.view.View callbackArg_0) {
        switch(sourceId) {
            case 5: {
                // localize variables for thread safety
                // mediaPlayer != null
                boolean mediaPlayerJavaLangObjectNull = false;
                // mediaPlayer
                com.appixiplugin.vrplayer.vr.plate.IMediaPlayer mediaPlayer = mMediaPlayer;



                mediaPlayerJavaLangObjectNull = (mediaPlayer) != (null);
                if (mediaPlayerJavaLangObjectNull) {


                    mediaPlayer.fastForward();
                }
                break;
            }
            case 3: {
                // localize variables for thread safety
                // mediaPlayer != null
                boolean mediaPlayerJavaLangObjectNull = false;
                // mediaPlayer
                com.appixiplugin.vrplayer.vr.plate.IMediaPlayer mediaPlayer = mMediaPlayer;



                mediaPlayerJavaLangObjectNull = (mediaPlayer) != (null);
                if (mediaPlayerJavaLangObjectNull) {


                    mediaPlayer.rewind();
                }
                break;
            }
            case 4: {
                // localize variables for thread safety
                // mediaPlayer != null
                boolean mediaPlayerJavaLangObjectNull = false;
                // mediaPlayer
                com.appixiplugin.vrplayer.vr.plate.IMediaPlayer mediaPlayer = mMediaPlayer;



                mediaPlayerJavaLangObjectNull = (mediaPlayer) != (null);
                if (mediaPlayerJavaLangObjectNull) {


                    mediaPlayer.toggleControl();
                }
                break;
            }
            case 1: {
                // localize variables for thread safety
                // mediaPlayer != null
                boolean mediaPlayerJavaLangObjectNull = false;
                // mediaPlayer
                com.appixiplugin.vrplayer.vr.plate.IMediaPlayer mediaPlayer = mMediaPlayer;



                mediaPlayerJavaLangObjectNull = (mediaPlayer) != (null);
                if (mediaPlayerJavaLangObjectNull) {


                    mediaPlayer.close();
                }
                break;
            }
            case 2: {
                // localize variables for thread safety
                // mediaPlayer != null
                boolean mediaPlayerJavaLangObjectNull = false;
                // mediaPlayer
                com.appixiplugin.vrplayer.vr.plate.IMediaPlayer mediaPlayer = mMediaPlayer;



                mediaPlayerJavaLangObjectNull = (mediaPlayer) != (null);
                if (mediaPlayerJavaLangObjectNull) {


                    mediaPlayer.changeMode();
                }
                break;
            }
        }
    }
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): mediaPlayer
        flag 1 (0x2L): null
    flag mapping end*/
    //end
}