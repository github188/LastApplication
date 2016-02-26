/**
 *  for 耳目摄像机添加
 *  在手机和摄像机在同一个局域网内，可以调用
 * 
 *  用法 ：
 * 		devSet myDevSet = new devSet(SetupDevActivity.this, m_strIpcDevID, true);
 *		myDevSet.setBindInfo(m_strToken, m_strStreamID);
 *		myDevSet.setwifi(m_wifi_list.get(m_iWifiPos).SSID, m_strWifiPwd, null, m_wifi_list.get(m_iWifiPos).capabilities);
 *		int newret = myDevSet.startSetDev();
 *
 *
 *  共8个文件 import中的4个+devset相同目录下的4个
 */

package com.iermu.lan.test;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.cms.iermu.cms.CmsCmdStruct;
import com.cms.iermu.cms.CmsConstants;
import com.cms.iermu.cms.CmsDev;
import com.cms.iermu.cms.CmsErr;
import com.cms.iermu.cms.CmsNetUtil;
import com.cms.iermu.cms.CmsProtocolDef;
import com.cms.iermu.cms.CmsUtil;
//import com.cms.iermu.cmsConstants;
//import com.cms.iermu.cmsErr;
//import com.cms.iermu.cmsUtils;
//import com.cms.iermu.database.DevRow;

public class SetupDev {
	
	Context m_context;
	CmsNetUtil myCmsNet;
	
	boolean bDHCP = true;
	String m_strNetIP;
	String m_strNetMask;
	String m_strGateway;
	
	//int m_iConnType;
	int m_iConnWifiType;
	String m_strSSID;
	String m_strWifiPwd;
	String m_strWifiUser;
	
	String m_strToken;
	String m_strStreamID;
	boolean bBD;
	
	String m_strUid;
    CmsDev devrow;
	
	int iMacType;
	
	String m_strIpcDevID;
	
	boolean m_bResetDev;
	
	boolean m_bExitSet; // 退出配置过程
	
	boolean m_bEthernet = false;
	
	boolean m_bSmartConfig = false;
	
	public SetupDev(Context c, String sDevID, boolean bResetDev, String strUid) {
		m_context = c;
		myCmsNet = new CmsNetUtil();
		devrow = new CmsDev();
		devrow.url = "192.168.58.1";
		devrow.username = "88888888";
		devrow.password = "cms3518";
		devrow.type = "CMS DVR";
		devrow.nat_server = CmsConstants.CMS_AP_IERMU;
		devrow.port = "3357";
		m_strUid = strUid;
		myCmsNet.setNatConn(false, devrow);
		m_strIpcDevID = sDevID;
		m_bResetDev = bResetDev;
		bBD = true;
		m_bExitSet = false;
		m_bEthernet = false;
		m_bSmartConfig = false;
	}
	
	public SetupDev(Context c, String sDevID, boolean bResetDev, String strIP, String strUid) {
		m_context = c;
		myCmsNet = new CmsNetUtil();
		devrow = new CmsDev();
		devrow.url = strIP;
		devrow.username = "88888888";
		devrow.password = strUid;
		devrow.type = CmsConstants.CMS_BD_IERMU;
		devrow.nat_server = CmsConstants.CMS_AP_IERMU;
		devrow.port = "3357";
		myCmsNet.setNatConn(false, devrow);
		m_strIpcDevID = sDevID;
		m_strUid = strUid;
		m_bResetDev = bResetDev;
		bBD = true;
		m_bExitSet = false;
		m_bEthernet = true;
		m_bSmartConfig = false;
	}
	
	// 这个接口只能是smart方式配置时调用，smart配置不需要设置wifi
	public SetupDev(Context c, String sDevID, boolean bResetDev, String strIP, String strUid, String strPwd) {
		m_context = c;
		myCmsNet = new CmsNetUtil();
		devrow = new CmsDev();
		devrow.url = strIP;
		devrow.username = strUid;
		devrow.password = strPwd;
		devrow.type = "CMS DVR";
		devrow.nat_server = CmsConstants.CMS_AP_IERMU;
		devrow.port = "3357";
		myCmsNet.setNatConn(false, devrow);
		m_strIpcDevID = sDevID;
		m_strUid = strUid;
		m_bResetDev = bResetDev;
		bBD = true;
		m_bExitSet = false;
		m_bEthernet = true;
		m_bSmartConfig = true;
	}
	
	public void setNet(boolean dhcp, String ip, String netmask, String gateway){
		bDHCP = dhcp;
		m_strNetIP = ip;
		m_strNetMask = netmask;
		m_strGateway = gateway;
	}
	
