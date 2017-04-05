package com.supets.petdemo.activity;

import java.util.Collection;

/**
 * SupetsCamera
 *
 * @user lihongjiang
 * @description
 * @date 2017/3/23
 * @updatetime 2017/3/23
 */

public class Optional<T> {


    private T target;

    private Optional(T target) {
        this.target = target;
    }

    public static <T> Optional<T> of(T obj) {
        return new Optional<T>(obj);
    }

    public boolean isPresent() {
        return target != null;
    }

    public T get() {
        return target;
    }

    public T or(T defaultVaule) {
        return target == null ? defaultVaule : target;
    }

    public static boolean isNullOrEmpty(String target) {
        return target != null && target.trim().length() > 0;
    }

    public static boolean isNullOrEmpty(Collection target) {
        return target != null && target.size()> 0;
    }

    public static String emptyToNull(String target) {
        return target != null && target.trim().length() == 0 ? null : target;
    }

    public static String nullToEmpty(String target) {
        return target == null ? "" : target;
    }

    public static boolean equals(String a, String b) {
        return (a == null) ? (b == null) : a.equals(b);
    }

}
