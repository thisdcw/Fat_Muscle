package com.mxsella.fatmuscle.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.mxsella.fat_muscle.R;
import com.mxsella.fat_muscle.databinding.ActivityMuscleMeasureResultBinding;
import com.mxsella.fatmuscle.common.MyApplication;
import com.mxsella.fatmuscle.common.base.BaseActivity;
import com.mxsella.fatmuscle.db.bean.FatRecord;
import com.mxsella.fatmuscle.sdk.common.MxsellaConstant;
import com.mxsella.fatmuscle.sdk.fat.entity.BitmapMsg;
import com.mxsella.fatmuscle.sdk.fat.entity.BodyParts;
import com.mxsella.fatmuscle.sdk.fat.entity.DeviceMsg;
import com.mxsella.fatmuscle.sdk.fat.entity.ShareData;
import com.mxsella.fatmuscle.sdk.fat.manager.FatConfigManager;
import com.mxsella.fatmuscle.sdk.fat.manager.MxsellaDeviceManager;
import com.mxsella.fatmuscle.sdk.fat.utils.AnrWatchDog;
import com.mxsella.fatmuscle.sdk.fat.utils.BitmapUtil;
import com.mxsella.fatmuscle.sdk.fat.utils.FileIOUtils;
import com.mxsella.fatmuscle.sdk.util.SystemParamUtil;
import com.mxsella.fatmuscle.sdk.util.ThreadUtils;
import com.mxsella.fatmuscle.sdk.util.ToastUtil;
import com.mxsella.fatmuscle.utils.DateUtil;
import com.mxsella.fatmuscle.utils.LogUtil;
import com.mxsella.fatmuscle.view.MeasureDividingRuleView;
import com.mxsella.fatmuscle.view.MeasureView;
import com.mxsella.fatmuscle.view.ResultRoundView;
import com.mxsella.fatmuscle.view.SlideLineView;
import com.mxsella.fatmuscle.view.dialog.CustomDialog;
import com.mxsella.fatmuscle.view.widget.FlashHelper;

