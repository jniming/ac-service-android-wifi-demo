package com.accloud.ac_service_android_wifi_demo.utils;

import android.os.CountDownTimer;
import com.accloud.ac_service_android_wifi_demo.listerner.MyCountDownTimerListener;


public class MyCountDownTimer extends CountDownTimer {

    private boolean isRunning ;
    private MyCountDownTimerListener listener;

    public MyCountDownTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        isRunning = false;
    }

    public void setOnListener(MyCountDownTimerListener listener) {
        if (listener != null) {
            this.listener = listener;
        }
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if (listener != null) {
            listener.onTick((int) (millisUntilFinished / 1000));
        }
    }

    @Override
    public void onFinish() {
        isRunning = false;
        listener.onFinish();
    }

    public void TimeStart() {
        if (!isRunning) {
            isRunning = true;
            this.start();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void TimeCacel(){
        this.cancel();
    }

};