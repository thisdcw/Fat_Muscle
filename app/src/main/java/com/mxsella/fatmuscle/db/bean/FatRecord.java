package com.mxsella.fatmuscle.db.bean;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "record")
public class FatRecord implements Serializable {

    @PrimaryKey
    private Integer id;
    @ColumnInfo(name = "array_avg")
    private String arrayAvg;
    @ColumnInfo(name = "hight")
    private Integer bitmapHight;
    @ColumnInfo(name = "body_position")
    private String bodyPosition;
    @ColumnInfo(name = "depth")
    private Integer depth;
    @ColumnInfo(name = "family_id")
    private Integer familyId;
    @ColumnInfo(name = "http_record_image")
    private String httpRecordImage;

    @ColumnInfo(name = "is_local_cache")
    private boolean isLocalCache;
    @ColumnInfo(name = "ocxo")
    private Integer ocxo;
    @ColumnInfo(name = "pkg_name")
    private String pkgName;
    @ColumnInfo(name = "record_date")
    private String recordDate;
    @ColumnInfo(name = "record_image")
    private String recordImage;
    @ColumnInfo(name = "record_type")
    private Integer recordType;
    @ColumnInfo(name = "record_value")
    private String recordValue;
    @ColumnInfo(name = "sn")
    private String sn;
    @ColumnInfo(name = "trans_type")
    private String transType;
    @ColumnInfo(name = "user_id")
    private String userId;

    /* loaded from: classes.dex */
    public enum TYPE {
        FAT(2),
        MUSCLE(1);

        int value;

        TYPE(int i) {
            this.value = i;
        }

        public int value() {
            return this.value;
        }
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer num) {
        this.id = num;
    }

    public String getRecordDate() {
        return this.recordDate;
    }

    public void setRecordDate(String str) {
        this.recordDate = str;
    }

    public String getRecordValue() {
        return this.recordValue;
    }

    public void setRecordValue(String str) {
        this.recordValue = str;
    }

    public String getBodyPosition() {
        return this.bodyPosition;
    }

    public void setBodyPosition(String str) {
        this.bodyPosition = str;
    }

    public String getRecordImage() {
        return this.recordImage;
    }

    public void setRecordImage(String str) {
        this.recordImage = str;
    }

    public String getSn() {
        return this.sn;
    }

    public void setSn(String str) {
        this.sn = str;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String str) {
        this.userId = str;
    }

    public Integer getRecordType() {
        return this.recordType;
    }

    public void setRecordType(Integer num) {
        this.recordType = num;
    }

    public String getTransType() {
        return this.transType;
    }

    public void setTransType(String str) {
        this.transType = str;
    }

    public String getPkgName() {
        return this.pkgName;
    }

    public void setPkgName(String str) {
        this.pkgName = str;
    }

    public Integer getOcxo() {
        return this.ocxo;
    }

    public void setOcxo(Integer num) {
        this.ocxo = num;
    }

    public Integer getBitmapHight() {
        return this.bitmapHight;
    }

    public void setBitmapHight(Integer num) {
        this.bitmapHight = num;
    }

    public void setIntArrayAvg(int[] iArr) {
        StringBuffer stringBuffer = new StringBuffer();
        if (iArr != null && iArr.length > 0) {
            for (int i : iArr) {
                stringBuffer.append(i);
                stringBuffer.append(",");
            }
        }
        this.arrayAvg = stringBuffer.toString();
    }

    public int[] getIntArrayAvg() {
        String str = this.arrayAvg;
        if (str != null) {
            String[] split = str.split(",");
            int length = split.length;
            int[] iArr = new int[length];
            for (int i = 0; i < length; i++) {
                iArr[i] = Integer.parseInt(split[i]);
            }
            return iArr;
        }
        return null;
    }

    public String getArrayAvg() {
        return this.arrayAvg;
    }

    public void setArrayAvg(String str) {
        this.arrayAvg = str;
    }

    public FatRecord() {
        this.depth = null;
        this.familyId = null;
        this.arrayAvg = null;
        this.isLocalCache = false;
    }

    public FatRecord(Integer num, String str, String str2, String str3, String str4, String str5, String str6, Integer num2, Integer num3, String str7, boolean z, Integer num4, Integer num5, String str8, String str9, Integer num6) {
        this.depth = null;
        this.familyId = null;
        this.arrayAvg = null;
        this.isLocalCache = false;
        this.id = num;
        this.recordDate = str.replace("-", "/");
        this.recordValue = str2;
        this.bodyPosition = str3;
        this.recordImage = str4;
        this.sn = str5;
        this.userId = str6;
        this.depth = num2;
        this.familyId = num3;
        this.arrayAvg = str7;
        this.isLocalCache = z;
        this.ocxo = num4;
        this.bitmapHight = num5;
        this.transType = str8;
        this.pkgName = str9;
        this.recordType = num6;
    }

    public boolean isLocalCache() {
        return this.isLocalCache;
    }

    public void setLocalCache(boolean z) {
        this.isLocalCache = z;
    }

    public Integer getDepth() {
        return this.depth;
    }

    public void setDepth(Integer num) {
        this.depth = num;
    }

    public Integer getFamilyId() {
        return this.familyId;
    }

    public void setFamilyId(Integer num) {
        this.familyId = num;
    }

    public String getHttpRecordImage() {
        return this.httpRecordImage;
    }

    public void setHttpRecordImage(String str) {
        this.httpRecordImage = str;
    }

    @Override
    public String toString() {
        return "FatRecord{" +
                "id=" + id +
                ", arrayAvg='" + arrayAvg + '\'' +
                ", bitmapHight=" + bitmapHight +
                ", bodyPosition='" + bodyPosition + '\'' +
                ", depth=" + depth +
                ", familyId=" + familyId +
                ", httpRecordImage='" + httpRecordImage + '\'' +
                ", isLocalCache=" + isLocalCache +
                ", ocxo=" + ocxo +
                ", pkgName='" + pkgName + '\'' +
                ", recordDate='" + recordDate + '\'' +
                ", recordImage='" + recordImage + '\'' +
                ", recordType=" + recordType +
                ", recordValue='" + recordValue + '\'' +
                ", sn='" + sn + '\'' +
                ", transType='" + transType + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}