package com.cw.fatmuscle.utils;

import android.content.Context;
import android.graphics.Paint;

public class PaintUtil {
    public static int getTextWidth(Paint paint, String str) {
        if (str == null || str.length() <= 0) {
            return 0;
        }
        int length = str.length();
        float[] fArr = new float[length];
        paint.getTextWidths(str, fArr);
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            i += (int) Math.ceil(fArr[i2]);
        }
        return i;
    }

    public static float sp2px(Context context, float f) {
        return (f * context.getResources().getDisplayMetrics().scaledDensity) + 0.5f;
    }
}
