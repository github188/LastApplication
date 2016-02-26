package com.cms.iermu.cms;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * Created by wcy on 15/6/22.
 */
public class CmsUtil {

    // 第三方应用sdk，获取百度授权帐号服务器
    public static final String IERMU_URL = "https://www.iermu.com/c.php";

    public static final String DEV_NO_ACTIVATED = "22817078135";//设备未激活

    public static final String IPC_AP_SSID = "iermu";
    public static final String IPC_LYYAP_SSID = "iermuly";
    public static final String IPC_AP_SSID_DIRECT = "DIRECT-iermu";

    public static boolean m_bCmsApp = true;
    private static String m_AppID;
    private static String m_AK;
    private static String m_SK;
    private static String m_pcsSK;
    private static String m_factoryNO;

    private static String m_iermu_token = null;  // 从耳目获取授权

    /**
     * 获取wifi加密方式
     * @param strEncryption
     * @return 1：open  2: wep 3: wpa 4: eap(wpa2企业版，目前不支持)
     */
    public static int getWifiType(String strEncryption){
        int ret = 1;
        if(strEncryption==null || strEncryption.indexOf("open")!=-1) return ret;

        if(strEncryption.toLowerCase().indexOf("eap")!=-1){ // eap
            ret = 4;
        }
        else if(strEncryption.toLowerCase().indexOf("wep")!=-1){  // wep
            ret = 2;
        }
        else if(strEncryption.toLowerCase().indexOf("wpa")!=-1){ // wpa
            ret = 3;
        }

        return ret;
    }

    /**
     * Split string into multiple strings
     *
     * @param original
     *            : Original string
     * @param separator
     *            : Separator string in original string
     * @return Splitted string array
     */
    public static String[] split(String original, String separator) {
        if(original==null) return null;
        Vector<String> nodes = new Vector<String>();
        nodes.removeAllElements();
        // Parse nodes into vector
        int index = original.indexOf(separator);
        while (index >= 0) {
            nodes.addElement(original.substring(0, index));
            original = original.substring(index + separator.length());
            index = original.indexOf(separator);
        }
        // Get the last node
        nodes.addElement(original);

        // Create splitted string array
        String[] result = new String[nodes.size()];
        if (nodes.size() > 0) {
            nodes.copyInto(result);
        }
        return result;
    }

    /**
     * Convert hex string to byte[]
     *
     * @param hexString
     *            the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * bytes字符串转换为Byte值
     *
     * @param src
     *            src Byte字符串，每个Byte之间没有分隔符
     * @return byte[]
     */
    public static byte[] hexStr2Bytes(String src) {
        String[] src_sp = src.split(" ");
        byte[] ret = new byte[src_sp.length];
        for (int i = 0; i < src_sp.length; i++) {
            ret[i] = Byte.decode("0x" + src_sp[i]);
        }
        return ret;
    }

    /**
     * 根据mac地址获取设备natid
     *
     * @param strMac
     * @return
     */
    public static String getDevIDByMac(String strMac) {
        String ret = null;
        String[] strMacB = CmsUtil.split(strMac, ":");
        String strTemp = "";
        int iLen = strMacB.length;
        for (int i = 0; i < iLen; i++) {
            strTemp += strMacB[i];
        }
        int[] bnatid = new int[6];
        byte[] b = CmsUtil.hexStringToBytes(strTemp);
        for (int i = 0; i < 6; i++) {
            bnatid[i] = b[i];
            bnatid[i] &= 0xff;
        }
        long tInt1 = (bnatid[0] << 8) | bnatid[1];
        long tInt2 = (bnatid[2] << 24) | (bnatid[3] << 16) | (bnatid[4] << 8)
                | (bnatid[5]);

        long natid = (tInt1 << 32) | (tInt2 & 0xffffffffl);
        ret = Long.toString(natid);
        Log.d("tanhx", "mac is " + strMac + ", natid=" + ret);

        return ret;
    }

    public static String getDevID(byte[] b) {
        String ret = null;
        int[] bnatid = new int[6];
        for (int i = 68; i < 74; i++) {
            bnatid[i - 68] = b[i];
            bnatid[i - 68] &= 0xff;
            String sTemp = Integer.toHexString(0xff & b[i]);
            sTemp = sTemp.length() == 1 ? "0" + sTemp : sTemp;
            // Log.d("tanhx", "mac[" + (i-68) + "]:" + sTemp);
        }
        long tInt1 = (bnatid[0] << 8) | bnatid[1];
        long tInt2 = (bnatid[2] << 24) | (bnatid[3] << 16) | (bnatid[4] << 8)
                | (bnatid[5]);

        long natid = (tInt1 << 32) | (tInt2 & 0xffffffffl);
        ret = Long.toString(natid);
        return ret;
    }

