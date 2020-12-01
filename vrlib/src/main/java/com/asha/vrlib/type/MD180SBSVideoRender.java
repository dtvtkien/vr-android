package com.asha.vrlib.type;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.view.View;
import android.view.ViewGroup;

import com.asha.vrlib.MD360Director;
import com.asha.vrlib.MD360Program;
import com.asha.vrlib.MDVRLibrary;
import com.asha.vrlib.model.MDPosition;
import com.asha.vrlib.objects.MDAbsObject3D;
import com.asha.vrlib.objects.MDObject3DHelper;
import com.asha.vrlib.objects.MDPlane;
import com.asha.vrlib.texture.MD360BitmapTexture;
import com.asha.vrlib.texture.MD360Texture;

public class MD180SBSVideoRender extends MDVideoRender {
    private boolean mIsInit;
    private long mTid;
    private MD360Texture mTexture;
    private boolean mInvalidate;
    private View mView;
    private Canvas mCanvas;
    private Bitmap mBitmap;
    MD360Program program;
    private MDAbsObject3D object3D;
    private RectF size;

    public MD180SBSVideoRender(Context context, int width, int height) {
        this.size = new RectF(0, 0, width, height);
        this.mView = new View(context);
        mView.setBackgroundColor(Color.BLACK);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        this.mView.setLayoutParams(layoutParams);

        try {
            this.mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            this.mCanvas = new Canvas(mBitmap);
        } catch (Exception e){
            e.printStackTrace();
        }

        mView.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        );
        mView.layout(0, 0, mView.getMeasuredWidth(), mView.getMeasuredHeight());

        invalidate();
    }

    public void invalidate(){
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mView.draw(mCanvas);
        mInvalidate = true;
    }

    public void setup(Context context){
        long tid = Thread.currentThread().getId();
        if (tid != mTid) {
            mTid = tid;
            mIsInit = false;
        }

        if (!mIsInit){
            init(context);
            mIsInit = true;
        }
    }

    public void init(Context context) {
        program = new MD360Program(MDVRLibrary.ContentType.BITMAP);
        program.build(context);
        object3D = new MDPlane(size);
        MDObject3DHelper.loadObj(context,object3D);

        mTexture = new MD360BitmapTexture(new MDVRLibrary.IBitmapProvider() {
            @Override
            public void onProvideBitmap(MD360BitmapTexture.Callback callback) {
                if (mBitmap != null){
                    callback.texture(mBitmap);
                }
            }
        });
        mTexture.create();
    }

    public void renderer(int index, int width, int height, MD360Director director) {
        if (mTexture == null || mBitmap == null){
            return;
        }

        if (mInvalidate){
            mInvalidate = false;
            mTexture.notifyChanged();
        }

        mTexture.texture(program);

        if (mTexture.isReady()){
            director.setViewport(width, height);
            program.use();
            object3D.uploadVerticesBufferIfNeed(program, index);
            object3D.uploadTexCoordinateBufferIfNeed(program, index);
            director.beforeShot();
            director.shot(program, MDPosition.newInstance().setZ(-1.0f));
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            object3D.draw();
//            director.beforeShot();
//            director.shot(program, MDPosition.newInstance().setZ(-4.0f));
//            GLES20.glEnable(GLES20.GL_BLEND);
//            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
//            object3D.draw();
            GLES20.glDisable(GLES20.GL_BLEND);
        }
    }
}
