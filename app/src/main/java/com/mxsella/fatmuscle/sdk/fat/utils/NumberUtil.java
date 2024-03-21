package com.mxsella.fatmuscle.sdk.fat.utils;

import java.text.DecimalFormat;

public class NumberUtil {
    public static String decimalPointStr(double d, int i) {
        try {
            if (i == 1) {
                return new DecimalFormat("0.0").format(d).replace(",", ".");
            }
            if (i == 2) {
                return new DecimalFormat("0.00").format(d).replace(",", ".");
            }
            if (i == 7) {
                return new DecimalFormat("0.0000000").format(d).replace(",", ".");
            }
            return d + "";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    public static String bytesToHexString(byte[] bArr) {
        StringBuilder sb = new StringBuilder("");
        if (bArr == null || bArr.length <= 0) {
            return null;
        }
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b & 255);
            if (hexString.length() < 2) {
                sb.append(0);
            }
            sb.append(hexString);
        }
        return sb.toString();
    }

    public static byte[] hexStringToBytes(String str) {
        if (str == null || str.equals("")) {
            return null;
        }
        String upperCase = str.toUpperCase();
        int length = upperCase.length() / 2;
        char[] charArray = upperCase.toCharArray();
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            bArr[i] = (byte) (charToByte(charArray[i2 + 1]) | (charToByte(charArray[i2]) << 4));
        }
        return bArr;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

}
