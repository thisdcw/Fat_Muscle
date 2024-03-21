package com.mxsella.fatmuscle.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.mxsella.fat_muscle.R;
import com.mxsella.fatmuscle.common.MyApplication;
import com.mxsella.fatmuscle.sdk.fat.manager.FatConfigManager;
import com.mxsella.fatmuscle.sdk.fat.utils.MetricInchUnitUtil;
import com.mxsella.fatmuscle.utils.PaintUtil;

public class FatStandardView extends View {
    private static final int sBound = 20;
    private int[] colorArray;
    int curBodyPosition;
    int dis;
    private int[] iconArray;
    boolean isBoy;
    private boolean isShowValue;
    private int[] leveArray;
    private Context mContext;
    private float mCurFatValue;
    private Paint mPaint;
    private TextPaint mTextPaint;
    private Bitmap markBitmap;
    private int maxNormalValue;
    private int maxThicknessValue;
    private int maxThinValue;
    private String normal;
    private int normalColor;
    private int[] strArray;
    private String thickness;
    private int thicknessColor;
    private String thin;
    private int thinColor;
    private int unSelectColor;
    private String unit;

    /* loaded from: classes.dex */
    private enum level {
        thin,
        normal,
        thickness
    }

    public void setCurBodyPosition(int i) {
        this.curBodyPosition = i;
        invalidate();
    }

    public void setShowValue(boolean z) {
        this.isShowValue = z;
    }

    public void setCurBodyPosition(int i, boolean z) {
        this.curBodyPosition = i;
        this.isBoy = z;
    }

    public FatStandardView(Context context) {
        super(context);
        this.mCurFatValue = 0.0f;
        this.maxThicknessValue = 55;
        this.maxThinValue = 6;
        this.maxNormalValue = 35;
        this.thinColor = 0;
        this.normalColor = 0;
        this.thicknessColor = 0;
        this.unSelectColor = 0;
        this.markBitmap = null;
        this.dis = 2;
        this.isShowValue = true;
        this.mContext = MyApplication.getInstance();
        initView();
    }