	public void setwifi(String ssid, String pwd, String user, String strEncryption){
		m_strSSID = ssid;
		m_strWifiPwd = pwd;
		m_strWifiUser = user;
		m_iConnWifiType = CmsUtil.getWifiType(strEncryption);
	}
	
	public void setFacID(int iID){
		iMacType = iID;
	}
	
	public void setBindInfo(String token, String streamID){
		m_strToken = token;
		m_strStreamID = streamID;
	}
	
	
	// 是否固定ip
    private boolean setDhcp(){
    	boolean ret = true;
    	if(!bDHCP){
			CmsCmdStruct cmsData = new CmsCmdStruct();
			cmsData.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_GET;
			cmsData.cmsSubCmd = CmsProtocolDef.PARA3_NETSET;
            CmsErr cmserr = new CmsErr(-1, "init");
			CmsCmdStruct cmdNetParams = myCmsNet.getDevParam(m_context, cmsData, cmserr);
			if(cmdNetParams!=null && cmdNetParams.bParams!=null && cmdNetParams.bParams.length>30){
				cmdNetParams.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_SET;
				cmdNetParams.bParams[3] = 0;
				String[] strIP = CmsUtil.split(m_strNetIP, ".");
				if (strIP.length == 4 && !TextUtils.isEmpty(strIP[3])) {
					cmdNetParams.bParams[4] = (byte) Integer.parseInt(strIP[0]);
					cmdNetParams.bParams[5] = (byte) Integer.parseInt(strIP[1]);
					cmdNetParams.bParams[6] = (byte) Integer.parseInt(strIP[2]);
					cmdNetParams.bParams[7] = (byte) Integer.parseInt(strIP[3]);
				}
				String[] strNetmask = CmsUtil.split(m_strNetMask, ".");
				if (strNetmask.length == 4 && !TextUtils.isEmpty(strNetmask[3])) {
					cmdNetParams.bParams[8] = (byte) Integer.parseInt(strNetmask[0]);
					cmdNetParams.bParams[9] = (byte) Integer.parseInt(strNetmask[1]);
					cmdNetParams.bParams[10] = (byte) Integer.parseInt(strNetmask[2]);
					cmdNetParams.bParams[11] = (byte) Integer.parseInt(strNetmask[3]);
				}
				String[] strGateway = CmsUtil.split(m_strGateway, ".");
				if (strGateway.length == 4 && !TextUtils.isEmpty(strGateway[3])) {
					cmdNetParams.bParams[12] = (byte) Integer.parseInt(strGateway[0]);
					cmdNetParams.bParams[13] = (byte) Integer.parseInt(strGateway[1]);
					cmdNetParams.bParams[14] = (byte) Integer.parseInt(strGateway[2]);
					cmdNetParams.bParams[15] = (byte) Integer.parseInt(strGateway[3]);
				}
				boolean bNetOK = myCmsNet.setDevParam(m_context, cmdNetParams);
				Log.d("tanhx", "set dhcp off ret " + bNetOK);
				ret = bNetOK;
			}
		}
    	return ret;
    }
    
    private boolean setWifi(){
    	if(m_bSmartConfig) return true;
    	if(m_bEthernet){ // eth, 有线网络下，从配置状态切换的工作状态
    		m_strSSID = CmsConstants.CMS_ETH;
    		m_strWifiPwd = CmsConstants.CMS_ETH;
    	}
    	byte[] bssid = m_strSSID.getBytes();
		//if(m_iConnWifiType!=4) m_strWifiMac = cmsConstants.CMS_ETH;  // 去掉mac地址适配比较
		byte[] bmac = m_iConnWifiType==4? m_strWifiUser.getBytes() : CmsConstants.CMS_ETH.getBytes();
        CmsCmdStruct cmdWifi = new CmsCmdStruct();
		cmdWifi.cmsMainCmd = CmsProtocolDef.LAN2_DEVICE_OP;
		cmdWifi.cmsSubCmd = CmsProtocolDef.DEVOP2_WIFI_CONNECT;
		cmdWifi.bParams = new byte[1+32*3];
		String strPwd = m_strWifiPwd;
		if(m_iConnWifiType==2){// wep 需要特殊处理  1<<6  是否直接连接设置wifi
			cmdWifi.bParams[0] |= (byte) (1<<7);
			int iLen = m_strWifiPwd.length();
			if(iLen==5 || iLen==13 || iLen==16){
				strPwd = "\"" + m_strWifiPwd + "\"";
			}
			Log.d("tanhx", "wep is " + cmdWifi.bParams[0] + ", pwd=" + strPwd);
		}
		else if(m_iConnWifiType==4){ // eap
			cmdWifi.bParams[0] |= (1<<1);
		}
		byte[] bpwd = strPwd.getBytes();
		int iPwdLen = bpwd.length;
		if(iPwdLen>32) iPwdLen = 32;
		System.arraycopy(bssid, 0, cmdWifi.bParams, 1, bssid.length);
		System.arraycopy(bmac, 0, cmdWifi.bParams, 33, bmac.length);
		System.arraycopy(bpwd, 0, cmdWifi.bParams, 65, iPwdLen);
		Boolean bOk = myCmsNet.cmsExecCMD(m_context, cmdWifi);  // 设置wifi
		Log.d("tanhx", "set wifi ret " + bOk);
		return bOk;
    }
    
