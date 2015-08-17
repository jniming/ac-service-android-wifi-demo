package com.accloud.ac_service_android_wifi_demo.config;

import android.graphics.Typeface;
import com.accloud.ac_service_android_wifi_demo.application.MainApplication;
import com.accloud.ac_service_android_wifi_demo.model.DeviceUpdateInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/28.
 */
public class ConstantCache {
    public static Typeface tf = Typeface.createFromAsset(MainApplication.getInstance().getAssets(), "fonts/myfont.ttf");
    public static List<DeviceUpdateInfo> deviceUpdateInfo = new ArrayList<>();
    public static boolean hasUpdate = false;
}
