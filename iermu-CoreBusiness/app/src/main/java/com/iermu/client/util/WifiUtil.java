package com.iermu.client.util;

/**
 * Created by xjy on 15/8/13.
 */
public class WifiUtil {


    public static boolean is5GHz(int freq) {
        return freq > 4900 && freq < 5900;
    }

}
