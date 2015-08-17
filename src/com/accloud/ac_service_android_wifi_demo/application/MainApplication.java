package com.accloud.ac_service_android_wifi_demo.application;

import android.app.Application;
import com.accloud.ac_service_android_wifi_demo.config.Config;
import com.accloud.ac_service_android_wifi_demo.config.ConstantCache;
import com.accloud.ac_service_android_wifi_demo.model.DeviceUpdateInfo;
import com.accloud.ac_service_android_wifi_demo.model.UserInfo;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACOTAUpgradeInfo;
import com.accloud.service.ACUserDevice;
import com.accloud.utils.PreferencesUtils;
import com.umeng.update.UpdateConfig;

import java.util.List;

/**
 * Created by Xuri on 2015/1/17.
 */
public class MainApplication extends Application {
    private static MainApplication mInstance;
    public static UserInfo mUser;

    @Override
    public void onCreate() {
        super.onCreate();
        AC.init(this, Config.MAJORDOAMIN, Config.MAJORDOMAINID, AC.TEST_MODE);
        mInstance = this;
        initUserInfo();
        getUpdateInfo();
        UpdateConfig.setDebug(true);
    }

    public void initUserInfo() {
        if (AC.accountMgr().isLogin()) {
            long uid = PreferencesUtils.getLong(MainApplication.getInstance(), "uid", 0);
            String name = PreferencesUtils.getString(MainApplication.getInstance(), "name");
            int loginType = PreferencesUtils.getInt(MainApplication.getInstance(), "loginType", 0);
            mUser = new UserInfo(loginType, uid, name);
        }
    }

    public static void UserLogin(UserInfo user) {
        mUser = user;
        PreferencesUtils.putLong(MainApplication.getInstance(), "uid", mUser.getUserId());
        PreferencesUtils.putString(MainApplication.getInstance(), "name", mUser.getNickName());
        PreferencesUtils.putInt(MainApplication.getInstance(), "loginType", mUser.getLoginType());
    }

    public void getUpdateInfo() {
        AC.bindMgr().listDevices(new PayloadCallback<List<ACUserDevice>>() {
            @Override
            public void success(final List<ACUserDevice> deviceList) {
                for (int i = 0; i < deviceList.size(); i++) {
                    final ACUserDevice userDevice = deviceList.get(i);
                    AC.OTAMgr().checkUpdate(Config.SUBMAJORDOMAIN, userDevice.getDeviceId(), new PayloadCallback<ACOTAUpgradeInfo>() {
                        @Override
                        public void success(ACOTAUpgradeInfo upgradeInfo) {
                            if (!upgradeInfo.getOldVersion().equals(upgradeInfo.getNewVersion())) {
                                ConstantCache.hasUpdate = true;
                                ConstantCache.deviceUpdateInfo.add(new DeviceUpdateInfo(userDevice, upgradeInfo));
                            }
                        }

                        @Override
                        public void error(ACException e) {

                        }
                    });
                }
            }

            @Override
            public void error(ACException e) {

            }
        });
    }

    public static MainApplication getInstance() {
        return mInstance;
    }
}
