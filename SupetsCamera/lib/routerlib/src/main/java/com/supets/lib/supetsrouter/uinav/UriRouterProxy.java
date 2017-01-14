package com.supets.lib.supetsrouter.uinav;

import com.supets.lib.supetscontext.App;
import com.supets.lib.supetsrouter.Rule.annotaion.RouterParam;
import com.supets.lib.supetsrouter.Rule.annotaion.RouterUri;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class UriRouterProxy {
    private final static String TAG = UriRouterProxy.class.getSimpleName();
    private static UriRouterProxy mInstance=new UriRouterProxy();

    public static UriRouterProxy getInstance() {
        return mInstance;
    }


    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> aClass) {
        return (T) Proxy.newProxyInstance(aClass.getClassLoader(), new Class<?>[]{aClass},
                new InvocationHandler() {

                    @Override
                    public Object invoke(Object proxy, Method method, Object... args) throws Throwable {
                        StringBuilder stringBuilder = new StringBuilder();
                        RouterUri reqUrl = method.getAnnotation(RouterUri.class);
                        stringBuilder.append(reqUrl.routerUri());
                        Annotation[][] parameterAnnotationsArray = method.getParameterAnnotations();//拿到参数注解
                        int pos = 0;
                        for (int i = 0; i < parameterAnnotationsArray.length; i++) {
                            Annotation[] annotations = parameterAnnotationsArray[i];
                            if (annotations != null && annotations.length != 0) {
                                if (pos == 0) {
                                    stringBuilder.append("?");
                                } else {
                                    stringBuilder.append("&");
                                }
                                pos++;
                                RouterParam reqParam = (RouterParam) annotations[0];
                                stringBuilder.append(reqParam.value());
                                stringBuilder.append("=");
                                stringBuilder.append(args[i]);
                            }
                        }
                        openRouterUri(stringBuilder.toString());
                        return null;
                    }


                });
    }

    private void openRouterUri(String url) {
        UINav.pushUri(App.INSTANCE,url);
    }

}