package com.iermu.client.business.impl.setupdev.setup;

import android.os.SystemClock;
import android.util.Log;

import com.cms.iermu.cms.CmsCmdStruct;
import com.cms.iermu.cms.CmsDev;
import com.cms.iermu.cms.CmsErr;
import com.cms.iermu.cms.CmsMenu;
import com.cms.iermu.cms.CmsNetUtil;
import com.iermu.client.ErmuApplication;
import com.iermu.client.business.impl.setupdev.HiWifiApi;
import com.iermu.client.model.CamDev;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.lan.utils.LanUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zsj on 15/12/4.
 * 极路由快连，先让设备上线，然后拿ip
 */
public class HiwifiConnectRunnable extends Thread{

    private HiWifiConnectListener listener;
    String hiwifiErmuMac;

    public HiwifiConnectRunnable(String hiwifiErmuMac) {
        this.hiwifiErmuMac       = hiwifiErmuMac;
        getHiWifiIpThread(hiwifiErmuMac);
    }

    public HiwifiConnectRunnable setOnHiWifiConnectListener(HiWifiConnectListener listener) {
        this.listener = listener;
        return this;
    }

    public void onHiWifiConnected(CamDev dev) {

        if(listener == null) return;
        listener.onHiWifiConnected(dev);
    }

    public interface HiWifiConnectListener {

        public void onHiWifiConnected(CamDev dev);

    }

    @Override
    public void run() {
        super.run();
        getHiWifiIpThread(hiwifiErmuMac);
    }


    private  void getHiWifiIpThread(final String hiwifiErmuMac) {

        final String strId = CmsMenu.getDevIDByMac(hiwifiErmuMac);
        String ip = "";
        final long st1 = System.currentTimeMillis();


        new Thread(new Runnable() {
            public void run() {
                try{
                    CamDev dev = new CamDev();
                    int connectType = -1;
                    String[] retMac = HiWifiApi.getMacToken();
                    if(retMac!=null) {
                        Log.d("mac", "mac:" + retMac[0] + ", token:" + retMac[1]);
                        String hiwifiMac = retMac[0];
//                    String m_hiwifiToken = retMac[1];
                        String hiwifiSecret = HiWifiApi.getSecret(retMac[0], retMac[1]);
                        Log.d("Hiwifi", "secret=" + hiwifiMac);
                        if(hiwifiSecret!=null) {
                            while (System.currentTimeMillis()-st1<60*1000){
                                String addRet = HiWifiApi.addIermu(hiwifiErmuMac, hiwifiMac, hiwifiSecret);//让设备上网
                                if(addRet !=null )  {
                                    JSONObject retMsg;
                                    try {
                                        retMsg = new JSONObject(addRet);
                                        int iCode = retMsg.getInt("code");
                                        int iAppCode = retMsg.getInt("app_code");
                                        if (iCode == 0 && iAppCode == 0) {
                                            break;

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                SystemClock.sleep(500);
                            }
                            String strIp="";
                            while (System.currentTimeMillis()-st1<60*1000){
                                String strret = HiWifiApi.getIermuIp(hiwifiErmuMac, hiwifiMac, hiwifiSecret);//拿设备ip
                                if(strret!=null){
                                    try {
                                        JSONObject retMsg = new JSONObject(strret);
                                        int iCode = retMsg.getInt("code");
                                        int iAppCode = retMsg.getInt("app_code");
                                        String appData = retMsg.getString("app_data");
                                        if (iCode == 0 && iAppCode == 0) {
                                            retMsg = new JSONObject(appData);
                                            strIp = retMsg.getString("ip");
                                            Log.d("Hiwifi", "devId = "+strId +"---devIP:"+ strIp);
                                            if (strIp != null && strIp.length() > 6) {
                                                break;
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                SystemClock.sleep(500);
                            }
                            dev.setDevIP(strIp);
                            while(System.currentTimeMillis()-st1<60*1000){
                                LanUtil util  = new LanUtil();
                                CmsDev cmsDev = util.getCmsDev(strIp, "");
                                CmsNetUtil netUtil = new CmsNetUtil();
                                netUtil.setNatConn(false, cmsDev);
                                CmsCmdStruct cmsData = new CmsCmdStruct();
                                cmsData.cmsMainCmd = 5;
                                cmsData.cmsSubCmd = 18;
                                CmsCmdStruct cmsGet = netUtil.getDevParam(ErmuApplication.getContext(),cmsData, new CmsErr(-1, "init"));
//                                CmsCmdStruct cmsGet = netUtil.getNoLoginDevParam(cmsData, new CmsErr(-1, "init"));
                                if(cmsGet!=null&&cmsGet.bParams!=null){
                                    int cloud = cmsGet.bParams[10];
                                    if(cloud == 0) {
                                        connectType = ConnectType.BAIDU;
                                    } else if(cloud == 1) {
                                        connectType = ConnectType.LINYANG;
                                    } else if(cloud == 50) {
                                        connectType = ConnectType.OTHER;
                                    }else{
                                        connectType = -1;
                                    }
                                    break;
                                }
                                SystemClock.sleep(500);
                            }
                            dev.setConnectType(connectType);

                            onHiWifiConnected(dev);
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
