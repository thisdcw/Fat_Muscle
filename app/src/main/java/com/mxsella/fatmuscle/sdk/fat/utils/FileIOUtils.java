package com.mxsella.fatmuscle.sdk.fat.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public final class FileIOUtils {
    private static final String LINE_SEP = System.getProperty("line.separator");
    private static int sBufferSize = 8192;
    public static final int MAX_ACTIVITY_COUNT_UNLIMITED = Integer.MAX_VALUE;

    private FileIOUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static boolean writeFileFromIS(String str, InputStream inputStream) {
        return writeFileFromIS(getFileByPath(str), inputStream, false);
    }

    public static boolean writeFileFromIS(String str, InputStream inputStream, boolean z) {
        return writeFileFromIS(getFileByPath(str), inputStream, z);
    }

    public static boolean writeFileFromIS(File file, InputStream inputStream) {
        return writeFileFromIS(file, inputStream, false);
    }

    public static boolean writeFileFromIS(File file, InputStream inputStream, boolean z) {
        if (!createOrExistsFile(file) || inputStream == null) {
            return false;
        }
        BufferedOutputStream bufferedOutputStream = null;
        try {
            BufferedOutputStream bufferedOutputStream2 = new BufferedOutputStream(new FileOutputStream(file, z));
            try {
                byte[] bArr = new byte[sBufferSize];
                while (true) {
                    int read = inputStream.read(bArr, 0, sBufferSize);
                    if (read != -1) {
                        bufferedOutputStream2.write(bArr, 0, read);
                    } else {
                        CloseUtils.closeIO(inputStream, bufferedOutputStream2);
                        return true;
                    }
                }
            } catch (IOException e) {
                e = e;
                bufferedOutputStream = bufferedOutputStream2;
                e.printStackTrace();
                CloseUtils.closeIO(inputStream, bufferedOutputStream);
                return false;
            } catch (Throwable th) {
                th = th;
                bufferedOutputStream = bufferedOutputStream2;
                CloseUtils.closeIO(inputStream, bufferedOutputStream);
                throw th;
            }
        } catch (Throwable th2) {
            th2.printStackTrace();
        }
        return z;
    }

    public static boolean writeFileFromBytesByStream(String str, byte[] bArr) {
        return writeFileFromBytesByStream(getFileByPath(str), bArr, false);
    }

    public static boolean writeFileFromBytesByStream(String str, byte[] bArr, boolean z) {
        return writeFileFromBytesByStream(getFileByPath(str), bArr, z);
    }

    public static boolean writeFileFromBytesByStream(File file, byte[] bArr) {
        return writeFileFromBytesByStream(file, bArr, false);
    }

    public static boolean writeFileFromBytesByStream(File file, byte[] bArr, boolean z) {
        if (bArr == null || !createOrExistsFile(file)) {
            return false;
        }
        BufferedOutputStream bufferedOutputStream = null;
        try {
            BufferedOutputStream bufferedOutputStream2 = new BufferedOutputStream(new FileOutputStream(file, z));
            try {
                bufferedOutputStream2.write(bArr);
                CloseUtils.closeIO(bufferedOutputStream2);
                return true;
            } catch (IOException e) {
                e = e;
                bufferedOutputStream = bufferedOutputStream2;
                e.printStackTrace();
                CloseUtils.closeIO(bufferedOutputStream);
                return false;
            } catch (Throwable th) {
                th = th;
                bufferedOutputStream = bufferedOutputStream2;
                CloseUtils.closeIO(bufferedOutputStream);
                throw th;
            }
        } catch (Throwable th2) {
            th2.printStackTrace();
        }
        return z;
    }

    public static boolean writeFileFromBytesByChannel(String str, byte[] bArr, boolean z) {
        return writeFileFromBytesByChannel(getFileByPath(str), bArr, false, z);
    }

    public static boolean writeFileFromBytesByChannel(String str, byte[] bArr, boolean z, boolean z2) {
        return writeFileFromBytesByChannel(getFileByPath(str), bArr, z, z2);
    }

    public static boolean writeFileFromBytesByChannel(File file, byte[] bArr, boolean z) {
        return writeFileFromBytesByChannel(file, bArr, false, z);
    }

    public static boolean writeFileFromBytesByChannel(File file, byte[] bArr, boolean z, boolean z2) {
        if (bArr == null) {
            return false;
        }
        FileChannel fileChannel = null;
        try {
            try {
                fileChannel = new FileOutputStream(file, z).getChannel();
                fileChannel.position(fileChannel.size());
                fileChannel.write(ByteBuffer.wrap(bArr));
                if (z2) {
                    fileChannel.force(true);
                }
                CloseUtils.closeIO(fileChannel);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                CloseUtils.closeIO(fileChannel);
                return false;
            }
        } catch (Throwable th) {
            CloseUtils.closeIO(fileChannel);
            throw th;
        }
    }

    public static boolean writeFileFromBytesByMap(String str, byte[] bArr, boolean z) {
        return writeFileFromBytesByMap(str, bArr, false, z);
    }

    public static boolean writeFileFromBytesByMap(String str, byte[] bArr, boolean z, boolean z2) {
        return writeFileFromBytesByMap(getFileByPath(str), bArr, z, z2);
    }

    public static boolean writeFileFromBytesByMap(File file, byte[] bArr, boolean z) {
        return writeFileFromBytesByMap(file, bArr, false, z);
    }

    public static boolean writeFileFromBytesByMap(File file, byte[] bArr, boolean z, boolean z2) {
        if (bArr == null || !createOrExistsFile(file)) {
            return false;
        }
        FileChannel fileChannel = null;
        try {
            try {
                fileChannel = new FileOutputStream(file, z).getChannel();
                MappedByteBuffer map = fileChannel.map(FileChannel.MapMode.READ_WRITE, fileChannel.size(), bArr.length);
                map.put(bArr);
                if (z2) {
                    map.force();
                }
                CloseUtils.closeIO(fileChannel);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                CloseUtils.closeIO(fileChannel);
                return false;
            }
        } catch (Throwable th) {
            CloseUtils.closeIO(fileChannel);
            throw th;
        }
    }

    public static boolean writeFileFromString(String str, String str2) {
        return writeFileFromString(getFileByPath(str), str2, false);
    }

    public static boolean writeFileFromString(String str, String str2, boolean z) {
        return writeFileFromString(getFileByPath(str), str2, z);
    }

    public static boolean writeFileFromString(File file, String str) {
        return writeFileFromString(file, str, false);
    }

    public static boolean writeFileFromString(File file, String str, boolean z) {
        if (file == null || str == null || !createOrExistsFile(file)) {
            return false;
        }
        BufferedWriter bufferedWriter = null;
        try {
            try {
                BufferedWriter bufferedWriter2 = new BufferedWriter(new FileWriter(file, z));
                try {
                    bufferedWriter2.write(str);
                    CloseUtils.closeIO(bufferedWriter2);
                    return true;
                } catch (IOException e) {
                    e = e;
                    bufferedWriter = bufferedWriter2;
                    e.printStackTrace();
                    CloseUtils.closeIO(bufferedWriter);
                    return false;
                } catch (Throwable th) {
                    th = th;
                    bufferedWriter = bufferedWriter2;
                    CloseUtils.closeIO(bufferedWriter);
                    throw th;
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        } catch (Throwable th2) {
            th2.printStackTrace();
        }
        return z;
    }

    public static List<String> readFile2List(String str) {
        return readFile2List(getFileByPath(str), (String) null);
    }

    public static List<String> readFile2List(String str, String str2) {
        return readFile2List(getFileByPath(str), str2);
    }

    public static List<String> readFile2List(File file) {
        return readFile2List(file, 0, (int) MAX_ACTIVITY_COUNT_UNLIMITED, (String) null);
    }

    public static List<String> readFile2List(File file, String str) {
        return readFile2List(file, 0, (int) MAX_ACTIVITY_COUNT_UNLIMITED, str);
    }

    public static List<String> readFile2List(String str, int i, int i2) {
        return readFile2List(getFileByPath(str), i, i2, (String) null);
    }

    public static List<String> readFile2List(String str, int i, int i2, String str2) {
        return readFile2List(getFileByPath(str), i, i2, str2);
    }

    public static List<String> readFile2List(File file, int i, int i2) {
        return readFile2List(file, i, i2, (String) null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static List<String> readFile2List(File file, int i, int i2, String str) {
        BufferedReader bufferedReader;
        String str2 = null;
        if (!isFileExists(file) || i > i2) {
            return null;
        }
        try {
            try {
                ArrayList arrayList = new ArrayList();
                if (isSpace(str)) {
                    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                } else {
                    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), str));
                }
                int i3 = 1;
                while (true) {
                    try {
                        String readLine = bufferedReader.readLine();
                        if (readLine == null || i3 > i2) {
                            break;
                        }
                        if (i <= i3 && i3 <= i2) {
                            arrayList.add(readLine);
                        }
                        i3++;
                    } catch (IOException e) {
                        e = e;
                        e.printStackTrace();
                        CloseUtils.closeIO(bufferedReader);
                        return null;
                    }
                }
                CloseUtils.closeIO(bufferedReader);
                return arrayList;
            } catch (IOException e2) {

                e2.printStackTrace();
            } catch (Throwable th) {
                th.printStackTrace();
            }
        } catch (Throwable th2) {
            th2.printStackTrace();
        }
        return null;
    }

    public static String readFile2String(String str) {
        return readFile2String(getFileByPath(str), (String) null);
    }

    public static String readFile2String(String str, String str2) {
        return readFile2String(getFileByPath(str), str2);
    }

    public static String readFile2String(File file) {
        return readFile2String(file, (String) null);
    }

    public static String readFile2String(File file, String str) {
        BufferedReader bufferedReader;
        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader2 = null;
        if (isFileExists(file)) {
            try {
                sb = new StringBuilder();
                if (isSpace(str)) {
                    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                } else {
                    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), str));
                }
            } catch (IOException e) {
                e.printStackTrace();
                bufferedReader = null;
            } catch (Throwable th) {
                th.printStackTrace();
                CloseUtils.closeIO(bufferedReader2);
                throw th;
            }
            try {
                try {
                    String readLine = bufferedReader.readLine();
                    if (readLine != null) {
                        sb.append(readLine);
                        while (true) {
                            String readLine2 = bufferedReader.readLine();
                            if (readLine2 == null) {
                                break;
                            }
                            sb.append(LINE_SEP).append(readLine2);
                        }
                    }
                    String sb2 = sb.toString();
                    CloseUtils.closeIO(bufferedReader);
                    return sb2;
                } catch (Throwable th2) {
                    th2.printStackTrace();
                    bufferedReader2 = bufferedReader;
                    CloseUtils.closeIO(bufferedReader2);
                    throw th2;
                }
            } catch (IOException e2) {
                e2.printStackTrace();
                CloseUtils.closeIO(bufferedReader);
                return null;
            }
        }
        return null;
    }

    public static byte[] readFile2BytesByStream(String str) {
        return readFile2BytesByStream(getFileByPath(str));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static byte[] readFile2BytesByStream(File file) {
        FileInputStream fileInputStream;
        Throwable th;
        ByteArrayOutputStream byteArrayOutputStream;
        if (!isFileExists(file)) {
            return null;
        }
        try {
            try {
                fileInputStream = new FileInputStream(file);
            } catch (IOException e) {
                e = e;
                byteArrayOutputStream = null;
                fileInputStream = null;
            } catch (Throwable th2) {
                fileInputStream = null;
                th = th2;
                file = null;
            }
            try {
                byteArrayOutputStream = new ByteArrayOutputStream();
            } catch (Throwable th3) {
                th = th3;
                file = null;
                CloseUtils.closeIO((Closeable) fileInputStream, (Closeable) file);
                throw th;
            }
            try {
                byte[] bArr = new byte[sBufferSize];
                while (true) {
                    int read = fileInputStream.read(bArr, 0, sBufferSize);
                    if (read != -1) {
                        byteArrayOutputStream.write(bArr, 0, read);
                    } else {
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        CloseUtils.closeIO(fileInputStream, byteArrayOutputStream);
                        return byteArray;
                    }
                }
            } catch (IOException e3) {
                e3.printStackTrace();
                CloseUtils.closeIO(fileInputStream, byteArrayOutputStream);
                return null;
            }
        } catch (Throwable th4) {
            th = th4;
        }
        return new byte[0];
    }

    public static byte[] readFile2BytesByChannel(String str) throws Throwable {
        return readFile2BytesByChannel(getFileByPath(str));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static byte[] readFile2BytesByChannel(File file) throws Throwable {
        Throwable th;
        FileChannel fileChannel;
        if (isFileExists(file)) {
            try {
                try {
                    fileChannel = new RandomAccessFile(file, "r").getChannel();
                } catch (IOException e) {
                    e = e;
                    fileChannel = null;
                } catch (Throwable th2) {
                    th = th2;
                    file = null;
                    CloseUtils.closeIO((Closeable) file);
                    throw th;
                }
                try {
                    ByteBuffer allocate = ByteBuffer.allocate((int) fileChannel.size());
                    do {
                    } while (fileChannel.read(allocate) > 0);
                    byte[] array = allocate.array();
                    CloseUtils.closeIO(fileChannel);
                    return array;
                } catch (IOException e2) {
                    e2.printStackTrace();
                    CloseUtils.closeIO(fileChannel);
                    return null;
                }
            } catch (Throwable th3) {
                CloseUtils.closeIO((Closeable) file);
                throw th3;
            }
        }
        return null;
    }

    public static byte[] readFile2BytesByMap(String str) {
        return readFile2BytesByMap(getFileByPath(str));
    }

    public static byte[] readFile2BytesByMap(File file) {
        Throwable th;
        FileChannel fileChannel;
        if (isFileExists(file)) {
            try {
                fileChannel = new RandomAccessFile(file, "r").getChannel();
            } catch (IOException e) {
                e = e;
                fileChannel = null;
            } catch (Throwable th2) {
                fileChannel = null;
                CloseUtils.closeIO(fileChannel);
                throw th2;
            }
            try {
                try {
                    int size = (int) fileChannel.size();
                    byte[] bArr = new byte[size];
                    fileChannel.map(FileChannel.MapMode.READ_ONLY, 0L, size).load().get(bArr, 0, size);
                    CloseUtils.closeIO(fileChannel);
                    return bArr;
                } catch (Throwable th3) {

                    CloseUtils.closeIO(fileChannel);
                    throw th3;
                }
            } catch (IOException e2) {
                e2.printStackTrace();
                CloseUtils.closeIO(fileChannel);
                return null;
            }
        }
        return null;
    }

    public static void setBufferSize(int i) {
        sBufferSize = i;
    }

    private static File getFileByPath(String str) {
        if (isSpace(str)) {
            return null;
        }
        return new File(str);
    }

    private static boolean createOrExistsFile(String str) {
        return createOrExistsFile(getFileByPath(str));
    }

    private static boolean createOrExistsFile(File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return file.isFile();
        }
        if (createOrExistsDir(file.getParentFile())) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private static boolean createOrExistsDir(File file) {
        return file != null && (!file.exists() ? !file.mkdirs() : !file.isDirectory());
    }

    private static boolean isFileExists(File file) {
        return file != null && file.exists();
    }

    private static boolean isSpace(String str) {
        if (str == null) {
            return true;
        }
        int length = str.length();
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
