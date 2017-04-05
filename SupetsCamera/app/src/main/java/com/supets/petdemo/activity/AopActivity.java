package com.supets.petdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.supets.petdemo.R;
import com.supets.petdemo.simpleaop.AOPInject;
import com.supets.petdemo.simpleaop.EventLoginHandler;
import com.supets.petdemo.simpleaop.OnLoginEventListener;
import com.supets.petdemo.simpleaop.OnLogin;

/**
 * SupetsCamera
 *
 * @user lihongjiang
 * @description
 * @date 2017/3/23
 * @updatetime 2017/3/23
 */

public class AopActivity extends Activity {
    public OnLoginEventListener business;

    public void setOnLoginEventListener(OnLoginEventListener business) {
        this.business = business;
    }

    @OnLogin
    public void onLogin(int loginstatus) {
        Log.v("EventLogin", loginstatus+"");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // business = (IEventLoginProxy) AopProxy.registerProxy(this, IEventLoginProxy.class);
        AOPInject.injectEvents(this, new EventLoginHandler());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                business.onLogin(2);
            }
        }, 3000);
    }

}
