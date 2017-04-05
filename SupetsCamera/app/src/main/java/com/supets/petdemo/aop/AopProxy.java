package com.supets.petdemo.aop;
import java.lang.reflect.Proxy;

/**
 * SupetsCamera
 *
 * @user lihongjiang
 * @description
 * @date 2017/3/23
 * @updatetime 2017/3/23
 */

public class AopProxy {

    public static Object registerProxy(Object proxyObject, Class<?> proxyInterfaces) {
        //需要代理的接口，被代理类实现的多个接口都必须在这里定义
        Class[] proxyInterface = new Class[]{proxyInterfaces};
        //构建AOP的Advice，这里需要传入业务类的实例
        DefaultTimeInvocationHandler handler = new DefaultTimeInvocationHandler(proxyObject);
        //生成代理类的字节码加载器
        ClassLoader classLoader = proxyObject.getClass().getClassLoader();
        //织入器，织入代码并生成代理类
        return   Proxy.newProxyInstance(classLoader, proxyInterface, handler);
    }

}
