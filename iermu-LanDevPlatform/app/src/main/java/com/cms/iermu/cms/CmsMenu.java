package com.cms.iermu.cms;

import android.net.wifi.ScanResult;
import android.util.Log;

/**
 * 与设备通讯类
 *
 * @author tanhx
 *
 */
public class CmsMenu {


    public static boolean isIpcAP(ScanResult wifiIPC){
        String strSsid = wifiIPC.SSID;
        String strMac = wifiIPC.BSSID;
        return isIpcAP(strSsid, strMac);
    }

    public static boolean isIpcAP(String strSsid, String strMac){
        boolean ret = false;
        boolean bLyy        = strSsid.indexOf(CmsUtil.IPC_LYYAP_SSID)==0;
        boolean bIermu      = strSsid.indexOf(CmsUtil.IPC_AP_SSID)==0;
        boolean bIermuDirect= strSsid.indexOf(CmsUtil.IPC_AP_SSID_DIRECT)==0;
        if((!bLyy && !bIermu && !bIermuDirect) || strSsid.length()<11) return ret;

        String strDevID = getDevIDByMac(strMac, bIermuDirect);
        int iLen = strDevID.length();
        if(iLen>6){
            Log.d("tanhx", "ssid=" + strSsid + ", devid=" + strDevID);
            String strSubDevID = bLyy ? strSsid.substring(CmsUtil.IPC_LYYAP_SSID.length())
                    : (bIermu ? strSsid.substring(CmsUtil.IPC_AP_SSID.length())
                        : strSsid.substring(CmsUtil.IPC_AP_SSID_DIRECT.length()));
            int iSubLen = strSubDevID.length();
            //if(iSubLen==7) return ret;			// TODO 需要提供参数来决定是否屏蔽
            String strMatch = strDevID.substring(iLen-iSubLen);
            String strIpc = strSsid.substring(strSsid.length()-iSubLen);
            if(strMatch.equals(strIpc)) ret = true;
        }

		/*if((strSsid.indexOf(cmsUtils.IPC_AP_SSID)==0 || strSsid.indexOf(cmsUtils.IPC_AP_SSID_DIRECT)==0)&& strSsid.length()>=11) {
			String strDevID = cmsMenu.getDevIDByMac(strMac, strSsid.indexOf(cmsUtils.IPC_AP_SSID_DIRECT)==0);
			int iLen = strDevID.length();
			if(iLen>6){
				String strMatch = strDevID.substring(iLen-6);
				String strIpc = strSsid.substring(strSsid.length()-6);
				if(strMatch.equals(strIpc))  ret = true;
			}
		}*/
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

    public static String getDevIDByMac(String strMac, boolean bDirect) {
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
        if (bDirect)
            bnatid[0] = bnatid[0] - 2;
        long tInt1 = (bnatid[0] << 8) | bnatid[1];
        long tInt2 = (bnatid[2] << 24) | (bnatid[3] << 16) | (bnatid[4] << 8)
                | (bnatid[5]);

        long natid = (tInt1 << 32) | (tInt2 & 0xffffffffl);
        ret = Long.toString(natid);
        //Log.d("tanhx", "direct mac is " + strMac + ", natid=" + ret);
        return ret;
    }

    public static String getDevIDByMac(byte[] b) {
        String ret = null;
        int[] bnatid = new int[6];
        for (int i = 0; i < 6; i++) {
            bnatid[i] = b[i];
            bnatid[i] &= 0xff;
        }
        long tInt1 = (bnatid[0] << 8) | bnatid[1];
        long tInt2 = (bnatid[2] << 24) | (bnatid[3] << 16) | (bnatid[4] << 8)
                | (bnatid[5]);

        long natid = (tInt1 << 32) | (tInt2 & 0xffffffffl);
        ret = Long.toString(natid);
        Log.d("tanhx", "log natid=" + ret);
        return ret;
    }

    public static byte[] getMacByDevID(String strDevID) {
        long natid = Long.parseLong(strDevID);
        long lInt1 = natid>>32;
        long lInt2 = natid & 0xffffffffl;
        byte[] bMac = new byte[6];
        bMac[0] = (byte) ((lInt1>>8)&0xff);
        bMac[1] = (byte) (lInt1 & 0xff);
        bMac[2] = (byte) ((lInt2>>24)&0xff);
        bMac[3] = (byte) ((lInt2>>16)&0xff);
        bMac[4] = (byte) ((lInt2>>8)&0xff);
        bMac[5] = (byte) (lInt2&0xff);
        return bMac;
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

}
