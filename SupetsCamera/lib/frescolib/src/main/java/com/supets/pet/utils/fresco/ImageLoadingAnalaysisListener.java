package com.supets.pet.utils.fresco;

import android.graphics.drawable.Animatable;
import android.util.Log;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.CloseableImage;

public class ImageLoadingAnalaysisListener<INFO> extends BaseControllerListener<INFO> {

    public static final String TAG = "BaseRequestListener";
    private long time;
    public String uri;

    @Override
    public void onSubmit(String id, Object callerContext) {
        super.onSubmit(id, callerContext);
        time = System.currentTimeMillis();
    }

    @Override
    public void onFinalImageSet(String id, INFO imageInfo, Animatable animatable) {
        super.onFinalImageSet(id, imageInfo, animatable);
        CloseableImage info = (CloseableImage) imageInfo;
        long endTime = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();

              sb.append("图片结束加载时间:" + endTime)
                .append(",总耗时:" + (endTime - time))
                .append(",图片宽高:" + info.getWidth() + "*" + info.getHeight())
                .append(",图片大小:" + info.getSizeInBytes());

        Log.d(TAG, "图片ID:" + id + sb.toString());
    }
}