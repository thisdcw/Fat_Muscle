package com.mxsella.fatmuscle.sdk.fat.entity;

import com.google.gson.Gson;

public class DeviceUdpMsg {
    private String cmd;
    private String content;
    private Integer errorCode;

    public Integer getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(Integer num) {
        this.errorCode = num;
    }

    public String getCmd() {
        return this.cmd;
    }

    public void setCmd(String str) {
        this.cmd = str;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String str) {
        this.content = str;
    }

    public byte[] getUdpProtocolBytes() {
        return new Gson().toJson(this).getBytes();
    }

    public String toString() {
        return "DeviceUdpMsg{cmd='" + this.cmd + "', content='" + this.content + "'}";
    }

}
