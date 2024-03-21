package com.mxsella.fatmuscle.utils;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.mxsella.fatmuscle.common.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
    private FileUtil() {
    }

    public static File getCacheDir(String str) {
        File file;
        if (existsSdcard().booleanValue()) {
            File externalCacheDir = MyApplication.getInstance().getExternalCacheDir();
            if (externalCacheDir == null) {
                file = new File(Environment.getExternalStorageDirectory(), "Android/data/" + MyApplication.getInstance().getPackageName() + "/cache/" + str);
            } else {
                file = new File(externalCacheDir, str);
            }
        } else {
            file = new File(MyApplication.getInstance().getCacheDir(), str);
        }
        if (file.exists() || file.mkdirs()) {
            return file;
        }
        return null;
    }

    public static boolean isDiskAvailable() {
        return getDiskAvailableSize() > 10485760;
    }

    public static long getDiskAvailableSize() {
        if (existsSdcard().booleanValue()) {
            StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
            return statFs.getAvailableBlocks() * statFs.getBlockSize();
        }
        return 0L;
    }

    public static Boolean existsSdcard() {
        return Boolean.valueOf(Environment.getExternalStorageState().equals("mounted"));
    }

    public static long getFileOrDirSize(File file) {
        long j = 0;
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] listFiles = file.listFiles();
                if (listFiles != null) {
                    for (File file2 : listFiles) {
                        j += getFileOrDirSize(file2);
                    }
                }
                return j;
            }
            return file.length();
        }
        return 0L;
    }

    public static boolean copy(String str, String str2) throws FileNotFoundException {
        FileOutputStream fileOutputStream;
        FileInputStream fileInputStream;
        File file = new File(str);
        boolean z = false;
        if (file.exists()) {
            File file2 = new File(str2);
            IOUtil.deleteFileOrDir(file2);
            File parentFile = file2.getParentFile();
            if (parentFile.exists() || parentFile.mkdirs()) {
                FileInputStream fileInputStream2 = null;
                try {
                    fileInputStream = new FileInputStream(file);
                    try {
                        fileOutputStream = new FileOutputStream(file2);
                    } catch (Throwable th) {
                        th = th;
                        fileOutputStream = null;
                    }
                } catch (Throwable th2) {
                    fileOutputStream = null;
                    throw  th2;
                }
                try {
                    IOUtil.copy(fileInputStream, fileOutputStream);
                    z = true;
                    IOUtil.closeQuietly(fileInputStream);
                } catch (Throwable th3) {
                    fileInputStream2 = fileInputStream;
                    try {
                        LogUtil.d(th3.getMessage(), th3);
                        IOUtil.closeQuietly(fileInputStream2);
                        IOUtil.closeQuietly(fileOutputStream);
                        return z;
                    } catch (Throwable th4) {
                        IOUtil.closeQuietly(fileInputStream2);
                        IOUtil.closeQuietly(fileOutputStream);
                        throw th4;
                    }
                }
                IOUtil.closeQuietly(fileOutputStream);
            }
            return z;
        }
        return false;
    }

    public static boolean copyFile(InputStream inputStream, String str) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(str);
            byte[] bArr = new byte[1024];
            while (true) {
                int read = inputStream.read(bArr);
                if (read != -1) {
                    fileOutputStream.write(bArr, 0, read);
                } else {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    return true;
                }
            }
        } catch (IOException unused) {
            Log.d("aa", "copy()_false");
            return false;
        }
    }

}
