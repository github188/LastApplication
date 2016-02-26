package com.iermu.client.util;

import java.util.Random;

/**
 * Created by zhoushaopei on 15/7/23.
 */
public class EncryptUtil {



    public static String encryptPass(String password) {
        Logger.i(password);
        int m = new Random().nextInt(6);
        int n = new Random().nextInt(6);
        int i = (int) Math.abs(Math.random() * 900 + 100);
        int j = (int) Math.abs(Math.random() * 9000 + 1000);
        String var1 = Integer.toString(i);
        String var2 = Integer.toString(j);

        if (m>n) {
            password = password.substring(0,m) + var1 + password.substring(m,password.length());
            password = password.substring(0,n) + var2  + password.substring(n,password.length()) + Integer.toString(m) + "3" + Integer.toString(n) + 4;
        }else if (m==n) {
            password = password.substring(0,m) + var1 + password.substring(m,password.length());
            password = password.substring(0,n) + var2  + password.substring(n,password.length()) + Integer.toString(m) + "3" + Integer.toString(n) + 4;
        }else {
            password = password.substring(0,n) + var1 + password.substring(n,password.length());
            password =  password.substring(0,m) + var2 + password.substring(m,password.length()) + Integer.toString(n) + "3" + Integer.toString(m) + 4;
        }
//        Logger.i(n + "------"+ m + "------" + var1 + "--------" + var2 + "-------" + password);
        return password;
    }


}
