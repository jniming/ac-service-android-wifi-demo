package com.accloud.ac_service_android_wifi_demo.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.accloud.ac_service_android_wifi_demo.config.ConstantCache;


/**
 * Created by Administrator on 2015/1/21 0021.
 */
public class FontManager {
    public static void changeFonts(ViewGroup root) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View v = root.getChildAt(i);
            if (v instanceof TextView) {
                ((TextView) v).setTypeface(ConstantCache.tf);
            } else if (v instanceof Button) {
                ((Button) v).setTypeface(ConstantCache.tf);
            } else if (v instanceof EditText) {
                ((EditText) v).setTypeface(ConstantCache.tf);
            } else if (v instanceof ViewGroup) {
                changeFonts((ViewGroup) v);
            }
        }
    }
}
