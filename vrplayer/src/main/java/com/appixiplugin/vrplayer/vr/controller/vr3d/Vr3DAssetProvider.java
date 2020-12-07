package com.appixiplugin.vrplayer.vr.controller.vr3d;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.asha.vrlib.MDVRLibrary;
import com.asha.vrlib.texture.MD360BitmapTexture;

import java.io.FileNotFoundException;

public class Vr3DAssetProvider implements MDVRLibrary.IImageLoadProvider {
    Context context;

    public Vr3DAssetProvider(Context context) {
        this.context = context;
    }

    @Override
    public void onProvideBitmap(Uri uri, MD360BitmapTexture.Callback callback) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
            callback.texture(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
