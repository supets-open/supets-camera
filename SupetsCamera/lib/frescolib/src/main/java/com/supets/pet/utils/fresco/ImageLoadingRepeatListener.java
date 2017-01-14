package com.supets.pet.utils.fresco;

import android.graphics.drawable.Animatable;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.supets.lib.frescolibrary.R;

public class ImageLoadingRepeatListener extends ImageLoadingListener<ImageInfo> {

    private String mUrl;
    private SimpleDraweeView view;

    public ImageLoadingRepeatListener(String uri, SimpleDraweeView view) {
        this.mUrl = uri;
        this.view = view;
    }

    @Override
    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
        super.onFinalImageSet(id, imageInfo, animatable);
        String tag = (String) view.getTag(R.id.tag_fresco_uri);
        if (mUrl != null && tag != null && imageInfo != null && imageInfo.getWidth() > 0) {
            if (tag.equals(mUrl)) {
                view.setTag(R.id.tag_fresco_flag, true);
            }
        }
    }

}