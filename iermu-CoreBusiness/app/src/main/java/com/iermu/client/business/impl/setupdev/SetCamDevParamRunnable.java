package com.iermu.client.business.impl.setupdev;

/**
 * 设置摄像头配置参数
 *
 * Created by wcy on 15/6/25.
 */
public class SetCamDevParamRunnable extends Thread {
    @Override
    public void run() {

    }

//    private Context mContext;
//    private CmsNetUtil mCmsNet;
//
//    public SetCamDevParamRunnable(Context context) {
//        this.mContext = context;
//        this.mCmsNet = new CmsNetUtil();
//        //TODO 创建环境
//        //this.mCmsNet.setNatConn();
//    }
//
//    @Override
//    public void run() {
//        boolean ret = false;
////        m_bAuth = true;
////        m_bNewToken = true;
//        CmsCmdStruct cmsVerInfo = new CmsCmdStruct();
//        cmsVerInfo.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_GET;
//        cmsVerInfo.cmsSubCmd = CmsProtocolDef.PARA3_PRODUCTTYPE;
//        CmsErr cmserr = new CmsErr(-1, "init");
//        CmsCmdStruct cmsDevInfo = mCmsNet.getDevParam(mContext, cmsVerInfo, cmserr);
////        if(m_bExitSet) return ret;
//        Log.d("tanhx", "set dev params start!!!");
//        // 得到device id
//        if(cmsDevInfo==null || cmsDevInfo.bParams.length<32) { // 不支持新协议
////            m_bNewToken = false;
//            cmsVerInfo.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_GET;
//            cmsVerInfo.cmsSubCmd = CmsProtocolDef.PARA3_VERINFO;
//            cmserr = new CmsErr(-1, "init");
//            cmsDevInfo = mCmsNet.getDevParam(mContext, cmsVerInfo, cmserr);
////            if(m_bExitSet) return ret;
//            // 得到device id
//            if(cmsDevInfo==null || cmsDevInfo.bParams.length<78) { // 错误, 重连设备ap
////                h.sendMessage(h.obtainMessage(MSG_GET_IPC_DATA_FAIL));
////                m_bGetDevID = false;
////                Log.d("tanhx", "get dev params err");
////                return ret;
//            }
//            // 检测是否是注册的设备
//            String strTemp = CmsMenu.getDevID(cmsDevInfo.bParams);
//            if(strTemp==null || !strTemp.equals(m_strIpcDevID)){
////                h.sendMessage(h.obtainMessage(MSG_GET_IPC_DATA_FAIL));
////                m_bGetDevID = false;
////                Log.d("tanhx", "get dev id diff scan dev id");
////                return ret;
//            }
//        }
//        else{ // 支持产品类别协议设备，判断是否具有设备权限
//            boolean retAuth = getDevAuth(cmsDevInfo.bParams);
////            if(m_bExitSet) return ret;
//            if(!retAuth && m_bAuth){
////                h.sendMessage(h.obtainMessage(MSG_GET_IPC_DATA_FAIL));
////                m_bGetDevID = false;
////                Log.d("tanhx", "no auth");
////                return ret;
//            }
//        }
//
////        if(!m_bAuth){ // 提示用户定制机，没有权限配置
////            h.sendEmptyMessage(MSG_WIFI_DIRECT_CONN_FAIL);
////            return ret;
////        }
//
////        if(m_bExitSet) return false;
//
//        // Log.d("tanhx", "dev id is " + m_strIpcDevID);
//        // 清空wifi列表
//        if(m_bResetDev){
//            resetDevParams();  // 作下容错处理，满足不支持该协议的设备配置
//        }
//
//        setDevUID();
//        setAppInfo();
//
////        if(m_bExitSet) return false;
//
//        // 设置token和streamid等ipc上传数据需要的身份认证信息
//        if(m_bRegisteredDevErr || !setToken()){
////            h.sendMessage(h.obtainMessage(MSG_GET_IPC_DATA_FAIL));
////            m_bGetDevID = false;
////            return ret;
//        }
//
//        // 是否固定ip
//        if(!setDhcp()){
////            m_handler.sendMessage(h.obtainMessage(MSG_GET_IPC_DATA_FAIL));
////            m_bGetDevID = false;
////            return ret;
//        }
//
//        // 设置时区
//        //setTimezone();
//
////        if(m_bExitSet) return false;
//
//        // 最后设置wifi
//        if(!setWifi()){
////            h.sendMessage(h.obtainMessage(MSG_GET_IPC_DATA_FAIL));
////            m_bGetDevID = false;
////            return ret;
//        }
//        // 参数设置完成后，关闭socket连接
//        mCmsNet.closeConn();
//        ret = true;
////        return ret;
//    }
//
//    // 设置厂家编号
//    private boolean setDevAuth(int iMacType){
//        boolean ret = false;
//
//        CmsCmdStruct cmsVerInfo = new CmsCmdStruct();
//        cmsVerInfo.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_SET;
//        cmsVerInfo.cmsSubCmd = CmsProtocolDef.PARA3_PRODUCTTYPE;
//        cmsVerInfo.bParams = new byte[8];
//        byte[] bCheckSum = new byte[]{0x01,0x02,0x03,0x04};
//        byte[] bType = CmsUtil.htonl(iMacType);
//        System.arraycopy(bCheckSum, 0, cmsVerInfo.bParams, 0, bCheckSum.length);
//        System.arraycopy(bType, 0, cmsVerInfo.bParams, 4, bType.length);
//        ret = mCmsNet.cmsExecCMD(mContext, cmsVerInfo);
//        Log.d("tanhx", "set dev auth ret=" + ret);
//        return ret;
//    }
//
//    private boolean getDevAuth(byte[] b){
//        boolean ret = false;
//
//        String strMAC = "";
//        int[] bnatid = new int[6];
//        for (int i = 0; i < 6; i++) {
//            bnatid[i] = b[i];
//            bnatid[i] &= 0xff;
//            String sTemp = Integer.toHexString(0xff & b[i]);
//            sTemp = sTemp.length() == 1 ? "0" + sTemp : sTemp;
//            strMAC += (sTemp + (i<5? ":" : ""));
//        }
//        String strDevID = CmsMenu.getDevIDByMac(strMAC);
//        if(strDevID.equals(m_strIpcDevID)){
//            // 判断当前程序是否有权限配置该摄像头
//            int iMacType = CmsUtil.ByteArrayToint(b, 12, true);
//            if(iMacType==0) ret = true;
//            else{
//                if(iMacType!=Integer.parseInt(CmsUtil.getMetaValue("f"))) {
//                    m_bAuth = false;
//                    ret = false;
//                }
//                else{ // 设置厂家编号
//                    int iNum = 0;
//                    while(!ret){
//                        ret = setDevAuth(iMacType);
//                        iNum++;
//                        if(iNum>3) break;
//                    }
//                }
//            }
//        }
//        Log.d("tanhx", "get dev auth ret " + ret);
//        return ret;
//    }
//
//    /**
//     * 摄像头参数恢复出厂设置
//     */
//    private boolean resetDevParams(){
//        //if(devrow.type.equals(cmsConstants.CMS_BD_IERMU)) return true;
//        CmsCmdStruct cmdResetDev = new CmsCmdStruct();
//        cmdResetDev.cmsMainCmd = CmsProtocolDef.LAN2_DEVICE_OP;
//        cmdResetDev.cmsSubCmd = CmsProtocolDef.DEVOP2_DEFULTMENU;
//        boolean bResetDev = mCmsNet.cmsExecCMD(mContext, cmdResetDev);
//        Log.d("tanhx", "reset dev ret " + bResetDev);
//        return bResetDev;
//    }
//
//    /**
//     * 设置设备UID
//     * @return
//     */
//    private boolean setDevUID(){
//        if(devrow.type.equals(CmsConstants.CMS_BD_IERMU)) return true;
//        if(m_baiduUser==null || m_baiduUser.strUID.length()>=20 || m_baiduUser.strUID.equals("")) return false;
//        CmsCmdStruct cmdUidDev = new CmsCmdStruct();
//        cmdUidDev.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_SET;
//        cmdUidDev.cmsSubCmd = CmsProtocolDef.PARA3_DEVUID;
//        cmdUidDev.bParams = new byte[20];
//        for(int i=0; i<cmdUidDev.bParams.length; i++){
//            cmdUidDev.bParams[i] = 0;
//        }
//        byte[] bUid = m_baiduUser.strUID.getBytes();
//        System.arraycopy(bUid, 0, cmdUidDev.bParams, 0, bUid.length);
//        boolean bsetDev = mCmsNet.cmsExecCMD(mContext, cmdUidDev);
//        Log.d("tanhx", "set dev uid ret " + bsetDev + ", uid len=" + bUid.length);
//        return bsetDev;
//    }
//
//    /**
//     * 设置appid和ak和sk
//     * @return
//     */
//    private boolean setAppInfo(){
//        if(!CmsUtil.m_bCmsApp) return true;  // 爱耳目通用程序不需要设置,第三方SDK需要设置
//        if(m_baiduUser==null || m_baiduUser.strUID.length()>=20 || m_baiduUser.strUID.equals("")) return false;
//        String AK = CmsUtil.getMetaValue("a");
//        String SK = CmsUtil.getMetaValue("s");
//        if(AK==null || SK==null) return true;
//        CmsCmdStruct cmdAppInfo = new CmsCmdStruct();
//        cmdAppInfo.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_SET;
//        cmdAppInfo.cmsSubCmd = CmsProtocolDef.PARA3_PUSHKEY;
//        cmdAppInfo.bParams = new byte[64];
//        for(int i=0; i<cmdAppInfo.bParams.length; i++){
//            cmdAppInfo.bParams[i] = 0;
//        }
//        byte[] bSK = SK.getBytes();
//        byte[] bAk = AK.getBytes();
//        System.arraycopy(bSK, 0, cmdAppInfo.bParams, 0, bSK.length);
//        System.arraycopy(bAk, 0, cmdAppInfo.bParams, 32, bAk.length);
//        boolean bsetDev = mCmsNet.cmsExecCMD(mContext, cmdAppInfo);
//        Log.d("tanhx", "set dev app info ret " + bsetDev);
//        return bsetDev;
//    }
//
//    // 是否固定ip
//    private boolean setDhcp(){
//        boolean ret = true;
//        if(!m_bDHCP){
//            CmsCmdStruct cmsData = new CmsCmdStruct();
//            cmsData.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_GET;
//            cmsData.cmsSubCmd = CmsProtocolDef.PARA3_NETSET;
//            CmsErr cmserr = new CmsErr(-1, "init");
//            CmsCmdStruct cmdNetParams = mCmsNet.getDevParam(mContext, cmsData, cmserr);
//            if(cmdNetParams!=null && cmdNetParams.bParams!=null && cmdNetParams.bParams.length>30){
//                cmdNetParams.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_SET;
//                cmdNetParams.bParams[3] = 0;
//                String[] strIP = CmsUtil.split(m_strNetIP, ".");
//                if (strIP.length == 4 && !TextUtils.isEmpty(strIP[3])) {
//                    cmdNetParams.bParams[4] = (byte) Integer.parseInt(strIP[0]);
//                    cmdNetParams.bParams[5] = (byte) Integer.parseInt(strIP[1]);
//                    cmdNetParams.bParams[6] = (byte) Integer.parseInt(strIP[2]);
//                    cmdNetParams.bParams[7] = (byte) Integer.parseInt(strIP[3]);
//                }
//                String[] strNetmask = CmsUtil.split(m_strNetMask, ".");
//                if (strNetmask.length == 4 && !TextUtils.isEmpty(strNetmask[3])) {
//                    cmdNetParams.bParams[8] = (byte) Integer.parseInt(strNetmask[0]);
//                    cmdNetParams.bParams[9] = (byte) Integer.parseInt(strNetmask[1]);
//                    cmdNetParams.bParams[10] = (byte) Integer.parseInt(strNetmask[2]);
//                    cmdNetParams.bParams[11] = (byte) Integer.parseInt(strNetmask[3]);
//                }
//                String[] strGateway = CmsUtil.split(m_strGateway, ".");
//                if (strGateway.length == 4 && !TextUtils.isEmpty(strGateway[3])) {
//                    cmdNetParams.bParams[12] = (byte) Integer.parseInt(strGateway[0]);
//                    cmdNetParams.bParams[13] = (byte) Integer.parseInt(strGateway[1]);
//                    cmdNetParams.bParams[14] = (byte) Integer.parseInt(strGateway[2]);
//                    cmdNetParams.bParams[15] = (byte) Integer.parseInt(strGateway[3]);
//                }
//                boolean bNetOK = mCmsNet.setDevParam(mContext, cmdNetParams);
//                Log.d("tanhx", "set dhcp off ret " + bNetOK);
//                ret = bNetOK;
//            }
//        }
//        return ret;
//    }
//
//    private boolean setWifi(){
//        if(devrow.type.equals(CmsConstants.CMS_BD_IERMU)) return true;
//        if(m_iConnType==1){ // eth, 有线网络下，从配置状态切换的工作状态
//            m_strSSID = CmsConstants.CMS_ETH;
//            m_strWifiPwd = CmsConstants.CMS_ETH;
//        }
//        byte[] bssid = m_strSSID.getBytes();
//        //if(m_iConnWifiType!=4) m_strWifiMac = cmsConstants.CMS_ETH;  // 去掉mac地址适配比较
//        byte[] bmac = m_iConnWifiType==4? m_strWifiUser.getBytes() : CmsConstants.CMS_ETH.getBytes();
//        CmsCmdStruct cmdWifi = new CmsCmdStruct();
//        cmdWifi.cmsMainCmd = CmsProtocolDef.LAN2_DEVICE_OP;
//        cmdWifi.cmsSubCmd = CmsProtocolDef.DEVOP2_WIFI_CONNECT;
//        cmdWifi.bParams = new byte[1+32*3];
//        String strPwd = m_strWifiPwd;
//        if(m_iConnWifiType==2){// wep 需要特殊处理  1<<6  是否直接连接设置wifi
//            cmdWifi.bParams[0] |= (byte) (1<<7);
//            int iLen = m_strWifiPwd.length();
//            if(iLen==5 || iLen==13 || iLen==16){
//                strPwd = "\"" + m_strWifiPwd + "\"";
//            }
//            Log.d("tanhx", "wep is " + cmdWifi.bParams[0] + ", pwd=" + strPwd);
//        }
//        else if(m_iConnWifiType==4){ // eap
//            cmdWifi.bParams[0] |= (1<<1);
//        }
//        byte[] bpwd = strPwd.getBytes();
//        int iPwdLen = bpwd.length;
//        if(iPwdLen>32) iPwdLen = 32;
//        System.arraycopy(bssid, 0, cmdWifi.bParams, 1, bssid.length);
//        System.arraycopy(bmac, 0, cmdWifi.bParams, 33, bmac.length);
//        System.arraycopy(bpwd, 0, cmdWifi.bParams, 65, iPwdLen);
//        Boolean bOk = mCmsNet.cmsExecCMD(mContext, cmdWifi);  // 设置wifi
//        Log.d("tanhx", "set wifi ret " + bOk);
//        return bOk;
//    }
//
//    private boolean setToken(){
//        byte[] bAccessToken = m_strToken.getBytes();
//        byte[] bRefreshToken = null;
//        if(CmsUtil.getIermuToken()!=null){  // iermu.com 取 token
//            byte[] bTemp = m_strRefreshToken.substring(7).getBytes();
//            boolean bHttps = m_strRefreshToken.substring(0, 6).indexOf("https")>=0;
//            bRefreshToken = new byte[1+bTemp.length];
//            bRefreshToken[0] = bHttps? (byte)0xcc : (byte) 0xee;
//            System.arraycopy(bTemp, 0, bRefreshToken, 1, bTemp.length);
//        }
//        else{  // baidu/cms token
//            if(!m_bNewToken && m_strRefreshToken!=null){//不支持新的权限服务器授权方式
//                bRefreshToken = m_strRefreshToken.getBytes();
//            }
//            else{
//                String strTemp = !m_settings.getIermuSvr()? CmsUtil.getMetaValue("") + "." + CmsUtil.getMetaValue("p") :
//                        "1.11111111111111111111";  //appid＝1 ak=1111111111 sk=11111111111111111111
//                byte[] bTemp = strTemp.getBytes();
//                bRefreshToken = new byte[1+bTemp.length];
//                bRefreshToken[0] = (byte) 0xdd;
//                System.arraycopy(bTemp, 0, bRefreshToken, 1, bTemp.length);
//            }
//        }
//
//        byte[] bStreamID = null;
//        if(m_strStreamID==null || m_strStreamID.length()<10){
//            return false;
//        }
//        bStreamID = m_strStreamID.getBytes();
//        CmsCmdStruct cmdToken = new CmsCmdStruct();
//        cmdToken.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_SET;
//        cmdToken.cmsSubCmd = CmsProtocolDef.PARA3_TOKEN;
//        cmdToken.bParams = new byte[80*2+32];
//        for(int i=0;i<cmdToken.bParams.length;i++){
//            cmdToken.bParams[i] = 0;
//        }
//        System.arraycopy(bAccessToken, 0, cmdToken.bParams, 0, bAccessToken.length);
//        if(bRefreshToken!=null) System.arraycopy(bRefreshToken, 0, cmdToken.bParams, 80, bRefreshToken.length);
//        if(bStreamID!=null) System.arraycopy(bStreamID, 0, cmdToken.bParams, 160, bStreamID.length);
//
//        Boolean bOk_token = mCmsNet.setDevParam(mContext, cmdToken);
//        Log.d("tanhx", "set token info is " + bOk_token);
//        return bOk_token;
//    }


//

}
