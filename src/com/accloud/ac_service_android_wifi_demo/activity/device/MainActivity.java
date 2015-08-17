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
import com.accloud.ac_service_android_wifi_demo.activity.add.DeviceReadyActivity;
import com.accloud.ac_service_android_wifi_demo.application.MainApplication;
import com.accloud.ac_service_android_wifi_demo.controller.Light;
import com.accloud.ac_service_android_wifi_demo.utils.Pop;
import com.accloud.ac_service_android_wifi_demo.utils.ViewHolder;
import com.accloud.ac_service_android_wifi_demo.view.DrawerMenu;
import com.accloud.ac_service_android_wifi_demo.view.XListView;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.ACNetworkChangeReceiver;
import com.accloud.cloudservice.NetEventHandler;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACBindMgr;
import com.accloud.service.ACDeviceFind;
import com.accloud.service.ACException;
import com.accloud.service.ACUserDevice;
import zxing.CaptureActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private ACBindMgr bindMgr;
    private XListView listView;
    private DeviceAdapter adapter;
    private LinearLayout noDevice;
    private Button addBtn;
    private DrawerMenu slideMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.main_title));
        setNavBtn(R.drawable.personal, R.drawable.add);
        slideMenu = initSlideMenu();

        listView = (XListView) findViewById(R.id.device_list);
        noDevice = (LinearLayout) findViewById(R.id.main_ll);
        addBtn = (Button) findViewById(R.id.main_btn);

        //获取设备管理器
        bindMgr = AC.bindMgr();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pop.popBindDialog(MainActivity.this, findViewById(R.id.device_list));
            }
        });

        adapter = new DeviceAdapter(this);
        listView.setAdapter(adapter);
        listView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                refreshDeviceStatus();
            }
        });
        ACNetworkChangeReceiver.addEventHandler(new NetEventHandler() {
            @Override
            public void onNetChange() {
                getDeviceList();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!AC.accountMgr().isLogin())
            finish();
        slideMenu.updateNickName();
        slideMenu.updateIcon();
        getDeviceList();
    }

    //获取设备列表
    public void getDeviceList() {
        bindMgr.listDevicesWithStatus(new PayloadCallback<List<ACUserDevice>>() {
            @Override
            public void success(List<ACUserDevice> deviceList) {
                if (deviceList.size() > 0) {
                    listView.setPullRefreshEnable(true);
                    noDevice.setVisibility(View.GONE);
                } else {
                    listView.setPullRefreshEnable(false);
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
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        switch (component) {
            case RIGHT:
                Pop.popBindDialog(this, findViewById(R.id.device_list));
                break;
        }
    }

    private class DeviceAdapter extends BaseAdapter {
        private Context context;
        private List<ACUserDevice> userDevices;
        private Light light;
        int[] icons = new int[]{R.drawable.light1, R.drawable.light2, R.drawable.light3, R.drawable.light4};

        public DeviceAdapter(Context context) {
            this.context = context;
            userDevices = new ArrayList<ACUserDevice>();
            light = new Light();
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
        public View getView(final int position, View view, ViewGroup parent) {
            view = LayoutInflater.from(context).inflate(R.layout.adapter_list_device, null);
            RelativeLayout rr = ViewHolder.get(view, R.id.adapter_main_list_device);
            TextView deviceName = ViewHolder.get(view, R.id.adapter_main_device_name);
            TextView deviceStatus = ViewHolder.get(view, R.id.adapter_main_device_status);
            final ToggleButton toggleButton = ViewHolder.get(view, R.id.adapter_main_toggle_button);
            ImageView img = ViewHolder.get(view, R.id.adapter_main_icon);

            final ACUserDevice device = userDevices.get(position);
            switch (device.getStatus()) {
                case ACUserDevice.OFFLINE:
                    deviceStatus.setText("(不在线)");
                    break;
                case ACUserDevice.NETWORK_ONLINE:
                    deviceStatus.setText("(云端在线)");
                    break;
                case ACUserDevice.LOCAL_ONLINE:
                    deviceStatus.setText("(局域网在线)");
                    break;
                case ACUserDevice.BOTH_ONLINE:
                    deviceStatus.setText("(云端与局域网在线)");
                    break;
            }
            deviceName.setText(device.getName());
            img.setBackgroundResource(icons[position % 4]);
            rr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (toggleButton.isChecked())
                        toggleButton.setChecked(false);
                    else
                        toggleButton.setChecked(true);
                }
            });
            toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        light.openLight(device.getDeviceId());
                    } else {
                        light.closeLight(device.getDeviceId());
                    }
                }
            });
            return view;
        }
    }

    public void refreshDeviceStatus() {
        AC.findLocalDevice(1000, new PayloadCallback<List<ACDeviceFind>>() {
            @Override
            public void success(List<ACDeviceFind> acDeviceFinds) {
                getDeviceList();
                listView.stopRefresh();
            }

            @Override
            public void error(ACException e) {
                getDeviceList();
                listView.stopRefresh();
            }
        });
    }
}
