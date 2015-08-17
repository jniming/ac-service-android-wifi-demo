package com.accloud.ac_service_android_wifi_demo.model;

import com.accloud.service.ACOTAUpgradeInfo;
import com.accloud.service.ACUserDevice;

/**
 * Created by Administrator on 2015/7/18.
 */
public class DeviceUpdateInfo {
    private ACUserDevice userDevice;
    private ACOTAUpgradeInfo upgradeInfo;

    public DeviceUpdateInfo(ACUserDevice userDevice, ACOTAUpgradeInfo upgradeInfo) {
        this.userDevice = userDevice;
        this.upgradeInfo = upgradeInfo;
    }

    public ACUserDevice getUserDevice() {
        return userDevice;
    }

    public void setUserDevice(ACUserDevice userDevice) {
        this.userDevice = userDevice;
    }

    public ACOTAUpgradeInfo getUpgradeInfo() {
        return upgradeInfo;
    }

    public void setUpgradeInfo(ACOTAUpgradeInfo upgradeInfo) {
        this.upgradeInfo = upgradeInfo;
    }
}
