package com.accloud.ac_service_android_wifi_demo.activity.personal;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.accloud.ac_service_android_wifi_demo.R;
import com.accloud.ac_service_android_wifi_demo.activity.BaseActivity;
import com.accloud.ac_service_android_wifi_demo.application.MainApplication;
import com.accloud.ac_service_android_wifi_demo.config.Config;
import com.accloud.ac_service_android_wifi_demo.model.UserInfo;
import com.accloud.ac_service_android_wifi_demo.utils.Pop;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACOpenIdInfo;
import com.accloud.service.ACThirdPlatform;
import com.accloud.utils.LogUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class BindAccountActivity extends BaseActivity implements View.OnClickListener {
    private TextView qq_login_text;
    private TextView weixin_login_text;
    private TextView sina_login_text;
    //友盟登陆
    private UMSocialService mController;
    ACThirdPlatform platform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_account);
        setTitle("绑定第三方账号");
        setNavBtn(R.drawable.back, 0);
        qq_login_text = (TextView) findViewById(R.id.bind_qq_text);
        weixin_login_text = (TextView) findViewById(R.id.bind_weixin_text);
        sina_login_text = (TextView) findViewById(R.id.bind_sina_text);
        AC.accountMgr().listAllOpenIds(new PayloadCallback<List<ACOpenIdInfo>>() {
            @Override
            public void success(List<ACOpenIdInfo> openIdInfos) {
                for (ACOpenIdInfo openIdInfo : openIdInfos) {
                    if (openIdInfo.getThirdPlatform() == ACThirdPlatform.QQ) {
                        qq_login_text.setText("已绑定");
                        qq_login_text.setTextColor(Color.BLACK);
                        qq_login_text.setEnabled(false);
                    } else if (openIdInfo.getThirdPlatform() == ACThirdPlatform.WEIXIN) {
                        weixin_login_text.setText("已绑定");
                        weixin_login_text.setTextColor(Color.BLACK);
                        weixin_login_text.setEnabled(false);
                    } else if (openIdInfo.getThirdPlatform() == ACThirdPlatform.SINA) {
                        sina_login_text.setText("已绑定");
                        sina_login_text.setTextColor(Color.BLACK);
                        sina_login_text.setEnabled(false);
                    }
                }
            }

            @Override
            public void error(ACException e) {

            }
        });
        qq_login_text.setOnClickListener(this);
        weixin_login_text.setOnClickListener(this);
        sina_login_text.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*使用SSO授权必须添加如下代码 */

       /* ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("人家正在努力获取信息，请等待啦");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();*/
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bind_qq_text:
                login(SHARE_MEDIA.QQ);
                break;
            case R.id.bind_weixin_text:
                login(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.bind_sina_text:
                login(SHARE_MEDIA.SINA);
                break;
        }
    }

    private void login(final SHARE_MEDIA share_media) {
        mController = UMServiceFactory.getUMSocialService("com.umeng.login");
        if (share_media == SHARE_MEDIA.SINA)
            mController.getConfig().setSsoHandler(new SinaSsoHandler());
        if (share_media == SHARE_MEDIA.QQ) {
            UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(BindAccountActivity.this, Config.qq_appID, Config.qq_appKey);
            qqSsoHandler.addToSocialSDK();
        }
        if (share_media == SHARE_MEDIA.WEIXIN) {
            UMWXHandler wxHandler = new UMWXHandler(BindAccountActivity.this, Config.wx_appID, Config.wx_appSecret);
            wxHandler.addToSocialSDK();
        }
        mController.doOauthVerify(BindAccountActivity.this, share_media, new SocializeListeners.UMAuthListener() {
            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
            }

            @Override
            public void onComplete(final Bundle value, SHARE_MEDIA platform) {
                if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
                    if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
                        String openId = value.getString("uid");
                        String accessToken = value.getString("access_token");
                        bindWithOpenId(share_media, openId, accessToken);
                    }
                } else {
                    Toast.makeText(BindAccountActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
            }

            @Override
            public void onStart(SHARE_MEDIA platform) {
            }
        });
    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        finish();
    }

    public void bindWithOpenId(SHARE_MEDIA share_media, String openId, String accessToken) {
        if (share_media == SHARE_MEDIA.SINA)
            platform = ACThirdPlatform.SINA;
        if (share_media == SHARE_MEDIA.QQ)
            platform = ACThirdPlatform.QQ;
        if (share_media == SHARE_MEDIA.WEIXIN)
            platform = ACThirdPlatform.WEIXIN;
        /**
         * 绑定第三方账号
         *
         * @param thirdPlatform 第三方类型（如QQ、微信、微博）
         * @param openId        通过第三方登录获取的openId
         * @param accessToken   通过第三方登录获取的accessToken
         * @param callback      返回结果的监听回调
         */
        AC.accountMgr().bindWithOpenId(platform, openId, accessToken, new VoidCallback() {
            @Override
            public void success() {
                showToast("绑定成功");
                if (platform == ACThirdPlatform.QQ) {
                    qq_login_text.setText("已绑定");
                    qq_login_text.setTextColor(Color.BLACK);
                    qq_login_text.setEnabled(false);
                } else if (platform == ACThirdPlatform.WEIXIN) {
                    weixin_login_text.setText("已绑定");
                    weixin_login_text.setTextColor(Color.BLACK);
                    weixin_login_text.setEnabled(false);
                } else if (platform == ACThirdPlatform.SINA) {
                    sina_login_text.setText("已绑定");
                    sina_login_text.setTextColor(Color.BLACK);
                    sina_login_text.setEnabled(false);
                }
            }

            @Override
            public void error(ACException e) {
                showToast(e.getErrorCode() + "-->" + e.getMessage());
            }
        });
    }
}
