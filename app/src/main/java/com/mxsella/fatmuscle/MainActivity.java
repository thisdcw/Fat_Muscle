package com.mxsella.fatmuscle;

import android.app.KeyguardManager;
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
import com.mxsella.fatmuscle.sdk.common.Constant;
import com.mxsella.fatmuscle.sdk.common.MxsellaConstant;
import com.mxsella.fatmuscle.sdk.fat.MeasureDepth;
import com.mxsella.fatmuscle.sdk.fat.entity.BitmapMsg;
import com.mxsella.fatmuscle.sdk.fat.entity.DeviceMsg;
import com.mxsella.fatmuscle.sdk.fat.entity.ResultMsg;
import com.mxsella.fatmuscle.sdk.fat.interfaces.DeviceResultInterface;
import com.mxsella.fatmuscle.sdk.fat.manager.FatConfigManager;
import com.mxsella.fatmuscle.sdk.fat.manager.MxsellaDeviceManager;
import com.mxsella.fatmuscle.sdk.fat.manager.OTGManager;
import com.mxsella.fatmuscle.ui.activity.FatMeasurePlusActivity;
import com.mxsella.fatmuscle.ui.activity.HistoryListActivity;
import com.mxsella.fatmuscle.ui.activity.MuscleMeasureResultActivity;
import com.mxsella.fatmuscle.utils.LogUtil;
import com.mxsella.fatmuscle.utils.PermissionUtils;
import com.mxsella.fatmuscle.view.dialog.CustomDialog;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements MxsellaDeviceManager.DeviceInterface{
    private int curPositionIndex = 0;

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
    private MxsellaDeviceManager manager;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initView() {
        requestPermission();

        MxsellaDeviceManager.getInstance().registerDeviceInterface(this);
        manager = MxsellaDeviceManager.getInstance();
        binding.selectPart.setText("选择的是" + str[curPositionIndex]);
        binding.fat.setOnClickListener(v -> {
            boolean existsInFat = Arrays.stream(fat).anyMatch(value -> value == curPositionIndex);
            if (existsInFat) {
                navToNoFinish(FatMeasurePlusActivity.class);
            }
        });
        binding.muscle.setOnClickListener(v -> {
            boolean existsInFat = Arrays.stream(muscle).anyMatch(value -> value == curPositionIndex);
            if (existsInFat) {
                navToNoFinish(MuscleMeasureResultActivity.class);
            }
        });
        binding.record.setOnClickListener(v -> {
            navToNoFinish(HistoryListActivity.class);
        });
        binding.fuzhiji.setOnClickListener(v -> {
            Log.d("mian", "fuzhiji");
            setIndex(6);
        });
        binding.gongertouji.setOnClickListener(v -> {
            Log.d("mian", "gongertouji");

            setIndex(2);
        });
        binding.shangbi.setOnClickListener(v -> {
            Log.d("mian", "shangbi");

            setIndex(2);
        });
        binding.datui.setOnClickListener(v -> {
            Log.d("mian", "datui");

            setIndex(3);
        });
        binding.xiaotui.setOnClickListener(v -> {
            Log.d("mian", "xiaotui");

            setIndex(5);
        });

        binding.yaobu.setOnClickListener(v -> {
            Log.d("mian", "yaobu");

            setIndex(0);
        });

        binding.tongyong1.setOnClickListener(v -> {
            Log.d("mian", "tongyong1");
            setIndex(11);
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    void setIndex(int index) {
        this.curPositionIndex = index;
        FatConfigManager.getInstance().setCurBodyPositionIndex(index);
        binding.selectPart.setText("选择的是" + str[curPositionIndex]);
    }

    public void init(View view){
        byte[] b1 = hexToByteArray("23E410B00000000400000000FEFEFE");
        byte[] b2 = hexToByteArray("23E410C80000000400000000FEFEFE");
        byte[] b3 = hexToByteArray("23E410C6000000800001020304050607090A0B0C0D0E0F1011121314151618191A1B1C1D1E1F20212324252627282A2B2C2D2F30313234353637373839393A3A3B3C3C3D3E3E3F3F404041424243444545464647474849494A4B4C4C4D4E4E4F50505050505050505050505050505050505050505050505050505050505050505050505050505050FEFEFE");
        byte[] b4 = hexToByteArray("23E410C00000000400000003FEFEFE");
        byte[] b5 = hexToByteArray("23E410C1000000040000001EFEFEFE");
        byte[] b6 = hexToByteArray("23E410C50000000400640C80FEFEFE");
        byte[] b7 = hexToByteArray("23E410C300000004FFC1017FFEFEFE");
        byte[] b8 = hexToByteArray("23E410B10000000400000000FEFEFE");
        byte[] b9 = hexToByteArray("23E410C80000000400000001FEFEFE");
        ArrayList<byte[]> arr = new ArrayList<>();
        arr.add(b1);
        arr.add(b2);
        arr.add(b3);
        arr.add(b4);
        arr.add(b5);
        arr.add(b6);
        arr.add(b7);
        arr.add(b8);
        arr.add(b9);

        for (int i = 0; i < arr.size(); i++) {
            manager.sendDatas(arr.get(i));
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static byte[] hexToByteArray(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            //奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            //偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
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


    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected(int i, String str) {

    }

    @Override
    public void onMessage(DeviceMsg deviceMsg) {
        int msgId = deviceMsg.getMsgId();
        if (msgId != 4261) {
            if (msgId == 4272 || msgId == 4512) {
                checkFimwareUpdate();
                if (!FatConfigManager.getInstance().isFatMeasureMode() && !MxsellaDeviceManager.getInstance().isToBusinessVersion() && !MxsellaConstant.isProduct) {
                    FatConfigManager.getInstance().setMeasureMode(1);
                }
                if (FatConfigManager.getInstance().getCurBodyPositionIndex() != 3 || MxsellaDeviceManager.getInstance().isToBusinessVersion()) {
                    return;
                }
                FatConfigManager.getInstance().setCurBodyPositionIndex(0);
                return;
            }
            return;
        }
        BitmapMsg bitmapMsg = (BitmapMsg) deviceMsg;
        if (((KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE)).inKeyguardRestrictedInputMode() || bitmapMsg.getState() != BitmapMsg.State.START) {
            return;
        }
        if (FatConfigManager.getInstance().isFatMeasureMode()) {
//            if (HookManager.getInstance().isLoadPlugin()) {
//                startProxy();
//                return;
//            }
//            this.mContext.startActivity(new Intent(this.mContext, FatMeasurePlusActivity.class));
//            overridePendingTransition(17432576, 17432577);
            return;
        }
//        this.mContext.startActivity(new Intent(this.mContext, MuscleMeasureResultActivity.class));
//        overridePendingTransition(17432576, 17432577);

    }
    private void checkFimwareUpdate() {

    }

}