package com.cw.fatmuscle.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    public static String getDateStrByLong(long j) {
        return new SimpleDateFormat("yyyyMMddHHmm").format(new Date(j));
    }

    public static String date2Str(Date date) {
        return date2Str(date, "yyyy-MM-dd");
    }
    public static String getDate2String(long j, String str) {
        return new SimpleDateFormat(str, Locale.getDefault()).format(new Date(j));
    }

    public static String date2LongStr(Date date) {
        return date2Str(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date str2Date(String str) {
        if (str != null) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
                return new Date();
            }
        }
        return null;
    }

    public static String date2Str(Date date, String str) {
        return date != null ? new SimpleDateFormat(str).format(date) : "";
    }

}
