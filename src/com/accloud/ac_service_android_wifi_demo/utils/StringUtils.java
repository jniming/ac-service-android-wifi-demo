package com.accloud.ac_service_android_wifi_demo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Xuri on 2015/4/7.
 */
public class StringUtils {


    public static boolean isPhoneNumber(String phone) {
        String regExp = "^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(phone);
        return m.find();
    }

    /**
     * ≈–∂œ” œ‰ «∑Ò∫œ∑®
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (null == email || "".equals(email)) return false;
        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //ºÚµ•∆•≈‰
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//∏¥‘”∆•≈‰
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
