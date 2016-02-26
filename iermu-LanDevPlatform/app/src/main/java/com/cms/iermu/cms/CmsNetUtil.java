package com.cms.iermu.cms;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;

/**
 * 与设备通信相关函数
 * @author tanhx
 *
 */
public class CmsNetUtil {
    public static final boolean BINDIMSI = false;
    // NAT参数
    private static String NAT_SERVER = "http://www.dvrnat.com";
    private static final int NATSTATUS_IDLE = 0;
    private static final int NATSTATUS_RUNNING = ((1<<0)|(1<<1));
    private static int DATA_PORT = 3356;
    //private static int CMD_PORT = 3357;
    private static int STREAM_VALUE = 2;  // 0-主码流  2-辅码流
    private int m_DvrVer;
    private boolean m_isNatOn;  //网络穿透
    private static final byte iMaxNID = 4; //同时允许的最大nid
    private byte m_iNowNatID;   //网络穿透nid, 需要保持唯一性
    private static boolean[] m_bNatID_status; //当前natid是否使用
    private volatile boolean bLogin = false;
    private volatile boolean bLogining;
    private Socket socketcmd;
    private  DataInputStream discmd;
    private  DataOutputStream doscmd;
    private  String ADDRESS = " ";
    private volatile boolean b_myClose;
    public static boolean bNatInitOn = false;//nat是否初始化
    public static boolean bExitApp = false;
    private static int mLoginRet = 0;
    private boolean m_bFirstLog = true;  //第一次登录dvr，不是重连的
    private boolean m_bLogDvrFail = false;  //如果成功登录后就不应该再出现这个提示
    private static boolean m_bIs2Gnet = false;
    private CmsDev mDevRow;
    int m_iCamInstance;
    private byte[] lockSocket = new byte[0];

