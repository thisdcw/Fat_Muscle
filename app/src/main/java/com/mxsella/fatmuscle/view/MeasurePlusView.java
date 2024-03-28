package com.mxsella.fatmuscle.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.InputDeviceCompat;

import com.mxsella.fat_muscle.R;
import com.mxsella.fatmuscle.common.MyApplication;
import com.mxsella.fatmuscle.db.bean.FatRecord;
import com.mxsella.fatmuscle.manager.FatConfigManager;
import com.mxsella.fatmuscle.utils.MetricInchUnitUtil;
import com.mxsella.fatmuscle.utils.DensityUtil;
import com.mxsella.fatmuscle.view.widget.PullUpDragLayout;

public class MeasurePlusView extends View {
    private static final String TAG = "MeasureView";
    private int[] array;
    int bitmapHight;

    int c0;
    float depth;
    private Bitmap fat;
    private boolean isShowUltrasoundImg;
    private Bitmap jirou;
    private Context mContext;
    private MeasureCallBack mMeasureCallBack;
    private Paint mPaint;
    private float mPosition;
    private TextPaint mTextPaint;
    float mmHeight;
    float mmWidth;
    int ocxo;
    FatRecord.TYPE type;

    /* loaded from: classes.dex */
    public interface MeasureCallBack {
        void onMeasureChange(int[] iArr);
    }

    public void setMeasureCallBack(MeasureCallBack measureCallBack) {
        this.mMeasureCallBack = measureCallBack;
    }

    public boolean isShowUltrasoundImg() {
        return this.isShowUltrasoundImg;
    }

    public void setShowUltrasoundImg(boolean z) {
        this.isShowUltrasoundImg = z;
        invalidate();
    }

    public MeasurePlusView(Context context) {
        super(context);
        this.mPosition = 0.0f;
        this.type = FatRecord.TYPE.FAT;
        this.jirou = null;
        this.fat = null;
        this.isShowUltrasoundImg = true;
        this.mContext = MyApplication.getInstance();
        initView();
    }

