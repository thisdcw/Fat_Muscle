package com.mxsella.fatmuscle.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class DialogManager {
    private boolean clickOutIsCancle;
    private Context context;
    private int customeLayoutId;
    private CharSequence dialogMessage;
    private String dialogTitle;
    private View dialogView;
    private boolean isShowVirtKey;
    private boolean isSingle;
    private boolean isSpan;
    private boolean isVerticalScreen;
    private String leftBtnText;
    private int leftButtonColor;
    private OnDialogListener listener;
    private CustomDialog mCustomDialog;
    private int mGravity;
    private OnDismissListener onDismissListener;
    private String rightBtnText;
    private int rightButtonColor;
    private String singleBtnText;
    private int singleButtonColor;
    private int titleColor;

    /* loaded from: classes.dex */
    public interface OnDialogListener {
        void dialogBtnLeftListener(View view, DialogInterface dialogInterface, int i);

        void dialogBtnRightOrSingleListener(View view, DialogInterface dialogInterface, int i);
    }

    /* loaded from: classes.dex */
    public interface OnDismissListener {
        void onDismiss();
    }

    public DialogManager(Context context, int i, String str, String str2, String str3) {
        this.rightButtonColor = -1;
        this.leftButtonColor = -1;
        this.singleButtonColor = -1;
        this.titleColor = -1;
        this.mGravity = -1;
        this.isSpan = false;
        this.clickOutIsCancle = false;
        this.isShowVirtKey = true;
        this.isVerticalScreen = false;
        this.context = context;
        this.customeLayoutId = i;
        this.dialogTitle = str;
        this.rightBtnText = str2;
        this.leftBtnText = str3;
        this.dialogView = View.inflate(context, i, null);
    }

    public DialogManager(Context context, String str, CharSequence charSequence, String str2, String str3) {
        this.rightButtonColor = -1;
        this.leftButtonColor = -1;
        this.singleButtonColor = -1;
        this.titleColor = -1;
        this.mGravity = -1;
        this.isSpan = false;
        this.clickOutIsCancle = false;
        this.isShowVirtKey = true;
        this.isVerticalScreen = false;
        this.context = context;
        this.isSingle = false;
        this.dialogTitle = str;
        this.dialogMessage = charSequence;
        this.rightBtnText = str2;
        this.leftBtnText = str3;
    }

    public DialogManager(Context context, String str, CharSequence charSequence, String str2) {
        this.rightButtonColor = -1;
        this.leftButtonColor = -1;
        this.singleButtonColor = -1;
        this.titleColor = -1;
        this.mGravity = -1;
        this.isSpan = false;
        this.clickOutIsCancle = false;
        this.isShowVirtKey = true;
        this.isVerticalScreen = false;
        this.context = context;
        this.isSingle = true;
        this.dialogTitle = str;
        this.dialogMessage = charSequence;
        this.singleBtnText = str2;
    }

    public void setDialogMessage(String str) {
        this.dialogMessage = str;
    }

    public void setVerticalScreen(boolean z) {
        this.isVerticalScreen = z;
    }

    public View getDialogView() {
        return this.dialogView;
    }

    public void setDialogView(View view) {
        this.dialogView = view;
    }

    public void setClickOutIsCancle(boolean z) {
        this.clickOutIsCancle = z;
    }

    public void setShowVirtKey(boolean z) {
        this.isShowVirtKey = z;
    }

    public void setRightButtonColor(int i) {
        this.rightButtonColor = i;
    }

    public void setLeftButtonColor(int i) {
        this.leftButtonColor = i;
    }

    public void setSingleButtonColor(int i) {
        this.singleButtonColor = i;
    }

    public void setSpan(boolean z) {
        this.isSpan = z;
    }

    public void setGravity(int i) {
        this.mGravity = i;
    }

    public void setTitleColor(int i) {
        this.titleColor = i;
    }

    public void showDialog() {
        CustomDialog customDialog = this.mCustomDialog;
        if (customDialog == null || !customDialog.isShowing()) {
            CustomDialog.Builder builder = new CustomDialog.Builder(this.context);
            builder.setTitle(this.dialogTitle);
            CharSequence charSequence = this.dialogMessage;
            if (charSequence != null) {
                builder.setMessage(charSequence);
            } else {
                builder.setContentView(this.dialogView);
            }
            if (this.clickOutIsCancle) {
                builder.setClickOutIsCancle(true);
            } else {
                builder.setClickOutIsCancle(false);
            }
            if (this.isSpan) {
                builder.setSpan(true);
            }
            int i = this.mGravity;
            if (i != -1) {
                builder.setGravity(i);
            }
            if (this.isShowVirtKey) {
                builder.setShowVirtKey(true);
            } else {
                builder.setShowVirtKey(false);
            }
            CustomDialog create = builder.setRightButton(this.rightBtnText, new DialogInterface.OnClickListener() { // from class: com.marvoto.fat.dialog.DialogManager.3
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    dialogInterface.dismiss();
                    if (DialogManager.this.listener != null) {
                        DialogManager.this.listener.dialogBtnRightOrSingleListener(DialogManager.this.dialogView, dialogInterface, i2);
                    }
                }
            }).setLeftButton(this.leftBtnText, new DialogInterface.OnClickListener() { // from class: com.marvoto.fat.dialog.DialogManager.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    dialogInterface.dismiss();
                    if (DialogManager.this.listener != null) {
                        DialogManager.this.listener.dialogBtnLeftListener(DialogManager.this.dialogView, dialogInterface, i2);
                    }
                }
            }).setSingleButton(this.singleBtnText, new DialogInterface.OnClickListener() { // from class: com.marvoto.fat.dialog.DialogManager.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    dialogInterface.dismiss();
                    if (DialogManager.this.listener != null) {
                        DialogManager.this.listener.dialogBtnRightOrSingleListener(DialogManager.this.dialogView, dialogInterface, i2);
                    }
                }
            }).setRightButtonColor(this.rightButtonColor).setLeftButtonColor(this.leftButtonColor).setSingleButtonColor(this.singleButtonColor).setTitleColor(this.titleColor).setSingle(this.isSingle).setDissmssListener(this.onDismissListener).setVerticalScreen(this.isVerticalScreen).create();
            this.mCustomDialog = create;
            create.setCancelable(false);
            this.mCustomDialog.show();
        }
    }

    public void showPosition(Context context, int i, int i2, int i3) {
        CustomDialog customDialog = this.mCustomDialog;
        if (customDialog != null) {
            Window window = customDialog.getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            if (i3 == 0) {
                window.setGravity(17);
            } else {
                window.setGravity(i3);
            }
            window.setAttributes(attributes);
        }
    }

    public void dismissDialog() {
        CustomDialog customDialog = this.mCustomDialog;
        if (customDialog != null) {
            customDialog.dismiss();
        }
    }

    public CustomDialog getDialog() {
        return this.mCustomDialog;
    }

    public DialogManager setOnDiaLogListener(OnDialogListener onDialogListener) {
        this.listener = onDialogListener;
        return this;
    }

    public DialogManager setOnDissmissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
        return this;
    }

}
