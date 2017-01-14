package com.supets.pet.utils.fresco;

import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;

public class ImageLoadingDensityListener extends ImageLoadingListener<CloseableStaticBitmap> {
    private String mUrl;
    private SimpleDraweeView view;

    public ImageLoadingDensityListener(String uri, SimpleDraweeView view) {
        this.mUrl = uri;
        this.view = view;
    }

    @Override
    public void onFinalImageSet(String id, CloseableStaticBitmap imageInfo, Animatable animatable) {
        if (imageInfo != null) {
            Bitmap bitmap = imageInfo.getUnderlyingBitmap();
            if (bitmap.getDensity() != 320) {
                bitmap.setDensity(320);
                view.getHierarchy().setImage(new BitmapDrawable(view.getResources(), bitmap), 1.0f, true);
            }
        }
    }
}
