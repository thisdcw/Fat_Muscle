package com.mxsella.fatmuscle.sdk.fat.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.mxsella.fatmuscle.comm.inter.MyDataListener;
import com.mxsella.fatmuscle.common.MyApplication;
import com.mxsella.fatmuscle.sdk.common.Constant;
import com.mxsella.fatmuscle.sdk.common.MxsellaConstant;
import com.mxsella.fatmuscle.sdk.fat.entity.BitmapMsg;
import com.mxsella.fatmuscle.sdk.fat.entity.DeviceDefaultParams;
import com.mxsella.fatmuscle.sdk.fat.entity.DeviceMsg;
import com.mxsella.fatmuscle.sdk.fat.entity.FirmwareUpdateMsg;
import com.mxsella.fatmuscle.sdk.fat.entity.FlashMsg;
import com.mxsella.fatmuscle.sdk.fat.entity.ResultMsg;
import com.mxsella.fatmuscle.sdk.fat.entity.VersionMsg;
import com.mxsella.fatmuscle.sdk.fat.inter.DataTransListerner;
import com.mxsella.fatmuscle.sdk.fat.interfaces.DeviceResultInterface;
import com.mxsella.fatmuscle.sdk.fat.utils.BitmapUtil;
import com.mxsella.fatmuscle.sdk.fat.utils.SharedPreferencesUtil;
import com.mxsella.fatmuscle.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MxsellaDeviceManager implements DataTransListerner {

    private static final String ACTION_USB_DETACHED_PERMISSION = "android.hardware.usb.action.USB_DEVICE_DETACHED";
    private static final String ACTION_USB_DEVICE_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    private static final String ACTION_USB_ACCESSORY_ATTACHED = "android.hardware.usb.action.USB_ACCESSORY_ATTACHED";
    private static final String ACTION_USB_PERMISSION = "com.template.USB_PERMISSION";
    private static final String CUR_SEL_DEVICE_CODE = "cur_device_code";
    private static final String DATA_DEVICE_DATA_RATE_KEY = "blue_speed_rate";
    private static final String DATA_DEVICE_FLASH_KEY = "device_sn";
    private static final String DATA_DEVICE_MODE_KEY = "deviceMode";
    private static final String DATA_DEVICE_NAME_KEY = "deviceIdentification";
    private static final String DATA_DEVICE_VERSION_KEY = "deviceVersion";
    private static final String TAG = "MxsellaDeviceManager";
    private static Object obj = new Object();
    private static MxsellaDeviceManager sMxsellaDeviceManager;
    private int blueSpeedRateLeve;
    private String compileTime;
    private int curDeviceCode = 0;
    private String deviceIdentification;
    private String deviceMode;
    private int deviceVersion;
    private String flashId;
    private BitmapMsg mBitmapMsg;
    private Context mContext;
    private OTGManager mOTGManager;
    private List<DeviceInterface> deviceInterfaces = new CopyOnWriteArrayList();
    private int ocxo = 32;
    private ConcurrentHashMap<Object, DeviceResultInterface> interfaceHashMap = new ConcurrentHashMap<>();
    private boolean isEnd = false;
    BitmapMsg bitmapMsg = null;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(MxsellaDeviceManager.TAG, "onReceive: " + intent);
            if (curDeviceCode == 1) {
                Log.d(TAG, "当前设备是蓝牙连接");
                return;
            }
            if (intent.getAction().equals(MxsellaDeviceManager.ACTION_USB_DETACHED_PERMISSION)) {
                if (MxsellaDeviceManager.this.mOTGManager.isConnect()) {
                    Log.d(TAG, "OTG 已经断开连接");
                    disConnectDevice();
                }
            } else if (!intent.getAction().equals(MxsellaDeviceManager.ACTION_USB_DEVICE_ATTACHED) || MxsellaDeviceManager.this.mOTGManager.isConnect()) {
                Log.d(TAG, "不是USB设备,或者OTG已经连接");
            } else if (intent.getAction().equals(MxsellaDeviceManager.ACTION_USB_DEVICE_ATTACHED)) {
                Log.d(TAG, "OTG 连接设备");
                connectDevice();
            }
        }
    };

    public void setEnd(boolean z) {
        this.isEnd = z;
    }

    public boolean isEnd() {
        return this.isEnd;
    }

    public void unregisterDeviceInterface(DeviceInterface deviceInterface) {
        this.deviceInterfaces.remove(deviceInterface);
    }

    public void connectDevice() {
        //去除蓝牙连接部分
        int i = this.curDeviceCode;
        Log.d(TAG, "连接设备" + i);
        if (i == 0) {
            Log.d(TAG, "初始化设备");
            this.mOTGManager.initUsbDevice();
        }
    }

    public int getCurDeviceCode() {
        return this.curDeviceCode;
    }

    public void registerDeviceInterface(DeviceInterface deviceInterface) {
        this.deviceInterfaces.add(deviceInterface);
    }

    public void setDeviceDefaultParams(int i, DeviceResultInterface deviceResultInterface) {
        DeviceDefaultParams depthAndGainParamByLeve = FatConfigManager.getInstance().getDepthAndGainParamByLeve(i);
        Log.i(TAG, "==DataLength=" + BitmapUtil.sBitmapHight + "=====Depth=" + depthAndGainParamByLeve.getDepth() + "===Gain=" + depthAndGainParamByLeve.getGain() + "==Sendcycle=" + depthAndGainParamByLeve.getSendcycle() + "==Dynamic=" + depthAndGainParamByLeve.getDynamic());
        setDeviceDepth(depthAndGainParamByLeve.getDepth().intValue(), deviceResultInterface);
        setDeviceGain(depthAndGainParamByLeve.getGain().intValue(), deviceResultInterface);
        if (this.curDeviceCode == 0) {
            setDeviceSendCycle(depthAndGainParamByLeve.getSendcycle().intValue(), deviceResultInterface);
            setDeviceDynamic(depthAndGainParamByLeve.getDynamic().intValue(), deviceResultInterface);
        }
    }

    public void getDeviceVersionInfo(DeviceResultInterface deviceResultInterface) {
        if (checkDeviceConnectStatus(deviceResultInterface, Constant.DEVICE_VERSION)) {
            if (this.mOTGManager.isConnect()) {
                sendCmd(Constant.DEVICE_VERSION, 0);
            } else if (checkDeviceConnectStatus(deviceResultInterface, 432)) {
                sendCmd(432, -1);
            }
        }
    }

    public void getFlashFileData(DeviceResultInterface deviceResultInterface) {
        if (checkDeviceConnectStatus(deviceResultInterface, 437)) {
            if (this.mOTGManager.isConnect()) {
                sendCmd(Constant.DEVICE_VERSION, 0);
            } else if (checkDeviceConnectStatus(deviceResultInterface, 437)) {
                sendCmd(437, -1);
            }
        }
    }

    public void setDeviceDepth(int i, DeviceResultInterface deviceResultInterface) {
        if (checkDeviceConnectStatus(deviceResultInterface, MxsellaConstant.DEVICE_DLPF_M_VALUE)) {
            LogUtil.d("i=>" + i);
            sendCmd(MxsellaConstant.DEVICE_DLPF_M_VALUE, i);
            if (1 == this.curDeviceCode) {
                int i2 = (i * BitmapUtil.sBitmapHight * 2) + 165 + 24;
                sendCmd(MxsellaConstant.DEVICE_RXATE_DELAY, new byte[]{0, -91, (byte) ((i2 >> 8) & 255), (byte) (i2 & 255)});
            }
        }
    }

    public void setDeviceGain(int i, DeviceResultInterface deviceResultInterface) {
        if (checkDeviceConnectStatus(deviceResultInterface, MxsellaConstant.DEVICE_DLPF_PARA)) {
            sendCmd(MxsellaConstant.DEVICE_DLPF_PARA, i);
        }
    }

    private void sendCmd(int i, int i2) {
        if (this.mOTGManager.isConnect()) {
            this.mOTGManager.send(i, i2);
        }
    }

    public void setDeviceSampleLen(int i, DeviceResultInterface deviceResultInterface) {
        if (checkDeviceConnectStatus(deviceResultInterface, Constant.DEVICE_SAMPLE_LEN)) {
            sendCmd(Constant.DEVICE_SAMPLE_LEN, i);
            int curDeviceDepth = (FatConfigManager.getInstance().getCurDeviceDepth() * i * 2) + 165 + 24;
            sendCmd(Constant.DEVICE_RXATE_DELAY, new byte[]{0, -91, (byte) ((curDeviceDepth >> 8) & 255), (byte) (curDeviceDepth & 255)});
        }
    }

    public void setDeviceTxWave(int i, DeviceResultInterface deviceResultInterface) {
        if (checkDeviceConnectStatus(deviceResultInterface, Constant.DEVICE_TX_WAVE)) {
            sendCmd(Constant.DEVICE_TX_WAVE, i);
        }
    }

    public void setDeviceCutPoint(int i, DeviceResultInterface deviceResultInterface) {
        if (checkDeviceConnectStatus(deviceResultInterface, Constant.DEVICE_CUT_POINTS)) {
            sendCmd(Constant.DEVICE_CUT_POINTS, i);
        }
    }

    public void setDeviceLineNum(int i, DeviceResultInterface deviceResultInterface) {
        if (checkDeviceConnectStatus(deviceResultInterface, Constant.DEVICE_LINE_NUM)) {
            sendCmd(Constant.DEVICE_LINE_NUM, i);
        }
    }

    public void setDevicePulseWidth(int i, DeviceResultInterface deviceResultInterface) {
        if (checkDeviceConnectStatus(deviceResultInterface, Constant.DEVICE_PULSE_WIDTH)) {
            sendCmd(Constant.DEVICE_PULSE_WIDTH, i);
        }
    }

    private void sendCmd(int i, byte[] bArr) {
        if (this.mOTGManager.isConnect()) {
            this.mOTGManager.send(i, bArr);
        }
    }

    public void setDeviceRxGate(byte[] bArr, DeviceResultInterface deviceResultInterface) {
        if (checkDeviceConnectStatus(deviceResultInterface, Constant.DEVICE_RXATE_DELAY)) {
            sendCmd(Constant.DEVICE_RXATE_DELAY, bArr);
        }
    }

    public void setDeviceFpgaSpiDma(int i, DeviceResultInterface deviceResultInterface) {
        if (checkDeviceConnectStatus(deviceResultInterface, Constant.DEVICE_FPGA_SPI_DMA)) {
            sendCmd(Constant.DEVICE_FPGA_SPI_DMA, i);
        }
    }

    public void setDeviceAdcConf(byte[] bArr, DeviceResultInterface deviceResultInterface) {
        if (checkDeviceConnectStatus(deviceResultInterface, Constant.DEVICE_ADC_CONF)) {
            sendCmd(Constant.DEVICE_ADC_CONF, bArr);
        }
    }

    public void setDeviceAfePowerCtrl(int i, DeviceResultInterface deviceResultInterface) {
        if (checkDeviceConnectStatus(deviceResultInterface, Constant.DEVICE_AFE_POWER_CTRL)) {
            sendCmd(Constant.DEVICE_AFE_POWER_CTRL, i);
        }
    }

    public void setBlueSpeedRateLeve(int i) {
        this.blueSpeedRateLeve = i;
        SharedPreferencesUtil.saveInt(this.mContext, DATA_DEVICE_DATA_RATE_KEY, i);
    }

    public void setDeviceTxDataRate(final int i, final DeviceResultInterface deviceResultInterface) {
        if (checkDeviceConnectStatus((z, obj2) -> {
            if (z) {
                MxsellaDeviceManager.this.setBlueSpeedRateLeve(i);
            }
            DeviceResultInterface deviceResultInterface2 = deviceResultInterface;
            if (deviceResultInterface2 != null) {
                deviceResultInterface2.result(z, obj2);
            }
        }, Constant.DEVICE_TXDATA_RATE)) {
            sendCmd(Constant.DEVICE_TXDATA_RATE, i);
        }
    }
    public void sendDatas(byte[] buffer) {
       mOTGManager.sendDatas(buffer);
    }
    public void startUpgradeFirmware(DeviceResultInterface deviceResultInterface) {
        if (checkDeviceConnectStatus(deviceResultInterface, Constant.DEVICE_FIRMWARE_START)) {
            sendCmd(Constant.DEVICE_FIRMWARE_START, 1);
        }
    }

    public void restartFirmware(DeviceResultInterface deviceResultInterface) {
        if (checkDeviceConnectStatus(deviceResultInterface, Constant.DEVICE_FIRMWARE_RESTART)) {
            sendCmd(Constant.DEVICE_FIRMWARE_RESTART, 1);
        }
    }

    public void setDeviceLowPassFilter(byte[] bArr, DeviceResultInterface deviceResultInterface) {
        if (checkDeviceConnectStatus(deviceResultInterface, Constant.DEVICE_LOW_PASS_FILTER_COEFFICIENT)) {
            sendCmd(Constant.DEVICE_LOW_PASS_FILTER_COEFFICIENT, bArr);
        }
    }

    public void setDeviceImageDataEnable(boolean z, DeviceResultInterface deviceResultInterface) {
        LogUtil.d(z ? "禁用" : "打开");
        if (checkDeviceConnectStatus(deviceResultInterface, Constant.DEVICE_SETTING_DOWN_UPDATE)) {
            sendCmd(Constant.DEVICE_SETTING_DOWN_UPDATE, z ? 1 : 0);
        }
    }

    private void initDeviceParameter(DeviceMsg deviceMsg) {
        deviceMsg.getProtocolType();
        LogUtil.d("初始化设备参数");
        setDeviceImageDataEnable(false, (z, obj2) -> {
            if (curDeviceCode == 0) {
                setDeviceTGC(Constant.tgcArray, null);
            }
            setDeviceDefaultParams(FatConfigManager.getInstance().getCurDeviceDepthLeve(), null);
            getDeviceFlashId(null);
            setDeviceImageDataEnable(true, null);
        });
    }

    public void setDeviceTGC(byte[] bArr, DeviceResultInterface deviceResultInterface) {
        if (checkDeviceConnectStatus(deviceResultInterface, Constant.DEVICE_TGC)) {
            sendCmd(Constant.DEVICE_TGC, bArr);
        }
    }

    public void getDeviceFlashId(DeviceResultInterface deviceResultInterface) {
        if (checkDeviceConnectStatus(deviceResultInterface, Constant.DEVICE_FLASHID)) {
            sendCmd(Constant.DEVICE_FLASHID, 0);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventDevice(DeviceMsg deviceMsg) {
        Log.i(TAG, "===eventDevice=====msg=" + Integer.toHexString(deviceMsg.getMsgId()) + " data=" + deviceMsg.toString() + " result :" + deviceMsg.getError());
        int msgId = deviceMsg.getMsgId();
        boolean z = false;
        if (msgId == Constant.DEVICE_DISCONNECTED_MSG_ID) {
            Log.i(TAG, "msgId == -1");
            for (DeviceInterface deviceInterface : this.deviceInterfaces) {
                if (deviceInterface != null) {
                    Log.i(TAG, "deviceInterface != null");
                    deviceInterface.onDisconnected(0, null);
                }
            }
            for (Object obj2 : this.interfaceHashMap.keySet()) {
                if (this.interfaceHashMap.get(obj2) instanceof com.mxsella.fatmuscle.sdk.manager.MxsellaDeviceManager.DeviceResultInterface.DeviceCommonInterface) {
                    Log.i(TAG, "remove");
                    this.interfaceHashMap.remove(obj2).result(false, new DeviceMsg());
                }
            }
            this.interfaceHashMap.clear();
            return;
        }
        if (msgId == Constant.R_DEVICE_VERSION || msgId == Constant.DEVICE_VERSION) {
            Log.i(TAG, "msgId == 432");
            initDeviceParameter(deviceMsg);
            deviceMsg.setMsgId(Constant.DEVICE_VERSION);
        } else if (msgId == Constant.DEVICE_CONNECTED_MSG_ID) {
            Log.i(TAG, "msgId == 34952");
            for (DeviceInterface deviceInterface2 : this.deviceInterfaces) {
                if (deviceInterface2 != null) {
                    Log.i(TAG, "msgId == 349521");
                    deviceInterface2.onConnected();
                }
            }

            getDeviceVersionInfo((z2, obj3) -> {
                if (z2) {
                    Log.i(TAG, "msgId == 349522");
                    return;
                }
                getDeviceVersionInfo(null);
            });
            return;
        }
        DeviceResultInterface remove = this.interfaceHashMap.remove(Integer.valueOf(deviceMsg.getMsgId()));
        if (remove == null) {
            Log.i(TAG, "remove == null");
            for (DeviceInterface deviceInterface3 : this.deviceInterfaces) {
                deviceInterface3.onMessage(deviceMsg);
            }
            return;
        }
        remove.result((deviceMsg.getError() == 0 || deviceMsg.getError() == 8) ? true : true, deviceMsg);
    }

    public String getFlashId() {
        return this.flashId;
    }

    public void setDeviceSendCycle(int i, DeviceResultInterface deviceResultInterface) {
        int i2 = 0;
        int i3;
        if (checkDeviceConnectStatus(deviceResultInterface, MxsellaConstant.DEVICE_LINE_CYCLE)) {
            if (this.curDeviceCode == 0) {
                if (this.deviceVersion >= 20) {
                    i2 = this.ocxo;
                } else {
                    i3 = (this.ocxo * 10 * i) | 6553600;
                    sendCmd(MxsellaConstant.DEVICE_LINE_CYCLE, i3);
                }
            } else {
                i2 = this.ocxo;
            }
            i3 = i2 * i * 1000;
            sendCmd(MxsellaConstant.DEVICE_LINE_CYCLE, i3);
        }
    }

    public void setDeviceDynamic(int i, DeviceResultInterface deviceResultInterface) {
        if (checkDeviceConnectStatus(deviceResultInterface, MxsellaConstant.DEVICE_DR_PARA)) {
            sendCmd(MxsellaConstant.DEVICE_DR_PARA, ((64 - (7650 / i)) << 16) | (23029 / i));
        }
    }

    private boolean checkDeviceConnectStatus(DeviceResultInterface deviceResultInterface, int i) {
        if (!isConnect()) {
            LogUtil.d("未连接");
            if (deviceResultInterface != null) {
                LogUtil.d("禁用2");
                deviceResultInterface.result(false, null);
            }
            return false;
        } else if (deviceResultInterface != null) {
            this.interfaceHashMap.put(Integer.valueOf(i), deviceResultInterface);
            return true;
        } else {
            return true;
        }
    }

    public boolean isConnect() {
        return this.mOTGManager.isConnect();
    }

    public int getOcxo() {
        return this.ocxo;
    }

    public int getBlueSpeedRateLeve() {
        return 0;
    }

    public boolean isToBusinessVersion() {
        String str = this.deviceMode;
        return str != null && "130".equalsIgnoreCase(str);
    }

    public void disConnectDevice() {
        //去除断开蓝牙连接部分
        if (this.mOTGManager.isConnect()) {
            this.mOTGManager.closeSession();
        }
    }

    public static MxsellaDeviceManager getInstance() {
        if (sMxsellaDeviceManager == null) {
            synchronized (obj) {
                if (sMxsellaDeviceManager == null) {
                    sMxsellaDeviceManager = new MxsellaDeviceManager(MyApplication.getInstance());
                }
            }
        }
        return sMxsellaDeviceManager;
    }


    public interface DeviceInterface {
        void onConnected();

        void onDisconnected(int i, String str);

        void onMessage(DeviceMsg deviceMsg);
    }

    public void setFlashId(String str) {
        this.flashId = str;
        SharedPreferencesUtil.savaString(this.mContext, DATA_DEVICE_FLASH_KEY, str);
    }

    public int getDeviceVersion() {
        return this.deviceVersion;
    }


    public void setDeviceMode(String str) {
        this.deviceMode = str;
        SharedPreferencesUtil.savaString(this.mContext, DATA_DEVICE_MODE_KEY, str);
    }

    public void notityMessage(DeviceMsg deviceMsg) {
        EventBus.getDefault().post(deviceMsg);
    }

    public void setDeviceIdentification(String str) {
        this.deviceIdentification = str;
        SharedPreferencesUtil.savaString(this.mContext, DATA_DEVICE_NAME_KEY, str);
    }

    public void setDeviceVersion(int i) {
        this.deviceVersion = i;
        SharedPreferencesUtil.saveInt(this.mContext, DATA_DEVICE_VERSION_KEY, i);
    }

    private MxsellaDeviceManager(Context context) {
        this.deviceVersion = -1;
        this.curDeviceCode = -1;
        this.blueSpeedRateLeve = 0;
        this.mContext = context;
        if (Constant.supportOnlyZ1OrZ2 != -1) {
            SharedPreferencesUtil.saveInt(this.mContext, CUR_SEL_DEVICE_CODE, Constant.supportOnlyZ1OrZ2);
        }
        EventBus.getDefault().register(this);
        initBroadCast();
        OTGManager oTGManager = new OTGManager(this.mContext);
        this.mOTGManager = oTGManager;
        oTGManager.setOTGManagerListerner(this);
        BitmapUtil.initList();
        this.deviceMode = SharedPreferencesUtil.getString(this.mContext, DATA_DEVICE_MODE_KEY, this.deviceMode);
        this.deviceIdentification = SharedPreferencesUtil.getString(this.mContext, DATA_DEVICE_NAME_KEY, this.deviceIdentification);
        this.deviceVersion = SharedPreferencesUtil.getInt(this.mContext, DATA_DEVICE_VERSION_KEY, this.deviceVersion);
        this.flashId = SharedPreferencesUtil.getString(this.mContext, DATA_DEVICE_FLASH_KEY, this.flashId);
        this.blueSpeedRateLeve = SharedPreferencesUtil.getInt(this.mContext, DATA_DEVICE_DATA_RATE_KEY, this.blueSpeedRateLeve);
        this.curDeviceCode = 0;
        BitmapUtil.sBitmapHight = OTGManager.BITMAP_WIDTH;
    }

    private void initBroadCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_USB_DETACHED_PERMISSION);
        intentFilter.addAction(ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(ACTION_USB_ACCESSORY_ATTACHED);
        intentFilter.addAction(ACTION_USB_PERMISSION);
        this.mContext.registerReceiver(this.broadcastReceiver, intentFilter);
    }

    @Override
    public void onCmdMessage(int i, byte[] bArr, int i2, DataTransListerner.ProtocolType protocolType) {
        LogUtil.d("i => " + i);
        if (i != Constant.DEVICE_DISCONNECTED_MSG_ID) {
            if (i != Constant.R_DEVICE_VERSION) {
                if (i != Constant.DEVICE_FIRMWARE_UPDATE) {
                    if (i != Constant.DEVICE_PULSE_WIDTH) {
                        if (i == Constant.DEVICE_CONNECTED_MSG_ID) {
                            setFlashId(null);
                            setDeviceMode(null);
                            DeviceMsg deviceMsg = new DeviceMsg();
                            deviceMsg.setMsgId(i);
                            deviceMsg.setProtocolType(protocolType);
                            notityMessage(deviceMsg);
                            return;
                        } else if (i != Constant.DEVICE_VERSION) {
                            if (i == Constant.DEVICE_FLASHID) {
                                FlashMsg flashMsg = new FlashMsg();
                                flashMsg.unpack(bArr);
                                flashMsg.setMsgId(i);
                                flashMsg.setError(i2);
                                notityMessage(flashMsg);
                                setFlashId(flashMsg.getUuid());
                                return;
                            }
                            switch (i) {
                                case Constant.DEVICE_DLPF_M_VALUE:
                                case Constant.DEVICE_DLPF_PARA:
                                case Constant.DEVICE_CUT_POINTS:
                                case Constant.DEVICE_DR_PARA:
                                case Constant.DEVICE_LINE_NUM:
                                case Constant.DEVICE_LINE_CYCLE:
                                    break;
                                default:
                                    DeviceMsg deviceMsg2 = new DeviceMsg();
                                    deviceMsg2.setMsgId(i);
                                    deviceMsg2.setError(i2);
                                    deviceMsg2.setContentArray(bArr);
                                    notityMessage(deviceMsg2);
                                    return;
                            }
                        }
                    }
                    ResultMsg resultMsg = new ResultMsg();
                    resultMsg.unpack(bArr);
                    resultMsg.setMsgId(i);
                    resultMsg.setError(i2);
                    notityMessage(resultMsg);
                    return;
                }
                FirmwareUpdateMsg firmwareUpdateMsg = new FirmwareUpdateMsg();
                firmwareUpdateMsg.setMsgId(Constant.DEVICE_FIRMWARE_UPDATE);
                firmwareUpdateMsg.setError(i2);
                firmwareUpdateMsg.unpack(bArr);
                notityMessage(firmwareUpdateMsg);
                return;
            }
            VersionMsg versionMsg = new VersionMsg();
            versionMsg.setProtocolType(protocolType);
            versionMsg.unpack(bArr);
            versionMsg.setMsgId(i);
            versionMsg.setError(i2);
            this.compileTime = ( "20" + versionMsg.getYear()) + "/" + versionMsg.getMonth() + "/" + versionMsg.getDay();
            if (this.curDeviceCode == 0) {
                setDeviceMode(versionMsg.getMode() + "");
            }
            if (versionMsg.getIdentificationCode() > 0) {
                setDeviceIdentification(versionMsg.getIdentificationCode() + "");
            }
            setDeviceVersion(versionMsg.getVersionNumber());
            if ((versionMsg.getVersionNumber() == 255 || i2 != 8)) {
                setDeviceVersion(0);
                this.compileTime = "";
            }
            notityMessage(versionMsg);
            return;
        }
        DeviceMsg deviceMsg3 = new DeviceMsg();
        deviceMsg3.setMsgId(i);
        deviceMsg3.setProtocolType(protocolType);
        notityMessage(deviceMsg3);
    }

    @Override
    public void onImageData(byte[] bArr, BitmapMsg.State state, ProtocolType protocolType) {
        LogUtil.d("图像数据");
        if (this.isEnd && state == BitmapMsg.State.RUN) {
            return;
        }
        if (state == BitmapMsg.State.START) {
            this.isEnd = false;
        }
        BitmapUtil.sBitmapHight = OTGManager.BITMAP_WIDTH;
        this.ocxo = 32;
        BitmapMsg bitmapMsg = new BitmapMsg();
        this.bitmapMsg = bitmapMsg;
        bitmapMsg.setProtocolType(protocolType);
        this.bitmapMsg.unpack(bArr);
        this.bitmapMsg.setContentArray(bArr);
        this.bitmapMsg.setState(state);
        this.bitmapMsg.setMsgId(Constant.DEVICE_IMAGE_DATA);
        this.mBitmapMsg = this.bitmapMsg;
        if (state == BitmapMsg.State.START) {
            BitmapUtil.initList();
        } else {
            BitmapMsg.State state2 = BitmapMsg.State.END;
        }
        notityMessage(this.bitmapMsg);

    }
}
