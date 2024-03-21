package com.mxsella.fatmuscle.sdk.fat.entity;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class BodyParts implements Serializable {
    private int curveMarkeIcon;
    private Drawable icon;
    private Integer iconNormal;
    private Integer iconSelect;
    private Integer index;
    private int[] leveArray;
    private Integer musclePartIcon;
    private Integer musclePartTip;

    private String name;
    private int tabIndex;
    private int maxThicknessValue = 55;
    private int maxThinValue = 6;
    private int maxNormalValue = 35;

    public Integer getIndex() {
        return this.index;
    }

    public void setIndex(Integer num) {
        this.index = num;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public void setIcon(Drawable drawable) {
        this.icon = drawable;
    }


    public Integer getMusclePartTip() {
        return this.musclePartTip;
    }

    public void setMusclePartTip(Integer num) {
        this.musclePartTip = num;
    }

    public Integer getMusclePartIcon() {
        return this.musclePartIcon;
    }

    public void setMusclePartIcon(Integer num) {
        this.musclePartIcon = num;
    }

    public Integer getIconNormal() {
        return this.iconNormal;
    }

    public void setIconNormal(Integer num) {
        this.iconNormal = num;
    }

    public Integer getIconSelect() {
        return this.iconSelect;
    }

    public int[] getLeveArray() {
        return this.leveArray;
    }

    public void setLeveArray(int[] iArr) {
        this.leveArray = iArr;
    }

    public void setIconSelect(Integer num) {
        this.iconSelect = num;
    }

    public int getMaxThicknessValue() {
        return this.maxThicknessValue;
    }

    public void setMaxThicknessValue(int i) {
        this.maxThicknessValue = i;
    }

    public int getMaxThinValue() {
        return this.maxThinValue;
    }

    public void setMaxThinValue(int i) {
        this.maxThinValue = i;
    }

    public int getMaxNormalValue() {
        return this.maxNormalValue;
    }

    public void setMaxNormalValue(int i) {
        this.maxNormalValue = i;
    }

    public int getTabIndex() {
        return this.tabIndex;
    }

    public void setTabIndex(int i) {
        this.tabIndex = i;
    }

    public int getCurveMarkeIcon() {
        return this.curveMarkeIcon;
    }

    public void setCurveMarkeIcon(int i) {
        this.curveMarkeIcon = i;
    }

}
