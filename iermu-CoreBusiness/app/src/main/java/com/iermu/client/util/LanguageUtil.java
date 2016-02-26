package com.iermu.client.util;

import com.iermu.client.config.AppConfig;

import java.util.Locale;

/**
 * Created by zhoushaopei on 16/2/18.
 */
public class LanguageUtil {

    /**
     * 判断是否是英文
     * @return
     */
    public static String language() {
        String language = Locale.getDefault().getLanguage();
        return language;
    }

    /**
     * 判断是否是英文
     * @return
     */
    public static boolean isEn() {
        String language = Locale.getDefault().getLanguage();
        if (language.equals("en")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取语言环境
     * @return
     */
    public static String getLanguage() {
        String language = language();
        if (AppConfig.INTERNATIONAL) {
            if (language.equals("en")) {
                return "en";
            } else if (language.equals("ko")) {
                return "en";
            } else if (language.equals("ja")) {
                return "en";
            } else {
                return "en";
            }
        } else {
            return "zh";
        }
    }
}
