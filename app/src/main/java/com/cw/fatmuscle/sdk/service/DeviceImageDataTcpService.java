package com.cw.fatmuscle.sdk.service;

import android.graphics.Bitmap;

import com.cw.fatmuscle.common.MyApplication;
import com.cw.fatmuscle.sdk.VolAlg;
import com.cw.fatmuscle.sdk.fat.utils.SharedPreferencesUtil;
import com.cw.fatmuscle.sdk.manager.MxsellaDeviceManager;
import com.cw.fatmuscle.sdk.manager.OdsAlgolManager;
import com.cw.fatmuscle.utils.ByteUtil;
import com.cw.fatmuscle.utils.DateUtil;
import com.cw.fatmuscle.utils.LogUtil;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;

public class DeviceImageDataTcpService extends Thread {
    public static final String SAVE_ORIGINAL_DATA_KEY = "SAVE_ORIGINAL_DATA";
    private MxsellaDeviceManager deviceManager;

    /* renamed from: ip */
    private String ip;
    private boolean isSupportOriginalSave;
    private int port;
    public Socket socket;
    private boolean isRunning = true;

    /* renamed from: is */
    private DataInputStream fis = null;
    byte[] temps = new byte[11796640];

    public DeviceImageDataTcpService(String str, int i, MxsellaDeviceManager marvotoDeviceManager) {
        this.isSupportOriginalSave = false;
        this.ip = str;
        this.port = i;
        this.deviceManager = marvotoDeviceManager;
        this.isSupportOriginalSave = SharedPreferencesUtil.getBoolean(MyApplication.getInstance(), "SAVE_ORIGINAL_DATA", false);
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public void run() {
        byte[] bArr = new byte[0];
        try {
            try {
                this.socket = new Socket(this.ip, this.port);
                this.fis = new DataInputStream(this.socket.getInputStream());
                bArr = new byte[4096];
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (this.socket.isConnected()) {
                LogUtil.i("=========DeviceImageDataTcpServic=start=");
                Bitmap createBitmap = Bitmap.createBitmap(VolAlg.IMG_WIDTH, VolAlg.IMG_HEIGHT, Bitmap.Config.ARGB_8888);
                loop0: while (true) {
                    int i = 0;
                    while (true) {
                        int read = this.fis.read(bArr);
                        if (read == -1) {
                            break loop0;
                        } else if ((bArr[read - 1] & 255) == 254 && (bArr[read - 2] & 255) == 254 && (bArr[read - 3] & 255) == 254) {
                            System.arraycopy(bArr, 0, this.temps, i, 4093);
                            int i2 = (i + read) - 3;
                            LogUtil.i("=========DeviceImageDataTcpServic=end totalLength=" + i2);
                            if (OdsAlgolManager.isSupport) {
                                byte[] bArr2 = new byte[i2];
                                if (this.isSupportOriginalSave) {
                                    byte[] bArr3 = new byte[i2];
                                    System.arraycopy(this.temps, 0, bArr3, 0, i2);
                                    ByteUtil.writeBytesToFile(bArr3, DateUtil.date2LongStr(new Date()) + "_before_algo");
                                }
                                this.deviceManager.receiveByteData(this.temps);
                            } else {
                                VolAlg.VolSriInit(null, this.deviceManager.getAlgoLeve());
                                VolAlg.drawBitmap(createBitmap, this.temps);
                                this.deviceManager.receiveImageData(createBitmap);
                            }
                        } else {
                            int i3 = read + i;
                            byte[] bArr4 = this.temps;
                            if (i3 >= bArr4.length) {
                                break;
                            }
                            System.arraycopy(bArr, 0, bArr4, i, 4096);
                            i = i3;
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void close() throws IOException {
        LogUtil.i("=========DeviceImageDataTcpServic=end close=");
        this.isRunning = false;
        DataInputStream dataInputStream = this.fis;
        try {
        } catch (Throwable th) {
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw th;
        }
        if (dataInputStream != null) {
            try {
                dataInputStream.close();
                this.socket.close();
            } catch (IOException e3) {
                e3.printStackTrace();
                this.socket.close();
            }
        }
    }

}
