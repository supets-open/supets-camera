package com.supets.pet.utils.fresco;

import android.content.Context;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.supets.pet.frescolibrary.R;


public class DraweeHierarchyBuilder {
    private static Context context;
    private static LruCache<String, Object> hierarchyMap;

    public static void init(Context ct) {
        context = ct;
        hierarchyMap = new LruCache<String, Object>(512);
    }

    public static void setSimpleHierarchy (int imageId,SimpleDraweeView imageView) {
        GenericDraweeHierarchy hierarchy = buildSimpleHierarchys(imageId,imageView);
        if ( hierarchy != null ) {
            imageView.setHierarchy(hierarchy);
        }
    }

    public static GenericDraweeHierarchy buildSimpleHierarchys(int imageId, SimpleDraweeView imageView) {
        Object existed = hierarchyMap.get(imageView.hashCode()+"");
        if(existed == null) {
            GenericDraweeHierarchy hierarchy = GenericDraweeHierarchyBuilder.newInstance(context.getResources())
                    .setPlaceholderImage(context.getResources().getDrawable(imageId), ScalingUtils.ScaleType.CENTER_CROP)
                    .setFailureImage(context.getResources().getDrawable(imageId), ScalingUtils.ScaleType.CENTER_CROP)
                    .build();
            hierarchyMap.put(imageView.hashCode()+"", "a");
            return hierarchy;
        }
       return null;//设置过hierarchy的view不再重复创建和设置
    }

    public static GenericDraweeHierarchy buildRoundDraweeHierarchys(int imageId, ImageView imageView) {
        return buildRoundDraweeHierarchys(imageId, true,imageView);
    }

    public static GenericDraweeHierarchy buildRoundDraweeHierarchys(int imageId, boolean pressable, ImageView imageView) {
        String key = imageView.hashCode()+ "";
        if (pressable) {
            key = key + "_pressable";
        }

        Object existed = hierarchyMap.get(key);
        if(existed == null) {
            GenericDraweeHierarchyBuilder builder = GenericDraweeHierarchyBuilder.newInstance(context.getResources());

            builder.setRoundingParams(RoundingParams.asCircle());

            if (imageId != -1) {
                builder.setPlaceholderImage(context.getResources().getDrawable(imageId));
                builder.setFailureImage(context.getResources().getDrawable(imageId));
            }

            if (pressable) {
                builder.setPressedStateOverlay(context.getResources().getDrawable(R.drawable.shape_header_press_overlay_default));
            }
            hierarchyMap.put(key, "a");
            return builder.build();
        }
        return null;
    }

}
