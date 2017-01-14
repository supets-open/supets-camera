package com.supets.commons.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.supets.commons.R;

/**
 * <b>公共通用的titleHeader</b>
 * <p>
 * 默认支持左边为返回按钮，中间和右边的区域可以自己定制<br/>
 * </p>
 *
 * @author Created by WeiDongliang on 2015/12/22.
 */
public class CommonHeader extends LinearLayout {
    //
    private TextView mLeftBtn, mRightBtn;
    private ImageButton mRightBtn2, mLeftBtn2;
    private TextView mTitleTv;
    private LinearLayout mLeftContainer, mCenterContainer;
    private LinearLayout mRightContainer;
    private int mLeftRightTextPadding;

    private RelativeLayout mWhoelView;
    private View mBottomLine;

    public CommonHeader(Context context) {
        super(context, null);
        init(context, null);
    }

    public CommonHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CommonHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }


    @SuppressWarnings("deprecation")
    private void init(Context mContext, AttributeSet attrs) {
        LayoutInflater.from(mContext).inflate(R.layout.mia_commons_title_bar, this);
        mWhoelView = (RelativeLayout) findViewById(R.id.wholeView);
        mLeftBtn = (TextView) findViewById(R.id.header_left_btn);
        mLeftBtn2 = (ImageButton) findViewById(R.id.header_left_btn2);
        mRightBtn = (TextView) findViewById(R.id.header_right_btn);
        mRightBtn2 = (ImageButton) findViewById(R.id.header_right_btn2);
        mTitleTv = (TextView) findViewById(R.id.header_title_text);
        mTitleTv.setSelected(true);
        mLeftContainer = (LinearLayout) findViewById(R.id.header_left);
        mCenterContainer = (LinearLayout) findViewById(R.id.header_center);
        mRightContainer = (LinearLayout) findViewById(R.id.header_right);
        mBottomLine = findViewById(R.id.bottom_line);
        if (attrs != null) {
            TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.CommonHeader);
            if (a != null) {
                // title的左右间距 ,默认为60dp
                // int titleContainerPadding = (int)
                // a.getDimension(R.styleable.CommonHeader_titleTextLayoutPadding,
                // getResources().getDimensionPixelSize(R.dimen.px120));
                // mCenterContainer.setPadding(titleContainerPadding, 0,
                // titleContainerPadding, 0);
                // 文字button的左右paddding值 ,默认为10dp
                mLeftRightTextPadding = (int) a.getDimension(R.styleable.CommonHeader_leftRightTextPadding,
                        getResources().getDimensionPixelSize(R.dimen.mia_commons_px20));

                // 设置左边btn文字
                String leftText = a.getString(R.styleable.CommonHeader_leftText);
                if (!TextUtils.isEmpty(leftText)) {
                    mLeftBtn.setText(leftText);
                    mLeftBtn.setPadding(mLeftRightTextPadding, 0, mLeftRightTextPadding, 0);
                }
                // 设置右边btn文字
                String rightText = a.getString(R.styleable.CommonHeader_rightText);
                if (!TextUtils.isEmpty(rightText)) {
                    mRightBtn.setText(rightText);
                    mRightBtn.setPadding(mLeftRightTextPadding, 0, mLeftRightTextPadding, 0);
                }
                // 设置title文字
                String titleText = a.getString(R.styleable.CommonHeader_titleText);
                if (!TextUtils.isEmpty(titleText)) {
                    mTitleTv.setText(titleText);
                }
                // 设置右边btn文字颜色
                ColorStateList rColorStateList = a.getColorStateList(R.styleable.CommonHeader_rightTextColor);
                if (rColorStateList != null) {
                    mRightBtn.setTextColor(rColorStateList);
                }

                // 设置左边btn文字颜色
                ColorStateList lColorStateList = a.getColorStateList(R.styleable.CommonHeader_leftTextColor);
                if (lColorStateList != null) {
                    mLeftBtn.setTextColor(lColorStateList);
                }

                // 设置右边btn背景
                Drawable rBgDrawable = a.getDrawable(R.styleable.CommonHeader_rightBgDrawable);
                if (rBgDrawable != null) {
                    mRightBtn.setBackgroundDrawable(rBgDrawable);
                    setPadding(0, 0, 0, 0);
                }

                // 设置左边btn背景
                Drawable lBgDrawable = a.getDrawable(R.styleable.CommonHeader_leftBgDrawable);
                if (lBgDrawable != null) {
                    mLeftBtn.setBackgroundDrawable(lBgDrawable);
                    setPadding(0, 0, 0, 0);
                }

            }
            a.recycle();
        }

        mLeftBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (getContext() instanceof Activity) {
                    ((Activity) getContext()).finish();
                }
            }
        });
    }

    /**
     * 获取左边的区域
     *
     * @return LinearLayout
     */
    public LinearLayout getLeftContainer() {
        return mLeftContainer;
    }

    /**
     * 获取右边区域
     *
     * @return LinearLayout
     */
    public LinearLayout getRightContainer() {
        mRightContainer.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                return true;
            }
        });
        return mRightContainer;
    }

    /**
     * 获取中间内容区域
     *
     * @return LinearLayout
     */
    public LinearLayout getCenterContainer() {
        return mCenterContainer;
    }

    /**
     * 获取左边的View
     *
     * @return TextView
     */
    public TextView getLeftButton() {
        return mLeftBtn;
    }

    /**
     * 获取右边的View
     *
     * @return TextView
     */
    public TextView getRightButton() {
        return mRightBtn;
    }

    /**
     * 获取右边的区域里的按钮
     *
     * @return ImageButton
     */
    public ImageButton getRightButton2() {
        mRightBtn.setVisibility(View.GONE);
        mRightBtn2.setVisibility(View.VISIBLE);
        return mRightBtn2;
    }

    public ImageButton getLeftButton2() {
        mLeftBtn.setVisibility(View.GONE);
        mLeftBtn2.setVisibility(View.VISIBLE);
        return mLeftBtn2;
    }

    /**
     * 获取中间区域的TextView
     *
     * @return TextView
     */
    public TextView getTitleTextView() {
        return mTitleTv;
    }

    /**
     * 设置左右button的padding值
     *
     * @param paddingPx
     */
    public void setButtonTextPaddint(int paddingPx) {
        mLeftRightTextPadding = paddingPx;
        setViewLRPadding(mLeftBtn);
        setViewLRPadding(mRightBtn);
    }

    /**
     * 如果Button是文字显示的话，设置button的左右padding值
     *
     * @param v
     */
    private void setViewLRPadding(TextView v) {
        if (v.getText().length() > 0) {
            v.setPadding(mLeftRightTextPadding, 0, mLeftRightTextPadding, 0);
        }
    }

    /**
     * 点击空白区域 scrollView 回滚到顶部
     *
     * @param mListener
     */
    public void setSpaceOnClickListener(OnClickListener mListener) {
        this.setOnClickListener(mListener);
    }

    /**
     * 获取header的整个View
     *
     * @return RelativeLayout
     */
    public RelativeLayout getWholeView() {
        return mWhoelView;
    }

    /**
     * 设置header的底线是否显示
     *
     * @param isShow
     */
    public void setBottomLineVisible(boolean isShow) {
        mBottomLine.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置左右区域为白色风格<br/>
     * <p/>
     * 例如:左边的返回键是白色的<br/>
     * <p/>
     * 详细说明：此方法和{@link #switchToNormalStyle()}配合使用，<br/>
     * <p/>
     * 场景：默认进来header透明，左右按钮设置成白色的{@link #switchToWhiteStyle()}，滑动后header变成不透明的，
     * 左右按钮设置成默认的灰色{@link #switchToNormalStyle()}
     */
    @SuppressWarnings("ResourceType")
    public void switchToWhiteStyle() {
        getLeftButton().setBackgroundResource(R.drawable.mia_commons_btn_title_bar_back_white_selector);
        ColorStateList colorStateList = getResources().getColorStateList(R.drawable.mia_commons_color_title_text_white);
        getRightButton().setTextColor(colorStateList);
        setBottomLineVisible(false);
    }

    /**
     * 设置左右区域为正常的灰色风格<br/>
     * <p/>
     * 例如：把左边按钮设置成默认的灰色
     */
    @SuppressWarnings("ResourceType")
    public void switchToNormalStyle() {
        ColorStateList colorStateList = getResources().getColorStateList(R.drawable.mia_commons_color_title_text);
        getRightButton().setTextColor(colorStateList);
        getLeftButton().setBackgroundResource(R.drawable.mia_commons_btn_title_bar_back_normal);
        setBottomLineVisible(true);
    }

    /*public TextView getmLeftBtn() {
        return mLeftBtn;
    }*/
}