    private boolean setToken(){
    	byte[] bAccessToken = m_strToken==null? null : m_strToken.getBytes();
		byte[] bRefreshToken = null; 
		// baidu/cms token
		if(m_strToken!=null && !m_strToken.equals("")){
			String strTemp = bBD? CmsUtil.getMetaValue("") + "." + CmsUtil.getMetaValue("p") :
				"1.11111111111111111111";  //appid＝1 ak=1111111111 sk=11111111111111111111
			byte[] bTemp = strTemp.getBytes();
			bRefreshToken = new byte[1+bTemp.length];
			bRefreshToken[0] = (byte) 0xdd;
			System.arraycopy(bTemp, 0, bRefreshToken, 1, bTemp.length);
		}
		else{ // for 爱耳目账号体系
			if(m_strUid==null || m_strUid.equals("")) return false;
			byte[] bTemp = m_strUid.getBytes();
			bRefreshToken = new byte[bTemp.length+1];
			bRefreshToken[0] = 0;
			System.arraycopy(bTemp, 0, bRefreshToken, 1, bTemp.length);
		}

		byte[] bStreamID = null;
		if(m_strStreamID==null || m_strStreamID.length()<3){
			return false;
		}
		bStreamID = m_strStreamID.getBytes();
        CmsCmdStruct cmdToken = new CmsCmdStruct();
		cmdToken.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_SET;
		cmdToken.cmsSubCmd = CmsProtocolDef.PARA3_TOKEN;
		cmdToken.bParams = new byte[80*2+32];
		for(int i=0;i<cmdToken.bParams.length;i++){
			cmdToken.bParams[i] = 0;
		}
		if(bAccessToken!=null) System.arraycopy(bAccessToken, 0, cmdToken.bParams, 0, bAccessToken.length);
		if(bRefreshToken!=null) System.arraycopy(bRefreshToken, 0, cmdToken.bParams, 80, bRefreshToken.length);
		if(bStreamID!=null) System.arraycopy(bStreamID, 0, cmdToken.bParams, 160, bStreamID.length);
		
		Boolean bOk_token = myCmsNet.setDevParam(m_context, cmdToken);
		Log.d("tanhx", "set token info is " + bOk_token);
		return bOk_token;
    }
    
    // 设置厂家编号
    private boolean setDevAuth(){
    	boolean ret = false;

        CmsCmdStruct cmsVerInfo = new CmsCmdStruct();
		cmsVerInfo.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_SET;
		cmsVerInfo.cmsSubCmd = CmsProtocolDef.PARA3_PRODUCTTYPE;
		cmsVerInfo.bParams = new byte[8];
		byte[] bCheckSum = new byte[]{0x01,0x02,0x03,0x04};
		byte[] bType = CmsUtil.htonl(iMacType);
		System.arraycopy(bCheckSum, 0, cmsVerInfo.bParams, 0, bCheckSum.length);
		System.arraycopy(bType, 0, cmsVerInfo.bParams, 4, bType.length);
		ret = myCmsNet.cmsExecCMD(m_context, cmsVerInfo);
    	Log.d("tanhx", "set dev auth ret=" + ret);
    	return ret;
    }
    
