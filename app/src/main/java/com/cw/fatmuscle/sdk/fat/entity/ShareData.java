package com.cw.fatmuscle.sdk.fat.entity;

import android.graphics.Bitmap;

import java.io.Serializable;

public class ShareData implements Serializable {
    public String FilePath;
    public String[] ImageArray;
    public String ImagePath;
    public SHARETYPE ShareType;
    public Bitmap bitmap;
    public String imagePath;
    public String imageUrl;
    public String text;
    public TYPE type;

    /* loaded from: classes.dex */
    public enum SHARETYPE {
        text,
        image,
        imgarray,
        video,
        imageUrl,
        videoUrl
    }

    /* loaded from: classes.dex */
    public enum TYPE {
        weibo,
        qqkongjian,
        weixin,
        pengyouquan,
        qq,
        facebook,
        twitter,
        youtubo,
        whatsapp,
        instagram
    }

}
