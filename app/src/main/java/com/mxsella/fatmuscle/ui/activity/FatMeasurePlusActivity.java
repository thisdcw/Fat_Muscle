package com.mxsella.fatmuscle.ui.activity;


import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

import com.mxsella.fat_muscle.R;
import com.mxsella.fat_muscle.databinding.ActivityFatMeasurePlusBinding;
import com.mxsella.fatmuscle.common.MyApplication;
import com.mxsella.fatmuscle.common.base.BaseActivity;
import com.mxsella.fatmuscle.common.Constant;
import com.mxsella.fatmuscle.common.MxsellaConstant;
import com.mxsella.fatmuscle.entity.BitmapMsg;
import com.mxsella.fatmuscle.entity.DeviceMsg;
import com.mxsella.fatmuscle.db.bean.FatRecord;
import com.mxsella.fatmuscle.manager.FatConfigManager;
import com.mxsella.fatmuscle.manager.MxsellaDeviceManager;
import com.mxsella.fatmuscle.utils.BitmapUtil;
import com.mxsella.fatmuscle.utils.MetricInchUnitUtil;
import com.mxsella.fatmuscle.utils.OpenCvMeasureUtil;
import com.mxsella.fatmuscle.utils.DensityUtil;
import com.mxsella.fatmuscle.utils.SystemParamUtil;
import com.mxsella.fatmuscle.utils.ThreadUtils;
import com.mxsella.fatmuscle.utils.ToastUtil;
import com.mxsella.fatmuscle.utils.ArrayUtil;
import com.mxsella.fatmuscle.utils.DateUtil;
import com.mxsella.fatmuscle.view.CustomVideoView;
import com.mxsella.fatmuscle.view.dialog.DialogManager;

