package com.cw.fatmuscle.sdk.fat.interfaces;


import com.cw.fatmuscle.sdk.fat.entity.DeviceMsg;

public interface IDataTransfer {
    void onSendTimeOut(int i, DeviceMsg deviceMsg);

    void sendRestransmissionData(DeviceMsg deviceMsg);

}
