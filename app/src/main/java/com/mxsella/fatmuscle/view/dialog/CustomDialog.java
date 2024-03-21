package com.mxsella.fatmuscle.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mxsella.fat_muscle.R;
import com.mxsella.fatmuscle.common.MyApplication;
import com.mxsella.fatmuscle.sdk.util.DensityUtil;

public class CustomDialog extends Dialog {
    private static DialogManager.OnDismissListener onDismissListener;

    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, int i) {
        super(context, i);
    }

    @Override // android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        super.dismiss();
        DialogManager.OnDismissListener onDismissListener2 = onDismissListener;
        if (onDismissListener2 != null) {
            onDismissListener2.onDismiss();
        }
    }

    /* loaded from: classes.dex */
    public static class Builder {
        private View contentView;
        private Context context;
        private OnClickListener leftButtonClickListener;
        private String leftButtonText;
        private CharSequence message;
        private OnClickListener rightButtonClickListener;
        private String rightButtonText;
        private OnClickListener singleButtonClickListener;
        private String singleButtonText;
        private String title;
        private int rightButtonColor = -1;
        private int leftButtonColor = -1;
        private int singleButtonColor = -1;
        private int titleColor = -1;
        private boolean isSingle = false;
        private boolean clickOutIsCancle = false;
        private boolean isShowVirtKey = true;
        private boolean isVerticalScreen = false;
        private int gravity = -1;
        private boolean isSpan = false;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(CharSequence charSequence) {
            this.message = charSequence;
            return this;
        }

        public Builder setMessage(int i) {
            this.message = (String) this.context.getText(i);
            return this;
        }

        public Builder setTitle(int i) {
            this.title = (String) this.context.getText(i);
            return this;
        }

        public Builder setTitle(String str) {
            this.title = str;
            return this;
        }

        public Builder setContentView(View view) {
            this.contentView = view;
            return this;
        }

        public Builder setRightButton(int i, OnClickListener onClickListener) {
            this.rightButtonText = (String) this.context.getText(i);
            this.rightButtonClickListener = onClickListener;
            return this;
        }

        public Builder setGravity(int i) {
            this.gravity = i;
            return this;
        }

        public Builder setRightButton(String str, OnClickListener onClickListener) {
            this.rightButtonText = str;
            this.rightButtonClickListener = onClickListener;
            return this;
        }

        public Builder setDissmssListener(DialogManager.OnDismissListener onDismissListener) {
            DialogManager.OnDismissListener unused = CustomDialog.onDismissListener = onDismissListener;
            return this;
        }

        public Builder setLeftButton(int i, OnClickListener onClickListener) {
            this.leftButtonText = (String) this.context.getText(i);
            this.leftButtonClickListener = onClickListener;
            return this;
        }

        public Builder setLeftButton(String str, OnClickListener onClickListener) {
            this.leftButtonText = str;
            this.leftButtonClickListener = onClickListener;
            return this;
        }

        public Builder setRightButtonColor(int i) {
            this.rightButtonColor = i;
            return this;
        }

        public Builder setLeftButtonColor(int i) {
            this.leftButtonColor = i;
            return this;
        }

        public Builder setSingleButtonColor(int i) {
            this.singleButtonColor = i;
            return this;
        }

        public Builder setSingle(boolean z) {
            this.isSingle = z;
            return this;
        }

        public Builder setSingleButton(int i, OnClickListener onClickListener) {
            this.singleButtonText = (String) this.context.getText(i);
            this.singleButtonClickListener = onClickListener;
            return this;
        }

        public Builder setSingleButton(String str, OnClickListener onClickListener) {
            this.singleButtonText = str;
            this.singleButtonClickListener = onClickListener;
            return this;
        }

        public Builder setClickOutIsCancle(boolean z) {
            this.clickOutIsCancle = z;
            return this;
        }

        public Builder setShowVirtKey(boolean z) {
            this.isShowVirtKey = z;
            return this;
        }

        public Builder setTitleColor(int i) {
            this.titleColor = i;
            return this;
        }

        public Builder setVerticalScreen(boolean z) {
            this.isVerticalScreen = z;
            return this;
        }

        public Builder setSpan(boolean z) {
            this.isSpan = z;
            return this;
        }

        public CustomDialog create() {
            View inflate;
            LayoutInflater layoutInflater = (LayoutInflater) MyApplication.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final CustomDialog customDialog = new CustomDialog(this.context, R.style.Dialog);
            inflate = layoutInflater.inflate(R.layout.app_vertical_dialog_custom, (ViewGroup) null);
            customDialog.addContentView(inflate, new LinearLayout.LayoutParams(-1, -2));
            if (this.rightButtonText != null) {
                ((Button) inflate.findViewById(R.id.btn_right)).setText(this.rightButtonText);
                if (this.rightButtonClickListener != null) {
                    ((Button) inflate.findViewById(R.id.btn_right)).setOnClickListener(view -> Builder.this.rightButtonClickListener.onClick(customDialog, -1));
                }
            }
            if (this.leftButtonText != null) {
                ((Button) inflate.findViewById(R.id.btn_left)).setText(this.leftButtonText);
                if (this.leftButtonClickListener != null) {
                    ((Button) inflate.findViewById(R.id.btn_left)).setOnClickListener(view -> Builder.this.leftButtonClickListener.onClick(customDialog, -2));
                }
            }
            if (this.singleButtonText != null) {
                ((Button) inflate.findViewById(R.id.btn_single)).setText(this.singleButtonText);
                if (this.singleButtonClickListener != null) {
                    ((Button) inflate.findViewById(R.id.btn_single)).setOnClickListener(view -> Builder.this.singleButtonClickListener.onClick(customDialog, -2));
                }
            }
            if (this.message != null) {
                ((TextView) inflate.findViewById(R.id.tv_content)).setText(this.message);
                ((TextView) inflate.findViewById(R.id.tv_content_sigle)).setText(this.message);
                ((TextView) inflate.findViewById(R.id.tv_content_sigle)).setMovementMethod(LinkMovementMethod.getInstance());
                ((TextView) inflate.findViewById(R.id.tv_content)).setMovementMethod(LinkMovementMethod.getInstance());
            }
            if (this.titleColor != -1) {
                ((TextView) inflate.findViewById(R.id.tv_title)).setTextColor(this.titleColor);
            }
            String str = this.title;
            if (str != null && !str.equals("")) {
                ((TextView) inflate.findViewById(R.id.tv_title)).setText(this.title);
            } else {
                ((TextView) inflate.findViewById(R.id.tv_title)).setVisibility(View.GONE);
                ((TextView) inflate.findViewById(R.id.tv_content)).setVisibility(View.GONE);
                ((TextView) inflate.findViewById(R.id.tv_content_sigle)).setVisibility(View.VISIBLE);
            }
            customDialog.setContentView(inflate);
            if (this.isSingle) {
                inflate.findViewById(R.id.rl_double).setVisibility(View.INVISIBLE);
                inflate.findViewById(R.id.btn_single).setVisibility(View.VISIBLE);
            } else {
                inflate.findViewById(R.id.rl_double).setVisibility(View.VISIBLE);
                inflate.findViewById(R.id.btn_single).setVisibility(View.INVISIBLE);
            }
            if (this.clickOutIsCancle) {
                customDialog.setCanceledOnTouchOutside(false);
                customDialog.setCancelable(false);
            } else {
                customDialog.setCanceledOnTouchOutside(true);
                customDialog.setCancelable(true);
            }
            if (this.isSpan) {
                ((TextView) inflate.findViewById(R.id.tv_content)).setMovementMethod(LinkMovementMethod.getInstance());
                ((TextView) inflate.findViewById(R.id.tv_content)).setHighlightColor(0);
            }
            if (this.rightButtonColor != -1) {
                ((Button) inflate.findViewById(R.id.btn_right)).setTextColor(this.rightButtonColor);
            }
            if (this.leftButtonColor != -1) {
                ((Button) inflate.findViewById(R.id.btn_left)).setTextColor(this.leftButtonColor);
            }
            if (this.singleButtonColor != -1) {
                ((Button) inflate.findViewById(R.id.btn_single)).setTextColor(this.singleButtonColor);
            }
            if (this.gravity != -1) {
                ((TextView) inflate.findViewById(R.id.tv_content)).setGravity(this.gravity);
            }
            if (this.isVerticalScreen) {
                customDialog.getWindow().setGravity(80);
                WindowManager.LayoutParams attributes = customDialog.getWindow().getAttributes();
                attributes.y = DensityUtil.dip2px(10.0f);
                attributes.width = (int) (DensityUtil.getScreenWidth() * 0.95f);
                customDialog.getWindow().setAttributes(attributes);
            }
            if (!this.isShowVirtKey) {
                customDialog.getWindow().getDecorView().setSystemUiVisibility(2);

                customDialog.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(i -> customDialog.getWindow().getDecorView().setSystemUiVisibility(Build.VERSION.SDK_INT >= 19 ? 5894 : 1799));
            }
            return customDialog;
        }

        private void setBoldText(TextView textView) {
            textView.getPaint().setFakeBoldText(true);
        }
    }

}
