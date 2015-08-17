package com.accloud.ac_service_android_wifi_demo.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.accloud.ac_service_android_wifi_demo.R;
import com.accloud.ac_service_android_wifi_demo.activity.BaseActivity;
import com.accloud.ac_service_android_wifi_demo.activity.device.MainActivity;
import com.accloud.ac_service_android_wifi_demo.application.MainApplication;
import com.accloud.ac_service_android_wifi_demo.config.AppConstant;
import com.accloud.ac_service_android_wifi_demo.config.Config;
import com.accloud.ac_service_android_wifi_demo.model.UserInfo;
import com.accloud.ac_service_android_wifi_demo.utils.Pop;
import com.accloud.ac_service_android_wifi_demo.utils.StringUtils;
import com.accloud.ac_service_android_wifi_demo.view.CusEditText;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACAccountMgr;
import com.accloud.service.ACException;
import com.accloud.service.ACThirdPlatform;
import com.accloud.service.ACUserInfo;
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

import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2015/7/8 0008.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private CusEditText phoneEditText;
    private CusEditText pwdEditText;
    private TextView forgetPwd;
    private TextView register;
    private Button loginBtn;

    private String tel;
    private String pwd;
    //账号管理器
    ACAccountMgr accountMgr;

    //登陆按钮
    private TextView qqLogin;
    private TextView weiboLogin;
    private TextView weixinLogin;

    //友盟登陆
    private UMSocialService mController;

    String nickName;
    int loginType;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setNavBtn(R.drawable.back, 0);
        setTitle(getString(R.string.login_title));
        initView();

        //通过AC获取账号管理器
        accountMgr = AC.accountMgr();
        //友盟登陆
        mController = UMServiceFactory.getUMSocialService("com.umeng.login");
    }

    public void initView() {
        phoneEditText = (CusEditText) findViewById(R.id.login_edit_phone);
        pwdEditText = (CusEditText) findViewById(R.id.login_edit_pwd);
        forgetPwd = (TextView) findViewById(R.id.forgetPwd);
        register = (TextView) findViewById(R.id.register);
        loginBtn = (Button) findViewById(R.id.login);
        weiboLogin = (TextView) findViewById(R.id.login_weibo);
        qqLogin = (TextView) findViewById(R.id.login_qq);
        weixinLogin = (TextView) findViewById(R.id.login_weixin);
        phoneEditText.setText(getString(R.string.login_phone));
        phoneEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        pwdEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        pwdEditText.setText(getString(R.string.login_pwd));

        forgetPwd.setText(Html.fromHtml("<u>" + getString(R.string.login_reset) + "?" + "</u>"));
        forgetPwd.setOnClickListener(this);
        register.setOnClickListener(this);
        loginBtn.setOnClickListener(this);

        qqLogin.setOnClickListener(this);
        weiboLogin.setOnClickListener(this);
        weixinLogin.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accountMgr.isLogin()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);//如果登陆成功
            startActivity(intent);
            LoginActivity.this.finish();
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.forgetPwd:
                intent = new Intent(LoginActivity.this, PhoneActivity.class);
                intent.putExtra("flag", AppConstant.RESETPASSWORD);
                startActivity(intent);
                break;
            case R.id.register:
                intent = new Intent(LoginActivity.this, PhoneActivity.class);
                intent.putExtra("flag", AppConstant.REGISTER);
                startActivity(intent);
                break;
            case R.id.login:
                phoneEditText.hideHint();
                pwdEditText.hideHint();
                tel = phoneEditText.getText();
                pwd = pwdEditText.getText();
                if (tel.length() == 0) {
                    phoneEditText.showHint();
                    phoneEditText.setHintText(getString(R.string.login_phone_empty));
                } else if (pwd.length() == 0) {
                    pwdEditText.showHint();
                    pwdEditText.setHintText(getString(R.string.login_pwd_empty));
                } else if (StringUtils.isPhoneNumber(tel) || StringUtils.isEmail(tel)) {
                    login();
                } else {
                    phoneEditText.showHint();
                    phoneEditText.setHintText(getString(R.string.login_phone_hint));
                }
                break;
            case R.id.login_qq:
                login(SHARE_MEDIA.QQ);
                break;
            case R.id.login_weixin:
                login(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.login_weibo:
                login(SHARE_MEDIA.SINA);
                break;
        }
    }

    private void login(final SHARE_MEDIA share_media) {
        mController = UMServiceFactory.getUMSocialService("com.umeng.login");
        if (share_media == SHARE_MEDIA.SINA)
            mController.getConfig().setSsoHandler(new SinaSsoHandler());
        if (share_media == SHARE_MEDIA.QQ) {
            UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(LoginActivity.this, Config.qq_appID, Config.qq_appKey);
            qqSsoHandler.addToSocialSDK();
        }
        if (share_media == SHARE_MEDIA.WEIXIN) {
            UMWXHandler wxHandler = new UMWXHandler(LoginActivity.this, Config.wx_appID, Config.wx_appSecret);
            wxHandler.addToSocialSDK();
        }
        mController.doOauthVerify(LoginActivity.this, share_media, new SocializeListeners.UMAuthListener() {
            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
                    final String openId = value.getString("uid");
                    final String accessToken = value.getString("access_token");
                    mController.getPlatformInfo(LoginActivity.this, share_media, new SocializeListeners.UMDataListener() {
                        @Override
                        public void onComplete(int status, Map<String, Object> info) {
                            if (status == 200 && info != null) {
//                                StringBuilder sb = new StringBuilder();
//                                Set<String> keys = info.keySet();
//                                for (String key : keys) {
//                                    sb.append(key + "=" + info.get(key).toString() + "\r\n");
//                                }
//                                LogUtil.d("TestData", sb.toString());
                                try {
                                    if (share_media == SHARE_MEDIA.SINA) {
                                        nickName = info.get("userName").toString();
                                    } else if (share_media == SHARE_MEDIA.QQ) {
                                        nickName = info.get("screen_name").toString();
                                    } else if (share_media == SHARE_MEDIA.WEIXIN) {
                                        nickName = info.get("nickname").toString();
                                    }
                                    loginWithOpenId(share_media, openId, accessToken);
                                } catch (NullPointerException e) {
                                    showToast("登录出现异常");
                                    return;
                                }
                            } else {
                                showToast("发生错误：" + status);
                            }
                        }

                        @Override
                        public void onStart() {
                            Toast.makeText(LoginActivity.this, "获取平台数据开始...", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }


    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        finish();
    }

    public void login() {
        /**
         * @param email或telephone
         * @param password
         * @param callback<userId>
         */
        accountMgr.login(tel, pwd, new PayloadCallback<ACUserInfo>() {
            @Override
            public void success(ACUserInfo userInfo) {
                Pop.popToast(LoginActivity.this, getString(R.string.login_success));
                MainApplication.getInstance().UserLogin(new UserInfo(userInfo.getUserId(), userInfo.getName()));
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }

            @Override
            public void error(ACException e) {
                pwdEditText.showHint();
                if (e.getErrorCode() == 404 || e.getErrorCode() == ACException.INTERNET_ERROR)
                    pwdEditText.setHintText(getString(R.string.internet_error));
                else
                    pwdEditText.setHintText(e.getMessage());
            }
        });
    }

    public void loginWithOpenId(SHARE_MEDIA share_media, String openId, String accessToken) {
        ACThirdPlatform platform = null;
        if (share_media == SHARE_MEDIA.SINA) {
            platform = ACThirdPlatform.SINA;
            loginType = 2;
        }
        if (share_media == SHARE_MEDIA.QQ) {
            platform = ACThirdPlatform.QQ;
            loginType = 3;
        }
        if (share_media == SHARE_MEDIA.WEIXIN) {
            platform = ACThirdPlatform.WEIXIN;
            loginType = 1;
        }
        /**
         * 第三方账号登录
         *
         * @param thirdPlatform 第三方类型（如QQ、微信、微博）
         * @param openId        通过第三方登录获取的openId
         * @param accessToken   通过第三方登录获取的accessToken
         * @param callback      返回结果的监听回调
         */

        accountMgr.loginWithOpenId(platform, openId, accessToken, new PayloadCallback<ACUserInfo>() {
            @Override
            public void success(ACUserInfo userInfo) {
                Pop.popToast(LoginActivity.this, getString(R.string.login_success));
                if (!userInfo.getName().equals(""))
                    nickName = userInfo.getName();
                MainApplication.getInstance().UserLogin(new UserInfo(loginType, userInfo.getUserId(), nickName));
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }

            @Override
            public void error(ACException e) {
                pwdEditText.showHint();
                if (e.getErrorCode() == 404 || e.getErrorCode() == ACException.INTERNET_ERROR)
                    pwdEditText.setHintText(getString(R.string.internet_error));
                else
                    pwdEditText.setHintText(e.getMessage());
            }
        });
    }
}
