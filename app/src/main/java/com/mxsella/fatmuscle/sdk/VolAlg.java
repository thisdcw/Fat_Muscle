package com.mxsella.fatmuscle.sdk;

import android.graphics.Bitmap;
import android.util.Log;

public final class VolAlg {
    public static int IMG_HEIGHT = 480;
    public static int IMG_WIDTH = 640;
    private static final String TAG = "MAVT-Java";
    private static int iRet;

    public static native void BmClose();

    public static native int BmDrawBitmap(Bitmap bitmap, byte[] bArr);

    public static native int BmOpen();

    public static native int BmSetColorLevel(int i);

    public static native int BmSetSriLevel(int i);

    public static native void VolClose();

    public static native String VolGetCode();

    public static native int VolOpen(String str, int i, int i2);

    public static native void VolResetRotation();

    public static native void VolSetContrastIntensity(int i, int i2);

    public static native void VolSetOpacityPoints(int i);

    public static native void VolSetRenderMode(int i);

    public static native void VolSetRotationAngle(char c, int i);

    public static native void VolSetSmoothLevel(int i);

    public static native void VolSetSurfaceThreshold(int i);

    public static native void VolSetTintLevel(int i);

    public static native void VolSetZoomRatio(float f);

    public static native int VolSriInit(byte[] bArr, int i);

    public static native int VolSriRun(byte[] bArr, byte[] bArr2);

    private static native int drawFrame(Bitmap bitmap, byte[] bArr);

    private static native int reDrawFrame(Bitmap bitmap, int i);

    public static int open(String str, int i, int i2) {
        Log.i(TAG, "open ret:" + VolOpen(str, i, i2));
        return 0;
    }

    public static void close() {
        VolClose();
    }

    public static int drawBitmap(Bitmap bitmap, byte[] bArr) {
        int drawFrame = drawFrame(bitmap, bArr);
        Log.i(TAG, "bmDraw: :" + drawFrame);
        return drawFrame;
    }

    public static void bmStop() {
        Log.i(TAG, "bm stop");
    }

    public static int reDrawBitmap(Bitmap bitmap) {
        int reDrawFrame = reDrawFrame(bitmap, 1);
        Log.i(TAG, "reDraw: :" + reDrawFrame);
        return reDrawFrame;
    }

    static {
        System.loadLibrary("us3D");
        open(null, IMG_WIDTH, IMG_HEIGHT);
        BmOpen();
        BmSetSriLevel(0);
    }

}
