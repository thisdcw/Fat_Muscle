package com.cw.fatmuscle.sdk.fat.entity;

public class MeasureObj {
    private int countIndex;
    private int index;
    private float resultD1;
    private float resultD2;
    private float resultD3;
    private String type;

    /* renamed from: x1 */
    private float x1;

    /* renamed from: x2 */
    private float x2;

    /* renamed from: x3 */
    private float x3;

    /* renamed from: x4 */
    private float x4;

    /* renamed from: x5 */
    private float x5;

    /* renamed from: x6 */
    private float x6;

    /* renamed from: y1 */
    private float y1;

    /* renamed from: y2 */
    private float y2;

    /* renamed from: y3 */
    private float y3;

    /* renamed from: y4 */
    private float y4;

    /* renamed from: y5 */
    private float y5;

    /* renamed from: y6 */
    private float y6;
    private int lineCount = 0;
    private boolean isFirstSection = true;
    private boolean isInitStart = false;
    private boolean isInitEnd = false;
    private boolean isComplete = false;

    public float getX1() {
        return this.x1;
    }

    public void setX1(float f) {
        this.x1 = f;
    }

    public float getY1() {
        return this.y1;
    }

    public void setY1(float f) {
        this.y1 = f;
    }

    public float getX2() {
        return this.x2;
    }

    public void setX2(float f) {
        this.x2 = f;
    }

    public float getY2() {
        return this.y2;
    }

    public void setY2(float f) {
        this.y2 = f;
    }

    public float getX3() {
        return this.x3;
    }

    public void setX3(float f) {
        this.x3 = f;
    }

    public float getY3() {
        return this.y3;
    }

    public void setY3(float f) {
        this.y3 = f;
    }

    public float getX4() {
        return this.x4;
    }

    public void setX4(float f) {
        this.x4 = f;
    }

    public float getY4() {
        return this.y4;
    }

    public void setY4(float f) {
        this.y4 = f;
    }

    public float getX5() {
        return this.x5;
    }

    public void setX5(float f) {
        this.x5 = f;
    }

    public float getY5() {
        return this.y5;
    }

    public void setY5(float f) {
        this.y5 = f;
    }

    public float getX6() {
        return this.x6;
    }

    public void setX6(float f) {
        this.x6 = f;
    }

    public float getY6() {
        return this.y6;
    }

    public void setY6(float f) {
        this.y6 = f;
    }

    public boolean isInitStart() {
        return this.isInitStart;
    }

    public void setInitStart(boolean z) {
        this.isInitStart = z;
    }

    public void setInitEnd(boolean z) {
        this.isInitEnd = z;
    }

    public boolean isInitEnd() {
        return this.isInitEnd;
    }

    public boolean isComplete() {
        return this.isComplete;
    }

    public void setComplete(boolean z) {
        this.isComplete = z;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int i) {
        this.index = i;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String str) {
        this.type = str;
    }

    public int getLineCount() {
        return this.lineCount;
    }

    public void setLineCount(int i) {
        this.lineCount = i;
    }

    public int getCountIndex() {
        return this.countIndex;
    }

    public void setCountIndex(int i) {
        this.countIndex = i;
    }

    public boolean isFirstSection() {
        return this.isFirstSection;
    }

    public void setFirstSection(boolean z) {
        this.isFirstSection = z;
    }

    public float getResultD1() {
        return this.resultD1;
    }

    public void setResultD1(float f) {
        this.resultD1 = f;
    }

    public float getResultD2() {
        return this.resultD2;
    }

    public void setResultD2(float f) {
        this.resultD2 = f;
    }

    public float getResultD3() {
        return this.resultD3;
    }

    public void setResultD3(float f) {
        this.resultD3 = f;
    }

    public void clearData() {
        this.x1 = 0.0f;
        this.y1 = 0.0f;
        this.x2 = 0.0f;
        this.y2 = 0.0f;
        this.x3 = 0.0f;
        this.y3 = 0.0f;
        this.x4 = 0.0f;
        this.y4 = 0.0f;
        this.x5 = 0.0f;
        this.y5 = 0.0f;
        this.x6 = 0.0f;
        this.y6 = 0.0f;
    }

    public String toString() {
        return "MeasureObj{x1=" + this.x1 + ", y1=" + this.y1 + ", x2=" + this.x2 + ", y2=" + this.y2 + ", x3=" + this.x3 + ", y3=" + this.y3 + ", x4=" + this.x4 + ", y4=" + this.y4 + ", x5=" + this.x5 + ", y5=" + this.y5 + ", x6=" + this.x6 + ", y6=" + this.y6 + ", lineCount=" + this.lineCount + ", index=" + this.index + ", type='" + this.type + "', isInitStart=" + this.isInitStart + ", isInitEnd=" + this.isInitEnd + ", isComplete=" + this.isComplete + '}';
    }

}
