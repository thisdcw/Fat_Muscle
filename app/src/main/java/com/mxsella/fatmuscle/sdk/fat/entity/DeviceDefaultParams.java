package com.mxsella.fatmuscle.sdk.fat.entity;

import java.util.List;

public class DeviceDefaultParams {
    private int[] defaultBodyPositionArray;
    private Integer depth;
    private Integer dynamic;
    private Integer gain;
    private List<Integer> gainArray;
    private Integer leve;
    private Integer sendcycle;

    public List<Integer> getGainArray() {
        return this.gainArray;
    }

    public void setGainArray(List<Integer> list) {
        this.gainArray = list;
    }

    public int[] getDefaultBodyPositionArray() {
        return this.defaultBodyPositionArray;
    }

    public void setDefaultBodyPositionArray(int[] iArr) {
        this.defaultBodyPositionArray = iArr;
    }

    public Integer getLeve() {
        return this.leve;
    }

    public void setLeve(Integer num) {
        this.leve = num;
    }

    public Integer getDepth() {
        return this.depth;
    }

    public void setDepth(Integer num) {
        this.depth = num;
    }

    public Integer getGain() {
        return this.gain;
    }

    public void setGain(Integer num) {
        this.gain = num;
    }

    public Integer getSendcycle() {
        return this.sendcycle;
    }

    public void setSendcycle(Integer num) {
        this.sendcycle = num;
    }

    public Integer getDynamic() {
        return this.dynamic;
    }

    public void setDynamic(Integer num) {
        this.dynamic = num;
    }

}
