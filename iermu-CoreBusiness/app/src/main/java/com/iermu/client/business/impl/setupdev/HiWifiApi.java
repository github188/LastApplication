package com.iermu.client.business.impl.setupdev;

import android.util.Log;

import com.cms.iermu.cmsUtils;
import com.iermu.client.business.api.HttpRequestUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by zsj on 15/11/27.
 */
public class HiWifiApi {
    private static final String TAG = HiWifiApi.class.getSimpleName();

    static String mClientID = cmsUtils.getDevIMEI();

    // for 极路由测试
		/*http://client.openapi.hiwifi.com/router_info?local=1
			(需要 content-type:application/json)
			RESPONSE：
			code:0
			app_code:0
			表示在极路由下，否则不在*/
    public static boolean isHiwifi(){
        boolean ret = false;
        Map<String, String> params = new HashMap<String, String>();
        params.put("local", "1");
        try {
            String strUrl = "http://client.openapi.hiwifi.com/router_info";
            HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil
                    .sendGetRequest(strUrl, params, null);
            int retCode = conn.getResponseCode();  // 404:设备不存在
            if (retCode==200) {
                // 读取服务器返回信息
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String retData = null;
                String responseData = "";
                while ((retData = in.readLine()) != null) {
                    responseData += retData;
                }
                in.close();
                //Log.d(TAG, responseData);
                JSONObject retMsg = new JSONObject(responseData);
                int icode = retMsg.getInt("code");
                int iAppCode = retMsg.getInt("app_code");
                ret = (icode==0&&iAppCode==0);
            }
            conn.disconnect();// 终止连接
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /***********************************
     第2步：  获取 iot_local_token
     **********************************

     URL:
     http://client.openapi.hiwifi.com/generate_iot_local_token?local=1*/
    public static String[] getMacToken(){
        String[] ret = null;
        Map<String, String> params = new HashMap<String, String>();
        params.put("local", "1");
        try {
            String strUrl = "http://client.openapi.hiwifi.com/generate_iot_local_token";
            HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil
                    .sendGetRequest(strUrl, params, null);
            int retCode = conn.getResponseCode();  // 404:设备不存在
            if (retCode==200) {
                // 读取服务器返回信息
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String retData = null;
                String responseData = "";
                while ((retData = in.readLine()) != null) {
                    responseData += retData;
                }
                in.close();
                //Log.d(TAG, responseData);
                JSONObject retMsg = new JSONObject(responseData);
                JSONObject appData = new JSONObject(retMsg.getString("app_data"));
                String sMac = appData.getString("mac");
                String sToken = appData.getString("iot_local_token");
                ret = new String[]{sMac, sToken};
                if(mClientID==null || mClientID.equals("")) mClientID = Long.toString(System.currentTimeMillis());
            }
            conn.disconnect();// 终止连接
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static String sha1Hash(String toHash)
    {
        String hash = null;
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = toHash.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            bytes = digest.digest();

            // This is ~55x faster than looping and String.formating()
            hash = bytesToHex( bytes );
        }
        catch( NoSuchAlgorithmException e )
        {
            e.printStackTrace();
        }
        catch( UnsupportedEncodingException e )
        {
            e.printStackTrace();
        }
        return hash;
    }

    // http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
    final protected static char[] hexArray = "0123456789abcdefg".toCharArray();
    public static String bytesToHex( byte[] bytes )
    {
        char[] hexChars = new char[ bytes.length * 2 ];
        for( int j = 0; j < bytes.length; j++ )
        {
            int v = bytes[ j ] & 0xFF;
            hexChars[ j * 2 ] = hexArray[ v >>> 4 ];
            hexChars[ j * 2 + 1 ] = hexArray[ v & 0x0F ];
        }
        return new String( hexChars );
    }

    public static String strrev(String str){
        StringBuffer stringBuffer = new StringBuffer(str);
        return stringBuffer.reverse().toString();
    }

    /*
    **********************************
    第3步：  获取 client_secret
    **********************************
    URL:http://m.hiwifi.com/api/Open/IotLocalBind
    POST:
    client_id=abc&iot_local_token=xxx&mac=D4EE072544D6&app_name=iot-iermu&timestamp=1443164172&signature=3de6498e7115e8bc2ce81378da1b58107cf01a7d
    signature 算法见附件 lt.php
    $timestamp = time();
    $app_secret ="srtc821fe6cb3128864edf9d827675cd";
    $client_id = "iermu";
    $timestamp_str = $timestamp - intval(strrev(substr($timestamp, -6, 6)) * strrev(substr($timestamp, -4, 4)) / 2);
    $signature= sha1($timestamp_str . $app_secret . $client_id);
    */
    public static String getSecret(String sMac, String sToken){
        String ret = null;
        Long lT = System.currentTimeMillis()/1000;
        String st = lT.toString();
        int iLen = st.length();
        Long lt1 = Long.parseLong(strrev(st.substring(iLen-6)));
        Long lt2 = Long.parseLong(strrev(st.substring(iLen-4)));
        Long t = lT - (lt1*lt2)/2;
        String sSignature = t.toString() + "srtc821fe6cb3128864edf9d827675cd" + mClientID;
        sSignature = sha1Hash(sSignature);
        Map<String, String> params = new HashMap<String, String>();
        params.put("client_id", mClientID);
        params.put("iot_local_token", sToken);
        params.put("mac", sMac);
        params.put("app_name", "iot-iermu");
        params.put("timestamp", Long.toString(lT));
        params.put("signature", sSignature);
        //Log.d(TAG, "t=" + st + ", sig=" + sSignature + ", params=" + params);
        try {
            String strUrl = "http://m.hiwifi.com/api/Open/IotLocalBind";
            HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(strUrl, params, null);
            int retCode = conn.getResponseCode();  // 404:设备不存在
            if (retCode==200) {
                // 读取服务器返回信息
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String retData = null;
                String responseData = "";
                while ((retData = in.readLine()) != null) {
                    responseData += retData;
                }
                in.close();
                //Log.d(TAG, responseData);
                JSONObject retMsg = new JSONObject(responseData);
                int iCode = retMsg.getInt("code");
                if(iCode==0) {
                    ret = retMsg.getString("client_secret");
                }
            }
            conn.disconnect();// 终止连接
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

		/*
		**********************************
		第4步： 未配对的爱耳目列表     local.apps.iot-iermu.discover_list
		**********************************

		URL:
		http://client.openapi.hiwifi.com/call?sign=af8c78b12f562abe1935f559ce189485
		POST BODY:
		{"app_id":"710485188","dev_id":"D4EE070A44CC","app_name":"iot-iermu","method":"local.apps.iot-iermu.discover_list","client_id":"abc","data":[],"timeout":1000}
		"app_id":"710485188","dev_id":"D4EE072E3AC6","app_name":"iot-iermu","method":"local.apps.iot-iermu.discover_list","client_id":"iermu","mac":"[]","timeout":1000
		sign 算法见附件 openapi.php
		$app_key = '710485188';
		$app_secret = 'BFL5DQV3ACMIKYX';
		$app_name = "iot-iermu";
		$dev_id = "D4EE070A44CC";

		//获取设备列表
		$method = "apps.iot-iermu.discover_list";
		$data = array();
		$data["mac"] = "00:20:1B:1A:E2:6B"; // 耳目mac
		$aa = array();
		$aa['app_id'] = $app_key;
		$aa['dev_id'] = $dev_id;
		$aa['app_name'] = $app_name;
		$aa['method'] = $method;
		$aa['client_id'] = "abc";
		$aa['data'] = $data;
		$aa['timeout'] = 1000;
		$jsonbody = json_encode($aa);
		$action = 'call';
		$str = $action.$jsonbody.$app_secret;
		$sign = md5($str);
		*/

    //dev_id=极路由的mac，mac参数指的是耳目的mac地址
    static String getSig(String sJsonbody, String sClientSecret){
        String sign = null;
        String action = "call";
        //Log.d(TAG, "jsonbody=" + jsonbody);
        sign = action + sJsonbody + sClientSecret;
        sign = cmsUtils.MD5(sign);
        return sign;
    }

    public static String getIermu(String sMac, String sToken, String sClientSecret){
        String ret = null;
        String sMethod = "local.apps.iot-iermu.discover_list";
        String sData = "\"data\":[]";
        String sAppID = "{\"app_id\":\"710485188\"";
        String sDevID = "\"dev_id\":\"" + sMac + "\"";
        String sAppName = "\"app_name\":\"iot-iermu\"";
        String strMethod = "\"method\":\"" + sMethod + "\"";
        String sClientID = "\"client_id\":\"" + mClientID + "\"";
        String sTimeout = "\"timeout\":\"1000\"}";
        String jsonbody = sAppID + "," + sDevID + "," + sAppName + "," + strMethod + "," + sClientID + "," + sData + "," + sTimeout;

        String sSignature = getSig(jsonbody, sClientSecret);
        //Log.d(TAG, "sig=" + sSignature + "\nparams=" + jsonbody);
        try {
            String strUrl = "http://client.openapi.hiwifi.com/call?sign=" + sSignature;
            HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(strUrl, jsonbody, null);
            int retCode = conn.getResponseCode();  // 404:设备不存在
            if (retCode==200) {
                // 读取服务器返回信息
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String retData = null;
                String responseData = "";
                while ((retData = in.readLine()) != null) {
                    responseData += retData;
                }
                in.close();
                ret = responseData;
                //Log.d(TAG, responseData);
            }
            conn.disconnect();// 终止连接
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /*
    **********************************
    第5步：爱耳目入网                   local.apps.iot-iermu.add
    **********************************

    URL:
    http://client.openapi.hiwifi.com/call?sign=9b922b6a14cfbf97a987a2567a5068c2

    POST BODY:
    {"app_id":"710485188","dev_id":"D4EE072544D6","app_name":"iot-iermu","method":"local.apps.iot-iermu.add","client_id":"abc","data":{"mac":"00:20:1B:1A:E2:6B"},"timeout":1000}

    sign 算法见附件 openapi.php
    */
    public static String addIermu(String sErmuMac, String sHiwifiMac, String sClientSecret){
        String ret = null;
        String sMethod = "local.apps.iot-iermu.add";
        String sData = "\"data\":{\"mac\":\"" + sErmuMac + "\"}";
        String sAppID = "{\"app_id\":\"710485188\"";
        String sDevID = "\"dev_id\":\"" + sHiwifiMac + "\"";
        String sAppName = "\"app_name\":\"iot-iermu\"";
        String strMethod = "\"method\":\"" + sMethod + "\"";
        String sClientID = "\"client_id\":\"" + mClientID + "\"";
        String sTimeout = "\"timeout\":\"1000\"}";
        String jsonbody = sAppID + "," + sDevID + "," + sAppName + "," + strMethod + "," + sClientID + "," +
                sData + "," + sTimeout;

        String sSignature = getSig(jsonbody, sClientSecret);
        //Log.d(TAG, "sig=" + sSignature + "\nparams=" + jsonbody);

        try {
            String strUrl = "http://client.openapi.hiwifi.com/call?sign=" + sSignature;
            HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(strUrl, jsonbody, null);
            int retCode = conn.getResponseCode();  // 404:设备不存在
            if (retCode==200) {
                // 读取服务器返回信息
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String retData = null;
                String responseData = "";
                while ((retData = in.readLine()) != null) {
                    responseData += retData;
                }
                in.close();
                //Log.d(TAG, responseData);
                ret = responseData;
            }
            conn.disconnect();// 终止连接
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    // 第六步： 获取ip， local.apps.iot-iermu.get_ip
    public static String getIermuIp(String sErmuMac, String sHiwifiMac, String sClientSecret){
        String ret = null;
        String sMethod = "local.apps.iot-iermu.get_ip";
        String sData = "\"data\":{\"mac\":\"" + sErmuMac + "\"}";
        String sAppID = "{\"app_id\":\"710485188\"";
        String sDevID = "\"dev_id\":\"" + sHiwifiMac + "\"";
        String sAppName = "\"app_name\":\"iot-iermu\"";
        String strMethod = "\"method\":\"" + sMethod + "\"";
        String sClientID = "\"client_id\":\"" + mClientID + "\"";
        String sTimeout = "\"timeout\":\"1000\"}";
        String jsonbody = sAppID + "," + sDevID + "," + sAppName + "," + strMethod + "," + sClientID + "," +
                sData + "," + sTimeout;

        String sSignature = getSig(jsonbody, sClientSecret);
        Log.d(TAG, "sig=" + sSignature + "\nparams=" + jsonbody);

        try {
            String strUrl = "http://client.openapi.hiwifi.com/call?sign=" + sSignature;
            HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(strUrl, jsonbody, null);
            int retCode = conn.getResponseCode();  // 404:设备不存在
            if (retCode==200) {
                // 读取服务器返回信息
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String retData = null;
                String responseData = "";
                while ((retData = in.readLine()) != null) {
                    responseData += retData;
                }
                in.close();
                //Log.d(TAG, responseData);
                ret = responseData;
            }
            conn.disconnect();// 终止连接
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
