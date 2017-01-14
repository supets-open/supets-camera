package com.supets.pet.utils.fresco;

import android.content.Context;
import android.util.AttributeSet;

import com.facebook.drawee.view.SimpleDraweeView;

public class RatioFrescoImageView extends SimpleDraweeView {

    private static final double DEFAULT_RATIO_WIDTH = 1.0;
    private static final double DEFAULT_RATIO_HEIGHT = 1.0;

    private double mRatioWidth = DEFAULT_RATIO_WIDTH;
    private double mRatioHeight = DEFAULT_RATIO_HEIGHT;

    private int mHeightRes;

    public RatioFrescoImageView(Context context) {
        this(context, null);
    }

    public RatioFrescoImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioFrescoImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setScaleType(ScaleType.CENTER_CROP);
    }

    public void setHeightRes(int dimenRes) {
        mHeightRes = getResources().getDimensionPixelSize(dimenRes);
    }

    public void setHeight(int px) {
        mHeightRes = px;
    }

    public void setRatio(double ratioWidth, double ratioHeight) {
        mRatioWidth = ratioWidth;
        mRatioHeight = ratioHeight;
        requestLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);

        int widthSize = 0;
        int heightSize = 0;

        if (mHeightRes > 0) { // 根据高度计算
            heightSize = mHeightRes;
            widthSize = (int) (1.0 * mRatioWidth * heightSize / mRatioHeight);
        } else {
            if (widthSpecMode == MeasureSpec.EXACTLY) {
                widthSize = MeasureSpec.getSize(widthMeasureSpec);
            } else {
                widthSize = getResources().getDisplayMetrics().widthPixels;
            }
            heightSize = (int) (1.0 * mRatioHeight * widthSize / mRatioWidth);
        }
        setMeasuredDimension(widthSize, heightSize);
    }
}
