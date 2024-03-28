package com.mxsella.fatmuscle.comm.interfaces;


import com.mxsella.fatmuscle.entity.DeviceMsg;

public interface IDataTransfer {
    void onSendTimeOut(int i, DeviceMsg deviceMsg);

    void sendRestransmissionData(DeviceMsg deviceMsg);

}
