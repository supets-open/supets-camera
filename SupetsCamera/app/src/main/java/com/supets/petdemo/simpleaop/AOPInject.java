package com.supets.petdemo.simpleaop;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * SupetsCamera
 *
 * @user lihongjiang
 * @description
 * @date 2017/3/27
 * @updatetime 2017/3/27
 */

public class AOPInject {

    public static void injectEvents(Object activity,EventInvocationHandler handler) {

        if (handler==null){
            handler=new EventInvocationHandler();
        }

        Class<? extends Object> classd = activity.getClass();
        Method[] methods = classd.getDeclaredMethods();
        for (Method methed : methods) {
            Annotation[] annotations = methed.getAnnotations();
            for (Annotation annotation : annotations) {
                Class<?> annotationType = annotation.annotationType();
                EventBase eventbase = annotationType.getAnnotation(EventBase.class);
                if (eventbase == null) {
                    continue;
                }
                String listenerSetter = eventbase.listenerSetter();
                Class<?> listenerType = eventbase.listenerType();
                String callback = eventbase.methednCallBack();

                //代理方法映射表
                Map<String, Method> methodMap = new HashMap<>();
                methodMap.put(callback, methed);
                //
                try {
                    //得到set方法
                    Method method = activity.getClass().getMethod(listenerSetter, listenerType);
                    //执行回调方法
                    handler.setEventInvocationHandler(methodMap, activity);
                    Object proxy = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class[]{listenerType}, handler);
                    method.invoke(activity, proxy);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }




    }
}
