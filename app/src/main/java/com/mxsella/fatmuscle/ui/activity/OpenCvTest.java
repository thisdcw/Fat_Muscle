package com.mxsella.fatmuscle.ui.activity;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.databinding.DataBindingUtil;

import com.mxsella.fat_muscle.R;
import com.mxsella.fat_muscle.databinding.ActivityOpencvBinding;
import com.mxsella.fatmuscle.common.base.BaseActivity;
import com.mxsella.fatmuscle.db.bean.FatRecord;
import com.mxsella.fatmuscle.sdk.fat.entity.BitmapMsg;
import com.mxsella.fatmuscle.sdk.fat.entity.DeviceMsg;
import com.mxsella.fatmuscle.sdk.fat.entity.FindContourInfo;
import com.mxsella.fatmuscle.sdk.fat.manager.FatConfigManager;
import com.mxsella.fatmuscle.sdk.fat.manager.MxsellaDeviceManager;
import com.mxsella.fatmuscle.sdk.fat.utils.BitmapUtil;
import com.mxsella.fatmuscle.sdk.fat.utils.OpenCvMeasureUtil;
import com.mxsella.fatmuscle.sdk.util.ToastUtil;
import com.mxsella.fatmuscle.ui.adapter.ParamSpinnerAdapter;
import com.mxsella.fatmuscle.utils.ArrayUtil;
import com.mxsella.fatmuscle.utils.LogUtil;
import com.mxsella.fatmuscle.view.MeasureView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class OpenCvTest extends BaseActivity implements AdapterView.OnItemSelectedListener, MxsellaDeviceManager.DeviceInterface {

    public static final int BIN_V = 20;
    private ImageView binIv;
    private ImageView blurIv;
    private Button buttonBinAdd;
    private Button buttonBinDel;
    private Button buttonThreAdd;
    private Button buttonThreDel;
    private ImageView cannyIv;
    private ImageView findBinIv;
    private ImageView findCannyIv;
    private ImageView grayIv;
    private MeasureView mMeasureView;
    private ImageView orgIv;
    Spinner spinnerBlur;
    Spinner spinnerBody;
    private ImageView zoneIv;
    private int blurValue = 13;
    private int binValue = 20;
    private int cannyValue = 3;
    private Bitmap bitmap = null;
    private Bitmap kenlerBitmap = null;
    private Mat src = null;
    private Mat gray = null;
    private Mat blur = null;
    private Mat bin = null;
    private Mat edges = null;
    private Point startPoint = new Point();
    private Point endPoint = new Point();
    boolean isHist = true;
    boolean isFirst = true;
    int bodyIndex = 0;
    int count = 0;
    boolean isSwitchTest = true;
    boolean isSwitchDebug = false;
    int[] binArray = null;
    ActivityOpencvBinding opencvBinding;

    @Override // com.marvoto.fat.manager.MarvotoDeviceManager.DeviceInterface
    public void onConnected() {
        LogUtil.d("已连接");
    }

    @Override // com.marvoto.fat.manager.MarvotoDeviceManager.DeviceInterface
    public void onDisconnected(int i, String str) {
        LogUtil.d("已断开连接");
    }


    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    protected void initView() {
        opencvBinding = DataBindingUtil.setContentView(this, R.layout.activity_opencv);


        this.orgIv = (ImageView) findViewById(R.id.image_org);
        this.grayIv = (ImageView) findViewById(R.id.image_gray);
        this.blurIv = (ImageView) findViewById(R.id.image_blur);
        this.binIv = (ImageView) findViewById(R.id.image_bin);
        this.cannyIv = (ImageView) findViewById(R.id.image_Canny);
        this.zoneIv = (ImageView) findViewById(R.id.image_find_z1);
        this.findCannyIv = (ImageView) findViewById(R.id.image_find_canny);
        this.findBinIv = (ImageView) findViewById(R.id.image_find_bin);
        this.buttonBinAdd = (Button) findViewById(R.id.bin_add);
        this.buttonBinDel = (Button) findViewById(R.id.bin_del);
        this.buttonThreAdd = (Button) findViewById(R.id.thre_add);
        this.buttonThreDel = (Button) findViewById(R.id.thre_del);
        this.mMeasureView = (MeasureView) findViewById(R.id.view_measure);
        MxsellaDeviceManager.getInstance().registerDeviceInterface(this);
        this.spinnerBlur = (Spinner) findViewById(R.id.blur_array);
        this.spinnerBody = (Spinner) findViewById(R.id.body_array);

        findViewById(R.id.save_image).setOnClickListener(view -> {
            try {
                OpenCvTest.this.measure();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        initBlurSpinner(this.spinnerBlur);
        initBodySpinner(this.spinnerBody);
    }

    public void initBlurSpinner(Spinner spinner) {
        int[] iArr = {5, 9, 11, 13, 19};
        int i = 0;
        for (int i2 = 0; i2 < 5; i2++) {
            if (this.blurValue == iArr[i2]) {
                i = i2;
            }
        }
        spinner.setAdapter((SpinnerAdapter) new ParamSpinnerAdapter(this, iArr));
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(i);
    }

    public void initBodySpinner(Spinner spinner) {
        int[] iArr = {0, 2, 3, 5, 6};
        int i = 0;
        for (int i2 = 0; i2 < 5; i2++) {
            if (this.bodyIndex == iArr[i2]) {
                i = i2;
            }
        }
        spinner.setAdapter((SpinnerAdapter) new ParamSpinnerAdapter(this, iArr));
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(i);
    }

    private void initSrc() {
        Mat mat = new Mat();
        this.src = mat;
        Utils.bitmapToMat(this.bitmap, mat);
        Mat mat2 = this.src;
        Imgproc.cvtColor(mat2, mat2, 1);
        onGray(null);
    }

    public void onGray(View view) {
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
        Utils.bitmapToMat(getAssetBitmap("temp.jpg"), mat2);
        Mat mat4 = new Mat(mat3.cols(), mat3.rows(), CvType.CV_32FC1);
        Imgproc.matchTemplate(mat3, mat2, mat4, 1);
        Core.normalize(mat4, mat4, com.github.mikephil.charting.utils.Utils.DOUBLE_EPSILON, 1.0d, 32, -1, new Mat());
        Core.MinMaxLocResult minMaxLoc = Core.minMaxLoc(mat4);
        double d = minMaxLoc.minLoc.x;
        double d2 = minMaxLoc.minLoc.y;
        Log.i("Imgproc", "匹配结果 min" + minMaxLoc.minVal + " max " + minMaxLoc.maxVal + " x=" + d + " y=" + d2);
        Imgproc.rectangle(mat3, new Point(d, d2), new Point(d + mat2.cols(), d2 + mat2.rows()), new Scalar(com.github.mikephil.charting.utils.Utils.DOUBLE_EPSILON, 255.0d, com.github.mikephil.charting.utils.Utils.DOUBLE_EPSILON), 4, 16);
        Bitmap createBitmap2 = Bitmap.createBitmap(mat3.width(), mat3.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat3, createBitmap2);
        this.grayIv.setImageBitmap(createBitmap2);
        onBlur(this.blurValue);
    }

    private Point matchTemplate(Mat mat, Mat mat2) {
        Mat mat3 = new Mat((mat.cols() - mat2.cols()) + 1, (mat.rows() - mat2.rows()) + 1, CvType.CV_32FC1);
        Imgproc.matchTemplate(mat, mat2, mat3, 1);
        Core.normalize(mat3, mat3, com.github.mikephil.charting.utils.Utils.DOUBLE_EPSILON, 1.0d, 32, -1, new Mat());
        Core.MinMaxLocResult minMaxLoc = Core.minMaxLoc(mat3);
        double d = minMaxLoc.minLoc.x;
        double d2 = minMaxLoc.minLoc.y;
        Log.i("Imgproc", "匹配结果matchTemplate--> min" + minMaxLoc.minVal + " max " + minMaxLoc.maxVal + " x=" + d + " y=" + d2);
        if (minMaxLoc.maxVal > 0.95d) {
            return new Point(d, d2);
        }
        return null;
    }

    public void onBlur(int i) {
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
        if (OpenCvMeasureUtil.getInstance().getBodyPosition() == 6) {
            Utils.bitmapToMat(getAssetBitmap("fuzhiji_blur.jpg"), mat3);
        } else {
            Utils.bitmapToMat(getAssetBitmap("line_blur.jpg"), mat3);
        }
        Point matchTemplate = matchTemplate(mat4, mat3);
        this.startPoint = matchTemplate;
        if (matchTemplate != null) {
            Point point = new Point(this.startPoint.x + mat3.cols(), this.startPoint.y + mat3.rows());
            this.endPoint = point;
            Imgproc.rectangle(mat4, this.startPoint, point, new Scalar(com.github.mikephil.charting.utils.Utils.DOUBLE_EPSILON, 255.0d, com.github.mikephil.charting.utils.Utils.DOUBLE_EPSILON), 4, 16);
            createBitmap = Bitmap.createBitmap(mat4.width(), mat4.height(), Bitmap.Config.ARGB_8888);
        }
        Utils.matToBitmap(mat4, createBitmap);
        this.blurIv.setImageBitmap(createBitmap);
        onBin(this.binValue);
    }

    public void onSetBlur(View view) {
        this.isSwitchDebug = !this.isSwitchDebug;
        int parseInt = Integer.parseInt(this.spinnerBlur.getSelectedItem().toString());
        this.blurValue = parseInt;
        onBlur(parseInt);
    }

    public void onBin(int i) {
        Mat mat = this.bin;
        if (mat != null) {
            mat.release();
        }
        Mat mat2 = new Mat();
        this.bin = mat2;
        Imgproc.threshold(this.blur, mat2, i, 255.0d, 0);
        Bitmap createBitmap = Bitmap.createBitmap(this.bin.width(), this.bin.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(this.bin, createBitmap);
        this.binIv.setImageBitmap(createBitmap);
        this.buttonBinAdd.setText("阈值 + " + i);
        this.buttonBinDel.setText("阈值 -" + i);
        if (this.isSwitchDebug) {
            onCanny(this.cannyValue);
        }
    }

    public void onBinDel(View view) {
        if (this.isSwitchDebug) {
            int i = this.binValue;
            this.binValue = i - 1;
            onBin(i);
            return;
        }
        initMeaView(OpenCvMeasureUtil.getInstance().deelFindByBinValue(OpenCvMeasureUtil.getInstance().getThreSholdValue() - 1).getResultArray());
    }

    public void onBinAdd(View view) {
        if (this.isSwitchDebug) {
            int i = this.binValue;
            this.binValue = i + 1;
            onBin(i);
            return;
        }
        FindContourInfo deelFindByBinValue = OpenCvMeasureUtil.getInstance().deelFindByBinValue(OpenCvMeasureUtil.getInstance().getThreSholdValue() + 1);
        this.binValue = OpenCvMeasureUtil.getInstance().getThreSholdValue();
        initMeaView(deelFindByBinValue.getResultArray());
    }

    private void onCanny(int i) {
        Mat mat = this.edges;
        if (mat != null) {
            mat.release();
        }
        Mat mat2 = new Mat();
        this.edges = mat2;
        Imgproc.Canny(this.bin, mat2, 100.0d, 200.0d, 3, true);
        Bitmap createBitmap = Bitmap.createBitmap(this.edges.width(), this.edges.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(this.edges, createBitmap);
        this.cannyIv.setImageBitmap(createBitmap);
        findBin();
        findZ1(null);
    }

    public void onCannyAdd(View view) {
        boolean z = !this.isHist;
        this.isHist = z;
        this.buttonThreAdd.setText(z ? "加强 已开启" : "加强 已关闭");
        onBin(this.binValue);
    }

    public void onCannyDel(View view) {
        int i = this.cannyValue;
        this.cannyValue = i - 1;
        onCanny(i);
    }

    public void findCanny(View view) {
        findCanny();
    }

    private void findCanny() {
        ArrayList<MatOfPoint> arrayList = new ArrayList<>();
        Mat mat = new Mat();
        Imgproc.dilate(this.edges, new Mat(), Imgproc.getStructuringElement(1, new Size(3.0d, 3.0d)));
        Imgproc.findContours(this.edges, arrayList, mat, 0, 1);
        this.findCannyIv.setImageBitmap(calArea(arrayList, mat, this.edges));
    }

    public void findBin(View view) {
        findBin();
    }

    public void findZ1(View view) {
        getImageOneDimensional();
        this.mMeasureView.setArray(getAvgArray(this.binArray), FatRecord.TYPE.FAT);
        this.mMeasureView.setDepth(FatConfigManager.getInstance().getCurDeviceDepth(), MxsellaDeviceManager.getInstance().getOcxo(), BitmapUtil.sBitmapHight);
        this.mMeasureView.setPosition(2.0f);
        ToastUtil.showToast(this, "value=2.0", 1);
        this.zoneIv.setImageBitmap(this.bitmap);
    }

    public static int[] getAvgArray(int[] iArr) {
        if (iArr == null) {
            return null;
        }
        int length = iArr.length;
        int[] iArr2 = new int[((int) Math.ceil(length / 26)) * 2];
        int i = 0;
        int i2 = 0;
        while (i < length) {
            iArr2[i2] = iArr[i];
            int i3 = i2 + 1;
            int i4 = i + 1;
            iArr2[i3] = iArr[i4];
            i2 = i3 + 1;
            i = i4 + 24 + 1;
        }
        return iArr2;
    }

    private void findBin() {
        ArrayList<MatOfPoint> arrayList = new ArrayList<>();
        Mat mat = new Mat();
        Imgproc.findContours(this.bin, arrayList, mat, 0, 2);
        Bitmap calArea = calArea(arrayList, mat, this.bin);
        if (calArea == null) {
            return;
        }
        this.findBinIv.setImageBitmap(calArea);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.i("cv", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
        } else {
            Log.i("cv", "OpenCV library found inside package. Using it!");
        }
    }

    @Override // com.marvoto.fat.manager.MarvotoDeviceManager.DeviceInterface
    public void onMessage(DeviceMsg deviceMsg) {
        if (deviceMsg.getMsgId() != 4261) {
            return;
        }
        BitmapMsg bitmapMsg = (BitmapMsg) deviceMsg;
        int i = C20252.$SwitchMap$com$marvoto$fat$entity$BitmapMsg$State[bitmapMsg.getState().ordinal()];
        if (i == 1) {
            this.binValue = 20;
            this.mMeasureView.setPosition(0.0f);
        } else if (i == 2) {
            measure();
        } else if (i != 3) {
        } else {
            this.orgIv.setImageBitmap(bitmapMsg.getBitmap());
        }
    }
    
    static /* synthetic */ class C20252 {
        static final /* synthetic */ int[] $SwitchMap$com$marvoto$fat$entity$BitmapMsg$State;

        static {
            int[] iArr = new int[BitmapMsg.State.values().length];
            $SwitchMap$com$marvoto$fat$entity$BitmapMsg$State = iArr;
            try {
                iArr[BitmapMsg.State.START.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$marvoto$fat$entity$BitmapMsg$State[BitmapMsg.State.END.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$marvoto$fat$entity$BitmapMsg$State[BitmapMsg.State.RUN.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void measure() {
        this.bodyIndex = Integer.parseInt(this.spinnerBody.getSelectedItem().toString());
        this.isFirst = true;
        Bitmap imageOneDimensional = getImageOneDimensional();
        this.bitmap = imageOneDimensional;
        this.orgIv.setImageBitmap(imageOneDimensional);
        this.count++;
        if (this.isSwitchDebug) {
            initSrc();
        } else {
            this.binArray = OpenCvMeasureUtil.getInstance().measure(this.bitmap, 13, this.bodyIndex);
            this.binValue = OpenCvMeasureUtil.getInstance().getThreSholdValue();
            initSrc();
            initMeaView(this.binArray);
        }
        this.binValue = 20;
    }

    private void initMeaView(int[] iArr) {
        if (iArr != null) {
            Log.i("Imgproc", "查找结果 result= " + iArr[0]);
            ToastUtil.showToast(this, "result code=" + iArr[0], 1);
        }
        this.mMeasureView.setArray(iArr, FatRecord.TYPE.FAT);
        this.mMeasureView.setDepth(FatConfigManager.getInstance().getCurDeviceDepth(), MxsellaDeviceManager.getInstance().getOcxo(), BitmapUtil.sBitmapHight);
        this.mMeasureView.setPosition(ArrayUtil.avgValue(iArr));
        this.mMeasureView.setShowUltrasoundImg(true);
        this.zoneIv.setImageBitmap(this.bitmap);
        onBin(OpenCvMeasureUtil.getInstance().getThreSholdValue());
    }

    private Bitmap getAssetBitmap(String str) {
        InputStream inputStream;
        try {
            inputStream = getAssets().open(str);
        } catch (IOException e) {
            e.printStackTrace();
            inputStream = null;
        }
        return BitmapFactory.decodeStream(inputStream);
    }

    Bitmap getImageOneDimensional() {
        AssetManager assets = getAssets();
        try {
            int i = this.bodyIndex;
            String str = "yb";
            if (i == 2) {
                str = "sb";
            } else if (i != 0) {
                if (i == 6) {
                    str = "fb";
                } else if (i == 3) {
                    str = "dt";
                } else if (i == 5) {
                    str = "xt";
                }
            }
            String[] list = assets.list(str);
            if (this.count >= list.length) {
                this.count = 0;
            }
            if (list[this.count].equalsIgnoreCase("temp")) {
                this.count++;
            }
            if (this.count >= list.length) {
                this.count = 0;
            }
            Bitmap decodeStream = BitmapFactory.decodeStream(assets.open(str + "/" + list[this.count]));
            if (this.isSwitchTest) {
                return decodeStream;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapUtil.getImageOneDimensional();
    }

    byte[] getPxByBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth() * bitmap.getHeight();
        int[] iArr = new int[width];
        byte[] bArr = new byte[width];
        bitmap.getPixels(iArr, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int i = 0; i < width; i++) {
            int i2 = iArr[i];
            int i3 = (16711680 & i2) >> 16;
            int i4 = (65280 & i2) >> 8;
            int i5 = i2 & 255;
            bArr[i] = (byte) Color.argb(255, i3, i4, i5);
            System.out.println("r=" + i3 + ",g=" + i4 + ",b=" + i5);
        }
        return bArr;
    }

    private Bitmap calArea(ArrayList<MatOfPoint> arrayList, Mat mat, Mat mat2) {
        int i;
        int i2;
        double d;
        int size = arrayList.size();
        Log.i("Imgproc", "contours size:" + size);
        double d2 = -10.0d;
        int i3 = 0;
        int i4 = 0;
        int i5 = -1;
        while (i3 < arrayList.size()) {
            MatOfPoint matOfPoint = arrayList.get(i3);
            Rect boundingRect = Imgproc.boundingRect(matOfPoint);
            int i6 = i5;
            double contourArea = Imgproc.contourArea(matOfPoint);
            MatOfPoint2f matOfPoint2f = new MatOfPoint2f(matOfPoint.toArray());
            double arcLength = Imgproc.arcLength(matOfPoint2f, true) * 0.01d;
            Imgproc.approxPolyDP(matOfPoint2f, new MatOfPoint2f(), arcLength, true);
            Log.i("Imgproc", "arcLength:" + arcLength + " y:" + boundingRect.y + " rect.width=" + boundingRect.width + " rect.height=" + boundingRect.height + " contourArea:" + contourArea + " binValue=" + this.binValue);
            if (boundingRect.width < this.bitmap.getWidth() || ((boundingRect.y < 10 && boundingRect.height <= 100) || boundingRect.height <= 2 || contourArea <= 10.0d)) {
                i2 = size;
                d = d2;
            } else {
                i2 = size;
                d = d2;
                if ((boundingRect.y + boundingRect.height < this.startPoint.y || boundingRect.y > this.endPoint.y) && this.binValue >= 50) {
                    i4++;
                    Log.i("Imgproc", "==不在匹配范围内");
                } else {
                    int i7 = (boundingRect.height * 2) + (boundingRect.width * 2);
                    double d3 = arcLength - i7;
                    int i8 = (int) d3;
                    Log.i("Imgproc", "合理周长 size:" + i7 + " 超出长度：" + d3);
                    boolean z = (boundingRect.height >= 150 && contourArea > 10000.0d) || (boundingRect.height >= 140 && contourArea > 7000.0d && i8 < 150) || i8 > 300 || ((boundingRect.height >= 140 && i8 > 200) || ((boundingRect.height >= 130 && i8 > 100 && contourArea > 10000.0d) || (boundingRect.height >= 130 && this.binValue < 40 && contourArea < 8000.0d)));
                    if (i8 <= 400 || boundingRect.height <= 300 || this.binValue <= 50 || Math.abs((boundingRect.height * boundingRect.width) - contourArea) <= boundingRect.height * 20) {
                        if (this.binValue > 150 && boundingRect.height > 200 && Math.abs((boundingRect.height * boundingRect.width) - contourArea) < boundingRect.height * 20) {
                            return null;
                        }
                        if (z && this.isFirst) {
                            int i9 = this.binValue + 1;
                            this.binValue = i9;
                            onBin(i9);
                            return null;
                        }
                        double d4 = ((contourArea + (arcLength / 2.0d)) - (boundingRect.y * 5)) + boundingRect.height;
                        Log.i("Imgproc", "PASS weight=" + d4 + " selCount=" + i4);
                        if (d4 > d) {
                            d2 = d4;
                            i5 = i3;
                        } else {
                            i5 = i6;
                            d2 = d;
                        }
                        i4++;
                        i3++;
                        size = i2;
                    }
                }
            }
            i5 = i6;
            d2 = d;
            i3++;
            size = i2;
        }
        int i10 = size;
        int i11 = i5;
        int i12 = this.binValue;
        if (i12 > 20 && i11 < 0 && this.isFirst) {
            this.isFirst = false;
            if (i12 > 40) {
                int i13 = i12 - 1;
                this.binValue = i13;
                onBin(i13);
                return null;
            }
            return null;
        } else if (i4 > 5) {
            return null;
        } else {
            if (i4 > 2) {
                int i14 = i12 + i4;
                this.binValue = i14;
                onBin(i14);
                return null;
            } else if (i11 >= 0) {
                MatOfPoint matOfPoint2 = arrayList.get(i11);
                Rect boundingRect2 = Imgproc.boundingRect(matOfPoint2);
                MatOfPoint2f matOfPoint2f2 = new MatOfPoint2f(matOfPoint2.toArray());
                double arcLength2 = Imgproc.arcLength(matOfPoint2f2, true);
                int i15 = (int) (arcLength2 - ((boundingRect2.height * 2) + (boundingRect2.width * 2)));
                double contourArea2 = Imgproc.contourArea(matOfPoint2);
                if (this.binValue < 90 && i10 <= 10 && i4 == 1 && i15 < 150 && boundingRect2.height < 100 && Math.abs((boundingRect2.height * boundingRect2.width) - contourArea2) < boundingRect2.height * 20) {
                    int i16 = this.binValue + 1;
                    this.binValue = i16;
                    onBin(i16);
                    return null;
                } else if (i15 <= 600 || boundingRect2.height <= 300) {
                    if (boundingRect2.height <= 80 || this.binValue <= 100 || Math.abs((boundingRect2.height * boundingRect2.width) - contourArea2) >= boundingRect2.height * 10) {
                        this.isFirst = false;
                        Mat mat3 = new Mat();
                        this.src.copyTo(mat3);
                        Imgproc.drawContours(mat3, arrayList, i11, new Scalar(com.github.mikephil.charting.utils.Utils.DOUBLE_EPSILON, 255.0d, com.github.mikephil.charting.utils.Utils.DOUBLE_EPSILON), 4, 4, mat);
                        arrayList.get(i11).toList();
                        MatOfPoint2f matOfPoint2f3 = new MatOfPoint2f();
                        Imgproc.approxPolyDP(matOfPoint2f2, matOfPoint2f3, arcLength2, true);
                        List<Point> list = matOfPoint2f3.toList();
                        Log.i("Imgproc", "size:" + list.size());
                        this.binArray = new int[300];
                        int[] iArr = new int[300];
                        new ArrayList();
                        for (int i17 = 0; i17 < list.size(); i17++) {
                            Point point = list.get(i17);
                            if (this.binArray[((int) point.x) * 2] == 0) {
                                this.binArray[((int) point.x) * 2] = (int) point.x;
                                iArr[((int) point.x) * 2] = (int) point.x;
                            }
                            int i18 = this.binArray[(((int) point.x) * 2) + 1];
                            if (point.y < i18 || i18 == 0) {
                                this.binArray[(((int) point.x) * 2) + 1] = (int) point.y;
                            }
                            int i19 = iArr[(((int) point.x) * 2) + 1];
                            if (point.y > i19 || i19 == 0) {
                                iArr[(((int) point.x) * 2) + 1] = (int) point.y;
                            }
                        }
                        if (ArrayUtil.getMaxValue(this.binArray) < this.startPoint.y || ArrayUtil.getMinValue(this.binArray) > this.endPoint.y) {
                            this.binArray = null;
                            Log.i("Imgproc", "不在匹配的模板范围之内");
                            int i20 = this.binValue;
                            if (i20 < 50) {
                                int i21 = i20 + 1;
                                this.binValue = i21;
                                onBin(i21);
                            }
                            return null;
                        }
                        int maxValue = ArrayUtil.getMaxValue(iArr) - ArrayUtil.getMinValue(iArr);
                        int maxValue2 = ArrayUtil.getMaxValue(this.binArray) - ArrayUtil.getMinValue(this.binArray);
                        if (maxValue <= 20 && maxValue2 > 20 && (i = this.binValue) < 90) {
                            int i22 = i + 1;
                            this.binValue = i22;
                            onBin(i22);
                            Log.i("Imgproc", "上突，下平，可以适当添加阈值 slopeB = " + maxValue + " slope=" + maxValue2);
                            return null;
                        }
                        while (true) {
                            int i23 = this.binValue + 1;
                            this.binValue = i23;
                            int[] deelFindByBinValue = deelFindByBinValue(i23);
                            if (deelFindByBinValue == null) {
                                break;
                            }
                            int maxValue3 = ArrayUtil.getMaxValue(deelFindByBinValue) - ArrayUtil.getMinValue(deelFindByBinValue);
                            int compare = ArrayUtil.compare(deelFindByBinValue, this.binArray);
                            Log.i("Imgproc", "compareCount = " + compare + " tempSlope=" + maxValue3 + " binValue=" + this.binValue + " slopeB=" + maxValue);
                            if (compare >= 20 && (compare > 40 || maxValue3 <= 60 || maxValue2 >= 25 || this.binValue >= 50)) {
                                break;
                            }
                            this.binArray = deelFindByBinValue;
                        }
                        StringBuffer stringBuffer = new StringBuffer("x:");
                        StringBuffer stringBuffer2 = new StringBuffer("y:");
                        for (Point point2 : list) {
                            stringBuffer.append(point2.x + ",");
                            stringBuffer2.append(point2.y + ",");
                        }
                        Log.i("Imgproc", "" + ((Object) stringBuffer));
                        Log.i("Imgproc", "" + ((Object) stringBuffer2));
                        StringBuffer stringBuffer3 = new StringBuffer("binArray-x:");
                        StringBuffer stringBuffer4 = new StringBuffer("binArray-y:");
                        for (int i24 = 0; i24 < 150; i24++) {
                            int i25 = i24 * 2;
                            stringBuffer3.append(this.binArray[i25] + ",");
                            stringBuffer4.append(this.binArray[i25 + 1] + ",");
                        }
                        Log.i("Imgproc", "startP:" + this.startPoint.y + ((Object) stringBuffer3));
                        Log.i("Imgproc", "endP:" + this.endPoint.y + ((Object) stringBuffer4));
                        Bitmap createBitmap = Bitmap.createBitmap(mat3.width(), mat3.height(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(mat3, createBitmap);
                        return createBitmap;
                    }
                    return null;
                } else {
                    return null;
                }
            } else {
                Bitmap createBitmap2 = Bitmap.createBitmap(mat2.width(), mat2.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat2, createBitmap2);
                return createBitmap2;
            }
        }
    }

    private int[] deelFindByBinValue(int i) {
        int i2;
        Mat mat = this.bin;
        if (mat != null) {
            mat.release();
        }
        Mat mat2 = new Mat();
        this.bin = mat2;
        Imgproc.threshold(this.blur, mat2, i, 255.0d, 0);
        ArrayList arrayList = new ArrayList();
        Imgproc.findContours(this.bin, arrayList, new Mat(), 1, 1);
        double d = -10.0d;
        int i3 = -1;
        for (int i4 = 0; i4 < arrayList.size(); i4++) {
            MatOfPoint matOfPoint = (MatOfPoint) arrayList.get(i4);
            Rect boundingRect = Imgproc.boundingRect(matOfPoint);
            double contourArea = Imgproc.contourArea(matOfPoint);
            double arcLength = Imgproc.arcLength(new MatOfPoint2f(matOfPoint.toArray()), true);
            if (boundingRect.width < this.bitmap.getWidth() || ((boundingRect.y < 10 && boundingRect.height <= 100) || boundingRect.height <= 2 || contourArea <= 10.0d)) {
                i2 = i3;
            } else {
                i2 = i3;
                if (boundingRect.y + boundingRect.height < this.startPoint.y || boundingRect.y > this.endPoint.y) {
                    Log.i("Imgproc", "==不在匹配范围内");
                } else {
                    int i5 = boundingRect.height;
                    int i6 = boundingRect.width;
                    double d2 = ((((boundingRect.width * 20) + contourArea) + (arcLength / 2.0d)) - (boundingRect.y * 5)) + boundingRect.height;
                    if (d2 > d) {
                        d = d2;
                        i3 = i4;
                    }
                }
            }
            i3 = i2;
        }
        int i7 = i3;
        if (i7 >= 0) {
            int[] iArr = new int[300];
            List<Point> list = ((MatOfPoint) arrayList.get(i7)).toList();
            for (int i8 = 0; i8 < list.size(); i8++) {
                Point point = list.get(i8);
                if (iArr[((int) point.x) * 2] == 0) {
                    iArr[((int) point.x) * 2] = (int) point.x;
                }
                int i9 = iArr[(((int) point.x) * 2) + 1];
                if (point.y < i9 || i9 == 0) {
                    iArr[(((int) point.x) * 2) + 1] = (int) point.y;
                }
            }
            return iArr;
        }
        return null;
    }

}