public class MuscleMeasureResultActivity extends BaseActivity<ActivityMuscleMeasureResultBinding> implements View.OnClickListener, MxsellaDeviceManager.DeviceInterface, SlideLineView.MeasureListener {
    private static final int CLOSE_FLASH_VIEW = 5;
    private static final int CLOSE_OPERATION_TIP = 4;
    private static final int DELAY = 1;
    private static final int START_FLASH_VIEW = 6;
    AnrWatchDog anrWatchDog;
    private BodyParts curBodyPars;
    private Button lastBtn;
    private ImageView mIvBodyIcon;
    private ImageView mIvClose;
    private ImageView mIvImage;
    private MeasureView mMeasureView;
    protected ResultRoundView mRoundView;
    private RelativeLayout mShowSaveRl;
    private SlideLineView mSlideLineView;
    private TextView mTvBottomGuide;
    private TextView mTvOperation;
    private TextView mTvTopGuide;
    private TextView mTvanalysis;
    private MeasureDividingRuleView measureDividingRuleView;
    private RelativeLayout measureRoot;
    private Button nextBtn;
    private ShareData shareData;
    CustomDialog showOpenMeasureLineDialog;
    private boolean isSaveRecord = false;
    private String TAG = "MuscleMeasureResultActivity";
    private int count = 0;
    private boolean isAgainMeasure = false;
    private boolean isShow = false;
    private int curGuideIndex = 0;
    private float fatthickness = 0.0f;
    private BitmapMsg mLastBitmapMsg = new BitmapMsg();
    private int measureFailCount = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 4) {
            } else if (message.what == 5) {
                MuscleMeasureResultActivity.this.mMeasureView.setShowUltrasoundImg(true);
                FlashHelper.stopFlick(MuscleMeasureResultActivity.this.mMeasureView);
            } else if (message.what == 6) {
                FlashHelper.startFlick(MuscleMeasureResultActivity.this.mMeasureView);
            }
        }
    };
    Handler handler = new Handler(Looper.getMainLooper(), message -> {
        BitmapMsg bitmapMsg = new BitmapMsg();
        bitmapMsg.setMsgId(4261);
        bitmapMsg.setState(BitmapMsg.State.END);
        MuscleMeasureResultActivity.this.onMessage(bitmapMsg);
        return true;
    });

    public void initOnClick() {
        binding.analysisButton.setOnClickListener(v -> {
            this.mSlideLineView.setMeasure(true);
            binding.analysisButton.setVisibility(View.GONE);
            binding.tvTitle.setText(R.string.muscle_manual_result_note);
        });
        binding.cancle.setOnClickListener(v -> {
            this.isSaveRecord = false;
            binding.analysisButton.setVisibility(View.VISIBLE);
            this.mSlideLineView.clearLine();
            this.mRoundView.setVisibility(View.INVISIBLE);
            this.mShowSaveRl.setVisibility(View.INVISIBLE);
            this.mIvBodyIcon.setVisibility(View.VISIBLE);
        });
        binding.save.setOnClickListener(v -> {
            this.isSaveRecord = true;
            Log.d(TAG, "点击了保存");
            this.mShowSaveRl.setVisibility(View.INVISIBLE);
        });
    }

    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 123;

    public void initPer() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 如果没有相册权限，则请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_READ_EXTERNAL_STORAGE);
        } else {
            // 如果已经有权限，则执行相册操作
            // 这里可以执行打开相册的操作
        }
    }

    @Override
    protected void initView() {
        initPer();
        MxsellaDeviceManager.getInstance().connectDevice();

        this.measureFailCount = 0;
        getWindow().addFlags(128);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.show_save_rl);
        this.mShowSaveRl = relativeLayout;
        relativeLayout.setClickable(true);
        this.mIvImage = (ImageView) findViewById(R.id.iv_image);
        this.mMeasureView = (MeasureView) findViewById(R.id.view_measure);
        this.mRoundView = (ResultRoundView) findViewById(R.id.rv);
        this.mSlideLineView = (SlideLineView) findViewById(R.id.view_slide);
        MeasureDividingRuleView measureDividingRuleView = (MeasureDividingRuleView) findViewById(R.id.view_dividing_rule);
        this.measureDividingRuleView = measureDividingRuleView;
        measureDividingRuleView.setOcxo(MxsellaDeviceManager.getInstance().getOcxo(), BitmapUtil.sBitmapHight);
        this.mIvClose = (ImageView) findViewById(R.id.iv_close);
        this.mIvBodyIcon = (ImageView) findViewById(R.id.guide_icon);

        this.mIvClose.setOnClickListener(view -> {
            MuscleMeasureResultActivity.this.mMeasureView.setPosition(0.0f);
            MuscleMeasureResultActivity.this.mIvImage.setImageBitmap(BitmapUtil.getImageOneDimensional());
            MuscleMeasureResultActivity.this.finish();
        });
        this.measureRoot = (RelativeLayout) findViewById(R.id.measure_root);

        MxsellaDeviceManager.getInstance().registerDeviceInterface(this);
        AnrWatchDog build = new AnrWatchDog.Builder().timeout(30000).ignoreDebugger(true).anrListener(str -> FileIOUtils.writeFileFromString(MxsellaConstant.APP_DIR_PATH + "/anr/" + DateUtil.getDate2String(System.currentTimeMillis(), "MM_dd_HH_mm_ss") + ".txt", str)).build();
        this.anrWatchDog = build;
        build.start();
        this.measureDividingRuleView.setInit(this.mIvImage.getWidth(), this.mIvImage.getHeight(), FatConfigManager.getInstance().getCurDeviceDepth());
        if (FatConfigManager.getInstance().getCurBodyPositionIndex() == 1) {
            Log.d(TAG, "getCurBodyPositionIndex() == 1");
        }
        this.mIvImage.setImageBitmap(BitmapUtil.getImageOneDimensional());
        BodyParts curBodyParts = FatConfigManager.getInstance().getCurBodyParts();
        this.curBodyPars = curBodyParts;
        binding.tvTitle.setText(getString(R.string.muscle_discover, new Object[]{getString(curBodyParts.getMusclePartTip().intValue())}));
        String str = MxsellaConstant.GUIDE_TIP + this.curBodyPars.getIndex();

        this.mIvBodyIcon.setImageResource(this.curBodyPars.getMusclePartIcon().intValue());
        this.mRoundView.setShowStandard(false);
        this.mSlideLineView.setMeasureListener(this);

        initOnClick();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_muscle_measure_result;
    }

    @Override
    public void result(float f, int[] iArr) {
        this.mRoundView.setFatness(f);
        this.mRoundView.setVisibility(View.VISIBLE);
        this.mLastBitmapMsg.setFatThickness(f);
        this.mLastBitmapMsg.setArray(iArr);
        if (FatConfigManager.getInstance().isFangkeMode()) {
            return;
        }
        this.mShowSaveRl.setVisibility(View.VISIBLE);
    }

    @Override // com.marvoto.fat.widget.SlideLineView.MeasureListener
    public void startMeasure() {
        this.mRoundView.setVisibility(View.VISIBLE);
        this.mIvBodyIcon.setVisibility(View.INVISIBLE);
        if (FatConfigManager.getInstance().isFangkeMode()) {
            return;
        }
        this.mShowSaveRl.setVisibility(View.VISIBLE);
    }

    @Override
    public void endMeasure() {
        this.mRoundView.setVisibility(View.INVISIBLE);
        this.mIvBodyIcon.setVisibility(View.VISIBLE);
        if (FatConfigManager.getInstance().isFangkeMode()) {
            return;
        }
        this.mShowSaveRl.setVisibility(View.GONE);
    }

    @Override
    public void onConnected() {
        binding.tvTitle.setText("");
        binding.tvTitle.setText(getString(R.string.muscle_discover, new Object[]{getString(this.curBodyPars.getMusclePartTip().intValue())}));
    }

    @Override
    public void onResume() {
        super.onResume();
        FatConfigManager.getInstance().setMeasure(true);
        Log.i(this.TAG, "onResume: " + (this.mHandler == null));
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.i(this.TAG, "onPause: ");
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeMessages(1);
        }
    }

    @Override
    public void onMessage(DeviceMsg deviceMsg) {
        int msgId = deviceMsg.getMsgId();
        if (msgId != 4261) {
            if (msgId != 4293) {
                return;
            }
            ToastUtil.showToast(this, getString(R.string.app_common_setting_success), 1);
            return;
        }
        BitmapMsg bitmapMsg = (BitmapMsg) deviceMsg;
        int i = State.stateMap[bitmapMsg.getState().ordinal()];
        if (i == 1) {
            if (this.isSaveRecord) {
                saveRecord();
            }
            this.mTvanalysis.setVisibility(View.GONE);
            binding.tvTitle.setText(getString(R.string.muscle_discover, new Object[]{getString(this.curBodyPars.getMusclePartTip().intValue())}));
            this.measureFailCount = 0;
            LogUtil.i("frame======================start=: ");
            BitmapUtil.frame = 0L;
            this.isAgainMeasure = false;
            this.mMeasureView.setPosition(0.0f);
            this.measureDividingRuleView.setInit(this.mIvImage.getWidth(), this.mIvImage.getHeight(), FatConfigManager.getInstance().getCurDeviceDepth());
        } else if (i != 2) {
            if (i != 3) {
                return;
            }
            MxsellaDeviceManager.getInstance().setEnd(false);
            this.mSlideLineView.clearLine();
            this.mIvImage.setImageBitmap(bitmapMsg.getBitmap());
            this.mLastBitmapMsg = bitmapMsg;
            this.handler.removeMessages(0);
            this.handler.sendEmptyMessageDelayed(0, 300L);
        } else {
            this.handler.removeMessages(0);
            LogUtil.i("frame======================end=: ");
            BitmapUtil.frame = 0L;
            this.isAgainMeasure = false;
            this.measureFailCount = 0;
            System.gc();
            this.mLastBitmapMsg = bitmapMsg;
            MxsellaDeviceManager.getInstance().setEnd(true);
            binding.analysisButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {

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


    @Override
    public void onDestroy() {
        super.onDestroy();
        overridePendingTransition(17432576, 17432577);
        MxsellaDeviceManager.getInstance().unregisterDeviceInterface(this);
        BitmapUtil.initList();
        FatConfigManager.getInstance().setMeasure(false);
        if (this.isSaveRecord) {
            saveRecord();
        }
    }

    @Override
    public void onDisconnected(int i, String str) {
        binding.tvTitle.setVisibility(View.VISIBLE);
        binding.tvTitle.setText(R.string.app_measure_resule_device_disconnect);
    }

    private void saveRecord() {
        this.isSaveRecord = false;
        if (FatConfigManager.getInstance().isFangkeMode()) {
            return;
        }
        FatConfigManager.getInstance().setReLoadData(true);
        if (this.mLastBitmapMsg.getBitmap() == null) {
            this.mLastBitmapMsg.setBitmap(BitmapUtil.getImageOneDimensional());
        }

        ThreadUtils.execute(() -> {
            String str = MxsellaConstant.APP_DIR_PATH + "/data/";
            String str2 = DateUtil.getDate2String(System.currentTimeMillis(), "MM_dd_HH_mm_ss") + "_" + FatConfigManager.getInstance().getCurBodyPositionIndex() + "_mm_" + MuscleMeasureResultActivity.this.mLastBitmapMsg.getFatThickness() + ".png";
            try {
                BitmapUtil.saveBitmap(MuscleMeasureResultActivity.this.mLastBitmapMsg.getBitmap(), str, str2);
                Log.d(TAG, "保存");
            } catch (Exception e) {
                e.printStackTrace();
            }
            FatRecord fatRecord = new FatRecord();
            fatRecord.setRecordType(Integer.valueOf(FatRecord.TYPE.MUSCLE.value()));
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

}