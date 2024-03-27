package com.mxsella.fatmuscle.sdk.fat;

import android.os.SystemClock;

import com.mxsella.fatmuscle.sdk.fat.entity.DeviceMsg;
import com.mxsella.fatmuscle.sdk.fat.interfaces.IDataTransfer;

import org.opencv.videoio.Videoio;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;

public class RetransmissionThread  extends Thread{
    private IDataTransfer mDataTransfer;
    private boolean isLoop = true;
    public LinkedBlockingDeque<DeviceMsg> mListReSend = new LinkedBlockingDeque<>();
    private final int sleepTime = Videoio.CAP_QT;

    public void addToSendList(DeviceMsg deviceMsg) {
        this.mListReSend.add(deviceMsg);
    }

    public boolean removeFromListByCmdID(int i) {
        boolean z;
        synchronized (this.mListReSend) {
            DeviceMsg deviceMsg = null;
            Iterator<DeviceMsg> it = this.mListReSend.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                DeviceMsg next = it.next();
                if (next.getMsgId() == i) {
                    deviceMsg = next;
                    break;
                }
            }
            if (deviceMsg != null) {
                z = true;
                this.mListReSend.remove(deviceMsg);
            } else {
                z = false;
            }
        }
        return z;
    }

    public RetransmissionThread(IDataTransfer iDataTransfer) {
        this.mDataTransfer = iDataTransfer;
    }

    @Override
    public void run() {
        while (this.isLoop) {
            if (!this.mListReSend.isEmpty()) {
                DeviceMsg deviceMsg = null;
                Iterator<DeviceMsg> it = this.mListReSend.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    DeviceMsg next = it.next();
                    if (SystemClock.uptimeMillis() - next.getLastSendTime() >= next.getOutTime()) {
                        int currentSendNum = next.getCurrentSendNum();
                        if (currentSendNum >= next.getReSendNum()) {
                            deviceMsg = next;
                            break;
                        }
                        next.setCurrentSendNum(currentSendNum + 1);
                        this.mDataTransfer.sendRestransmissionData(next);
                        next.setLastSendTime(SystemClock.uptimeMillis());
                    }
                }
                if (deviceMsg != null) {
                    this.mDataTransfer.onSendTimeOut(deviceMsg.getMsgId(), deviceMsg);
                    this.mListReSend.remove(deviceMsg);
                }
            } else {
                try {
                    Thread.sleep(400L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void exit() {
        this.isLoop = false;
        interrupt();
    }

}
