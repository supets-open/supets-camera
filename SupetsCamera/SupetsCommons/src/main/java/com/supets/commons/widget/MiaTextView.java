package com.supets.commons.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * TextView的子类，内部解决了因设置行距在不版本下产生的Bug.
 *
 * @author Created by FengZuyan on 2015/12/3.
 */
public class MiaTextView extends TextView {

    private float mLineSpacing;
    private int mPaddingBottom = -1;

    public MiaTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mLineSpacing = Build.VERSION.SDK_INT < 21 ? getLineSpacing() : 0;

        if (mLineSpacing > 0) {
            setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
        }
    }

    public MiaTextView(Context context) {
        this(context, null);
    }

    private float getLineSpacing() {
        try {
            Field field = getClass().getSuperclass().getDeclaredField("mSpacingAdd");
            field.setAccessible(true);
            return (Float) field.get(this);
        } catch (Exception e) {
           // e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getPaddingBottom() {
        if (mPaddingBottom == -1) {
            mPaddingBottom = super.getPaddingBottom();
        }
        return mPaddingBottom;
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        mPaddingBottom = bottom;
        super.setPadding(left, top, right, (int) (mPaddingBottom - mLineSpacing));
    }
}
