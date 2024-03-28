package com.mxsella.fatmuscle.entity;

public class PointArrayInfo {
    int[] binArray = new int[300];
    int[] bottomBinArray = new int[300];

    public int[] getBinArray() {
        return this.binArray;
    }

    public void setBinArray(int[] iArr) {
        this.binArray = iArr;
    }

    public int[] getBottomBinArray() {
        return this.bottomBinArray;
    }

    public void setBottomBinArray(int[] iArr) {
        this.bottomBinArray = iArr;
    }
}
