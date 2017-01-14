package com.supets.lib.supetsrouter.rpc;


 interface IProxy<T extends IBaseUI,C  extends IBaseService>{

    T getUiInterface();

    C getServiceInterface();
}
