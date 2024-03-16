package com.cw.fatmuscle.sdk.fat.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {

    private static final int mode = 0;
    private static final String name = "sdk_config";

    public static void saveBoolean(Context context, String str, boolean z) {
        SharedPreferences.Editor edit = context.getSharedPreferences(name, 0).edit();
        edit.putBoolean(str, z);
        edit.commit();
    }

    public static boolean getBoolean(Context context, String str, boolean z) {
        return context.getSharedPreferences(name, 0).getBoolean(str, z);
    }

    public static void saveLong(Context context, String str, long j) {
        SharedPreferences.Editor edit = context.getSharedPreferences(name, 0).edit();
        edit.putLong(str, j);
        edit.commit();
    }

    public static long getLong(Context context, String str, long j) {
        return context.getSharedPreferences(name, 0).getLong(str, j);
    }

    public static void saveInt(Context context, String str, int i) {
        SharedPreferences.Editor edit = context.getSharedPreferences(name, 0).edit();
        edit.putInt(str, i);
        edit.commit();
    }

    public static int getInt(Context context, String str, int i) {
        return context.getSharedPreferences(name, 0).getInt(str, i);
    }

    public static String getString(Context context, String str, String str2) {
        return context.getSharedPreferences(name, 0).getString(str, str2);
    }

    public static void savaString(Context context, String str, String str2) {
        SharedPreferences.Editor edit = context.getSharedPreferences(name, 0).edit();
        edit.putString(str, str2);
        edit.commit();
    }

}
