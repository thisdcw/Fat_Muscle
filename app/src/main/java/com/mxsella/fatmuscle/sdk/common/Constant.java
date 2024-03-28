package com.mxsella.fatmuscle.sdk.common;

import com.alibaba.fastjson.parser.JSONLexer;
import com.fasterxml.jackson.dataformat.smile.SmileConstants;

public class Constant {
    public static final int DEVICE_CONNECTED_MSG_ID = 34952;//连接状态
    public static final int DEVICE_FIRMWARE_UPDATE = 4277;
    public static int supportOnlyZ1OrZ2 = 0;
    public static final int DEVICE_ADC_CONF = 4305;
    public static final int DEVICE_AFE_POWER_CTRL = 4306;
    public static final int DEVICE_BATTERY_MSG_ID = 30583;
    public static final int DEVICE_CUT_POINTS = 4290;
    public static final int DEVICE_DISCONNECTED_MSG_ID = -1;
    public static final int DEVICE_DLPF_M_VALUE = 4288;
    public static final int DEVICE_DLPF_PARA = 4289;
    public static final int DEVICE_LINE_CYCLE = 4293;
    public static final int DEVICE_DR_PARA = 4291;
    public static final int DEVICE_FIRMWARE_RESTART = 4279;
    public static final int DEVICE_FIRMWARE_START = 4278;
    public static final int DEVICE_FLASHID = 4273;
    public static final int DEVICE_FPGA_SPI_DMA = 4304;
    public static final int DEVICE_IMAGE_DATA = 4261;
    public static final int DEVICE_INFO_MSG_ID = 26214;
    public static final int DEVICE_LINE_NUM = 4292;
    public static final int DEVICE_LOW_PASS_FILTER_COEFFICIENT = 4299;
    public static final int DEVICE_MESSAGE_MSG_ID = 39321;
    public static final int DEVICE_PULSE_WIDTH = 4295;
    public static final int DEVICE_RXATE_DELAY = 4301;
    public static final int DEVICE_R_FIRMWARE_UPDATE = 437;
    public static final int DEVICE_SAMPLE_LEN = 4302;
    public static final int DEVICE_SETTING_DOWN_UPDATE = 4296;//刷新设备
    public static final int DEVICE_TGC = 4294;
    public static final int DEVICE_TXATE_DELAY = 4300;
    public static final int DEVICE_TXDATA_RATE = 4308;
    public static final int DEVICE_TX_WAVE = 4297;
    public static final int DEVICE_VERSION = 4272;//版本信息
    public static final int MAGIC_NUMBER = 9188;
    public static final int R_DEVICE_MODE = 4512;
    public static final int R_DEVICE_SAMPLE_LEN = 462;
    public static final int R_DEVICE_VERSION = 432;
    public static final byte[] tgcArray = {0, 1, 2, 3, 4, 5, 6, 7, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 35, 36, 37, 38, 39, 40, 42, 43, 44, 45, 47, 48, 49, 50, 52, 53, 54, 55, 55, 56, 57, 57, 58, 58, 59, 60, 60, 61, 62, 62, 63, 63, 64, 64, 65, 66, 66, 67, 68, 69, 69, 70, 70, 71, 71, 72, 73, 73, 74, 75, 76, 76, 77, 78, 78, 79, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80};
    public static final byte[] lowPassFilterCoefficientArray = {0, 0, 0, 0, 1, 2, 3, 5, 7, 9, 11, 13, 15, 17, 18, 18};

}
