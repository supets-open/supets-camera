package com.supets.commons.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.supets.commons.MiaCommons;

/**
 * 系统相关工具类
 * <p/>
 * <p>提供获取IMEI、MAC地址、应用版本名称（VersionName）、应用版本号（VersionCode）、Android版本号（SDK_INT）等功能</p>
 *
 * @author Created by FengZuyan on 2015/12/23.
 */
public class SystemUtils {

    private static String sIMEI;

    /**
     * 获取设备IMEI
     *
     * @param context context of the application
     * @return 返回设备IMEI
     */
    public static String getIMEI(Context context) {
        if (TextUtils.isEmpty(sIMEI)) {
            sIMEI = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
            if (TextUtils.isEmpty(sIMEI)) {
                sIMEI = getLocalMacAddress();
            }
        }
        return sIMEI;
    }

    /**
     * 获取设备的MAC地址
     *
     * @return 返回设备的MAC地址
     */
    public static String getLocalMacAddress() {
        try {
            WifiManager wifi = (WifiManager) MiaCommons.getContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            return info.getMacAddress();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取应用的版本名称
     *
     * @return 返回应用的版本名称（Version Name）
     */
    public static String getAppVersionName() {
        PackageManager packageManager = MiaCommons.getContext().getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(MiaCommons.getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String version = "";
        if (packInfo != null) {
            version = packInfo.versionName;
        }
        return version;
    }

    /**
     * 获取应用的版本号
     *
     * @return The version number of this package
     */
    public static String getAppVersionCode() {
        PackageManager packageManager = MiaCommons.getContext().getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(MiaCommons.getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String versionCode = "";
        if (packInfo != null) {
            versionCode = String.valueOf(packInfo.versionCode);
        }
        return versionCode;
    }

    /**
     * 获取设备SDK的版本号
     *
     * @return 返回设备SDK的版本号，即api level.
     */
    public static int getAndroidAPILevel() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取Manifest meta-data中指定key对应的value值
     *
     * @param key key
     * @return 返回key对应的value值
     */
    public static String getMetaData(String key) {
        Bundle metaData = null;
        Object value = null;
        if (key == null) {
            return null;
        }
        try {
            ApplicationInfo ai = MiaCommons.getContext().getPackageManager().getApplicationInfo(MiaCommons.getContext().getPackageName(),
                    PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                value = metaData.get(key);
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        return value == null ? null : value.toString();
    }

    /**
     * 判断当前设备的网络连接是否为WIFI
     *
     * @return 如果网络接连类型为WIFI返回true，否则返回false
     */
    public static boolean isWifiNetwork() {
        try {
            ConnectivityManager connectMgr = (ConnectivityManager) UIUtils.getContext().getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectMgr.getActiveNetworkInfo();
            return info != null && info.getType() == ConnectivityManager.TYPE_WIFI;
        } catch (Exception e) {
            return false;
        }
    }
}
