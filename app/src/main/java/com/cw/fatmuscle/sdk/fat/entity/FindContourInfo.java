package com.cw.fatmuscle.sdk.fat.entity;

import org.opencv.core.Point;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindContourInfo {
    private int endHeight;
    private int height;
    private int index;
    private int maxValue;
    private int maxX;
    private int startHeight;

    /* renamed from: y */
    private int f3768y;
    private int findCount = 0;
    private PointArrayInfo pointArrayInfo = null;
    private int[] resultArray = null;
    private Map<Integer, List<Point>> pointList = new HashMap();

    public int getMaxX() {
        return this.maxX;
    }

    public void setMaxX(int i) {
        this.maxX = i;
    }

    public int getMaxValue() {
        return this.maxValue;
    }

    public void setMaxValue(int i) {
        this.maxValue = i;
    }

    public int getStartHeight() {
        return this.startHeight;
    }

    public void setStartHeight(int i) {
        this.startHeight = i;
    }

    public int getEndHeight() {
        return this.endHeight;
    }

    public void setEndHeight(int i) {
        this.endHeight = i;
    }

    public int getY() {
        return this.f3768y;
    }

    public void setY(int i) {
        this.f3768y = i;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int i) {
        this.height = i;
    }

    public PointArrayInfo getPointArrayInfo() {
        return this.pointArrayInfo;
    }

    public void setPointArrayInfo(PointArrayInfo pointArrayInfo) {
        this.pointArrayInfo = pointArrayInfo;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int i) {
        this.index = i;
    }

    public int getFindCount() {
        return this.findCount;
    }

    public void setFindCount(int i) {
        this.findCount = i;
    }

    public int[] getResultArray() {
        return this.resultArray;
    }

    public void setResultArray(int[] iArr) {
        this.resultArray = iArr;
    }

    public Map<Integer, List<Point>> getPointList() {
        return this.pointList;
    }

    public void setPointList(Map<Integer, List<Point>> map) {
        this.pointList = map;
    }

}
