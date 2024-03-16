package com.cw.fatmuscle.sdk.sproutcameramedical;

import android.graphics.Bitmap;
import android.util.Log;

public class USL implements Runnable{
    private static USL usl;

    public static native int GetODSVolumeData(byte[] bArr, int i);

    public static native byte[] GetRawVolumeData();

    public static native float[] GetVoiPos();

    public native int AdaptActive(String str, String str2);

    public native int AdaptDec(String str, String str2, int i);

    public native int AdaptEnable(String str, String str2);

    public native int AdaptGetId(String str, String str2);

    public native int AdaptGetLevel(String str, String str2);

    public native int AdaptInc(String str, String str2, int i);

    public native String AdaptList(String str, String str2);

    public native String AdaptName(String str, String str2);

    public native int AdaptSetLevel(String str, String str2, int i);

    public native int AdaptTrigger(String str, String str2, int i);

    public native String AdaptValue(String str, String str2);

    public native int BDrawBitmap(Bitmap bitmap);

    public native int ConnectImageDevice(String str);

    public native int DeCompressData(byte[] bArr, int i, byte[] bArr2, int i2);

    public native int DisConnectImageDevice();

    public native int FourDDrawBitmap(Bitmap bitmap);

    public native int FourDDrawBitmaps(Bitmap bitmap, Bitmap bitmap2, Bitmap bitmap3, Bitmap bitmap4);

    public native int GetEngineStateEnum();

    public native int GetImgingStateEnum();

    public native String GetLibVersion();

    public native float[] GetMMarkLinePara();

    public native int[] GetMarkLinePos();

    public native float[] GetRoiFanPara();

    public native int ImgDspDataWrite(byte[] bArr, int i);

    public native int InitImageDevice();

    public native int InitImageDevice(String str, String str2);

    public native int MDrawBitmap(Bitmap bitmap);

    public native int MPRXDrawBitmap(Bitmap bitmap);

    public native int MPRYDrawBitmap(Bitmap bitmap);

    public native int MPRZDrawBitmap(Bitmap bitmap);

    public native int Open(String str);

    public native void ReadRfData(int i);

    public native void SetDTGCPos(int[] iArr);

    public native void SetDeltaMoveX(int i);

    public native void SetMarkLinePos(float f);

    public USL() {
        try {
//            System.loadLibrary("gnustl_shared");
//            System.loadLibrary("Qt5Core");
//            System.loadLibrary("Qt5Network");
//            System.loadLibrary("Qt5AndroidExtras");
            System.loadLibrary("opencv_java4");
//            System.loadLibrary("opencv_core");
//            System.loadLibrary("Marvoto");
            // 如果没有抛出异常，则加载成功
            Log.i("LibraryLoader", "All libraries loaded successfully.");
        } catch (UnsatisfiedLinkError e) {
            // 加载失败
            Log.e("LibraryLoader", "Failed to load one or more libraries: " + e.getMessage());
        }

    }

    private static void PostEvent(int i, int i2, int i3) {
        Log.e("mavt", "cmd:" + i + "arg1:" + i2 + "arg2:" + i3);
    }

    public static USL getUslInstance() {
        if (usl == null) {
            usl = new USL();
        }
        return usl;
    }

//    public void loadLibrary(MarvotoMedicalAlgoManager.ExecuteResultInterface executeResultInterface) {
//        this.executeResultInterface = executeResultInterface;
//        new Thread(this).start();
//    }

    public static void destoryUSL() {
        usl = null;
    }

    @Override
    public void run() {
//        this.executeResultInterface.onExecuteResult(true, "loadLibrary");
    }

}
