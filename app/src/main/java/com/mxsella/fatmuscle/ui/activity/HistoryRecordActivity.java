package com.mxsella.fatmuscle.ui.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mxsella.fat_muscle.R;
import com.mxsella.fat_muscle.databinding.ActivityHistoryRecordBinding;
import com.mxsella.fatmuscle.common.MyApplication;
import com.mxsella.fatmuscle.common.base.BaseActivity;
import com.mxsella.fatmuscle.db.bean.FatRecord;
import com.mxsella.fatmuscle.entity.ApiMemberInfo;
import com.mxsella.fatmuscle.entity.BodyParts;
import com.mxsella.fatmuscle.manager.FatConfigManager;
import com.mxsella.fatmuscle.utils.BitmapUtil;
import com.mxsella.fatmuscle.utils.MetricInchUnitUtil;
import com.mxsella.fatmuscle.utils.DensityUtil;
import com.mxsella.fatmuscle.utils.ArrayUtil;
import com.mxsella.fatmuscle.view.MeasureDividingRuleView;
import com.mxsella.fatmuscle.view.MeasureView;
import com.mxsella.fatmuscle.view.widget.FatStandardView;

import java.util.ArrayList;

public class HistoryRecordActivity extends BaseActivity<ActivityHistoryRecordBinding> implements View.OnClickListener {

    public static ArrayList<FatRecord> listObj = null;
    public static final String sHISTORY_RECORD_POSITION = "history_record_position";


