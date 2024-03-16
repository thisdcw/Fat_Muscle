package com.cw.fatmuscle.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.cw.fat_muscle.R;
import com.cw.fatmuscle.common.MyApplication;
import com.cw.fatmuscle.sdk.fat.manager.FatConfigManager;
import com.cw.fatmuscle.sdk.fat.utils.MetricInchUnitUtil;
import com.cw.fatmuscle.sdk.util.DensityUtil;
import com.cw.fatmuscle.utils.PaintUtil;

public class ResultRoundView extends View {
    private Bitmap bgBitmap;
    private int[] colorArray;
    private String failTip;
    private String fatness;
    float fatnessValue;
    private int greyColor;
    private String ingTip;
    private boolean isShowStandard;
    private int[] leveArray;
    private Context mContext;
    private Paint mPaint;
    private STATUS mStatus;
    private TextPaint mTextPaint;
    private TextPaint mUnitTextPaint;
    private int maxNormalValue;
    private int maxThicknessValue;
    private int maxThinValue;
    private int normalColor;
    private int[] strArray;
    private int thicknessColor;
    private int thinColor;

    /* loaded from: classes.dex */
    public enum STATUS {
        ING,
        SUCCESS,
        FAIL
    }

    public ResultRoundView(Context context) {
        super(context);
        this.fatness = "0";
        this.bgBitmap = null;
        this.mStatus = STATUS.ING;
        this.greyColor = 0;
        this.thinColor = 0;
        this.normalColor = 0;
        this.thicknessColor = 0;
        this.isShowStandard = true;
        this.maxThicknessValue = 55;
        this.maxThinValue = 6;
        this.maxNormalValue = 35;
        this.mContext = context;
        initView();
    }

