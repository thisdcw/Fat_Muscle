package com.mxsella.fatmuscle.sdk;

import android.graphics.Bitmap;

public final class JNIDetecter {
    public static native int[] measureBitmap(Bitmap bitmap, byte[] bArr, int i);

    static {
        System.loadLibrary("fat");
    }

}
