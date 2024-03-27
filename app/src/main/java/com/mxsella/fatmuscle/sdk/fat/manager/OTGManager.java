package com.mxsella.fatmuscle.sdk.fat.manager;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;


import com.mxsella.fatmuscle.MainActivity;
import com.mxsella.fatmuscle.SendData;
import com.mxsella.fatmuscle.common.Config;
import com.mxsella.fatmuscle.common.MyApplication;
import com.mxsella.fatmuscle.sdk.common.Constant;
import com.mxsella.fatmuscle.sdk.fat.entity.BitmapMsg;
import com.mxsella.fatmuscle.sdk.fat.entity.DeviceMsg;
import com.mxsella.fatmuscle.sdk.fat.entity.FirmwareUpdateMsg;
import com.mxsella.fatmuscle.sdk.fat.inter.DataTransListerner;
import com.mxsella.fatmuscle.sdk.fat.interfaces.IDataTransfer;
import com.mxsella.fatmuscle.utils.ByteUtil;
import com.mxsella.fatmuscle.utils.LogUtil;

import org.opencv.videoio.Videoio;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OTGManager implements IDataTransfer {
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private HashMap<String, UsbDevice> deviceList;
    private int fimwareAllSize;
    private int fimwareCurrentSize;
    private PendingIntent intent;
    private Context mContext;
    private DataTransListerner mDataTransListerner;
    private ReceiveThread mReceiveThread;
    private SendThread mSendThread;
    private UsbDevice usbDevice;
    private UsbEndpoint usbEpIn;
    private UsbInterface usbInterface;
    private UsbManager usbManager;
    private boolean isRunning = false;
    private Queue<DeviceMsg> msgsQueue = new ConcurrentLinkedQueue();
    private Queue<DeviceMsg> fimwareQueue = new ConcurrentLinkedQueue();
    private long currentTime = 0;
    private boolean isRequestPermission = false;
    private boolean isFimwareFlag = false;

    public static final int BITMAP_WIDTH = 496;
    private static final int MAGIC_NUMBER = 9188;
    private static final String TAG = "OTGManager";
    private UsbDeviceConnection deviceConnection;
    private UsbEndpoint usbEpOut;

    BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "onReceive: " + action);
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    Log.i(TAG, "onReceive: 111111111111");
                    UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//                    UsbDevice usbDevice = MainActivity.usbDevice;
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        LogUtil.i("-> 1111");
                        if (usbDevice.getVendorId() == Config.VENDOR_ID && usbDevice.getProductId() == Config.PRODUCT_Id) {
                            LogUtil.i("-> 1112");
                            OTGManager.this.usbDevice = usbDevice;
                        }
                        findInterface();
                        start();
                    }
                }
            }
        }
    };

    public void sendFimware(DeviceMsg deviceMsg) {
        if (this.isRunning) {
            this.fimwareQueue.add(deviceMsg);
            return;
        }
        DataTransListerner dataTransListerner = this.mDataTransListerner;
        if (dataTransListerner != null) {
            dataTransListerner.onCmdMessage(deviceMsg.getMsgId(), new byte[4], 1, DataTransListerner.ProtocolType.OTG);
        }
    }

    public void findInterface() {
        UsbDevice usbDevice = this.usbDevice;
        if (usbDevice == null) {
            return;
        }
        if (usbDevice.getInterfaceCount() > 0) {
            UsbInterface usbInterface = this.usbDevice.getInterface(0);
            this.usbInterface = usbInterface;
            Log.i(TAG, "22" + usbInterface.toString());
        }
        getEndpoint(this.usbInterface);
        if (this.usbInterface != null) {
            Log.i(TAG, "usbInterface not null == " + (usbDevice == null ? "null -> " : "not null -> ") + usbManager.hasPermission(usbDevice));
            HashMap<String, UsbDevice> deviceList1 = usbManager.getDeviceList();
            for (int i = 0; i < deviceList1.size(); i++) {
                UsbDevice usbDevice1 = deviceList1.get(i);
                System.out.println("device => " + usbDevice1);
            }
            if (usbManager.hasPermission(usbDevice)) {
                Log.i(TAG, "已经获得权限");
                UsbDeviceConnection openDevice = this.usbManager.openDevice(usbDevice);
                boolean b = openDevice.claimInterface(this.usbInterface, true);
                Log.i(TAG, "打开设备" + (openDevice != null ? "true" : "false") + " claimInterface-> " + b);
                if (openDevice == null) {
                    Log.i(TAG, "设备连接为空");
                } else if (openDevice != null && openDevice.claimInterface(this.usbInterface, true)) {
                    this.deviceConnection = openDevice;
                    Log.i(TAG, "连接状态-> " + (openDevice != null ? "true" : "false"));
                } else {
                    Log.i(TAG, "关闭连接 ");
                    openDevice.close();
                }
            } else if (isRequestPermission) {
                Log.d(TAG, "已经请求权限");
            } else {
                Log.d(TAG, "请求权限");
                isRequestPermission = true;
                this.usbManager.requestPermission(this.usbDevice, this.intent);
            }
        }
    }

    private boolean isSendFirmwareData = false;

    public void setOTGManagerListerner(DataTransListerner dataTransListerner) {
        this.mDataTransListerner = dataTransListerner;
    }

    public void setSendFirmwareData(boolean z) {
        this.isSendFirmwareData = z;
        if (z) {
            return;
        }
        this.fimwareQueue.clear();
    }

    public OTGManager(Context context) {
        this.mContext = context;
        this.intent = PendingIntent.getBroadcast(MyApplication.getInstance(), 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_USB_PERMISSION);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(this.mUsbReceiver, intentFilter);

    }

    public void send(DeviceMsg deviceMsg) {
        if (this.isRunning) {
            this.msgsQueue.add(deviceMsg);
            return;
        }
        DataTransListerner dataTransListerner = this.mDataTransListerner;
        if (dataTransListerner != null) {
            dataTransListerner.onCmdMessage(deviceMsg.getMsgId(), new byte[4], 1, DataTransListerner.ProtocolType.OTG);
        }
    }

    public void send(int i, int i2) {
        LogUtil.d("不添加数据数组");
        DeviceMsg deviceMsg = new DeviceMsg();
        deviceMsg.setMsgId(i);
        deviceMsg.setContent(i2);
        send(deviceMsg);
    }

    public void send(int i, byte[] bArr) {
        LogUtil.d("添加数据数组");
        DeviceMsg deviceMsg = new DeviceMsg();
        deviceMsg.setMsgId(i);
        deviceMsg.setContentArray(bArr);
        send(deviceMsg);
    }


    private void getEndpoint(UsbInterface usbInterface) {
        Log.i(TAG, "getEndpoint1: " + usbInterface.getEndpointCount());
        for (int endpointCount = usbInterface.getEndpointCount() - 1; endpointCount >= 0; endpointCount--) {
            UsbEndpoint endpoint = usbInterface.getEndpoint(endpointCount);
            Log.i(TAG, "getEndpoint: " + endpoint.getType());
            if (endpoint.getType() == 2) {
                if (endpoint.getDirection() == 0) {
                    LogUtil.d("1");
                    this.usbEpOut = endpoint;
                } else {
                    LogUtil.d("2");
                    this.usbEpIn = endpoint;
                }
            }
        }
    }

    public boolean start() {
        LogUtil.i("开始" + isRunning + "连接状态 -> " + (deviceConnection == null ? "null" : "not null"));
        if (!this.isRunning && this.deviceConnection != null) {
            LogUtil.d("非运行状态已连接");
            SendThread sendThread = this.mSendThread;
            if (sendThread != null && sendThread.isRunning) {
                this.mSendThread.close();
            }
            this.mSendThread = new SendThread();
            ReceiveThread receiveThread = this.mReceiveThread;
            if (receiveThread != null && receiveThread.isRunning) {
                this.mReceiveThread.close();
            }
            this.mReceiveThread = new ReceiveThread();
            this.isRunning = true;
            send(SendData.no1());
            send(SendData.no2());
            send(SendData.no3());
            send(SendData.no4());
            send(SendData.no5());
            send(SendData.no6());
            send(SendData.no7());
            send(SendData.no8());
            send(SendData.no9());
            this.mSendThread.start();
            this.mReceiveThread.start();
        }
        if (this.isRunning) {
            LogUtil.d("运行状态");
            DataTransListerner dataTransListerner = this.mDataTransListerner;
            if (dataTransListerner != null) {
                dataTransListerner.onCmdMessage(Constant.DEVICE_CONNECTED_MSG_ID, new byte[0], 0, DataTransListerner.ProtocolType.OTG);
            }
        } else {
            LogUtil.d("非运行状态");
            DataTransListerner dataTransListerner2 = this.mDataTransListerner;
            if (dataTransListerner2 != null) {
                LogUtil.d("dataTransListerner2 != null");
                dataTransListerner2.onCmdMessage(-1, new byte[0], 0, DataTransListerner.ProtocolType.OTG);
            }
        }
        LogUtil.i("运行状态:" + isRunning);
        return this.isRunning;
    }

    @Override
    public void onSendTimeOut(int i, DeviceMsg deviceMsg) {

    }

    @Override
    public void sendRestransmissionData(DeviceMsg deviceMsg) {
        Log.i(TAG, "sendRestransmissionData: " + deviceMsg.getMsgId());
        sendDatas(deviceMsg);
    }

    public boolean sendDatas(DeviceMsg deviceMsg) {
        if (this.deviceConnection == null) {
            return false;
        }
        byte[] protocolBytes = deviceMsg.getProtocolBytes();
        int bulkTransfer = this.deviceConnection.bulkTransfer(this.usbEpOut, protocolBytes, protocolBytes.length, 3000);
        Log.i(TAG, "sendDatas: result:" + bulkTransfer);
        return bulkTransfer > 0;
    }

    static UsbDeviceConnection getConnection(OTGManager otgManager) {
        return otgManager.deviceConnection;
    }

    static UsbEndpoint getUsbEpIn(OTGManager otgManager) {
        return otgManager.usbEpIn;
    }

    static DataTransListerner setDataTransListerner(OTGManager otgManager) {
        return otgManager.mDataTransListerner;
    }

    static int setFirmwareCurrentSize(OTGManager otgManager) {
        return otgManager.fimwareCurrentSize;
    }

    static boolean getFlag(OTGManager otgManager, boolean flag) {
        otgManager.isFimwareFlag = flag;
        return flag;
    }

    static int getFirmWareAllSize(OTGManager otgManager) {
        return otgManager.fimwareAllSize;
    }

    private class ReceiveThread extends Thread {
        private boolean isRunning;

        private ReceiveThread() {
            this.isRunning = true;
        }

        public void close() {
            this.isRunning = false;
        }

        public void run() {
            super.run();
            Log.i("OTGManager", "run: ReceiveThread");
            try {
                try {
                    byte[] bArr = new byte[512];
                    while (this.isRunning) {
                        int i = 2;
                        int i1 = deviceConnection.bulkTransfer(usbEpIn, bArr, 512, 3000);
                        LogUtil.d("读取otg 设备数据 => " + Arrays.toString(bArr) + "数据长度" + i1);
                        if (i1 > 0) {
                            currentTime = SystemClock.uptimeMillis();
                            if (ByteUtil.getIntByShort(bArr, 2) == 9188 && ByteUtil.getIntByShort(bArr, 4) == 4261) {
                                byte[] bArr2 = new byte[496];
                                System.arraycopy(bArr, 13, bArr2, 0, 496);
                                byte b = bArr[12];
                                BitmapMsg.State state = BitmapMsg.State.RUN;
                                if (b == 16) {
                                    Log.i("OTGManager", "run: start");
                                    state = BitmapMsg.State.START;
                                } else if (b == 1) {
                                    Log.i("OTGManager", "run: end");
                                    state = BitmapMsg.State.END;
                                }
                                LogUtil.e("-->1");
                                if (mDataTransListerner != null) {
                                    LogUtil.e("-->2");
                                    mDataTransListerner.onImageData(bArr2, state, DataTransListerner.ProtocolType.OTG);
                                }
                            } else if (ByteUtil.getIntByShort(bArr, 2) == 9188 && ByteUtil.getIntByShort(bArr, 4) == 4277 && MxsellaDeviceManager.getInstance().getDeviceVersion() < 20) {
                                if (bArr[8] == 0) {
                                    if ((bArr[18] & 255) == 170) {
                                        if (bArr[8] != 0) {
                                            if ((bArr[18] & 255) == 85) {
                                                i = 3;
                                            }
                                            LogUtil.i("=====================Receive=fimwareCurrentSize=" + fimwareCurrentSize + " error=" + i);
                                            isFimwareFlag = true;
                                            int intByShort = ByteUtil.getIntByShort(bArr, 4);
                                            byte[] bArr3 = new byte[1];
                                            if (fimwareAllSize > 0) {
                                                LogUtil.e("-->3");
                                                bArr3[0] = (byte) ((fimwareCurrentSize * 100) / fimwareAllSize);
                                            }
                                            if (mDataTransListerner != null) {
                                                LogUtil.e("-->4");
                                                LogUtil.i("======Receive=onCmdMessage=" + Arrays.toString(bArr3) + " error=" + i);
                                                mDataTransListerner.onCmdMessage(intByShort, bArr3, i, DataTransListerner.ProtocolType.OTG);
                                            }
                                        }
                                    }
                                    i = 0;
                                    if (bArr[8] != 0) {
                                        LogUtil.e("-->5");
                                    }
                                } else {
                                    if (bArr[8] == 170) {
                                        if (bArr[8] != 0) {
                                            LogUtil.e("-->6");
                                        }
                                    }
                                    i = 0;
                                    if (bArr[8] != 0) {
                                        LogUtil.e("-->7");
                                    }
                                }
                            } else if (ByteUtil.getIntByShort(bArr, 2) == 9188) {
                                LogUtil.e("-->8");
                                int intByShort2 = ByteUtil.getIntByShort(bArr, 4);
                                int intByShort3 = ByteUtil.getIntByShort(bArr, 6);
                                Log.i("OTGManager", "receiveCmd: msgId:" + Integer.toHexString(intByShort2) + " length:" + intByShort3);
                                byte[] bArr4 = new byte[intByShort3];
                                System.arraycopy(bArr, 8, bArr4, 0, intByShort3);
                                if (mDataTransListerner != null) {
                                    LogUtil.e("-->9");
                                    LogUtil.i("======Receive=onCmdMessage=" + Arrays.toString(bArr4) + " error=" + i);
                                    mDataTransListerner.onCmdMessage(intByShort2, bArr4, 0, DataTransListerner.ProtocolType.OTG);
                                }
                            }
                        }
                    }
                    LogUtil.e("-->10");
                    close();
                    this.isRunning = false;
                    new DeviceMsg().setMsgId(-1);
                    if (mDataTransListerner == null) {
                        LogUtil.e("-->11");
                        return;
                    }
                    mDataTransListerner.onCmdMessage(-1, new byte[0], 1, DataTransListerner.ProtocolType.OTG);
                } catch (Exception e) {
                    LogUtil.e("-->12");
                    Log.e("OTGManager", "run: " + e.getMessage());
                    e.printStackTrace();
                    close();
                    this.isRunning = false;
                    new DeviceMsg().setMsgId(-1);
                    if (mDataTransListerner == null) {
                        LogUtil.e("-->15");
                        return;
                    }
                    mDataTransListerner.onCmdMessage(-1, new byte[0], 1, DataTransListerner.ProtocolType.OTG);
                }
            } catch (Throwable th) {
                close();
                LogUtil.e("-->13");
                this.isRunning = false;
                new DeviceMsg().setMsgId(-1);
                if (mDataTransListerner != null) {
                    LogUtil.e("-->14");
                    mDataTransListerner.onCmdMessage(-1, new byte[0], 1, DataTransListerner.ProtocolType.OTG);
                }
                throw th;
            }
            LogUtil.e("-->16");
        }
    }


    public void closeSession() {
        this.isRequestPermission = false;
        this.isRunning = false;
        Queue<DeviceMsg> queue = this.fimwareQueue;
        if (queue != null) {
            queue.clear();
        }
        Queue<DeviceMsg> queue2 = this.msgsQueue;
        if (queue2 != null) {
            queue2.clear();
        }
        SendThread sendThread = this.mSendThread;
        if (sendThread != null) {
            sendThread.close();
        }
        ReceiveThread receiveThread = this.mReceiveThread;
        if (receiveThread != null) {
            receiveThread.close();
        }
        if (this.deviceConnection != null) {
            this.deviceConnection = null;
        }
        if (this.usbDevice != null) {
            this.usbDevice = null;
        }
        if (this.usbInterface != null) {
            this.usbInterface = null;
        }
        //此处或 蓝牙是否连接
        if (this.mDataTransListerner == null) {
            return;
        }
        this.mDataTransListerner.onCmdMessage(-1, new byte[0], 1, DataTransListerner.ProtocolType.OTG);
    }

    public boolean checkIsConnectDevice() {
        UsbManager usbManager = (UsbManager) this.mContext.getSystemService(Context.USB_SERVICE);
        this.usbManager = usbManager;
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        this.deviceList = deviceList;
        for (UsbDevice usbDevice : deviceList.values()) {
            if (usbDevice.getVendorId() == 1027 && usbDevice.getProductId() == 24596) {
                this.usbDevice = usbDevice;
                LogUtil.d("已有权限设备" + usbDevice.toString());
            }
        }
        return this.usbDevice != null;
    }

    public boolean isConnect() {
        return this.isRunning;
    }

    public boolean initUsbDevice() {

        UsbManager usbManager = (UsbManager) MyApplication.getInstance().getSystemService(Context.USB_SERVICE);

        this.usbManager = usbManager;
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        this.deviceList = deviceList;
        Log.d(TAG, "初始化");

        for (UsbDevice usbDevice : deviceList.values()) {
            if (usbDevice.getVendorId() == 1027 && usbDevice.getProductId() == 24596) {
                Log.d(TAG, "找到设备");
                this.usbDevice = usbDevice;
            }
        }
        findInterface();
        return start();
    }

    class SendThread1 extends Thread {
        private boolean isRunning = true;

        private SendThread1() {
        }

        public void close() {
            this.isRunning = false;
            interrupt();
        }

        public void run() {
            super.run();
            Log.i("OTGManager", "run: SendThread");
            try {
                Thread.sleep(100L);
            } catch (InterruptedException localInterruptedException1) {
                localInterruptedException1.printStackTrace();
            }
            int i = 0;
            int j = i + 1;
            while (this.isRunning) {
                DeviceMsg localDeviceMsg;
                if ((!fimwareQueue.isEmpty()) && isSendFirmwareData) {
                    localDeviceMsg = (DeviceMsg) msgsQueue.poll();
                    localDeviceMsg.setCurrentSendNum(1200);
                    isFimwareFlag = false;
                    OTGManager localOTGManager;
                    if (((FirmwareUpdateMsg) localDeviceMsg).getFirmwareFlag() == 16) {
                        localOTGManager = OTGManager.this;
                        localOTGManager.fimwareAllSize = localOTGManager.fimwareQueue.size();
                        fimwareCurrentSize = 0;
                        mDataTransListerner.onCmdMessage(4277, new byte[4], 16, DataTransListerner.ProtocolType.OTG);
                    } else {
                        localOTGManager = OTGManager.this;
                        localOTGManager.fimwareCurrentSize = localOTGManager.fimwareAllSize - OTGManager.this.fimwareQueue.size();
                    }
                    LogUtil.i("=====================Send=fimwareCurrentSize=" + fimwareCurrentSize + " size=" + fimwareQueue.size());
                    while (true) {
                        j = localDeviceMsg.getCurrentSendNum();
                        if (j < 0)
                            break;
                        if (j % 600 == 0)
                            sendDatas(localDeviceMsg);
                        if (isFimwareFlag)
                            break;
                        localDeviceMsg.setCurrentSendNum(localDeviceMsg.getCurrentSendNum() - 1);
                        try {
                            Thread.sleep(5L);
                        } catch (InterruptedException localInterruptedException3) {
                            localInterruptedException3.printStackTrace();
                        }
                    }
                    if (j != -1)
                        continue;
                    mDataTransListerner.onCmdMessage(4277, new byte[4], 3, DataTransListerner.ProtocolType.OTG);
                    fimwareQueue.clear();
                    continue;
                }
                if (SystemClock.uptimeMillis() - currentTime > 50L) {
                    if (!msgsQueue.isEmpty()) {
                        Log.i("OTGManager", "run: " + (SystemClock.uptimeMillis() - currentTime));
                        localDeviceMsg = (DeviceMsg) msgsQueue.poll();
                        localDeviceMsg.setLastSendTime(SystemClock.uptimeMillis());
                        Log.i("OTGManager", "send: " + Integer.toHexString(localDeviceMsg.getMsgId()));
                        sendDatas(localDeviceMsg);
                    }
                } else
                    Log.i("OTGManager", "run: send error");

                i = j;
                if (j > 3) {
                    if (!checkIsConnectDevice()) {
                        closeSession();
                    }
                    i = 0;
                }
                try {
                    Thread.sleep(60L);
                } catch (InterruptedException localInterruptedException2) {
                    localInterruptedException2.printStackTrace();
                }
            }
            this.isRunning = false;
        }
    }

    public class SendThread extends Thread {
        private boolean isRunning;

        private SendThread() {
            this.isRunning = true;
        }

        public void close() {
            this.isRunning = false;
            interrupt();
        }

        @Override
        public void run() {
            int currentSendNum;
            super.run();
            Log.i(TAG, "run: SendThread");
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int i = 0;
            while (this.isRunning) {
                if (fimwareQueue.isEmpty() || !isSendFirmwareData) {
                    if (SystemClock.uptimeMillis() - currentTime > 50) {
                        if (!msgsQueue.isEmpty()) {
                            Log.i(TAG, "run: " + (SystemClock.uptimeMillis() - currentTime));
                            DeviceMsg deviceMsg = (DeviceMsg) msgsQueue.poll();
                            deviceMsg.setLastSendTime(SystemClock.uptimeMillis());
                            Log.i(TAG, "send: " + Integer.toHexString(deviceMsg.getMsgId()));
                            sendDatas(deviceMsg);
                        }
                    } else {

                        Log.i(TAG, "run: send error");
                        return;
                    }
                    i++;
                    if (i > 3) {
                        if (!checkIsConnectDevice()) {
                            LogUtil.d("未连接关闭");
                            closeSession();
                        }
                        i = 0;
                    }
                    try {
                        Thread.sleep(60L);
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                } else {
                    DeviceMsg deviceMsg2 = (DeviceMsg) fimwareQueue.poll();
                    deviceMsg2.setCurrentSendNum(Videoio.CAP_AVFOUNDATION);
                    isFimwareFlag = false;
                    if (((FirmwareUpdateMsg) deviceMsg2).getFirmwareFlag() == 16) {
                        OTGManager oTGManager = OTGManager.this;
                        oTGManager.fimwareAllSize = oTGManager.fimwareQueue.size();
                        fimwareCurrentSize = 0;
                        mDataTransListerner.onCmdMessage(Constant.DEVICE_FIRMWARE_UPDATE, new byte[4], 16, DataTransListerner.ProtocolType.OTG);
                    } else {
                        OTGManager oTGManager2 = OTGManager.this;
                        oTGManager2.fimwareCurrentSize = oTGManager2.fimwareAllSize - fimwareQueue.size();
                    }
                    Log.i("", "=====================Send=firmwareCurrentSize=" + fimwareCurrentSize + " size=" + fimwareQueue.size());
                    while (true) {
                        currentSendNum = deviceMsg2.getCurrentSendNum();
                        if (currentSendNum < 0) {
                            break;
                        }
                        if (currentSendNum % 600 == 0) {
                            sendDatas(deviceMsg2);
                        }
                        if (isFimwareFlag) {
                            break;
                        }
                        deviceMsg2.setCurrentSendNum(deviceMsg2.getCurrentSendNum() - 1);
                        try {
                            Thread.sleep(5L);
                        } catch (InterruptedException e3) {
                            e3.printStackTrace();
                        }
                    }
                    if (currentSendNum == -1) {
                        mDataTransListerner.onCmdMessage(Constant.DEVICE_FIRMWARE_UPDATE, new byte[4], 3, DataTransListerner.ProtocolType.OTG);
                        fimwareQueue.clear();
                    }
                }
            }
            this.isRunning = false;
        }
    }

}
