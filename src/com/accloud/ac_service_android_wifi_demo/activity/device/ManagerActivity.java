package com.accloud.ac_service_android_wifi_demo.activity.device;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.accloud.ac_service_android_wifi_demo.R;
import com.accloud.ac_service_android_wifi_demo.activity.BaseActivity;
import com.accloud.ac_service_android_wifi_demo.activity.personal.AuthorizedMemberActivity;
import com.accloud.ac_service_android_wifi_demo.activity.personal.InviteUserActivity;
import com.accloud.ac_service_android_wifi_demo.application.MainApplication;
import com.accloud.ac_service_android_wifi_demo.config.Config;
import com.accloud.ac_service_android_wifi_demo.utils.CustomDialog;
import com.accloud.ac_service_android_wifi_demo.utils.Pop;
import com.accloud.ac_service_android_wifi_demo.utils.ViewHolder;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACBindMgr;
import com.accloud.service.ACException;
import com.accloud.service.ACUserDevice;
import zxing.ShareActivity;

import java.util.ArrayList;
import java.util.List;

public class ManagerActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListView listView;
    private ACBindMgr bindMgr;
    private ManagerAdapter adapter;
    private LinearLayout noDevice;
    private Button addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        setTitle(getString(R.string.device_manager_title));
        setNavBtn(R.drawable.back, 0);
        listView = (ListView) findViewById(R.id.manager_list);
        noDevice = (LinearLayout) findViewById(R.id.manager_ll);
        addBtn = (Button) findViewById(R.id.manager_btn);

        bindMgr = AC.bindMgr();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pop.popBindDialog(ManagerActivity.this, findViewById(R.id.manager_list));
            }
        });
        adapter = new ManagerAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDeviceList();
    }

    public void getDeviceList() {
        bindMgr.listDevices(new PayloadCallback<List<ACUserDevice>>() {
            @Override
            public void success(List<ACUserDevice> deviceList) {
                if (deviceList.size() > 0) {
                    noDevice.setVisibility(View.GONE);
                } else {
                    noDevice.setVisibility(View.VISIBLE);
                }
                adapter.userDevices = deviceList;
                adapter.notifyDataSetChanged();
            }

            @Override
            public void error(ACException e) {
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View v, int i, long l) {
        final ACUserDevice userDevice = adapter.userDevices.get(i);
        final PopupWindow popupWindow = Pop.popWindow(ManagerActivity.this, findViewById(R.id.manager_list));
        View view = popupWindow.getContentView();
        view.findViewById(R.id.popup_add).setVisibility(View.GONE);
        TextView share = (TextView) view.findViewById(R.id.popup_share);
        TextView delete = (TextView) view.findViewById(R.id.popup_delete);
        TextView invite = (TextView) view.findViewById(R.id.popup_invite_user);
        TextView modify = (TextView) view.findViewById(R.id.popup_modify_device);
        TextView sharedUser = (TextView) view.findViewById(R.id.popup_shared_user);
        //分享设备
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                if (userDevice.getOwner() != MainApplication.getInstance().mUser.getUserId()) {
                    showToast(getString(R.string.share_with_owner_only));
                } else {
                    AC.bindMgr().getShareCode(Config.SUBMAJORDOMAIN, userDevice.getDeviceId(), 5 * 60, new PayloadCallback<String>() {
                        @Override
                        public void success(String shareCode) {
                            Intent intent = new Intent(ManagerActivity.this, ShareActivity.class);
                            intent.putExtra("shareCode", shareCode);
                            intent.putExtra("deviceId", userDevice.getDeviceId());
                            startActivity(intent);
                        }

                        @Override
                        public void error(ACException e) {
                            Pop.popToast(ManagerActivity.this, e.getErrorCode() + "-->" + e.getMessage());
                        }
                    });
                }
            }
        });
        //解绑设备
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                final CustomDialog dialog = Pop.popDialog(ManagerActivity.this, R.layout.dialog_unbind_device, R.style.customDialog);
                LinearLayout linearLayout = dialog.findView(R.id.dialog_unbind_device);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
                params.width = getResources().getDisplayMetrics().widthPixels - 100;
                linearLayout.setLayoutParams(params);
                TextView cancel = dialog.findView(R.id.unbind_cancel);
                TextView sure = dialog.findView(R.id.unbind_sure);
                cancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        unbindDevice(userDevice.getDeviceId());//解绑设备
                    }
                });
            }
        });
        //邀请用户绑定设备
        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                if (userDevice.getOwner() != MainApplication.getInstance().mUser.getUserId()) {
                    showToast(getString(R.string.share_with_owner_only));
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("deviceId", userDevice.getDeviceId());
                    intent.setClass(ManagerActivity.this, InviteUserActivity.class);
                    startActivity(intent);
                }
            }
        });
        //修改设备名字
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                final CustomDialog dialog = Pop.popDialog(ManagerActivity.this, R.layout.dialog_set_name, R.style.customDialog);
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
                            showToast("设备名不能为空");
                        } else {
                            AC.bindMgr().changeName(Config.SUBMAJORDOMAIN, userDevice.getDeviceId(), name, new VoidCallback() {
                                @Override
                                public void success() {
                                    showToast("修改设备名成功");
                                    dialog.dismiss();
                                    userDevice.setName(name);
                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void error(ACException e) {
                                    showToast(e.getErrorCode() + "-->" + e.getMessage());
                                }
                            });
                        }
                    }
                });
            }
        });
        //被分享成员
        sharedUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                Intent intent = new Intent();
                intent.setClass(ManagerActivity.this, AuthorizedMemberActivity.class);
                intent.putExtra("isOwner", MainApplication.getInstance().mUser.getUserId() == userDevice.getOwner());
                intent.putExtra("deviceId", userDevice.getDeviceId());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        finish();
    }

    private class ManagerAdapter extends BaseAdapter {
        private Context context;
        private List<ACUserDevice> userDevices;
        int[] icons = new int[]{R.drawable.light1, R.drawable.light2, R.drawable.light3, R.drawable.light4};

        public ManagerAdapter(Context context) {
            this.context = context;
            this.userDevices = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return userDevices.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            view = LayoutInflater.from(context).inflate(R.layout.adapter_manager_device_list, null);
            TextView deviceName = ViewHolder.get(view, R.id.adapter_manager_device_name);
            ImageView img = ViewHolder.get(view, R.id.adapter_manager_icon);

            deviceName.setText(userDevices.get(position).getName());
            img.setBackgroundResource(icons[position % 4]);
            return view;
        }
    }

    //管理员解绑设备
    public void unbindDevice(long deviceId) {
        AC.bindMgr().unbindDevice(Config.SUBMAJORDOMAIN, deviceId, new VoidCallback() {
            @Override
            public void success() {
                showToast(ManagerActivity.this.getString(R.string.unbind_success));
                getDeviceList();
            }

            @Override
            public void error(ACException e) {
                Pop.popToast(ManagerActivity.this, e.getErrorCode() + "-->" + e.getMessage());
            }
        });
    }
}
