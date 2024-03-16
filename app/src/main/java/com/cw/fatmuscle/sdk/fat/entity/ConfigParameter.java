package com.cw.fatmuscle.sdk.fat.entity;

import java.util.List;

public class ConfigParameter {
    private int dataLength;
    private List<DeviceDefaultParams> deviceDefaultParamsList;

    public List<DeviceDefaultParams> getDeviceDefaultParamsList() {
        return this.deviceDefaultParamsList;
    }

    public void setDeviceDefaultParamsList(List<DeviceDefaultParams> list) {
        this.deviceDefaultParamsList = list;
    }

    public int getDataLength() {
        return this.dataLength;
    }

    public void setDataLength(int i) {
        this.dataLength = i;
    }

}
