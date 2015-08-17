package com.accloud.ac_service_android_wifi_demo.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.accloud.ac_service_android_wifi_demo.R;
import com.accloud.ac_service_android_wifi_demo.activity.BaseActivity;
import com.accloud.ac_service_android_wifi_demo.config.AppConstant;
import com.accloud.ac_service_android_wifi_demo.utils.StringUtils;
import com.accloud.ac_service_android_wifi_demo.view.CusEditText;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;

/**
 * Created by Xuri on 2015/4/7.
 */
public class PhoneActivity extends BaseActivity {

    private int flag;
    private LinearLayout hint;
    private CusEditText phoneEditText;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        flag = getIntent().getIntExtra("flag", AppConstant.REGISTER);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        setNavBtn(R.drawable.back, 0);
        initView();
        if (flag == AppConstant.REGISTER) {
            setTitle(getString(R.string.login_register));
            hint.setVisibility(View.GONE);
            phoneEditText.setText(getString(R.string.login_phone));
        } else {
            setTitle(getString(R.string.login_reset));
            hint.setVisibility(View.VISIBLE);
            phoneEditText.setText(getString(R.string.phone_text));
        }
    }

    public void initView() {
        hint = (LinearLayout) findViewById(R.id.phone_text_hint);
        phoneEditText = (CusEditText) findViewById(R.id.phone);
        phoneEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        next = (Button) findViewById(R.id.phone_next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneEditText.hideHint();
                String phone = phoneEditText.getText();
                if (phone.length() == 0) {
                    phoneEditText.showHint();
                    phoneEditText.setHintText(getString(R.string.phone_text_error_hint));
                } else if (StringUtils.isEmail(phone) || StringUtils.isPhoneNumber(phone)) {
                    checkExist(phone);
                } else {
                    phoneEditText.showHint();
                    phoneEditText.setHintText(getString(R.string.phone_text_error_hint));
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

    private void checkExist(final String phone) {
        AC.accountMgr().checkExist(phone, new PayloadCallback<Boolean>() {
            @Override
            public void success(Boolean isExist) {
                if (flag == AppConstant.REGISTER && !isExist) {
                    Intent intent = new Intent(PhoneActivity.this, VCodeActivity.class);
                    intent.putExtra("flag", flag);
                    intent.putExtra("phone", phone);
                    startActivity(intent);
                } else if (flag == AppConstant.RESETPASSWORD && isExist) {
                    Intent intent = new Intent(PhoneActivity.this, VCodeActivity.class);
                    intent.putExtra("flag", flag);
                    intent.putExtra("phone", phone);
                    startActivity(intent);
                } else if (flag == AppConstant.REGISTER) {
                    phoneEditText.showHint();
                    phoneEditText.setHintText(getString(R.string.phone_exist));
                } else {
                    phoneEditText.showHint();
                    phoneEditText.setHintText(getString(R.string.phone_no_exist));
                }
            }

            @Override
            public void error(ACException e) {
                phoneEditText.showHint();
                if (e.getErrorCode() == 404 || e.getErrorCode() == ACException.INTERNET_ERROR)
                    phoneEditText.setHintText(getString(R.string.internet_error));
                else
                    phoneEditText.setHintText(e.getMessage());
            }
        });
    }
}
