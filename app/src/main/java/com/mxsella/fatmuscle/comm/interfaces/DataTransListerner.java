package com.mxsella.fatmuscle.comm.interfaces;


import com.mxsella.fatmuscle.entity.BitmapMsg;

public interface DataTransListerner {
    public enum ProtocolType {
        OTG
    }

    void onCmdMessage(int i, byte[] bArr, int i2, ProtocolType protocolType);

    void onImageData(byte[] bArr, BitmapMsg.State state, ProtocolType protocolType);

}
