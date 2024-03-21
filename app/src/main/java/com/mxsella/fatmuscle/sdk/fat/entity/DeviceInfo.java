package com.mxsella.fatmuscle.sdk.fat.entity;

public class DeviceInfo {
    private String ip;
    private String mac;

    /* renamed from: sn */
    private String sn;

    public DeviceInfo(String str, String str2, String str3) {
        this.ip = str;
        this.mac = str2;
        this.sn = str3;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String str) {
        this.ip = str;
    }

    public String getMac() {
        return this.mac;
    }

    public void setMac(String str) {
        this.mac = str;
    }

    public String getSn() {
        return this.sn;
    }

    public void setSn(String str) {
        this.sn = str;
    }

}
