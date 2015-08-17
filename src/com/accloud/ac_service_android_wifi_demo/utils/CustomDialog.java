package com.accloud.ac_service_android_wifi_demo.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import com.accloud.ac_service_android_wifi_demo.listerner.OnCustomDialogListener;
import com.accloud.utils.LogUtil;

/**
 * Created by Administrator on 2015/1/15 0015.
 */
public class CustomDialog {
    public static String TAG = "CustomDialog";

    private int layout;
    private int style;
    private Dialog dialog = null;
    private final Context context;
    private OnCustomDialogListener mListener;

    public CustomDialog(Context context, int layout) {
        this(context, layout, 0);
    }

    public CustomDialog(Context context, int layout, int style) {
        this.context = context;
        this.layout = layout;
        this.style = style;
        init();
    }

    private void init() {
        dialog = new Dialog(context, style) {
            @Override
            protected void onStop() {
                super.onStop();
                if (mListener != null) {
                    mListener.onFinish();
                }
            }

            @Override
            public void show() {
                super.show();
            }

            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event) {
                if (mListener != null) {
                    mListener.onKeyDown(keyCode, event);
                }
                return super.onKeyDown(keyCode, event);
            }
        };
        dialog.setContentView(layout);
    }

    public <T extends View> T findView(int id) {
        return (T) dialog.findViewById(id);
    }


    public CustomDialog setOnCustomDialogListener(OnCustomDialogListener listener) {
        this.mListener = listener;
        return this;
    }

    public CustomDialog show() {
        try {
            dialog.show();
        } catch (Exception e) {
            LogUtil.e(TAG, "dialog err", e);
        }
        return this;
    }

    public void dismiss() {
        try {
            dialog.dismiss();
        } catch (Exception e) {
            LogUtil.e(TAG, "dialog err", e);
        }
    }

    public void cancel() {
        try {
            dialog.cancel();
        } catch (Exception e) {
            LogUtil.e(TAG, "dialog err", e);
        }
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }
}
