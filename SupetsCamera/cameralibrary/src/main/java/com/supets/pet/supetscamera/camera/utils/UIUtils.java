package com.supets.pet.supetscamera.camera.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class UIUtils {

    public static Context getContext() {
        return App.INSTANCE;
    }
    public static Resources getResources() {
        return getContext().getResources();
    }
    private static DisplayMetrics getDisplayMetrics() {
        return getResources().getDisplayMetrics();
    }
    public static int getScreenWidth() {
        return getDisplayMetrics().widthPixels;
    }
    public static int getScreenHeight() {
        return getDisplayMetrics().heightPixels;
    }
    public static int dp2px(float dp) {
        return (int) (dp * getDisplayMetrics().density + 0.5f);
    }


}
