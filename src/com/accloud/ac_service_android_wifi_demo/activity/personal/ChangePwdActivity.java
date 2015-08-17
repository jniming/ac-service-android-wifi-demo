package com.accloud.ac_service_android_wifi_demo.activity.personal;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import com.accloud.ac_service_android_wifi_demo.R;
import com.accloud.ac_service_android_wifi_demo.activity.BaseActivity;
import com.accloud.ac_service_android_wifi_demo.utils.Pop;
import com.accloud.ac_service_android_wifi_demo.view.CusEditText;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.VoidCallback;

import com.accloud.service.ACException;

/**
 * Created by Administrator on 2015/4/25.
 */
public class ChangePwdActivity extends BaseActivity {

    private CusEditText oldEditText;
    private CusEditText newEditText;
    private CusEditText reEditText;
    private Button finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        setTitle(getString(R.string.change_pwd_title));
        setNavBtn(R.drawable.back, 0);
        initView();

    }

    private void initView() {
        oldEditText = (CusEditText) findViewById(R.id.change_pwd_old);
        newEditText = (CusEditText) findViewById(R.id.change_pwd_new);
        reEditText = (CusEditText) findViewById(R.id.change_pwd_confirm);
        oldEditText.setText(getString(R.string.change_pwd_old));
        newEditText.setText(getString(R.string.change_pwd_new));
        reEditText.setText(getString(R.string.change_pwd_sure));
        oldEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        reEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        finish = (Button) findViewById(R.id.change_finish);

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oldEditText.hideHint();
                newEditText.hideHint();
                reEditText.hideHint();
                String pwd = oldEditText.getText();
                String newPwd = newEditText.getText();
                String rePwd = reEditText.getText();
                if (!AC.accountMgr().isLogin()) {
                    Pop.popToast(ChangePwdActivity.this, getString(R.string.no_login));
                    ChangePwdActivity.this.finish();
                } else if (pwd.length() == 0) {
                    oldEditText.showHint();
                    oldEditText.setHintText(getString(R.string.change_pwd_old_hint));
                } else if (newPwd.length() == 0) {
                    newEditText.showHint();
                    newEditText.setHintText(getString(R.string.change_pwd_new_hint));
                } else if (!rePwd.equals(newPwd)) {
                    reEditText.showHint();
                    reEditText.setHintText(getString(R.string.change_pwd_sure_hint));
                } else {
                    changePassword(pwd, newPwd);
                }
            }
        });
    }


    public void changePassword(String oldPwd, String newPwd) {
        AC.accountMgr().changePassword(oldPwd, newPwd, new VoidCallback() {
            @Override
            public void success() {
                Pop.popToast(ChangePwdActivity.this, getString(R.string.change_pwd_success));
                ChangePwdActivity.this.finish();
            }

            @Override
            public void error(ACException e) {
                if (e.getErrorCode() == 404|| e.getErrorCode() == ACException.INTERNET_ERROR) {
                    reEditText.showHint();
                    reEditText.setHintText(getString(R.string.internet_error));
                } else if (e.getErrorCode() == 3504) {
                    oldEditText.showHint();
                    oldEditText.setHintText(getString(R.string.change_pwd_old_hint2));
                } else {
                    reEditText.showHint();
                    reEditText.setHintText(e.getMessage());
                }
            }
        });
    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        finish();
    }
}