    /**
     * Convert char to byte
     *
     * @param c
     *            char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }


    public static short decodeShort(byte[] acPack, int iInc, int intLen) {
        int m = 0;
        int k = 0;
        for (int j = iInc; j < intLen + iInc; j++) {
            int i = (0xFF & acPack[j]) << k * 8;
            k++;
            m = i + m;
        }
        return (short) m;
    }

    public static byte[] htonl(int i) {
        byte[] ret = new byte[4];
        ret[3] = (byte) (i & 0xFF);
        ret[2] = (byte) (0xFF & i >> 8);
        ret[1] = (byte) (0xFF & i >> 16);
        ret[0] = (byte) (0xFF & i >> 24);
        return ret;
    }

    public static String getIpAddr(String strUrl) {
        String ret = strUrl;
        if (strUrl != null) {
            ret = strUrl.replaceAll("http://", "");
            ret = ret.replaceAll("[\r\n]", "");
        }
        return ret;
    }

    public static int ByteArrayToint(byte[] b) {
        int j = 0;
        for (int i = 3; i >= 0; i--)
            j = j << 8 | 0xFF & b[i];
        return j;
    }

    public static int ByteArrayToint(byte[] b, int iStart) {
        int ret = 0;
        for (int i = iStart; i < iStart + 4; i++)
            ret = ret << 8 | (0xFF & b[i]);
        return ret;
    }

    public static int ByteArrayToint(byte[] b, int iStart, boolean bBig) {
        int ret = 0;
        if (bBig) {
            for (int i = iStart; i < iStart + 4; i++) {
                ret = ret << 8 | (0xFF & b[i]);
            }
        } else {
            for (int i = iStart + 3; i > iStart - 1; i--) {
                ret = ret << 8 | (0xFF & b[i]);
            }
        }
        return ret;
    }

    // 获取imsi
    public static String getIMSI(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String ret = tm.getSubscriberId();
        if (ret == null || ret.length()<10)
            ret = tm.getDeviceId();
        if (ret == null || ret.length()<10)
            ret = getLocalMacAddress(context);
        if (ret == null || ret.length()<10)
            ret = getDevIMEI();
        return ret;
    }

    // 获取wifi mac地址
    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    public static String getDevIMEI(){
        String ret = "35"
                + // we make this look like a valid IMEI
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10
                + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10
                + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10
                + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10
                + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10
                + Build.TAGS.length() % 10 + Build.TYPE.length() % 10
                + Build.USER.length() % 10; // 13 digits
        return ret;
    }

    public static byte[] htons(short i) {
        byte[] ret = new byte[2];
        ret[0] = (byte) (0xFF & i >> 8);
        ret[1] = (byte) (i & 0xFF);
        return ret;
    }

    // 获取当前应用元数据信息
    public static String getMetaValue(String str) {
        String ret = null;
        if (str.equals("s")) {  // 应用sk
            ret = m_bCmsApp? cmsNative.getAppInfoS(str) : m_SK;
        } else if (str.equals("a")) { // 应用ak
            ret = m_bCmsApp? cmsNative.getAppInfoA(str) : m_AK;
        } else if (str.equals("p")) { // 应用pcsSk
            ret = m_bCmsApp? cmsNative.getPcsTokenS(str) : m_pcsSK;
        } else if (str.equals("f")) { // 应用厂家编号，在设备注册前需要验证是否具有该设备的权限
            ret = m_bCmsApp? "0" : m_factoryNO;
        } else {  // 应用ID
            ret = m_bCmsApp? cmsNative.getAppInfo(str) : m_AppID;
        }

        return ret;
    }

    /**
     * 第三方应用启动程序需要设置SDK获取授权的方式
     * @param strAppID  ： 应用id
     * @param strUser   ： 耳目提供的帐号
     * @param strPwd    ： 耳目帐号密码， 以上需要向耳目公司申请
     * @param strUid	 ： 第三方应用登录的用户id
     */
    public static void setIermuToken(String strAppID, String strUser, String strPwd, String strUid){
        m_iermu_token = getIermuUrl(strAppID, strUser, strPwd, strUid) + "&pwd=" + strPwd;
    }

    public static String getIermuToken(){
        return m_iermu_token;
    }

    private static String getIermuUrl(String strAppID, String strUser, String strPwd, String strUid){
        //String ret = cmsUtils.bytesToHexString(new byte[]{(byte) 0xee});
        String strUrl = IERMU_URL;
        String strExpire = Long.toString(System.currentTimeMillis()/1000);
        String strSign = CmsUtil.MD5(strUser + strPwd + strExpire);
        strSign = strSign.substring(0,16);
        Map<String, String> params = new HashMap<String, String>();
        params.put("c", strAppID);
        params.put("u", strUser);
        params.put("i", strUid);
        params.put("e", strExpire);
        params.put("s", strSign);

        StringBuilder buf = new StringBuilder(strUrl);
        Set<Map.Entry<String, String>> entrys = null;
        buf.append("?");
        entrys = params.entrySet();
        try {
            for (Map.Entry<String, String> entry : entrys) {
                buf.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                        .append("&");
            }
            buf.deleteCharAt(buf.length() - 1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return buf.toString();
    }

    // MD5加密，32位
    public static String MD5(String str) {
        // Log.d("tanhx", "md5 input:" + str);
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        // Log.d("tanhx", "md5 output:" + hexValue.toString());
        return hexValue.toString();
    }

}
