package com.supets.commons;

import android.content.Context;

import com.supets.lib.supetscontext.App;

/**
 * 为公共工具类提供Context的类，使用公共工具类之前必需先初始化上下文.
 *
 * @author Created by FengZuyan on 2015/12/23.
 */
public class MiaCommons {

    /**
     * 获取应用上下文
     *
     * @return 返回应用上下文
     */
    public static Context getContext() {
        return App.INSTANCE;
    }
}
