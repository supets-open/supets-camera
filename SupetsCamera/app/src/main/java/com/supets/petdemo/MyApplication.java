package com.supets.petdemo;

import android.app.Application;

import com.supets.commons.MiaCommons;
import com.supets.pet.utils.fresco.FrescoUtils;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MiaCommons.init(this);
        FrescoUtils.init();
    }

}
