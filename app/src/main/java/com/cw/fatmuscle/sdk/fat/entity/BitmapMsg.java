package com.cw.fatmuscle.sdk.fat.entity;

import android.graphics.Bitmap;

import com.cw.fatmuscle.sdk.fat.utils.BitmapUtil;
import com.cw.fatmuscle.utils.ArrayUtil;

public class BitmapMsg extends DeviceMsg{
    public static final int END = 1;
    public static final int RUN = 0;
    public static final int START = 16;
    private int[] array;
    private Bitmap mBitmap;
    private float mFatThickness;
    private State mState;

    /* loaded from: classes.dex */
    public enum State {
        START,
        END,
        RUN,
        MEASURE
    }

    public int getMaxValue() {
        return ArrayUtil.getMaxValue(this.array);
    }

    public int getMinValue() {
        return ArrayUtil.getMinValue(this.array);
    }

    public int[] getArray() {
        return this.array;
    }

    public void setArray(int[] iArr) {
        this.array = iArr;
    }

    public State getState() {
        return this.mState;
    }

    public void setState(State state) {
        this.mState = state;
    }

    public float getFatThickness() {
        return this.mFatThickness;
    }

    public void setFatThickness(float f) {
        this.mFatThickness = f;
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    @Override
    public void unpack(byte[] bArr) {
        try {
            this.mBitmap = BitmapUtil.add(bArr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
