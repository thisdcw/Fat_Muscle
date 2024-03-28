package com.mxsella.fatmuscle.view.percent;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.view.MarginLayoutParamsCompat;
import androidx.core.view.ViewCompat;

import com.mxsella.fat_muscle.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PercentLayoutHelper {
    private static final String REGEX_PERCENT = "^(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)%([wh]?)$";
    private static final String TAG = "PercentLayout";
    private final ViewGroup mHost;

    /* loaded from: classes.dex */
    public interface PercentLayoutParams {
        PercentLayoutInfo getPercentLayoutInfo();
    }

    public PercentLayoutHelper(ViewGroup viewGroup) {
        this.mHost = viewGroup;
    }

    public static void fetchWidthAndHeight(ViewGroup.LayoutParams layoutParams, TypedArray typedArray, int i, int i2) {
        layoutParams.width = typedArray.getLayoutDimension(i, 0);
        layoutParams.height = typedArray.getLayoutDimension(i2, 0);
    }

    public void adjustChildren(int i, int i2) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "adjustChildren: " + this.mHost + " widthMeasureSpec: " + View.MeasureSpec.toString(i) + " heightMeasureSpec: " + View.MeasureSpec.toString(i2));
        }
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "widthHint = " + size + " , heightHint = " + size2);
        }
        int childCount = this.mHost.getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = this.mHost.getChildAt(i3);
            ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "should adjust " + childAt + " " + layoutParams);
            }
            if (layoutParams instanceof PercentLayoutParams) {
                PercentLayoutInfo percentLayoutInfo = ((PercentLayoutParams) layoutParams).getPercentLayoutInfo();
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "using " + percentLayoutInfo);
                }
                if (percentLayoutInfo != null) {
                    supportTextSize(size, size2, childAt, percentLayoutInfo);
                    supportPadding(size, size2, childAt, percentLayoutInfo);
                    supportMinOrMaxDimesion(size, size2, childAt, percentLayoutInfo);
                    if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                        percentLayoutInfo.fillMarginLayoutParams((ViewGroup.MarginLayoutParams) layoutParams, size, size2);
                    } else {
                        percentLayoutInfo.fillLayoutParams(layoutParams, size, size2);
                    }
                }
            }
        }
    }

    private void supportPadding(int i, int i2, View view, PercentLayoutInfo percentLayoutInfo) {
        int paddingLeft = view.getPaddingLeft();
        int paddingRight = view.getPaddingRight();
        int paddingTop = view.getPaddingTop();
        int paddingBottom = view.getPaddingBottom();
        PercentLayoutInfo.PercentVal percentVal = percentLayoutInfo.paddingLeftPercent;
        if (percentVal != null) {
            paddingLeft = (int) ((percentVal.isBaseWidth ? i : i2) * percentVal.percent);
        }
        PercentLayoutInfo.PercentVal percentVal2 = percentLayoutInfo.paddingRightPercent;
        if (percentVal2 != null) {
            paddingRight = (int) ((percentVal2.isBaseWidth ? i : i2) * percentVal2.percent);
        }
        PercentLayoutInfo.PercentVal percentVal3 = percentLayoutInfo.paddingTopPercent;
        if (percentVal3 != null) {
            paddingTop = (int) ((percentVal3.isBaseWidth ? i : i2) * percentVal3.percent);
        }
        PercentLayoutInfo.PercentVal percentVal4 = percentLayoutInfo.paddingBottomPercent;
        if (percentVal4 != null) {
            if (!percentVal4.isBaseWidth) {
                i = i2;
            }
            paddingBottom = (int) (i * percentVal4.percent);
        }
        view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    private void supportMinOrMaxDimesion(int i, int i2, View view, PercentLayoutInfo percentLayoutInfo) {
        try {
            Class<?> cls = view.getClass();
            invokeMethod("setMaxWidth", i, i2, view, cls, percentLayoutInfo.maxWidthPercent);
            invokeMethod("setMaxHeight", i, i2, view, cls, percentLayoutInfo.maxHeightPercent);
            invokeMethod("setMinWidth", i, i2, view, cls, percentLayoutInfo.minWidthPercent);
            invokeMethod("setMinHeight", i, i2, view, cls, percentLayoutInfo.minHeightPercent);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        }
    }

    private void invokeMethod(String str, int i, int i2, View view, Class cls, PercentLayoutInfo.PercentVal percentVal) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (percentVal != null) {
            Method method = cls.getMethod(str, Integer.TYPE);
            method.setAccessible(true);
            if (!percentVal.isBaseWidth) {
                i = i2;
            }
            method.invoke(view, Integer.valueOf((int) (i * percentVal.percent)));
        }
    }

    private void supportTextSize(int i, int i2, View view, PercentLayoutInfo percentLayoutInfo) {
        PercentLayoutInfo.PercentVal percentVal = percentLayoutInfo.textSizePercent;
        if (percentVal == null) {
            return;
        }
        if (!percentVal.isBaseWidth) {
            i = i2;
        }
        float f = (int) (i * percentVal.percent);
        if (view instanceof TextView) {
            ((TextView) view).setTextSize(0, f);
        }
    }

    public static PercentLayoutInfo getPercentLayoutInfo(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.PercentLayout_Layout);
        PercentLayoutInfo paddingRelatedVal = setPaddingRelatedVal(obtainStyledAttributes, setMinMaxWidthHeightRelatedVal(obtainStyledAttributes, setTextSizeSupportVal(obtainStyledAttributes, setMarginRelatedVal(obtainStyledAttributes, setWidthAndHeightVal(obtainStyledAttributes, null)))));
        Log.d(TAG, "constructed: " + paddingRelatedVal);
        obtainStyledAttributes.recycle();
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "constructed: " + paddingRelatedVal);
        }
        return paddingRelatedVal;
    }

    private static PercentLayoutInfo setWidthAndHeightVal(TypedArray typedArray, PercentLayoutInfo percentLayoutInfo) {
        PercentLayoutInfo.PercentVal percentVal = getPercentVal(typedArray, R.styleable.PercentLayout_Layout_layout_widthPercent, true);
        if (percentVal != null) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "percent width: " + percentVal.percent);
            }
            percentLayoutInfo = checkForInfoExists(percentLayoutInfo);
            percentLayoutInfo.widthPercent = percentVal;
        }
        PercentLayoutInfo.PercentVal percentVal2 = getPercentVal(typedArray, R.styleable.PercentLayout_Layout_layout_heightPercent, false);
        if (percentVal2 != null) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "percent height: " + percentVal2.percent);
            }
            PercentLayoutInfo checkForInfoExists = checkForInfoExists(percentLayoutInfo);
            checkForInfoExists.heightPercent = percentVal2;
            return checkForInfoExists;
        }
        return percentLayoutInfo;
    }

    private static PercentLayoutInfo setTextSizeSupportVal(TypedArray typedArray, PercentLayoutInfo percentLayoutInfo) {
        PercentLayoutInfo.PercentVal percentVal = getPercentVal(typedArray, R.styleable.PercentLayout_Layout_layout_textSizePercent, false);
        if (percentVal != null) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "percent text size: " + percentVal.percent);
            }
            PercentLayoutInfo checkForInfoExists = checkForInfoExists(percentLayoutInfo);
            checkForInfoExists.textSizePercent = percentVal;
            return checkForInfoExists;
        }
        return percentLayoutInfo;
    }

    private static PercentLayoutInfo setMinMaxWidthHeightRelatedVal(TypedArray typedArray, PercentLayoutInfo percentLayoutInfo) {
        PercentLayoutInfo.PercentVal percentVal = getPercentVal(typedArray, R.styleable.PercentLayout_Layout_layout_maxWidthPercent, true);
        if (percentVal != null) {
            checkForInfoExists(percentLayoutInfo);
            percentLayoutInfo.maxWidthPercent = percentVal;
        }
        PercentLayoutInfo.PercentVal percentVal2 = getPercentVal(typedArray, R.styleable.PercentLayout_Layout_layout_maxHeightPercent, false);
        if (percentVal2 != null) {
            checkForInfoExists(percentLayoutInfo);
            percentLayoutInfo.maxHeightPercent = percentVal2;
        }
        PercentLayoutInfo.PercentVal percentVal3 = getPercentVal(typedArray, R.styleable.PercentLayout_Layout_layout_minWidthPercent, true);
        if (percentVal3 != null) {
            checkForInfoExists(percentLayoutInfo);
            percentLayoutInfo.minWidthPercent = percentVal3;
        }
        PercentLayoutInfo.PercentVal percentVal4 = getPercentVal(typedArray, R.styleable.PercentLayout_Layout_layout_minHeightPercent, false);
        if (percentVal4 != null) {
            checkForInfoExists(percentLayoutInfo);
            percentLayoutInfo.minHeightPercent = percentVal4;
        }
        return percentLayoutInfo;
    }

    private static PercentLayoutInfo setMarginRelatedVal(TypedArray typedArray, PercentLayoutInfo percentLayoutInfo) {
        PercentLayoutInfo.PercentVal percentVal = getPercentVal(typedArray, R.styleable.PercentLayout_Layout_layout_marginPercent, true);
        if (percentVal != null) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "percent margin: " + percentVal.percent);
            }
            percentLayoutInfo = checkForInfoExists(percentLayoutInfo);
            percentLayoutInfo.leftMarginPercent = percentVal;
            percentLayoutInfo.topMarginPercent = percentVal;
            percentLayoutInfo.rightMarginPercent = percentVal;
            percentLayoutInfo.bottomMarginPercent = percentVal;
        }
        PercentLayoutInfo.PercentVal percentVal2 = getPercentVal(typedArray, R.styleable.PercentLayout_Layout_layout_marginLeftPercent, true);
        if (percentVal2 != null) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "percent left margin: " + percentVal2.percent);
            }
            percentLayoutInfo = checkForInfoExists(percentLayoutInfo);
            percentLayoutInfo.leftMarginPercent = percentVal2;
        }
        PercentLayoutInfo.PercentVal percentVal3 = getPercentVal(typedArray, R.styleable.PercentLayout_Layout_layout_marginTopPercent, false);
        if (percentVal3 != null) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "percent top margin: " + percentVal3.percent);
            }
            percentLayoutInfo = checkForInfoExists(percentLayoutInfo);
            percentLayoutInfo.topMarginPercent = percentVal3;
        }
        PercentLayoutInfo.PercentVal percentVal4 = getPercentVal(typedArray, R.styleable.PercentLayout_Layout_layout_marginRightPercent, true);
        if (percentVal4 != null) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "percent right margin: " + percentVal4.percent);
            }
            percentLayoutInfo = checkForInfoExists(percentLayoutInfo);
            percentLayoutInfo.rightMarginPercent = percentVal4;
        }
        PercentLayoutInfo.PercentVal percentVal5 = getPercentVal(typedArray, R.styleable.PercentLayout_Layout_layout_marginBottomPercent, false);
        if (percentVal5 != null) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "percent bottom margin: " + percentVal5.percent);
            }
            percentLayoutInfo = checkForInfoExists(percentLayoutInfo);
            percentLayoutInfo.bottomMarginPercent = percentVal5;
        }
        PercentLayoutInfo.PercentVal percentVal6 = getPercentVal(typedArray, R.styleable.PercentLayout_Layout_layout_marginStartPercent, true);
        if (percentVal6 != null) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "percent start margin: " + percentVal6.percent);
            }
            percentLayoutInfo = checkForInfoExists(percentLayoutInfo);
            percentLayoutInfo.startMarginPercent = percentVal6;
        }
        PercentLayoutInfo.PercentVal percentVal7 = getPercentVal(typedArray, R.styleable.PercentLayout_Layout_layout_marginEndPercent, true);
        if (percentVal7 != null) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "percent end margin: " + percentVal7.percent);
            }
            PercentLayoutInfo checkForInfoExists = checkForInfoExists(percentLayoutInfo);
            checkForInfoExists.endMarginPercent = percentVal7;
            return checkForInfoExists;
        }
        return percentLayoutInfo;
    }

    private static PercentLayoutInfo setPaddingRelatedVal(TypedArray typedArray, PercentLayoutInfo percentLayoutInfo) {
        PercentLayoutInfo.PercentVal percentVal = getPercentVal(typedArray, R.styleable.PercentLayout_Layout_layout_paddingPercent, true);
        if (percentVal != null) {
            percentLayoutInfo = checkForInfoExists(percentLayoutInfo);
            percentLayoutInfo.paddingLeftPercent = percentVal;
            percentLayoutInfo.paddingRightPercent = percentVal;
            percentLayoutInfo.paddingBottomPercent = percentVal;
            percentLayoutInfo.paddingTopPercent = percentVal;
        }
        PercentLayoutInfo.PercentVal percentVal2 = getPercentVal(typedArray, R.styleable.PercentLayout_Layout_layout_paddingLeftPercent, true);
        if (percentVal2 != null) {
            percentLayoutInfo = checkForInfoExists(percentLayoutInfo);
            percentLayoutInfo.paddingLeftPercent = percentVal2;
        }
        PercentLayoutInfo.PercentVal percentVal3 = getPercentVal(typedArray, R.styleable.PercentLayout_Layout_layout_paddingRightPercent, true);
        if (percentVal3 != null) {
            percentLayoutInfo = checkForInfoExists(percentLayoutInfo);
            percentLayoutInfo.paddingRightPercent = percentVal3;
        }
        PercentLayoutInfo.PercentVal percentVal4 = getPercentVal(typedArray, R.styleable.PercentLayout_Layout_layout_paddingTopPercent, true);
        if (percentVal4 != null) {
            percentLayoutInfo = checkForInfoExists(percentLayoutInfo);
            percentLayoutInfo.paddingTopPercent = percentVal4;
        }
        PercentLayoutInfo.PercentVal percentVal5 = getPercentVal(typedArray, R.styleable.PercentLayout_Layout_layout_paddingBottomPercent, true);
        if (percentVal5 != null) {
            PercentLayoutInfo checkForInfoExists = checkForInfoExists(percentLayoutInfo);
            checkForInfoExists.paddingBottomPercent = percentVal5;
            return checkForInfoExists;
        }
        return percentLayoutInfo;
    }

    private static PercentLayoutInfo.PercentVal getPercentVal(TypedArray typedArray, int i, boolean z) {
        return getPercentVal(typedArray.getString(i), z);
    }

    private static PercentLayoutInfo checkForInfoExists(PercentLayoutInfo percentLayoutInfo) {
        return percentLayoutInfo != null ? percentLayoutInfo : new PercentLayoutInfo();
    }

    private static PercentLayoutInfo.PercentVal getPercentVal(String str, boolean z) {
        if (str == null) {
            return null;
        }
        Matcher matcher = Pattern.compile(REGEX_PERCENT).matcher(str);
        if (!matcher.matches()) {
            throw new RuntimeException("the value of layout_xxxPercent invalid! ==>" + str);
        }
        int length = str.length();
        boolean z2 = true;
        String group = matcher.group(1);
        String substring = str.substring(length - 1);
        float parseFloat = Float.parseFloat(group) / 100.0f;
        if ((!z || substring.equals("h")) && !substring.equals("w")) {
            z2 = false;
        }
        return new PercentLayoutInfo.PercentVal(parseFloat, z2);
    }

    public void restoreOriginalParams() {
        int childCount = this.mHost.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.mHost.getChildAt(i);
            ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "should restore " + childAt + " " + layoutParams);
            }
            if (layoutParams instanceof PercentLayoutParams) {
                PercentLayoutInfo percentLayoutInfo = ((PercentLayoutParams) layoutParams).getPercentLayoutInfo();
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "using " + percentLayoutInfo);
                }
                if (percentLayoutInfo != null) {
                    if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                        percentLayoutInfo.restoreMarginLayoutParams((ViewGroup.MarginLayoutParams) layoutParams);
                    } else {
                        percentLayoutInfo.restoreLayoutParams(layoutParams);
                    }
                }
            }
        }
    }

    public boolean handleMeasuredStateTooSmall() {
        PercentLayoutInfo percentLayoutInfo;
        int childCount = this.mHost.getChildCount();
        boolean z = false;
        for (int i = 0; i < childCount; i++) {
            View childAt = this.mHost.getChildAt(i);
            ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
            if ((layoutParams instanceof PercentLayoutParams) && (percentLayoutInfo = ((PercentLayoutParams) layoutParams).getPercentLayoutInfo()) != null) {
                if (shouldHandleMeasuredWidthTooSmall(childAt, percentLayoutInfo)) {
                    layoutParams.width = -2;
                    z = true;
                }
                if (shouldHandleMeasuredHeightTooSmall(childAt, percentLayoutInfo)) {
                    layoutParams.height = -2;
                    z = true;
                }
            }
        }
        return z;
    }

    private static boolean shouldHandleMeasuredWidthTooSmall(View view, PercentLayoutInfo percentLayoutInfo) {
        return percentLayoutInfo != null && percentLayoutInfo.widthPercent != null && percentLayoutInfo.mPreservedParams != null && (ViewCompat.getMeasuredWidthAndState(view) & ViewCompat.MEASURED_STATE_MASK) == 16777216 && percentLayoutInfo.widthPercent.percent >= 0.0f && percentLayoutInfo.mPreservedParams.width == -2;
    }

    private static boolean shouldHandleMeasuredHeightTooSmall(View view, PercentLayoutInfo percentLayoutInfo) {
        return percentLayoutInfo != null && percentLayoutInfo.heightPercent != null && percentLayoutInfo.mPreservedParams != null && (ViewCompat.getMeasuredHeightAndState(view) & ViewCompat.MEASURED_STATE_MASK) == 16777216 && percentLayoutInfo.heightPercent.percent >= 0.0f && percentLayoutInfo.mPreservedParams.height == -2;
    }

    /* loaded from: classes.dex */
    public static class PercentLayoutInfo {
        public PercentVal bottomMarginPercent;
        public PercentVal endMarginPercent;
        public PercentVal heightPercent;
        public PercentVal leftMarginPercent;
        final ViewGroup.MarginLayoutParams mPreservedParams = new ViewGroup.MarginLayoutParams(0, 0);
        public PercentVal maxHeightPercent;
        public PercentVal maxWidthPercent;
        public PercentVal minHeightPercent;
        public PercentVal minWidthPercent;
        public PercentVal paddingBottomPercent;
        public PercentVal paddingLeftPercent;
        public PercentVal paddingRightPercent;
        public PercentVal paddingTopPercent;
        public PercentVal rightMarginPercent;
        public PercentVal startMarginPercent;
        public PercentVal textSizePercent;
        public PercentVal topMarginPercent;
        public PercentVal widthPercent;

        /* loaded from: classes.dex */
        public static class PercentVal {
            public boolean isBaseWidth;
            public float percent;

            public PercentVal() {
                this.percent = -1.0f;
            }

            public PercentVal(float f, boolean z) {
                this.percent = -1.0f;
                this.percent = f;
                this.isBaseWidth = z;
            }

            public String toString() {
                return "PercentVal{percent=" + this.percent + ", isBaseWidth=" + this.isBaseWidth + '}';
            }
        }

        public void fillLayoutParams(ViewGroup.LayoutParams layoutParams, int i, int i2) {
            this.mPreservedParams.width = layoutParams.width;
            this.mPreservedParams.height = layoutParams.height;
            PercentVal percentVal = this.widthPercent;
            if (percentVal != null) {
                layoutParams.width = (int) ((percentVal.isBaseWidth ? i : i2) * this.widthPercent.percent);
            }
            PercentVal percentVal2 = this.heightPercent;
            if (percentVal2 != null) {
                if (!percentVal2.isBaseWidth) {
                    i = i2;
                }
                layoutParams.height = (int) (i * this.heightPercent.percent);
            }
        }

        public void fillMarginLayoutParams(ViewGroup.MarginLayoutParams marginLayoutParams, int i, int i2) {
            fillLayoutParams(marginLayoutParams, i, i2);
            this.mPreservedParams.leftMargin = marginLayoutParams.leftMargin;
            this.mPreservedParams.topMargin = marginLayoutParams.topMargin;
            this.mPreservedParams.rightMargin = marginLayoutParams.rightMargin;
            this.mPreservedParams.bottomMargin = marginLayoutParams.bottomMargin;
            MarginLayoutParamsCompat.setMarginStart(this.mPreservedParams, MarginLayoutParamsCompat.getMarginStart(marginLayoutParams));
            MarginLayoutParamsCompat.setMarginEnd(this.mPreservedParams, MarginLayoutParamsCompat.getMarginEnd(marginLayoutParams));
            PercentVal percentVal = this.leftMarginPercent;
            if (percentVal != null) {
                marginLayoutParams.leftMargin = (int) ((percentVal.isBaseWidth ? i : i2) * this.leftMarginPercent.percent);
            }
            PercentVal percentVal2 = this.topMarginPercent;
            if (percentVal2 != null) {
                marginLayoutParams.topMargin = (int) ((percentVal2.isBaseWidth ? i : i2) * this.topMarginPercent.percent);
            }
            PercentVal percentVal3 = this.rightMarginPercent;
            if (percentVal3 != null) {
                marginLayoutParams.rightMargin = (int) ((percentVal3.isBaseWidth ? i : i2) * this.rightMarginPercent.percent);
            }
            PercentVal percentVal4 = this.bottomMarginPercent;
            if (percentVal4 != null) {
                marginLayoutParams.bottomMargin = (int) ((percentVal4.isBaseWidth ? i : i2) * this.bottomMarginPercent.percent);
            }
            PercentVal percentVal5 = this.startMarginPercent;
            if (percentVal5 != null) {
                MarginLayoutParamsCompat.setMarginStart(marginLayoutParams, (int) ((percentVal5.isBaseWidth ? i : i2) * this.startMarginPercent.percent));
            }
            PercentVal percentVal6 = this.endMarginPercent;
            if (percentVal6 != null) {
                if (!percentVal6.isBaseWidth) {
                    i = i2;
                }
                MarginLayoutParamsCompat.setMarginEnd(marginLayoutParams, (int) (i * this.endMarginPercent.percent));
            }
            if (Log.isLoggable(PercentLayoutHelper.TAG, Log.DEBUG)) {
                Log.d(PercentLayoutHelper.TAG, "after fillMarginLayoutParams: (" + marginLayoutParams.width + ", " + marginLayoutParams.height + ")");
            }
        }

        public String toString() {
            return "PercentLayoutInfo{widthPercent=" + this.widthPercent + ", heightPercent=" + this.heightPercent + ", leftMarginPercent=" + this.leftMarginPercent + ", topMarginPercent=" + this.topMarginPercent + ", rightMarginPercent=" + this.rightMarginPercent + ", bottomMarginPercent=" + this.bottomMarginPercent + ", startMarginPercent=" + this.startMarginPercent + ", endMarginPercent=" + this.endMarginPercent + ", textSizePercent=" + this.textSizePercent + ", maxWidthPercent=" + this.maxWidthPercent + ", maxHeightPercent=" + this.maxHeightPercent + ", minWidthPercent=" + this.minWidthPercent + ", minHeightPercent=" + this.minHeightPercent + ", paddingLeftPercent=" + this.paddingLeftPercent + ", paddingRightPercent=" + this.paddingRightPercent + ", paddingTopPercent=" + this.paddingTopPercent + ", paddingBottomPercent=" + this.paddingBottomPercent + ", mPreservedParams=" + this.mPreservedParams + '}';
        }

        public void restoreMarginLayoutParams(ViewGroup.MarginLayoutParams marginLayoutParams) {
            restoreLayoutParams(marginLayoutParams);
            marginLayoutParams.leftMargin = this.mPreservedParams.leftMargin;
            marginLayoutParams.topMargin = this.mPreservedParams.topMargin;
            marginLayoutParams.rightMargin = this.mPreservedParams.rightMargin;
            marginLayoutParams.bottomMargin = this.mPreservedParams.bottomMargin;
            MarginLayoutParamsCompat.setMarginStart(marginLayoutParams, MarginLayoutParamsCompat.getMarginStart(this.mPreservedParams));
            MarginLayoutParamsCompat.setMarginEnd(marginLayoutParams, MarginLayoutParamsCompat.getMarginEnd(this.mPreservedParams));
        }

        public void restoreLayoutParams(ViewGroup.LayoutParams layoutParams) {
            layoutParams.width = this.mPreservedParams.width;
            layoutParams.height = this.mPreservedParams.height;
        }
    }

}
