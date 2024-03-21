package com.mxsella.fatmuscle.ui.activity;

import androidx.databinding.DataBindingUtil;

import com.mxsella.fat_muscle.R;
import com.mxsella.fat_muscle.databinding.ActivityHostBinding;
import com.mxsella.fatmuscle.common.base.BaseActivity;

public class HostActivity extends BaseActivity {

    ActivityHostBinding hostBinding;

    @Override
    protected void initView() {
        hostBinding = DataBindingUtil.setContentView(this, R.layout.activity_host);
    }
}