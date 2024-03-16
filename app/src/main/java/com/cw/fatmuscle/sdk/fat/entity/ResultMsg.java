package com.cw.fatmuscle.sdk.fat.entity;

public class ResultMsg extends DeviceMsg{
    private ResultMode resultCode = ResultMode.OK;
    private boolean isAllow = true;

    /* loaded from: classes.dex */
    public enum ResultMode {
        OK,
        FAIL
    }

    public boolean isAllow() {
        return this.isAllow;
    }

    @Override // com.marvoto.fat.entity.DeviceMsg
    public String toString() {
        return super.toString() + " ResultMsg{resultCode=" + this.resultCode + ", isAllow=" + this.isAllow + '}';
    }

    public ResultMode getResultCode() {
        return this.resultCode;
    }

    @Override // com.marvoto.fat.entity.DeviceMsg
    public void unpack(byte[] bArr) {
        super.unpack(bArr);
        if (bArr != null && bArr.length == 4) {
            int i = bArr[0] & 255;
            if (i == 1) {
                this.resultCode = ResultMode.OK;
            } else if (i == 9) {
                this.resultCode = ResultMode.FAIL;
            } else if (i == 2) {
                this.resultCode = ResultMode.OK;
            } else if (i == 10) {
                this.resultCode = ResultMode.FAIL;
            }
            this.isAllow = (bArr[1] & 255) == 1;
        } else if (bArr != null && bArr.length == 1) {
            if ((bArr[0] & 255) == 1) {
                this.resultCode = ResultMode.OK;
            } else {
                this.resultCode = ResultMode.FAIL;
            }
        }
        if (this.resultCode == ResultMode.OK) {
            setError(0);
        } else {
            setError(-1);
        }
    }

}