    private boolean getDevAuth(byte[] b){
    	boolean ret = false;
    	
    	String strMAC = "";
		int[] bnatid = new int[6];
		for (int i = 0; i < 6; i++) {
			bnatid[i] = b[i];
			bnatid[i] &= 0xff;
			String sTemp = Integer.toHexString(0xff & b[i]);
			sTemp = sTemp.length() == 1 ? "0" + sTemp : sTemp;
			strMAC += (sTemp + (i<5? ":" : ""));
		}
		String strDevID = CmsUtil.getDevIDByMac(strMAC);
		if(strDevID.equals(m_strIpcDevID)){
			// 判断当前程序是否有权限配置该摄像头
			iMacType = CmsUtil.ByteArrayToint(b, 12, true);
			if(iMacType==0) ret = true;
			else{
				if(iMacType!=Integer.parseInt(CmsUtil.getMetaValue("f"))) {
					//m_bAuth = false;
					ret = false;
				}
				else{ // 设置厂家编号
					int iNum = 0;
					while(!ret){
						ret = setDevAuth();
						iNum++;
						if(iNum>3) break;
					}
				}
			}
		}
		Log.d("tanhx", "get dev auth ret " + ret);
		return ret;
    }
    
    /**
     * replace by reset
     */
    private void clearWifilist(){
        CmsCmdStruct cmdClearWifi = new CmsCmdStruct();
		cmdClearWifi.cmsMainCmd = CmsProtocolDef.LAN2_DEVICE_OP;
		cmdClearWifi.cmsSubCmd = CmsProtocolDef.DEVOP2_WIFI_CANCEL;
		cmdClearWifi.bParams = new byte[32+32];
		for(int i=0;i<cmdClearWifi.bParams.length;i++){
			cmdClearWifi.bParams[i] = 0;
		}
		byte[] bSSID = CmsConstants.CMS_ETH.getBytes();
		System.arraycopy(bSSID, 0, cmdClearWifi.bParams, 0, bSSID.length);
		boolean bClearWifi = myCmsNet.cmsExecCMD(m_context, cmdClearWifi);
		Log.d("tanhx", "clear wifi list ret " + bClearWifi);
    }
    
    /**
     * 摄像头参数恢复出厂设置
     */
    private boolean resetDevParams(){
    	//if(devrow.type.equals(cmsConstants.CMS_BD_IERMU)) return true;
        CmsCmdStruct cmdResetDev = new CmsCmdStruct();
		cmdResetDev.cmsMainCmd = CmsProtocolDef.LAN2_DEVICE_OP;
		cmdResetDev.cmsSubCmd = CmsProtocolDef.DEVOP2_DEFULTMENU;
		boolean bResetDev = myCmsNet.cmsExecCMD(m_context, cmdResetDev);
		Log.d("tanhx", "reset dev ret " + bResetDev);
		return bResetDev;
    }
    
    /**
     * 设置设备UID, AP模式有效 
     * @return
     */
    private boolean setDevUID(){
    	if(m_strUid==null) return false;
        CmsCmdStruct cmdUidDev = new CmsCmdStruct();
		cmdUidDev.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_SET;
		cmdUidDev.cmsSubCmd = CmsProtocolDef.PARA3_DEVUID;
		cmdUidDev.bParams = new byte[20]; 
		for(int i=0; i<cmdUidDev.bParams.length; i++){
			cmdUidDev.bParams[i] = 0;
		}
		byte[] bUid = m_strUid.getBytes();
		System.arraycopy(bUid, 0, cmdUidDev.bParams, 0, bUid.length);
		boolean bsetDev = myCmsNet.cmsExecCMD(m_context, cmdUidDev);
		Log.d("tanhx", "set dev uid ret " + bsetDev + ", uid len=" + bUid.length);
		return bsetDev;
    }
    
    /**
     * 设置appid和ak和sk
     * @return
     */
    private boolean setAppInfo(){
    	if(!CmsUtil.m_bCmsApp) return true;  // 爱耳目通用程序不需要设置,第三方SDK需要设置
    	if(m_strUid.equals("")) return false;
    	String AK = CmsUtil.getMetaValue("a");
		String SK = CmsUtil.getMetaValue("s");
		if(AK==null || SK==null) return true;
        CmsCmdStruct cmdAppInfo = new CmsCmdStruct();
		cmdAppInfo.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_SET;
		cmdAppInfo.cmsSubCmd = CmsProtocolDef.PARA3_PUSHKEY;
		cmdAppInfo.bParams = new byte[64]; 
		for(int i=0; i<cmdAppInfo.bParams.length; i++){
			cmdAppInfo.bParams[i] = 0;
		}
		byte[] bSK = SK.getBytes();
		byte[] bAk = AK.getBytes();
		System.arraycopy(bSK, 0, cmdAppInfo.bParams, 0, bSK.length);
		System.arraycopy(bAk, 0, cmdAppInfo.bParams, 32, bAk.length);
		boolean bsetDev = myCmsNet.cmsExecCMD(m_context, cmdAppInfo);
		Log.d("tanhx", "set dev app info ret " + bsetDev);
		return bsetDev;
    }
    