    private static final String TAG = "HistoryRecordActivity";
    private EditText edit;
    private Integer familyId;
    private TextView mComparisonTv;
    private FatStandardView mFatThinessView;
    private ImageView mIvImage;
    private MeasureView mMeasureView;
    private RelativeLayout mRlScreenShots;
    private TextView mTvValue;
    private TextView maxValueTv;
    private MeasureDividingRuleView measureDividingRuleView;
    private TextView minValueTv;
    int position;
    private FatRecord.TYPE recordType;
    TextView title;
    String bodyPosition = "0";
    float x = 0.0f;
    public static void setListObj(ArrayList<FatRecord> arrayList) {
        listObj = arrayList;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        listObj = null;
    }
    @Override
    protected void initView() {

        ((ImageView) findViewById(R.id.back_icon)).setOnClickListener(view -> HistoryRecordActivity.this.finish());
        TextView textView = (TextView) findViewById(R.id.title);
        this.title = textView;
        textView.setText(getResources().getString(R.string.country_title));
        this.mIvImage = (ImageView) findViewById(R.id.iv_image);
        this.maxValueTv = (TextView) findViewById(R.id.max_tv);
        this.minValueTv = (TextView) findViewById(R.id.min_tv);
        MeasureView measureView = (MeasureView) findViewById(R.id.view_measure);
        this.mMeasureView = measureView;
        measureView.setFocusable(false);
        this.measureDividingRuleView = (MeasureDividingRuleView) findViewById(R.id.view_dividing_rule);
        this.mTvValue = (TextView) findViewById(R.id.tv_value);
        this.mComparisonTv = (TextView) findViewById(R.id.comparison);
        this.mFatThinessView = (FatStandardView) findViewById(R.id.fat_thiness);
        this.mRlScreenShots = (RelativeLayout) findViewById(R.id.rl_bottom);
        if (getIntent() != null) {
            this.position = getIntent().getIntExtra(sHISTORY_RECORD_POSITION, 0);
            initData();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_history_record;
    }

    public void initData() {
        ArrayList<FatRecord> arrayList = listObj;
        if (arrayList == null) {
            finish();
            return;
        }
        if (this.position < 0) {
            this.position = 0;
        }
        if (this.position >= arrayList.size()) {
            this.position = listObj.size() - 1;
        }
        Glide.with(this).load(listObj.get(this.position).getRecordImage()).error((int) R.drawable.icon_unkown).dontAnimate().skipMemoryCache(true).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onResourceReady(Drawable drawable, Object obj, Target<Drawable> target, DataSource dataSource, boolean z) {
                return false;
            }

            @Override
            public boolean onLoadFailed(GlideException glideException, Object obj, Target<Drawable> target, boolean z) {
                HistoryRecordActivity.this.mMeasureView.setVisibility(View.GONE);
                return false;
            }
        }).into(this.mIvImage);
        String recordValue = listObj.get(this.position).getRecordValue();
        this.bodyPosition = listObj.get(this.position).getBodyPosition();
        this.familyId = listObj.get(this.position).getFamilyId();
        int[] arrayByStr = ArrayUtil.getArrayByStr(listObj.get(this.position).getArrayAvg(), ",");
        FatRecord.TYPE type = (listObj.get(this.position).getRecordType() == null || listObj.get(this.position).getRecordType().intValue() != FatRecord.TYPE.MUSCLE.value()) ? FatRecord.TYPE.FAT : FatRecord.TYPE.MUSCLE;
        this.recordType = type;
        if (type == FatRecord.TYPE.MUSCLE) {
            this.maxValueTv.setVisibility(View.INVISIBLE);
            this.minValueTv.setVisibility(View.INVISIBLE);
            this.mFatThinessView.setVisibility(View.INVISIBLE);
            this.mComparisonTv.setVisibility(View.INVISIBLE);
        } else {
            this.maxValueTv.setVisibility(View.VISIBLE);
            this.minValueTv.setVisibility(View.VISIBLE);
            this.mFatThinessView.setVisibility(View.VISIBLE);
            this.mComparisonTv.setVisibility(View.VISIBLE);
            Integer num = this.familyId;
            if (num != null && num.intValue() > 0) {
                this.mComparisonTv.setVisibility(View.INVISIBLE);

                HistoryRecordActivity historyRecordActivity;
                int i;
                HistoryRecordActivity historyRecordActivity2;
                int i2;
                ApiMemberInfo curMeasureMember = FatConfigManager.getInstance().getCurMeasureMember();
                boolean z = curMeasureMember.getSex() == null || curMeasureMember.getSex().intValue() == 1;
                HistoryRecordActivity.this.mFatThinessView.setCurBodyPosition(Integer.parseInt(this.bodyPosition), z);
                TextView textView = HistoryRecordActivity.this.mComparisonTv;
                Context context = MyApplication.getInstance();
                Object[] objArr = new Object[2];
                if (HistoryRecordActivity.this.recordType == FatRecord.TYPE.MUSCLE) {
                    historyRecordActivity = HistoryRecordActivity.this;
                    i = R.string.muscle;
                } else {
                    historyRecordActivity = HistoryRecordActivity.this;
                    i = R.string.fat;
                }
                objArr[0] = historyRecordActivity.getString(i);
                if (z) {
                    historyRecordActivity2 = HistoryRecordActivity.this;
                    i2 = R.string.boy;
                } else {
                    historyRecordActivity2 = HistoryRecordActivity.this;
                    i2 = R.string.girl;
                }
                objArr[1] = historyRecordActivity2.getString(i2);
                textView.setText(context.getString(R.string.app_measure_resule_bottom_comparison, objArr));
                if (Integer.parseInt(HistoryRecordActivity.this.bodyPosition) != 11) {
                    HistoryRecordActivity.this.mComparisonTv.setVisibility(View.VISIBLE);
                }
            } else {
                boolean z = false;
                this.mFatThinessView.setCurBodyPosition(Integer.parseInt(this.bodyPosition), z);
                TextView textView = this.mComparisonTv;
                Object[] objArr = new Object[2];
                objArr[0] = getString(this.recordType == FatRecord.TYPE.MUSCLE ? R.string.muscle : R.string.fat);
                objArr[1] = getString(z ? R.string.boy : R.string.girl);
                textView.setText(getString(R.string.app_measure_resule_bottom_comparison, objArr));
            }
        }
        this.mMeasureView.setArray(arrayByStr, this.recordType);
        BodyParts curBodyParts = FatConfigManager.getInstance().getCurBodyParts(Integer.parseInt(this.bodyPosition), FatConfigManager.getInstance().isBoy(), this.recordType == FatRecord.TYPE.FAT);
        Integer depth = listObj.get(this.position).getDepth();
        Integer valueOf = Integer.valueOf(listObj.get(this.position).getOcxo() == null ? 32 : listObj.get(this.position).getOcxo().intValue());
        Integer valueOf2 = Integer.valueOf(listObj.get(this.position).getBitmapHight() == null ? BitmapUtil.sBitmapHight : listObj.get(this.position).getBitmapHight().intValue());
        if (recordValue == null) {
            finish();
            return;
        }
        if (depth == null) {
            depth = 3;
        }

