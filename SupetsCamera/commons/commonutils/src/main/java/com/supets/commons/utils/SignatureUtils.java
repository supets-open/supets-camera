package com.supets.commons.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * <b>获取apk签名</b><br/>
 *
 * 提供一个获取本应用apk签名和获取指定路径apk的签名
 *
 * @author Created by FengZuyan on 2015/12/23.
 */
public class SignatureUtils {

    /**
     * 获取本应用apk签名
     *
     * @param context
     * @return 签名的String
     */
    public static String getSign(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> apps = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
        if( apps == null ) {
            return null;
        }
        Iterator<PackageInfo> iter = apps.iterator();
        while (iter != null && iter.hasNext()) {
            PackageInfo packageinfo = iter.next();
            if( packageinfo != null ) {
                String packageName = packageinfo.packageName;
                if (context.getPackageName().equals(packageName)) {
                    if( packageinfo.signatures != null && packageinfo.signatures.length > 0 ) {
                        System.out.println("----------------------" + packageinfo.signatures[0].toCharsString());
                        return packageinfo.signatures[0].toCharsString();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取指定路径下的apk签名<br/>
     * 注意：本方法在Android api 版本14及以上才能使用
     * @param context
     * @param apkPath
     * @return
     */
    public static String getUnInstallApkSign(Context context, String apkPath) {
        if (!TextUtils.isEmpty(apkPath)) {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageArchiveInfo = packageManager.getPackageArchiveInfo(apkPath, PackageManager.GET_SIGNATURES);
            if (packageArchiveInfo != null) {
                if( packageArchiveInfo.signatures != null && packageArchiveInfo.signatures.length > 0 ) {
                    return packageArchiveInfo.signatures[0].toCharsString();
                }
            }
        }
        return null;
    }

    /**
     * 获取指定路径下的apk签名(不受版本号限制)<br/>
     * @param apkPath
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String getUninstallAPKSignatures(String apkPath) {
        String PATH_PackageParser = "android.content.pm.PackageParser";
        try {

            Class pkgParserCls = Class.forName(PATH_PackageParser);
            Class[] typeArgs = new Class[1];
            typeArgs[0] = String.class;
            @SuppressWarnings("unchecked")
            Constructor pkgParserCt = pkgParserCls.getConstructor(typeArgs);
            Object[] valueArgs = new Object[1];
            valueArgs[0] = apkPath;
            Object pkgParser = pkgParserCt.newInstance(valueArgs);
            DisplayMetrics metrics = new DisplayMetrics();
            metrics.setToDefaults();

            typeArgs = new Class[4];
            typeArgs[0] = File.class;
            typeArgs[1] = String.class;
            typeArgs[2] = DisplayMetrics.class;
            typeArgs[3] = Integer.TYPE;
            @SuppressWarnings("unchecked")
            Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod("parsePackage", typeArgs);
            valueArgs = new Object[4];
            valueArgs[0] = new File(apkPath);
            valueArgs[1] = apkPath;
            valueArgs[2] = metrics;
            valueArgs[3] = PackageManager.GET_SIGNATURES;
            Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser, valueArgs);

            typeArgs = new Class[2];
            typeArgs[0] = pkgParserPkg.getClass();
            typeArgs[1] = Integer.TYPE;
            @SuppressWarnings("unchecked")
            Method pkgParser_collectCertificatesMtd = pkgParserCls.getDeclaredMethod("collectCertificates", typeArgs);
            valueArgs = new Object[2];
            valueArgs[0] = pkgParserPkg;
            valueArgs[1] = PackageManager.GET_SIGNATURES;
            pkgParser_collectCertificatesMtd.invoke(pkgParser, valueArgs);
            Field packageInfoFld = pkgParserPkg.getClass().getDeclaredField("mSignatures");
            Signature[] info = (Signature[]) packageInfoFld.get(pkgParserPkg);
            if( info != null && info.length > 0 ) {
                return info[0].toCharsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
