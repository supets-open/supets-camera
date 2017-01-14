package com.supets.lib.supetsrouter.uinav;

public class UINavConfig{
    //注意特殊处理,打开网页方式
    public static String web = "10000pets://web?url=?";

    public static void setLocalWebViewUrl(String web) {
        UINavConfig.web = web;
    }

}
