package com.cw.fatmuscle.sdk.manager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import com.cw.fatmuscle.common.MyApplication;
import com.cw.fatmuscle.sdk.common.MxsellaConstant;
import com.cw.fatmuscle.sdk.fat.entity.DeviceInfo;
import com.cw.fatmuscle.sdk.fat.entity.DeviceTcpMsg;
import com.cw.fatmuscle.sdk.fat.entity.DeviceUdpMsg;
import com.cw.fatmuscle.sdk.service.DaemonThreads;
import com.cw.fatmuscle.sdk.service.DeviceImageDataTcpService;
import com.cw.fatmuscle.sdk.service.DeviceUdpSerialDataService;
import com.cw.fatmuscle.sdk.util.ThreadUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.videoio.Videoio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MxsellaDeviceManager {
    private static final String DATA_DEVICE_IP_KEY = "device_ip";
    private static final String DATA_DEVICE_MAC_KEY = "device_mac";
    private static final String DATA_DEVICE_MODE_KEY = "deviceMode";
    private static final String DATA_DEVICE_NAME_KEY = "deviceIdentification";
    private static final String DATA_DEVICE_SN_KEY = "device_sn";
    private static MxsellaDeviceManager deviceManager;
    private static Object obj = new Object();
    private Intent connectServiceIntent;
    private byte[] data;
    private String deviceIdentification;
    private DeviceImageDataTcpService deviceImageDataTcpService;
    private String deviceIp;
    private String deviceMac;
    private String deviceMode;
    private DeviceUdpSerialDataService deviceSerialDataService;
    private String deviceSn;
    private SharedPreferences sharedPreferences;
    private IMAGEMODE imagemode = IMAGEMODE.B_FREE;
    private IMAGETYPE imagetype = IMAGETYPE.IMAGE;
    private List<DeviceInterface> deviceInterfaces = new ArrayList();
    private List<ReceivingImageDataInterface> receivingImageDataInterfaces = new ArrayList();
    private ConcurrentHashMap<Object, DeviceResultInterface.BaseInterface> interfaceHashMap = new ConcurrentHashMap<>();
    private int IMG_WIDTH = 640;
    private int IMG_HEIGHT = Videoio.CAP_PROP_XI_CC_MATRIX_01;
    Bitmap imageBitmap = null;
    private StatusType statusType = StatusType.Normal;
    private boolean isConnected = false;
    private boolean isReConnect = false;
    private Map deviceInfos = new HashMap();
    private long msgReceiveTime = 0;
    int algoLeve = 0;
    private boolean isEnd = false;

    private DaemonThreads daemonThreads = null;

    
    public interface DeviceInterface {

        
        public enum ConnType {
            TCP,
            UDP
        }

        void onConnected(ConnType connType);

        void onDisconnected(int i, String str, ConnType connType);

        void onTcpMessage(DeviceTcpMsg deviceTcpMsg);

        void onUdpMessage(DeviceUdpMsg deviceUdpMsg);
    }

    public boolean isConnected() {
        return false;
    }
    public String getDeviceIp() {
        return this.deviceIp;
    }
    public void receiveByteData(byte[] bArr) {
        EventBus.getDefault().post(bArr);
    }

    public void onUdpConnected() {
        DeviceUdpMsg deviceUdpMsg = new DeviceUdpMsg();
        deviceUdpMsg.setCmd(MxsellaConstant.DEVICE_UDP_CONNECTED_MSG_ID);
        EventBus.getDefault().post(deviceUdpMsg);
    }
    public void connectDevice() {
        if (isConnected()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 26) {
            getInstance().reconnectDevice();
        } else {
            MyApplication.getInstance().startService(this.connectServiceIntent);
        }
    }
    public void reconnectDevice() {
        if (isConnected()) {
            return;
        }
        receiveUdpPackage();
        this.statusType = StatusType.Normal;
        synchronized (obj) {
            DaemonThreads daemonThreads = this.daemonThreads;
            if (daemonThreads == null || !daemonThreads.isRunning()) {
                this.daemonThreads = new DaemonThreads();
            }
        }
        this.isConnected = true;
        synchronized (obj) {
//            connectDeviceTcp(true);
            if (!SdkManager.isSupportMedical) {
                Log.i("sdk MxsellaDeviceManager ","isSupportMedical");
//                connectUltraTcp(true);
            }
        }
        this.msgReceiveTime = System.currentTimeMillis();
    }

    public void receiveImageData(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        this.imageBitmap = bitmap;
        EventBus.getDefault().post(bitmap);
    }

    public void receiveUdpPackage() {
        synchronized (obj) {
            DeviceUdpSerialDataService deviceUdpSerialDataService = this.deviceSerialDataService;
            if (deviceUdpSerialDataService == null || !deviceUdpSerialDataService.isRunning()) {
                this.deviceInfos.clear();
                DeviceUdpSerialDataService deviceUdpSerialDataService2 = new DeviceUdpSerialDataService(this.deviceIp, 5001, this);
                this.deviceSerialDataService = deviceUdpSerialDataService2;
                ThreadUtils.execute(deviceUdpSerialDataService2);
            }
        }
    }
    public int getAlgoLeve() {
        return this.algoLeve;
    }


    public void onStopped(int i, String str, DeviceInterface.ConnType connType) {

        DeviceUdpMsg deviceUdpMsg = new DeviceUdpMsg();
        deviceUdpMsg.setCmd(MxsellaConstant.DEVICE_UDP_DISCONNECTED_MSG_ID);
        deviceUdpMsg.setContent(str);
        deviceUdpMsg.setErrorCode(Integer.valueOf(i));
        EventBus.getDefault().post(deviceUdpMsg);
    }

    public void onMessage(int i, String str, int i2, DeviceInterface.ConnType connType) {
        Log.i("", "==receive: connType:" + connType + " content:" + str + " port:" + i2);
        try {
            this.isConnected = true;
            this.msgReceiveTime = System.currentTimeMillis();

            try {
                JSONObject jSONObject = new JSONObject(str);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (i != 4261) {
                if (i == 17476) {

                } else if (i == 24739) {
                    this.statusType = StatusType.Upgrade;
                } else if (i == 24741) {
                    JSONObject jSONObject2 = new JSONObject(str);
                    if (!jSONObject2.isNull("result") && jSONObject2.getString("result").contains("OK")) {

                    }
                    this.statusType = StatusType.Normal;
                }
            }
        } catch (JSONException e6) {
            e6.printStackTrace();
        }
    }

    public void setDeviceMac(String str) {
        this.deviceMac = str;
        this.sharedPreferences.edit().putString(DATA_DEVICE_MAC_KEY, str).commit();
    }
    public boolean isEnd() {
        return this.isEnd;
    }

    public void setEnd(boolean z) {
        this.isEnd = z;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventDevice(DeviceTcpMsg deviceTcpMsg) throws IOException {
        DeviceImageDataTcpService deviceImageDataTcpService;
        Log.i("eventDevice ","==main TCP UI receive: msg:" + deviceTcpMsg);
        int msgId = deviceTcpMsg.getMsgId();
        if (msgId == -1) {
            for (DeviceInterface deviceInterface : this.deviceInterfaces) {
                if (deviceInterface != null) {
                    deviceInterface.onDisconnected(deviceTcpMsg.getErrcode(), deviceTcpMsg.getContent(), DeviceInterface.ConnType.TCP);
                }
            }
            for (Object obj2 : this.interfaceHashMap.keySet()) {
                if (this.interfaceHashMap.get(obj2) instanceof DeviceResultInterface.DeviceCommonInterface) {
                    ((DeviceResultInterface.DeviceCommonInterface) this.interfaceHashMap.remove(obj2)).result(deviceTcpMsg.getContent(), false);
                }
            }
            this.interfaceHashMap.clear();
            DaemonThreads daemonThreads = this.daemonThreads;
            if (daemonThreads == null || daemonThreads.isRunning()) {
                return;
            }
            ThreadUtils.execute(this.daemonThreads);
        } else if (msgId == 0) {
            for (DeviceInterface deviceInterface2 : this.deviceInterfaces) {
                if (deviceInterface2 != null) {
                    deviceInterface2.onConnected(DeviceInterface.ConnType.TCP);
                }
            }
        } else {
            if (deviceTcpMsg.getMsgId() == 28850 && (deviceImageDataTcpService = this.deviceImageDataTcpService) != null && deviceImageDataTcpService.isRunning()) {
                this.deviceImageDataTcpService.close();
            }
            DeviceResultInterface.DeviceCommonInterface deviceCommonInterface = (DeviceResultInterface.DeviceCommonInterface) this.interfaceHashMap.remove(Integer.valueOf(deviceTcpMsg.getMsgId()));
            if (deviceCommonInterface != null) {
                deviceCommonInterface.result(deviceTcpMsg.getContent(), deviceTcpMsg.isSuccess());
                return;
            }
            for (DeviceInterface deviceInterface3 : this.deviceInterfaces) {
                deviceInterface3.onTcpMessage(deviceTcpMsg);
            }
            if (getInstance().getDeviceMac() == null && deviceTcpMsg.getMsgId() == 4261) {
                getDeviceMacInfo((DeviceResultInterface.DeviceCommonInterface) (str, z) -> {
                    if (z) {
                        MxsellaDeviceManager.this.setDeviceMac(str);
                    }
                });
            }
        }
    }
    public void getDeviceMacInfo(DeviceResultInterface.DeviceCommonInterface deviceCommonInterface) {
        if (checkDeviceConnectStatus(deviceCommonInterface)) {
            this.interfaceHashMap.put(Integer.valueOf((int) MxsellaConstant.DEVICE_MAC_INFO_MSG_ID), deviceCommonInterface);
            DeviceTcpMsg deviceTcpMsg = new DeviceTcpMsg();
            deviceTcpMsg.setMsgId(MxsellaConstant.DEVICE_MAC_INFO_MSG_ID);
//            sendMsg(deviceTcpMsg);
        }
    }


    private boolean checkDeviceConnectStatus(DeviceResultInterface.DeviceCommonInterface deviceCommonInterface) {
        boolean isConnected = isConnected();
        if (!isConnected) {
            deviceCommonInterface.result("", false);
        }
        return isConnected;
    }

    public String getDeviceMac() {
        String str = this.deviceMac;
        if (str == null) {
            this.deviceMac = this.sharedPreferences.getString(DATA_DEVICE_MAC_KEY, str);
        }
        return this.deviceMac;
    }

    public interface DeviceResultInterface {

        
        public interface BaseInterface {
        }

        
        public interface DeviceCommonInterface extends BaseInterface {
            void result(String str, boolean z);
        }

        
        public interface DeviceUpgradeInterface extends DeviceCommonInterface {
            void onUploadProgress(String str, long j, long j2);
        }
    }

    
    public enum IMAGEMODE {
        B_FREE,
        B_REAL,
        THR_FREE,
        THR_REAL,
        FOUR_FREE,
        FOUR_REAL
    }

    
    public enum IMAGETYPE {
        IMAGE,
        VIDEO
    }

    
    public interface ReceivingImageDataInterface {
        void receiveByteBitmat(byte[] bArr);

        void receiveImageBitmat(Bitmap bitmap);
    }

    public enum StatusType {
        Normal,
        Upgrade
    }

    public void onError(Exception exc) {
    }

    public Map<String, DeviceInfo> getDeviceInfos() {
        return this.deviceInfos;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public String getDeviceMode() {
        return this.deviceMode;
    }

    public static MxsellaDeviceManager getInstance() {
        if (deviceManager == null) {
            synchronized (obj) {
                if (deviceManager == null) {
                    deviceManager = new MxsellaDeviceManager();
                }
            }
        }
        return deviceManager;
    }

}
