package com.mxsella.fatmuscle.common;

import android.app.Application;

import com.mxsella.fatmuscle.db.AppDatabase;
import com.mxsella.fatmuscle.manager.FatConfigManager;
import com.mxsella.fatmuscle.manager.MxsellaDeviceManager;
import com.mxsella.fatmuscle.sdk.USL;
import com.mxsella.fatmuscle.utils.LogUtil;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

public class MyApplication extends Application {
    private static MyApplication instance;
    private static final String TAG = "MyApplication";
    public AppDatabase db;
    BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                LogUtil.d("OpenCV loaded successfully");
            } else {
                LogUtil.e("OpenCV failed to load. Error code: " + status);
                super.onManagerConnected(status);
            }
        }
    };

    private void initDir() {
        MxsellaConstant.APP_DIR_PATH = getExternalCacheDir().getPath();
        MxsellaConstant.CACH_PHONE_MIDIR = MxsellaConstant.APP_DIR_PATH + "/icon";
        MxsellaConstant.ICON = MxsellaConstant.CACH_PHONE_MIDIR + "/temp.jpeg";
        MxsellaConstant.ICON_TEMP = MxsellaConstant.CACH_PHONE_MIDIR + "/temp01.jpeg";
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        FatConfigManager.getInstance().init(instance);
        initOpenCV();
        initDir();
        USL usl = USL.getUslInstance();
        usl.run();
        db = AppDatabase.getInstance(instance);
    }

    private void initOpenCV() {
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //取消Eventbus订阅
//        MxsellaDeviceManager.getInstance().unregisterEventbus();
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
