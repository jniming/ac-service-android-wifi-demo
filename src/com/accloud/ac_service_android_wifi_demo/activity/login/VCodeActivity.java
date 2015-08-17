package com.accloud.ac_service_android_wifi_demo.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.accloud.ac_service_android_wifi_demo.R;
import com.accloud.ac_service_android_wifi_demo.activity.BaseActivity;
import com.accloud.ac_service_android_wifi_demo.config.AppConstant;
import com.accloud.ac_service_android_wifi_demo.controller.ResendVCodeController;
import com.accloud.ac_service_android_wifi_demo.view.CusEditText;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;

/**
 * Created by Xuri on 2015/4/7.
 */
public class VCodeActivity extends BaseActivity {
    private int flag;
    private String phone;

    private CusEditText vcodeEditText;
    private Button next;
    private Button resend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        flag = getIntent().getIntExtra("flag", AppConstant.REGISTER);
        phone = getIntent().getStringExtra("phone");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vcode);
        setNavBtn(R.drawable.back, 0);
        setTitle(getString(R.string.vcode_title));

        initView();
        new ResendVCodeController(this, resend, phone, 60);
    }

    private void initView() {
        vcodeEditText = (CusEditText) findViewById(R.id.vcode);
        vcodeEditText.setText(getString(R.string.vcode_text));
        next = (Button) findViewById(R.id.vcode_next);
        resend = (Button) findViewById(R.id.vcode_resend);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vcodeEditText.hideHint();
                String verifyCode = vcodeEditText.getText();
                if (verifyCode.length() == 0 || verifyCode.length() != 6) {
                    vcodeEditText.showHint();
                    vcodeEditText.setHintText(getString(R.string.vcode_error));
                } else
                    checkVerifyCode(verifyCode);
            }
        });
    }

    private void checkVerifyCode(final String verifyCode) {
        AC.accountMgr().checkVerifyCode(phone, verifyCode, new PayloadCallback<Boolean>() {
            @Override
            public void success(Boolean aBoolean) {
                if (aBoolean) {
                    Intent intent = new Intent(VCodeActivity.this, PasswordActivity.class);
                    intent.putExtra("flag", flag);
                    intent.putExtra("phone", phone);
                    intent.putExtra("verifyCode", verifyCode);
                    startActivity(intent);
                } else {
                    vcodeEditText.showHint();
                    vcodeEditText.setHintText(getString(R.string.vcode_error));
                }
            }

            @Override
            public void error(ACException e) {
                vcodeEditText.showHint();
                vcodeEditText.setHintText(getString(R.string.vcode_error));
                if (e.getErrorCode() == 404|| e.getErrorCode() == ACException.INTERNET_ERROR)
                    vcodeEditText.setHintText(getString(R.string.internet_error));
                else
                    vcodeEditText.setHintText(e.getMessage());
            }
        });
    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        if (component == TitleBar.LEFT)
            finish();
    }
}
