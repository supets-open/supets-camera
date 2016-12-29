package com.supets.commons.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

public class NetworkUtil {

    public static enum NetworkConnection {
        NOT_CONNECTED, WIFI_CONNECTED, MOBILE_2G_CONNECTED, MOBILE_3G_CONNECTED, MOBILE_4G_CONNECTED;

        public String toString() {
            switch (this) {
                case NOT_CONNECTED:
                    return null;

                case WIFI_CONNECTED:
                    return "wifi";

                case MOBILE_2G_CONNECTED:
                    return "2G";

                case MOBILE_3G_CONNECTED:
                    return "3G";

                case MOBILE_4G_CONNECTED:
                    return "4G";

                default:
                    return null;
            }
        }

        ;
    }

    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    public static NetworkConnection getNetworkConnection(Context context) {
        if (context == null) {
            return NetworkConnection.NOT_CONNECTED;
        }
        ConnectivityManager connect = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi != null && wifi.isConnected()) {
            return NetworkConnection.WIFI_CONNECTED;
        } else if (mobile != null && mobile.isConnected()) {
            return getNetworkConnectoinBySubtype(mobile.getSubtype());
        } else {
            return NetworkConnection.NOT_CONNECTED;
        }
    }

    private static NetworkConnection getNetworkConnectoinBySubtype(int subtype) {
        switch (subtype) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NetworkConnection.MOBILE_2G_CONNECTED;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NetworkConnection.MOBILE_3G_CONNECTED;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NetworkConnection.MOBILE_4G_CONNECTED;
            default:
                return NetworkConnection.NOT_CONNECTED;
        }
    }

    public static String getNetworkSubType(Context context) {
        String subType = "Unknown";
        if (context == null) {
            return subType;
        }
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return subType;
        }
        NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
            subType = networkInfo.getSubtypeName();
        }
        return subType;
    }

    public static boolean isNetCircuitBreaker(Context context) {
        return getNetworkConnection(context) == NetworkConnection.NOT_CONNECTED;
    }
}
