package com.mxsella.fatmuscle.sdk.fat.entity;

import com.mxsella.fatmuscle.utils.ByteUtil;
import com.mxsella.fatmuscle.utils.LogUtil;

public class FlashMsg extends DeviceMsg {
    private String mfid;
    private String uuid;

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String str) {
        this.uuid = str;
    }

    public String getMfid() {
        return this.mfid;
    }

    public void setMfid(String str) {
        this.mfid = str;
    }

    @Override
    public void unpack(byte[] bArr) {
        LogUtil.d("flash解包");
        String bytesToHex;
        if (bArr == null || (bytesToHex = String.valueOf(ByteUtil.bytesToHex(bArr))) == null || bytesToHex.length() <= 10) {
            return;
        }
        this.mfid = bytesToHex.substring(0, 4);
        this.uuid = bytesToHex.substring(4, bytesToHex.length());
    }

    @Override // com.marvoto.fat.entity.DeviceMsg
    public String toString() {
        return "FlashMsg{uuid='" + this.uuid + "', mfid='" + this.mfid + "'}";
    }

}
