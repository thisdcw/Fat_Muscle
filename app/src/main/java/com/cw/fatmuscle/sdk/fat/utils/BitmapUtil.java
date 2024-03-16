package com.cw.fatmuscle.sdk.fat.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.cw.fatmuscle.sdk.common.MxsellaConstant;
import com.cw.fatmuscle.sdk.fat.manager.OTGManager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class BitmapUtil {
    public static long frame = 0;
    private static final ArrayList<byte[]> mLinkedList = new ArrayList<>();
    public static int sBitmapHight = OTGManager.BITMAP_WIDTH;
    public static byte[] bytePx = new byte[OTGManager.BITMAP_WIDTH * 150];

    public static final void initList() {
        bytePx = new byte[sBitmapHight * 150];
        mLinkedList.clear();
        for (int i = 0; i < 150; i++) {
            int i2 = sBitmapHight;
            byte[] bArr = new byte[i2];
            for (int i3 = 0; i3 < i2; i3++) {
                bArr[i3] = 0;
                bytePx[(i3 * 150) + i] = 0;
            }
            mLinkedList.add(bArr);
        }
    }

    public static String saveImg(Bitmap bitmap, String str) throws Exception {
        String str2 = MxsellaConstant.IMAGEPATH;
        File file = new File(str2);
        if (!file.exists()) {
            file.mkdirs();
        }
        Log.i("zk", str2 + "/" + str);
        File file2 = new File(str2 + "/" + str);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file2));
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, bufferedOutputStream);
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
        return file2.getPath();
    }

    public static void saveBitmap(Bitmap bitmap, String str, String str2) {
        Log.d("save", "保存");
        File file = new File(str);
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(str + str2);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Log.d("save", "保存成功");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public static final String screenShots(View view) {
        String str = MxsellaConstant.APP_DIR_PATH + "/image/";
        File file = new File(str + "1.png");
        if (!file.exists()) {
            file.delete();
        }
        File file2 = new File(str);
        if (!file2.exists()) {
            file2.mkdirs();
        }
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Bitmap createBitmap = Bitmap.createBitmap(view.getDrawingCache());
        if (createBitmap != null) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(str + "1.png");
                boolean compress = createBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                view.setDrawingCacheEnabled(false);
                view.destroyDrawingCache();
                if (compress) {
                    return str + "1.png";
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static final Bitmap getImageOneDimensional() {
        int i = sBitmapHight * 150;
        int[] iArr = new int[i];
        for (int i2 = 0; i2 < i; i2++) {
            int i3 = bytePx[i2] & 255;
            iArr[i2] = Color.argb(255, i3, i3, i3);
        }
        return Bitmap.createBitmap(iArr, 150, sBitmapHight, Bitmap.Config.ARGB_8888);
    }

    public static final synchronized Bitmap add(byte[] bArr) {
        Bitmap createBitmap;
        synchronized (BitmapUtil.class) {
            ArrayList<byte[]> arrayList = mLinkedList;
            arrayList.remove(0);
            arrayList.add(bArr);
            frame++;
            int[] iArr = new int[sBitmapHight * 150];
            for (int i = 0; i < 150; i++) {
                byte[] bArr2 = mLinkedList.get(i);
                for (int i2 = 0; i2 < sBitmapHight; i2++) {
                    int i3 = bArr2[i2] & 255;
                    int i4 = (i2 * 150) + i;
                    iArr[i4] = Color.argb(255, i3, i3, i3);
                    bytePx[i4] = bArr2[i2];
                }
            }
            createBitmap = Bitmap.createBitmap(iArr, 150, sBitmapHight, Bitmap.Config.ARGB_8888);
        }
        return createBitmap;
    }

}
