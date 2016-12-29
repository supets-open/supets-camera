package com.supets.commons;

import android.content.Context;

/**
 * 为公共工具类提供Context的类，使用公共工具类之前必需先调用{@link #init(Context)}.
 *
 * @author Created by FengZuyan on 2015/12/23.
 */
public class MiaCommons {

    private static Context sContext;

    /**
     * 初始化公共工具类
     *
     * @param context 应用上下文
     */
    public static void init(Context context) {
        sContext = context;
    }

    /**
     * 获取应用上下文
     *
     * @return 返回应用上下文
     * @see #init(Context)
     */
    public static Context getContext() {
        return sContext;
    }
}
