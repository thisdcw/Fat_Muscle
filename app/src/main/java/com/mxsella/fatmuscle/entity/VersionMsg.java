package com.mxsella.fatmuscle.entity;


public class VersionMsg extends DeviceMsg{
    private int day;
    private int hour;
    private int identificationCode;
    private int min;
    private int mode;
    private int month;
    private int versionNumber;
    private int year;

    public int getMode() {
        return this.mode;
    }

    public void setMode(int i) {
        this.mode = i;
    }

    public int getIdentificationCode() {
        return this.identificationCode;
    }

    public void setIdentificationCode(int i) {
        this.identificationCode = i;
    }

    public int getVersionNumber() {
        return this.versionNumber;
    }

    public void setVersionNumber(int i) {
        this.versionNumber = i;
    }

    public int getMonth() {
        return this.month;
    }

    public void setMonth(int i) {
        this.month = i;
    }

    public int getDay() {
        return this.day;
    }

    public void setDay(int i) {
        this.day = i;
    }

    public int getHour() {
        return this.hour;
    }

    public void setHour(int i) {
        this.hour = i;
    }

    public int getMin() {
        return this.min;
    }

    public void setMin(int i) {
        this.min = i;
    }

    public int getYear() {
        return this.year;
    }

    @Override // com.marvoto.fat.entity.DeviceMsg
    public void unpack(byte[] bArr) {
        super.unpack(bArr);
        if (bArr == null || bArr.length < 10) {
            return;
        }
        this.mode = bArr[0] & 255;
        this.identificationCode = bArr[1] & 255;
        this.versionNumber = bArr[2] & 255;
        this.year = bArr[5] & 255;
        this.month = bArr[6] & 255;
        this.day = bArr[7] & 255;
        this.hour = bArr[8] & 255;
        this.min = bArr[9] & 255;
    }

    @Override // com.marvoto.fat.entity.DeviceMsg
    public String toString() {
        return "VersionMsg{mode=" + this.mode + ", identificationCode=" + this.identificationCode + ", versionNumber=" + this.versionNumber + ", year=" + this.year + ", month=" + this.month + ", day=" + this.day + ", hour=" + this.hour + ", min=" + this.min + ", msgId=" + this.msgId + '}';
    }

}
