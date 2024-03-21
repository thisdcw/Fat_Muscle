package com.mxsella.fatmuscle.ui.activity;


import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.mxsella.fat_muscle.R;
import com.mxsella.fat_muscle.databinding.ActivityFatMeasurePlusBinding;
import com.mxsella.fatmuscle.common.MyApplication;
import com.mxsella.fatmuscle.common.base.BaseActivity;
import com.mxsella.fatmuscle.sdk.common.MxsellaConstant;
import com.mxsella.fatmuscle.sdk.fat.entity.BitmapMsg;
import com.mxsella.fatmuscle.sdk.fat.entity.DeviceMsg;
import com.mxsella.fatmuscle.db.bean.FatRecord;
import com.mxsella.fatmuscle.sdk.fat.manager.FatConfigManager;
import com.mxsella.fatmuscle.sdk.fat.manager.MxsellaDeviceManager;
import com.mxsella.fatmuscle.sdk.fat.utils.AnrWatchDog;
import com.mxsella.fatmuscle.sdk.fat.utils.BitmapUtil;
import com.mxsella.fatmuscle.sdk.fat.utils.FileIOUtils;
import com.mxsella.fatmuscle.sdk.fat.utils.MetricInchUnitUtil;
import com.mxsella.fatmuscle.sdk.fat.utils.OpenCvMeasureUtil;
import com.mxsella.fatmuscle.sdk.util.SystemParamUtil;
import com.mxsella.fatmuscle.sdk.util.ThreadUtils;
import com.mxsella.fatmuscle.sdk.util.ToastUtil;
import com.mxsella.fatmuscle.utils.ArrayUtil;
import com.mxsella.fatmuscle.utils.DateUtil;
import com.mxsella.fatmuscle.view.MeasureDividingRuleView;

public class FatMeasurePlusActivity extends BaseActivity implements MxsellaDeviceManager.DeviceInterface {

    ActivityFatMeasurePlusBinding fatMeasurePlusBinding;
    private static final String TAG = "MuscleMeasureResultActivity";
    AnrWatchDog anrWatchDog;
    private TextView mTvStatusTip;
    private MeasureDividingRuleView measureDividingRuleView;
    private LinearLayout measureRoot;
    private int count = 0;
    private boolean isAgainMeasure = false;
    private boolean isShow = false;
    private BitmapMsg mLastBitmapMsg = new BitmapMsg();
    private int measureFailCount = 0;
    private int thresholdValue = 3;

