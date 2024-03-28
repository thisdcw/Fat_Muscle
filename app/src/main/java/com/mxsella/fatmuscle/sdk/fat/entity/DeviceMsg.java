package com.mxsella.fatmuscle.sdk.fat.entity;

import android.util.Log;


import com.mxsella.fatmuscle.sdk.fat.inter.DataTransListerner;
import com.mxsella.fatmuscle.utils.ByteUtil;
import com.mxsella.fatmuscle.utils.LogUtil;

import org.opencv.videoio.Videoio;

import java.util.Arrays;

public class DeviceMsg {
    private static final String TAG = "MainActivity";
    private boolean isResult;
    private long lastSendTime;
    protected DataTransListerner.ProtocolType mProtocolType;
    protected int msgId;
    protected String head = "23E4";
    private int content = -1;
    private int reSendNum = 5;
    private int outTime = Videoio.CAP_QT;
    private int currentSendNum = 0;
    protected byte[] contentArray = new byte[1];
    private int error = 0;
    private boolean isAddToSendQueue = true;

    public void unpack(byte[] bArr) {
    }

    public DataTransListerner.ProtocolType getProtocolType() {
        return this.mProtocolType;
    }

    public void setProtocolType(DataTransListerner.ProtocolType protocolType) {
        this.mProtocolType = protocolType;
    }

    public int getError() {
        return this.error;
    }

    public void setError(int i) {
        this.error = i;
    }

    public boolean isResult() {
        return this.isResult;
    }

    public void setResult(boolean z) {
        this.isResult = z;
    }

    public int getCurrentSendNum() {
        return this.currentSendNum;
    }

    public void setCurrentSendNum(int i) {
        this.currentSendNum = i;
    }

    public int getReSendNum() {
        return this.reSendNum;
    }

    public void setReSendNum(int i) {
        this.reSendNum = i;
    }

    public long getLastSendTime() {
        return this.lastSendTime;
    }

    public void setLastSendTime(long j) {
        this.lastSendTime = j;
    }

    public int getOutTime() {
        return this.outTime;
    }

    public void setOutTime(int i) {
        this.outTime = i;
    }

    public String getHead() {
        return this.head;
    }

    public void setHead(String str) {
        this.head = str;
    }

    public int getMsgId() {
        return this.msgId;
    }

    public void setMsgId(int i) {
        this.msgId = i;
    }

    public int getContent() {
        return this.content;
    }

    public byte[] getContentArray() {
        return this.contentArray;
    }

    public void setContentArray(byte[] bArr) {
        this.contentArray = bArr;
    }

    public void setContent(int i) {
        LogUtil.i("setContent: " + i);
        this.content = i;
    }

    public boolean isAddToSendQueue() {
        return this.isAddToSendQueue;
    }

    public void setAddToSendQueue(boolean z) {
        this.isAddToSendQueue = z;
    }

    public byte[] getProtocolBytes() {
        byte[] hexStringToByteArray = ByteUtil.hexStringToByteArray(this.head + String.format("%04x", Integer.valueOf(this.msgId)) + String.format("%08x", 0));
        int i = this.content;
        if (i != -1) {
            try {
                byte[] intToByteArray = ByteUtil.intToByteArray(i);
                hexStringToByteArray = ByteUtil.hexStringToByteArray(this.head + String.format("%04x", Integer.valueOf(this.msgId)) + String.format("%08x", Integer.valueOf(intToByteArray.length)));
                byte[] bArr = new byte[hexStringToByteArray.length + intToByteArray.length + 3];
                System.arraycopy(hexStringToByteArray, 0, bArr, 0, hexStringToByteArray.length);
                System.arraycopy(intToByteArray, 0, bArr, hexStringToByteArray.length, intToByteArray.length);
                System.arraycopy(new byte[]{-2, -2, -2}, 0, bArr, hexStringToByteArray.length + intToByteArray.length, 3);
                return bArr;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (this.contentArray.length != 1) {
            byte[] hexStringToByteArray2 = ByteUtil.hexStringToByteArray(this.head + String.format("%04x", Integer.valueOf(this.msgId)) + String.format("%08x", Integer.valueOf(this.contentArray.length)));
            byte[] bArr2 = new byte[hexStringToByteArray2.length + this.contentArray.length + 3];
            System.arraycopy(hexStringToByteArray2, 0, bArr2, 0, hexStringToByteArray2.length);
            byte[] bArr3 = this.contentArray;
            System.arraycopy(bArr3, 0, bArr2, hexStringToByteArray2.length, bArr3.length);
            System.arraycopy(new byte[]{-2, -2, -2}, 0, bArr2, hexStringToByteArray2.length + this.contentArray.length, 3);
            return bArr2;
        }
        return hexStringToByteArray;
    }

    public String toString() {
        return "DeviceMsg{head='" + this.head + "', msgId=" + this.msgId + ", content=" + this.content + ", reSendNum=" + this.reSendNum + ", lastSendTime=" + this.lastSendTime + ", outTime=" + this.outTime + ", currentSendNum=" + this.currentSendNum + ", contentArray=" + Arrays.toString(this.contentArray) + ", error=" + this.error + ", mProtocolType=" + this.mProtocolType + ", isResult=" + this.isResult + ", isAddToSendQueue=" + this.isAddToSendQueue + '}';
    }

}
