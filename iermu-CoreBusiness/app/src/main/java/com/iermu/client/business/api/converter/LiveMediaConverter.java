package com.iermu.client.business.api.converter;

import com.iermu.client.model.LiveMedia;
import com.iermu.client.model.constant.ConnectType;

import org.json.JSONObject;

/**
 * 摄像机直播数据转换器
 *
 * Created by wcy on 15/7/29.
 */
public class LiveMediaConverter {

    /**
     * 解析JSON数据
     *
     * @param deviceId
     * @param json
     * @return
     */
    public static LiveMedia fromJson(String deviceId, JSONObject json) {
        int status      = json.optInt(Field.STATUS);
        int playNum     = json.optInt(Field.PLAYNUM);
        int maxPlayNum  = json.optInt(Field.MAX_PLAYNUM);
        int userPlayNum = json.optInt(Field.USER_PLAYNUM);
        String desc     = json.optString(Field.DESCRIPTION);
        String url      = json.optString(Field.URL);
        String type     = json.optString(Field.TYPE);
        int connectType = json.optInt(Field.CONNECT_TYPE);
        int lyStatus    = json.optInt(Field.LY_STATUS);
        String devToken = json.optString(Field.DEV_TOKEN);
        String trackIp  = json.optString(Field.TRACT_IP);
        int trackPort   = json.optInt(Field.TRACT_PORT);
        long expiresIn  = json.optLong(Field.EXPIRES_IN);

        LiveMedia media = new LiveMedia();
        media.setStatus(status);
        media.setDeviceId(deviceId);
        media.setConnectType(ConnectType.BAIDU);
        media.setDescription(desc);
        media.setPlayUrl(url);
        media.setType(type);
        media.setConnectType(connectType);
        media.setLyStatus(lyStatus);
        media.setDevToken(devToken);
        media.setExpiresIn(expiresIn);
        media.setTrackIp(trackIp);
        media.setTrackPort(trackPort);
        media.setPlayNum(playNum);
        media.setMaxPlayNum(maxPlayNum);
        media.setUserPlayNum(userPlayNum);
        return media;
    }

    class Field {
        public static final String STATUS       = "status";
        public static final String PLAYNUM      = "playnum";
        public static final String MAX_PLAYNUM  = "max_playnum";
        public static final String USER_PLAYNUM = "user_playnum";
        public static final String DESCRIPTION  = "description";
        public static final String URL          = "url";
        public static final String TYPE         = "type";
        public static final String CONNECT_TYPE = "connect_type";
        public static final String LY_STATUS    = "state";
        public static final String EXPIRES_IN   = "expires_in";
        public static final String DEV_TOKEN    = "access_token";
        public static final String TRACT_IP     = "tracker_ip";
        public static final String TRACT_PORT   = "tracker_port";
    }

}
