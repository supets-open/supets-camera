package com.supets.lib.json;

import android.text.TextUtils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JSonUtil {

    public static Gson getGsonExcludeFields(ExclusionStrategy exclusion) {
        GsonBuilder builder = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT | Modifier.STATIC | Modifier.PRIVATE);

        return (exclusion == null) ? builder.create() : builder.setExclusionStrategies(exclusion).create();
    }

    public static String toJson(Object src) {
        Gson gson = getGsonExcludeFields(null);
        return gson.toJson(src);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            return getGsonExcludeFields(null).fromJson(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
           return fromJson2(json,clazz);
        }
    }

    private static <T> T fromJson2(String json, Class<T> clazz) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            //正则过滤
            String jsonfilter = json.replaceAll(
                    "((?<=\\{)\"\\w+\":\"\",|,*\"\\w+\":\"\"|(?<=\\{)\"\\w+\":\\[\\],|,*\"\\w+\":\\[\\])", "");
            return getGsonExcludeFields(null).fromJson(jsonfilter, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T fromJson(String json, Type clazz) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            return getGsonExcludeFields(null).fromJson(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String createJson(Map<String, ?> map) {
        return toJson(map);
    }

    public static String createArrayJson(ArrayList<?> array) {
        return toJson(array);
    }

    public static String createArrayJson(String key, ArrayList<?> array) {
        Gson gson = getGsonExcludeFields(null);
        HashMap<String, ArrayList<?>> map = new HashMap<String, ArrayList<?>>();
        map.put(key, array);
        String text = gson.toJson(map);
        return text;
    }

    public static String filterJson(String json) {
        return   json.replaceAll(
                "((?<=\\{)\"\\w+\":\"\",|,*\"\\w+\":\"\"|(?<=\\{)\"\\w+\":\\[\\],|,*\"\\w+\":\\[\\])", "");
    }

}
