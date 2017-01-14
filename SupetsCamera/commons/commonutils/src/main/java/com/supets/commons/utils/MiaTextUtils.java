package com.supets.commons.utils;

import android.content.Context;
import android.text.TextUtils;

import com.supets.commons.MiaCommons;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文本相关工具类
 *
 * @author Created by FengZuyan on 2015/12/23.
 */
public class MiaTextUtils {

    /**
     * 封装了{@link Context#getString(int, Object...)} 从strings文件中获取String
     *
     * @param resId string对应的资源ID
     * @param formatArgs 用来被替换的格式化参数列表.
     * @return The string data associated with the resource, formatted and
     *         stripped of styled text information.
     */
    public static String getString(int resId, Object... formatArgs) {
        Context context = MiaCommons.getContext();
        if (context == null) {
            return null;
        }
        return context.getString(resId, formatArgs);
    }

    /**
     * 判断给定字符串是否都由字数组成
     *
     * @param str 用来判断的字符串
     * @return 给定字符串都由数字组成则返回true，否则返回false.
     */
    public static boolean isNumber(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }

        Pattern pattern = Pattern.compile("^[0-9]\\d*$");
        Matcher matcher = pattern.matcher(str.trim());
        return matcher.matches();
    }
}
