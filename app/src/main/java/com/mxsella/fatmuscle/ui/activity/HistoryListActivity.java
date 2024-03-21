package com.mxsella.fatmuscle.ui.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.databinding.DataBindingUtil;

import com.mxsella.fat_muscle.R;
import com.mxsella.fat_muscle.databinding.ActivityHistoryListBinding;
import com.mxsella.fatmuscle.common.MyApplication;
import com.mxsella.fatmuscle.common.base.BaseActivity;
import com.mxsella.fatmuscle.db.bean.FatRecord;
import com.mxsella.fatmuscle.sdk.util.ThreadUtils;
import com.mxsella.fatmuscle.ui.adapter.HistoryListAdapter;

import java.util.List;

public class HistoryListActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    ActivityHistoryListBinding historyListBinding;
    HistoryListAdapter listAdapter;

    @Override
    protected void initView() {
        historyListBinding = DataBindingUtil.setContentView(this, R.layout.activity_history_list);
        listAdapter = new HistoryListAdapter(this);
        historyListBinding.lvHistory.setAdapter(listAdapter);
        historyListBinding.lvHistory.setOnItemClickListener(this);
        findViewById(R.id.back_icon).setOnClickListener(v -> {
            HistoryListActivity.this.finish();
        });
        ThreadUtils.execute(() -> {
            Log.d("record", "获取历史记录");
            List<FatRecord> allFatRecord = MyApplication.getInstance().db.fatRecordDao().getAllFatRecord();
            if (allFatRecord.size() < 1) {
                Log.d("record", "没有历史记录");
                historyListBinding.tvEmptyHint.setVisibility(View.VISIBLE);
            } else {
                historyListBinding.tvEmptyHint.setVisibility(View.GONE);
                for (FatRecord fatRecord : allFatRecord) {
                    Log.d("record", fatRecord.toString());
                }
                listAdapter.setData(allFatRecord);
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("history", "进入详细记录");
        Intent intent = new Intent(this, HistoryRecordActivity.class);
        HistoryRecordActivity.setListObj(this.listAdapter.getList());
        intent.putExtra(HistoryRecordActivity.sHISTORY_RECORD_POSITION, position);
        startActivity(intent);
    }
}