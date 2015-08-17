package com.accloud.ac_service_android_wifi_demo.controller;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.accloud.ac_service_android_wifi_demo.R;
import com.accloud.ac_service_android_wifi_demo.listerner.MyCountDownTimerListener;
import com.accloud.ac_service_android_wifi_demo.utils.MyCountDownTimer;
import com.accloud.ac_service_android_wifi_demo.utils.Pop;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACException;

/**
 * Created by xuri
 */
public class ResendVCodeController {

    private Context mContext;
    private String phone;
    private Button mResendButton = null;
    private final String mConfirm;
    private boolean mIsSending = true;         //是否正在发送
    private MyCountDownTimer mTimer = null;

    public ResendVCodeController(Context context, Button resend_btn, String phone, int time) {
        mContext = context;
        mConfirm = mContext.getString(R.string.vcode_resend);
        mResendButton = resend_btn;
        this.phone = phone;
        mResendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //重新发送验证码
                if (!mIsSending) {
                    getVCode();
                    mIsSending = true;
                    startCount();
                }
            }
        });
        mTimer = new MyCountDownTimer(time * 1000, 1000);
        mTimer.setOnListener(new MyCountDownTimerListener() {
            @Override
            public void onTick(int count) {
                mResendButton.setText(mConfirm + "(" + count + ")");
            }

            @Override
            public void onFinish() {
                mResendButton.setText(mConfirm);
                mResendButton.setEnabled(true);
                mResendButton.setTextColor(mContext.getResources().getColor(R.color.theme));
                mIsSending = false;
            }
        });

        startCount();
        mIsSending = true;
    }

    public void startCount() {
        getVCode();
        if (!mTimer.isRunning()) {
            mResendButton.setEnabled(false);
            mResendButton.setTextColor(mContext.getResources().getColor(R.color.login_text));
            mTimer.start();
            mResendButton.setText(mConfirm);
        }
    }

    public void getVCode() {
        AC.accountMgr().sendVerifyCode(phone, 1, new VoidCallback() {
            @Override
            public void success() {
            }

            @Override
            public void error(ACException e) {
                Pop.popToast(mContext, e.getMessage());
            }
        });
    }
}

