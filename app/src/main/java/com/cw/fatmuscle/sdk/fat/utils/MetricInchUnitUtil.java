package com.cw.fatmuscle.sdk.fat.utils;

import com.cw.fatmuscle.common.MyApplication;
import com.cw.fatmuscle.sdk.common.MxsellaConstant;
import com.cw.fatmuscle.sdk.fat.manager.FatConfigManager;

public class MetricInchUnitUtil {
    public static final double cmToIn(double d) {
        return d * 0.393700787d;
    }

    public static final double inToCm(double d) {
        return d * 2.54d;
    }

    public static final double kgToLib(double d) {
        return d * 2.2046226d;
    }

    public static final double libTokg(double d) {
        return d * 0.45359237d;
    }

    public static final double mmToCm(double d) {
        return d / 10.0d;
    }

    public static final double mmToIn(double d) {
        return d * 0.0393701d;
    }

    public static String cmToFtAndIn(double d) {
        StringBuffer stringBuffer = new StringBuffer();
        int i = (int) (d / 30.48d);
        if (i > 0) {
            stringBuffer.append(i + "ft");
        }
        int round = (int) Math.round((d % 30.48d) / 2.54d);
        if (round > 0) {
            stringBuffer.append(round + "in");
        }
        return stringBuffer.toString();
    }

    public static final String getUnitStr(double d) {
        int i = SharedPreferencesUtil.getInt(MyApplication.getInstance(), MxsellaConstant.CUR_UNIT, FatConfigManager.defaultUnit.value());
        if (i == FatConfigManager.UNIT.IN.value()) {
            return NumberUtil.decimalPointStr(mmToIn(d), 2) + " in";
        }
        if (i == 2) {
            return NumberUtil.decimalPointStr(d, 1) + " mm";
        }
        return NumberUtil.decimalPointStr(mmToCm(d), 1) + " cm";
    }

    public static final String getValueStr(double d) {
        int i = SharedPreferencesUtil.getInt(MyApplication.getInstance(), MxsellaConstant.CUR_UNIT, FatConfigManager.defaultUnit.value());
        if (i == 1) {
            return NumberUtil.decimalPointStr(mmToIn(d), 2);
        }
        if (i == 2) {
            return NumberUtil.decimalPointStr(d, 1);
        }
        return NumberUtil.decimalPointStr(mmToCm(d), 1);
    }

    public static final String getValueStr(int i) {
        int i2 = SharedPreferencesUtil.getInt(MyApplication.getInstance(), MxsellaConstant.CUR_UNIT, FatConfigManager.defaultUnit.value());
        if (i2 == 1) {
            return NumberUtil.decimalPointStr(mmToIn(i), 1);
        }
        if (i2 == 2) {
            return i + "";
        }
        return NumberUtil.decimalPointStr(mmToCm(i), 1);
    }

    public static final String getUnitStr() {
        int i = SharedPreferencesUtil.getInt(MyApplication.getInstance(), MxsellaConstant.CUR_UNIT, FatConfigManager.defaultUnit.value());
        return i == 1 ? " in" : i == 2 ? " mm" : " cm";
    }

    public static final float getUnitValue(double d) {
        int i = SharedPreferencesUtil.getInt(MyApplication.getInstance(), MxsellaConstant.CUR_UNIT, FatConfigManager.defaultUnit.value());
        if (i == 1) {
            return Float.parseFloat(NumberUtil.decimalPointStr(mmToIn(d), 2));
        }
        if (i == 2) {
            return Float.parseFloat(NumberUtil.decimalPointStr(d, 1));
        }
        return Float.parseFloat(NumberUtil.decimalPointStr(mmToCm(d), 1));
    }

}
