package com.supets.petdemo.aop;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 打印日志的切面
 */
public class DefaultTimeInvocationHandler implements InvocationHandler {

    private Object target; //目标对象   

    DefaultTimeInvocationHandler(Object target) {
        this.target = target;
    }


    long startTime;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //执行原有逻辑

        if ("onResume".equals(method.getName())){
             startTime = System.currentTimeMillis();
        }
        if ("onPause".equals(method.getName())){
            long time = System.currentTimeMillis() - startTime;
            if (time > 1000) {
                Log.w("methed_time", method.getName() + "---time:" + time);
            } else {
                Log.v("methed_time", method.getName() + "---time:" + time);
            }
        }

        // Object rev = method.invoke(target, args);
        //执行织入的日志，你可以控制哪些方法执行切入逻辑   

        return null;
    }


    public void beforeMethed() {

    }

    public void afterMethed() {

    }

}   