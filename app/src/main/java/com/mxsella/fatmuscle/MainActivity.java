package com.mxsella.fatmuscle;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.mxsella.fat_muscle.R;
import com.mxsella.fat_muscle.databinding.ActivityMainBinding;
import com.mxsella.fatmuscle.common.MyApplication;
import com.mxsella.fatmuscle.common.base.BaseActivity;
import com.mxsella.fatmuscle.sdk.fat.manager.FatConfigManager;
import com.mxsella.fatmuscle.ui.activity.FatMeasurePlusActivity;
import com.mxsella.fatmuscle.ui.activity.HistoryListActivity;
import com.mxsella.fatmuscle.ui.activity.MuscleMeasureResultActivity;
import com.mxsella.fatmuscle.view.dialog.CustomDialog;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity {

    ActivityMainBinding mainBinding;

    CustomDialog switchPartDialog;
    private int curPositionIndex = 11;
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

    @Override
    protected void initView() {
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        requestPermission();
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