    public void stopSet(){
    	m_bExitSet = true;
    }
    
    // 设置摄像头配置参数
    /**
     * 
     * 状态码定义：
     * 	 0 成功
     * 	-1 连接摄像头失败，网络连接失败/获取设备信息出错
     * 	-2 设备ID不匹配
     *  -3 没有设备权限，第三方定制机
     *  -4 同-3
     *  -5 设置token失败
     *  -6 设置固定IP出错
     *  -7 设置wifi出错
     *  -100 用户主动中断配置过程
     * 
     * @return 状态码
     */
    public int startSetDev(){
    	int ret = 0;
    	boolean m_bAuth = true;
    	boolean m_bNewToken = true;
        CmsCmdStruct cmsVerInfo = new CmsCmdStruct();
		cmsVerInfo.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_GET;
		cmsVerInfo.cmsSubCmd = CmsProtocolDef.PARA3_PRODUCTTYPE;
        CmsErr cmserr = new CmsErr(-1, "init");
        CmsCmdStruct cmsDevInfo = myCmsNet.getDevParam(m_context, cmsVerInfo, cmserr);
		Log.d("tanhx", "set dev params start!!!");
		if(m_bExitSet) return -100;
		// 得到device id
		if(cmsDevInfo==null || cmsDevInfo.bParams.length<32) { // 不支持新协议
			m_bNewToken = false;
			cmsVerInfo.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_GET;
			cmsVerInfo.cmsSubCmd = CmsProtocolDef.PARA3_VERINFO;
			cmserr = new CmsErr(-1, "init");
			cmsDevInfo = myCmsNet.getDevParam(m_context, cmsVerInfo, cmserr);
			
			// 得到device id
			if(cmsDevInfo==null || cmsDevInfo.bParams.length<78) { // 错误, 重连设备ap
				ret = -1;
				myCmsNet.closeConn();
				Log.d("tanhx", "get dev params err");
				return ret;
			}
			// 检测是否是注册的设备
			String strTemp = CmsUtil.getDevID(cmsDevInfo.bParams);
			if(strTemp==null || !strTemp.equals(m_strIpcDevID)){
				ret = -2;
				myCmsNet.closeConn();
				Log.d("tanhx", "get dev id diff scan dev id");
				return ret;
			}
		}
		else{ // 支持产品类别协议设备，判断是否具有设备权限
			boolean retAuth = getDevAuth(cmsDevInfo.bParams);
			if(m_bExitSet) return -100;
			if(!retAuth && m_bAuth){
				ret = -3;
				myCmsNet.closeConn();
				Log.d("tanhx", "no auth");
				return ret;
			}
		}
		
		if(!m_bAuth){ // 提示用户定制机，没有权限配置
			ret = -4;
			myCmsNet.closeConn();
			return ret;
		}

		// Log.d("tanhx", "dev id is " + m_strIpcDevID); 
		// 清空wifi列表
		if(m_bResetDev){
			resetDevParams();  // 作下容错处理，满足不支持该协议的设备配置
			if(m_bExitSet) {
				myCmsNet.closeConn();
				return -100;
			}
		}
		
		setDevUID();
		if(m_bExitSet) {
			myCmsNet.closeConn();
			return -100;
		}
		/* 通用程序去掉
		setAppInfo();
		if(m_bExitSet) return -100;*/
		
		// 设置token和streamid等ipc上传数据需要的身份认证信息
		if(!setToken()){
			if(m_bExitSet){
				myCmsNet.closeConn();
				return -100;
			}
			ret = -5;
			myCmsNet.closeConn();
			return ret;
		}
		
		// 是否固定ip
		if(!setDhcp()){
			if(m_bExitSet) {
				myCmsNet.closeConn();
				return -100;
			}
			ret = -6;
			myCmsNet.closeConn();
			return ret;
		}
		
		// 设置时区
		//setTimezone();
		
		// 最后设置wifi
		if(!m_bSmartConfig && !setWifi()){
			if(m_bExitSet) {
				myCmsNet.closeConn();
				return -100;
			}
			ret = -7;
			myCmsNet.closeConn();
			return ret;
		}
		// 参数设置完成后，关闭socket连接
		myCmsNet.closeConn();
		return ret;
    }
    
}
