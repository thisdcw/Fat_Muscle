package com.mxsella.fatmuscle.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mxsella.fat_muscle.R;
import com.mxsella.fatmuscle.db.bean.FatRecord;
import com.mxsella.fatmuscle.sdk.fat.manager.FatConfigManager;
import com.mxsella.fatmuscle.sdk.fat.utils.MetricInchUnitUtil;

import java.util.ArrayList;
import java.util.List;

public class HistoryListAdapter extends BaseAdapter implements SpinnerAdapter {

    private ArrayList<FatRecord> data = new ArrayList<>();
    private Context mContext;
    private View.OnClickListener mDeleteOnClickListener;
    @Override
    public long getItemId(int i) {
        return i;
    }

    public HistoryListAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return this.data.size();
    }

    @Override
    public Object getItem(int i) {
        return this.data.get(i);
    }

    public void removeFatReCord(FatRecord fatRecord) {
        this.data.remove(fatRecord);
    }

    public void setDeleteOnClickListener(View.OnClickListener onClickListener) {
        this.mDeleteOnClickListener = onClickListener;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2;
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view2 = viewHolder.initView(viewGroup);
            view2.setTag(viewHolder);
        } else {
            view2 = view;
            viewHolder = (ViewHolder) view.getTag();
        }
        ArrayList<FatRecord> arrayList = this.data;
        if (arrayList == null) {
            return view2;
        }
        FatRecord fatRecord = arrayList.get(i);
        viewHolder.timeTv.setText(fatRecord.getRecordDate());
        String recordValue = fatRecord.getRecordValue();
        viewHolder.typeTv.setText((fatRecord.getRecordType() == null || fatRecord.getRecordType().intValue() != FatRecord.TYPE.MUSCLE.value()) ? R.string.fat : R.string.muscle);
        viewHolder.valueTv.setText(MetricInchUnitUtil.getUnitStr(Float.parseFloat(recordValue.substring(0, recordValue.length() - 2))));
        viewHolder.positionTv.setText(FatConfigManager.getInstance().getCurBodyParts(Integer.parseInt(fatRecord.getBodyPosition()), FatConfigManager.getInstance().isBoy(), fatRecord.getRecordType().intValue() == FatRecord.TYPE.FAT.value()).getName());
        ImageView imageView = viewHolder.bitmatIv;
        viewHolder.deleteIv.setTag(Integer.valueOf(i));
        viewHolder.deleteIv.setOnClickListener(this.mDeleteOnClickListener);
        Glide.with(this.mContext).load(fatRecord.getRecordImage()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).placeholder(R.drawable.icon_unkown).error(R.drawable.icon_unkown).dontAnimate().into(imageView);
        return view2;
    }

    public void setData(List<FatRecord> arrayList) {
        this.data.clear();
        this.data.addAll(arrayList);
        notifyDataSetChanged();
    }

    public ArrayList<FatRecord> getList() {
        return this.data;
    }

    private class ViewHolder {
        public ImageView bitmatIv;
        private ImageView deleteIv;
        public TextView positionTv;
        public TextView timeTv;
        public TextView typeTv;
        public TextView valueTv;

        private ViewHolder() {
        }

        public View initView(ViewGroup viewGroup) {
            View inflate = LayoutInflater.from(HistoryListAdapter.this.mContext).inflate(R.layout.adapt_history, viewGroup, false);
            this.timeTv = (TextView) inflate.findViewById(R.id.tv_time);
            this.valueTv = (TextView) inflate.findViewById(R.id.tv_value);
            this.bitmatIv = (ImageView) inflate.findViewById(R.id.iv_image);
            this.positionTv = (TextView) inflate.findViewById(R.id.tv_position);
            this.typeTv = (TextView) inflate.findViewById(R.id.tv_type);
            this.deleteIv = (ImageView) inflate.findViewById(R.id.delete);
            return inflate;
        }
    }

}
