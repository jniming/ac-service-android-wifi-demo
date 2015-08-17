package com.accloud.ac_service_android_wifi_demo.activity.personal;

import android.os.Bundle;
import android.view.View;
import com.accloud.ac_service_android_wifi_demo.R;
import com.accloud.ac_service_android_wifi_demo.activity.BaseActivity;


/**
 * Created by Xuri on 2015/1/27.
 */
public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle(getString(R.string.about_title));
        setNavBtn(R.drawable.back, 0);
    }

    @Override
    protected void HandleTitleBarEvent(BaseActivity.TitleBar component, View v) {
        finish();
    }
}
