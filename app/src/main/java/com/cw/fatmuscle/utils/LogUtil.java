package com.cw.fatmuscle.utils;

import android.text.TextUtils;
import android.util.Log;

import com.cw.fatmuscle.common.Config;

import java.util.Locale;

public class LogUtil {
    public static String customTagPrefix = "x";

    private LogUtil() {
    }

    private static String generateTag() {

        // 索引0代表getStackTrace()方法本身，索引1代表当前方法，索引2代表调用当前方法的方法

        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[2];
        String className = stackTraceElement.getClassName();
        String methodName = stackTraceElement.getMethodName();
        String name;

        //生成log头
        className = className.substring(className.lastIndexOf(".") + 1);
        if (className.contains("Fragment")){
            name = className.replace("Fragment","-F");
        } else if (className.contains("Activity")) {
            name = className.replace("Activity","-A");
        }else {
            name = methodName;
        }
        String format = String.format(Locale.CHINA, "%s(%d)", name, stackTraceElement.getLineNumber());

        return TextUtils.isEmpty(customTagPrefix) ? format : customTagPrefix + "-" + format;
    }

    public static void d(String str) {
        if (Config.isDebug) {
            Log.d(generateTag(), str);
        }
    }

    public static void d(String str, Throwable th) {
        if (Config.isDebug) {
            Log.d(generateTag(), str, th);
        }
    }

    public static void e(String str) {
        if (Config.isDebug) {
            Log.e(generateTag(), str);
        }
    }

    public static void e(String str, Throwable th) {
        if (Config.isDebug) {
            Log.e(generateTag(), str, th);
        }
    }

    public static void i(String str) {
        if (Config.isDebug) {
            Log.i(generateTag(), str);
        }
    }

    public static void i(String str, Throwable th) {
        if (Config.isDebug) {
            Log.i(generateTag(), str, th);
        }
    }

    public static void v(String str) {
        if (Config.isDebug) {
            Log.v(generateTag(), str);
        }
    }

    public static void v(String str, Throwable th) {
        if (Config.isDebug) {
            Log.v(generateTag(), str, th);
        }
    }

    public static void w(String str) {
        if (Config.isDebug) {
            Log.w(generateTag(), str);
        }
    }

    public static void w(String str, Throwable th) {
        if (Config.isDebug) {
            Log.w(generateTag(), str, th);
        }
    }

    public static void w(Throwable th) {
        if (Config.isDebug) {
            Log.w(generateTag(), th);
        }
    }

    public static void wtf(String str) {
        if (Config.isDebug) {
            Log.wtf(generateTag(), str);
        }
    }

    public static void wtf(String str, Throwable th) {
        if (Config.isDebug) {
            Log.wtf(generateTag(), str, th);
        }
    }

    public static void wtf(Throwable th) {
        if (Config.isDebug) {
            Log.wtf(generateTag(), th);
        }
    }
}
