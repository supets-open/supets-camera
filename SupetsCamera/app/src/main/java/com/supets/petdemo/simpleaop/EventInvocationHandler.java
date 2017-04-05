package com.supets.petdemo.simpleaop;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public class EventInvocationHandler implements InvocationHandler {

    //methed对应onclick
    private Map<String, Method> methedmap;
    private Object srcObj;
    private String methedName;
    private long startTime = 0;

    public void setEventInvocationHandler(Map<String, Method> methedmap, Object activity) {
        this.methedmap = methedmap;
        this.srcObj = activity;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        methedName = method.getName();
        startTime = System.currentTimeMillis();

        if (beforeInvoke()) {
            return null;
        }
        Method mtd = methedmap.get(method.getName());
        if (mtd != null) {
            try {
                mtd.setAccessible(true);
                return mtd.invoke(srcObj, args);
            } finally {
                afterInvoke();
            }
        }
        return method.invoke(proxy, args);
    }

    public boolean beforeInvoke() {
        Log.v("EventInvocationHandler", methedName + "----->" + (System.currentTimeMillis() - startTime));
        return false;
    }

    public boolean afterInvoke() {
        Log.v("EventInvocationHandler", methedName + "----->" + (System.currentTimeMillis() - startTime));
        return false;
    }
}