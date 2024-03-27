package com.mxsella.fatmuscle.sdk.fat.manager;

import android.util.Log;


import com.mxsella.fatmuscle.sdk.fat.entity.DeviceMsg;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class NoticeManager {

    private static Object obj = new Object();
    private static NoticeManager sNoticeManager;
    private List<MxsellaDeviceManager.DeviceInterface> deviceInterfaces = new ArrayList<>();
    MxsellaDeviceManager mMxsellaDeviceManager;

    public static NoticeManager getInstance() {
        if (sNoticeManager == null) {
            synchronized (obj) {
                if (sNoticeManager == null) {
                    sNoticeManager = new NoticeManager();
                }
            }
        }
        return sNoticeManager;
    }

    public NoticeManager() {
        EventBus.getDefault().register(this);
    }

    public void registerDeviceInterface(MxsellaDeviceManager.DeviceInterface deviceInterface) {
        this.deviceInterfaces.add(deviceInterface);
    }

    public void unregisterDeviceInterface(MxsellaDeviceManager.DeviceInterface deviceInterface) {
        this.deviceInterfaces.remove(deviceInterface);
    }

    public void addNotify() {
        EventBus.getDefault().register(this);
    }

    public void removeNotify() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventDevice(DeviceMsg deviceMsg) {
        int msgId = deviceMsg.getMsgId();
        if (msgId == -1 || msgId == 34952) {
            return;
        }
        for (MxsellaDeviceManager.DeviceInterface deviceInterface : this.deviceInterfaces) {
            Log.i("message => ", deviceMsg.toString());
            deviceInterface.onMessage(deviceMsg);
        }
    }

}
