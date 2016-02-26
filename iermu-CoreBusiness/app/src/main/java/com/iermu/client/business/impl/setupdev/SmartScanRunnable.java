package com.iermu.client.business.impl.setupdev;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

import com.cms.iermu.cms.CmsMenu;
import com.cms.iermu.cmsUtils;
import com.iermu.client.model.CamDev;
import com.iermu.client.model.constant.CamDevType;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.model.constant.ScanDevMode;
import com.iermu.client.util.Logger;
import com.lyy.lyyapi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zsj on 15/10/27.
 */
public class SmartScanRunnable extends Thread{



    private Context context;
    private String baiduUID;//m_baiduUser.strUID
    private String camDevID;
    private List<CamDev> m_dev_list;
    private boolean threadExit=false;
    private OnScanCamDevListener listener;


    private String m_strWifiPwd;//wifi密码
    private String m_strWifiUser;//wifi用户名
    private int m_iConnWifiType;//wifi类型
    private String m_strSSID;//wifi名称
    //private boolean isInterrupted = false;
    ExecutorService mSocketThreadPool;
    private long timeStart;

    public SmartScanRunnable(Context context, String m_strSSID, String m_strWifiPwd, int m_iConnWifiType, String m_strWifiUser){
        this.context = context;
        this.m_strSSID = m_strSSID;
        this.m_strWifiPwd = m_strWifiPwd;
        this.m_iConnWifiType = m_iConnWifiType;
        this.m_strWifiUser = m_strWifiUser;
        this.m_dev_list = new ArrayList<CamDev>();
    }

    public SmartScanRunnable setConfigWifi(String m_strSSID, String m_strWifiPwd, int m_iConnWifiType, String m_strWifiUser) {
        this.m_strSSID = m_strSSID;
        this.m_strWifiPwd = m_strWifiPwd;
        this.m_iConnWifiType = m_iConnWifiType;
        this.m_strWifiUser = m_strWifiUser;
        this.m_dev_list = new ArrayList<CamDev>();
        return this;
    }

    /**
     * 查找指定设备的ID
     * @param camDevID
     */
    public SmartScanRunnable setFindDevID(String camDevID) {
        this.camDevID = camDevID;
        return this;
    }

