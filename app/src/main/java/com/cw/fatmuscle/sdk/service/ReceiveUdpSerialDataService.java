package com.cw.fatmuscle.sdk.service;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;


import com.cw.fatmuscle.common.MyApplication;
import com.cw.fatmuscle.sdk.manager.MxsellaDeviceManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ReceiveUdpSerialDataService extends Thread{
    private static WifiManager.MulticastLock lock;
    private DatagramSocket datagramSocket;
    private MxsellaDeviceManager deviceManager;
    private boolean isRunning = false;

    public ReceiveUdpSerialDataService(DatagramSocket datagramSocket, MxsellaDeviceManager marvotoDeviceManager) {
        lock = ((WifiManager) MyApplication.getInstance().getSystemService(Context.WIFI_SERVICE)).createMulticastLock("GM-Test");
        this.deviceManager = marvotoDeviceManager;
        this.datagramSocket = datagramSocket;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public void run() {
        this.isRunning = true;
        try {
            try {
                DatagramPacket datagramPacket = new DatagramPacket(new byte[2048], 2048);
                try {
                    Log.i("","==UDP 准备接受数据");
                    this.deviceManager.onUdpConnected();
                    while (this.isRunning) {
                        lock.acquire();
                        this.datagramSocket.receive(datagramPacket);
                        String str = new String(datagramPacket.getData(), 0, datagramPacket.getLength(), "UTF-8");
                        Log.i("","==UDP content=" + str);
                        this.deviceManager.onMessage(1, str, this.datagramSocket.getPort(), MxsellaDeviceManager.DeviceInterface.ConnType.UDP);
                        lock.release();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } finally {
            close();
        }
    }

    public void close() {
        Log.i("","ReceiveSerial UDP 回收==");
        this.isRunning = false;
        DatagramSocket datagramSocket = this.datagramSocket;
        if (datagramSocket != null) {
            try {
                datagramSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.deviceManager.onStopped(4, "ReceiveSerial UDP 回收资源", MxsellaDeviceManager.DeviceInterface.ConnType.UDP);
    }

}
