package com.supets.lib.supetsrouter.uinav;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * @ 参考资料 http://handsomeliuyang.iteye.com/blog/1315283
 * @ 模式导航类
 */
public class UINav extends UINavBase {
    /**
     * @ 正常启动activity
     * @ 没有返回值
     **/
    public static void pushStandard(Context context, Intent intent) {
        push(context, intent);
    }

    /**
     * @ 正常启动activity
     * @ 需要返回值
     * @ 调用onActivityResult
     **/
    public static void pushStandardWithResult(Activity activity, int requestCode, Intent intent) {
        pushActivityForResult(activity, requestCode, intent);
    }

    /**
     * @ 清除栈某个Activity之上的所有Activity，自己不重新创建
     * @ manifest不需要任何配置启动模式
     * @ 调用onNewIntent
     **/
    public static void pushCleanTopRecycler(Context context, Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        push(context, intent);
    }

    /**
     * @ 清除栈某个Activity之上的所有Activity，自己不重新创建
     * @ manifest不需要任何配置启动模式
     * @ 调用onNewIntent
     */
    public static void pushCleanTopRecycler2(Context context, Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        push(context, intent);
    }

    /**
     * @ 使用方法1 清除栈某个Activity之上的所有Activity，自己重新创建
     * @ manifest不需要任何配置启动模式
     * @
     * @ 使用方法2  清除栈某个Activity之上的所有Activity，自己不重新创建
     * @ manifest需要配置启动模式singleTop启动模式
     * @ 调用onNewIntent
     */
    public static void pushCleanTop(Context context, Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        push(context, intent);
    }

    /**
     * @ 多次启动某个Activity，自己不重新创建
     * @ manifest不需要任何配置启动模式
     * @ 调用onNewIntent
     */
    public static void pushSingleTop(Context context, Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        push(context, intent);
    }

    /**
     * @ 清除栈某个Activity之上的所有Activity，自己不重新创建
     * @ manifest需要配置singletask启动模式
     * @ 调用onNewIntent
     * @ 这种模式主要另起一个单任务，需要和taskAffinity一起使用发挥更好的作用
     */
    public static void pushSingleTask(Context context, Intent intent) {
        push(context, intent);
    }

    /**
     * @ 清除栈某个Activity之上的所有Activity，自己不重新创建
     * @ manifest需要配置singleInstance启动模式
     * @ 调用onNewIntent
     * @ 这种模式主要单独使用单独使用一个任务堆栈，有却只有一个单例模式。
     */
    public static void pushSingleInstance(Context context, Intent intent) {
        push(context, intent);
    }

}
