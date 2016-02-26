package com.iermu.client.business.api.converter;

import com.iermu.client.model.Email;

import org.json.JSONObject;

/**
 * 摄像机Email信息数据转换器
 *
 * Created by wcy on 15/7/8.
 */
public class CamEmailConverter {

    /**
     * 解析JSON数据
     * @param json
     * @return
     */
    public static Email fromJson(JSONObject json) {
        String from     = json.optString(Field.FROM);
        String to       = json.optString(Field.TO);
        String cc       = json.optString(Field.CC);
        String server   = json.optString(Field.SERVER);
        String user     = json.optString(Field.USER);
        String passwd   = json.optString(Field.PASSWD);
        String port     = json.optString(Field.PORT);

        Email email = new Email();
        email.setFrom(from);
        email.setTo(to);
        email.setCc(cc);
        email.setServer(server);
        email.setUser(user);
        email.setPasswd(passwd);
        email.setPort(port);
        return email;
    }

    class Field {
        public static final String FROM     = "mail_from";
        public static final String TO       = "mail_to";
        public static final String CC       = "mail_cc";
        public static final String SERVER   = "mail_server";
        public static final String USER     = "mail_user";
        public static final String PASSWD   = "mail_passwd";
        public static final String PORT     = "mail_port";
    }

}
