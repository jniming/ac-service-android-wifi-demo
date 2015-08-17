package com.accloud.ac_service_android_wifi_demo.controller;

import android.widget.Toast;
import com.accloud.ac_service_android_wifi_demo.application.MainApplication;
import com.accloud.ac_service_android_wifi_demo.config.Config;
import com.accloud.ac_service_android_wifi_demo.utils.Pop;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACDeviceMsg;
import com.accloud.service.ACDeviceMsgMarshaller;
import com.accloud.service.ACException;
import com.accloud.utils.PreferencesUtils;

/**
 * Created by Xuri on 2015/1/24.
 */
public class Light {
    private static final int OPENLIGHT = 0;
    private static final int CLOSELIGHT = 1;

    private String subDomain;

    public Light() {
        //设备消息的序列化/反序列化器，用于解释ACDeviceMsg的内容
        AC.bindMgr().setDeviceMsgMarshaller(new ACDeviceMsgMarshaller() {
            @Override
            public byte[] marshal(ACDeviceMsg msg) throws Exception {
                return (byte[]) msg.getContent();
            }

            @Override
            public ACDeviceMsg unmarshal(int msgCode, byte[] payload) throws Exception {
                return new ACDeviceMsg(msgCode, new String(payload));
            }
        });
        subDomain = PreferencesUtils.getString(MainApplication.getInstance(), "subDomain", Config.SUBMAJORDOMAIN);
    }

    public void openLight(long deviceId) {
        /**
         * 通过云端服务往设备发送命令/消息
         *
         * @param subDomain 子域名，如glass（智能眼镜）
         * @param deviceId  设备逻辑id
         * @param msg       具体的消息内容
         *
         * @return 设备返回的监听回调，返回设备的响应消息
         */
        AC.bindMgr().sendToDeviceWithOption(subDomain, deviceId, getACDeviceMsg(Light.OPENLIGHT), AC.LOCAL_FIRST, new PayloadCallback<ACDeviceMsg>() {
            @Override
            public void success(ACDeviceMsg deviceMsg) {
                Pop.popToast(AC.context, "开灯成功");
            }

            @Override
            public void error(ACException e) {
                Toast.makeText(AC.context, e.getErrorCode() + "-->" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void closeLight(long deviceId) {
        /**
         * 通过云端服务往设备发送命令/消息
         *
         * @param subDomain 子域名，如glass（智能眼镜）
         * @param deviceId  设备逻辑id
         * @param msg       具体的消息内容
         *
         * @return 设备返回的监听回调，返回设备的响应消息
         */
        AC.bindMgr().sendToDeviceWithOption(subDomain, deviceId, getACDeviceMsg(Light.CLOSELIGHT), AC.LOCAL_FIRST, new PayloadCallback<ACDeviceMsg>() {
            @Override
            public void success(ACDeviceMsg deviceMsg) {
                Pop.popToast(AC.context, "关灯成功");
            }

            @Override
            public void error(ACException e) {
                Toast.makeText(AC.context, e.getErrorCode() + "-->" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private ACDeviceMsg getACDeviceMsg(int i) {
        if (i == Light.OPENLIGHT) {
            return new ACDeviceMsg(Config.LIGHT_MSGCODE, new byte[]{1, 0, 0, 0});
        } else {
            return new ACDeviceMsg(Config.LIGHT_MSGCODE, new byte[]{0, 0, 0, 0});
        }
    }
}