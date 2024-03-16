package com.cw.fatmuscle.utils;

public class ArrayUtil {
    public static int[] getArrayByStr(String str, String str2) {
        if (str == null) {
            return null;
        }
        String[] split = str.split(str2);
        int length = split.length;
        int[] iArr = new int[length];
        for (int i = 0; i < length; i++) {
            if (!"".equalsIgnoreCase(split[i])) {
                iArr[i] = Integer.parseInt(split[i]);
            }
        }
        return iArr;
    }

    public static int[] getAvgArray(int[] iArr) {
        int i = iArr[1];
        if (iArr[i - 1] == 0) {
            int i2 = i - 2;
            if (iArr[i2] == 0) {
                i = i2;
            }
        }
        int[] iArr2 = new int[((int) Math.ceil(i / 26)) * 2];
        int i3 = 0;
        int i4 = 4;
        while (i4 < i) {
            iArr2[i3] = iArr[i4];
            int i5 = i3 + 1;
            int i6 = i4 + 1;
            iArr2[i5] = iArr[i6];
            i3 = i5 + 1;
            i4 = i6 + 24 + 1;
        }
        return iArr2;
    }

    public static float avgValue(int[] iArr) {
        if (iArr == null || iArr.length <= 1) {
            return -1.0f;
        }
        int i = 0;
        int i2 = 0;
        float f = 0.0f;
        while (i < iArr.length) {
            int i3 = i + 1;
            int i4 = iArr[i3];
            if (i4 > 0) {
                f += i4;
                i2++;
            }
            i = i3 + 1;
        }
        if (i2 <= 0) {
            return 0.0f;
        }
        if (i2 > 2) {
            f = (f - getMinValue(iArr)) - getMaxValue(iArr);
            i2 -= 2;
        }
        return f / i2;
    }

    public static int getMaxValue(int[] iArr) {
        int i = 0;
        if (iArr == null || iArr.length <= 1) {
            return 0;
        }
        int i2 = 0;
        while (i < iArr.length) {
            int i3 = i + 1;
            int i4 = iArr[i3];
            if (i4 > i2) {
                i2 = i4;
            }
            i = i3 + 1;
        }
        return i2;
    }

    public static int getMinValue(int[] iArr) {
        int i = 0;
        if (iArr == null || iArr.length <= 1) {
            return 0;
        }
        int i2 = 1000;
        while (i < iArr.length) {
            int i3 = i + 1;
            int i4 = iArr[i3];
            if (i4 > 0 && i4 < i2) {
                i2 = i4;
            }
            i = i3 + 1;
        }
        return i2;
    }

    public static int getMinXValue(int[] iArr) {
        int i = 0;
        if (iArr != null) {
            int i2 = 1;
            if (iArr.length > 1) {
                int i3 = iArr[1];
                while (i < iArr.length) {
                    int i4 = i + 1;
                    int i5 = iArr[i4];
                    if (i5 > 0 && i5 < i3) {
                        i2 = i4;
                        i3 = i5;
                    }
                    i = i4 + 1;
                }
                return i2;
            }
        }
        return 0;
    }

    public static int getMaxXValue(int[] iArr) {
        int i = 0;
        if (iArr != null) {
            int i2 = 1;
            if (iArr.length > 1) {
                int i3 = 0;
                while (i < iArr.length) {
                    int i4 = i + 1;
                    int i5 = iArr[i4];
                    if (i5 > i3) {
                        i2 = i4;
                        i3 = i5;
                    }
                    i = i4 + 1;
                }
                return i2;
            }
        }
        return 0;
    }

    public static int[] getMaxValueAndX(int[] iArr) {
        if (iArr == null || iArr.length <= 1) {
            return new int[]{0, 0};
        }
        int[] iArr2 = {0, 0};
        int i = 0;
        while (i < iArr.length) {
            int i2 = i + 1;
            int i3 = iArr[i2];
            if (i3 > iArr2[1]) {
                iArr2[0] = iArr[i2 - 1];
                iArr2[1] = i3;
            }
            i = i2 + 1;
        }
        return iArr2;
    }

    public static int compare(int[] iArr, int[] iArr2) {
        if (iArr == null || iArr2 == null || iArr2.length != iArr.length) {
            return -1;
        }
        int i = 0;
        int i2 = 0;
        while (i < iArr.length) {
            int i3 = i + 1;
            if (Math.abs(iArr[i3] - iArr2[i3]) > 5) {
                i2++;
            }
            i = i3 + 1;
        }
        return i2;
    }

    public static int[] smoothness(int[] iArr) {
        if (iArr == null || iArr.length == 0) {
            return null;
        }
        float avgValue = avgValue(iArr);
        int i = 0;
        while (i < iArr.length) {
            int i2 = i + 1;
            if (Math.abs(iArr[i2] - avgValue) > 10.0f) {
                iArr[i2] = (int) avgValue;
            }
            i = i2 + 1;
        }
        return iArr;
    }

    public static int[] rectifyForFubu(int[] iArr, int i) {
        if (iArr == null || iArr.length == 0) {
            return null;
        }
        int i2 = 0;
        while (i2 < iArr.length) {
            int i3 = i2 + 1;
            if (iArr[i3] < i) {
                iArr[i3] = i;
            }
            i2 = i3 + 1;
        }
        return iArr;
    }

    public static int[] getSubAvgForFubu(int[] iArr) {
        if (iArr == null) {
            return null;
        }
        int ceil = ((int) Math.ceil(iArr.length / 26)) * 2;
        int[] iArr2 = new int[ceil];
        int[] maxValueAndX = getMaxValueAndX(iArr);
        float f = 24;
        int ceil2 = (int) Math.ceil(maxValueAndX[0] / f);
        int i = ceil2 > 0 ? (maxValueAndX[0] - iArr[1]) / ceil2 : 0;
        int i2 = 0;
        int i3 = 0;
        while (i2 < maxValueAndX[0]) {
            iArr2[i2] = i3 * 24;
            int i4 = i2 + 1;
            iArr2[i4] = iArr[1] + (i * i3);
            i3++;
            i2 = i4 + 1;
        }
        int ceil3 = (int) Math.ceil((iArr[iArr.length - 1] - maxValueAndX[0]) / f);
        int i5 = ceil3 > 0 ? (maxValueAndX[1] - iArr[iArr.length - 1]) / ceil3 : 0;
        int i6 = maxValueAndX[0];
        int i7 = 0;
        while (i6 < ceil) {
            iArr2[i6] = maxValueAndX[0] + (i7 * 24);
            int i8 = i6 + 1;
            iArr2[i8] = maxValueAndX[1] - (i5 * i7);
            i7++;
            i6 = i8 + 1;
        }
        return iArr2;
    }

    public static int[] getSubAvgArray(int[] iArr) {
        if (iArr == null) {
            return null;
        }
        int length = iArr.length;
        int[] iArr2 = new int[((int) Math.ceil(length / 26)) * 2];
        int i = 0;
        int i2 = 0;
        while (i < length) {
            iArr2[i2] = iArr[i];
            int i3 = i + 1;
            int i4 = i2 + 1;
            iArr2[i4] = iArr[i3];
            i2 = i4 + 1;
            i = i3 + 24 + 1;
        }
        return iArr2;
    }
}