    public ResultRoundView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.fatness = "0";
        this.bgBitmap = null;
        this.mStatus = STATUS.ING;
        this.greyColor = 0;
        this.thinColor = 0;
        this.normalColor = 0;
        this.thicknessColor = 0;
        this.isShowStandard = true;
        this.maxThicknessValue = 55;
        this.maxThinValue = 6;
        this.maxNormalValue = 35;
        this.mContext = context;
        initView();
    }

    private void initView() {
        this.mPaint = new Paint();
        this.greyColor = MyApplication.getInstance().getResources().getColor(R.color.grey_low);
        this.ingTip = this.mContext.getResources().getString(R.string.measure_ing_tip);
        this.failTip = this.mContext.getResources().getString(R.string.measure_fail_tip);
        TextPaint textPaint = new TextPaint();
        this.mTextPaint = textPaint;
        textPaint.setAntiAlias(true);
        this.mTextPaint.setColor(-149746);
        this.mTextPaint.setTextSize(DensityUtil.dip2px(getContext(), 60.0f));
        this.mTextPaint.setTextAlign(Paint.Align.CENTER);
        TextPaint textPaint2 = new TextPaint();
        this.mUnitTextPaint = textPaint2;
        textPaint2.setAntiAlias(true);
        this.mUnitTextPaint.setColor(-149746);
        this.mUnitTextPaint.setTextSize(DensityUtil.dip2px(getContext(), 14.0f));
        this.mUnitTextPaint.setTextAlign(Paint.Align.CENTER);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inSampleSize = 1;
        options.inInputShareable = true;
        this.bgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cycle_background, options);
        this.thinColor = this.mContext.getResources().getColor(R.color.standard_thin);
        this.normalColor = this.mContext.getResources().getColor(R.color.standard_normal);
        this.thicknessColor = this.mContext.getResources().getColor(R.color.standard_thickness);
        this.maxThinValue = FatConfigManager.getInstance().getCurBodyParts().getMaxThinValue();
        this.maxNormalValue = FatConfigManager.getInstance().getCurBodyParts().getMaxNormalValue();
        this.maxThicknessValue = FatConfigManager.getInstance().getCurBodyParts().getMaxThicknessValue();
        TypedArray obtainTypedArray = getResources().obtainTypedArray(R.array.leve_standard_str);
        this.strArray = new int[obtainTypedArray.length()];
        int i = 0;
        while (true) {
            int[] iArr = this.strArray;
            if (i < iArr.length) {
                iArr[i] = obtainTypedArray.getResourceId(i, 0);
                i++;
            } else {
                this.colorArray = this.mContext.getResources().getIntArray(R.array.leve_mark_color);
                this.leveArray = FatConfigManager.getInstance().getCurBodyParts().getLeveArray();
                return;
            }
        }
    }

    public ResultRoundView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.fatness ="0";
        this.bgBitmap = null;
        this.mStatus = STATUS.ING;
        this.greyColor = 0;
        this.thinColor = 0;
        this.normalColor = 0;
        this.thicknessColor = 0;
        this.isShowStandard = true;
        this.maxThicknessValue = 55;
        this.maxThinValue = 6;
        this.maxNormalValue = 35;
        this.mContext = context;
        initView();
    }

    public ResultRoundView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.fatness = "0";
        this.bgBitmap = null;
        this.mStatus = STATUS.ING;
        this.greyColor = 0;
        this.thinColor = 0;
        this.normalColor = 0;
        this.thicknessColor = 0;
        this.isShowStandard = true;
        this.maxThicknessValue = 55;
        this.maxThinValue = 6;
        this.maxNormalValue = 35;
    }

    public void setShowStandard(boolean z) {
        this.isShowStandard = z;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int i;
        super.onDraw(canvas);
        int[] iArr = this.leveArray;
        if (iArr != null) {
            i = iArr.length - 1;
            int i2 = 0;
            while (true) {
                if (i2 >= this.leveArray.length) {
                    break;
                } else if (Math.round(this.fatnessValue) < this.leveArray[i2]) {
                    i = i2;
                    break;
                } else {
                    i2++;
                }
            }
        } else {
            i = 1;
        }
        canvas.drawBitmap(this.bgBitmap, 0.0f, 0.0f, (Paint) null);
        this.mUnitTextPaint.setTextSize(DensityUtil.dip2px(this.mContext, 16.0f));
        int i3 = C20671.$SwitchMap$com$marvoto$fat$widget$ResultRoundView$STATUS[this.mStatus.ordinal()];
        if (i3 == 1) {
            this.mUnitTextPaint.setColor(this.greyColor);
            canvas.drawText(this.ingTip, getWidth() / 2, (getHeight() / 2) + DensityUtil.dip2px(this.mContext, 10.0f), this.mUnitTextPaint);
        } else if (i3 != 2) {
            if (i3 != 3) {
                return;
            }
            this.mUnitTextPaint.setColor(this.greyColor);
            canvas.drawText(this.failTip, getWidth() / 2, (getHeight() / 2) + DensityUtil.dip2px(this.mContext, 10.0f), this.mUnitTextPaint);
        } else {
            this.mUnitTextPaint.setColor(-149746);
            canvas.drawText(this.fatness + "", getWidth() / 2, (getHeight() / 2) + DensityUtil.dip2px(this.mContext, this.isShowStandard ? 10.0f : 20.0f), this.mTextPaint);
            canvas.drawText(MetricInchUnitUtil.getUnitStr(), (getWidth() / 2) + (PaintUtil.getTextWidth(this.mTextPaint, this.fatness) / 2) + 25, (getHeight() / 2) + DensityUtil.dip2px(this.mContext, 10.0f), this.mUnitTextPaint);
            if (!this.isShowStandard || FatConfigManager.getInstance().getCurBodyPositionIndex() == 1 || Build.VERSION.SDK_INT < 21) {
                return;
            }
            this.mContext.getString(R.string.standard_thin);
            this.mUnitTextPaint.setColor(this.colorArray[i]);
            String string = this.mContext.getString(this.strArray[i]);
            this.mUnitTextPaint.setTextSize(DensityUtil.dip2px(this.mContext, 14.0f));
            float f = this.mUnitTextPaint.getFontMetrics().descent - this.mUnitTextPaint.getFontMetrics().ascent;
            float f2 = f / 3.0f;
            canvas.drawRoundRect(((getWidth() / 2) - (PaintUtil.getTextWidth(this.mUnitTextPaint, string) / 2)) - 15, ((((getHeight() / 2) + (getHeight() / 4)) + (getHeight() / 8)) - f2) - f, (getWidth() / 2) + (PaintUtil.getTextWidth(this.mUnitTextPaint, string) / 2) + 15, ((((getHeight() / 2) + (getHeight() / 4)) + (getHeight() / 8)) - f2) + 16.0f, 55.0f, 55.0f, this.mUnitTextPaint);
            this.mUnitTextPaint.setColor(this.mContext.getResources().getColor(R.color.app_main_tab_unselector));
            canvas.drawText(string, getWidth() / 2, (((getHeight() / 2) + (getHeight() / 4)) + (getHeight() / 8)) - f2, this.mUnitTextPaint);
            String string2 = this.mContext.getString(R.string.avg_tip);
            this.mUnitTextPaint.setColor(this.mContext.getColor(R.color.grey_low));
            canvas.drawText(string2, getWidth() / 2, ((getHeight() / 2) + (getHeight() / 4)) - (f / 2.0f), this.mUnitTextPaint);
        }
    }

    /* renamed from: com.marvoto.fat.widget.ResultRoundView$1 */
    /* loaded from: classes.dex */
    static /* synthetic */ class C20671 {
        static final /* synthetic */ int[] $SwitchMap$com$marvoto$fat$widget$ResultRoundView$STATUS;

        static {
            int[] iArr = new int[STATUS.values().length];
            $SwitchMap$com$marvoto$fat$widget$ResultRoundView$STATUS = iArr;
            try {
                iArr[STATUS.ING.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$marvoto$fat$widget$ResultRoundView$STATUS[STATUS.SUCCESS.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$marvoto$fat$widget$ResultRoundView$STATUS[STATUS.FAIL.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.bgBitmap = Bitmap.createScaledBitmap(this.bgBitmap, getWidth(), getHeight(), true);
    }

    public void setFatness(String str) {
        this.fatness = str;
        invalidate();
    }

    public void setFatness(float f) {
        this.fatnessValue = f;
        if (f > 0.0f) {
            this.fatness = MetricInchUnitUtil.getValueStr(f);
            Log.i("==", "====fatness=" + this.fatnessValue + "========fatness=" + f);
            this.mStatus = STATUS.SUCCESS;
        }
        invalidate();
    }

    public void startMeasure() {
        this.mStatus = STATUS.ING;
        this.fatnessValue = 0.0f;
        invalidate();
    }

    public void stopMeasure() {
        if (this.fatnessValue > 0.0f) {
            this.mStatus = STATUS.SUCCESS;
        } else {
            this.mStatus = STATUS.FAIL;
        }
        invalidate();
    }

}
