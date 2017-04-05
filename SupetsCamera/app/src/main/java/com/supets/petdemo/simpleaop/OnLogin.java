package com.supets.petdemo.simpleaop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) //注解用在属性
@EventBase(listenerSetter = "setOnLoginEventListener",
 methednCallBack = "onLogin",
listenerType =OnLoginEventListener.class)
public @interface OnLogin {

}