package com.accloud.ac_service_android_wifi_demo.activity.add;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.accloud.ac_service_android_wifi_demo.R;
import com.accloud.ac_service_android_wifi_demo.activity.BaseActivity;

public class DeviceReadyActivity extends BaseActivity {

    private Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_ready);
        setTitle("添加设备");
        setNavBtn(R.drawable.back, 0);

        nextBtn = (Button) findViewById(R.id.ready_next_btn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent();
                intent.setClass(DeviceReadyActivity.this, AddDeviceActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        finish();
    }
}
