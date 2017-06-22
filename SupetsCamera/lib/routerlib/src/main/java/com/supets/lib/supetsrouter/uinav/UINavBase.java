package com.supets.lib.supetsrouter.uinav;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

class UINavBase {

    /**
     * 关闭当前activity
     * 没有返回值
     **/
    public static void pop(Activity activity) {
        activity.finish();
    }

    /**
     * 关闭当前activity
     * 有返回值
     * 调用onActivityResult
     **/
    public static void popActivityResult(Activity activity, int resultCode, Intent intent) {
        activity.setResult(resultCode, intent);
        activity.finish();
    }

    /**
     * 启动activity
     * 没有返回值
     **/
    protected static void push(Context context, Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 启动activity
     * 需要返回值
     * 调用onActivityResult
     **/
    public static void pushActivityForResult(Activity activity, int requestCode, Intent intent) {
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 隐式启动
     */
    public static void pushCustomAction(Context context, String url, String action, Bundle bundle) {
        try {
            Intent intent = new Intent();
            if (!TextUtils.isEmpty(url)) {
                Uri uri = Uri.parse(url);
                intent.setData(uri);
            }
            intent.setAction(action);
            intent.putExtras(bundle == null ? new Bundle() : bundle);
            push(context, intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐式启动,带返回值，需要直接配置在相应的activity,不能统一导航。
     */
    public static void pushActivityForResult(Activity activity, String url, String action,
                                             int requestCode, Bundle bundle) {
        try {
            Intent intent = new Intent();
            if (!TextUtils.isEmpty(url)) {
                Uri uri = Uri.parse(url);
                intent.setData(uri);
            }
            intent.setAction(action);
            intent.putExtras(bundle == null ? new Bundle() : bundle);
            pushActivityForResult(activity, requestCode, intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐式启动
     *
     * @ 支持app内部http/https统一导航，需要自己实现的webview配置协议，例如supets://web
     * @ 支持自定义uri协议。
     */
    public static void pushUri(Context context, String url) {
        try {
            url = url == null ? null : url.trim();
            if (TextUtils.isEmpty(url)) {
                return;
            }
            Uri uri = Uri.parse(url);
            if (uri != null) {
                String scheme = uri.getScheme();
                if (scheme != null && scheme.startsWith("http")) {
                  url= UINavConfig.web.concat(url);
                }
            }
            pushCustomAction(context, url, Intent.ACTION_VIEW, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
