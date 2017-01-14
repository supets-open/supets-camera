package com.supets.pet.utils.fresco;

import android.graphics.Bitmap;

public interface ImagePreloadListener {
        void onLoadingSuccess(Bitmap bitmap);
        void onLoadingFailed();
    }