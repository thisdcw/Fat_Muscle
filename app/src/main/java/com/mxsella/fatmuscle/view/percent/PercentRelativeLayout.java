package com.mxsella.fatmuscle.view.percent;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class PercentRelativeLayout extends RelativeLayout {
//    private final PercentLayoutHelper mHelper;

    public PercentRelativeLayout(Context context) {
        super(context);
//        this.mHelper = new PercentLayoutHelper(this);
    }

    public PercentRelativeLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
//        this.mHelper = new PercentLayoutHelper(this);
    }

    public PercentRelativeLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
//        this.mHelper = new PercentLayoutHelper(this);
    }

    @Override // android.widget.RelativeLayout, android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    @Override // android.widget.RelativeLayout, android.view.View
    protected void onMeasure(int i, int i2) {
//        this.mHelper.adjustChildren(i, i2);
        super.onMeasure(i, i2);
//        if (this.mHelper.handleMeasuredStateTooSmall()) {
//            super.onMeasure(i, i2);
//        }
    }

    @Override // android.widget.RelativeLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
//        this.mHelper.restoreOriginalParams();
    }

    /* loaded from: classes.dex */
    public static class LayoutParams extends RelativeLayout.LayoutParams  {
//        private PercentLayoutHelper.PercentLayoutInfo mPercentLayoutInfo;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
//            this.mPercentLayoutInfo = PercentLayoutHelper.getPercentLayoutInfo(context, attributeSet);
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
        }
    }

}
