package com.iermu.client.business.impl.setupdev;

import android.os.SystemClock;
import android.util.Log;

import com.cms.iermu.cms.CmsMenu;
import com.cms.iermu.cms.cmsNative;
import com.iermu.client.model.CamDev;
import com.iermu.client.model.constant.CamDevType;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.model.constant.ScanDevMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zsj on 15/11/30.
 */
public class HiWifiScanRunnable extends Thread{
    boolean m_isHiwifi = false; // 连接极路由，只能用极路由模式配置，演示版如此
    String m_hiwifiSecret = null; // hiwifi secret
    String m_hiwifiErmuMac = null;
    String m_hiwifiMac = null;
    String m_hiwifiToken = null;

    long st;

    HashMap map = new HashMap();

    private List<CamDev> m_dev_list = new ArrayList<CamDev>();

    private boolean threadExit=false;

    private OnScanCamDevListener listener;

    private long timeStart;
    private long timeOut = 60*1000;

    public HiWifiScanRunnable setListener(OnScanCamDevListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        this.threadExit = true;
    }

    // for 极路由快连
    @Override
    public void run() {
        st = System.currentTimeMillis();
        timeStart = System.currentTimeMillis();
        if(!HiWifiApi.isHiwifi()) return;
        String[] retMac = HiWifiApi.getMacToken();
        if(retMac!=null){
            Log.d("mac", "mac:" + retMac[0] + ", token:" + retMac[1]);
            m_hiwifiMac = retMac[0];
            m_hiwifiToken = retMac[1];
            m_hiwifiSecret = HiWifiApi.getSecret(retMac[0], retMac[1]);
            Log.d("Hiwifi", "secret=" + m_hiwifiSecret);
            if(m_hiwifiSecret!=null) {
                while (!threadExit) {
                    if(System.currentTimeMillis() - timeStart > timeOut) break;;
                    String ermuList = HiWifiApi.getIermu(retMac[0], retMac[1], m_hiwifiSecret);
                    if(ermuList!=null){
                        // 解析ermuList返回
                        JSONObject retMsg;
                        try {
                            retMsg = new JSONObject(ermuList);
                            int iCode = retMsg.getInt("code");
                            int iAppCode = retMsg.getInt("app_code");
                            String appData = retMsg.getString("app_data");
                            if(iCode==0 && iAppCode==0){
                                retMsg = new JSONObject(appData);
                                JSONArray listDevs = retMsg.getJSONArray("iot_list");
                                if(listDevs!=null){
                                    int iCount = listDevs.length();
                                    if(iCount>0){
                                        Log.d("Hiwifi", "iermu count=" + iCount);
                                        String strPwd = cmsNative.getIpcLogPwd("3");
                                        for(int i=0; i<iCount; i++){
                                            JSONObject myCam = new JSONObject(listDevs.getString(i));
                                            Log.i("myCam","myCam"+myCam);
                                            String strMac = myCam.getString("mac");
                                            m_hiwifiErmuMac = strMac;
                                            String devId = CmsMenu.getDevIDByMac(strMac);
                                            Log.d("Hiwifi", "iermu devId = " + devId);


//                                            getHiwifiIpThread(m_hiwifiErmuMac, m_hiwifiMac, m_hiwifiSecret,strPwd);
                                                int iret = checkDevOne(devId);
                                                if(iret<0){
//                                                String devIP = getHiwifiIp(m_hiwifiErmuMac, m_hiwifiMac, m_hiwifiSecret);
//                                                Log.d("Hiwifi", "devId = "+devId +"---devIP:"+ devIP+"time:"+(System.currentTimeMillis() - st));
//                                                if(devIP!=null&&!"".equals(devIP)){
//                                                    String addRet = HiWifiApi.addIermu(m_hiwifiErmuMac, m_hiwifiMac, m_hiwifiSecret);//让设备上网
//                                                    String strret = HiWifiApi.getIermuIp(m_hiwifiErmuMac, m_hiwifiMac, m_hiwifiSecret);//拿设备ip
                                                    CamDev mydev = new CamDev();
                                                    mydev.setDevType(CamDevType.TYPE_HIWIFI);
                                                    mydev.setDevIP("");
                                                    mydev.setBSSID(strMac);
                                                    mydev.setDevID(CmsMenu.getDevIDByMac(strMac));
                                                    mydev.setDevPwd(strPwd);
                                                    mydev.setConnectType(ConnectType.BAIDU);
                                                    m_dev_list.add(mydev);
//                                                    Log.i("ipid", mydev.getDevID() + "====" + devIP);
                                                    if(listener != null) {
                                                        //返回扫描结果: 有设备、没有设备
                                                        listener.onDevList(ScanDevMode.HIWIFI, m_dev_list);
                                                    }
//                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    SystemClock.sleep(1000);
                }
            }
        }
    }



    public  static String getHiwifiIp(String hiwifiErmuMac, String hiwifiMac,String hiwifiSecret){

        String addRet = HiWifiApi.addIermu(hiwifiErmuMac, hiwifiMac, hiwifiSecret);//让设备上网
        JSONObject retMsg;
        String strIp = null;
        try {
            retMsg = new JSONObject(addRet);
            int iCode = retMsg.getInt("code");
            int iAppCode = retMsg.getInt("app_code");
            if(iCode!=0 || iAppCode!=0){
                // 提示绑定hiwifi失败
                return null;
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        int count = 0;
//        while(count<3){
//            count ++;
//                    if(m_bExitSet || m_bExitThread) break;
            String strret = HiWifiApi.getIermuIp(hiwifiErmuMac, hiwifiMac, hiwifiSecret);//拿设备ip
            if(addRet ==null) return null;
            Log.i("strret","strret:"+strret);
            try {
                retMsg = new JSONObject(strret);
                int iCode = retMsg.getInt("code");
                int iAppCode = retMsg.getInt("app_code");
                String appData = retMsg.getString("app_data");
                if(iCode==0 && iAppCode==0){
                    retMsg = new JSONObject(appData);
                    strIp = retMsg.getString("ip");
//                            Log.d(TAG, "from hiwifi: dev ip:" + strIp);
                    if(strIp!=null && strIp.length()>6){
//                                m_iConnType = 1;
//                                m_ethDevIp = strIp;
//                                startEthSet();
//                        break;
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
//            SystemClock.sleep(1000);
//        }
        return strIp;
    }




    private  void getHiwifiIpThread(final String hiwifiErmuMac, final String hiwifiMac,final String hiwifiSecret,final String strPwd) {

        new Thread(new Runnable() {
            public void run() {

                String strId = CmsMenu.getDevIDByMac(hiwifiErmuMac);
                if(map.get(strId)!=null) return;

                String addRet = HiWifiApi.addIermu(hiwifiErmuMac, hiwifiMac, hiwifiSecret);//让设备上网
                if(addRet ==null) return;
                JSONObject retMsg;
                try {
                    retMsg = new JSONObject(addRet);
                    int iCode = retMsg.getInt("code");
                    int iAppCode = retMsg.getInt("app_code");
                    if (iCode != 0 || iAppCode != 0) {
                        // 提示绑定hiwifi失败
//                        return null;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                int count = 0;
//                while (count < 3) {
//                    count++;
//                    if(m_bExitSet || m_bExitThread) break;
                String strret = HiWifiApi.getIermuIp(hiwifiErmuMac, hiwifiMac, hiwifiSecret);//拿设备ip
                Log.i("strret", "strret:" + strret);
                try {
                    retMsg = new JSONObject(strret);
                    int iCode = retMsg.getInt("code");
                    int iAppCode = retMsg.getInt("app_code");
                    String appData = retMsg.getString("app_data");
                    if (iCode == 0 && iAppCode == 0) {
                        retMsg = new JSONObject(appData);
                        String strIp = retMsg.getString("ip");
                        Log.d("Hiwifi", "devId = "+strId +"---devIP:"+ strIp+"time:"+(System.currentTimeMillis() - st));
                        if (strIp != null && strIp.length() > 6) {
                            CamDev mydev = new CamDev();
                            mydev.setDevType(CamDevType.TYPE_HIWIFI);
                            mydev.setDevIP(strIp);
                            mydev.setBSSID(hiwifiErmuMac);
                            mydev.setDevID(strId);
                            mydev.setDevPwd(strPwd);
                            mydev.setConnectType(ConnectType.BAIDU);
                            if(map.get(strId)==null){
                                m_dev_list.add(mydev);
                                map.put(mydev.getDevID(),true);
                            }
                            Log.i("ipid", mydev.getDevID() + "====" + strIp);
                            if(listener != null) {
                                //返回扫描结果: 有设备、没有设备
                                listener.onDevList(ScanDevMode.HIWIFI, m_dev_list);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    }
//            SystemClock.sleep(1000);
                }

            }
        }).start();
    }


    private int checkDevOne(String strDevID){
        int ret = -1;
        if(m_dev_list==null || m_dev_list.size()==0) return -1;

        int iLen = m_dev_list.size();
        for(int i=0; i<iLen; i++){
            if(strDevID.equals(m_dev_list.get(i).getDevID())) {
                ret = i;
                break;
            }
        }

        return ret;
    }




}
