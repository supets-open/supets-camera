package com.supets.lib.ottolib;

import com.squareup.otto.Bus;

public class AppBus extends Bus {
    private static AppBus bus;
    public static AppBus getInstance() {
        if (bus == null) {  
            bus = new AppBus();  
        }  
        return bus;  
    }  
}