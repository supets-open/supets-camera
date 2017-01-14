package com.supets.lib.supetsrouter.Rule;

import android.content.Context;

/**
 * Usage: <br />
 * <pre>
 * step 1. 调用Router.router方法添加路由
 * step 2. 调用Router.invoke方法根据pattern调用路由
 * </pre>
 * Created by qibin on 2016/10/9.
 */

public class Router {

    /**
     * 添加自定义路由规则
     *
     * @param scheme 路由scheme
     * @param rule   路由规则
     * @return {@code RouterInternal} Router真实调用类
     */
    public static RouterInternal addRule(String scheme, Rule rule) {
        RouterInternal router = RouterInternal.get();
        router.addRule(scheme, rule);
        return router;
    }

    /**
     * 添加路由
     *
     * @param pattern 路由uri
     * @param klass   路由class
     * @return {@code RouterInternal} Router真实调用类
     */
    public static <T> RouterInternal router(String pattern, Class<T> klass) {
        return RouterInternal.get().router(pattern, klass);
    }

    /**
     * 路由调用
     *
     * @param ctx     Context
     * @param pattern 路由uri
     * @return {@code V} 返回对应的返回值
     */
    public static <V> V invoke(Context ctx, String pattern) {
        return RouterInternal.get().invoke(ctx, pattern);
    }

    /**
     * 路由调用
     *
     * @param pattern 路由uri
     * @return {@code V} 返回对应的返回值
     */
    public static Class<?> invoke(String pattern) {
        Rule rule = RouterInternal.get().getRule(pattern);
        return rule!=null?rule.get(pattern):null;
    }

    /**
     * 是否存在该路由
     *
     * @param pattern
     * @return
     */
    public static boolean resolveRouter(String pattern) {
        return RouterInternal.get().resolveRouter(pattern);
    }
}

//public class Router {
//
//    public static void addRule(String key, Class<?> context) {
//        RouterInternal.addRule(key, context);
//    }
//
//    public static Intent getActivityIntent(Context context, String schema) throws Exception {
//        return new ActivityRule().getIntent(context, schema);
//    }
//
//    public static Intent getServiceIntent(Context context, String schema) throws Exception {
//        return new ActivityRule().getIntent(context, schema);
//    }
//
//    public static Intent getBoradCastIntent(Context context, String schema) throws Exception {
//        return new ActivityRule().getIntent(context, schema);
//    }
//
//    public static Class<?> getFragmentClass( String schema) throws Exception {
//        return new FragmentRule().getFragmentClass( schema);
//    }
//
//    public static Fragment getFragment( String schema) throws Exception {
//        return (Fragment) new FragmentRule().getFragmentClass( schema).newInstance();
//    }
//
//    public static Fragment getFragment(String schema, Bundle bundle) throws Exception{
//        Class<?> cls = new FragmentRule().getFragmentClass( schema);
//        Object object = cls.newInstance();
//        Method getInstance = cls.getDeclaredMethod("getInstance", Bundle.class);
//        return (Fragment) getInstance.invoke(object,bundle);
//    }
//
//}
