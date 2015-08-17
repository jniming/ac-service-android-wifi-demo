package com.accloud.ac_service_android_wifi_demo.model;

/**
 * Created by Administrator on 2015/4/25.
 */
public class UserInfo {
    //0ÆÕÍ¨ÓÃ»§µÇÂ¼ 1Î¢ÐÅµÇÂ¼ 2ÐÂÀËµÇÂ¼ 3qqµÇÂ¼
    private int loginType;
    private long userId;
    private String nickName;

    public UserInfo() {
    }

    public UserInfo(long userId, String nickName) {
        loginType = 0;
        this.userId = userId;
        this.nickName = nickName;
    }

    public UserInfo(int loginType, long userId, String nickName) {
        this.loginType = loginType;
        this.userId = userId;
        this.nickName = nickName;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userId=" + userId +
                ", nickName='" + nickName + '\'' +
                '}';
    }
}