    @Override // android.view.View
    protected boolean dispatchGenericFocusedEvent(MotionEvent motionEvent) {
        return super.dispatchGenericFocusedEvent(motionEvent);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.isShowUltrasoundImg) {
            if (this.mMeasureCallBack == null) {
                return super.onTouchEvent(motionEvent);
            }
            int action = motionEvent.getAction();
            int i = 0;
            if (action == 0) {
                PullUpDragLayout.isSlide = false;
            } else if (action == 1) {
                PullUpDragLayout.isSlide = true;
            }
            Log.i(TAG, "onTouchEvent: x=" + (motionEvent.getX() / this.mmWidth) + " y=" + ((motionEvent.getY() * this.bitmapHight) / getHeight()));
            if (motionEvent.getY() > getHeight()) {
                return true;
            }
            StringBuffer stringBuffer = new StringBuffer();
            if (this.type == FatRecord.TYPE.MUSCLE) {
                int y = (int) ((motionEvent.getY() * this.bitmapHight) / getHeight());
                if (y < 10) {
                    y = 10;
                }
                int[] iArr = this.array;
                iArr[1] = y;
                iArr[3] = y;
                iArr[5] = y;
                invalidate();
                MeasureCallBack measureCallBack = this.mMeasureCallBack;
                if (measureCallBack != null) {
                    measureCallBack.onMeasureChange(this.array);
                }
                return true;
            }
            int[] iArr2 = this.array;
            if (iArr2 != null && iArr2.length >= 2) {
                int x = (int) (motionEvent.getX() / this.mmWidth);
                int y2 = (int) ((motionEvent.getY() * this.bitmapHight) / getHeight());
                while (true) {
                    int[] iArr3 = this.array;
                    if (i >= iArr3.length) {
                        break;
                    }
                    int i2 = iArr3[i];
                    int i3 = i + 1;
                    stringBuffer.append("x=" + i2 + " y=" + iArr3[i3]);
                    if (Math.abs(i2 - x) <= 6) {
                        this.array[i3] = y2;
                    }
                    i = i3 + 1;
                }
                Log.i(TAG, "onTouchEvent:array=" + ((Object) stringBuffer));
                MeasureCallBack measureCallBack2 = this.mMeasureCallBack;
                if (measureCallBack2 != null) {
                    measureCallBack2.onMeasureChange(this.array);
                }
            }
            invalidate();
            return true;
        }
        return true;
    }

    public MeasurePlusView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mPosition = 0.0f;
        this.type = FatRecord.TYPE.FAT;
        this.jirou = null;
        this.fat = null;
        this.isShowUltrasoundImg = true;
        this.mContext = MyApplication.getInstance();
        initView();
    }

    public MeasurePlusView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mPosition = 0.0f;
        this.type = FatRecord.TYPE.FAT;
        this.jirou = null;
        this.fat = null;
        this.isShowUltrasoundImg = true;
        this.mContext = MyApplication.getInstance();
        initView();
    }

    private void initView() {
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setColor(InputDeviceCompat.SOURCE_ANY);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStrokeWidth(DensityUtil.dip2px(this.mContext, 1.6f));
        TextPaint textPaint = new TextPaint();
        this.mTextPaint = textPaint;
        textPaint.setColor(-149746);
        this.mTextPaint.setAntiAlias(true);
        this.mTextPaint.setTextSize(DensityUtil.dip2px(this.mContext, 12.0f));
        this.mTextPaint.setTextAlign(Paint.Align.LEFT);
        this.jirou = BitmapFactory.decodeResource(getResources(), R.mipmap.blue);
        this.fat = BitmapFactory.decodeResource(getResources(), R.mipmap.yellow);
    }

    public void setDepth(int i, int i2, int i3) {
        if (i <= 0) {
            i = 3;
        }
        this.c0 = i;
        this.ocxo = i2;
        this.bitmapHight = i3;
    }

    /**
     * 画图
     * @param canvas the canvas on which the background will be drawn
     */
    @Override
    protected void onDraw(Canvas canvas) {
        int i;
        super.onDraw(canvas);
        if (this.mPosition == 0.0f) {
            return;
        }
        this.depth = FatConfigManager.getInstance().getAlgoDepth(this.ocxo, this.c0);
        this.mmHeight = getHeight() / this.depth;
        float width = getWidth();
        this.mmWidth = width / 150.0f;
        int i2 = 2;
        int width2 = getWidth() / 2;
        int[] iArr = this.array;
        if (iArr == null || iArr.length <= 1) {
            Log.i(TAG, "onDraw: " + this.mPosition + "," + this.mmHeight + "," + (this.mPosition * this.mmHeight));
            canvas.drawLine(getWidth() / 2, 0.0f, getWidth() / 2, this.mPosition * this.mmHeight, this.mPaint);
            canvas.drawLine((getWidth() / 2) - DensityUtil.dip2px(this.mContext, 3.0f), (this.mPosition * this.mmHeight) - DensityUtil.dip2px(this.mContext, 3.0f), getWidth() / 2, this.mmHeight * this.mPosition, this.mPaint);
            canvas.drawLine((getWidth() / 2) + DensityUtil.dip2px(this.mContext, 3.0f), (this.mPosition * this.mmHeight) - DensityUtil.dip2px(this.mContext, 3.0f), getWidth() / 2, this.mmHeight * this.mPosition, this.mPaint);
            String unitStr = MetricInchUnitUtil.getUnitStr(this.mPosition);
            Paint.FontMetrics fontMetrics = this.mTextPaint.getFontMetrics();
            canvas.drawText(unitStr, (getWidth() / 2) + DensityUtil.dip2px(this.mContext, 5.0f), ((this.mPosition * this.mmHeight) / 2.0f) + (((fontMetrics.bottom - fontMetrics.top) / 2.0f) - fontMetrics.bottom), this.mTextPaint);
        } else if (this.type == FatRecord.TYPE.MUSCLE) {
            Paint paint = new Paint();
            this.mPaint = paint;
            paint.setColor(InputDeviceCompat.SOURCE_ANY);
            this.mPaint.setAntiAlias(true);
            this.mPaint.setStrokeWidth(DensityUtil.dip2px(this.mContext, 1.6f));
            float height = (this.array[0] * getHeight()) / this.bitmapHight;
            float height2 = (this.array[1] * getHeight()) / this.bitmapHight;
            canvas.drawLine(0.0f, height, getWidth(), height, this.mPaint);
            canvas.drawLine(0.0f, height2, getWidth(), height2, this.mPaint);
            this.mPaint.setPathEffect(new DashPathEffect(new float[]{4.0f, 4.0f}, 0.0f));
            canvas.drawLine(getWidth() / 2, height, getWidth() / 2, height2, this.mPaint);
            drawTrangle(canvas, this.mPaint, getWidth() / 2, height, getWidth() / 2, height + 10.0f, 10, 10);
            drawTrangle(canvas, this.mPaint, getWidth() / 2, height2, getWidth() / 2, height2 - 10.0f, 10, 10);
            this.mPaint.setPathEffect(null);
            this.mPaint.setTextSize(26.0f);
            String str = MetricInchUnitUtil.getUnitStr(this.mPosition) + "";
            float width3 = (getWidth() / 2) + 10;
            if (height <= height2) {
                height2 = height + (Math.abs(height - height2) / 2.0f);
            }
            canvas.drawText(str, width3, height2, this.mPaint);
        } else {
            Log.i(TAG, "================array====" + this.array[0] + "=====" + this.array[1]);
            Path path = new Path();
            Path path2 = new Path();
            this.mPaint.setPathEffect(new DashPathEffect(new float[]{10.0f, 5.0f}, 0.0f));
            this.mPaint.setStrokeWidth(DensityUtil.dip2px(this.mContext, 1.6f));
            float strokeWidth = this.mPaint.getStrokeWidth() / 2.0f;
            int i3 = 0;
            while (true) {
                int[] iArr2 = this.array;
                if (i3 >= iArr2.length) {
                    break;
                }
                float f = (iArr2[i3] * this.mmWidth) + strokeWidth;
                if (iArr2[i3] < 0 || (i3 > i2 && iArr2[i3] < iArr2[i3 - 2])) {
                    i = i3 + 1;
                } else {
                    i = i3 + 1;
                    int i4 = iArr2[i];
                    if (i4 > 0) {
                        this.mPosition = (i4 * this.depth) / this.bitmapHight;
                        Log.i(TAG, "onDraw: " + this.mPosition + "," + this.mmHeight + "," + (this.mPosition * this.mmHeight));
                        String valueStr = MetricInchUnitUtil.getValueStr(this.mPosition);
                        Paint.FontMetrics fontMetrics2 = this.mTextPaint.getFontMetrics();
                        float f2 = ((this.mPosition * this.mmHeight) / 2.0f) + (((fontMetrics2.bottom - fontMetrics2.top) / 2.0f) - fontMetrics2.bottom);
                        if (this.isShowUltrasoundImg) {
                            canvas.drawText(valueStr, DensityUtil.dip2px(this.mContext, 2.0f) + f, f2, this.mTextPaint);
                        }
                        if (i == 1) {
                            if (this.isShowUltrasoundImg) {
                                path.moveTo(0.0f, this.mPosition * this.mmHeight);
                                path.lineTo(f, this.mPosition * this.mmHeight);
                            } else {
                                path.moveTo(0.0f, 0.0f);
                                path.lineTo(0.0f, this.mPosition * this.mmHeight);
                                path2.moveTo(0.0f, this.mPosition * this.mmHeight);
                            }
                        } else {
                            path.lineTo(f, this.mPosition * this.mmHeight);
                            path2.lineTo(f, this.mPosition * this.mmHeight);
                        }
                    }
                }
                i3 = i + 1;
                i2 = 2;
            }
            if (!this.isShowUltrasoundImg) {
                path.lineTo(width, this.mPosition * this.mmHeight);
                path.lineTo(width, 0.0f);
                path.lineTo(0.0f, 0.0f);
                path2.lineTo(width, this.mPosition * this.mmHeight);
                path2.lineTo(width, getHeight());
                path2.lineTo(0.0f, getHeight());
                path.close();
                path2.close();
                this.mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                this.mPaint.setAntiAlias(true);
                this.mPaint.setARGB(80, 255, 255, 0);
                canvas.drawPath(path, this.mPaint);
                this.mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                this.mPaint.setARGB(80, 0, 222, 255);
                canvas.drawPath(path2, this.mPaint);
                this.mPaint.setStrokeWidth(DensityUtil.dip2px(this.mContext, 3.6f));
                this.mPaint.setPathEffect(null);
                this.mPaint.setColor(InputDeviceCompat.SOURCE_ANY);
                return;
            }
            path.lineTo(width, this.mPosition * this.mmHeight);
            this.mPaint.setStrokeWidth(DensityUtil.dip2px(this.mContext, 3.6f));
            this.mPaint.setStyle(Paint.Style.STROKE);
            this.mPaint.setPathEffect(null);
            canvas.drawPath(path, this.mPaint);
        }
    }

    public void setPosition(float f) {
        this.mPosition = f;
        invalidate();
    }

    public void setArray(int[] iArr, FatRecord.TYPE type) {
        int[] iArr2;
        int i;
        this.array = iArr;
        this.type = type;
        if (type != FatRecord.TYPE.MUSCLE && (iArr2 = this.array) != null && iArr2.length < 24 && iArr2.length > 1) {
            int[] iArr3 = new int[24];
            int i2 = 0;
            int i3 = 0;
            int i4 = 0;
            while (i2 < 24) {
                iArr3[i2] = i3 + 13;
                if (Math.abs(iArr3[i2] - iArr[i4 >= iArr.length ? iArr.length - 2 : i4]) <= 13) {
                    iArr3[i2] = iArr[i4 >= iArr.length ? iArr.length - 2 : i4];
                    int i5 = i4 + 1;
                    i = i2 + 1;
                    iArr3[i] = iArr[i5 >= iArr.length ? iArr.length - 1 : i5];
                    i4 = i5 + 1;
                } else {
                    i = i2 + 1;
                    iArr3[i] = iArr[i4 >= iArr.length ? iArr.length - 1 : i4 + 1];
                }
                i3 = iArr3[i - 1];
                i2 = i + 1;
            }
            this.array = iArr3;
        }
    }

    public float getPosition() {
        return this.mPosition;
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

}
