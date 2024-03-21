package com.mxsella.fatmuscle.view.widget;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.customview.widget.ViewDragHelper;

import org.opencv.videoio.Videoio;

public class PullUpDragLayout extends ViewGroup {
    private static final String TAG = "PullUpDragLayout";
    public static boolean isSlide = true;
    private boolean isOpen;
    private Point mAutoBackBottomPos;
    private Point mAutoBackTopPos;
    private int mBottomBorderHeigth;
    private float mBottomBorderHeigthPercent;
    private View mBottomView;
    private int mBoundTopY;
    ViewDragHelper.Callback mCallback;
    private View mContentView;
    LayoutInflater mLayoutInflater;
    private OnStateListener mOnStateListener;
    private OnScrollChageListener mScrollChageListener;
    private ViewDragHelper mViewDragHelper;

    /* loaded from: classes.dex */
    public interface OnScrollChageListener {
        void onScrollChange(float f);
    }

    /* loaded from: classes.dex */
    public interface OnStateListener {
        void close();

        void open();
    }

    public void setOnStateListener(OnStateListener onStateListener) {
        this.mOnStateListener = onStateListener;
    }

    public void setScrollChageListener(OnScrollChageListener onScrollChageListener) {
        this.mScrollChageListener = onScrollChageListener;
    }

    public PullUpDragLayout(Context context) {
        this(context, null, 0);
    }

