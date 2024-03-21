package com.mxsella.fatmuscle.sdk.service;

import android.util.Log;

import com.mxsella.fatmuscle.sdk.fat.entity.DeviceUdpMsg;
import com.mxsella.fatmuscle.sdk.manager.MxsellaDeviceManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DeviceUdpSerialDataService extends Thread {

    private InetAddress address;
    private MxsellaDeviceManager deviceManager;

    private String ip;
    private Integer port;
    private boolean isRunning = false;
    private ReceiveUdpSerialDataService receiveService = null;
    private Queue<DeviceUdpMsg> msgsQueue = new ConcurrentLinkedQueue();
    private DatagramSocket datagramSocket = null;

    public DeviceUdpSerialDataService(String str, int i, MxsellaDeviceManager marvotoDeviceManager) {
        this.ip = str;
        this.port = Integer.valueOf(i);
        this.deviceManager = marvotoDeviceManager;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        try {
            this.address = InetAddress.getByName(this.ip);
            DatagramSocket datagramSocket = new DatagramSocket((SocketAddress) null);
            this.datagramSocket = datagramSocket;
            datagramSocket.setReuseAddress(true);
            this.datagramSocket.bind(new InetSocketAddress(5001));
            this.datagramSocket.setBroadcast(true);
            ReceiveUdpSerialDataService receiveUdpSerialDataService = new ReceiveUdpSerialDataService(this.datagramSocket, this.deviceManager);
            this.receiveService = receiveUdpSerialDataService;
            receiveUdpSerialDataService.start();
            setRunning(true);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            this.deviceManager.onError(e2);
            e2.printStackTrace();
        }
    }

    public void sendMsg(DeviceUdpMsg deviceUdpMsg) {
        if (deviceUdpMsg != null && this.deviceManager.isConnected() && isRunning()) {
            Log.i("","==udp=========add=" + deviceUdpMsg);
            try {
                this.datagramSocket.send(new DatagramPacket(deviceUdpMsg.getUdpProtocolBytes(), deviceUdpMsg.getUdpProtocolBytes().length, this.address, this.port.intValue()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isRunning() {
        return this.isRunning && this.receiveService.isRunning();
    }

    private void setRunning(boolean z) {
        this.isRunning = z;
        Log.i("", "DeviceSerialDataService ==udp连接状态为：" + z);
        if (z) {
            return;
        }
        this.deviceManager.onStopped(5, "udp Connection break", MxsellaDeviceManager.DeviceInterface.ConnType.UDP);
    }

    public void close() {
        Log.i("", "==udp 回收资源");
        this.isRunning = false;
        DatagramSocket datagramSocket = this.datagramSocket;
        if (datagramSocket != null) {
            datagramSocket.close();
        }
        ReceiveUdpSerialDataService receiveUdpSerialDataService = this.receiveService;
        if (receiveUdpSerialDataService != null) {
            receiveUdpSerialDataService.close();
        }
        Queue<DeviceUdpMsg> queue = this.msgsQueue;
        if (queue != null) {
            queue.clear();
        }
    }

}
