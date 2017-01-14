package com.supets.pet.utils.fresco;

import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.request.BaseRepeatedPostProcessor;

public class ImageProcessListener extends BaseRepeatedPostProcessor {

    @Override
    public CloseableReference<Bitmap> process(
            Bitmap sourceBitmap,
            PlatformBitmapFactory bitmapFactory) {
        CloseableReference<Bitmap> destBitmapRef =
                bitmapFactory.createBitmap(
                        sourceBitmap.getWidth(),
                        sourceBitmap.getHeight(),
                        sourceBitmap.getConfig());
        try {
            sourceBitmap.setDensity(320);
            process(destBitmapRef.get(), sourceBitmap);
            return CloseableReference.cloneOrNull(destBitmapRef);
        } finally {
            CloseableReference.closeSafely(destBitmapRef);
        }
    }
}
