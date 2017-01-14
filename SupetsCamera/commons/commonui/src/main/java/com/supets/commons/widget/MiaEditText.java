package com.supets.commons.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * EditText的子类，可以在输入状态下按下Back键的时候执行一些操作
 *
 * @see com.supets.commons.widget.MiaEditText.OnBackPressListener
 *
 * @author FengZuyan
 */
public class MiaEditText extends EditText {

    public MiaEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MiaEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MiaEditText(Context context) {
        super(context);
    }

    private OnBackPressListener mListener;

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && (event == null || event.getAction() == KeyEvent.ACTION_UP) && mListener != null) {
            mListener.onBackPress();
        }
        return super.onKeyPreIme(keyCode, event);
    }

    /**
     * 设置Back键按下的回调
     *
     * @param listener Back键按下的回调
     */
    public void setOnBackPressListener(OnBackPressListener listener) {
        this.mListener = listener;
    }

    /**
     * 输入状态时Back键按下的回调的接口定义
     */
    public interface OnBackPressListener {

        /**
         * Back键按下的回调方法
         */
        void onBackPress();
    }

    /**
     * 对{@link TextWatcher} 接口的一个空实现
     */
    public static class SimpleTextWatch implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