    /**
     * 重新启动Smart扫描
     * @return
     */
    public SmartScanRunnable restart() {
        if(threadExit) return this;
        if((System.currentTimeMillis()-timeStart)>=100*1000) {
            try {
                if(serverSocket!=null) serverSocket.close();
                if(mSocketThreadPool!=null)mSocketThreadPool.shutdown();
                serverSocket = null;
                mSocketThreadPool = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.run();
        } else {
            timeStart = System.currentTimeMillis();
        }
        return this;
    }

    public SmartScanRunnable setListener(OnScanCamDevListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        //this.isInterrupted = true;
        this.threadExit = true;
        try {
            if(serverSocket!=null) serverSocket.close();
            if(mSocketThreadPool!=null)mSocketThreadPool.shutdown();
            serverSocket = null;
            mSocketThreadPool = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        short iport = 3357;
        while(!startSocket(iport)){
            if(!threadExit){
                SystemClock.sleep(100);
                iport++;
            }
            else {
                Logger.i("SmartScanRunnable", "start smart connecton fail!");
                return;
            }
        }
        Logger.i("SmartScanRunnable", "start smart connecton success!");

        byte bAuthMode = 0;
        String strPwd = m_strWifiPwd;
        if(m_iConnWifiType==2){// wep 需要特殊处理  1<<6  是否直接连接设置wifi
            bAuthMode |= (byte) (1<<7);
            int iLen = m_strWifiPwd.length();
            if(iLen==5 || iLen==13 || iLen==16){
                strPwd = "\"" + m_strWifiPwd + "\"";
            }
        }
        else if(m_iConnWifiType==4){ // eap
            bAuthMode |= (1<<1);
        }

        getSocketDataThread();

        getDevFromIermuThread();

        lyyapi lyyInst = new lyyapi();
        int iIP = WifiNetworkManager.getInstance(context).getIpAddress();

        if(m_iConnWifiType!=4) m_strWifiUser = null;
        String strUser = TextUtils.isEmpty(m_strWifiUser) ? "" : m_strWifiUser;
        Long lt = System.currentTimeMillis();
//        Log.i("smartConfig", "socket send!"+m_strSSID+"-"+strPwd+"-"+strUser+"-"+iIP+"-"+iport);
        timeStart = System.currentTimeMillis();
        while (!threadExit && (System.currentTimeMillis()-timeStart)<100*1000) {
            if (System.currentTimeMillis() - lt > 200) {
                lyyInst.cmsMSendWifiInfo(m_strSSID, strPwd, bAuthMode, strUser, iIP, iport);
                Logger.i("SmartScanRunnable", "socket send!" + m_strSSID + "-" + strPwd + "-" + strUser + "-" + iIP + "-" + iport);
                lt = System.currentTimeMillis();
            }
            SystemClock.sleep(200);  // 间隔1秒
        }
        Logger.i("SmartScanRunnable", "Smart scan exit!");
    }


    ServerSocket serverSocket;
    private boolean startSocket(int iPort){
        boolean ret = false;
        try {
            serverSocket = new ServerSocket(iPort);
            ret = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private void getSocketDataThread(){
        new Thread(new Runnable() {
            public void run() {
                getSocketData();
            }
        }).start();
    }

    private void getSocketData(){
        mSocketThreadPool = Executors.newFixedThreadPool(10);
        while (/*!m_bStartSetDev && !m_bExitSet &&*/ !threadExit) {  // 注意退出条件
            try {
                if(serverSocket!=null&&!serverSocket.isClosed()){
                    Logger.i("SmartScanRunnable", "socket accept start!");
                    final Socket socket = serverSocket.accept();
                    Logger.i("SmartScanRunnable", "socket accept end!");
                    mSocketThreadPool.execute(new Runnable() {

                        @Override
                        public void run() {
                            handleSocket(socket);  // 需要起线程处理
                        }
                    });
                }
                else{
                    SystemClock.sleep(40);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            if(serverSocket!=null) serverSocket.close();
            if(mSocketThreadPool!=null) mSocketThreadPool.shutdown();
            serverSocket = null;
            mSocketThreadPool = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSocket(Socket socket){
        try {
            boolean bErr = false;
            DataInputStream discmd = new DataInputStream(socket.getInputStream());
            DataOutputStream doscmd = new DataOutputStream(socket.getOutputStream());
            long st = System.currentTimeMillis();
            while(discmd.available()<32){ //
                if(System.currentTimeMillis()-st>5000 /*|| m_bStartSetDev || m_bExitSet*/ || threadExit) {
                    bErr = true;
                    break;
                }
                SystemClock.sleep(20);
            }
            if(!bErr) {
                byte[] buffer = new byte[discmd.available()];
                int iret = discmd.read(buffer);
                doscmd.write(new byte[1]);
                doscmd.flush();
                handleBuffer(buffer);  // 需要同步控制
            }

            discmd.close();
            doscmd.close();
            discmd = null;
            doscmd = null;
        } catch (IOException e) {
            //e.printStackTrace();
        }
        try {
            socket.shutdownInput();
            socket.shutdownOutput();
            socket.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        Logger.i("SmartScanRunnable", "socket close!");
    }

    // 下标22表示云平台类型
    private void handleBuffer(byte[] buffer){
        if(buffer==null || buffer.length<32 /*|| m_bStartSetDev || m_bExitSet*/ || threadExit) return;
        int[] ip = new int[4];
        ip[0] = buffer[0]&0xff;
        ip[1] = buffer[1]&0xff;
        ip[2] = buffer[2]&0xff;
        ip[3] = buffer[3]&0xff;

        String strMAC = "";
        int[] bnatid = new int[6];
        for (int i = 4; i < 10; i++) {
            bnatid[i - 4] = buffer[i]&0xff;
            String sTemp = Integer.toHexString(0xff & buffer[i]);
            sTemp = sTemp.length() == 1 ? "0" + sTemp : sTemp;
            strMAC += (sTemp + ":");
        }
        Logger.i("SmartScanRunnable", "strMAC"+strMAC);
        strMAC = strMAC.substring(0, strMAC.lastIndexOf(":"));
        String strDevIP = ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3];
        String strDevID = CmsMenu.getDevIDByMac(strMAC, false);
        String strPwd = new String(buffer, 14, 8);
        long scantime = new Date().getTime();
        CamDev dev = new CamDev();
        dev.setDevType(CamDevType.TYPE_SMART);
        dev.setDevPwd(strPwd);
        dev.setDevIP(strDevIP);
        dev.setDevID(strDevID);
        dev.setScanTime(scantime);
        Logger.i("SmartScanRunnable", "strDevID:"+strDevID+" strDevIP:"+strDevIP+" strPwd:"+strPwd+" scantime:"+scantime);

        int cloud = buffer[22];
        int connectType = ConnectType.BAIDU;
        if(cloud == 0) {
            connectType = ConnectType.BAIDU;
        } else if(cloud == 1) {
            connectType = ConnectType.LINYANG;
        } else if(cloud == 50) {
            connectType = ConnectType.OTHER;
        }
        dev.setConnectType(connectType);
        //dev.setConnectType(lyyAP ? ConnectType.LINYANG : ConnectType.BAIDU);
        int iret = checkDevOne(dev.getDevID());
        if(iret<0){
            m_dev_list.add(dev);
            Logger.i("SmartScanRunnable", "ip=" + strDevIP + ", devid=" + strDevID + ", pwd=" + strPwd + ", platform=" + buffer[22]);
        }
        else{ // 更新dev信息
            m_dev_list.get(iret).setDevType(CamDevType.TYPE_SMART);
            m_dev_list.get(iret).setDevIP(strDevIP);
            m_dev_list.get(iret).setDevPwd(strPwd);
            m_dev_list.get(iret).setScanTime(scantime);
        }

        if(listener != null) {
            //返回扫描结果: 有设备、没有设备
            listener.onDevList(ScanDevMode.SMART, m_dev_list);
        }
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

    private void getDevFromIermuThread(){
        new Thread(new Runnable() {

            @Override
            public void run() {
                getDevFromIermu();
            }
        }).start();
    }

    private void getDevFromIermu(){
        Socket socket = new Socket();
        DataInputStream discmd = null;
        DataOutputStream doscmd;
        String hostUrl = "www.iermu.com";
        int iPort = 3398;
        try {
            socket.setSoTimeout(3000);
            socket.setKeepAlive(true);
            InetAddress[] ip = InetAddress.getAllByName(hostUrl);
            String strIP = ip[0].getHostAddress();

            while(!socket.isConnected()){
                if(!threadExit){
                    SystemClock.sleep(100);
                    socket.connect(new InetSocketAddress(strIP, iPort), 3000);
                }
                else {
                    Logger.i("SmartScanRunnable", "start smart connecton fail!");
                    return;
                }
            }

//            socket.connect(new InetSocketAddress(strIP, iPort), 3000);
            if(!socket.isConnected()) return;
            doscmd = new DataOutputStream(socket.getOutputStream());
            discmd = new DataInputStream(socket.getInputStream());
            byte[] bCmd = new byte[]{1};
            while(true){
                if(threadExit) break;
                if(discmd.available()>0) discmd.read(new byte[discmd.available()]);
                doscmd.write(bCmd);
                doscmd.flush();
                while(discmd.available() < 4){
                    if(threadExit) break;
                    SystemClock.sleep(10);
                }
//				Log.d(TAG, "iermu server getdata!!!");
                if(threadExit) break;
                byte[] bHead = new byte[8];
                discmd.read(bHead, 0, 4);
                int iCount = cmsUtils.ByteArrayToint(bHead, 0, true);
                int iLen = 0;
                if(iCount>=0){
                    while(discmd.available() < 4){
                        if(threadExit) break;
                        SystemClock.sleep(10);
                    }
                    if(threadExit) break;
                    discmd.read(bHead, 4, 4);
                    iLen = cmsUtils.ByteArrayToint(bHead, 4, true);
                }
                if(iCount>0 && iLen>0){
                    for(int i=0; i<iCount; i++){
                        byte[] buffer = new byte[iLen];
                        while(discmd.available() < iLen){
                            if(threadExit) break;
                            SystemClock.sleep(10);
                        }
                        if(threadExit) break;
                        discmd.read(buffer, 0, iLen);
                        Logger.i("SmartScanRunnable", "getDevFromIermu"+buffer.toString());
                        handleBuffer(buffer);
                    }
                }
                SystemClock.sleep(1000);
            }

            discmd.close();
            discmd = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if(socket.isConnected()) socket.close();
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
//		Log.d(TAG, "exit iermu server thread!");
    }

}