    public FatStandardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCurFatValue = 0.0f;
        this.maxThicknessValue = 55;
        this.maxThinValue = 6;
        this.maxNormalValue = 35;
        this.thinColor = 0;
        this.normalColor = 0;
        this.thicknessColor = 0;
        this.unSelectColor = 0;
        this.markBitmap = null;
        this.dis = 2;
        this.isShowValue = true;
        this.mContext = MyApplication.getInstance();
        initView();
    }

    public FatStandardView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mCurFatValue = 0.0f;
        this.maxThicknessValue = 55;
        this.maxThinValue = 6;
        this.maxNormalValue = 35;
        this.thinColor = 0;
        this.normalColor = 0;
        this.thicknessColor = 0;
        this.unSelectColor = 0;
        this.markBitmap = null;
        this.dis = 2;
        this.isShowValue = true;
        this.mContext = MyApplication.getInstance();
        initView();
    }

    public FatStandardView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mCurFatValue = 0.0f;
        this.maxThicknessValue = 55;
        this.maxThinValue = 6;
        this.maxNormalValue = 35;
        this.thinColor = 0;
        this.normalColor = 0;
        this.thicknessColor = 0;
        this.unSelectColor = 0;
        this.markBitmap = null;
        this.dis = 2;
        this.isShowValue = true;
        this.mContext = MyApplication.getInstance();
        initView();
    }

    private void initView() {
        this.thin = MyApplication.getInstance().getString(R.string.standard_thin);
        this.normal = this.mContext.getResources().getString(R.string.standard_normal);
        this.thickness = this.mContext.getString(R.string.standard_thickness);
        this.unit = this.mContext.getString(R.string.app_measure_resule_unit);
        this.thinColor = this.mContext.getResources().getColor(R.color.standard_thin);
        this.normalColor = this.mContext.getResources().getColor(R.color.standard_normal);
        this.thicknessColor = this.mContext.getResources().getColor(R.color.standard_thickness);
        this.unSelectColor = this.mContext.getResources().getColor(R.color.grey_low);
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setStrokeWidth(2.0f);
        this.mPaint.setAntiAlias(true);
        TextPaint textPaint = new TextPaint();
        this.mTextPaint = textPaint;
        textPaint.setTextSize(PaintUtil.sp2px(this.mContext, 11.0f));
        this.mTextPaint.setAntiAlias(true);
        this.mTextPaint.setTextAlign(Paint.Align.CENTER);
        TypedArray obtainTypedArray = this.mContext.getResources().obtainTypedArray(R.array.leve_mark_icon);
        TypedArray obtainTypedArray2 = this.mContext.getResources().obtainTypedArray(R.array.leve_standard_str);
        this.iconArray = new int[obtainTypedArray.length()];
        this.strArray = new int[obtainTypedArray2.length()];
        int i = 0;
        while (true) {
            int[] iArr = this.iconArray;
            if (i >= iArr.length) {
                break;
            }
            iArr[i] = obtainTypedArray.getResourceId(i, 0);
            i++;
        }
        int i2 = 0;
        while (true) {
            int[] iArr2 = this.strArray;
            if (i2 >= iArr2.length) {
                break;
            }
            iArr2[i2] = obtainTypedArray2.getResourceId(i2, 0);
            i2++;
        }
        this.colorArray = this.mContext.getResources().getIntArray(R.array.leve_mark_color);
        if (FatConfigManager.getInstance().isBoy()) {
            this.dis = 2;
        } else {
            this.dis = 3;
        }
        this.curBodyPosition = FatConfigManager.getInstance().getCurBodyPositionIndex();
        this.isBoy = FatConfigManager.getInstance().isBoy();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int i;
        float f;
        super.onDraw(canvas);
        this.maxThinValue = FatConfigManager.getInstance().getCurBodyParts(this.curBodyPosition, this.isBoy).getMaxThinValue();
        this.maxNormalValue = FatConfigManager.getInstance().getCurBodyParts(this.curBodyPosition, this.isBoy).getMaxNormalValue();
        this.maxThicknessValue = FatConfigManager.getInstance().getCurBodyParts(this.curBodyPosition, this.isBoy).getMaxThicknessValue();
        int[] iArr = (int[]) FatConfigManager.getInstance().getCurBodyParts(this.curBodyPosition, this.isBoy).getLeveArray().clone();
        this.leveArray = iArr;
        if (this.isBoy) {
            this.dis = 2;
        } else {
            this.dis = 3;
        }
        int length = iArr.length;
        int[] iArr2 = new int[length];
        int i2 = 0;
        while (true) {
            int[] iArr3 = this.leveArray;
            if (i2 >= iArr3.length) {
                break;
            }
            iArr2[i2] = iArr3[i2] - this.dis;
            i2++;
        }
        float f2 = this.mCurFatValue - this.dis;
        int i3 = length - 1;
        this.maxThicknessValue = iArr2[i3];
        this.mTextPaint.setColor(this.colorArray[i3]);
        this.markBitmap = BitmapFactory.decodeResource(getResources(), this.iconArray[i3]);
        int i4 = this.maxThicknessValue;
        if (f2 > i4) {
            f2 = i4;
        }
        float f3 = f2 < 0.0f ? 0.0f : f2;
        float width = ((getWidth() - (i3 * 6)) - 10) / this.maxThicknessValue;
        int i5 = 0;
        while (true) {
            if (i5 >= length) {
                i = i3;
                break;
            } else if (Math.round(f3) < iArr2[i5]) {
                this.mTextPaint.setColor(this.colorArray[i5]);
                this.markBitmap = BitmapFactory.decodeResource(getResources(), this.iconArray[i5]);
                i = i5;
                break;
            } else {
                i5++;
            }
        }
        float f4 = this.mCurFatValue;
        if (f4 > 0.0f) {
            String unitStr = MetricInchUnitUtil.getUnitStr(f4);
            if (f3 == 0.0f) {
                canvas.drawText(unitStr, 5 + (Math.round(f3) * width) + (i * 6) + (PaintUtil.getTextWidth(this.mTextPaint, unitStr) / 2), 30, this.mTextPaint);
            } else if (this.isShowValue) {
                canvas.drawText(unitStr, 5 + (Math.round(f3) * width) + (i * 6) + (Math.round(f3) == iArr2[i3] ? (-PaintUtil.getTextWidth(this.mTextPaint, unitStr)) / 2 : width / 2.0f), 30, this.mTextPaint);
            }
        }
        int height = 30 + this.markBitmap.getHeight();
        int i6 = 0;
        while (i6 < length) {
            this.mPaint.setColor(this.colorArray[i6]);
            float f5 = 5;
            canvas.drawRect(f5 + (i6 == 0 ? 0.0f : (iArr2[i6 - 1] * width) + (i6 * 6)), height, f5 + (iArr2[i6] * width) + (i6 * 6), height + 40, this.mPaint);
            i6++;
            height = height;
        }
        int i7 = height + 40 + 35;
        for (int i8 = 0; i8 < length; i8++) {
            String string = this.mContext.getString(this.strArray[i8]);
            this.mTextPaint.setColor(this.colorArray[i8]);
            if (i8 == 0) {
                f = (iArr2[i8] * width) / 2.0f;
            } else {
                int i9 = i8 - 1;
                f = (i8 * 6) + ((iArr2[i9] + ((iArr2[i8] - iArr2[i9]) / 2.0f)) * width);
            }
            canvas.drawText(string, 5 + f, i7, this.mTextPaint);
        }
        int width2 = this.markBitmap.getWidth();
        if (this.mCurFatValue > 0.0f) {
            canvas.drawBitmap(this.markBitmap, ((5 + (Math.round(f3) * width)) - (width2 / 2)) + (i * 6) + (Math.round(f3) == iArr2[i3] ? -12.0f : width / 2.0f), 40.0f, (Paint) null);
        }
    }

    public void setCurFatValue(float f) {
        this.leveArray = FatConfigManager.getInstance().getCurBodyParts().getLeveArray();
        this.mCurFatValue = f;
        invalidate();
    }

    public void setStandardValue(int i, int i2, int i3) {
        this.maxNormalValue = i2;
        this.maxThicknessValue = i3;
        this.maxThinValue = i;
    }

}
