package com.mxsella.fatmuscle.sdk.fat;

import android.graphics.Bitmap;

public final class MeasureDepth {
    private static final String TAG = "MAVTJ";
    private static int iRet;

    public static native int[] DrawBitmap(Bitmap bitmap, byte[] bArr, int i);

    public static native String GetVersion();

    public static native int ParaSet(float f, float f2);

    public static native int SaveBmp(byte[] bArr, int i, int i2, String str);

    static {
        System.loadLibrary("fat");
    }

}
