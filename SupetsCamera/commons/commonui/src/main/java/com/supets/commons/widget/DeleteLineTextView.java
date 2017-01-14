package com.supets.commons.widget;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * TextView的子类，可将文本统一加上删除线
 *
 * @author FengZuyan
 */
public class DeleteLineTextView extends TextView {

    private int mStartPosition = 0;
    private boolean mDeleteLineEnable = true;

    /**
     * Constructor
     *
     * @param context
     */
    public DeleteLineTextView(Context context) {
        this(context, null);
    }

    public DeleteLineTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeleteLineTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 设置删除线的起始位置
     *
     * @param start 删除线的起始索引
     */
    public void setStartPosition(int start) {
        mStartPosition = start;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text == null) {
            return;
        }
        if (!mDeleteLineEnable) {
            super.setText(text, type);
            return;
        }
        StrikethroughSpan span = new StrikethroughSpan();
        SpannableString string = new SpannableString(text);
        int end = text.length();
        int start = Math.min(end, mStartPosition);
        string.setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        super.setText(string, type);
    }

    /**
     * 是否显示删除线，默认显示
     *
     * @param enable True则显示删除线，false则不显示.
     */
    public void setDeleteLineEnable(boolean enable) {
        if (mDeleteLineEnable == enable) {
            return;
        }
        mDeleteLineEnable = enable;

        setText(getText());
    }
}
