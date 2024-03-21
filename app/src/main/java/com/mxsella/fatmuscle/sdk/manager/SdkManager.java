package com.mxsella.fatmuscle.sdk.manager;

import com.mxsella.fatmuscle.common.MyApplication;
import com.mxsella.fatmuscle.sdk.common.MxsellaConstant;
import com.mxsella.fatmuscle.sdk.fat.utils.SharedPreferencesUtil;

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
