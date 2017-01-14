package com.supets.lib.supetsrouter.uinav;

import java.io.Serializable;

/**
 * <T>主要作用</T>
 * <L>解决混淆带来类名不一致问题</L>。
 * <L>传递页面路径来源</L>
 */
public final class NavigationStack implements Serializable {

    public String mClassName;

    public NavigationStack(String mClassName) {
        this.mClassName = mClassName;
    }

    public NavigationStack() {
    }

    public NavigationStack setClassName(String mClassName) {
        this.mClassName = mClassName;
        return this;
    }

    public Class<?> getClassType() {
        try {
            return getClass().getClassLoader().loadClass(mClassName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}