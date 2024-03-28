package com.mxsella.fatmuscle.entity;

import android.util.Log;

import com.mxsella.fatmuscle.manager.MxsellaDeviceManager;
import com.mxsella.fatmuscle.utils.ByteUtil;

public class FirmwareUpdateMsg extends DeviceMsg {
    private static final String TAG = "FirmwareUpdateMsg";
    public static final byte sFIRMWAR_END = 1;
    public static final byte sFIRMWAR_FAIL = 3;
    public static final byte sFIRMWAR_RUN = 0;
    public static final byte sFIRMWAR_START = 16;
    public static final byte sFIRMWAR_SUCCESS = 2;
    private boolean isAllow = true;
    private byte firmwareFlag = 0;
    private int flashAdr = 0;
    private int percent = 0;

    public boolean isAllow() {
        return this.isAllow;
    }

    public int getPercent() {
        return this.percent;
    }

    public void setPercent(int i) {
        this.percent = i;
    }

    @Override
    public String toString() {
        return "FirmwareUpdateMsg{isAllow=" + this.isAllow + ", firmwareFlag=" + ((int) this.firmwareFlag) + ", flashAdr=" + this.flashAdr + ", percent=" + this.percent + ", msgId=" + this.msgId + ", mProtocolType=" + this.mProtocolType + ", error=" + getError() + '}';
    }

    @Override
    public void unpack(byte[] bArr) {
        super.unpack(bArr);
        if (bArr == null || bArr.length == 0) {
            return;
        }
        this.percent = bArr[0];
        if (bArr.length == 4) {
            int i = bArr[0] & 255;
            if (i == 1) {
                setError(0);
            } else if (i == 9) {
                setError(-1);
            } else if (i == 2) {
                setError(0);
            } else if (i == 10) {
                setError(-1);
            } else if (i == 0) {
                setError(0);
            }
            this.isAllow = (bArr[1] & 255) == 1;
        }
    }

    @Override
    public byte[] getProtocolBytes() {
        String str = this.head + String.format("%04x", Integer.valueOf(this.msgId)) + String.format("%08x", 0);
        byte[] bArr = new byte[7];
        if (this.contentArray.length != 1) {
            Log.i(TAG, "getProtocolBytes: ");
            byte[] bArr2 = {-2, -2, -2};
            byte[] hexStringToByteArray;
            if (MxsellaDeviceManager.getInstance().getDeviceVersion() > 20) {
                hexStringToByteArray = ByteUtil.hexStringToByteArray(this.head + String.format("%04x", Integer.valueOf(this.msgId)) + String.format("%08x", Integer.valueOf(this.contentArray.length + 4)) + String.format("%02x", Byte.valueOf(this.firmwareFlag)) + String.format("%06x", Integer.valueOf(this.flashAdr)));
            } else {
                hexStringToByteArray = ByteUtil.hexStringToByteArray(this.head + String.format("%04x", Integer.valueOf(this.msgId)) + String.format("%04x", Integer.valueOf(this.contentArray.length)) + String.format("%02x", Byte.valueOf(this.firmwareFlag)));
            }
            //版本小于20
            byte[] bArr3 = new byte[hexStringToByteArray.length + this.contentArray.length + 3];
            System.arraycopy(hexStringToByteArray, 0, bArr3, 0, hexStringToByteArray.length);
            System.arraycopy(this.contentArray, 0, bArr3, hexStringToByteArray.length, this.contentArray.length);
            System.arraycopy(bArr2, 0, bArr3, hexStringToByteArray.length + this.contentArray.length, 3);
            return bArr3;
        }
        return bArr;
    }

    public byte getFirmwareFlag() {
        return this.firmwareFlag;
    }

    public void setFlashAdr(int i) {
        this.flashAdr = i;
    }

    public void setFirmwareFlag(byte b) {
        this.firmwareFlag = b;
    }

}
