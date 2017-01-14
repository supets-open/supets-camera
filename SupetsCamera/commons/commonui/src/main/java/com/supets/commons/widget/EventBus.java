package com.supets.commons.widget;

import java.lang.reflect.Method;

/**
 * Created by WeiDongliang on 2015/12/23.
 */
class EventBus {
    // 重要： 回调方法命名必须以onEvent开头， 否则release build会混淆代码导致无法回调。

    public static void postEvent(Object subscriber, String eventName, Object... args) {
        try {
            if (subscriber == null || eventName == null) {
                return;
            }

            Class<?>[] parameterTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null) {
                    parameterTypes[i] = args[i].getClass();
                }
            }

            Method method = null;
            Class<?> clazz = subscriber.getClass();

            while (method == null && clazz != null) {
                try {
                    method = clazz.getDeclaredMethod(eventName, parameterTypes);
                } catch (Exception e) {
                }
                if (method == null) {
                    Method[] methods = clazz.getMethods();
                    for (Method m : methods) {
                        String methodName = m.getName();
                        int parameterCount = m.getParameterTypes().length;
                        if (eventName.equals(methodName) && parameterCount == args.length) {
                            method = m;
                            break;
                        }
                    }
                }
                clazz = clazz.getSuperclass();
            }

            method.setAccessible(true);
            method.invoke(subscriber, args);
        } catch (Exception e) {
        }
    }

}
