package com.mxsella.fatmuscle.comm.interfaces;

import com.mxsella.fatmuscle.entity.DeviceMsg;

public interface MyDataListener {
    void onMessage(DeviceMsg deviceMsg);
}