        String recordValue2 = listObj.get(this.position).getRecordValue();
        if (arrayByStr != null) {
            float algoDepth = FatConfigManager.getInstance().getAlgoDepth(valueOf.intValue(), depth.intValue(), valueOf2.intValue());
            String unitStr = MetricInchUnitUtil.getUnitStr((ArrayUtil.getMaxValue(arrayByStr) * algoDepth) / valueOf2.intValue());
            String unitStr2 = MetricInchUnitUtil.getUnitStr((ArrayUtil.getMinValue(arrayByStr) * algoDepth) / valueOf2.intValue());
            this.maxValueTv.setText(getString(R.string.max_value_lable, new Object[]{unitStr}));
            this.minValueTv.setText(getString(R.string.min_value_lable, new Object[]{unitStr2}));
        }
        this.mTvValue.setText(getSpannableStringFatValue(MetricInchUnitUtil.getUnitStr(Float.parseFloat(recordValue.substring(0, recordValue.length() - 2))), curBodyParts.getName()));
        this.mFatThinessView.setCurFatValue(Float.parseFloat(recordValue2.replace("mm", "").

                replace("cm", "")));
        this.title.setText(listObj.get(this.position).

                getRecordDate());
        this.mMeasureView.setPosition(Float.parseFloat(recordValue2.replace("mm", "")));
        this.mMeasureView.setDepth(depth.intValue(), valueOf.intValue(), valueOf2.intValue());
        this.measureDividingRuleView.setInit(this.mIvImage.getWidth(), this.mIvImage.getHeight(), depth.intValue());
        this.measureDividingRuleView.setOcxo(valueOf.intValue(), valueOf2.intValue());
        if (Integer.parseInt(this.bodyPosition) == 1 || Integer.parseInt(this.bodyPosition) == 11) {
            this.mFatThinessView.setVisibility(View.GONE);
            this.mComparisonTv.setVisibility(View.GONE);
        }

        binding.scrollView.
                setOnTouchListener((view, motionEvent) ->

                {
                    Log.i(HistoryRecordActivity.TAG, "===============event=" + motionEvent.getAction() + " x=" + motionEvent.getX());
                    int action = motionEvent.getAction();
                    if (action == 0) {
                        HistoryRecordActivity.this.x = motionEvent.getX();
                        return false;
                    } else if (action != 1) {
                        return false;
                    } else {
                        if (HistoryRecordActivity.this.x - motionEvent.getX() > 200.0f) {
                            Log.i(HistoryRecordActivity.TAG, "===============left=" + motionEvent.getAction() + " x=" + motionEvent.getX());
                            HistoryRecordActivity.this.position++;
                            HistoryRecordActivity.this.initData();
                            return false;
                        } else if (HistoryRecordActivity.this.x - motionEvent.getX() < -200.0f) {
                            Log.i(HistoryRecordActivity.TAG, "===============right=" + motionEvent.getAction() + " x=" + motionEvent.getX());
                            HistoryRecordActivity.this.position--;
                            HistoryRecordActivity.this.initData();
                            return false;
                        } else {
                            return false;
                        }
                    }
                });
    }

    private SpannableString getSpannableStringFatValue(String str, String str2) {
        if (this.bodyPosition.equalsIgnoreCase("1")) {
            String string = getString(R.string.face_result_tip);
            SpannableString spannableString = new SpannableString(string + str + getString(R.string.fat_unit));
            spannableString.setSpan(new ForegroundColorSpan(MyApplication.getInstance().getResources().getColor(R.color.app_login_protocol_and_privacy)), string.length(), string.length() + str.length(), 33);
            spannableString.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View view) {
                }

                @Override
                public void updateDrawState(TextPaint textPaint) {
                    super.updateDrawState(textPaint);
                    textPaint.setColor(HistoryRecordActivity.this.getResources().getColor(R.color.app_login_protocol_and_privacy));
                    textPaint.setTextSize(DensityUtil.dip2px(HistoryRecordActivity.this, 24.0f));
                    textPaint.setUnderlineText(false);
                }
            }, string.length(), string.length() + str.length(), 33);
            return spannableString;
        }
        StringBuilder append = new StringBuilder().append(str2).append(" ");
        Object[] objArr = new Object[1];
        objArr[0] = getString(this.recordType == FatRecord.TYPE.MUSCLE ? R.string.muscle : R.string.fat);
        String sb = append.append(getString(R.string.app_measure_fat_thickness_value, objArr)).toString();
        SpannableString spannableString2 = new SpannableString(sb + str);
        spannableString2.setSpan(new ForegroundColorSpan(MyApplication.getInstance().getResources().getColor(R.color.app_measure_result_content)), 0, sb.length(), 33);
        spannableString2.setSpan(new ForegroundColorSpan(MyApplication.getInstance().getResources().getColor(R.color.app_login_protocol_and_privacy)), 0, str2.length(), 33);
        spannableString2.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
            }

            @Override
            public void updateDrawState(TextPaint textPaint) {
                super.updateDrawState(textPaint);
                textPaint.setColor(HistoryRecordActivity.this.getResources().getColor(R.color.app_login_protocol_and_privacy));
                textPaint.setTextSize(DensityUtil.dip2px(MyApplication.getInstance(), 24.0f));
                textPaint.setUnderlineText(false);
            }
        }, sb.length(), sb.length() + str.length(), 33);
        return spannableString2;
    }

    @Override
    public void onClick(View view) {

    }
}