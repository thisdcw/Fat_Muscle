package com.mxsella.fatmuscle.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.InputDeviceCompat;

import com.mxsella.fatmuscle.sdk.fat.entity.MeasureObj;
import com.mxsella.fatmuscle.sdk.fat.manager.FatConfigManager;
import com.mxsella.fatmuscle.sdk.fat.manager.MxsellaDeviceManager;
import com.mxsella.fatmuscle.sdk.fat.utils.BitmapUtil;
import com.mxsella.fatmuscle.sdk.fat.utils.MetricInchUnitUtil;
import com.mxsella.fatmuscle.sdk.util.DensityUtil;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class SlideLineView extends View {
    private int depth;
    boolean isDouble;
    private boolean isMeasure;
    private boolean isVisiable;
    private GestureDetector mGestureDetector;
    private CopyOnWriteArrayList<MeasureObj> mMeasureArray;
    private MeasureListener measureListener;
    private int ocxo;

    /* renamed from: p */
    Paint f3804p;
    Rect rectRect;

    /* renamed from: x1 */
    private float f3805x1;

    /* renamed from: x2 */
    private float f3806x2;

    /* renamed from: y1 */
    private float f3807y1;

    /* renamed from: y2 */
    private float f3808y2;

    /* loaded from: classes.dex */
    public interface MeasureListener {
        void endMeasure();

        void result(float f, int[] iArr);

        void startMeasure();
    }

    public void setMeasureListener(MeasureListener measureListener) {
        this.measureListener = measureListener;
    }

    public void setMeasure(boolean z) {
        this.isMeasure = z;
        if (z) {
            MeasureObj measureObj = new MeasureObj();
            measureObj.setY1(100.0f);
            measureObj.setY2(200.0f);
            measureObj.setType("D");
            MeasureListener measureListener = this.measureListener;
            if (measureListener != null) {
                measureListener.startMeasure();
                float algoDepth = (FatConfigManager.getInstance().getAlgoDepth() / getHeight()) * Math.abs(measureObj.getY1() - measureObj.getY2());
                measureObj.setResultD1(algoDepth);
                this.measureListener.result(algoDepth, new int[]{(int) ((this.f3807y1 * BitmapUtil.sBitmapHight) / getHeight()), (int) ((this.f3808y2 * BitmapUtil.sBitmapHight) / getHeight())});
            }
            measureObj.setType("D");
            this.mMeasureArray.clear();
            this.mMeasureArray.add(measureObj);
            invalidate();
        }
    }

    public void clearLine() {
        this.isMeasure = false;
        this.mMeasureArray.clear();
        invalidate();
        MeasureListener measureListener = this.measureListener;
        if (measureListener != null) {
            measureListener.endMeasure();
        }
    }

    public void setVisiable(boolean z) {
        MeasureListener measureListener;
        this.isVisiable = z;
        invalidate();
        if (z || (measureListener = this.measureListener) == null) {
            return;
        }
        measureListener.endMeasure();
    }

    public SlideLineView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f3805x1 = 100.0f;
        this.f3806x2 = 200.0f;
        this.f3807y1 = 100.0f;
        this.f3808y2 = 200.0f;
        this.depth = 3;
        this.ocxo = 32;
        this.isVisiable = true;
        this.isMeasure = false;
        this.isDouble = false;
        this.f3804p = new Paint();
        this.rectRect = new Rect();
        this.mMeasureArray = new CopyOnWriteArrayList<>();
        this.mGestureDetector = new GestureDetector(context, new StartPointModifyGesture());
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (MxsellaDeviceManager.getInstance().isEnd() && this.isMeasure) {
            if (this.mGestureDetector.onTouchEvent(motionEvent)) {
                return this.mGestureDetector.onTouchEvent(motionEvent);
            }
            if (motionEvent.getAction() != 2) {
                return false;
            }
            move(motionEvent);
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void move(MotionEvent motionEvent) {
        Log.i("SlideLineView", "event=" + motionEvent.getAction());
        if (this.mMeasureArray.size() == 0) {
            return;
        }
        CopyOnWriteArrayList<MeasureObj> copyOnWriteArrayList = this.mMeasureArray;
        MeasureObj measureObj = copyOnWriteArrayList.get(copyOnWriteArrayList.size() - 1);
        if (measureObj.isComplete()) {
            return;
        }
        this.f3807y1 = measureObj.getY1();
        this.f3808y2 = measureObj.getY2();
        if (Math.abs(motionEvent.getY() - this.f3807y1) < Math.abs(motionEvent.getY() - this.f3808y2)) {
            this.f3807y1 = motionEvent.getY();
        } else {
            this.f3808y2 = motionEvent.getY();
        }
        if (this.f3808y2 <= 0.0f) {
            this.f3808y2 = 0.0f;
        }
        if (this.f3807y1 <= 0.0f) {
            this.f3807y1 = 0.0f;
        }
        if (this.f3807y1 >= getHeight()) {
            this.f3807y1 = getHeight();
        }
        if (this.f3808y2 >= getHeight()) {
            this.f3808y2 = getHeight();
        }
        float algoDepth = (FatConfigManager.getInstance().getAlgoDepth() / getHeight()) * Math.abs(this.f3807y1 - this.f3808y2);
        measureObj.setResultD1(algoDepth);
        measureObj.setY1(this.f3807y1);
        measureObj.setY2(this.f3808y2);
        MeasureListener measureListener = this.measureListener;
        if (measureListener != null) {
            measureListener.result(algoDepth, new int[]{(int) ((this.f3807y1 * BitmapUtil.sBitmapHight) / getHeight()), (int) ((this.f3808y2 * BitmapUtil.sBitmapHight) / getHeight())});
        }
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.isVisiable) {
            this.f3804p.setStyle(Paint.Style.FILL);
            this.f3804p.setAntiAlias(true);
            this.f3804p.setStrokeWidth(DensityUtil.dip2px(getContext(), 1.6f));
            int size = this.mMeasureArray.size();
            int i = 0;
            Iterator<MeasureObj> it = this.mMeasureArray.iterator();
            while (it.hasNext()) {
                MeasureObj next = it.next();
                int i2 = i + 1;
                this.f3807y1 = next.getY1();
                this.f3808y2 = next.getY2();
                if (next.isComplete()) {
                    this.f3804p.setColor(-7829368);
                } else {
                    this.f3804p.setColor(InputDeviceCompat.SOURCE_ANY);
                }
                float f = this.f3807y1;
                float f2 = this.f3808y2;
                if (f > f2) {
                    this.f3808y2 = f;
                    this.f3807y1 = f2;
                }
                canvas.drawLine(0.0f, this.f3807y1, getWidth(), this.f3807y1, this.f3804p);
                canvas.drawLine(0.0f, this.f3808y2, getWidth(), this.f3808y2, this.f3804p);
                this.f3804p.setPathEffect(new DashPathEffect(new float[]{4.0f, 4.0f}, 0.0f));
                int i3 = size + 1;
                canvas.drawLine((getWidth() / i3) * i2, this.f3807y1, (getWidth() / i3) * i2, this.f3808y2, this.f3804p);
                drawTrangle(canvas, this.f3804p, (getWidth() / i3) * i2, this.f3807y1, (getWidth() / i3) * i2, this.f3807y1 + 10.0f, 10, 10);
                drawTrangle(canvas, this.f3804p, (getWidth() / i3) * i2, this.f3808y2, (getWidth() / i3) * i2, this.f3808y2 - 10.0f, 10, 10);
                this.f3804p.setPathEffect(null);
                this.f3804p.setTextSize(26.0f);
                float f3 = this.f3807y1;
                canvas.drawText(MetricInchUnitUtil.getUnitStr(next.getResultD1()) + "", ((getWidth() / i3) * i2) + 10, f3 + (Math.abs(f3 - this.f3808y2) / 2.0f), this.f3804p);
                i = i2;
            }
        }
    }

    private void drawTrangle(Canvas canvas, Paint paint, float f, float f2, float f3, float f4, int i, int i2) {
        float f5 = f3 - f;
        float f6 = f4 - f2;
        try {
            float sqrt = (float) Math.sqrt((f5 * f5) + (f6 * f6));
            float f7 = i / sqrt;
            float f8 = f3 - (f7 * f5);
            float f9 = f4 - (f7 * f6);
            Path path = new Path();
            path.moveTo(f3, f4);
            float f10 = i2 / sqrt;
            float f11 = f6 * f10;
            float f12 = f10 * f5;
            path.lineTo(f8 + f11, f9 - f12);
            path.lineTo(f8 - f11, f9 + f12);
            path.close();
            canvas.drawPath(path, paint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* loaded from: classes.dex */
    class StartPointModifyGesture extends GestureDetector.SimpleOnGestureListener {
        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onDown(MotionEvent motionEvent) {
            return true;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return true;
        }

        StartPointModifyGesture() {
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
        public boolean onDoubleTap(MotionEvent motionEvent) {
            SlideLineView.this.isDouble = true;
            return true;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public void onLongPress(MotionEvent motionEvent) {
            super.onLongPress(motionEvent);
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            SlideLineView.this.move(motionEvent2);
            return true;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            if (SlideLineView.this.mMeasureArray.size() > 0) {
                return true;
            }
            if (!SlideLineView.this.isDouble) {
                if (SlideLineView.this.mMeasureArray.size() > 0 && !((MeasureObj) SlideLineView.this.mMeasureArray.get(SlideLineView.this.mMeasureArray.size() - 1)).isComplete()) {
                    return true;
                }
                MeasureObj measureObj = new MeasureObj();
                measureObj.setY1(motionEvent.getY());
                measureObj.setY2(motionEvent.getY() + (motionEvent.getY() > 400.0f ? -200 : 200));
                measureObj.setType("D");
                SlideLineView.this.mMeasureArray.add(measureObj);
                if (SlideLineView.this.measureListener != null) {
                    SlideLineView.this.measureListener.startMeasure();
                }
                SlideLineView.this.move(motionEvent);
            } else {
                SlideLineView.this.isDouble = false;
                if (SlideLineView.this.mMeasureArray.size() > 0) {
                    MeasureObj measureObj2 = (MeasureObj) SlideLineView.this.mMeasureArray.get(SlideLineView.this.mMeasureArray.size() - 1);
                    if (!measureObj2.isComplete()) {
                        measureObj2.setComplete(true);
                        SlideLineView.this.invalidate();
                    }
                }
            }
            return true;
        }
    }

}