public class FatMeasurePlusActivity extends BaseActivity<ActivityFatMeasurePlusBinding> implements MxsellaDeviceManager.DeviceInterface {
    private static final String TAG = "MuscleMeasureResultActivity";
    private int count = 0;
    private boolean isAgainMeasure = false;
    private boolean isShow = false;
    private BitmapMsg mLastBitmapMsg = new BitmapMsg();
    private int measureFailCount = 0;
    private int thresholdValue = 3;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 2) {
                showResult((BitmapMsg) message.obj, false);
            } else if (message.what == 3) {
                showResult((BitmapMsg) message.obj, true);
            } else if (message.what == 5) {
                binding.viewMeasure.setShowUltrasoundImg(true);
            } else {
                int i = message.what;
            }
        }
    };

    Handler handler = new Handler(Looper.getMainLooper(), message -> {
        BitmapMsg bitmapMsg = new BitmapMsg();
        bitmapMsg.setMsgId(Constant.DEVICE_IMAGE_DATA);
        bitmapMsg.setState(BitmapMsg.State.END);
        onMessage(bitmapMsg);
        return true;
    });
    CustomVideoView customVideoView = null;
    CustomVideoView customLineVideoView = null;

    public void toShowMeasureResult() {
        binding.statusTip.setVisibility(View.GONE);
        setVideoVisibility(false);
        binding.analysisButton.setVisibility(View.VISIBLE);
        measureFailCount = 0;
        binding.viewMeasure.setShowUltrasoundImg(true);
        if (FatConfigManager.getInstance().isAutoMeasure()) {
            binding.resultRl.setVisibility(View.VISIBLE);
            this.binding.analysisButton.setText(R.string.analysis);
            return;
        }
        binding.viewMeasure.setShowUltrasoundImg(false);
        binding.otherGuide.setVisibility(View.VISIBLE);
        binding.analysisButton.setText(R.string.to_measure);
        binding.otherGuide.setText(getString(R.string.part_fat_measure, new Object[]{FatConfigManager.getInstance().getCurBodyParts().getName()}));
        binding.otherGuide.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getDrawable(FatConfigManager.getInstance().getCurBodyParts().getMusclePartIcon().intValue()));
    }

    public void close(View view) {
        finish();
    }

    public void showResult(BitmapMsg bitmapMsg, boolean z) {
        this.mLastBitmapMsg = bitmapMsg;
        float fatThickness = bitmapMsg.getFatThickness();
        Log.i(TAG, "=============showResult=fatthickness" + fatThickness);
        if (fatThickness < 0.0f) {
            this.measureFailCount++;
            setVideoVisibility(true);
            if (this.measureFailCount > this.thresholdValue) {
                if (FatConfigManager.getInstance().getCurBodyPositionIndex() == 6 && !FatConfigManager.getInstance().isAutoMeasure()) {
                    toShowMeasureResult();
                    MxsellaDeviceManager.getInstance().setEnd(true);
                    return;
                }
                MxsellaDeviceManager.getInstance().setEnd(true);
                setVideoVisibility(false);
                binding.statusTip.setVisibility(View.GONE);
                if (fatThickness == -3.0f) {
                    binding.showReason.setText(R.string.too_week_result);
                    return;
                } else if (FatConfigManager.getInstance().isAutoMeasure()) {
                    binding.showReason.setText(MxsellaDeviceManager.getInstance().isToBusinessVersion() ? R.string.un_found_result_manually : R.string.un_found_result);
                    if (MxsellaDeviceManager.getInstance().isToBusinessVersion()) {
                        this.binding.analysisButton.setVisibility(View.VISIBLE);
                    } else {
                        this.binding.analysisButton.setVisibility(View.GONE);
                    }
                    this.binding.analysisButton.setText(R.string.manual_measure);
                }
                showOpenMeasureLineDialog();
            }
        } else {
            toShowMeasureResult();
            if (!FatConfigManager.getInstance().isAutoMeasure()) {
                return;
            }
        }
        if (fatThickness == -1.0f || fatThickness == -2.0f || fatThickness == -3.0f || fatThickness == -4.0f || !z) {
            return;
        }
        this.binding.viewMeasure.setDepth(FatConfigManager.getInstance().getCurDeviceDepth(), MxsellaDeviceManager.getInstance().getOcxo(), BitmapUtil.sBitmapHight);
        binding.viewDividingRule.setOcxo(MxsellaDeviceManager.getInstance().getOcxo(), BitmapUtil.sBitmapHight);
        this.binding.viewMeasure.setPosition(fatThickness);
        this.mHandler.sendEmptyMessageDelayed(6, 1000L);
        binding.viewDividingRule.setInit(this.binding.ivImage.getWidth(), this.binding.ivImage.getHeight(), FatConfigManager.getInstance().getCurDeviceDepth());
        this.binding.ivImage.setImageBitmap(bitmapMsg.getBitmap());
        showOpenMeasureLineDialog();
    }

    private void setVideoVisibility(boolean z) {
        if (z) {
            playGuideVideo();
            return;
        }
        this.customVideoView.setVisibility(View.GONE);
        CustomVideoView customVideoView = this.customVideoView;
        if (customVideoView == null || !customVideoView.isPlaying()) {
            return;
        }
        this.customVideoView.stopPlayback();

    }

    private void warningDialog(float f, BitmapMsg bitmapMsg) {
        if (isFinishing()) {
            return;
        }
        SpannableString spannableString = new SpannableString(getString(R.string.measure_warning_content));
        final DialogManager dialogManager = new DialogManager(this, getString(R.string.measure_warning_tip), spannableString, this.mContext.getString(R.string.save), this.mContext.getString(R.string.ignore));
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint textPaint) {
                super.updateDrawState(textPaint);
                textPaint.setColor(getResources().getColor(R.color.grey));
                textPaint.setTextSize(DensityUtil.dip2px(mContext, 13.0f));
            }

            @Override
            public void onClick(View view) {
                toTeach();
                dialogManager.dismissDialog();
            }
        }, spannableString.toString().indexOf("\n\n") + 1, spannableString.length(), 33);
        dialogManager.setVerticalScreen(true);
        dialogManager.setOnDiaLogListener(new DialogManager.OnDialogListener() {
            @Override
            public void dialogBtnRightOrSingleListener(View view, DialogInterface dialogInterface, int i) {
            }

            @Override
            public void dialogBtnLeftListener(View view, DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                mLastBitmapMsg.setFatThickness(-3.0f);
            }
        });
        dialogManager.showDialog();
    }

    public void toTeach() {
        //TODO 使用教学
    }

    public void analysis(View view) {
        if (FatConfigManager.getInstance().isAutoMeasure()) {
            if (binding.rlUnFound.getVisibility() == View.VISIBLE) {
                binding.analysisText.setVisibility(View.GONE);
                int[] iArr = {0, 100, 25, 88, 50, 88, 75, 86, 100, 89, 125, 87, 149, 89};
                binding.viewMeasure.setArray(iArr, FatRecord.TYPE.FAT);
                float avgValue = (ArrayUtil.avgValue(iArr) * FatConfigManager.getInstance().getAlgoDepth(MxsellaDeviceManager.getInstance().getOcxo())) / BitmapUtil.sBitmapHight;
                mLastBitmapMsg.setArray(iArr);
                mLastBitmapMsg.setFatThickness(avgValue);
                binding.resultValue.setVisibility(View.VISIBLE);
                binding.resultValue.setText(getSpannableStringFatValue(avgValue));
                binding.fatThiness.setCurFatValue(avgValue);
                binding.viewMeasure.setShowUltrasoundImg(true);
                binding.viewMeasure.setPosition(avgValue);
                binding.resultRl.setVisibility(View.VISIBLE);
                binding.rlUnFound.setVisibility(View.GONE);
                if (measureFailCount > 0) {
                    if (!FatConfigManager.getInstance().isFangkeMode()) {
                        binding.showSaveRl.setVisibility(View.VISIBLE);
                        return;
                    }
                    binding.analysisButton.setVisibility(View.VISIBLE);
                    binding.analysisButton.setText(R.string.analysis);
                    return;
                }
                return;
            } else if (binding.viewMeasure.isShowUltrasoundImg()) {
                binding.resultRl.setVisibility(View.GONE);
                binding.rlAns.setVisibility(View.VISIBLE);
                binding.viewMeasure.setShowUltrasoundImg(false);
                return;
            } else {
                binding.resultRl.setVisibility(View.VISIBLE);
                binding.rlAns.setVisibility(View.GONE);
                binding.viewMeasure.setShowUltrasoundImg(true);
                return;
            }
        }
        binding.otherGuide.setVisibility(View.GONE);
        binding.analysisButton.setVisibility(View.GONE);
        int[] iArr2 = {0, 90, 75, 90, 149, 90};
        binding.viewMeasure.setArray(iArr2, FatRecord.TYPE.MUSCLE);
        float avgValue2 = (ArrayUtil.avgValue(iArr2) * FatConfigManager.getInstance().getAlgoDepth(MxsellaDeviceManager.getInstance().getOcxo())) / BitmapUtil.sBitmapHight;
        mLastBitmapMsg.setArray(iArr2);
        mLastBitmapMsg.setFatThickness(avgValue2);
        binding.resultValue.setVisibility(View.VISIBLE);
        binding.resultValue.setText(getSpannableStringFatValue(avgValue2));
        binding.fatThiness.setCurFatValue(avgValue2);
        binding.viewMeasure.setShowUltrasoundImg(true);
        binding.viewMeasure.setPosition(avgValue2);
        binding.resultRl.setVisibility(View.VISIBLE);
        binding.rlUnFound.setVisibility(View.GONE);
        if (FatConfigManager.getInstance().isFangkeMode()) {
            return;
        }
        binding.showSaveRl.setVisibility(View.VISIBLE);

    }

    public void save(View view) {
        FatMeasurePlusActivity.this.measureFailCount = 0;
        if (FatConfigManager.getInstance().isAutoMeasure()) {
            binding.rlAns.setVisibility(View.VISIBLE);
            binding.analysisText.setText(R.string.analysis);
        }
        binding.showSaveRl.setVisibility(View.GONE);
    }

    public void cancle(View view) {
        binding.resultRl.setVisibility(View.GONE);
        binding.viewMeasure.setShowUltrasoundImg(true);
        binding.viewMeasure.setPosition(0.0f);
        binding.showSaveRl.setVisibility(View.GONE);
        if (FatConfigManager.getInstance().isAutoMeasure()) {
            binding.rlUnFound.setVisibility(View.VISIBLE);
            binding.rlAns.setVisibility(View.VISIBLE);
        } else {
            toShowMeasureResult();
        }
        mLastBitmapMsg.setFatThickness(0.0f);
    }

    public void share(View view) {
        //TODO 分享
    }

    @Override
    protected void initView() {
        measureFailCount = 0;

        getWindow().addFlags(128);
        binding.showSaveRl.setClickable(true);
        binding.fatThiness.setShowValue(false);
        binding.viewDividingRule.setOcxo(MxsellaDeviceManager.getInstance().getOcxo(), BitmapUtil.sBitmapHight);
        binding.resultValue.setText(getSpannableStringFatValue(0.0f));

        binding.viewDividingRule.setOcxo(MxsellaDeviceManager.getInstance().getOcxo(), BitmapUtil.sBitmapHight);
        binding.bodyPosition.setImageDrawable(getResources().getDrawable(FatConfigManager.getInstance().getCurBodyParts().getIconNormal().intValue()));
        if (FatConfigManager.getInstance().isAutoMeasure()) {
            binding.note.setText(getString(R.string.result_note, new Object[]{FatConfigManager.getInstance().getCurBodyParts().getName()}));
        } else {
            binding.note.setText(getString(R.string.manual_result_note, new Object[]{FatConfigManager.getInstance().getCurBodyParts().getName()}));
        }
        MxsellaDeviceManager.getInstance().registerDeviceInterface(this);
        Log.i(TAG, "initGetData: " + this.isShow);
        binding.resultValue.setVisibility(View.INVISIBLE);
        binding.viewDividingRule.setInit(binding.ivImage.getWidth(), binding.ivImage.getHeight(), FatConfigManager.getInstance().getCurDeviceDepth());
        binding.viewDividingRule.setInit(binding.ivImage.getWidth(), binding.ivImage.getHeight(), FatConfigManager.getInstance().getCurDeviceDepth());
        if (FatConfigManager.getInstance().getCurBodyPositionIndex() == 1 || FatConfigManager.getInstance().getCurBodyPositionIndex() == 12 || FatConfigManager.getInstance().getCurBodyPositionIndex() == 11) {
            binding.fatThiness.setVisibility(View.GONE);
        }
        if (FatConfigManager.getInstance().getCurBodyPositionIndex() == 6) {
            this.thresholdValue = 1;
        } else {
            this.thresholdValue = 3;
        }
        binding.ivImage.setImageBitmap(BitmapUtil.getImageOneDimensional());

        binding.viewMeasure.setMeasureCallBack(iArr -> {
            if (iArr == null) {
                return;
            }
            float algoDepth = FatConfigManager.getInstance().getAlgoDepth(MxsellaDeviceManager.getInstance().getOcxo());
            float avgValue = ArrayUtil.avgValue(iArr);
            if (avgValue < 0.0f) {
                avgValue = 0.0f;
            }
            float f = (avgValue * algoDepth) / BitmapUtil.sBitmapHight;
            binding.fatThiness.setCurFatValue(f);
            binding.viewMeasure.setPosition(f);
            binding.resultValue.setVisibility(View.VISIBLE);
            binding.resultValue.setText(FatMeasurePlusActivity.this.getSpannableStringFatValue(f));
            mLastBitmapMsg.setArray(iArr);
            mLastBitmapMsg.setFatThickness(f);
        });
        this.customVideoView = (CustomVideoView) findViewById(R.id.video_guide);
        if (FatConfigManager.getInstance().getCurBodyParts().getGuideVideo() != null) {
            this.customVideoView.setVideoURI(Uri.parse("android.resource://com.mxsella.fat/" + FatConfigManager.getInstance().getCurBodyParts().getGuideVideo()));
            this.customVideoView.setVisibility(View.VISIBLE);
        } else {
            this.customVideoView.setVisibility(View.GONE);
            binding.otherGuide.setText(R.string.common_fat_measure);
            binding.otherGuide.setVisibility(View.VISIBLE);
            binding.shareMeasure.setVisibility(View.VISIBLE);
            binding.bodyPosition.setVisibility(View.VISIBLE);
        }
        playGuideVideo();
        binding.viewMeasure.setDepth(FatConfigManager.getInstance().getCurDeviceDepth(), MxsellaDeviceManager.getInstance().getOcxo(), BitmapUtil.sBitmapHight);
        binding.viewDividingRule.setOcxo(MxsellaDeviceManager.getInstance().getOcxo(), BitmapUtil.sBitmapHight);

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_fat_measure_plus;
    }


    @Override
    public void onConnected() {
        binding.statusTip.setText(R.string.collected);
    }

    private void measureResult(final BitmapMsg bitmapMsg, final boolean z) {
        Log.i(TAG, "=============measureResult=start");
        if (bitmapMsg == null) {
            return;
        }
        if (MxsellaDeviceManager.getInstance().isToBusinessVersion() || FatConfigManager.getInstance().getCurBodyPositionIndex() != 11) {

            ThreadUtils.execute(() -> {
                float f;
                Bitmap imageOneDimensional = BitmapUtil.getImageOneDimensional();
                Bitmap imageOneDimensional2 = BitmapUtil.getImageOneDimensional();
                float algoDepth = FatConfigManager.getInstance().getAlgoDepth(MxsellaDeviceManager.getInstance().getOcxo());
                int[] measure = OpenCvMeasureUtil.getInstance().measure(imageOneDimensional, 13, FatConfigManager.getInstance().getCurBodyPositionIndex());
                if (measure.length > 1) {
                    f = ArrayUtil.avgValue(measure);
                } else {
                    f = measure[0];
                }
                Message message = new Message();
                message.obj = bitmapMsg;
                Log.i(FatMeasurePlusActivity.TAG, "======measureResult===end run: fatthickness1:" + f + " frame=" + BitmapUtil.frame + " Z=>" + z);
                if (z || f > 1.0f) {
                    BitmapUtil.frame = 0L;
                    MxsellaDeviceManager.getInstance().setEnd(true);
                    bitmapMsg.setBitmap(imageOneDimensional2);
                    bitmapMsg.setFatThickness((f * algoDepth) / BitmapUtil.sBitmapHight);
                    bitmapMsg.setArray(measure);
                    binding.viewMeasure.setArray(measure, FatRecord.TYPE.FAT);
                    message.what = 3;
                } else {
                    bitmapMsg.setFatThickness(f);
                    message.what = 2;
                }
                mHandler.sendMessage(message);
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        FatConfigManager.getInstance().setMeasure(true);
        Log.i(TAG, "onResume: " + (this.mHandler == null));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        playGuideVideo();
        CustomVideoView customVideoView = this.customLineVideoView;
        if (customVideoView == null || customVideoView.isPlaying()) {
            return;
        }
        this.customLineVideoView.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        CustomVideoView customVideoView = this.customVideoView;
        if (customVideoView != null && customVideoView.isPlaying()) {
            this.customVideoView.stopPlayback();
        }
        CustomVideoView customVideoView2 = this.customLineVideoView;
        if (customVideoView2 == null || !customVideoView2.isPlaying()) {
            return;
        }
        this.customLineVideoView.stopPlayback();

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeMessages(1);
        }
    }

    private void toStartMeasure() {
        BitmapUtil.frame = 0L;
        isAgainMeasure = false;
        binding.resultValue.setVisibility(View.INVISIBLE);
        binding.viewMeasure.setPosition(0.0f);
        binding.viewMeasure.setArray(null, FatRecord.TYPE.FAT);
        binding.viewDividingRule.setInit(binding.ivImage.getWidth(), binding.ivImage.getHeight(), FatConfigManager.getInstance().getCurDeviceDepth());
        mHandler.removeMessages(5);
        binding.rlUnFound.setVisibility(View.GONE);
        binding.showSaveRl.setVisibility(View.GONE);
        binding.resultRl.setVisibility(View.GONE);
        binding.otherGuide.setVisibility(View.GONE);
        binding.analysisButton.setVisibility(View.GONE);
        binding.statusTip.setVisibility(View.VISIBLE);
        binding.shareMeasure.setVisibility(View.GONE);
        binding.bodyPosition.setVisibility(View.GONE);
        binding.rlAns.setVisibility(View.GONE);
        if (FatConfigManager.getInstance().getCurBodyPositionIndex() == 11) {
            binding.otherGuide.setText(R.string.common_fat_measure);
            binding.otherGuide.setVisibility(View.VISIBLE);
            binding.shareMeasure.setVisibility(View.VISIBLE);
            binding.bodyPosition.setVisibility(View.VISIBLE);
        } else {
            this.customVideoView.setVisibility(View.VISIBLE);
        }

        playGuideVideo();
    }

    @Override
    public void onMessage(DeviceMsg deviceMsg) {
        int msgId = deviceMsg.getMsgId();
        if (msgId != 4261) {
            if (msgId != 4293) {
                return;
            }
            ToastUtil.showToast(MyApplication.getInstance().getString(R.string.app_common_setting_success), 1);
            return;
        }
        BitmapMsg bitmapMsg = (BitmapMsg) deviceMsg;
        int i = MuscleMeasureResultActivity.State.stateMap[bitmapMsg.getState().ordinal()];
        if (i == 1) {
            if (this.measureFailCount < this.thresholdValue) {
                saveRecord();
                this.measureFailCount = 0;
            } else {
                this.measureFailCount = 1;
            }
            toStartMeasure();
            Log.i("", "frame======================start=: ");
        } else if (i != 2) {
            if (i != 3) {
                return;
            }
            binding.statusTip.setText(R.string.collecting);
            this.binding.ivImage.setImageBitmap(bitmapMsg.getBitmap());
            if (BitmapUtil.frame >= 150 || (BitmapUtil.frame > 50 && this.isAgainMeasure)) {
                Log.i(TAG, "frame=======================: " + BitmapUtil.frame);
                this.isAgainMeasure = true;
                if (FatConfigManager.getInstance().isAutoMeasure()) {
                    BitmapUtil.frame = 0L;
                    measureResult(bitmapMsg, false);
                }
            }
            this.handler.removeMessages(0);
            this.handler.sendEmptyMessageDelayed(0, 300L);
        } else {
            this.handler.removeMessages(0);
            Log.i(TAG, "============ ");
            this.isAgainMeasure = false;
            System.gc();
            binding.statusTip.setText(R.string.collected);
            if (!MxsellaDeviceManager.getInstance().isToBusinessVersion() && FatConfigManager.getInstance().getCurBodyPositionIndex() == 11) {
                setVideoVisibility(false);
                binding.statusTip.setVisibility(View.GONE);
            }
            if (BitmapUtil.frame >= 150 && !FatConfigManager.getInstance().isAutoMeasure()) {
                this.mLastBitmapMsg.setBitmap(BitmapUtil.getImageOneDimensional());
                Log.i(TAG, "保存图片");
                toShowMeasureResult();
            }
            BitmapUtil.initList();
            BitmapUtil.frame = 0L;
        }
    }

    public static class State {
        static final int[] stateMap;

        static {
            int[] map = new int[BitmapMsg.State.values().length];
            stateMap = map;
            try {
                map[BitmapMsg.State.START.ordinal()] = 1;
            } catch (NoSuchFieldError ignored) {
            }
            try {
                map[BitmapMsg.State.END.ordinal()] = 2;
            } catch (NoSuchFieldError ignored) {
            }
            try {
                map[BitmapMsg.State.RUN.ordinal()] = 3;
            } catch (NoSuchFieldError ignored) {
            }
        }
    }

    private void saveRecord() {
        if (this.mLastBitmapMsg.getFatThickness() <= 0.0f || FatConfigManager.getInstance().isFangkeMode()) {
            return;
        }
        if (this.mLastBitmapMsg.getBitmap() == null) {
            this.mLastBitmapMsg.setBitmap(BitmapUtil.getImageOneDimensional());
        }
        FatConfigManager.getInstance().setReLoadData(true);

        ThreadUtils.execute(() -> {
            String str = MxsellaConstant.APP_DIR_PATH + "/data/";
            String str2 = DateUtil.getDate2String(System.currentTimeMillis(), "MM_dd_HH_mm_ss") + "_" + FatConfigManager.getInstance().getCurBodyPositionIndex() + "_mm_" + mLastBitmapMsg.getFatThickness() + ".png";
            try {
                BitmapUtil.saveBitmap(mLastBitmapMsg.getBitmap(), str, str2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            FatRecord fatRecord = new FatRecord();
            fatRecord.setRecordType(Integer.valueOf(FatRecord.TYPE.FAT.value()));
            fatRecord.setDepth(Integer.valueOf(FatConfigManager.getInstance().getCurDeviceDepth()));
            fatRecord.setRecordValue(this.mLastBitmapMsg.getFatThickness() + "mm");
            fatRecord.setUserId("1");
            fatRecord.setOcxo(Integer.valueOf(MxsellaDeviceManager.getInstance().getOcxo()));
            fatRecord.setBitmapHight(Integer.valueOf(BitmapUtil.sBitmapHight));
            if (this.mLastBitmapMsg.getProtocolType() != null) {
                fatRecord.setTransType(this.mLastBitmapMsg.getProtocolType().name());
            }
            fatRecord.setPkgName(SystemParamUtil.getPackageName(this));
            fatRecord.setSn("V:" + MxsellaDeviceManager.getInstance().getDeviceVersion() + "_ID:" + MxsellaDeviceManager.getInstance().getFlashId());
            fatRecord.setBodyPosition(FatConfigManager.getInstance().getCurBodyPositionIndex() + "");
            fatRecord.setRecordDate(DateUtil.getDate2String(System.currentTimeMillis(), "yyy-MM-dd HH:mm:ss"));
            fatRecord.setIntArrayAvg(this.mLastBitmapMsg.getArray());
            if (FatConfigManager.getInstance().getCurMeasureMember() != null) {
                fatRecord.setFamilyId(FatConfigManager.getInstance().getCurMeasureMember().getId());
            }
            if (this.mLastBitmapMsg.getBitmap() == null) {
                return;
            }

            fatRecord.setRecordImage(str + str2);

            fatRecord.setLocalCache(true);
            //保存记录到本地
            MyApplication.getInstance().db.fatRecordDao().AddRecord(fatRecord);
            this.mLastBitmapMsg.setFatThickness(-3.0f);
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        overridePendingTransition(17432576, 17432577);
        MxsellaDeviceManager.getInstance().unregisterDeviceInterface(this);
        FatConfigManager.getInstance().setMeasure(false);
        BitmapUtil.initList();
        if (measureFailCount < thresholdValue) {
            saveRecord();
        }
    }

    @Override
    public void onDisconnected(int i, String str) {
        binding.statusTip.setText(R.string.app_measure_resule_device_disconnect);
    }

    public SpannableString getSpannableStringFatValue(float f) {
        SpannableString spannableString;
        String replace = MetricInchUnitUtil.getUnitStr(f).replace(" ", "");
        if (FatConfigManager.getInstance().isAutoMeasure()) {
            spannableString = new SpannableString(replace + " (avg)");
        } else {
            spannableString = new SpannableString(replace);
        }
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
            }

            @Override
            public void updateDrawState(TextPaint textPaint) {
                super.updateDrawState(textPaint);
                textPaint.setColor(getResources().getColor(R.color.text_black_color));
                textPaint.setTextSize(140.0f);
                textPaint.setUnderlineText(false);
            }
        }, 0, replace.length() - 2, 33);
        return spannableString;
    }

    private void playGuideVideo() {
        //播放演示视频
    }

    private void showOpenMeasureLineDialog() {
        //第一次使用提示
    }

}
