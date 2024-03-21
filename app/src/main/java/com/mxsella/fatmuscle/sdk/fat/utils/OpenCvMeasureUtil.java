package com.mxsella.fatmuscle.sdk.fat.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.mxsella.fatmuscle.common.MyApplication;
import com.mxsella.fatmuscle.sdk.fat.entity.FindContourInfo;
import com.mxsella.fatmuscle.sdk.fat.entity.PointArrayInfo;
import com.mxsella.fatmuscle.sdk.fat.manager.FatConfigManager;
import com.mxsella.fatmuscle.sdk.fat.manager.MxsellaDeviceManager;
import com.mxsella.fatmuscle.utils.ArrayUtil;
import com.mxsella.fatmuscle.utils.LogUtil;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OpenCvMeasureUtil {
    public static final int TOO_WEEK_RESULT = -3;
    private static Object obj = new Object();
    private static OpenCvMeasureUtil openCvMeasureUtil;
    private Context context;
    private int threSholdValue;
    private Mat src = null;
    private Mat gray = null;
    private Mat blur = null;
    private Mat bin = null;
    private Mat edges = null;
    private Point startPoint = new Point();
    private Point startGrayPoint = new Point();
    private Point endGrayPoint = new Point();
    private Point endPoint = new Point();
    private ArrayList<Float> queue = new ArrayList<>();
    private int binDefaultValue = 20;
    private int bitmapWidth = 150;
    boolean isFirst = true;
    private int bodyPosition = -1;
    int changeValue = 30;

    public int getBodyPosition() {
        return this.bodyPosition;
    }

    public int getThreSholdValue() {
        return this.threSholdValue;
    }

    public static OpenCvMeasureUtil getInstance() {
        if (openCvMeasureUtil == null) {
            synchronized (obj) {
                if (openCvMeasureUtil == null) {
                    openCvMeasureUtil = new OpenCvMeasureUtil();
                }
            }
        }
        return openCvMeasureUtil;
    }

    private OpenCvMeasureUtil() {
        if (!OpenCVLoader.initDebug()) {
            Log.i("OpenCvMeasureUtil","cv Internal OpenCV library not found. Using OpenCV Manager for initialization");
        } else {
            Log.i("OpenCvMeasureUtil","cv OpenCV library found inside package. Using it!");
        }
        this.context = MyApplication.getInstance();
    }

    public int[] measure(Bitmap bitmap, int i, int i2) {
        int i3 = 30;
        if (i2 == 2) {
            this.changeValue = 30;
        } else {
            if (i2 == 3) {
                i3 = 60;
            } else if (i2 == 6) {
                this.changeValue = 30;
            } else if (i2 != 0) {
            }
            this.binDefaultValue = i3;
            if (this.bodyPosition != i2) {
                this.queue.clear();
//                float queryLocalAvgValue = FatCloudManager.getInstance().queryLocalAvgValue(CloudSdkManager.getLoginUser().getUserId(), FatConfigManager.getInstance().getCurBodyPositionIndex() + "", FatConfigManager.getInstance().getCurMeasureMemberId(), Integer.valueOf(FatRecord.TYPE.FAT.value()), 10);
                //TODO 脂肪厚度是否非零，并且不是“防刻模式”。如果是，则查询本地平均值，如果脂肪厚度超过平均值加5.0或低于平均值减5.0，并且平均值大于零，则显示警告对话框
                Log.i("OpenCvMeasureUtil","Imgproc plugin avg =?  bodyPosition=" + i2);
//                if (queryLocalAvgValue > 0.0f) {
//                    this.queue.add(Float.valueOf(queryLocalAvgValue * 10.0f));
//                }
            }
            this.bodyPosition = i2;
            this.bitmapWidth = bitmap.getWidth();
            this.isFirst = true;
            Mat mat = new Mat();
            this.src = mat;
            Utils.bitmapToMat(bitmap, mat);
            Mat mat2 = this.src;
            Imgproc.cvtColor(mat2, mat2, 1);
            onGray();
            onBlur(i);
            return onBin(i3);
        }
        i3 = 50;
        this.binDefaultValue = i3;
        if (this.bodyPosition != i2) {
        }
        this.bodyPosition = i2;
        this.bitmapWidth = bitmap.getWidth();
        this.isFirst = true;
        Mat mat3 = new Mat();
        this.src = mat3;
        Utils.bitmapToMat(bitmap, mat3);
        Mat mat22 = this.src;
        Imgproc.cvtColor(mat22, mat22, 1);
        onGray();
        onBlur(i);
        return onBin(i3);
    }
    private void onBlur(int i) {
        Mat mat = this.blur;
        if (mat != null) {
            mat.release();
        }
        Mat mat2 = new Mat();
        this.blur = mat2;
        Imgproc.medianBlur(this.gray, mat2, i);
        Bitmap createBitmap = Bitmap.createBitmap(this.blur.width(), this.blur.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(this.blur, createBitmap);
        Mat mat3 = new Mat();
        Mat mat4 = new Mat();
        Utils.bitmapToMat(createBitmap, mat4);
        if (this.bodyPosition == 6) {
            Utils.bitmapToMat(getAssetBitmap("fuzhiji_blur.jpg"), mat3);
        } else {
            Utils.bitmapToMat(getAssetBitmap("line_blur.jpg"), mat3);
        }
        Point matchTemplate = matchTemplate(mat4, mat3, 4.0E-8d, 2.0E-7d);
        this.startPoint = matchTemplate;
        if (matchTemplate != null) {
            if (matchTemplate.y == com.github.mikephil.charting.utils.Utils.DOUBLE_EPSILON) {
                this.endPoint = new Point((int) com.github.mikephil.charting.utils.Utils.DOUBLE_EPSILON, (int) com.github.mikephil.charting.utils.Utils.DOUBLE_EPSILON);
            } else {
                this.endPoint = new Point(this.startPoint.x + mat3.cols(), this.startPoint.y + mat3.rows());
            }
        }
    }
    private void onGray() {
        if (this.src == null) {
            return;
        }
        Mat mat = new Mat();
        this.gray = mat;
        Imgproc.cvtColor(this.src, mat, 6);
        Bitmap createBitmap = Bitmap.createBitmap(this.gray.width(), this.gray.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(this.gray, createBitmap);
        Mat mat2 = new Mat();
        Mat mat3 = new Mat();
        Utils.bitmapToMat(createBitmap, mat3);
        if (this.bodyPosition == 6) {
            Utils.bitmapToMat(getAssetBitmap("temp.jpg"), mat2);
            Point matchTemplate = matchTemplate(mat3, mat2, 4.0E-8d, 6.0E-8d);
            this.startGrayPoint = matchTemplate;
            if (matchTemplate.y == com.github.mikephil.charting.utils.Utils.DOUBLE_EPSILON) {
                this.endGrayPoint = new Point((int) com.github.mikephil.charting.utils.Utils.DOUBLE_EPSILON, (int) com.github.mikephil.charting.utils.Utils.DOUBLE_EPSILON);
            } else {
                this.endGrayPoint = new Point(this.startGrayPoint.x + mat2.cols(), this.startGrayPoint.y + mat2.rows());
            }
        }
    }
    private int[] onBin(int i) {
        this.threSholdValue = i;
        Mat mat = this.bin;
        if (mat != null) {
            mat.release();
        }
        Mat mat2 = new Mat();
        this.bin = mat2;
        Imgproc.threshold(this.blur, mat2, i, 255.0d, 0);
        ArrayList<MatOfPoint> arrayList = new ArrayList<>();
        Mat mat3 = new Mat();
        Imgproc.findContours(this.bin, arrayList, mat3, 0, 1);
        return calArea(arrayList, mat3, i, this.bitmapWidth);
    }
    private Point matchTemplate(Mat mat, Mat mat2, double d, double d2) {
        Mat mat3 = new Mat((mat.cols() - mat2.cols()) + 1, (mat.rows() - mat2.rows()) + 1, CvType.CV_32FC1);
        Imgproc.matchTemplate(mat, mat2, mat3, 1);
        Core.normalize(mat3, mat3, com.github.mikephil.charting.utils.Utils.DOUBLE_EPSILON, 1.0d, 32, -1, new Mat());
        Core.MinMaxLocResult minMaxLoc = Core.minMaxLoc(mat3);
        double d3 = minMaxLoc.minLoc.x;
        double d4 = minMaxLoc.minLoc.y;
        String format = new DecimalFormat("#.########").format(minMaxLoc.minVal);
        Log.i("Imgproc", "Utils matchTemplate--> min=" + format + " max= " + minMaxLoc.maxVal + " minLimit=" + d + " x=" + d3 + " y=" + d4 + " Math.abs(1-mmr.maxVal)=" + Math.abs(1.0d - minMaxLoc.maxVal));
        if (Math.abs(Double.valueOf(format).doubleValue()) <= d && Math.abs(1.0d - minMaxLoc.maxVal) < d2) {
            Log.i("Imgproc", "Utils matchTemplate--> is OK");
            return new Point(d3, d4);
        }
        return new Point(com.github.mikephil.charting.utils.Utils.DOUBLE_EPSILON, com.github.mikephil.charting.utils.Utils.DOUBLE_EPSILON);
    }

    private Bitmap getAssetBitmap(String str) {
        InputStream inputStream;
        try {
            inputStream = this.context.getAssets().open(str);
        } catch (IOException e) {
            e.printStackTrace();
            inputStream = null;
        }
        return BitmapFactory.decodeStream(inputStream);
    }

    private int[] calArea(ArrayList<MatOfPoint> arrayList, Mat mat, int i, int i2) {
        int i3;
        int i4;
        int i5;
        char c;
        int i6;
        FindContourInfo deelFindByBinValue;
        int maxValue = 0;
        int i7 = 0;
        int i8;
        int i9;
        String str;
        double d;
        int i10;
        int i11;
        double d2 = 0;
        int i12;
        int i13;
        FindContourInfo deelFindByBinValue2;
        int i14;
        int i15;
        int i16;
        ArrayList<MatOfPoint> arrayList2 = arrayList;
        int size = arrayList.size();
        LogUtil.i("Imgproc Contours star findSize:" + size);
        double d3 = -10000.0d;
        int i17 = -3;
        int i18 = 0;
        int i19 = -1;
        int i20 = 0;
        int i21 = 0;
        int i22 = 0;
        while (true) {
            int i23 = i17;
            if (i18 < arrayList.size()) {
                MatOfPoint matOfPoint = arrayList2.get(i18);
                Rect boundingRect = Imgproc.boundingRect(matOfPoint);
                double d4 = d3;
                double contourArea = Imgproc.contourArea(matOfPoint);
                int i24 = size;
                double arcLength = Imgproc.arcLength(new MatOfPoint2f(matOfPoint.toArray()), true);
                int i25 = i19;
                int i26 = i20;
                double d5 = arcLength - ((boundingRect.height * 2) + (boundingRect.width * 2));
                int i27 = i22;
                int abs = (int) Math.abs(d5);
                LogUtil.i("ImgprocoutLength:" + abs + " y:" + boundingRect.y + " rect.width=" + boundingRect.width + " rect.height=" + boundingRect.height + " contourArea:" + contourArea + " binValue=" + i);
                if (boundingRect.width >= i2 && boundingRect.height > 200) {
                    i25 = -2;
                }
                if (boundingRect.width >= i2 && boundingRect.height > 50 && boundingRect.y < 5 && Math.abs(abs) < 20) {
                    LogUtil.i("Imgproc空打耦合");
                    return new int[]{-2};
                }
                int i28 = this.bodyPosition;
                if (i28 == 6 || i28 == 0) {
                    if (boundingRect.width >= i2 && boundingRect.height > 60 && boundingRect.y < 5 && Math.abs(abs) > 150 && i < 120) {
                        LogUtil.i("Imgproc近场图像太高亮，");
                        return onBin(1 + i);
                    }
                } else if (boundingRect.width >= i2 && boundingRect.height > 45 && boundingRect.y < 5 && Math.abs(abs) > 150 && i < 120) {
                    LogUtil.i("Imgproc近场图像太高亮，");
                    return onBin(1 + i);
                }
                if (boundingRect.width < i2 || boundingRect.y < 10 || ((boundingRect.y < 10 && boundingRect.height <= 100) || boundingRect.height <= 2 || contourArea <= 10.0d)) {
                    i20 = i26;
                } else {
                    if (this.bodyPosition == 6) {
                        d = d5;
                        i9 = i26;
                        str = " binValue=";
                        if ((boundingRect.y + boundingRect.height < this.startPoint.y || boundingRect.y > this.endPoint.y) && i >= 50) {
                            Log.i("Imgproc", "==不在匹配范围内");
                            d3 = d4;
                            i19 = i25;
                            i22 = i27;
                            i20 = i9;
                            i17 = -2;
                            i18++;
                            arrayList2 = arrayList;
                            size = i24;
                        }
                    } else {
                        i9 = i26;
                        str = " binValue=";
                        d = d5;
                    }
                    if (boundingRect.width >= i2 && boundingRect.height > 20 && Math.abs(abs) <= 10 && Math.abs((boundingRect.width * boundingRect.height) - contourArea) < 400.0d) {
                        i21++;
                        LogUtil.i("Imgproc出现干净的直线，存在空打耦合的可能");
                        if (i21 > 1) {
                            return new int[]{-2};
                        }
                        if (boundingRect.width >= i2 && boundingRect.height > 60 && Math.abs(abs) <= 5) {
                            return new int[]{-2};
                        }
                    }
                    PointArrayInfo upArraPoint = getUpArraPoint(matOfPoint.toList());
                    int maxValue2 = ArrayUtil.getMaxValue(upArraPoint.getBinArray()) - ArrayUtil.getMinValue(upArraPoint.getBinArray());
                    if ((boundingRect.height >= 200 || (boundingRect.height > 150 && maxValue2 < 100)) && contourArea > 10000.0d) {
                        if (this.bodyPosition == 6) {
                            i10 = 1;
                            LogUtil.i("ImgprocisAddTh==" + i10 + " slope=" + maxValue2 + " isFirst=" + this.isFirst);
                            if (abs <= 400 && boundingRect.height > 350 && ((i > 50 || !this.isFirst) && Math.abs((boundingRect.height * boundingRect.width) - contourArea) > boundingRect.height * 20)) {
                                i17 = i23;
                                d3 = d4;
                                i19 = i25;
                                i22 = i27;
                                i20 = i9;
                                i18++;
                                arrayList2 = arrayList;
                                size = i24;
                            } else if (i <= 150 && boundingRect.height > 200 && Math.abs((boundingRect.height * boundingRect.width) - contourArea) < boundingRect.height * 20) {
                                return new int[]{-1};
                            } else {
                                if (this.bodyPosition != 6 && i > 60) {
                                    deelFindByBinValue2 = deelFindByBinValue(i);
                                    if (deelFindByBinValue(i + 1).getY() - deelFindByBinValue2.getY() > 30 && deelFindByBinValue2.getHeight() > 50) {
                                        i10 = 0;
                                    }
                                }
                                if (i10 <= 0 && this.isFirst) {
                                    return onBin(i + 1);
                                }
                                if (boundingRect.width < i2 && boundingRect.height > 60 && Math.abs(abs) <= 5 && maxValue2 < 5) {
                                    return new int[]{-2};
                                }
                                i11 = this.bodyPosition;
                                if (i11 == 6) {
                                    if (maxValue2 <= 20) {
                                        maxValue2 = 0;
                                    }
                                    if (i11 == 0) {
                                        i12 = ((i18 * 5) + boundingRect.height) - (maxValue2 * 5);
                                        i13 = boundingRect.y / 3;
                                    } else {
                                        i12 = (i18 * 5) - (maxValue2 * 5);
                                        i13 = boundingRect.y / 3;
                                    }
                                    d2 = i12 - i13;
                                } else {
                                    d2 = ((((boundingRect.width * 20) + contourArea) + (arcLength / 2.0d)) - (boundingRect.y * 5)) + boundingRect.height;
                                }
                                int i29 = i9;
                                LogUtil.i("ImgprocPASS weight=" + d2 + " selCount=" + i29 + " index=" + i18 + str + i + " 超出长度：" + d + " y=" + boundingRect.y + " slope =" + maxValue2);
                                if (d2 > d4) {
                                    d4 = d2;
                                    i25 = i18;
                                }
                                i20 = i29 + 1;
                            }
                        }
                    }
                    if (boundingRect.height < 150 || contourArea <= 10000.0d || this.bodyPosition == 6 || maxValue2 <= 30) {
                        if (boundingRect.height < 140 || contourArea <= 10000.0d || abs >= 150) {
                            i14 = 6;
                        } else {
                            i14 = 6;
                            if (this.bodyPosition == 6) {
                                i10 = 3;
                            }
                        }
                        if (abs > 300) {
                            i15 = 100;
                            if (maxValue2 < 100) {
                            }
                        } else {
                            i15 = 100;
                        }
                        if (abs <= 200 || maxValue2 <= 30 || boundingRect.height <= i15 || this.bodyPosition == i14) {
                            if (boundingRect.height < 140 || abs <= 200 || maxValue2 <= 30 || this.bodyPosition == 6) {
                                if (boundingRect.height < 140 || abs <= 300) {
                                    i16 = 100;
                                } else {
                                    i16 = 100;
                                    if (maxValue2 < 100 && this.bodyPosition == 6) {
                                        i10 = 6;
                                    }
                                }
                                if (boundingRect.height >= 130 && abs > i16 && maxValue2 < i16 && contourArea > 10000.0d && this.bodyPosition == 6) {
                                    i10 = 7;
                                } else if (boundingRect.height >= 130 && abs > 100 && contourArea > 10000.0d && maxValue2 > 30 && this.bodyPosition != 6) {
                                    i10 = 8;
                                } else if (boundingRect.height >= 130 && i < 40 && contourArea < 8000.0d) {
                                    i10 = 9;
                                } else if (boundingRect.height >= 100 && abs > 200 && contourArea > 10000.0d && this.bodyPosition != 6 && maxValue2 > 20) {
                                    i10 = 10;
                                } else if (boundingRect.height < 100 || contourArea <= 5000.0d || this.bodyPosition == 6 || maxValue2 <= 60) {
                                    i10 = (boundingRect.height < 200 || abs <= 200 || contourArea <= 10000.0d || this.bodyPosition != 3 || maxValue2 <= 5) ? 0 : 12;
                                } else {
                                    i10 = 11;
                                }
                            } else {
                                i10 = 5;
                            }
                        }
                        i10 = 4;
                    } else {
                        i10 = 2;
                    }
                    LogUtil.i("ImgprocisAddTh==" + i10 + " slope=" + maxValue2 + " isFirst=" + this.isFirst);
                    if (abs <= 400) {
                    }
                    if (i <= 150) {
                    }
                    if (this.bodyPosition != 6) {
                        deelFindByBinValue2 = deelFindByBinValue(i);
                        if (deelFindByBinValue(i + 1).getY() - deelFindByBinValue2.getY() > 30) {
                            i10 = 0;
                        }
                    }
                    if (i10 <= 0) {
                    }
                    if (boundingRect.width < i2) {
                    }
                    i11 = this.bodyPosition;
                    if (i11 == 6) {
                    }
                    int i292 = i9;
                    LogUtil.i("ImgprocPASS weight=" + d2 + " selCount=" + i292 + " index=" + i18 + str + i + " 超出长度：" + d + " y=" + boundingRect.y + " slope =" + maxValue2);
                    if (d2 > d4) {
                    }
                    i20 = i292 + 1;
                }
                d3 = d4;
                if (boundingRect.width <= i2 / 2 || boundingRect.y <= 20) {
                    i17 = i23;
                    i19 = i25;
                    i22 = i27;
                } else {
                    i22 = i27 + 1;
                    i17 = i23;
                    i19 = i25;
                }
                i18++;
                arrayList2 = arrayList;
                size = i24;
            } else {
                int i30 = size;
                int i31 = i19;
                int i32 = i20;
                int i33 = i22;
                if (i > this.binDefaultValue && i31 < 0 && this.isFirst) {
                    this.isFirst = false;
                    if (i > 40) {
                        return onBin((-1) + i);
                    }
                }
                int i34 = this.bodyPosition;
                if (i34 != 6) {
                    if (i32 > 3 && i > 80) {
                        return new int[]{-1};
                    }
                } else if (i32 > 2 && i > 80) {
                    return new int[]{-1};
                }
                if (i >= 50 || i31 != -2) {
                    if ((i32 < 2 || i >= 50) && (i32 < 3 || i >= 60)) {
                        if (i32 > 5) {
                            return new int[]{-1};
                        }
                        if (i34 != 6 || i32 <= 2 || i >= 150) {
                            if (i31 >= 0) {
                                MatOfPoint matOfPoint2 = arrayList.get(i31);
                                Rect boundingRect2 = Imgproc.boundingRect(matOfPoint2);
                                int arcLength2 = (int) (Imgproc.arcLength(new MatOfPoint2f(matOfPoint2.toArray()), true) - ((boundingRect2.height * 2) + (boundingRect2.width * 2)));
                                double contourArea2 = Imgproc.contourArea(matOfPoint2);
                                if (this.bodyPosition != 6) {
                                    if (i <= 90) {
                                        if (i30 > 10 || i32 != 1 || arcLength2 >= 150 || boundingRect2.height < 80) {
                                            i30 = i30;
                                        } else {
                                            i30 = i30;
                                            if (Math.abs((boundingRect2.height * boundingRect2.width) - contourArea2) < boundingRect2.height * 20) {
                                                return onBin(i + 1);
                                            }
                                        }
                                    }
                                    if (boundingRect2.height > 80 && i > 90 && Math.abs((boundingRect2.height * boundingRect2.width) - contourArea2) < boundingRect2.height * 10) {
                                        LogUtil.i("Imgproc可能是空打耦合剂");
                                        return new int[]{-2};
                                    }
                                }
                                if (arcLength2 > 600 && boundingRect2.height > 300) {
                                    LogUtil.i("Imgproc图像太复杂，异常情况，不取");
                                    return new int[]{-2};
                                }
                                this.isFirst = false;
                                List<Point> list = arrayList.get(i31).toList();
                                LogUtil.i("Imgproc继续分析轮廓 size:" + list.size());
                                PointArrayInfo upArraPoint2 = getUpArraPoint(list);
                                int[] binArray = upArraPoint2.getBinArray();
                                int[] bottomBinArray = upArraPoint2.getBottomBinArray();
                                int maxValue3 = ArrayUtil.getMaxValue(bottomBinArray) - ArrayUtil.getMinValue(bottomBinArray);
                                int i35 = ArrayUtil.getMaxValueAndX(binArray)[1];
                                int i36 = ArrayUtil.getMaxValueAndX(binArray)[0];
                                int minValue = ArrayUtil.getMinValue(binArray);
                                int i37 = i35 - minValue;
                                float algoDepth = (i35 * FatConfigManager.getInstance().getAlgoDepth(MxsellaDeviceManager.getInstance().getOcxo())) / BitmapUtil.sBitmapHight;
                                LogUtil.i("Imgprocy的最大值 :" + algoDepth + " slope=" + i37 + " slopeB=" + maxValue3 + " maxX=" + i36);
                                int i38 = this.bodyPosition;
                                if (i38 == 6) {
                                    double d6 = i35;
                                    i3 = i35;
                                    i4 = i36;
                                    if (d6 >= this.startPoint.y) {
                                        double d7 = minValue;
                                        i5 = i31;
                                        if (d7 <= this.endPoint.y) {
                                            if (d6 < this.startGrayPoint.y || d7 > this.endGrayPoint.y) {
                                                LogUtil.i("Imgproc不在匹配的模板范围之内 startGrayPoint.y =" + this.startGrayPoint.y + " startGrayPoint.y=" + this.endGrayPoint.y);
                                                return i < 50 ? onBin(1 + i) : new int[]{-4};
                                            } else if (i33 > 3 && i30 > 15 && Math.abs(i37 - maxValue3) > 20 && boundingRect2.y > 200) {
                                                LogUtil.i("Imgproc多出很多半条线，图像复杂，不出图");
                                                return new int[]{-2};
                                            } else {
                                                c = 1;
                                            }
                                        }
                                    }
                                    LogUtil.i("Imgproc不在匹配的模板范围之内 startPoint.y =" + this.startPoint.y + " endPoint.y=" + this.endPoint.y);
                                    return i < 50 ? onBin(i + 1) : new int[]{-4};
                                }
                                i3 = i35;
                                i4 = i36;
                                i5 = i31;
                                c = 1;
                                if (i38 == 3) {
                                    if (algoDepth > 23.0f) {
                                        return new int[]{-2};
                                    }
                                } else if (i38 == 2 && algoDepth > 23.0f) {
                                    return new int[]{-2};
                                }
                                if (this.bodyPosition != 6) {
                                    int i39 = binArray[c];
                                    int i40 = binArray[299];
                                    float avgValue = ArrayUtil.avgValue(binArray);
                                    LogUtil.i("Imgproc  x为0的Y值:" + i39 + " x为150的Y值=" + i40 + " 平均值=" + avgValue);
                                    if (i37 > 100 && i39 > avgValue && i40 > avgValue) {
                                        return new int[]{-1};
                                    }
                                }
                                LogUtil.i("Imgproc下边高度 slopeB = " + maxValue3 + " 上边高度slope=" + i37 + " 选中index=" + i5);
                                if (this.bodyPosition != 6) {
                                    if ((maxValue3 <= 20 && i37 > 20 && i < 120) || (maxValue3 < 30 && i37 > 30 && boundingRect2.height > 80)) {
                                        i6 = i + 1;
                                        FindContourInfo deelFindByBinValue3 = deelFindByBinValue(i6);
                                        int compare = ArrayUtil.compare(deelFindByBinValue3.getResultArray(), binArray);
                                        LogUtil.i("Imgproc上突，下平，可以适当添加阈值 slopeB = " + maxValue3 + " slope=" + i37 + " 加阈值后差异=" + compare);
                                        if (deelFindByBinValue3.getResultArray() != null && compare < 100) {
                                            return onBin(i6);
                                        }
                                        int i41 = 20;
                                        if (maxValue3 <= 20) {
                                            if (i37 > 50 && this.bodyPosition == 2) {
                                                LogUtil.i("Imgproc手臂 如果最后还是未能磨平，则不出图 ");
                                                return new int[]{-2};
                                            }
                                            i41 = 20;
                                        }
                                        if (maxValue3 <= i41 && i37 > 40 && this.bodyPosition != 2) {
                                            LogUtil.i("Imgproc如果最后还是未能磨平，则不出图 ");
                                            return new int[]{-2};
                                        }
                                    }
                                    i6 = i;
                                } else {
                                    if (maxValue3 <= 20 && i37 > 30 && i < 90) {
                                        LogUtil.i("Imgproc上突，下平，可以适当添加阈值 slopeB = " + maxValue3 + " slope=" + i37);
                                        i6 = i + 1;
                                        if (deelFindByBinValue(i6).getResultArray() != null) {
                                            return onBin(i6);
                                        }
                                    }
                                    i6 = i;
                                }
                                int i42 = boundingRect2.height;
                                int i43 = boundingRect2.y;
                                int i44 = 1;
                                int i45 = binArray[1];
                                int i46 = binArray[binArray.length - 1];
                                LogUtil.i("Imgproc开始打磨边界");
                                while (true) {
                                    i6 += i44;
                                    deelFindByBinValue = deelFindByBinValue(i6);
                                    if (deelFindByBinValue.getResultArray() == null) {
                                        LogUtil.i("Imgproc 退出0");
                                        break;
                                    }
                                    int[] resultArray = deelFindByBinValue.getResultArray();
                                    int maxValue4 = ArrayUtil.getMaxValue(resultArray) - ArrayUtil.getMinValue(resultArray);
                                    int compare2 = ArrayUtil.compare(resultArray, binArray);
                                    int compare3 = ArrayUtil.compare(bottomBinArray, deelFindByBinValue.getPointArrayInfo().getBottomBinArray());
                                    int[] iArr = bottomBinArray;
                                    maxValue = ArrayUtil.getMaxValue(deelFindByBinValue.getPointArrayInfo().getBottomBinArray()) - ArrayUtil.getMinValue(deelFindByBinValue.getPointArrayInfo().getBottomBinArray());
                                    int[] iArr2 = binArray;
                                    LogUtil.i("Imgproc compare = " + compare2 + " compareB=" + compare3 + " tempSlope=" + maxValue4 + " binValue=" + i6 + " findContourInfo=" + deelFindByBinValue.getFindCount() + " slopeB=" + maxValue + " starH=" + deelFindByBinValue.getStartHeight() + " endH=" + deelFindByBinValue.getEndHeight() + " maxX=" + deelFindByBinValue.getMaxX() + " maxValue=" + deelFindByBinValue.getMaxValue());
                                    int i47 = this.bodyPosition;
                                    if (i47 != 6) {
                                        if ((i47 == 3 || i47 == 5) && maxValue4 < 20 && maxValue < 20 && deelFindByBinValue.getHeight() > 20) {
                                            binArray = resultArray;
                                            i7 = maxValue4;
                                        } else {
                                            i7 = i37;
                                            binArray = iArr2;
                                        }
                                        if (compare2 > 0 && maxValue4 > i37 + 13) {
                                            LogUtil.i("Imgproc阈值已添加到极致，再添加则会变形，退出1");
                                            break;
                                        } else if ((Math.abs(i42 - deelFindByBinValue.getHeight()) <= 40 || Math.abs(i43 - deelFindByBinValue.getY()) <= 20 || Math.abs(i46 - deelFindByBinValue.getEndHeight()) <= 0) && (compare2 <= 100 || compare3 <= 100 || Math.abs(i43 - deelFindByBinValue.getY()) <= 20)) {
                                            if (i37 + 2 < maxValue4) {
                                                i8 = 30;
                                                if (i37 < 30) {
                                                    break;
                                                }
                                            } else {
                                                i8 = 30;
                                            }
                                            if (maxValue4 > 40 || maxValue > i8 || deelFindByBinValue.getHeight() <= 40) {
                                            }
                                            if (maxValue4 <= 20) {
                                                if (maxValue <= 20) {
                                                }
                                            }
                                            if (compare2 >= 20) {
                                                if (compare2 < 40) {
                                                }
                                                if (compare2 < 40) {
                                                }
                                                if (compare2 < 80) {
                                                }
                                                if (compare2 < 40) {
                                                    if (maxValue < 20) {
                                                    }
                                                }
                                                if (compare2 <= 40) {
                                                    if (maxValue4 > 40) {
                                                        if (i7 < 25) {
                                                        }
                                                    }
                                                }
                                                int i48 = (i37 - maxValue4) * 10;
                                                if (compare2 < i48) {
                                                    if (deelFindByBinValue.getHeight() > 40) {
                                                        if (maxValue4 <= 30) {
                                                        }
                                                    }
                                                }
                                                if (Math.abs(i46 - deelFindByBinValue.getEndHeight()) == 0) {
                                                }
                                                if (compare2 >= i48) {
                                                    break;
                                                } else if (deelFindByBinValue.getHeight() <= 40) {
                                                    break;
                                                } else if (maxValue4 > 45) {
                                                    break;
                                                } else if (compare3 != 0) {
                                                    break;
                                                }
                                            }
                                        } else {
                                            break;
                                        }
                                    } else if (compare2 > 0 && maxValue4 > i37 + 10 && i42 != deelFindByBinValue.getHeight()) {
                                        LogUtil.i("Imgproc阈值已添加到极致，再添加则会变形，退出1");
                                        break;
                                    } else if (compare2 > 30 && maxValue4 < 40 && Math.abs(i42 - deelFindByBinValue.getHeight()) > 50 && Math.abs(i43 - deelFindByBinValue.getY()) > 30) {
                                        LogUtil.i("Imgproc退出2+preHeight=" + i42 + " findContourInfo.getHeight()=" + deelFindByBinValue.getHeight() + " preY=" + i43 + " findContourInfo.getY()=" + deelFindByBinValue.getY());
                                        break;
                                    } else {
                                        int i49 = Math.abs(i46 - deelFindByBinValue.getEndHeight()) > 30 ? 1 : 0;
                                        if (Math.abs(i45 - deelFindByBinValue.getStartHeight()) > 30) {
                                            i49++;
                                        }
                                        if (Math.abs(i4 - deelFindByBinValue.getMaxX()) > 30) {
                                            i49++;
                                        }
                                        if (Math.abs(i3 - deelFindByBinValue.getMaxValue()) > 30) {
                                            i49++;
                                        }
                                        if (i49 >= 2) {
                                            LogUtil.i("Imgproc出现两种情况 " + deelFindByBinValue.getMaxX() + " v=" + deelFindByBinValue.getMaxValue());
                                            break;
                                        } else if (compare2 >= 30 && ((compare2 >= 70 || maxValue4 != i37 || maxValue > maxValue3 + 2) && (compare2 >= 60 || maxValue > maxValue3 + 2 || maxValue4 > i37 + 10))) {
                                            int i50 = i37 - maxValue4;
                                            if (compare2 >= (i50 * 10) + 10 && ((maxValue3 != maxValue || compare2 >= 35) && (compare2 >= 50 || i50 <= 10))) {
                                                if (compare2 <= 40) {
                                                    if (maxValue4 > 60) {
                                                        if (i37 < 25) {
                                                        }
                                                    }
                                                }
                                                if (Math.abs(i46 - deelFindByBinValue.getEndHeight()) >= 30 || Math.abs(i45 - deelFindByBinValue.getStartHeight()) >= 30 || Math.abs(i4 - deelFindByBinValue.getMaxX()) >= 30 || Math.abs(i3 - deelFindByBinValue.getMaxValue()) >= 30) {
                                                    break;
                                                }
                                                int y = deelFindByBinValue.getY();
                                                int height = deelFindByBinValue.getHeight();
                                                int endHeight = deelFindByBinValue.getEndHeight();
                                                int startHeight = deelFindByBinValue.getStartHeight();
                                                i4 = deelFindByBinValue.getMaxX();
                                                binArray = resultArray;
                                                maxValue3 = maxValue;
                                                i37 = maxValue4;
                                                i43 = y;
                                                i42 = height;
                                                i46 = endHeight;
                                                i45 = startHeight;
                                                bottomBinArray = iArr;
                                                i3 = deelFindByBinValue.getMaxValue();
                                                i44 = 1;
                                            }
                                        }
                                    }
                                    int y2 = deelFindByBinValue.getY();
                                    int height2 = deelFindByBinValue.getHeight();
                                    int endHeight2 = deelFindByBinValue.getEndHeight();
                                    int startHeight2 = deelFindByBinValue.getStartHeight();
                                    i4 = deelFindByBinValue.getMaxX();
                                    binArray = resultArray;
                                    maxValue3 = maxValue;
                                    i37 = maxValue4;
                                    i43 = y2;
                                    i42 = height2;
                                    i46 = endHeight2;
                                    i45 = startHeight2;
                                    bottomBinArray = iArr;
                                    i3 = deelFindByBinValue.getMaxValue();
                                    i44 = 1;
                                }
                                i37 = i7;
                                maxValue3 = maxValue;
                                if (this.bodyPosition != 6) {
                                    if (i37 > 30 && maxValue3 < 15) {
                                        binArray = ArrayUtil.smoothness(binArray);
                                    }
                                    if (deelFindByBinValue.getFindCount() >= 3 && i6 > 80) {
                                        LogUtil.i("Imgproc过虑到最后，如果还有三条以上比较完整的钱，则放弃查找 = ");
                                        return new int[]{-2};
                                    }
                                } else if (i37 > 50 && this.startGrayPoint.y > 0.0d) {
                                    binArray = ArrayUtil.rectifyForFubu(binArray, (int) this.startGrayPoint.y);
                                }
                                StringBuffer stringBuffer = new StringBuffer("x:");
                                StringBuffer stringBuffer2 = new StringBuffer("y:");
                                for (Point point : list) {
                                    stringBuffer.append(point.x + ",");
                                    stringBuffer2.append(point.y + ",");
                                }
                                StringBuffer stringBuffer3 = new StringBuffer("binArray-x:");
                                StringBuffer stringBuffer4 = new StringBuffer("binArray-y:");
                                for (int i51 = 0; i51 < 150; i51++) {
                                    int i52 = i51 * 2;
                                    stringBuffer3.append(binArray[i52] + ",");
                                    stringBuffer4.append(binArray[i52 + 1] + ",");
                                }
                                int[] subAvgArray = ArrayUtil.getSubAvgArray(binArray);
                                float avgValue2 = ArrayUtil.avgValue(subAvgArray);
                                if (this.queue.size() > 5) {
                                    this.queue.remove(0);
                                }
                                if (this.queue.size() > 0) {
                                    float f = 0.0f;
                                    Iterator<Float> it = this.queue.iterator();
                                    while (it.hasNext()) {
                                        f += it.next().floatValue();
                                    }
                                    float size2 = f / this.queue.size();
                                    Log.i("Imgproc", "plugin resultAvg=" + avgValue2 + " size=" + this.queue.size() + " avg=" + size2);
                                    float f2 = avgValue2 - size2;
                                    float abs2 = Math.abs(f2);
                                    int i53 = this.changeValue;
                                    if (abs2 >= i53) {
                                        ArrayList<Float> arrayList3 = this.queue;
                                        if (f2 <= 0.0f) {
                                            i53 = -i53;
                                        }
                                        arrayList3.add(Float.valueOf(size2 + i53));
                                        return new int[]{-2};
                                    }
                                    this.queue.add(Float.valueOf(avgValue2));
                                } else {
                                    this.queue.add(Float.valueOf(avgValue2));
                                }
                                return subAvgArray;
                            }
                            Log.i("Imgproc", "没有找到满足条件的一线条");
                            return new int[]{i23};
                        }
                        return onBin(i + 1);
                    }
                    return onBin(i + i32);
                }
                return onBin(i + 1);
            }
        }
    }

    public FindContourInfo deelFindByBinValue(int i) {
        double d;
        int i2;
        int i3;
        double d2;
        int i4;
        int i5;
        this.threSholdValue = i;
        FindContourInfo findContourInfo = new FindContourInfo();
        Mat mat = this.bin;
        if (mat != null) {
            mat.release();
        }
        Mat mat2 = new Mat();
        this.bin = mat2;
        Imgproc.threshold(this.blur, mat2, i, 255.0d, 0);
        ArrayList arrayList = new ArrayList();
        boolean z = true;
        Imgproc.findContours(this.bin, arrayList, new Mat(), 1, 1);
        double d3 = -1000.0d;
        int i6 = -1;
        int i7 = 0;
        int i8 = 0;
        while (i7 < arrayList.size()) {
            MatOfPoint matOfPoint = (MatOfPoint) arrayList.get(i7);
            Rect boundingRect = Imgproc.boundingRect(matOfPoint);
            double contourArea = Imgproc.contourArea(matOfPoint);
            double arcLength = Imgproc.arcLength(new MatOfPoint2f(matOfPoint.toArray()), z);
            int i9 = this.bitmapWidth;
            Log.i("OpenCvMeasureUtil","Imgproc deelFindByBinValue rect.y=" + boundingRect.y + " rect.width=" + boundingRect.width + " rect.height=" + boundingRect.height);
            if (boundingRect.width < i9 || boundingRect.y < 5 || ((boundingRect.y < 5 && boundingRect.height <= 100) || boundingRect.height <= 2 || contourArea <= 10.0d)) {
                d = d3;
                i2 = i7;
            } else {
                i8++;
                findContourInfo.getPointList().put(Integer.valueOf(i7), matOfPoint.toList());
                if (this.bodyPosition == 6) {
                    d = d3;
                    i3 = i7;
                    if (boundingRect.y + boundingRect.height < this.startPoint.y || boundingRect.y > this.endPoint.y) {
                        Log.i("OpenCvMeasureUtil","Imgproc deelFindByBinValue 不在匹配范围内");
                        i2 = i3;
                    }
                } else {
                    d = d3;
                    i3 = i7;
                }
                if (this.bodyPosition != 6) {
                    PointArrayInfo upArraPoint = getUpArraPoint(matOfPoint.toList());
                    int maxValue = ArrayUtil.getMaxValue(upArraPoint.getBinArray()) - ArrayUtil.getMinValue(upArraPoint.getBinArray());
                    if (maxValue <= 20) {
                        maxValue = 0;
                    }
                    if (this.bodyPosition == 0) {
                        i4 = ((i3 * 5) + boundingRect.height) - (maxValue * 5);
                        i5 = boundingRect.y / 3;
                    } else {
                        i4 = (i3 * 5) - (maxValue * 5);
                        i5 = boundingRect.y / 3;
                    }
                    d2 = i4 - i5;
                } else {
                    d2 = ((((boundingRect.width * 20) + contourArea) + (arcLength / 2.0d)) - (boundingRect.y * 5)) + boundingRect.height;
                }
                i2 = i3;
                Log.i("OpenCvMeasureUtil","Imgproc DeelFindByBinValue PASS weight=" + d2 + " selCount=" + i8 + " index=" + i2 + " binValue=" + i + " y=" + boundingRect.y + " height=" + boundingRect.height);
                if (d2 > d) {
                    findContourInfo.setHeight(boundingRect.height);
                    findContourInfo.setY(boundingRect.y);
                    d = d2;
                    i6 = i2;
                }
            }
            i7 = i2 + 1;
            d3 = d;
            z = true;
        }
        findContourInfo.setFindCount(i8);
        if (i6 >= 0) {
            PointArrayInfo upArraPoint2 = getUpArraPoint(((MatOfPoint) arrayList.get(i6)).toList());
            findContourInfo.setPointArrayInfo(upArraPoint2);
            findContourInfo.setResultArray(upArraPoint2.getBinArray());
            findContourInfo.setStartHeight(upArraPoint2.getBinArray()[1]);
            findContourInfo.setEndHeight(upArraPoint2.getBinArray()[upArraPoint2.getBinArray().length - 1]);
            findContourInfo.setMaxX(ArrayUtil.getMaxValueAndX(upArraPoint2.getBinArray())[0]);
            findContourInfo.setMaxValue(ArrayUtil.getMaxValueAndX(upArraPoint2.getBinArray())[1]);
        }
        return findContourInfo;
    }
    private PointArrayInfo getUpArraPoint(List<Point> list) {
        PointArrayInfo pointArrayInfo = new PointArrayInfo();
        int[] iArr = new int[300];
        int[] iArr2 = new int[300];
        for (int i = 0; i < list.size(); i++) {
            Point point = list.get(i);
            if (iArr[((int) point.x) * 2] == 0) {
                iArr[((int) point.x) * 2] = (int) point.x;
                iArr2[((int) point.x) * 2] = (int) point.x;
            }
            int i2 = iArr[(((int) point.x) * 2) + 1];
            if (point.y < i2 || i2 == 0) {
                iArr[(((int) point.x) * 2) + 1] = (int) point.y;
            }
            int i3 = iArr2[(((int) point.x) * 2) + 1];
            if (point.y > i3 || i3 == 0) {
                iArr2[(((int) point.x) * 2) + 1] = (int) point.y;
            }
        }
        pointArrayInfo.setBinArray(iArr);
        pointArrayInfo.setBottomBinArray(iArr2);
        return pointArrayInfo;
    }

}
