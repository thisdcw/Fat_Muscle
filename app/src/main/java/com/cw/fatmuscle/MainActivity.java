package com.cw.fatmuscle;

import androidx.databinding.DataBindingUtil;

import com.cw.fat_muscle.R;
import com.cw.fat_muscle.databinding.ActivityMainBinding;
import com.cw.fatmuscle.common.base.BaseActivity;
import com.cw.fatmuscle.ui.activity.FatMeasurePlusActivity;
import com.cw.fatmuscle.ui.activity.MuscleMeasureResultActivity;

public class MainActivity extends BaseActivity {

    ActivityMainBinding mainBinding;

    @Override
    protected void initView() {
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.fat.setOnClickListener(v -> {
            navTo(FatMeasurePlusActivity.class);
        });
        mainBinding.muscle.setOnClickListener(v -> {
            navTo(MuscleMeasureResultActivity.class);
        });
    }

}