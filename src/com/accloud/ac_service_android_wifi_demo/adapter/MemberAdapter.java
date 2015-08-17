package com.accloud.ac_service_android_wifi_demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.accloud.ac_service_android_wifi_demo.R;
import com.accloud.ac_service_android_wifi_demo.config.Config;
import com.accloud.ac_service_android_wifi_demo.utils.CustomDialog;
import com.accloud.ac_service_android_wifi_demo.utils.Pop;
import com.accloud.ac_service_android_wifi_demo.utils.ViewHolder;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACDeviceUser;
import com.accloud.service.ACException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/27.
 */
public class MemberAdapter extends BaseAdapter {

    private Context context;
    public List<ACDeviceUser> bindingList;
    private Boolean flag = true;
    private Boolean isOwner;
    private CustomDialog dialog;

    public MemberAdapter(Context context, boolean isOwner) {
        this.context = context;
       // bindingList = new ArrayList<>();
        bindingList=new ArrayList<ACDeviceUser>();

        this.isOwner = isOwner;
    }

    @Override
    public int getCount() {
        return bindingList.size();
    }

    @Override
    public Object getItem(int i) {
        return bindingList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.adapter_member, null);
        TextView name = ViewHolder.get(view, R.id.adapter_member_name);
        final ToggleButton toggleButton = ViewHolder.get(view, R.id.adapter_member_toggle_button);
        final ACDeviceUser binding = bindingList.get(i);
        name.setText(binding.getName());
        if (!isOwner)
            toggleButton.setVisibility(View.INVISIBLE);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (flag && b) {
                    showProgressDialog();
                    AC.bindMgr().bindDeviceWithUser(Config.SUBMAJORDOMAIN, binding.getDeviceId(), binding.getPhone(), new VoidCallback() {
                        @Override
                        public void success() {
                            dialog.dismiss();
                        }

                        @Override
                        public void error(ACException e) {
                            dialog.dismiss();
                            flag = false;
                            toggleButton.setChecked(false);
                        }
                    });
                } else if (flag) {
                    showProgressDialog();
                    AC.bindMgr().unbindDeviceWithUser(Config.SUBMAJORDOMAIN, binding.getUserId(), binding.getDeviceId(), new VoidCallback() {
                        @Override
                        public void success() {
                            dialog.dismiss();
                        }

                        @Override
                        public void error(ACException e) {
                            dialog.dismiss();
                            flag = false;
                            toggleButton.setChecked(true);
                        }
                    });
                } else
                    flag = true;
            }
        });
        return view;
    }

    private void showProgressDialog() {
        if (dialog == null)
            dialog = Pop.popDialog(context, R.layout.window_layout, R.style.customDialog);
        else
            dialog.show();
    }
}
