package com.mxsella.fatmuscle.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

public class PermissionUtils {

    private static PermissionUtils mInstance;

    public static final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String CAMERA = Manifest.permission.CAMERA;

    public static final int REQUEST_STORAGE_CODE = 1001;
    public static final int REQUEST_CAMERA_CODE = 1002;
    public static final int REQUEST_MANAGE_EXTERNAL_STORAGE_CODE = 1000;

    public static synchronized PermissionUtils getInstance() {
        if (mInstance == null) {
            mInstance = new PermissionUtils();
        }
        return mInstance;
    }

    /**
     * 检查是否拥有某权限
     *
     * @param activity   Activity实例
     * @param permission 权限名称
     * @return true 有，false 没有
     */
    public boolean hasPermission(Activity activity, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    /**
     * 通过权限名称获取请求码
     *
     * @param permissionName 权限名称
     * @return requestCode 权限请求码
     */
    private int getPermissionRequestCode(String permissionName) {
        switch (permissionName) {
            case READ_EXTERNAL_STORAGE:
            case WRITE_EXTERNAL_STORAGE:
                return REQUEST_STORAGE_CODE;
            case CAMERA:
                return REQUEST_CAMERA_CODE;
            default:
                return 1000;
        }
    }

    /**
     * 请求权限
     *
     * @param activity   Activity实例
     * @param permission 权限名称
     */
    public void requestPermission(Activity activity, String permission) {
        int requestCode = getPermissionRequestCode(permission);
        // 请求此权限
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
    }

}
