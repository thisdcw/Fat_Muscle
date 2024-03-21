package com.mxsella.fatmuscle.comm.inter;

import com.mxsella.fatmuscle.sdk.fat.entity.DeviceMsg;

public interface MyDataListener {
    void onMessage(DeviceMsg deviceMsg);
}
