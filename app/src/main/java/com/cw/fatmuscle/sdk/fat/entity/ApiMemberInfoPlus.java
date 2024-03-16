package com.cw.fatmuscle.sdk.fat.entity;

public class ApiMemberInfoPlus extends ApiMemberInfo{
    private String phoneNumber;
    private String remark;

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String str) {
        this.phoneNumber = str;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String str) {
        this.remark = str;
    }

}
