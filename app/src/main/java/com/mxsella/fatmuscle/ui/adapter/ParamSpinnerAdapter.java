package com.mxsella.fatmuscle.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import com.mxsella.fatmuscle.sdk.util.DensityUtil;

public class ParamSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {
    int[] data;
    Context mContext;

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    public ParamSpinnerAdapter(Context context, int[] iArr) {
        this.data = iArr;
        this.mContext = context;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.data.length;
    }

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return Integer.valueOf(this.data[i]);
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView = new TextView(this.mContext);
        textView.setTextSize(16.0f);
        textView.setTextColor(ViewCompat.MEASURED_STATE_MASK);
        if (i >= this.data.length) {
            textView.setText("====");
        } else {
            textView.setText(this.data[i] + "");
        }
        textView.setHeight(DensityUtil.dip2px(this.mContext, 30.0f));
        textView.setWidth(DensityUtil.dip2px(this.mContext, 60.0f));
        textView.setPadding(DensityUtil.dip2px(this.mContext, 5.0f), 0, 0, 0);
        textView.setGravity(16);
        return textView;
    }

    public void setData(int[] iArr) {
        this.data = iArr;
    }
}
