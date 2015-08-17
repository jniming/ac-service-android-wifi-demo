package com.accloud.ac_service_android_wifi_demo.activity.personal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.accloud.ac_service_android_wifi_demo.R;
import com.accloud.ac_service_android_wifi_demo.activity.BaseActivity;
import com.accloud.ac_service_android_wifi_demo.adapter.MemberAdapter;
import com.accloud.ac_service_android_wifi_demo.config.Config;
import com.accloud.ac_service_android_wifi_demo.config.ConstantCache;
import com.accloud.ac_service_android_wifi_demo.listerner.OnCustomDialogListener;
import com.accloud.ac_service_android_wifi_demo.utils.CustomDialog;
import com.accloud.ac_service_android_wifi_demo.utils.Pop;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACDeviceUser;
import com.accloud.service.ACException;
import com.accloud.utils.LogUtil;

import java.util.List;

/**
 * Created by Administrator on 2015/4/27.
 */
public class AuthorizedMemberActivity extends BaseActivity {

    private TextView ownerName;
    private TextView ownerUnbind;
    private ListView memberList;
    private RelativeLayout ownerRl;
    private MemberAdapter adapter;

    private long deviceId;
    private boolean isOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorize_member);
        deviceId = getIntent().getLongExtra("deviceId", 0);
        isOwner = getIntent().getBooleanExtra("isOwner", true);
        setTitle(getString(R.string.authorized_member_title));
        setNavBtn(R.drawable.back, R.drawable.add);
        initView();
        listUsers();
    }

    private void initView() {
        ownerRl = (RelativeLayout) findViewById(R.id.member_owner_rl);
        ownerName = (TextView) findViewById(R.id.member_owner_name);
        ownerUnbind = (TextView) findViewById(R.id.member_owner_unbind);
        memberList = (ListView) findViewById(R.id.member_people_list);
        adapter = new MemberAdapter(this, isOwner);
        memberList.setAdapter(adapter);
        ownerUnbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(AuthorizedMemberActivity.this).setTitle(getString(R.string.unbind)).setMessage(getString(R.string.authorized_member_unbind_hint)).setNegativeButton(getString(R.string.no), null).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int m) {
                        unbindDevice();
                    }
                }).show();
            }
        });
    }

    public void listUsers() {
        AC.bindMgr().listUsers(Config.SUBMAJORDOMAIN, deviceId, new PayloadCallback<List<ACDeviceUser>>() {
            @Override
            public void success(List<ACDeviceUser> acBindings) {
                if (acBindings == null) {
                } else {
                    for (int i = 0; i < acBindings.size(); i++) {
                        ACDeviceUser deviceUser = acBindings.get(i);
                        LogUtil.d("test", deviceUser.toString());
                        if (deviceUser.getUserType() == 1) {
                            ownerRl.setVisibility(View.VISIBLE);
                            if (!isOwner)
                                ownerUnbind.setVisibility(View.INVISIBLE);
                            ownerName.setText(deviceUser.getName());
                            acBindings.remove(i);
                        }
                    }
                    adapter.bindingList = acBindings;
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void error(ACException e) {
                Pop.popToast(AuthorizedMemberActivity.this, e.getErrorCode() + "-->" + e.getMessage());
            }
        });
    }

    public void unbindDevice() {
        AC.bindMgr().unbindDevice(Config.SUBMAJORDOMAIN, deviceId, new VoidCallback() {
            @Override
            public void success() {
                Pop.popToast(AuthorizedMemberActivity.this, getString(R.string.unbind_success));
                AuthorizedMemberActivity.this.finish();
            }

            @Override
            public void error(ACException e) {
                Pop.popToast(AuthorizedMemberActivity.this, e.getErrorCode() + "-->" + e.getMessage());
            }
        });
    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        switch (component) {
            case LEFT:
                finish();
                break;
            case RIGHT:
                showDialog();
                break;
        }
    }

    private void showDialog() {
        final CustomDialog dialog = Pop.popDialog(AuthorizedMemberActivity.this, R.layout.dialog_set_name, R.style.customDialog);
        LinearLayout linearLayout = dialog.findView(R.id.dialog_set_name);
        TextView textView = dialog.findView(R.id.dialog_text_hint);
        textView.setText(getString(R.string.authorized_member_add));
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels - 100;
        linearLayout.setLayoutParams(params);
        final EditText editName = dialog.findView(R.id.set_device_name);
        Button cancel = dialog.findView(R.id.set_cancel);
        Button sure = dialog.findView(R.id.set_sure);
        dialog.setOnCustomDialogListener(new OnCustomDialogListener() {
            @Override
            public void onFinish() {
                listUsers();
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String deviceName = editName.getText().toString();
                if (deviceName.length() > 0) {
                    AC.bindMgr().bindDeviceWithUser(Config.SUBMAJORDOMAIN, deviceId, deviceName, new VoidCallback() {
                        @Override
                        public void success() {
                            dialog.dismiss();
                        }

                        @Override
                        public void error(ACException e) {
                            Pop.popToast(AuthorizedMemberActivity.this, e.getErrorCode() + "-->" + e.getMessage());
                        }
                    });
                }
            }
        });
    }

}
