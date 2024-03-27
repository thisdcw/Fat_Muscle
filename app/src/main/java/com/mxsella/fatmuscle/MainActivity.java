package com.mxsella.fatmuscle;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.ftdi.j2xx.D2xxManager;
import com.mxsella.fat_muscle.R;
import com.mxsella.fat_muscle.databinding.ActivityMainBinding;
import com.mxsella.fatmuscle.common.MyApplication;
import com.mxsella.fatmuscle.common.base.BaseActivity;
import com.mxsella.fatmuscle.sdk.fat.MeasureDepth;
import com.mxsella.fatmuscle.sdk.fat.entity.ResultMsg;
import com.mxsella.fatmuscle.sdk.fat.interfaces.DeviceResultInterface;
import com.mxsella.fatmuscle.sdk.fat.manager.FatConfigManager;
import com.mxsella.fatmuscle.sdk.fat.manager.MxsellaDeviceManager;
import com.mxsella.fatmuscle.ui.activity.FatMeasurePlusActivity;
import com.mxsella.fatmuscle.ui.activity.HistoryListActivity;
import com.mxsella.fatmuscle.ui.activity.MuscleMeasureResultActivity;
import com.mxsella.fatmuscle.utils.LogUtil;
import com.mxsella.fatmuscle.utils.PermissionUtils;
import com.mxsella.fatmuscle.view.dialog.CustomDialog;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity {

    ActivityMainBinding mainBinding;

    private int curPositionIndex = 11;

    public static UsbDevice usbDevice;
    /**
     * 0: 腰部
     * 1: 脸皮
     * 2: 上臂,肱二头肌
     * 3: 大腿,股直肌
     * 4: 胸部
     * 5: 小腿,腓肠肌
     * 6: 腹部,腹直肌
     * 11: 通用
     * 12: 通用2
     */
    private final int[] fat = {0, 2, 3, 5, 11};
    private final int[] muscle = {2, 6, 12};
    private final String[] str = {"腰部", "脸皮", "上臂,肱二头肌", "大腿,股直肌", "胸部", "小腿,腓肠肌", "腹部,腹直肌", "", "", "", "", "脂肪通用", "肌肉通用"};
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            // 在此处执行 USB 设备已授权的操作
                            LogUtil.d("USB权限已经授予");
                        }
                    } else {
                        // 在此处执行 USB 设备未授权的操作
                        LogUtil.d("USB权限未被授予");
                    }
                }
            }
        }
    };

    // 请求 USB 权限
    private void requestUsbPermission() {
        boolean b = PermissionUtils.getInstance().hasPermission(this, ACTION_USB_PERMISSION);
        if (b) {
            LogUtil.d("已经有权限");
            return;
        }
        LogUtil.d("没有权限");
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        // 获取连接的 USB 设备列表
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        for (UsbDevice device : deviceList.values()) {
            // 检查每个 USB 设备是否需要权限
            if (usbManager.hasPermission(device)) {
                // USB 设备已经有权限
                // 在此处执行 USB 设备已授权的操作
                LogUtil.d("已有权限的设备: " + device.toString());
                usbDevice = device;
                PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                usbManager.requestPermission(device, permissionIntent);
            } else {
                // USB 设备没有权限，请求权限
                LogUtil.d("没有有权限的设备: ");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initView() {
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        requestPermission();
        requestUsbPermission();
        try {
            ftD2xx = D2xxManager.getInstance(this);
        } catch (D2xxManager.D2xxException e) {
            e.printStackTrace();
        }
        if (!ftD2xx.setVIDPID(1027, 44449)) {
            Log.i("ftd2xx-java", "setVIDPID Error");
        }
//        MeasureDepth.ParaSet(3.0f,3.0f);
        mainBinding.selectPart.setText("选择的是" + str[curPositionIndex]);
        mainBinding.fat.setOnClickListener(v -> {
            boolean existsInFat = Arrays.stream(fat).anyMatch(value -> value == curPositionIndex);
            if (existsInFat) {
                navToNoFinish(FatMeasurePlusActivity.class);
            }
        });
        mainBinding.muscle.setOnClickListener(v -> {
            boolean existsInFat = Arrays.stream(muscle).anyMatch(value -> value == curPositionIndex);
            if (existsInFat) {
                navToNoFinish(MuscleMeasureResultActivity.class);
            }
        });
        mainBinding.record.setOnClickListener(v -> {
            navToNoFinish(HistoryListActivity.class);
        });
        mainBinding.fuzhiji.setOnClickListener(v -> {
            Log.d("mian", "fuzhiji");
            setIndex(6);
        });
        mainBinding.gongertouji.setOnClickListener(v -> {
            Log.d("mian", "gongertouji");

            setIndex(2);
        });
        mainBinding.shangbi.setOnClickListener(v -> {
            Log.d("mian", "shangbi");

            setIndex(2);
        });
        mainBinding.datui.setOnClickListener(v -> {
            Log.d("mian", "datui");

            setIndex(3);
        });
        mainBinding.xiaotui.setOnClickListener(v -> {
            Log.d("mian", "xiaotui");

            setIndex(5);
        });

        mainBinding.yaobu.setOnClickListener(v -> {
            Log.d("mian", "yaobu");

            setIndex(0);
        });

        mainBinding.tongyong1.setOnClickListener(v -> {
            Log.d("mian", "tongyong1");

            setIndex(11);
        });
        mainBinding.tongyong2.setOnClickListener(v -> {
            Log.d("mian", "tongyong2");
            setIndex(12);
        });
//        mainBinding.test.setOnClickListener(v -> {
//            navToNoFinish(OpenCvTest.class);
//        });
    }

    void setIndex(int index) {
        this.curPositionIndex = index;
        FatConfigManager.getInstance().setCurBodyPositionIndex(index);
        mainBinding.selectPart.setText("选择的是" + str[curPositionIndex]);
    }


    public void requestPermission() {
        XXPermissions.with(this)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (!allGranted) {
                            Log.i("main", "获取部分权限成功，但部分权限未正常授予");
                            return;
                        }
                        Log.i("main", "获取文件管理权限成功");
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        if (doNotAskAgain) {
                            Log.i("main", "被永久拒绝授权，请手动授予文件管理权限");
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(MyApplication.getInstance(), permissions);
                        } else {
                            Log.i("main", "获取文件管理权限失败");
                        }
                    }
                });
    }


}