package com.mxsella.fatmuscle.sdk.fat.entity;

import com.mxsella.fatmuscle.utils.ByteUtil;

public class DeviceTcpMsg {
    private String content;
    private byte[] imageData;
    private int msgId;
    private int port;
    private String tcpConstant = "23E4";
    private int errcode = -1;

    public int getPort() {
        return this.port;
    }

    public void setPort(int i) {
        this.port = i;
    }

    public byte[] getImageData() {
        return this.imageData;
    }

    public void setImageData(byte[] bArr) {
        this.imageData = bArr;
    }

    public boolean isSuccess() {
        return this.errcode == 0;
    }

    public DeviceTcpMsg() {
    }

    public DeviceTcpMsg(int i, String str) {
        this.msgId = i;
        this.content = str;
    }

    public int getMsgId() {
        return this.msgId;
    }

    public void setMsgId(int i) {
        this.msgId = i;
    }

    public String toString() {
        return "msgID:" + this.msgId + "body:" + this.content + " isSuccess=" + isSuccess();
    }

    public byte[] getTcpProtocolBytes() {
        byte[] hexStringToByteArray = ByteUtil.hexStringToByteArray(this.tcpConstant + String.format("%04x", Integer.valueOf(this.msgId)) + String.format("%08x", 0));
        String str = this.content;
        if (str != null && !"".equalsIgnoreCase(str)) {
            try {
                byte[] bytes = this.content.getBytes("UTF-8");
                hexStringToByteArray = ByteUtil.hexStringToByteArray(this.tcpConstant + String.format("%04x", Integer.valueOf(this.msgId)) + String.format("%08x", Integer.valueOf(bytes.length)));
                byte[] bArr = new byte[hexStringToByteArray.length + bytes.length];
                System.arraycopy(hexStringToByteArray, 0, bArr, 0, hexStringToByteArray.length);
                System.arraycopy(bytes, 0, bArr, hexStringToByteArray.length, bytes.length);
                return bArr;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return hexStringToByteArray;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String str) {
        this.content = str;
    }

    public int getErrcode() {
        return this.errcode;
    }

    public void setErrcode(int i) {
        this.errcode = i;
    }

}
