package com.accloud.ac_service_android_wifi_demo.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import com.accloud.ac_service_android_wifi_demo.R;
import com.accloud.ac_service_android_wifi_demo.activity.BaseActivity;
import com.accloud.ac_service_android_wifi_demo.application.MainApplication;
import com.accloud.ac_service_android_wifi_demo.config.AppConstant;
import com.accloud.ac_service_android_wifi_demo.model.UserInfo;
import com.accloud.ac_service_android_wifi_demo.utils.Pop;
import com.accloud.ac_service_android_wifi_demo.utils.StringUtils;
import com.accloud.ac_service_android_wifi_demo.view.CusEditText;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACUserInfo;

/**
 * Created by Xuri on 2015/4/7.
 */
public class PasswordActivity extends BaseActivity {

    private int flag;
    private String phone;
    private String verifyCode;

    private CusEditText nameEditText;
    private CusEditText pwdEditText;
    private CusEditText repwdEditText;
    private Button finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        flag = getIntent().getIntExtra("flag", AppConstant.REGISTER);
        phone = getIntent().getStringExtra("phone");
        verifyCode = getIntent().getStringExtra("verifyCode");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        setNavBtn(R.drawable.back, 0);
        setTitle(getString(R.string.password_title));
        initView();
    }

    private void initView() {
        nameEditText = (CusEditText) findViewById(R.id.password_name);
        pwdEditText = (CusEditText) findViewById(R.id.password);
        repwdEditText = (CusEditText) findViewById(R.id.password_confirm);
        nameEditText.setText(getString(R.string.password_name));
        pwdEditText.setText(getString(R.string.password_pwd));
        repwdEditText.setText(getString(R.string.password_repwd));
        nameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        pwdEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        repwdEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        finish = (Button) findViewById(R.id.finish);

        if (flag == AppConstant.RESETPASSWORD)
            nameEditText.setVisibility(View.GONE);

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameEditText.hideHint();
                pwdEditText.hideHint();
                repwdEditText.hideHint();
                String name = nameEditText.getText();
                String pwd = pwdEditText.getText();
                String rePwd = repwdEditText.getText();
                if (flag == AppConstant.REGISTER && name.length() == 0) {
                    nameEditText.showHint();
                    nameEditText.setHintText(getString(R.string.password_name_hint));
                } else if (pwd.length() == 0) {
                    pwdEditText.showHint();
                    pwdEditText.setHintText(getString(R.string.password_pwd_hint));
                } else if (!rePwd.equals(pwd)) {
                    repwdEditText.showHint();
                    repwdEditText.setHintText(getString(R.string.password_repwd_hint));
                } else {
                    if (flag == AppConstant.REGISTER)
                        register(name, pwd);
                    else
                        resetPassword(pwd);
                }
            }
        });
    }

    private void register(String name, String password) {
        if (StringUtils.isPhoneNumber(phone))
            AC.accountMgr().register("", phone, password, name, verifyCode, new PayloadCallback<ACUserInfo>() {
                @Override
                public void success(ACUserInfo userInfo) {
                    Pop.popToast(PasswordActivity.this, getString(R.string.password_register_success));
                    MainApplication.getInstance().UserLogin(new UserInfo(userInfo.getUserId(), userInfo.getName()));
                    Intent intent = new Intent(PasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

                @Override
                public void error(ACException e) {
                    repwdEditText.showHint();
                    if (e.getErrorCode() == 404 || e.getErrorCode() == ACException.INTERNET_ERROR)
                        repwdEditText.setHintText(getString(R.string.internet_error));
                    else
                        repwdEditText.setHintText(e.getMessage());
                }
            });
        else {
            AC.accountMgr().register(phone, "", password, name, verifyCode, new PayloadCallback<ACUserInfo>() {
                @Override
                public void success(ACUserInfo userInfo) {
                    Pop.popToast(PasswordActivity.this, getString(R.string.password_register_success));
                    MainApplication.getInstance().UserLogin(new UserInfo(userInfo.getUserId(), userInfo.getName()));
                    Intent intent = new Intent(PasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

                @Override
                public void error(ACException e) {
                    repwdEditText.showHint();
                    if (e.getErrorCode() == 404 || e.getErrorCode() == ACException.INTERNET_ERROR)
                        repwdEditText.setHintText(getString(R.string.internet_error));
                    else
                        repwdEditText.setHintText(e.getMessage());
                }
            });
        }
    }

    private void resetPassword(String password) {
        AC.accountMgr().resetPassword(phone, password, verifyCode, new PayloadCallback<ACUserInfo>() {
            @Override
            public void success(ACUserInfo userInfo) {
                Pop.popToast(PasswordActivity.this, getString(R.string.password_reset_success));
                MainApplication.getInstance().UserLogin(new UserInfo(userInfo.getUserId(), userInfo.getName()));
                Intent intent = new Intent(PasswordActivity.this, LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void error(ACException e) {
                repwdEditText.showHint();
                if (e.getErrorCode() == 404 || e.getErrorCode() == ACException.INTERNET_ERROR)
                    repwdEditText.setHintText(getString(R.string.internet_error));
                else
                    repwdEditText.setHintText(e.getMessage());
            }
        });
    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        if (component == TitleBar.LEFT) {
            Intent intent = new Intent(PasswordActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