    private Handler mHandler = new Handler() { // from class: com.marvoto.fat.module.measure.ui.FatMeasurePlusActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 2) {
                FatMeasurePlusActivity.this.showResult((BitmapMsg) message.obj, false);
            } else if (message.what == 3) {
                FatMeasurePlusActivity.this.showResult((BitmapMsg) message.obj, true);
            } else if (message.what == 5) {
                FatMeasurePlusActivity.this.fatMeasurePlusBinding.viewMeasure.setShowUltrasoundImg(true);
            } else {
                int i = message.what;
            }
        }
    };

    Handler handler = new Handler(Looper.getMainLooper(), message -> {
        BitmapMsg bitmapMsg = new BitmapMsg();
        bitmapMsg.setMsgId(4261);
        bitmapMsg.setState(BitmapMsg.State.END);
        FatMeasurePlusActivity.this.onMessage(bitmapMsg);
        return true;
    });

    public void toShowMeasureResult() {
        this.mTvStatusTip.setVisibility(View.GONE);
        setVideoVisibility(false);
        this.fatMeasurePlusBinding.analysisButton.setVisibility(View.VISIBLE);
        this.measureFailCount = 0;
        this.fatMeasurePlusBinding.viewMeasure.setShowUltrasoundImg(true);
        if (FatConfigManager.getInstance().isAutoMeasure()) {
            this.fatMeasurePlusBinding.analysisButton.setText(R.string.analysis);
            return;
        }
        this.fatMeasurePlusBinding.viewMeasure.setShowUltrasoundImg(false);
        this.fatMeasurePlusBinding.analysisButton.setText(R.string.to_measure);
    }

    @Override
    protected void initView() {
        fatMeasurePlusBinding = DataBindingUtil.setContentView(this, R.layout.activity_fat_measure_plus);

        MxsellaDeviceManager.getInstance().connectDevice();

        this.measureFailCount = 0;
        //TODO 状态条
//        StatusBarUtil.setColor(this, -1, 5);
//        StatusBarUtil.StatusBarLightMode(this);
        fatMeasurePlusBinding.analysisButton.setOnClickListener(view -> {
            if (FatConfigManager.getInstance().isAutoMeasure()) {
                if (FatMeasurePlusActivity.this.fatMeasurePlusBinding.viewMeasure.isShowUltrasoundImg()) {
                    FatMeasurePlusActivity.this.fatMeasurePlusBinding.viewMeasure.setShowUltrasoundImg(false);
                } else {
                    FatMeasurePlusActivity.this.fatMeasurePlusBinding.viewMeasure.setShowUltrasoundImg(true);
                }
                return;
            }
            FatMeasurePlusActivity.this.fatMeasurePlusBinding.analysisButton.setVisibility(View.GONE);
            int[] iArr2 = {0, 90, 75, 90, 149, 90};
            FatMeasurePlusActivity.this.fatMeasurePlusBinding.viewMeasure.setArray(iArr2, FatRecord.TYPE.MUSCLE);
            float avgValue2 = (ArrayUtil.avgValue(iArr2) * FatConfigManager.getInstance().getAlgoDepth(MxsellaDeviceManager.getInstance().getOcxo())) / BitmapUtil.sBitmapHight;
            FatMeasurePlusActivity.this.mLastBitmapMsg.setArray(iArr2);
            FatMeasurePlusActivity.this.mLastBitmapMsg.setFatThickness(avgValue2);
            FatMeasurePlusActivity.this.fatMeasurePlusBinding.viewMeasure.setShowUltrasoundImg(true);
            FatMeasurePlusActivity.this.fatMeasurePlusBinding.viewMeasure.setPosition(avgValue2);
            if (FatConfigManager.getInstance().isFangkeMode()) {
                return;
            }
        });
        this.mTvStatusTip = fatMeasurePlusBinding.statusTip;
        MeasureDividingRuleView measureDividingRuleView = fatMeasurePlusBinding.viewDividingRule;
        this.measureDividingRuleView = measureDividingRuleView;
        measureDividingRuleView.setOcxo(MxsellaDeviceManager.getInstance().getOcxo(), BitmapUtil.sBitmapHight);


        this.measureRoot = fatMeasurePlusBinding.measureRoot;

        MxsellaDeviceManager.getInstance().registerDeviceInterface(this);
        Log.i(TAG, "initGetData: " + this.isShow);
        AnrWatchDog build = new AnrWatchDog.Builder().timeout(30000).ignoreDebugger(true).anrListener(str -> {

                    Log.i(TAG, "文件保存");
                    FileIOUtils.writeFileFromString(MxsellaConstant.APP_DIR_PATH + "/anr/" + DateUtil.getDate2String(System.currentTimeMillis(), "MM_dd_HH_mm_ss") + ".txt", str);
                }
        ).build();
        this.anrWatchDog = build;
        build.start();
        this.measureDividingRuleView.setInit(this.fatMeasurePlusBinding.ivImage.getWidth(), this.fatMeasurePlusBinding.ivImage.getHeight(), FatConfigManager.getInstance().getCurDeviceDepth());

        if (FatConfigManager.getInstance().getCurBodyPositionIndex() == 6) {
            this.thresholdValue = 1;
        } else {
            this.thresholdValue = 3;
        }
        this.fatMeasurePlusBinding.ivImage.setImageBitmap(BitmapUtil.getImageOneDimensional());
        this.fatMeasurePlusBinding.viewMeasure.setMeasureCallBack(iArr -> {
            if (iArr == null) {
                return;
            }
            float algoDepth = FatConfigManager.getInstance().getAlgoDepth(MxsellaDeviceManager.getInstance().getOcxo());
            float avgValue = ArrayUtil.avgValue(iArr);
            if (avgValue < 0.0f) {
                avgValue = 0.0f;
            }
            float f = (avgValue * algoDepth) / BitmapUtil.sBitmapHight;
            FatMeasurePlusActivity.this.fatMeasurePlusBinding.viewMeasure.setPosition(f);
            FatMeasurePlusActivity.this.mLastBitmapMsg.setArray(iArr);
            FatMeasurePlusActivity.this.mLastBitmapMsg.setFatThickness(f);
        });

        playGuideVideo();
        this.fatMeasurePlusBinding.viewMeasure.setDepth(3, MxsellaDeviceManager.getInstance().getOcxo(), BitmapUtil.sBitmapHight);
        this.measureDividingRuleView.setOcxo(MxsellaDeviceManager.getInstance().getOcxo(), BitmapUtil.sBitmapHight);
        initGetData();

        getWindow().addFlags(128);
        fatMeasurePlusBinding.close.setOnClickListener(view -> {
            FatMeasurePlusActivity.this.finish();
        });
    }


    @Override
    public void onConnected() {
        this.mTvStatusTip.setText(R.string.collected);
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
                this.mTvStatusTip.setVisibility(View.GONE);
                if (fatThickness == -3.0f) {
                    fatMeasurePlusBinding.showReason.setText(R.string.too_week_result);
                    return;
                } else if (FatConfigManager.getInstance().isAutoMeasure()) {
                    fatMeasurePlusBinding.showReason.setText(MxsellaDeviceManager.getInstance().isToBusinessVersion() ? R.string.un_found_result_manually : R.string.un_found_result);
                    if (MxsellaDeviceManager.getInstance().isToBusinessVersion()) {
                        this.fatMeasurePlusBinding.analysisButton.setVisibility(View.VISIBLE);
                    } else {
                        this.fatMeasurePlusBinding.analysisButton.setVisibility(View.GONE);
                    }
                    this.fatMeasurePlusBinding.analysisButton.setText(R.string.manual_measure);
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
        this.fatMeasurePlusBinding.viewMeasure.setDepth(FatConfigManager.getInstance().getCurDeviceDepth(), MxsellaDeviceManager.getInstance().getOcxo(), BitmapUtil.sBitmapHight);
        this.measureDividingRuleView.setOcxo(MxsellaDeviceManager.getInstance().getOcxo(), BitmapUtil.sBitmapHight);
        this.fatMeasurePlusBinding.viewMeasure.setPosition(fatThickness);
        this.mHandler.sendEmptyMessageDelayed(6, 1000L);
        this.measureDividingRuleView.setInit(this.fatMeasurePlusBinding.ivImage.getWidth(), this.fatMeasurePlusBinding.ivImage.getHeight(), FatConfigManager.getInstance().getCurDeviceDepth());
        this.fatMeasurePlusBinding.ivImage.setImageBitmap(bitmapMsg.getBitmap());
        showOpenMeasureLineDialog();
    }

    private void setVideoVisibility(boolean z) {
        if (z) {
            playGuideVideo();
        }
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
                    FatMeasurePlusActivity.this.fatMeasurePlusBinding.viewMeasure.setArray(measure, FatRecord.TYPE.FAT);
                    message.what = 3;
                } else {
                    bitmapMsg.setFatThickness(f);
                    message.what = 2;
                }
                FatMeasurePlusActivity.this.mHandler.sendMessage(message);
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
    }

    @Override
    public void onStop() {
        super.onStop();
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
        this.isAgainMeasure = false;
        this.fatMeasurePlusBinding.viewMeasure.setPosition(0.0f);
        this.fatMeasurePlusBinding.viewMeasure.setArray(null, FatRecord.TYPE.FAT);
        this.measureDividingRuleView.setInit(this.fatMeasurePlusBinding.ivImage.getWidth(), this.fatMeasurePlusBinding.ivImage.getHeight(), FatConfigManager.getInstance().getCurDeviceDepth());
        this.mHandler.removeMessages(5);
        this.fatMeasurePlusBinding.analysisButton.setVisibility(View.GONE);
        this.mTvStatusTip.setVisibility(View.VISIBLE);
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
        int i = C198418.$SwitchMap$com$marvoto$fat$entity$BitmapMsg$State[bitmapMsg.getState().ordinal()];
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
            this.mTvStatusTip.setText(R.string.collecting);
            this.fatMeasurePlusBinding.ivImage.setImageBitmap(bitmapMsg.getBitmap());
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
            this.mTvStatusTip.setText(R.string.collected);
            if (!MxsellaDeviceManager.getInstance().isToBusinessVersion() && FatConfigManager.getInstance().getCurBodyPositionIndex() == 11) {
                setVideoVisibility(false);
                this.mTvStatusTip.setVisibility(View.GONE);
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

    public static /* synthetic */ class C198418 {
        static final /* synthetic */ int[] $SwitchMap$com$marvoto$fat$entity$BitmapMsg$State;

        static {
            int[] iArr = new int[BitmapMsg.State.values().length];
            $SwitchMap$com$marvoto$fat$entity$BitmapMsg$State = iArr;
            try {
                iArr[BitmapMsg.State.START.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$marvoto$fat$entity$BitmapMsg$State[BitmapMsg.State.END.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$marvoto$fat$entity$BitmapMsg$State[BitmapMsg.State.RUN.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
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
            String str2 = DateUtil.getDate2String(System.currentTimeMillis(), "MM_dd_HH_mm_ss") + "_" + FatConfigManager.getInstance().getCurBodyPositionIndex() + "_mm_" + FatMeasurePlusActivity.this.mLastBitmapMsg.getFatThickness() + ".png";
            try {
                BitmapUtil.saveBitmap(FatMeasurePlusActivity.this.mLastBitmapMsg.getBitmap(), str, str2);
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
        if (this.measureFailCount < this.thresholdValue) {
            saveRecord();
        }
    }

    @Override // com.marvoto.fat.manager.MxsellaDeviceManager.DeviceInterface
    public void onDisconnected(int i, String str) {
        this.mTvStatusTip.setText(R.string.app_measure_resule_device_disconnect);
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
                textPaint.setColor(FatMeasurePlusActivity.this.getResources().getColor(R.color.text_black_color));
                textPaint.setTextSize(140.0f);
                textPaint.setUnderlineText(false);
            }
        }, 0, replace.length() - 2, 33);
        return spannableString;
    }

    private void playGuideVideo() {
    }

    private void showOpenMeasureLineDialog() {
        //第一次使用提示
    }

}
