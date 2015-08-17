package com.accloud.ac_service_android_wifi_demo.activity.personal;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.accloud.ac_service_android_wifi_demo.R;
import com.accloud.ac_service_android_wifi_demo.activity.BaseActivity;
import com.accloud.ac_service_android_wifi_demo.config.Config;
import com.accloud.ac_service_android_wifi_demo.utils.Pop;
import com.accloud.ac_service_android_wifi_demo.view.CusEditText;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACException;

public class InviteUserActivity extends BaseActivity {
    private CusEditText cusEditText;
    private Button invite_ok_button;
    private long deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceId = getIntent().getLongExtra("deviceId", 0);
        setContentView(R.layout.activity_invite_user);
        setTitle("邀请用户");
        setNavBtn(R.drawable.back, 0);
        cusEditText = (CusEditText) findViewById(R.id.invite_user_phone);
        invite_ok_button = (Button) findViewById(R.id.invite_ok);

        cusEditText.setText("请输入被邀请人的手机号");

        invite_ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = cusEditText.getText().toString();
                if (account.length() > 0) {
                    AC.bindMgr().bindDeviceWithUser(Config.SUBMAJORDOMAIN, deviceId, account, new VoidCallback() {
                        @Override
                        public void success() {
                            Pop.popToast(InviteUserActivity.this, "邀请成功");
                            finish();
                        }

                        @Override
                        public void error(ACException e) {
                            cusEditText.showHint();
                            if (e.getErrorCode() == 404 || e.getErrorCode() == ACException.INTERNET_ERROR)
                                cusEditText.setHintText(getString(R.string.internet_error));
                            else
                                cusEditText.setHintText(e.getMessage());
                        }
                    });
                } else {
                    cusEditText.setHintText(getString(R.string.login_phone_empty));
                    cusEditText.showHint();
                }

            }
        });

    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        switch (component) {
            case LEFT:
                finish();
                break;
        }
    }
}
