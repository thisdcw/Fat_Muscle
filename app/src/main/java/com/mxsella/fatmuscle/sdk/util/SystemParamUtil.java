package com.mxsella.fatmuscle.sdk.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.TelephonyManager;

import java.util.List;
import java.util.Locale;

public class SystemParamUtil {

    public static String getModelName() {
        return Build.MODEL;
    }

    public static String getManufacturerName() {
        return Build.MANUFACTURER;
    }

    public static String getAndroidId(Context context) {
        return "" + Settings.Secure.getString(context.getContentResolver(), "android_id");
    }

    public static String getDeviceID(Context context) {
        return Build.MODEL + "," + ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    public static String getLocalLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static String getVersionName(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo == null) {
            return null;
        }
        return packageInfo.versionName;
    }

    public static int getVersionCode(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo == null) {
            return 0;
        }
        return packageInfo.versionCode;
    }

    public static PackageInfo getPackageInfo(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getPackageName(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo == null) {
            return null;
        }
        return packageInfo.packageName;
    }

    public static boolean isWifiNetwork(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected() && activeNetworkInfo.getTypeName().equalsIgnoreCase("WIFI");
    }

    public static boolean isSDFreeSize(int i) {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return ((((long) statFs.getAvailableBlocks()) * ((long) statFs.getBlockSize())) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID <= ((long) i);
    }

    public static boolean isTopActivy(String str, Activity activity) {
        List<ActivityManager.RunningTaskInfo> runningTasks = ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1);
        String className = runningTasks != null ? runningTasks.get(0).topActivity.getClassName() : null;
        if (className == null) {
            return false;
        }
        return className.equals(str);
    }

    public static boolean isAvilible(Context context, String str) {
        List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < installedPackages.size(); i++) {
            if (installedPackages.get(i).packageName.equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

}
