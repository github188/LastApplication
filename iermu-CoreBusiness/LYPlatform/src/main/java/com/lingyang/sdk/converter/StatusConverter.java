package com.lingyang.sdk.converter;

import android.text.TextUtils;

import com.lingyang.sdk.model.MediaSource;
import com.lingyang.sdk.model.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 摄像机状态数据转换器
 *
 * Created by wcy on 15/8/4.
 */
public class StatusConverter {

    /**
     * 解析JSON数据
     * @param str
     * @return
     * @throws JSONException
     */
    public static Status fromJson(String str) throws JSONException {
        if(TextUtils.isEmpty(str)) return null;
        JSONObject json = new JSONObject(str);
        return fromJson(json);
    }

    /**
     * 解析JSON数据
     * @param json
     * @return
     */
    public static Status fromJson(JSONObject json) throws JSONException {
        String id           = json.optString(Field.ID);
        int statusT         = json.optInt(Field.Status);
        int natType         = json.optInt(Field.NatType);
        String userName     = json.optString(Field.UserName);
        boolean loginSucc   = optBoolean(json, Field.LoginSucc);
        int curMediaScrNo   = json.optInt(Field.CurMediaScrNo);
        int bufferCount     = json.optInt(Field.BufferCount);
        int connectTime     = json.optInt(Field.ConnectTime);
        String connectInfo  = json.optString(Field.Connecttion_Info);
        String gatewayIP    = json.optString(Field.GatewayIP);
        int lPopFrameRate   = json.optInt(Field.LPopFrameRate);
        int lPushFrameRate  = json.optInt(Field.LPushFrameRate);
        int lPushSpeed      = json.optInt(Field.LPushSpeed);
        int lRecvAvgSpeed   = json.optInt(Field.LRecvAvgSpeed);
        int lRecvSpeed      = json.optInt(Field.LRecvSpeed);
        int lSendAvgSpeed   = json.optInt(Field.LSendAvgSpeed);
        int lSendSpeed      = json.optInt(Field.LSendSpeed);
        int popDelay        = json.optInt(Field.PopDelay);
        int RPopFrameRate   = json.optInt(Field.RPopFrameRate);
        int RPushFrameRate  = json.optInt(Field.RPushFrameRate);
        int RSendAvgSpeed   = json.optInt(Field.RSendAvgSpeed);
        int RSendSpeed      = json.optInt(Field.RSendSpeed);
        long SessionID      = json.optLong(Field.SessionID);
        long StorageExpireTime= json.optLong(Field.StorageExpireTime);
        JSONArray array     = json.optJSONArray(Field.MediaSource);

        List<MediaSource> list = MediaSourceConverter.fromJson(array);
        Status status = new Status();
        status.setId(id);
        status.setStatus(statusT);
        status.setNatType(natType);
        status.setUserName(userName);
        status.setLoginSucc(loginSucc);
        status.setCurMediaScrNo(curMediaScrNo);
        status.setBufferCount(bufferCount);
        status.setConnectTime(connectTime);
        status.setConnectionInfo(connectInfo);
        status.setGatewayIP(gatewayIP);
        status.setlPopFrameRate(lPopFrameRate);
        status.setlPushFrameRate(lPushFrameRate);
        status.setlPushSpeed(lPushSpeed);
        status.setlRecvAvgSpeed(lRecvAvgSpeed);
        status.setlRecvSpeed(lRecvSpeed);
        status.setlSendAvgSpeed(lSendAvgSpeed);
        status.setlSendSpeed(lSendSpeed);
        status.setPopDelay(popDelay);
        status.setrPopFrameRate(RPopFrameRate);
        status.setrPushFrameRate(RPushFrameRate);
        status.setrSendAvgSpeed(RSendAvgSpeed);
        status.setrSendSpeed(RSendSpeed);
        status.setSessionID(SessionID);
        status.setStorageExpireTime(StorageExpireTime);
        status.setMediaList(list);
        return status;
    }

    private static boolean optBoolean(JSONObject json, String key) {
        return json.optInt(key)==1;
    }

    class Field {
        public static final String ID               = "ID";
        public static final String Status           = "Status";
        public static final String NatType          = "NatType";
        public static final String UserName         = "UserName";
        public static final String MediaSource      = "MediaSource";
        public static final String CurMediaScrNo    = "CurMediaScrNo";
        public static final String BufferCount      = "BufferCount";
        public static final String ConnectTime      = "ConnectTime";
        public static final String Connecttion_Info = "Connecttion Info";
        public static final String GatewayIP        = "GatewayIP";
        public static final String LPopFrameRate    = "LPopFrameRate";
        public static final String LPushFrameRate   = "LPushFrameRate";
        public static final String LPushSpeed       = "LPushSpeed";
        public static final String LRecvAvgSpeed    = "LRecvAvgSpeed";
        public static final String LRecvSpeed       = "LRecvSpeed";
        public static final String LSendAvgSpeed    = "LSendAvgSpeed";
        public static final String LSendSpeed       = "LSendSpeed";
        public static final String LoginSucc        = "LoginSucc";
        public static final String PopDelay         = "PopDelay";
        public static final String RPopFrameRate    = "RPopFrameRate";
        public static final String RPushFrameRate   = "RPushFrameRate";
        public static final String RSendAvgSpeed    = "RSendAvgSpeed";
        public static final String RSendSpeed       = "RSendSpeed";
        public static final String SessionID        = "SessionID";
        public static final String StorageExpireTime= "StorageExpireTime";
    }
}
