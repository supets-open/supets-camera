package com.supets.lib.supetsrouter.rpc;

import android.text.TextUtils;

public   abstract   class BaseProxy<T extends IBaseUI,C extends IBaseService> implements IProxy<T,C>{

    private Module<T,C >  proxy;

    public abstract String getModuleClassName();
    public abstract Module<T,C > getDefaultModule();

    private Module<T,C > getProxy() {
        if (proxy == null) {
            String module = getModuleClassName();
            if (!TextUtils.isEmpty(module)) {
                try {
                    proxy = (Module<T,C >) Class.forName(module).newInstance();
                } catch (Throwable e) {
                    proxy = getDefaultModule();
                }
            }
        }
        return proxy;
    }

    @Override
    public  T getUiInterface() {
        return getProxy().getUiInterface();
    }

    @Override
    public  C getServiceInterface() {
        return getProxy().getServiceInterface();
    }

}