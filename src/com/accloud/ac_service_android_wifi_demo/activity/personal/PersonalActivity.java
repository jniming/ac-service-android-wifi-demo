package com.accloud.ac_service_android_wifi_demo.activity.personal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.accloud.ac_service_android_wifi_demo.R;
import com.accloud.ac_service_android_wifi_demo.activity.BaseActivity;
import com.accloud.ac_service_android_wifi_demo.activity.login.LoginActivity;
import com.accloud.ac_service_android_wifi_demo.application.MainApplication;
import com.accloud.ac_service_android_wifi_demo.utils.CustomDialog;
import com.accloud.ac_service_android_wifi_demo.utils.Pop;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACException;
import com.accloud.utils.PreferencesUtils;

/**
 * Created by Administrator on 2015/4/18.
 */
public class PersonalActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout changeName;
    private RelativeLayout changePwd;
    private TextView Name;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        setTitle(getString(R.string.personal_title));
        setNavBtn(R.drawable.back, 0);

        initView();
    }

    private void initView() {
        changeName = (RelativeLayout) findViewById(R.id.personal_change_name);
        changePwd = (RelativeLayout) findViewById(R.id.personal_change_pwd);
        Name = (TextView) findViewById(R.id.personal_name);
        logout = (Button) findViewById(R.id.personal_logout);
        Name.setText(MainApplication.getInstance().mUser.getNickName());
        changeName.setOnClickListener(this);
        changePwd.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.personal_change_name:
                showDialog();
                break;
            case R.id.personal_change_pwd:
                intent = new Intent(PersonalActivity.this, ChangePwdActivity.class);
                startActivity(intent);
                break;
            case R.id.personal_logout:
                AC.accountMgr().logout();
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    public void showDialog() {
        final CustomDialog dialog = Pop.popDialog(this, R.layout.dialog_set_name, R.style.customDialog);
        LinearLayout linearLayout = dialog.findView(R.id.dialog_set_name);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels - 100;
        linearLayout.setLayoutParams(params);
        TextView textView = dialog.findView(R.id.dialog_text_hint);
        final EditText editName = dialog.findView(R.id.set_device_name);
        Button cancel = dialog.findView(R.id.set_cancel);
        Button sure = dialog.findView(R.id.set_sure);
        textView.setText(getString(R.string.personal_set_nickname));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = editName.getText().toString();
                if (name.length() <= 0) {
                    Pop.popToast(PersonalActivity.this, getString(R.string.personal_set_nickname_hint));
                } else {
                    AC.accountMgr().changeNickName(name, new VoidCallback() {
                        @Override
                        public void success() {
                            Pop.popToast(PersonalActivity.this, getString(R.string.personal_change_nickname_success));
                            dialog.dismiss();
                            Name.setText(name);
                            PreferencesUtils.putString(PersonalActivity.this, "name", name);
                            MainApplication.getInstance().mUser.setNickName(name);
                        }

                        @Override
                        public void error(ACException e) {
                            Pop.popToast(PersonalActivity.this, e.getMessage());
                        }
                    });
                }
            }
        });

    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        finish();
    }
}
