package com.cw.fatmuscle.sdk.manager;

import android.app.Application;

import com.cw.fatmuscle.common.MyApplication;
import com.cw.fatmuscle.sdk.common.MxsellaConstant;
import com.cw.fatmuscle.sdk.fat.utils.SharedPreferencesUtil;

public class SdkManager {

    public static String accessToken = null;
    public static String firmwareDirectoryBasePath = null;
    public static boolean isSupportMedical = false;

    public static void setIsSupportMedical(boolean z) {
        isSupportMedical = z;
    }

    public static void saveAccessToke(String str) {
        if (str != null) {
            accessToken = str;
            SharedPreferencesUtil.savaString(MyApplication.getInstance(), MxsellaConstant.ACCESS_TOKEN, str);
        }
    }

}