    public PullUpDragLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PullUpDragLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mBottomBorderHeigth = Videoio.CAP_QT;
        this.mBottomBorderHeigthPercent = 0.2f;
        this.mAutoBackBottomPos = new Point();
        this.mAutoBackTopPos = new Point();
        this.mCallback = new ViewDragHelper.Callback() { // from class: com.marvoto.fat.widget.PullUpDragLayout.1
            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public boolean tryCaptureView(View view, int i2) {
                Log.i(PullUpDragLayout.TAG, "tryCaptureView: ");
                return PullUpDragLayout.this.mBottomView == view;
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public int getViewHorizontalDragRange(View view) {
                Log.i(PullUpDragLayout.TAG, "getViewHorizontalDragRange: ");
                return PullUpDragLayout.this.getMeasuredWidth() - view.getMeasuredWidth();
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public int getViewVerticalDragRange(View view) {
                Log.i(PullUpDragLayout.TAG, "getViewVerticalDragRange: ");
                return PullUpDragLayout.this.getMeasuredHeight() - view.getMeasuredHeight();
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public int clampViewPositionHorizontal(View view, int i2, int i3) {
                Log.i(PullUpDragLayout.TAG, "clampViewPositionHorizontal: " + view.getId());
                int paddingLeft = PullUpDragLayout.this.getPaddingLeft();
                return Math.min(Math.max(i2, paddingLeft), (PullUpDragLayout.this.getWidth() - PullUpDragLayout.this.mBottomView.getWidth()) - paddingLeft);
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public int clampViewPositionVertical(View view, int i2, int i3) {
                Log.i(PullUpDragLayout.TAG, "clampViewPositionVertical:top: " + i2 + ",dy:" + i3);
                return Math.min((int) (PullUpDragLayout.this.mContentView.getHeight() - (PullUpDragLayout.this.getMeasuredHeight() * PullUpDragLayout.this.mBottomBorderHeigthPercent)), Math.max(i2, PullUpDragLayout.this.mContentView.getHeight() - PullUpDragLayout.this.mBottomView.getHeight()));
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public void onViewPositionChanged(View view, int i2, int i3, int i4, int i5) {
                Log.i(PullUpDragLayout.TAG, "onViewPositionChanged: " + view.getId());
                if (view == PullUpDragLayout.this.mBottomView) {
                    float height = PullUpDragLayout.this.mContentView.getHeight() - PullUpDragLayout.this.mBottomView.getHeight();
                    float height2 = 1.0f - ((i3 - height) / ((PullUpDragLayout.this.mContentView.getHeight() - (PullUpDragLayout.this.getMeasuredHeight() * PullUpDragLayout.this.mBottomBorderHeigthPercent)) - height));
                    if (PullUpDragLayout.this.mScrollChageListener != null) {
                        PullUpDragLayout.this.mScrollChageListener.onScrollChange(height2);
                    }
                }
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public void onViewReleased(View view, float f, float f2) {
                Log.i(PullUpDragLayout.TAG, "onViewReleased:===yvel=" + f2);
                if (view == PullUpDragLayout.this.mBottomView) {
                    if (view.getY() < PullUpDragLayout.this.mBoundTopY || f2 <= -1000.0f) {
                        PullUpDragLayout.this.mViewDragHelper.settleCapturedViewAt(PullUpDragLayout.this.mAutoBackTopPos.x, PullUpDragLayout.this.mAutoBackTopPos.y);
                        PullUpDragLayout.this.isOpen = true;
                        if (PullUpDragLayout.this.mOnStateListener != null) {
                            PullUpDragLayout.this.mOnStateListener.open();
                        }
                    } else if (view.getY() >= PullUpDragLayout.this.mBoundTopY || f2 >= 1000.0f) {
                        PullUpDragLayout.this.mViewDragHelper.settleCapturedViewAt(PullUpDragLayout.this.mAutoBackBottomPos.x, PullUpDragLayout.this.mAutoBackBottomPos.y);
                        PullUpDragLayout.this.isOpen = false;
                        if (PullUpDragLayout.this.mOnStateListener != null) {
                            PullUpDragLayout.this.mOnStateListener.close();
                        }
                    }
                    PullUpDragLayout.this.invalidate();
                }
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public void onEdgeDragStarted(int i2, int i3) {
                super.onEdgeDragStarted(i2, i3);
                PullUpDragLayout.this.mViewDragHelper.captureChildView(PullUpDragLayout.this.mBottomView, i3);
            }
        };
        Log.i(TAG, "PullUpDragLayout: ");
        init(context);
        initCustomAttrs(context, attributeSet);
    }

    private void init(Context context) {
        this.mLayoutInflater = LayoutInflater.from(context);
        ViewDragHelper create = ViewDragHelper.create(this, 1.0f, this.mCallback);
        this.mViewDragHelper = create;
        create.setEdgeTrackingEnabled(8);
    }

    public boolean isOpen() {
        return this.isOpen;
    }

    public void toggleBottomView() {
        if (this.isOpen) {
            this.mViewDragHelper.smoothSlideViewTo(this.mBottomView, this.mAutoBackBottomPos.x, this.mAutoBackBottomPos.y);
            OnStateListener onStateListener = this.mOnStateListener;
            if (onStateListener != null) {
                onStateListener.close();
            }
        } else {
            this.mViewDragHelper.smoothSlideViewTo(this.mBottomView, this.mAutoBackTopPos.x, this.mAutoBackTopPos.y);
            OnStateListener onStateListener2 = this.mOnStateListener;
            if (onStateListener2 != null) {
                onStateListener2.open();
            }
        }
        invalidate();
        this.isOpen = !this.isOpen;
    }

    private void initCustomAttrs(Context context, AttributeSet attributeSet) {
//        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.PullUpDragLayout);
//        if (obtainStyledAttributes != null) {
//            if (obtainStyledAttributes.hasValue(R.styleable.PullUpDragLayout_PullUpDrag_ContentView)) {
//                inflateContentView(obtainStyledAttributes.getResourceId(R.styleable.PullUpDragLayout_PullUpDrag_ContentView, 0));
//            }
//            if (obtainStyledAttributes.hasValue(R.styleable.PullUpDragLayout_PullUpDrag_BottomView)) {
//                inflateBottomView(obtainStyledAttributes.getResourceId(R.styleable.PullUpDragLayout_PullUpDrag_BottomView, 0));
//            }
//            if (obtainStyledAttributes.hasValue(R.styleable.PullUpDragLayout_PullUpDrag_BottomBorderHeigth)) {
//                this.mBottomBorderHeigth = (int) obtainStyledAttributes.getDimension(R.styleable.PullUpDragLayout_PullUpDrag_BottomBorderHeigth, 20.0f);
//            }
//            if (obtainStyledAttributes.hasValue(R.styleable.PullUpDragLayout_PullUpDrag_BottomBorderHeigthPercent)) {
//                this.mBottomBorderHeigthPercent = obtainStyledAttributes.getFloat(R.styleable.PullUpDragLayout_PullUpDrag_BottomBorderHeigthPercent, 20.0f);
//            }
//            obtainStyledAttributes.recycle();
//        }
    }

    private void inflateContentView(int i) {
        this.mContentView = this.mLayoutInflater.inflate(i, (ViewGroup) this, true);
    }

    private void inflateBottomView(int i) {
        this.mBottomView = this.mLayoutInflater.inflate(i, (ViewGroup) this, true);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        Log.i(TAG, "onTouchEvent: onMeasure=" + isSlide);
        this.mContentView = getChildAt(0);
        View childAt = getChildAt(1);
        this.mBottomView = childAt;
        measureChild(childAt, i, i2);
        measureChild(this.mContentView, i, i2);
        setMeasuredDimension(MeasureSpec.getSize(i), ((int) (this.mBottomView.getMeasuredHeight() * 0.75f)) + this.mContentView.getMeasuredHeight() + getPaddingBottom() + getPaddingTop());
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        Log.i(TAG, "onTouchEvent: onLayout=" + isSlide);
        if (isSlide) {
            this.mContentView = getChildAt(0);
            this.mBottomView = getChildAt(1);
            this.mContentView.layout(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), this.mContentView.getMeasuredHeight());
            this.mBottomView.layout(getPaddingLeft(), (int) (this.mContentView.getHeight() - (getMeasuredHeight() * this.mBottomBorderHeigthPercent)), getWidth() - getPaddingRight(), (int) (getMeasuredHeight() - (getMeasuredHeight() * this.mBottomBorderHeigthPercent)));
            this.mAutoBackBottomPos.x = this.mBottomView.getLeft();
            this.mAutoBackBottomPos.y = this.mBottomView.getTop();
            this.mAutoBackTopPos.x = this.mBottomView.getLeft();
            this.mAutoBackTopPos.y = this.mContentView.getHeight() - this.mBottomView.getHeight();
            this.mBoundTopY = (this.mContentView.getHeight() - (this.mBottomView.getHeight() / 2)) - 300;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (isSlide) {
            return this.mViewDragHelper.shouldInterceptTouchEvent(motionEvent);
        }
        Log.i(TAG,"onInterceptTouchEvent: " + motionEvent.getEdgeFlags());
        return super.onInterceptTouchEvent(motionEvent);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        Log.i(TAG, "onTouchEvent: isSlide=" + isSlide);
        if (isSlide) {
            this.mViewDragHelper.processTouchEvent(motionEvent);
            return true;
        }
        return true;
    }

    @Override // android.view.View
    public void computeScroll() {
        if (this.mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

}
