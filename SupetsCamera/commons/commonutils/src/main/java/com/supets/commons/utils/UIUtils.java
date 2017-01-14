package com.supets.commons.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.supets.commons.MiaCommons;
import com.supets.commons.model.ListViewPosition;

/**
 * UI相关工具类，提供获取屏幕宽高、dp2px等工具方法
 *
 * @author Created by FengZuyan on 2015/12/23.
 */
public class UIUtils {

    /**
     * 设置一个View的宽高
     *
     * @param view   View
     * @param width  宽
     * @param height 高
     */
    public static void setViewSize(View view, int width, int height) {
        if (view == null) {
            return;
        }

        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            return;
        }

        params.width = width;
        params.height = height;
    }

    /**
     * 获取一个View测量后的高度
     *
     * @param view 等测量的View
     * @return 测量后的高度
     */
    public static int getMeasureHeight(View view) {
        if (view == null) {
            return 0;
        }
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(getScreenWidth(), View.MeasureSpec.EXACTLY);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        return view.getMeasuredHeight();
    }

    /**
     * 获取一个View测量后的宽度
     *
     * @param view 等测量的View
     * @return 测量后的宽度
     */
    public static int getMeasureWidth(View view) {
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        return view.getMeasuredWidth();
    }

    /**
     * 根据颜色的资源ID获取这个颜色的整型值
     *
     * @param resId 颜色的资源ID
     * @return 颜色的整型值
     */
    public static int getColor(int resId) {
        return getResources().getColor(resId);
    }

    /**
     * 将View从父View中移除
     *
     * @param view 要移除的View
     */
    public static void removeFromParent(View view) {
        if (view == null) {
            return;
        }
        if (view.getParent() == null) {
            return;
        }
        ((ViewGroup) view.getParent()).removeView(view);
    }

    /**
     * 根据<code>position</code>参数设置<code>listView</code>的滚动位置
     *
     * @param listView 要设置滚动位置的ListView
     * @param position 要滚动的位置
     * @see ListViewPosition
     */
    public static void restorePosition(ListView listView, ListViewPosition position) {
        if (listView == null) {
            return;
        }
        if (position != null) {
            listView.setSelectionFromTop(position.position, position.top);
        }
    }

    /**
     * 根据<code>restore</code>参数决定是否需要设置listView滚动位置
     *
     * @param listView 要设置滚动位置的ListView
     * @param position 要滚动的位置
     * @param restore  True则需要设置ListView滚动位置，false则不需要
     */
    public static void restorePosition(ListView listView, ListViewPosition position, boolean restore) {
        if (!restore) {
            return;
        }
        restorePosition(listView, position);
    }

    /**
     * 获取<code>listView</code>参数当前的滚动位置
     *
     * @param listView ListView
     * @return 当前的滚动位置
     * @see ListViewPosition
     */
    public static ListViewPosition getListViewPosition(ListView listView) {
        if (listView == null || listView.getChildCount() <= 0) {
            return null;
        }
        int position = listView.getFirstVisiblePosition();
        int top = 0;
        if (position > -1) {
            top = listView.getChildAt(0).getTop();
        }
        return new ListViewPosition(position, top);
    }

    /**
     * 获取Activity中的ContentView
     *
     * @param activity
     * @return ContentView
     */
    public static View getContentView(Activity activity) {
        return ((ViewGroup) (activity.getWindow().getDecorView().findViewById(android.R.id.content))).getChildAt(0);
    }

    /**
     * 根据资源ID的名称获取这资源ID的整型值
     *
     * @param name 资源ID的名称
     * @return 资源ID的整型值
     */
    public static int getIdByName(String name) {
        return getResources().getIdentifier(name, "id", getContext().getPackageName());
    }

    /**
     * 判断指定的View是否处于显示状态
     *
     * @param view View
     * @return True表示给定的View处于显示状态
     */
    public static boolean isSelfShown(View view) {
        return view != null && view.getVisibility() == View.VISIBLE;
    }

    /**
     * 根据透明度和颜色值设置View的背景色
     *
     * @param view  要设置背景色的view
     * @param alpha 透明度，[0...255]
     * @param resId 颜色的资源ID
     */
    public static void setBackgroundAlphaColor(View view, int alpha, int resId) {
        int color = getColor(resId);
        view.setBackgroundColor(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)));
    }

    /**
     * 获取应用的Context
     *
     * @return context
     */
    public static Context getContext() {
        return MiaCommons.getContext();
    }

    /**
     * Return a Resources instance for your application's package.
     *
     * @return Resources
     */
    public static Resources getResources() {
        return getContext().getResources();
    }

    private static DisplayMetrics getDisplayMetrics() {
        return getResources().getDisplayMetrics();
    }

    /**
     * 获取屏幕宽度的像素值
     *
     * @return 屏幕宽度的像素值
     */
    public static int getScreenWidth() {
        return getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕宽度的像素值
     *
     * @param context context
     * @return 屏幕宽度的像素值
     */
    public static int getScreenWidth(Context context) {
        return getScreenWidth();
    }

    /**
     * 获取屏幕高度的像素值
     *
     * @return 屏幕高度的像素值
     */
    public static int getScreenHeight() {
        return getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕高度的像素值
     *
     * @param context context
     * @return 屏幕高度的像素值
     */
    public static int getScreenHeight(Context context) {
        return getScreenHeight();
    }

    /**
     * Dip转为px
     *
     * @param dp dip
     * @return px
     */
    public static int dp2px(float dp) {
        return (int) (dp * getDisplayMetrics().density + 0.5f);
    }

    /**
     * Px转为dp
     *
     * @param pxValue px
     * @param context context
     * @return Value in dp.
     */
    public static int px2dp(float pxValue, Context context) {
        return (int) (pxValue / getDisplayMetrics().density + 0.5f);
    }

    /**
     * Dip转为px
     *
     * @param dipValue dip
     * @return px
     */
    public static int dp2px(float dipValue, Context context) {
        return (int) (dipValue * getDisplayMetrics().density + 0.5f);
    }

    /**
     * Px转为sp
     *
     * @param pxValue px
     * @param context context
     * @return sp
     */
    public static int px2sp(float pxValue, Context context) {
        return (int) (pxValue / getDisplayMetrics().scaledDensity + 0.5f);
    }

    /**
     * Sp转为px
     *
     * @param spValue sp
     * @param context context
     * @return px
     */
    public static int sp2px(float spValue, Context context) {
        return (int) (spValue * getDisplayMetrics().scaledDensity + 0.5f);
    }

    /**
     * 根据指定的<code>textSize</code>返回文本的宽度
     *
     * @param textSize Text size
     * @param text     The text to measure.
     * @return 文本的宽度
     */
    public static float getTextDisplayWidth(float textSize, String text) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        return paint.measureText(text);
    }

    /**
     * 获取状态栏的高度
     *
     * @return 状态栏的高度
     */
    public static int getStatusBarHeight() {
        int result = 0;
        int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = getDimension(resId);
        }
        return result;
    }

    /**
     * 根据尺寸资源ID获取它的px值
     *
     * @param resId 尺寸资源ID
     * @return 尺寸资源ID的px值
     */
    public static int getDimension(int resId) {
        return getResources().getDimensionPixelSize(resId);
    }

    /**
     * 平滑的滚动到ListView的底部
     *
     * @param listview ListView
     */
    public static void smoothScrollToBottom(ListView listview) {
        listview.smoothScrollToPosition(listview.getCount() - 1);
    }

    /**
     * 滚动到ListView的底部
     *
     * @param listview ListView
     */
    public static void scrollToBottom(ListView listview) {
        listview.setSelection(listview.getCount() - 1);
    }

    /**
     * 得到Activity的View
     *
     * @param context
     * @return
     */
    public static View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }


}