    public void closeConn(){
        if(bLogin) bLogin = false;
        if(socketcmd!=null && socketcmd.isConnected()){
            try {
                socketcmd.shutdownInput();
                socketcmd.shutdownOutput();
                socketcmd.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally{
                socketcmd = null;
            }
        }

        try {
            if (null != doscmd) {
                doscmd.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            doscmd = null;
        }

        try {
            if (null != discmd) {
                discmd.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            discmd = null;
        }
    }

    public void setNatConn(boolean isNatConn, CmsDev devrow) {
        m_isNatOn = isNatConn;
        mDevRow = devrow;
        mLoginRet = 0;
        if(mDevRow!=null & mDevRow.nat_server.length()>10){
            NAT_SERVER = "http://" + mDevRow.nat_server;
        }
    }

    public boolean setDevParam(Context context, CmsCmdStruct cmsData) {
        synchronized (lockSocket) {
            boolean ret = false;
			/*for(int i=0; i<argB.length; i++){
				Log.i(TAG, "tanhx debug code: " + i + "=" + argB[i]);
			}*/
            if(!bLogin){
                b_myClose = false;
                bLogin = Network_Start(context);
                if(!bLogin) return false;
            }

            if(socketcmd == null || socketcmd.isClosed()){
                bLogin = false;
                return false;  //没有建立连接
            }

            try {
                if(!Network_Send(cmsData)) {
                    bLogin = false;
                    closeConn();
                    return false;
                }
            } catch (SocketException e1) {
                e1.printStackTrace();
            }
            if(receive_data()){
                if(cmsData.cmsMainCmd==CmsProtocolDef.LAN2_RUNPARA3_SET){
                    if(cmsData.cmsSubCmd==CmsProtocolDef.PARA3_RTMPLOCALPLAY ||
                            cmsData.cmsSubCmd==CmsProtocolDef.PARA3_NASPLAYPARA ||
                            cmsData.cmsSubCmd==CmsProtocolDef.PARA3_NASCONF ||
                            cmsData.cmsSubCmd==CmsProtocolDef.PARA3_HTTPUPDATE) {
                        ret = true;
                    }
                    else{  // 写盘
                        try {
                            CmsCmdStruct cmdSaveMenu = new CmsCmdStruct();
                            cmdSaveMenu.cmsMainCmd = CmsProtocolDef.LAN2_DVR_CTRL;
                            cmdSaveMenu.cmsSubCmd = CmsProtocolDef.DVR2_CTR_SAVEMENU;
                            Network_Send(cmdSaveMenu);
                            Log.d("tanhx", "send write cmd!!");
                        } catch (SocketException e) {
                            Log.d("tanhx", "send write cmd err:" + e.getMessage().toString());
                            e.printStackTrace();
                        }
                        ret = receive_data();
                    }
                }
                else {
                    ret = true;
                }
            }

            return ret;
        }
    }

    public boolean cmsExecCMD(Context context, CmsCmdStruct cmsData) {
        synchronized (lockSocket) {
            boolean ret = false;
	
			/*for(int i=0; i<argB.length; i++){
				Log.i(TAG, "tanhx debug code: " + i + "=" + argB[i]);
			}*/
            if(!bLogin){
                b_myClose = false;
                bLogin = Network_Start(context);
            }

            if(!bLogin || socketcmd == null || socketcmd.isClosed()){
                bLogin = false;
                return false;  //没有建立连接
            }

            try {
                if(!Network_Send(cmsData)){
                    bLogin = false;
                    closeConn();
                    return false;
                }
            } catch (SocketException e1) {
                e1.printStackTrace();
            }

            ret = receive_data();

            return ret;
        }
    }

    /**
     * 发送获取设备信息指令
     * @param cmsData:主命令、子命令、命令参数
     */
    public CmsCmdStruct getDevParam(Context context, CmsCmdStruct cmsData, CmsErr cmserr){
        synchronized (lockSocket) {
            CmsCmdStruct ret = new CmsCmdStruct();
            ret.cmsMainCmd = cmsData.cmsMainCmd;
            ret.cmsSubCmd = cmsData.cmsSubCmd;

            //if(!m_isNatOn) bLogin = false;
            if(!bLogin){
                b_myClose = false;
                bLogin = Network_Start(context);
            }
            if(m_bLogDvrFail) {
                ret.bParams = new byte[]{(byte) 0xff, 0x9};
                cmserr.setErrValue(CmsConstants.CMS_LAN_PWD_ERR, "login fail");
                return ret;
            }
            if(!bLogin) return null;
            if(socketcmd == null || socketcmd.isClosed()){
                cmserr.setErrValue(CmsConstants.CMS_LAN_CONN_FAIL, "conn fail");
                return null;  //没有建立连接
            }

            try {
                if(discmd.available()>0) {
                    discmd.read(new byte[discmd.available()]);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                if(!Network_Send(cmsData)){
                    bLogin = false;
                    closeConn();
                    cmserr.setErrValue(CmsConstants.CMS_LAN_SEND_FAIL, "send data fail");
                    return null;
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
            // 不能用receive_data函数 返回值不一样
            byte[] cmdData = null;
            try{
                int iNum = 0;
                while(discmd.available() < 4){
                    if(b_myClose || iNum > 5000  || bExitApp) {
                        cmserr.setErrValue(CmsConstants.CMS_LAN_RECV_FAIL, "recv data fail");
                        return null;
                    }
                    SystemClock.sleep(20);
                    iNum ++;
                }
                byte[] bT = new byte[4];
                discmd.read(bT, 0, 4);
                Log.d("tanhx", "recv: " + bT[0] + ", " + bT[1] + ", "  + bT[2] + ", "  + bT[3] );
                short sT = CmsUtil.decodeShort(new byte[]{bT[3],bT[2]}, 0, 2);
                if(sT<=0) return null;
                iNum = 0;
                while(discmd.available()<sT){
                    if(b_myClose || iNum > 5000  || bExitApp) {
                        cmserr.setErrValue(CmsConstants.CMS_LAN_RECV_FAIL, "recv data fail");
                        return null;
                    }
                    SystemClock.sleep(20);
                    iNum ++;
                }
                byte[] cmdData1 = new byte[discmd.available()];
                int iLen = cmdData1.length;
                if(iLen > 0){
                    discmd.read(cmdData1);
                    cmdData = new byte[iLen];
                    System.arraycopy(cmdData1, 0, cmdData, 0, iLen);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

            if (cmdData!=null){
                int iHaveHeader = 0;
                int iLen = cmdData.length-iHaveHeader;
                ret.bParams = new byte[iLen];
                System.arraycopy(cmdData, iHaveHeader, ret.bParams, 0, iLen);
                Log.d("tanhx", "ret len is " + ret.bParams.length);
                cmserr.setErrValue(CmsConstants.CMS_LAN_OK, "ok");
            }
            return ret;
        }
    }

    /**
     * 不需要登陆验证时局域网指令
     * @param cmsData:主命令、子命令、命令参数
     */
    public CmsCmdStruct getNoLoginDevParam(CmsCmdStruct cmsData, CmsErr cmserr){
        synchronized (lockSocket) {

            try {
                //m_sConnState = cmsappres.m_context.getString(cmsappres.getRes("tip_conn_connecting", "string"));
                //sendTipMsg();
                //closeConn();
                m_bLogDvrFail = false;

                socketcmd = new Socket();
                socketcmd.setSoTimeout(5000);
                socketcmd.setKeepAlive(true);
                if(mDevRow.url==null || mDevRow.url.equals("")){
                    mDevRow.url = "192.168.1.22";
                    mDevRow.password = "";
                }
                ADDRESS = CmsUtil.getIpAddr(mDevRow.url);
                if(b_myClose || bExitApp){
                    return null;
                }
                Log.d("tanhx", "conn " + ADDRESS);
                socketcmd.connect(new InetSocketAddress(ADDRESS, Integer.parseInt(mDevRow.port)), 10000);
                if(socketcmd.isConnected()){
                    doscmd = new DataOutputStream(socketcmd.getOutputStream());
                    discmd = new DataInputStream(socketcmd.getInputStream());
                }
                else{
                    return null;
                }

                if(b_myClose || bExitApp){
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            CmsCmdStruct ret = new CmsCmdStruct();
            ret.cmsMainCmd = cmsData.cmsMainCmd;
            ret.cmsSubCmd = cmsData.cmsSubCmd;
            try {
                if(!Network_Send(cmsData)){
                    bLogin = false;
                    closeConn();
                    cmserr.setErrValue(CmsConstants.CMS_LAN_SEND_FAIL, "send data fail");
                    return null;
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
            // 不能用receive_data函数 返回值不一样
            byte[] cmdData = null;
            try{
                int iNum = 0;
                while(discmd.available() < 4){
                    if(b_myClose || iNum > 5000  || bExitApp) {
                        cmserr.setErrValue(CmsConstants.CMS_LAN_RECV_FAIL, "recv data fail");
                        return null;
                    }
                    SystemClock.sleep(20);
                    iNum ++;
                }
                byte[] bT = new byte[4];
                discmd.read(bT, 0, 4);
                Log.d("tanhx", "recv: " + bT[0] + ", " + bT[1] + ", "  + bT[2] + ", "  + bT[3] );
                short sT = CmsUtil.decodeShort(new byte[]{bT[3],bT[2]}, 0, 2);
                if(sT<=0) return null;
                iNum = 0;
                while(discmd.available()<sT){
                    if(b_myClose || iNum > 5000  || bExitApp) {
                        cmserr.setErrValue(CmsConstants.CMS_LAN_RECV_FAIL, "recv data fail");
                        return null;
                    }
                    SystemClock.sleep(20);
                    iNum ++;
                }
                byte[] cmdData1 = new byte[discmd.available()];
                int iLen = cmdData1.length;
                if(iLen > 0){
                    discmd.read(cmdData1);
                    cmdData = new byte[iLen];
                    System.arraycopy(cmdData1, 0, cmdData, 0, iLen);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

            if (cmdData!=null){
                int iHaveHeader = 0;
                int iLen = cmdData.length-iHaveHeader;
                ret.bParams = new byte[iLen];
                System.arraycopy(cmdData, iHaveHeader, ret.bParams, 0, iLen);
                Log.d("tanhx", "ret len is " + ret.bParams.length);
                cmserr.setErrValue(CmsConstants.CMS_LAN_OK, "ok");
            }
            return ret;
        }
    }

    private boolean isConnByHttp(){
        boolean isConn = false;

        URL url;
        HttpURLConnection conn = null;
        try {
            url = new URL(NAT_SERVER);
            conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(1000*5);
            if(conn.getResponseCode()!=-1){
                isConn = true;
            }
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            conn.disconnect();
        }
        return isConn;
    }

    //需要区分网络穿透与正常连接
    private synchronized boolean Network_Start(Context context) {
        boolean ret = false;
        try {
            //m_sConnState = cmsappres.m_context.getString(cmsappres.getRes("tip_conn_connecting", "string"));
            //sendTipMsg();
            //closeConn();
            if(b_myClose  || bExitApp){
                return false;
            }
            m_bLogDvrFail = false;

            socketcmd = new Socket();
            socketcmd.setSoTimeout(5000);
            socketcmd.setKeepAlive(true);
            if(mDevRow.url==null || mDevRow.url.equals("")){
                mDevRow.url = "192.168.1.22";
                mDevRow.password = "";
            }
            ADDRESS = CmsUtil.getIpAddr(mDevRow.url);
            if(b_myClose || bExitApp){
                return false;
            }
            Log.d("tanhx", "conn " + ADDRESS);
            socketcmd.connect(new InetSocketAddress(ADDRESS, Integer.parseInt(mDevRow.port)), 10000);
            if(socketcmd.isConnected()){
                doscmd = new DataOutputStream(socketcmd.getOutputStream());
                discmd = new DataInputStream(socketcmd.getInputStream());
            }
            else{
                return false;
            }

            if(b_myClose || bExitApp){
                return false;
            }
            //if(isNatOn) return true;

            // get ver from dvr
            if((!m_isNatOn || mLoginRet==0)&&!"".equals(mDevRow.password)){
                getDvrVer();
                int iTemp = 0;
                while(!ret){
                    if(b_myClose || bExitApp) return ret;
                    logCmsDVR(context, mDevRow.username, mDevRow.password);
                    //getLoginRequest(Integer.parseInt(PASSWORD));//mDevRow.password
                    //SystemClock.sleep(100);
                    ret = receive_data();
                    if(!ret){
                        if(mDevRow.type.equals(CmsConstants.CMS_BD_IERMU)){  // 通过百度认证登录，用默认密码重试一次
                            logCmsDVR_byAp();
                            //logCmsDVR(context, mDevRow.username, cmsNative.getIpcLogPwd("3"));
                            ret = receive_data();
                            if(ret) break;
                        }
                        else if(m_bLogDvrFail){
                            //Log.d("tanhx", "password error, please check!!!");
                            break;
                        }
                        if(iTemp > 0) break;
                        SystemClock.sleep(50);
                        iTemp ++;
                    }
                }
            }
            else{
                ret = true;
            }

        } catch (Exception e) {
            //Log.d("tanhx", e.getMessage());
            e.printStackTrace();
        }
        return ret;
    }

    private synchronized boolean Network_Start(Context context, boolean bLog) {
        boolean ret = false;
        try {
            //m_sConnState = cmsappres.m_context.getString(cmsappres.getRes("tip_conn_connecting", "string"));
            //sendTipMsg();
            //closeConn();
            if(b_myClose  || bExitApp){
                return false;
            }
            m_bLogDvrFail = false;

            socketcmd = new Socket();
            if(mDevRow.url==null || mDevRow.url.equals("")){
                mDevRow.url = "192.168.1.22";
                mDevRow.password = "";
            }
            ADDRESS = CmsUtil.getIpAddr(mDevRow.url);
            if(b_myClose || bExitApp){
                return false;
            }
            socketcmd.connect(new InetSocketAddress(ADDRESS, Integer.parseInt(mDevRow.port)), 3000);
            if(socketcmd.isConnected()){
                doscmd = new DataOutputStream(socketcmd.getOutputStream());
                discmd = new DataInputStream(socketcmd.getInputStream());
            }
            else{

                return false;
            }



            if(b_myClose || bExitApp){
                return false;
            }
            //if(isNatOn) return true;

            // get ver from dvr
            if(!m_isNatOn || mLoginRet==0){
                getDvrVer();
                if(bLog){
                    int iTemp = 0;
                    while(!ret){
                        if(b_myClose || bExitApp) return ret;
                        logCmsDVR(context, mDevRow.username, mDevRow.password);
                        //getLoginRequest(Integer.parseInt(PASSWORD));//mDevRow.password
                        //SystemClock.sleep(100);
                        ret = receive_data();
                        if(!ret){
                            if(m_bLogDvrFail){
                                //Log.d("tanhx", "password error, please check!!!");
                                break;
                            }
                            if(iTemp > 5) break;
                            SystemClock.sleep(50);
                            iTemp ++;
                        }
                    }
                }
                else ret = true;
            }
            else{
                ret = true;
            }

        } catch (Exception e) {
            //Log.d("tanhx", e.getMessage());
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 发送网络命令通用函数
     * @param cmsData：主命令、子命令、参数
     */
    private boolean Network_Send(CmsCmdStruct cmsData) throws SocketException{

        boolean ret = false;
        short length = (short) ((cmsData.bParams==null)? 0 : cmsData.bParams.length);
        byte[] messes = new byte[4];
        messes[0] = (byte) cmsData.cmsMainCmd;
        messes[1] = (byte) cmsData.cmsSubCmd;
        messes[2] = (byte) (0xFF & length);
        messes[3] = (byte) (0xFF & length >> 8);

        if(messes != null){
            try{
                if(socketcmd == null){
                    return ret;
                }
                else if(socketcmd.isClosed()) {
                    Log.d("tanhx", "socket is closed! reconn:" + ADDRESS);
                    socketcmd.connect(new InetSocketAddress(ADDRESS, Integer.parseInt(mDevRow.port)), 10000);
                }
				/*String str = "";
				for(int i=0; i<4; i++){
					str += (",0x" + Integer.toHexString(0xff & messes[i]));
				}*/
                //Log.d("tanhx", "send head:" + str);
                if(doscmd==null) return ret;
                doscmd.write(messes, 0, messes.length);	//包头
                if(length != 0){
                    doscmd.write(cmsData.bParams, 0, cmsData.bParams.length); //内容
					
					/*str = "";
					for(int i=0; i<cmsData.bParams.length; i++){
						str += (",0x" + Integer.toHexString(0xff & cmsData.bParams[i]));
					}*/
                    //Log.d("tanhx", "send data after:" + str);
                }
                ret = true;
                doscmd.flush();
            }
            catch (IOException e){  // 需要处理是否重新设置socket
                e.printStackTrace();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return ret;
    }
    private synchronized boolean receive_data(){
        boolean ret = false;
        byte[] cmdData = null;
        try {
            int iTimeout = 100;
            int iNum = 0;
            if(socketcmd == null){
                return ret;
            }
            else if(socketcmd.isClosed()) {
                socketcmd.connect(new InetSocketAddress(ADDRESS, Integer.parseInt(mDevRow.port)), 10000);
            }
            while(discmd.available() <= 0){
                if(b_myClose || iNum > iTimeout || bExitApp) return ret;
                SystemClock.sleep(100);
                iNum ++;
            }
            byte[] bT = new byte[4];
            discmd.read(bT, 0, 4);
            short sT = CmsUtil.decodeShort(new byte[]{bT[3],bT[2]}, 0, 2);
            if(sT > 0){
                iNum = 0;
                while(discmd.available()<sT){
                    if(b_myClose || iNum > iTimeout || bExitApp) return ret;
                    SystemClock.sleep(100);
                    iNum ++;
                }
                byte[] cmdData1 = new byte[discmd.available()];
                if(cmdData1.length > 0){
                    discmd.read(cmdData1);
                }
                cmdData = new byte[sT+4];
                System.arraycopy(bT, 0, cmdData, 0, 4);
                System.arraycopy(cmdData1, 0, cmdData, 4, sT);
            }
            else{
                cmdData = new byte[4];
                System.arraycopy(bT, 0, cmdData, 0, 4);
            }

            if(cmdData.length > 0){
                // 判断接收数据是否正确
                Log.d("tanhx", "len=" + sT + "recv=" + cmdData[0] + "," + cmdData[1] + "," + cmdData[2] + "," + cmdData[3]);
                if(sT<0){
                    if(cmdData[0]==CmsProtocolDef.LAN2_RUNPARA3_SET ||
                            cmdData[0]==CmsProtocolDef.LAN2_RUNPARA3_GET) {
                        return false;
                    }
                    else if(cmdData[3]!=0){
                        return false;
                    }
                }
                switch(cmdData[0]){
                    case 0x5:  // get dvr version
                        if(cmdData[1]!=17 || cmdData[2]==0xff) break;
                        int dataLen = cmdData[3];
                        m_DvrVer = ((CmsUtil.ByteArrayToint(new byte[]{
                                cmdData[7], cmdData[6], cmdData[5], cmdData[4]
                        }))>>12) & 0xffff;
                        //Log.d("tanhx", "dvr version is " + Integer.toHexString(m_DvrVersion));
                        //m_sConnState = "ver is " + Integer.toHexString(m_DvrVersion);
                        //sendTipMsg();
                        switch(dataLen){
                            case 4:
                            case 8:  //不支持独立对讲通道
                                //m_bTalkAlone = false;
                                break;
                            case 12:  //判断是否支持独立对讲
                                ret = true;
                                //m_bTalkAlone = (0!=(cmdData[15]&0x3))? true : false;
                                //Log.d("tanhx", "talk alone is " + m_bTalkAlone + "!!!");
                                break;
                            default:
                                break;
                        }

                        break;
                    case 0x45:  // login   69, 6, -1, 9:密码错误
                        if(cmdData.length>=8){
                            mLoginRet = CmsUtil.ByteArrayToint(new byte[]{cmdData[7], cmdData[6], cmdData[5], cmdData[4]});
                            m_bFirstLog = false;
                            //m_sConnState = "login dvr ok!";
                            //m_sConnState = cmsappres.m_context.getString(cmsappres.getRes("tip_conn_log_dvr_ok", "string"));
                            //sendTipMsg();
                            ret = true;
                        }
                        else if(cmdData.length == 4 && cmdData[2]==-1 && cmdData[3]==9){  // pwd error
                            mLoginRet = 0xff09;  //
                            //m_sConnState = "login dvr fail!";
                            //m_sConnState = cmsappres.m_context.getString(cmsappres.getRes("tip_conn_log_dvr_failed", "string"));
                            //sendTipMsg();
                            if(m_bFirstLog){
                                m_bLogDvrFail = true;
                            }
                            else{
                                m_bLogDvrFail = false;
                            }
                        }
                        break;
                    case 0x46:  // device info
                        if(cmdData.length<40) break;
                        //GetZSDeviceInfo(cmdData);   // 需要区分nat
                        ret = true;
                        break;
                    case 0x0d:  // send ticket
                        //Log.d(TAG, "tanhx debug code: do send ticket!!!!!");
                        ret = true;
                        break;
                    case 0x4a:  // get parameter info
                        switch(cmdData[1]){
                            case 1:

                                break;
                            case 2:

                                break;
                            case 3: // get version info
                                //GetCmsVerInfo(cmdData);
                                break;
                            case CmsProtocolDef.PARA3_NASFIND:

                                ret = true;
                                break;
                        }
                        break;
                    case 0x4b:  // set parameter info
                        ret = true;
                        break;
                    case 0x41:
                        ret = true;
                        break;
                    case 0x48:
                        ret = true;
                        break;
                    default:
                        ret = true;
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return ret;
    }
    private int getDvrVer(){
        int ret = 0;
        try{
            CmsCmdStruct cmdVer = new CmsCmdStruct();
            cmdVer.cmsMainCmd = 0x5;
            cmdVer.cmsSubCmd = 17;
            Network_Send(cmdVer);
        }
        catch(SocketException e){
            e.printStackTrace();
        }
        if(receive_data()){
            ret = m_DvrVer;
        }

        return ret;
    }

    /**
     *
     * @param c
     * @param bLog 是否登录
     * @return
     */
    int getDvrVer(Context c, boolean bLog){
        int ret = 0;
        if(!bLog) {
            Network_Start(c, bLog);
        }
        try{
            CmsCmdStruct cmdVer = new CmsCmdStruct();
            cmdVer.cmsMainCmd = 0x5;
            cmdVer.cmsSubCmd = 17;
            Network_Send(cmdVer);
        }
        catch(SocketException e){
            e.printStackTrace();
        }
        if(receive_data()){
            ret = m_DvrVer;
        }

        return m_DvrVer;
    }

    private int getDvrVersion() {
        return m_DvrVer==-1? getDvrVer() : m_DvrVer;
    }

    private int getSession(){
        return 0;//mLoginRet;
    }

    private void getLoginRequest(int intPwd) {
        byte[] data = new byte[4];
        data[0] = (byte) (0xFF & intPwd >> 24);
        data[1] = (byte) (0xFF & intPwd >> 16);
        data[2] = (byte) (0xFF & intPwd >> 8);
        data[3] = (byte) (0xFF & intPwd >> 0);
        try {
            CmsCmdStruct cmdVer = new CmsCmdStruct();
            cmdVer.cmsMainCmd = CmsProtocolDef.LAN2_AUTH;
            cmdVer.cmsSubCmd = 1;
            cmdVer.bParams = data;
            Network_Send(cmdVer);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    // login cms dvr, 如果知道socket登录密码，不需要处理
    // 登录帐号的uid作为登录密码时，uid需要异或(0x81+i)  by tanhx 20141020
    private void logCmsDVR(Context context, String strUser, String strPwd){
        if(m_DvrVer>0x6117){ // pwd=cms3518 传进密码为“”时，也按照配置模式密码，有线配置用
            byte[] bPwd = strPwd.getBytes();
            Log.d("cms", "cms=" + strPwd);
            if(mDevRow.type.equals(CmsConstants.CMS_BD_IERMU)){  // 通过百度认证登录，需要进行处理
                Log.d("cms", "bd auth! user=" + strUser);
                for(int i=0; i<bPwd.length; i++){
                    bPwd[i] = (byte) (bPwd[i]^((byte)(0x81+i)));
                }
            }
            byte[] bUser = strUser.getBytes();
            byte[] bParams = new byte[40];
            for(int i=0; i<bParams.length; i++){
                bParams[i] = 0x0;
            }
            System.arraycopy(bUser, 0, bParams, 0, bUser.length);
            System.arraycopy(bPwd, 0, bParams, 20, bPwd.length);
            if(BINDIMSI){	// 山东电信根据imsi登录dvr
                byte[] bimsi = CmsUtil.getIMSI(context).getBytes();
                System.arraycopy(bimsi, 0, bParams, 40, bimsi.length);
            }
            try {
                CmsCmdStruct cmdVer = new CmsCmdStruct();
                cmdVer.cmsMainCmd = CmsProtocolDef.LAN2_AUTH;
                cmdVer.cmsSubCmd = BINDIMSI? 0x7 : 0x6;
                cmdVer.bParams = new byte[bParams.length];
                System.arraycopy(bParams, 0, cmdVer.bParams, 0, bParams.length);
                Log.d("cms", "send user=" + new String(cmdVer.bParams, 0, 20).trim());
                Network_Send(cmdVer);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        else if(m_DvrVer>0x6100 || m_isNatOn || strUser!=null){//6
            byte[] bUser = strUser.getBytes();
            byte[] bPwd = strPwd.getBytes();
            byte[] bParams = new byte[40];
            for(int i=0; i<bParams.length; i++){
                bParams[i] = 0x0;
            }
            System.arraycopy(bUser, 0, bParams, 0, bUser.length);
            System.arraycopy(bPwd, 0, bParams, 20, bPwd.length);
            if(BINDIMSI){	// 山东电信根据imsi登录dvr
                byte[] bimsi = CmsUtil.getIMSI(context).getBytes();
                System.arraycopy(bimsi, 0, bParams, 40, bimsi.length);
            }
            try {
                CmsCmdStruct cmdVer = new CmsCmdStruct();
                cmdVer.cmsMainCmd = CmsProtocolDef.LAN2_AUTH;
                cmdVer.cmsSubCmd = BINDIMSI? 0x7 : 0x6;
                cmdVer.bParams = bParams;
                Network_Send(cmdVer);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        else if(m_DvrVer>0x6012){//5

        }
        else if(m_DvrVer>0x6011){//4

        }
        else{//1   数字密码
            getLoginRequest(Integer.parseInt(strPwd));
        }
    }

    // ipc配置模式登录方式  added by tanhx 20150311
    private void logCmsDVR_byAp(){
        if(m_DvrVer>0x6117){ // pwd=cms3518 传进密码为“”时，也按照配置模式密码，有线配置用
            String strPwd = cmsNative.getIpcLogPwd("3");
            Log.d("cms", "cms=" + strPwd);
            byte[] bPwd = strPwd.getBytes();
            byte[] bUser = "88888888".getBytes();
            byte[] bParams = new byte[40];
            for(int i=0; i<bParams.length; i++){
                bParams[i] = 0x0;
            }
            System.arraycopy(bUser, 0, bParams, 0, bUser.length);
            System.arraycopy(bPwd, 0, bParams, 20, bPwd.length);
            try {
                CmsCmdStruct cmdVer = new CmsCmdStruct();
                cmdVer.cmsMainCmd = CmsProtocolDef.LAN2_AUTH;
                cmdVer.cmsSubCmd = 0x6;
                cmdVer.bParams = new byte[bParams.length];
                System.arraycopy(bParams, 0, cmdVer.bParams, 0, bParams.length);
                Network_Send(cmdVer);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

    }

    private  int Int2inthtonl(int i) {
        byte[] tran = new byte[4];
        int net = 0x0;
        tran[0] = (byte) (i & 0xFF);
        tran[1] = (byte) ((0xFF00 & i) >> 8);
        tran[2] = (byte) ((0xFF0000 & i) >> 16);
        tran[3] = (byte) ((0xFF000000 & i) >> 24);
        net = ((0xFF & tran[0]) << 24) + ((0xFF & tran[1]) << 16) + ((0xFF & tran[2]) << 8) + tran[3];
        return net;
    }

    private byte[] intArrayTobyteArray(int[] intArray) {
        byte[] byteArray = new byte[4 * intArray.length];
        int intArraylen = intArray.length;
        int byteArraylen = byteArray.length;
        if ((intArraylen != 0) && (byteArraylen != 0) && (intArraylen * 4 == byteArraylen)) {
            for (int j = 0; j < intArraylen; j++) {
                byteArray[(j * 4)] = (byte) (0xFF & intArray[j]);
                byteArray[(1 + j * 4)] = (byte) (0xFF & intArray[j] >> 8);
                byteArray[(2 + j * 4)] = (byte) (0xFF & intArray[j] >> 16);
                byteArray[(3 + j * 4)] = (byte) (0xFF & intArray[j] >> 24);
            }
        }
        return byteArray;
    }

    private  byte[] BigEdition(int i) {
        byte[] trans = new byte[4];
        trans[3] = (byte) (i & 0xFF);
        trans[2] = (byte) ((0xFF00 & i) >> 8);
        trans[1] = (byte) ((0xFF0000 & i) >> 16);
        trans[0] = (byte) ((0xFF000000 & i) >> 24);
        byte[] bytes = new byte[4];
        bytes[0] = trans[3];
        bytes[1] = trans[2];
        bytes[2] = trans[1];
        bytes[3] = trans[0];
        return bytes;
    }

    // 处理日志上传
    public static void uploadLog(String strDevID, short sType, String strData){
        Socket socket = new Socket();
        DataOutputStream dosLog;
        String hostUrl = "www.iermu.com";
        int iPort = 3399;
        try {
            if(strData==null) strData = "0";
            iermuLogStruct iermuLog = new iermuLogStruct();
            iermuLog.sType = sType;
            iermuLog.sData = strData;
            iermuLog.iTimestamp = (int) (System.currentTimeMillis()/1000);
            iermuLog.sMac = "";//cmsUtils.getLocalMacAddress();
            byte[] b1 = CmsUtil.htons(iermuLog.sHead);
            byte[] b2 = CmsUtil.htons(iermuLog.sVer);
            byte[] b3 = CmsUtil.htons(iermuLog.sType);
            byte[] b8 = iermuLog.sData.getBytes();
            iermuLog.sLen = (short) b8.length;
            if(iermuLog.sLen>512) iermuLog.sLen=512;
            byte[] b4 = CmsUtil.htons(iermuLog.sLen);
            byte[] b5 = (byte[]) (strDevID==null? new byte[]{0,0,0,0,0,0} : CmsMenu.getMacByDevID(strDevID));
            //cmsMenu.getDevIDByMac(b5);
            byte[] b6 = CmsUtil.htonl(iermuLog.iTimestamp);
            byte[] bHead = new byte[18];
            System.arraycopy(b1, 0, bHead, 0, b1.length);
            System.arraycopy(b2, 0, bHead, 2, b2.length);
            System.arraycopy(b3, 0, bHead, 4, b3.length);
            System.arraycopy(b4, 0, bHead, 6, b4.length);
            System.arraycopy(b5, 0, bHead, 8, b5.length);
            System.arraycopy(b6, 0, bHead, 14, b6.length);
            iermuLog.iChecksum = cmsNative.cmsCrcGet(bHead, bHead.length, 0x05a23c39);
            byte[] b7 = CmsUtil.htonl(iermuLog.iChecksum);
            byte[] bLog = new byte[18+4+iermuLog.sLen];
            System.arraycopy(bHead, 0, bLog, 0, bHead.length);
            System.arraycopy(b7, 0, bLog, 18, b7.length);
            System.arraycopy(b8, 0, bLog, 22, iermuLog.sLen);
            InetAddress[] ip = InetAddress.getAllByName(hostUrl);
            String strIP = ip[0].getHostAddress();
            socket.connect(new InetSocketAddress(strIP, iPort), 3000);
            if(!socket.isConnected()) return;
            dosLog = new DataOutputStream(socket.getOutputStream());
			/*Log.d("tanhx", "upload log len = " + bLog.length);
			for(int i=0; i<bLog.length; i++){
				Log.d("tanhx", "blog["+i+"]=" + Integer.toHexString(bLog[i]));
			}*/
            dosLog.write(bLog, 0, bLog.length);	//包头
            dosLog.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if(socket.isConnected()) socket.close();
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    请按如下的编码空间(2字节)安排各自的日志事件类型编码。
    设备：             0-3999
    Android app：4000-5999
    ios app：         6000-7999
    web：              8000-9999
    保留：             10000-65535
    修改格式定义如下:
    short: 固定为0xfffa。
    short: 日志版本。1
    short: 类型，按照丰总邮件中的区间自行分类。
    short: 自定义数据长度（不包括已经定义的头部）。
    char[6]: 设备端，6个字节的mac地址；
    int: 时间戳，从1970至今的秒数。
    int: 校验码。
    char[?]: 自定义数据，最大长度为512字节。
    注：以上均为网络字节序。
    > 上传地址为www.iermu.com ，端口为3399。
    */
    public static class iermuLogStruct{
        public short sHead = (short) 0xfffa;
        public short sVer = 1;
        public short sType;  // 4000-5999   4000-4999:设备相关操作  5000-5999：百度帐号相关操作
        public short sLen;
        public String sMac;   // 6字节
        public int iTimestamp;
        public int iChecksum;
        public String sData;  // 最多512
    }
}
