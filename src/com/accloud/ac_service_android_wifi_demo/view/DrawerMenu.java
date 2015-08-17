package com.accloud.ac_service_android_wifi_demo.view;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import android.widget.TextView;
import com.accloud.ac_service_android_wifi_demo.R;
import com.accloud.ac_service_android_wifi_demo.activity.device.FirmwareUpdateActivity;
import com.accloud.ac_service_android_wifi_demo.activity.device.ManagerActivity;
import com.accloud.ac_service_android_wifi_demo.activity.personal.AboutActivity;
import com.accloud.ac_service_android_wifi_demo.activity.personal.BindAccountActivity;
import com.accloud.ac_service_android_wifi_demo.activity.personal.PersonalActivity;
import com.accloud.ac_service_android_wifi_demo.adapter.DrawerMenuAdapter;
import com.accloud.ac_service_android_wifi_demo.application.MainApplication;
import com.accloud.ac_service_android_wifi_demo.utils.Pop;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

/**
 * Created by Xuri on 2014/11/7.
 */
public class DrawerMenu {

    private Activity activity;
    public SlidingMenu slidingMenu;
    DrawerMenuAdapter adapter;
    public TextView nick_name;

    public DrawerMenu(Activity activity) {
        this.activity = activity;
        initMenuSettings();
        initMenuView();
        activity.findViewById(R.id.header_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (slidingMenu.isMenuShowing()) {
                    slidingMenu.showContent();
                } else {
                    slidingMenu.showMenu();
                }
            }
        });
    }

    public void initMenuSettings() {
        slidingMenu = new SlidingMenu(activity);
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        slidingMenu.setShadowDrawable(R.drawable.drawer_shadow);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.setFadeEnabled(true);
        slidingMenu.attachToActivity(activity, SlidingMenu.SLIDING_CONTENT);
        slidingMenu.setMenu(R.layout.view_slide_menu);
    }

    public void initMenuView() {
        ListView menu = (ListView) slidingMenu.findViewById(R.id.slide_menu);
        RelativeLayout rl = (RelativeLayout) slidingMenu.findViewById(R.id.slide_rl);
        nick_name = (TextView) slidingMenu.findViewById(R.id.user_name);
        adapter = new DrawerMenuAdapter(activity);
        menu.setAdapter(adapter);
        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                switch (i) {
                    case 0:
                        intent.setClass(activity, ManagerActivity.class);
                        activity.startActivity(intent);
                        break;
                    case 1:
                        intent.setClass(activity, FirmwareUpdateActivity.class);
                        activity.startActivity(intent);
                        break;
                    case 3:
                        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
                            @Override
                            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                                switch (updateStatus) {
                                    case UpdateStatus.Yes: // has update
                                        UmengUpdateAgent.showUpdateDialog(activity, updateInfo);
                                        break;
                                    case UpdateStatus.No: // has no update
                                        Pop.popToast(activity, activity.getString(R.string.menu_update_already));
                                        break;
                                    case UpdateStatus.NoneWifi: // none wifi
                                        Pop.popToast(activity, activity.getString(R.string.menu_update_no_net));
                                        break;
                                    case UpdateStatus.Timeout: // time out
                                        Pop.popToast(activity, activity.getString(R.string.timeout));
                                        break;
                                }
                            }
                        });
                        UmengUpdateAgent.forceUpdate(activity);
                        break;
                    case 2:
                        intent.setClass(activity, BindAccountActivity.class);
                        activity.startActivity(intent);
                        break;
                    case 4:
                        intent.setClass(activity, AboutActivity.class);
                        activity.startActivity(intent);
                        break;
                }
                slidingMenu.showContent();
            }
        });
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingMenu.showContent();
                Intent intent = new Intent(activity, PersonalActivity.class);
                activity.startActivity(intent);
            }
        });
    }

    public void updateNickName() {
        String nickName = MainApplication.getInstance().mUser.getNickName();
        if (nickName == null || nickName.length() == 0)
            nick_name.setText(activity.getString(R.string.set_nick_name));
        else
            nick_name.setText(nickName);
    }

    public void updateIcon() {
        adapter.notifyDataSetChanged();
    }

}
