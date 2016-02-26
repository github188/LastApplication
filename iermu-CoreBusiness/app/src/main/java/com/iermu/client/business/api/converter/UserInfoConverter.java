package com.iermu.client.business.api.converter;

import com.iermu.client.model.UserInfo;

import org.json.JSONObject;

/**
 * Created by xjy on 15/8/27.
 */
public class UserInfoConverter {

    /**
     * 解析JSON数据
     * @param json
     * @return
     */
    public static UserInfo fromJson( JSONObject json) {
        String uid = json.optString(Field.UID);
        String username  = json.optString(Field.USERNAME);
        String email  = json.optString(Field.EMAIL);
        String avatar    = json.optString(Field.AVATAR);

        String mobile    = json.optString(Field.MOBILE);
        int emailStatus    = json.optInt(Field.EMAIL_STATUS);
        int mobileStatus    = json.optInt(Field.MOBILE_STATUS);
        int avatarStatus    = json.optInt(Field.AVATAR_STATUS);


        UserInfo personInfo = new UserInfo();
        personInfo.setUid(uid);
        personInfo.setUserName(username);
        personInfo.setEmail(email);
        personInfo.setAvatar(avatar);
        personInfo.setMobile(mobile);
        personInfo.setEmailStatus(emailStatus);
        personInfo.setMobileStatus(mobileStatus);
        personInfo.setAvatarStatus(avatarStatus);
        return personInfo;
    }

    class Field {
        public static final String UID          = "uid";
        public static final String USERNAME     = "username";
        public static final String EMAIL        = "email";
        public static final String AVATAR       = "avatar";
        public static final String MOBILE       = "mobile";
        public static final String EMAIL_STATUS = "emailstatus";
        public static final String MOBILE_STATUS= "mobilestatus";
        public static final String AVATAR_STATUS= "avatarstatus";

    }
}
