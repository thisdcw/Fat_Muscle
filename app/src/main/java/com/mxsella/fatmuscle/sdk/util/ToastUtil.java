package com.mxsella.fatmuscle.sdk.util;

import android.content.Context;
import android.widget.Toast;

import com.mxsella.fatmuscle.common.MyApplication;

public class ToastUtil {
    public static final int LENGTH_LONG = 1;
    public static final int LENGTH_SHORT = 0;
    private static Toast mTosat;

    public static void showToast(Context context, String str, int i) {
        if (context == null) {
            return;
        }
        Toast toast = mTosat;
        if (toast != null) {
            toast.setText(str);
        } else if (i == 0) {
            mTosat = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        } else if (i == 1) {
            mTosat = Toast.makeText(context, str, Toast.LENGTH_LONG);
        }
        mTosat.show();
    }

    public static void showToast(String str, int i) {
        showToast(MyApplication.getInstance(), str, i);
    }

    public static void showToast(int i, int i2) {
        showToast(MyApplication.getInstance().getString(i), i2);
    }

    public static void showToast(Context context, int i, int i2) {
        if (context != null) {
            showToast(context, context.getString(i), i2);
        }
    }

    public static void cancelTosat() {
        Toast toast = mTosat;
        if (toast != null) {
            toast.cancel();
        }
    }

}
