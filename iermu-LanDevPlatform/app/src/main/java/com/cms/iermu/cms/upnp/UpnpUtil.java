package com.cms.iermu.cms.upnp;

import android.util.Log;

import com.cms.iermu.cms.CmsUtil;

import java.io.IOException;
import java.net.DatagramPacket;

/**
 *
 *
 * Created by wcy on 15/6/22.
 */
public class UpnpUtil {


    /**
     * 通过ssdp发现指定i耳目设备
     *
     * @param strDevID
     * @param iNum
     * @return
     */
    public static String sendSSDPSearchMessage(String strDevID, int iNum) {
        String ret = null;
        SSDPSearchMsg searchProduct = new SSDPSearchMsg(SSDPConstants.cmsIermu);
        SSDPSocket sock = null;
        try {
            sock = new SSDPSocket();
            sock.send(searchProduct.toString());
            for (int i=0; i<iNum; i++) {
                try{
                    DatagramPacket dp = iNum>10? sock.receive(1000) : sock.receive();

                    if(dp==null) break;
                    String c = new String(dp.getData()).trim();
                    String ip = new String(dp.getAddress().toString()).trim();
                    if(c==null) continue;
                    if(c.indexOf("cmsIermu")>=0){
                        //Log.i("tanhx", "rcv msg：\n" + c + "\ndev ip：" + ip + "\n\n");
                        int iS = c.indexOf("LOCATION");
                        String strTemp = c.substring(iS);
                        iS = strTemp.indexOf("http");
                        int ie = strTemp.indexOf("\r\n");
                        if(strDevID==null){
                            if(ret==null) ret = "";
                            ret += (strTemp.substring(iS+7, ie) + "\n");
                        }
                        else{
                            String strDevs = strTemp.substring(iS+7, ie);
                            String[] strDev = CmsUtil.split(strDevs, "/");
                            if(strDev==null || strDev.length<3) continue;
                            if(strDev[1].equals(strDevID)){
                                ret = strDevs;
                                break;
                            }
                        }
                    }
                }
                catch (IOException e) {
                    //e.printStackTrace();
                    break;
                }
                catch (Exception e){
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(sock!=null)sock.close();
        //Log.d("tanhx", "iermu dev is: " + ret);
        return ret;
    }

    /**
     * 通过ssdp发现指定i耳目设备
     *
     * @param strDevID
     * @return
     */
    public static String getLanDeviceIPByDeviceId(String strDevID) {
        String ret = null;
        SSDPSearchMsg searchProduct = new SSDPSearchMsg(SSDPConstants.cmsIermu);
        SSDPSocket sock = null;
        String ip = null;
        try {
            sock = new SSDPSocket();
            sock.send(searchProduct.toString());
            for (int i=0; i<20; i++) {
                try{
                    DatagramPacket dp = sock.receive(1000);

                    if(dp==null) break;
                    String c = new String(dp.getData()).trim();
                    if(c==null) continue;
                    if(c.indexOf("cmsIermu")>=0){
                        //Log.i("tanhx", "rcv msg：\n" + c + "\ndev ip：" + ip + "\n\n");
                        int iS = c.indexOf("LOCATION");
                        String strTemp = c.substring(iS);
                        iS = strTemp.indexOf("http");
                        int ie = strTemp.indexOf("\r\n");
                        if(strDevID==null){
                            if(ret==null) ret = "";
                            ret += (strTemp.substring(iS+7, ie) + "\n");
                        }
                        else{
                            String strDevs = strTemp.substring(iS+7, ie);
                            String[] strDev = CmsUtil.split(strDevs, "/");
                            if(strDev==null || strDev.length<3) continue;
                            if(strDev[1].equals(strDevID)){
                                ip = new String(dp.getAddress().toString()).trim().substring(1);
                                Log.i("deviceip", ip);
                                break;
                            }
                        }
                    }
                }
                catch (IOException e) {
                    //e.printStackTrace();
                    break;
                }
                catch (Exception e){
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(sock!=null)sock.close();
        //Log.d("tanhx", "iermu dev is: " + ret);
        return ip;
    }

}
