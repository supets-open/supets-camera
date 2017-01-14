package com.supets.commons.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.supets.commons.R;

/**
 * <b>PageLoadingView 页面加载控制View</b>
 * <p>
 * 包括：loading状态、网络错误状态、内容展示状态、空页面展示、以及自己定义的OtherView<br/>
 * </p>
 * <p>
 * 用法：同ScrollView一样，在布局文件中包裹内容，但是包裹的内容布局有且只有一个根布局<br/>
 *
 * @author Created by WeiDongliang on 2015/12/22.
 */
public class PageLoadingView extends FrameLayout implements View.OnClickListener {

    private static final String EventName = "onEventErrorRefresh";

    private View mLoading;
    private View mRefresh;
    private View mNetworkError;

    private View mOther;
    private View mEmpty;

    private View mContent;
    private TextView mEmptyText;

    private TextView mNetworkErrorText;
    private Integer mRequestCode;
    private Object mSubscriber;

    public PageLoadingView(Context context) {
        this(context, null);
    }

    public PageLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.mia_commons_page_view, this);

        initView();
    }

    private void initView() {
        mLoading = findViewById(R.id.page_view_loading);
        mEmpty = findViewById(R.id.page_view_empty);
        mEmptyText = (TextView) findViewById(R.id.page_view_empty_text);
        mRefresh = findViewById(R.id.page_view_refresh);
        mNetworkError = findViewById(R.id.page_view_network_error);
        mNetworkErrorText = (TextView) findViewById(R.id.page_view_network_error_text);
        mRefresh.setOnClickListener(this);

        hideAll();
    }

    protected void hideAll() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).setVisibility(View.GONE);
        }
    }

    /**
     * 注册网络错误时候，重新加载的回调
     *
     * @param subscriber  注册事件的当前类
     * @param requestCode 请求码，区别不同注册者的同一个回调
     */
    public void subscribeRefreshEvent(Object subscriber, Integer requestCode) {
        mSubscriber = subscriber;
        mRequestCode = requestCode;
    }

    /**
     * 同上{@link #subscribeRefreshEvent(Object, Integer)}
     *
     * @param subscriber
     */
    public void subscribeRefreshEvent(Object subscriber) {
        subscribeRefreshEvent(subscriber, null);
    }

    /**
     * 设置要展示的定制的OtherView
     *
     * @param other 直接传入View
     */
    public void setOtherView(View other) {
        if (other == null) {
            return;
        }
        mOther = other;
        hideView(mOther);
    }

    /**
     * 设置要展示的定制的OtherView
     *
     * @param resid 直接传入View的id
     */
    public void setOtherView(int resid) {
        setOtherView(findViewById(resid));
    }

    /**
     * 展示自定义的OtherView
     */
    public void showOtherView() {
        hideAll();
        showView(mOther);
    }

    /**
     * 设置是否展示OtherView
     *
     * @param show
     */
    public void showOtherView(boolean show) {
        if (show) {
            showOtherView();
        } else {
            hideOtherView();
        }
    }

    /**
     * 隐藏OtherView
     */
    public void hideOtherView() {
        hideView(mOther);
    }

    /**
     * 设置空页面的提示语
     *
     * @param text
     */
    public void setEmptyText(String text) {
        mEmptyText.setText(text);
    }

    /**
     * 设置空页面的提示语
     *
     * @param resid
     */
    public void setEmptyText(int resid) {
        mEmptyText.setText(resid);
    }

    /**
     * 获取空页面的View
     *
     * @return
     */
    public View getEmptyView() {
        return mEmpty;
    }

    /**
     * 设置空页面
     *
     * @param resId
     */
    public void setEmptyView(int resId) {
        View view = findViewById(resId);
        setEmptyView(view);
    }

    /**
     * 设置空页面
     *
     * @param empty
     */
    public void setEmptyView(View empty) {
        if (empty == null) {
            return;
        }

        if (empty.getParent() != this) {
            if (empty.getParent() != null) {
                ((ViewGroup) empty.getParent()).removeView(empty);
            }
            addView(empty);
        }

        mEmpty = empty;
        hideView(mEmpty);
    }

    /**
     * 设置内容页面
     *
     * @param content
     */
    public void setContentView(View content) {
        mContent = content;
        hideView(mContent);
    }

    /**
     * 展示Loading页面
     */
    public void showLoading() {
        hideAll();
        showView(mLoading);
    }

    /**
     * 隐藏loading页面
     */
    public void hideLoading() {
        hideView(mLoading);
    }

    /**
     * 展示网络错误的页面
     */
    public void showNetworkError() {
        hideAll();
        showView(mNetworkError);
    }

    /**
     * 是否展示内容页面
     *
     * @param show
     */
    public void showContent(boolean show) {
        if (show) {
            showContent();
        }
    }

    /**
     * 展示内容页面
     */
    public void showContent() {
        hideAll();
        showView(mContent);
    }

    /**
     * 是否展示空页面
     *
     * @param show
     */
    public void showEmpty(boolean show) {
        if (show) {
            showEmpty();
        }
    }

    /**
     * 展示空页面
     */
    public void showEmpty() {
        hideAll();
        showView(mEmpty);
    }

    /**
     * 隐藏指定的View
     *
     * @param view 传入指定想隐藏的View
     */
    private static void hideView(View view) {
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * 展示指定的View
     *
     * @param view 传入指定想展示的View
     */
    private static void showView(View view) {
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        showLoading();

        if (mRequestCode != null) {
            EventBus.postEvent(mSubscriber, EventName, mRequestCode);
        } else {
            EventBus.postEvent(mSubscriber, EventName);
        }
    }

    /**
     * 获取内容是否展示状态
     *
     * @return 返回true是展示状态，反之false
     */
    public boolean isContentShow() {
        return mContent.isShown();
    }

    public boolean isOtherShow() {
        return mOther.isShown();
    }

    public boolean isEmptyShow() {
        return mEmpty.isShown();
    }


    public View getVisibleChildView() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (view != null && view.getVisibility() == View.VISIBLE) {
                return view;
            }
        }
        return null;
    }
}
