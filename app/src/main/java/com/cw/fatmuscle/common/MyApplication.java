package com.cw.fatmuscle.common;

import android.app.Application;

import com.cw.fatmuscle.sdk.fat.manager.FatConfigManager;
import com.cw.fatmuscle.sdk.sproutcameramedical.USL;
import com.cw.fatmuscle.utils.LogUtil;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

public class MyApplication extends Application {
    private static MyApplication instance;
    private static final String TAG = "MyApplication";
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


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        FatConfigManager.getInstance().init(instance);
        initOpenCV();
        USL usl = USL.getUslInstance();
    }

    private void initOpenCV() {
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
