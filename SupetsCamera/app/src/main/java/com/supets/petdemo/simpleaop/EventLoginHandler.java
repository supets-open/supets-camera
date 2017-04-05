package com.supets.petdemo.simpleaop;

import android.util.Log;

/**
 * SupetsCamera
 *
 * @user lihongjiang
 * @description
 * @date 2017/3/27
 * @updatetime 2017/3/27
 */

public class EventLoginHandler extends EventInvocationHandler {


    @Override
    public boolean beforeInvoke() {
        Log.v("EventLoginHandler","未登录");
        return false;
    }

    @Override
    public boolean afterInvoke() {
        Log.v("EventLoginHandler","登录过");
        return false;
    }
}
