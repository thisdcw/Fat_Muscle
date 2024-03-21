package com.mxsella.fatmuscle.common;

import android.content.Context;
import android.content.SharedPreferences;

public class Config {
    public static final boolean isProduct = false;

    private static final String name = "sdk_config";

    public static final boolean isDebug = true;
    public static final int VENDOR_ID = 1027;
    public static final int PRODUCT_Id = 24596;

    public static boolean getBoolean(Context context, String str, boolean z) {
        return context.getSharedPreferences(name, 0).getBoolean(str, z);
    }

    public static void saveBoolean(Context context, String str, boolean z) {
        SharedPreferences.Editor edit = context.getSharedPreferences(name, 0).edit();
        edit.putBoolean(str, z);
        edit.commit();
    }

    public static int getInt(Context context, String str, int i) {
        return context.getSharedPreferences(name, 0).getInt(str, i);
    }

}
