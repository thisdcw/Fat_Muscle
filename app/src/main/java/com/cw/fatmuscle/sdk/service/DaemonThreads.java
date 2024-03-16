package com.cw.fatmuscle.sdk.service;

import android.util.Log;

import com.cw.fatmuscle.sdk.common.MxsellaConstant;
import com.cw.fatmuscle.sdk.manager.MxsellaDeviceManager;
import com.cw.fatmuscle.utils.ByteUtil;

public class DaemonThreads extends Thread {
    private boolean isRunning = true;
    private long curRunningTime = System.currentTimeMillis();

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        try {
            Thread.sleep(3000L);
            int i = 0;
            while (this.isRunning) {
                if (!MxsellaDeviceManager.getInstance().isConnected()) {
                    Log.i("Daemon", "==已经断开，正在重连中....isRunning=" + this.isRunning);
                    if (!ByteUtil.pingIpAddress(MxsellaDeviceManager.getInstance().getDeviceIp())) {
                        if (ByteUtil.pingIpAddress(MxsellaConstant.DEVICE_IP)) {
                        }
                        i = 0;
                    }
                    i++;
                    if (i > 1 && this.isRunning) {
                        MxsellaDeviceManager.getInstance().connectDevice();
                        Thread.sleep(3000L);
                        i = 0;
                    }
                }
                Thread.sleep(2000L);
                this.curRunningTime = System.currentTimeMillis();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            this.isRunning = false;
        }
    }

    public void destory() {
        this.isRunning = false;
        interrupt();
    }

    public boolean isRunning() {
        return this.isRunning && System.currentTimeMillis() - this.curRunningTime <= 4000;
    }

}
