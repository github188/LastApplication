package com.iermu.ui.util;

/**
 * Created by zhoushaopei on 15/11/24.
 */
public class CapsuleUtil {

    public static int temNum(int num) {
        if (num > 38) {
            return 1;
        } else if (num >= 31 && num <= 37) {
            return 2;
        } else if (num >= 29 && num <= 30) {
            return 3;
        } else if (num >= 27 && num < 28) {
            return 4;
        } else if (num >= 23 && num < 26) {
            return 5;
        } else if (num >= 18 && num <= 22) {
            return 6;
        } else if (num >= 13 && num <= 17) {
            return 7;
        } else if (num >= 9 && num <= 12) {
            return 8;
        } else if (num >= 1 && num <= 8) {
            return 9;
        } else if (num < 0) {
            return 10;
        }
        return 0;
    }

    public static int humNum(int num) {
        if (num >= 81 & num < 100) {
            return 1;
        } else if (num >= 61 && num <= 80) {
            return 2;
        } else if (num >= 30 && num <= 60) {
            return 3;
        } else if (num >= 0 && num <= 29) {
            return 4;
        }
        return 0;
    }

}
