package com.accloud.ac_service_android_wifi_demo.activity.device;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.accloud.ac_service_android_wifi_demo.R;
import com.accloud.ac_service_android_wifi_demo.activity.BaseActivity;
import com.accloud.ac_service_android_wifi_demo.config.Config;
import com.accloud.ac_service_android_wifi_demo.config.ConstantCache;
import com.accloud.ac_service_android_wifi_demo.model.DeviceUpdateInfo;
import com.accloud.ac_service_android_wifi_demo.utils.CustomDialog;
import com.accloud.ac_service_android_wifi_demo.utils.Pop;
import com.accloud.ac_service_android_wifi_demo.utils.ViewHolder;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACException;

public class FirmwareUpdateActivity extends BaseActivity {
    private ListView listView;
    private Button updateAllBtn;
    private FirmwareUpdateAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmware_update);
        setTitle("固件升级");
        setNavBtn(R.drawable.back, 0);
        listView = (ListView) findViewById(R.id.firmware_update_listView);
        updateAllBtn = (Button) findViewById(R.id.firmware_update_all_btn);
        adapter = new FirmwareUpdateAdapter(this);
        listView.setAdapter(adapter);
        updateAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ConstantCache.deviceUpdateInfo.size() > 0) {
                    final CustomDialog dialog = Pop.popDialog(FirmwareUpdateActivity.this, R.layout.dialog_update_device, R.style.customDialog);
                    LinearLayout linearLayout = dialog.findView(R.id.dialog_update_device);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
                    params.width = getResources().getDisplayMetrics().widthPixels - 100;
                    linearLayout.setLayoutParams(params);
                    TextView title = dialog.findView(R.id.dialog_update_title);
                    TextView log = dialog.findView(R.id.dialog_update_log);
                    TextView cancel = dialog.findView(R.id.dialog_update_cancel);
                    TextView sure = dialog.findView(R.id.dialog_update_sure);
                    title.setText("更新新版本");
                    log.setText("是否全部升级");
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
                            for (final DeviceUpdateInfo info : ConstantCache.deviceUpdateInfo) {
                                AC.OTAMgr().confirmUpdate(Config.SUBMAJORDOMAIN, info.getUserDevice().deviceId, info.getUpgradeInfo().getNewVersion(), new VoidCallback() {
                                    @Override
                                    public void success() {
                                        ConstantCache.deviceUpdateInfo.remove(info);
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void error(ACException e) {

                                    }
                                });
                            }
                        }
                    });
                } else {
                    showToast("没有可升级固件");
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

    private class FirmwareUpdateAdapter extends BaseAdapter {
        private Context context;

        public FirmwareUpdateAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return ConstantCache.deviceUpdateInfo.size();
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
        public View getView(final int i, View view, ViewGroup parent) {
            view = LayoutInflater.from(context).inflate(R.layout.adapter_firmware_update, null);
            TextView deviceName = ViewHolder.get(view, R.id.adapter_firmware_device_name);
            Button updateBtn = ViewHolder.get(view, R.id.adapter_firmware_update_btn);
            final DeviceUpdateInfo updateInfo = ConstantCache.deviceUpdateInfo.get(i);
            deviceName.setText(updateInfo.getUserDevice().getName());
            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final CustomDialog dialog = Pop.popDialog(context, R.layout.dialog_update_device, R.style.customDialog);
                    LinearLayout linearLayout = dialog.findView(R.id.dialog_update_device);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
                    params.width = getResources().getDisplayMetrics().widthPixels - 100;
                    linearLayout.setLayoutParams(params);
                    TextView title = dialog.findView(R.id.dialog_update_title);
                    TextView log = dialog.findView(R.id.dialog_update_log);
                    TextView cancel = dialog.findView(R.id.dialog_update_cancel);
                    TextView sure = dialog.findView(R.id.dialog_update_sure);
                    title.setText("是否更新新版本：" + updateInfo.getUpgradeInfo().getNewVersion());
                    log.setText(updateInfo.getUpgradeInfo().getUpgradeLog());
                    cancel.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    sure.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AC.OTAMgr().confirmUpdate(Config.SUBMAJORDOMAIN, updateInfo.getUserDevice().deviceId, updateInfo.getUpgradeInfo().getNewVersion(), new VoidCallback() {
                                @Override
                                public void success() {
                                    ConstantCache.deviceUpdateInfo.remove(updateInfo);
                                    notifyDataSetChanged();
                                    Pop.popToast(context, "更新成功");
                                    dialog.dismiss();
                                }

                                @Override
                                public void error(ACException e) {
                                    Pop.popToast(context, e.getErrorCode() + "-->" + e.getMessage());
                                }
                            });
                        }
                    });
                }
            });
            return view;
        }
    }
}
