package com.mxsella.fatmuscle.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.mxsella.fat_muscle.R;
import com.mxsella.fatmuscle.manager.FatConfigManager;
import com.mxsella.fatmuscle.manager.MxsellaDeviceManager;
import com.mxsella.fatmuscle.utils.BitmapUtil;
import com.mxsella.fatmuscle.utils.MetricInchUnitUtil;
import com.mxsella.fatmuscle.utils.PaintUtil;

public class MeasureDividingRuleView extends View {
    private static final String TAG = "MeasureDividingRuleView";
    private int bitmapHight;
    private int depth;
    private float mHeight;
    private Paint mPaint;
    private TextPaint mTextPaint;
    private float mWidth;
    private int ocxo;

    public MeasureDividingRuleView(Context context) {
        super(context);
        MxsellaDeviceManager.getInstance().getCurDeviceCode();
        this.depth = 3;
        this.ocxo = MxsellaDeviceManager.getInstance().getOcxo();
        this.bitmapHight = BitmapUtil.sBitmapHight;
        initView();
    }

    public MeasureDividingRuleView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        MxsellaDeviceManager.getInstance().getCurDeviceCode();
        this.depth = 3;
        this.ocxo = MxsellaDeviceManager.getInstance().getOcxo();
        this.bitmapHight = BitmapUtil.sBitmapHight;
        initView();
    }

    public MeasureDividingRuleView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        MxsellaDeviceManager.getInstance().getCurDeviceCode();
        this.depth = 3;
        this.ocxo = MxsellaDeviceManager.getInstance().getOcxo();
        this.bitmapHight = BitmapUtil.sBitmapHight;
        initView();
    }

    public MeasureDividingRuleView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        MxsellaDeviceManager.getInstance().getCurDeviceCode();
        this.depth = 3;
        this.ocxo = MxsellaDeviceManager.getInstance().getOcxo();
        this.bitmapHight = BitmapUtil.sBitmapHight;
        initView();
    }

    private void initView() {
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setColor(getContext().getResources().getColor(R.color.grey_low));
        this.mPaint.setStrokeWidth(3.0f);
        this.mPaint.setAntiAlias(true);
        TextPaint textPaint = new TextPaint();
        this.mTextPaint = textPaint;
        textPaint.setColor(getContext().getResources().getColor(R.color.grey_low));
        this.mTextPaint.setAntiAlias(true);
        this.mTextPaint.setTextSize(PaintUtil.sp2px(getContext(), 13.0f));
        this.mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mWidth = getWidth();
        this.mHeight = getHeight();
        float algoDepth = FatConfigManager.getInstance().getAlgoDepth(this.ocxo, this.depth, this.bitmapHight);
        float f = this.mHeight;
        float f2 = (f * 10.0f) / algoDepth;
        int i = ((int) (f / f2)) + 1;
        if (i > 0) {
            for (int i2 = 0; i2 < i; i2++) {
                float f3 = f2 * i2;
                canvas.drawLine(0.0f, f3, 23.0f, f3, this.mPaint);
                if (i2 == 0) {
                    canvas.drawText(i2 + "", 45.0f, f3 + 22.0f, this.mTextPaint);
                } else {
                    canvas.drawText(MetricInchUnitUtil.getValueStr(i2 * 10), 50.0f, f3 + 10.0f, this.mTextPaint);
                }
            }
            for (int i3 = 0; i3 < i * 5; i3++) {
                float f4 = (f2 / 5.0f) * i3;
                canvas.drawLine(0.0f, f4, 10.0f, f4, this.mPaint);
            }
        }
    }

    public void setInit(float f, float f2, int i) {
        if (i <= 0) {
            MxsellaDeviceManager.getInstance().getCurDeviceCode();
            i = 3;
        }
        this.depth = i;
        invalidate();
        Log.i(TAG, "setInit: " + i);
    }

    public void setOcxo(int i, int i2) {
        this.ocxo = i;
        this.bitmapHight = i2;
        invalidate();
    }

    public int getOcxo() {
        return this.ocxo;
    }

}
