package com.supets.petdemo.simpleaop;


/**
 * SupetsCamera
 *
 * @user lihongjiang
 * @description
 * @date 2017/3/23
 * @updatetime 2017/3/23
 */

public interface OnLoginEventListener {

    @OnLogin
    void onLogin(int loginstatus);
}
