package com.mxsella.fatmuscle.common.base;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.mxsella.fatmuscle.utils.PermissionUtils;
import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.ftdi.j2xx.FT_EEPROM;
import com.ftdi.j2xx.FT_EEPROM_232H;

public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity {

    public Context mContext;

    public T binding;

    public D2xxManager ftD2xx;

    FT_Device ftDevice = null;


    protected abstract void initView();

    public abstract int getLayoutId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        ActivityStackManager.getInstance().addActivity(this);
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        initGetData();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ftD2xx.createDeviceInfoList(this);
    }

    public void initGetData() {

        try {
            ftD2xx = D2xxManager.getInstance(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!ftD2xx.setVIDPID(1027, 44449)) {
            Log.i("ftd2xx-java", "setVIDPID Error");
        } else {
            Log.i("ftd2xx-java", "setVIDPID Success");
        }
    }

    /**
     * 视图onclick触发事件
     *
     * @param view
     */
    public void onFifoBtn(View view) {
        startEEpromWrite();
    }

    /**
     * 启动 EEPROM 写入操作
     */
    public void startEEpromWrite() {
        if (ftD2xx == null) {
            try {
                ftD2xx = D2xxManager.getInstance(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ftD2xx == null) {
                show("设备未获取到,请重新打开!");
                return;
            }
        }
        if (ftD2xx.createDeviceInfoList(this) < 0) {
            show("未发现设备节点");
            return;
        }
        FT_Device openByIndex = ftD2xx.openByIndex(this, 0);
        this.ftDevice = openByIndex;
        FT_EEPROM eepromRead = openByIndex.eepromRead();
        if (eepromRead == null) {
            show("not support device");
        } else {
            FT_EEPROM_232H ft_eeprom_232H = (FT_EEPROM_232H) eepromRead;
            ft_eeprom_232H.FIFO = true;
            ft_eeprom_232H.UART = false;
            this.ftDevice.eepromWrite(ft_eeprom_232H);
        }
        boolean resetDevice = this.ftDevice.resetDevice();
        Log.i("Main", "StartEEpromWrite: " + resetDevice);
        this.ftDevice.close();
        if (resetDevice) {
            show("切换成功");
        } else {
            show("切换失败");
        }
    }

    public void show(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStackManager.getInstance().removeActivity(this);
        mContext = null;
        binding =null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityStackManager.getInstance().removeActivity(this);
    }

    public Context getmContext() {
        return mContext;
    }

    protected void showMsg(String msg) {
        if (mContext != null) {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public void navTo(Class<?> cls) {
        ActivityStackManager.getInstance().startActivity(this, cls);
    }

    public void navToFinishAll(Class<?> cls) {
        ActivityStackManager.getInstance().finishAllAndStart(this, cls);
    }

    public void navToNoFinish(Class<?> cls) {
        ActivityStackManager.getInstance().startActivityNoFinish(this, cls);
    }

    public void navToWithParam(Class<?> cls, Bundle bundle) {
        ActivityStackManager.getInstance().startActivity(this, cls, bundle);
    }

    /**
     * 当前是否在Android11.0及以上
     */
    protected boolean isAndroid11() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R;
    }

    /**
     * 当前是否在Android10.0及以上
     */
    protected boolean isAndroid10() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    /**
     * 当前是否在Android7.0及以上
     */
    protected boolean isAndroid7() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    protected boolean isStorageManager() {
        return Environment.isExternalStorageManager();
    }

    protected void requestPermission(String permissionName) {
        PermissionUtils.getInstance().requestPermission(this, permissionName);
    }

    /**
     * 当前是否在Android6.0及以上
     */
    protected boolean isAndroid6() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    protected boolean hasPermission(String permissionName) {
        return PermissionUtils.getInstance().hasPermission(this, permissionName);
    }

}
