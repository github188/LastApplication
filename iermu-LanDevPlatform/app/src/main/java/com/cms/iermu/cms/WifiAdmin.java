package com.cms.iermu.cms;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.GroupCipher;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.PairwiseCipher;
import android.net.wifi.WifiConfiguration.Protocol;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class WifiAdmin {
	// 定义一个WifiManager对象
	private WifiManager mWifiManager;
	// 定义一个WifiInfo对象
	private WifiInfo mWifiInfo;
	// 扫描出的网络连接列表
	private List<ScanResult> mWifiList;
	// 网络连接列表
	private List<WifiConfiguration> mWifiConfigurations;
	WifiLock mWifiLock;

	public WifiAdmin(Context context) {
		// 取得WifiManager对象
		mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		// 取得WifiInfo对象
		mWifiInfo = mWifiManager.getConnectionInfo();		
		
	}

	// 打开wifi
	public void openWifi() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
	}

	// 关闭wifi
	public void closeWifi() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
	}

	// 检查当前wifi状态
	public int checkState() {
		return mWifiManager.getWifiState();
	}

	// 锁定wifiLock
	public void acquireWifiLock() {
		mWifiLock.acquire();
	}

	// 解锁wifiLock
	public void releaseWifiLock() {
		// 判断是否锁定
		if (mWifiLock.isHeld()) {
			mWifiLock.acquire();
		}
	}

	// 创建一个wifiLock
	public void createWifiLock() {
		mWifiLock = mWifiManager.createWifiLock("test");
	}

	// 得到配置好的网络
	public List<WifiConfiguration> getConfiguration() {
		return mWifiConfigurations;
	}

	// 指定配置好的网络进行连接
	public void connetionConfiguration(int index) {
		if (index > mWifiConfigurations.size()) {
			return;
		}
		// 连接配置好指定ID的网络
		mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId,
				true);
	}

	// 指定配置好的网络进行连接
	public void connWifi(String sSSID) {
		int iLen = mWifiConfigurations.size();
		for(int i=0; i<iLen; i++){
            WifiConfiguration configuration = mWifiConfigurations.get(i);
			if(configuration.SSID.indexOf(sSSID) >= 0){
				// 连接配置好指定ID的网络
				mWifiManager.enableNetwork(mWifiConfigurations.get(i).networkId,
						true);
			}
		}
	}
	
	public void startScan() {
		mWifiManager.startScan();
		
		// 得到扫描结果
		List<ScanResult> wifiList = mWifiManager.getScanResults();
		//mWifiList = mWifiManager.getScanResults();
		if(wifiList==null) return;
		int iLen = wifiList.size();
		if(mWifiList!=null){
			mWifiList.clear();
			//mWifiList = null;
		}
		mWifiList = new ArrayList<ScanResult>();
		for(int i=0; i<iLen; i++){
			ScanResult mywifi = wifiList.get(i);
			//if(mywifi.capabilities.toLowerCase().indexOf("eap")!=-1) continue;
			boolean haveSameSSID = false;
			if(mWifiList.size()>1){
				for(int j=0; j<mWifiList.size(); j++){
					if(mywifi.SSID.equals(mWifiList.get(j).SSID)){ 						
						haveSameSSID = true;
						break;
					}
				}
			}
			if(haveSameSSID) continue;
			mWifiList.add(mywifi);
		}
		
		// 得到配置好的网络连接
		mWifiConfigurations = mWifiManager.getConfiguredNetworks();      
	}
	
	// 得到网络列表
	public List<ScanResult> getWifiList() {
        List<ScanResult> list = new ArrayList<ScanResult>(Arrays.asList(new ScanResult[mWifiList.size()]));
        Collections.copy(list, mWifiList);
		return list;
	}

	public boolean getHideSSID(){
		return mWifiInfo.getHiddenSSID();
	}
	
	// 查看扫描结果
	public StringBuffer lookUpScan() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mWifiList.size(); i++) {
			sb.append("Index_" + new Integer(i + 1).toString() + ":");
			// 将ScanResult信息转换成一个字符串包
			// 其中把包括：BSSID、SSID、capabilities、frequency、level
			sb.append((mWifiList.get(i)).toString()).append("\n");
		}
		return sb;
	}

	public String getMacAddress() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}

	public String getBSSID() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
	}

	public String getSSID() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
	}

	public int getIpAddress() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}
	
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public int getFrequency() {  // >2000 2.4G >5000 5G
		int iFreq = 0;
		if(mWifiInfo == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
			startScan();
			if(mWifiList!=null && mWifiList.size()>0){
				for(int i=0; i<mWifiList.size(); i++){
					if(mWifiInfo.getBSSID().equals(mWifiList.get(i).BSSID)) {
						iFreq = mWifiList.get(i).frequency;
						break;
					}
				}
			}
		}
		else iFreq = mWifiInfo.getFrequency();
		return iFreq;
	}

	public int getGateway(){
		int ret = mWifiManager.getDhcpInfo().gateway;
		return ret;
	}
	
	public int getNetmask(){
		int ret = mWifiManager.getDhcpInfo().netmask;
		return ret;
	}
	
	// 得到连接的ID
	public int getNetWorkId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	// 得到wifiInfo的所有信息
	public String getWifiInfo() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}

	//1：open  2: wep 3: wpa 4: eap   -1:error
	public int getSecurity() {
		try{
			WifiConfiguration config = null;
			if(mWifiConfigurations==null) mWifiConfigurations = mWifiManager.getConfiguredNetworks();
			
			for (WifiConfiguration wifiConfiguration : mWifiConfigurations) {
				String configSSID = wifiConfiguration.SSID;
				configSSID = configSSID.replace("\"", "");
				String currSSID = mWifiInfo.getSSID();
				currSSID = currSSID.replace("\"", "");
				if (currSSID.equals(configSSID) && mWifiInfo.getNetworkId()==wifiConfiguration.networkId) {
		            config = wifiConfiguration;
		            break;
		        }
			}
			if(config==null) return -1;			
			
	        if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
	            return 3;
	        }
	        if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
	            return 4;
	        }
	        return (config.wepKeys[0] != null) ? 2 : 1; 
		}
		catch(Exception e){
			e.printStackTrace();
			return -1;
		}
    }
	
	public void removeWifi(int iNetid){
		mWifiManager.removeNetwork(getNetWorkId());
	}
	
	// 添加一个网络并连接
	public boolean addNetWork(WifiConfiguration configuration) {
		int wcgId = mWifiManager.addNetwork(configuration);
		boolean isSuccess = mWifiManager.enableNetwork(wcgId, true);		
		Log.d("tanhx", "add net ret " + wcgId + ", enable " + isSuccess);
		return isSuccess;
	}

	// 断开指定ID的网络
	public void disConnectionWifi(int netId) {
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
	}
	
	/*
	 * 删除指定网络
	 */
	public void delWifi(String SSID) {
		WifiConfiguration tempConfig = isExsits(SSID, mWifiManager);
		if (tempConfig != null) {
			mWifiManager.removeNetwork(tempConfig.networkId);
		}
	}

	void readWepConfig() { 
		List<WifiConfiguration> item = mWifiManager.getConfiguredNetworks();
		int i = item.size();
		Log.d("WifiPreference", "NO OF CONFIG: " + i );
		Iterator<WifiConfiguration> iter =  item.iterator();
		WifiConfiguration config = item.get(0);
		Log.d("WifiPreference", "SSID:" + config.SSID);
		Log.d("WifiPreference", "PASSWORD:" + config.preSharedKey);
		Log.d("WifiPreference", "ALLOWED ALGORITHMS");
		Log.d("WifiPreference", "LEAP:" + config.allowedAuthAlgorithms.get(AuthAlgorithm.LEAP));
		Log.d("WifiPreference", "OPEN:" + config.allowedAuthAlgorithms.get(AuthAlgorithm.OPEN));
		Log.d("WifiPreference", "SHARED:" + config.allowedAuthAlgorithms.get(AuthAlgorithm.SHARED));
		Log.d("WifiPreference", "GROUP CIPHERS");
		Log.d("WifiPreference", "CCMP:" + config.allowedGroupCiphers.get(GroupCipher.CCMP));
		Log.d("WifiPreference", "TKIP:" + config.allowedGroupCiphers.get(GroupCipher.TKIP));
		Log.d("WifiPreference", "WEP104:" + config.allowedGroupCiphers.get(GroupCipher.WEP104));
		Log.d("WifiPreference", "WEP40:" + config.allowedGroupCiphers.get(GroupCipher.WEP40));
		Log.d("WifiPreference", "KEYMGMT");
		Log.d("WifiPreference", "IEEE8021X:" + config.allowedKeyManagement.get(KeyMgmt.IEEE8021X));
		Log.d("WifiPreference", "NONE:" + config.allowedKeyManagement.get(KeyMgmt.NONE));
		Log.d("WifiPreference", "WPA_EAP:" + config.allowedKeyManagement.get(KeyMgmt.WPA_EAP));
		Log.d("WifiPreference", "WPA_PSK:" + config.allowedKeyManagement.get(KeyMgmt.WPA_PSK));
		Log.d("WifiPreference", "PairWiseCipher");
		Log.d("WifiPreference", "CCMP:" + config.allowedPairwiseCiphers.get(PairwiseCipher.CCMP));
		Log.d("WifiPreference", "NONE:" + config.allowedPairwiseCiphers.get(PairwiseCipher.NONE));
		Log.d("WifiPreference", "TKIP:" + config.allowedPairwiseCiphers.get(PairwiseCipher.TKIP));
		Log.d("WifiPreference", "Protocols");
		Log.d("WifiPreference", "RSN:" + config.allowedProtocols.get(Protocol.RSN));
		Log.d("WifiPreference", "WPA:" + config.allowedProtocols.get(Protocol.WPA));
		Log.d("WifiPreference", "WEP Key Strings");
		String[] wepKeys = config.wepKeys;
		Log.d("WifiPreference", "WEP KEY 0:" + wepKeys[0]);
		Log.d("WifiPreference", "WEP KEY 1:" + wepKeys[1]);
		Log.d("WifiPreference", "WEP KEY 2:" + wepKeys[2]);
		Log.d("WifiPreference", "WEP KEY 3:" + wepKeys[3]);

	}
	
	void saveEapConfig(String passString, String userName)
    {/*
    *//********************************Configuration Strings****************************************************//*
    final String ENTERPRISE_EAP = "TLS";
    final String ENTERPRISE_CLIENT_CERT = "keystore://USRCERT_CertificateName";
    final String ENTERPRISE_PRIV_KEY = "keystore://USRPKEY_CertificateName";
    //CertificateName = Name given to the certificate while installing it

    Optional Params- My wireless Doesn't use these
    final String ENTERPRISE_PHASE2 = "";
    final String ENTERPRISE_ANON_IDENT = "ABC";
    final String ENTERPRISE_CA_CERT = "";
    *//********************************Configuration Strings****************************************************//*

    Create a WifiConfig
    WifiConfiguration selectedConfig = new WifiConfiguration();

    AP Name
    selectedConfig.SSID = "\"SSID_Name\"";

    Priority
    selectedConfig.priority = 40;

    Enable Hidden SSID
    selectedConfig.hiddenSSID = true;

    Key Mgmnt
    selectedConfig.allowedKeyManagement.clear();
    selectedConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
    selectedConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);

    Group Ciphers
    selectedConfig.allowedGroupCiphers.clear();
    selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
    selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
    selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
    selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

    Pairwise ciphers
    selectedConfig.allowedPairwiseCiphers.clear();
    selectedConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
    selectedConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);

    Protocols
    selectedConfig.allowedProtocols.clear();
    selectedConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
    selectedConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

    // Enterprise Settings
    // Reflection magic here too, need access to non-public APIs
    try {
        // Let the magic start
        Class[] wcClasses = WifiConfiguration.class.getClasses();
        // null for overzealous java compiler
        Class wcEnterpriseField = null;

        for (Class wcClass : wcClasses)
            if (wcClass.getName().equals(INT_ENTERPRISEFIELD_NAME)) 
            {
                wcEnterpriseField = wcClass;
                break;
            }
        boolean noEnterpriseFieldType = false; 
        if(wcEnterpriseField == null)
            noEnterpriseFieldType = true; // Cupcake/Donut access enterprise settings directly

        Field wcefAnonymousId = null, wcefCaCert = null, wcefClientCert = null, wcefEap = null, wcefIdentity = null, wcefPassword = null, wcefPhase2 = null, wcefPrivateKey = null;
        Field[] wcefFields = WifiConfiguration.class.getFields();
        // Dispatching Field vars
        for (Field wcefField : wcefFields) 
        {
            if (wcefField.getName().equals(INT_ANONYMOUS_IDENTITY))
                wcefAnonymousId = wcefField;
            else if (wcefField.getName().equals(INT_CA_CERT))
                wcefCaCert = wcefField;
            else if (wcefField.getName().equals(INT_CLIENT_CERT))
                wcefClientCert = wcefField;
            else if (wcefField.getName().equals(INT_EAP))
                wcefEap = wcefField;
            else if (wcefField.getName().equals(INT_IDENTITY))
                wcefIdentity = wcefField;
            else if (wcefField.getName().equals(INT_PASSWORD))
                wcefPassword = wcefField;
            else if (wcefField.getName().equals(INT_PHASE2))
                wcefPhase2 = wcefField;
            else if (wcefField.getName().equals(INT_PRIVATE_KEY))
                wcefPrivateKey = wcefField;
        }


        Method wcefSetValue = null;
        if(!noEnterpriseFieldType){
        for(Method m: wcEnterpriseField.getMethods())
            //System.out.println(m.getName());
            if(m.getName().trim().equals("setValue"))
                wcefSetValue = m;
        }


        EAP Method
        if(!noEnterpriseFieldType)
        {
                wcefSetValue.invoke(wcefEap.get(selectedConfig), ENTERPRISE_EAP);
        }
        else
        {
                wcefEap.set(selectedConfig, ENTERPRISE_EAP);
        }
        EAP Phase 2 Authentication
        if(!noEnterpriseFieldType)
        {
                wcefSetValue.invoke(wcefPhase2.get(selectedConfig), ENTERPRISE_PHASE2);
        }
        else
        {
              wcefPhase2.set(selectedConfig, ENTERPRISE_PHASE2);
        }
        EAP Anonymous Identity
        if(!noEnterpriseFieldType)
        {
                wcefSetValue.invoke(wcefAnonymousId.get(selectedConfig), ENTERPRISE_ANON_IDENT);
        }
        else
        {
              wcefAnonymousId.set(selectedConfig, ENTERPRISE_ANON_IDENT);
        }
        EAP CA Certificate
        if(!noEnterpriseFieldType)
        {
                wcefSetValue.invoke(wcefCaCert.get(selectedConfig), ENTERPRISE_CA_CERT);
        }
        else
        {
              wcefCaCert.set(selectedConfig, ENTERPRISE_CA_CERT);
        }               
        EAP Private key
        if(!noEnterpriseFieldType)
        {
                wcefSetValue.invoke(wcefPrivateKey.get(selectedConfig), ENTERPRISE_PRIV_KEY);
        }
        else
        {
              wcefPrivateKey.set(selectedConfig, ENTERPRISE_PRIV_KEY);
        }               
        EAP Identity
        if(!noEnterpriseFieldType)
        {
                wcefSetValue.invoke(wcefIdentity.get(selectedConfig), userName);
        }
        else
        {
              wcefIdentity.set(selectedConfig, userName);
        }               
        EAP Password
        if(!noEnterpriseFieldType)
        {
                wcefSetValue.invoke(wcefPassword.get(selectedConfig), passString);
        }
        else
        {
              wcefPassword.set(selectedConfig, passString);
        }               
        EAp Client certificate
        if(!noEnterpriseFieldType)
        {
            wcefSetValue.invoke(wcefClientCert.get(selectedConfig), ENTERPRISE_CLIENT_CERT);
        }
        else
        {
              wcefClientCert.set(selectedConfig, ENTERPRISE_CLIENT_CERT);
        }               
        // Adhoc for CM6
        // if non-CM6 fails gracefully thanks to nested try-catch

        try{
        Field wcAdhoc = WifiConfiguration.class.getField("adhocSSID");
        Field wcAdhocFreq = WifiConfiguration.class.getField("frequency");
        //wcAdhoc.setBoolean(selectedConfig, prefs.getBoolean(PREF_ADHOC,
        //      false));
        wcAdhoc.setBoolean(selectedConfig, false);
        int freq = 2462;    // default to channel 11
        //int freq = Integer.parseInt(prefs.getString(PREF_ADHOC_FREQUENCY,
        //"2462"));     // default to channel 11
        //System.err.println(freq);
        wcAdhocFreq.setInt(selectedConfig, freq); 
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    } catch (Exception e)
    {
        // TODO Auto-generated catch block
        // FIXME As above, what should I do here?
        e.printStackTrace();
    }

    WifiManager wifiManag = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    boolean res1 = wifiManag.setWifiEnabled(true);
    int res = wifiManag.addNetwork(selectedConfig);
    Log.d("WifiPreference", "add Network returned " + res );
    boolean b = wifiManag.enableNetwork(selectedConfig.networkId, false);
    Log.d("WifiPreference", "enableNetwork returned " + b );
    boolean c = wifiManag.saveConfiguration();
    Log.d("WifiPreference", "Save configuration returned " + c );
    boolean d = wifiManag.enableNetwork(res, true);   
    Log.d("WifiPreference", "enableNetwork returned " + d );  
*/}
	
	/**
	 * 配置wifi
	 * 
	 * @param SSID
	 * @param Password
	 * @param Type
	 * @param bRemoveWifi 是否清除原来的网络设置
	 * @return
	 */
	public WifiConfiguration createWifiInfo(String SSID, String Password, int Type, boolean bRemoveWifi) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";

		WifiConfiguration tempConfig = isExsits(SSID, mWifiManager);
		if (tempConfig != null) {
			if(Type==4 || Type==2 || !bRemoveWifi){ 
				config = tempConfig;
				return config;
			}
			else {
				mWifiManager.removeNetwork(tempConfig.networkId);
			}
		}
		
		if (Type == 1) // WIFICIPHER_NOPASS
		{
			/*config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;*/
			
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			config.allowedProtocols.set(Protocol.RSN);
			config.allowedProtocols.set(Protocol.WPA);
			config.allowedAuthAlgorithms.clear();
			config.allowedPairwiseCiphers.set(PairwiseCipher.CCMP);
			config.allowedPairwiseCiphers.set(PairwiseCipher.TKIP);
			config.allowedGroupCiphers.set(GroupCipher.WEP40);
			config.allowedGroupCiphers.set(GroupCipher.WEP104);
			config.allowedGroupCiphers.set(GroupCipher.CCMP);
			config.allowedGroupCiphers.set(GroupCipher.TKIP);
			
		}
		else if (Type == 2) // WIFICIPHER_WEP
		{
			/*config.hiddenSSID = true;
			config.wepKeys[0] = "\"" + Password + "\"";
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;*/
			
			//readWepConfig();
			
			/*config.hiddenSSID = true;
			config.status = WifiConfiguration.Status.DISABLED;     
			config.wepKeys[0] = "\"" + Password + "\"";
			config.priority = 40;
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN); 
			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.wepTxKeyIndex = 0;*/
		}
		else if (Type == 3) // WIFICIPHER_WPA
		{
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms
					.set(AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(GroupCipher.TKIP);
			config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers
					.set(PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(GroupCipher.CCMP);
			config.allowedPairwiseCiphers
					.set(PairwiseCipher.CCMP);
		}
		else if (Type == 4) // eap
		{
			config.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
            config.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
           // config.enterpriseConfig.setIdentity(identity);
            
            
            return config;
		}
		config.status = WifiConfiguration.Status.ENABLED;
		return config;
	}

	/**
	 * 判断wifi是否存在
	 * 
	 * @param SSID
	 * @param wifiManager
	 * @return
	 */
	private static WifiConfiguration isExsits(String SSID,
			WifiManager wifiManager) {
		List<WifiConfiguration> existingConfigs = wifiManager
				.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}
	
	/**
	 * 已经废除
	 * 
	 * 设置wifi的ip，dns，gateway等,
	 * 解决ipc作为AP是手机端分配ip不在同一网段问题
	 * @param c
	 * @param strDevSSID
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	public void setWifiStaticNet(Context c, String strDevSSID){
		WifiConfiguration wifiConf = null;
		WifiManager wifiManager = (WifiManager)c.getSystemService(Context.WIFI_SERVICE);
		WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();        
        for (WifiConfiguration conf : configuredNetworks){
            if (conf.networkId == connectionInfo.getNetworkId()){
                wifiConf = conf;
                break;              
            }
        }
		
        if(wifiConf==null) return;
        String strSSID = wifiConf.SSID.toString().trim();
        strSSID = strSSID.substring(1, strSSID.length()-1);
        if(!(strSSID.equals(strDevSSID))) return;
		try{
	        setIpAssignment("STATIC", wifiConf); //or "DHCP" for dynamic setting
	        setIpAddress(InetAddress.getByName("192.168.58.251"), 24, wifiConf);
	        setGateway(InetAddress.getByName("192.168.58.1"), wifiConf);
	        setDNS(InetAddress.getByName("4.4.4.4"), wifiConf);
	        mWifiManager.updateNetwork(wifiConf); //apply the setting
	    }
		catch(Exception e){
	        e.printStackTrace();
	    }
	}
	
	public static void setIpAssignment(String assign , WifiConfiguration wifiConf)
		    throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException{
        setEnumField(wifiConf, assign, "ipAssignment");     
    }

	public static void setIpAddress(InetAddress addr, int prefixLength, WifiConfiguration wifiConf)
			throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException,
				NoSuchMethodException, ClassNotFoundException, InstantiationException, InvocationTargetException{
        Object linkProperties = getField(wifiConf, "linkProperties");
        if(linkProperties == null)return;
        Class laClass = Class.forName("android.net.LinkAddress");
        Constructor laConstructor = laClass.getConstructor(new Class[]{InetAddress.class, int.class});
        Object linkAddress = laConstructor.newInstance(addr, prefixLength);

        ArrayList mLinkAddresses = (ArrayList)getDeclaredField(linkProperties, "mLinkAddresses");
        mLinkAddresses.clear();
        mLinkAddresses.add(linkAddress);        
    }

    public static void setGateway(InetAddress gateway, WifiConfiguration wifiConf)
    		throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException, 
    			ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException{
        Object linkProperties = getField(wifiConf, "linkProperties");
        if(linkProperties == null)return;
        Class routeInfoClass = Class.forName("android.net.RouteInfo");
        Constructor routeInfoConstructor = routeInfoClass.getConstructor(new Class[]{InetAddress.class});
        Object routeInfo = routeInfoConstructor.newInstance(gateway);

        ArrayList mRoutes = (ArrayList)getDeclaredField(linkProperties, "mRoutes");
        mRoutes.clear();
        mRoutes.add(routeInfo);
    }

    public static void setDNS(InetAddress dns, WifiConfiguration wifiConf)
    		throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException{
        Object linkProperties = getField(wifiConf, "linkProperties");
        if(linkProperties == null)return;

        ArrayList<InetAddress> mDnses = (ArrayList<InetAddress>)getDeclaredField(linkProperties, "mDnses");
        mDnses.clear(); //or add a new dns address , here I just want to replace DNS1
        mDnses.add(dns); 
    }

    public static Object getField(Object obj, String name)
    		throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
        Field f = obj.getClass().getField(name);
        Object out = f.get(obj);
        return out;
    }

    public static Object getDeclaredField(Object obj, String name)
    		throws SecurityException, NoSuchFieldException,
    				IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        Object out = f.get(obj);
        return out;
    }  

    public static void setEnumField(Object obj, String value, String name)
    		throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
        Field f = obj.getClass().getField(name);
        f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
    }
	
}
