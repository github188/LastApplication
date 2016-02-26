package com.iermu.client.business.api.request;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wcy on 15/7/19.
 */
public class BaiduTokenRequest extends Request {

    private String  grantType;
    private String  code;
    private String  clientId;
    private String  clientSecret;
    private String  redirectUri;

    public BaiduTokenRequest(String accessCode, String clientId, String clientSecret, String redirectUri) {
        this.grantType  = "authorization_code";
        this.code       = accessCode;
        this.clientId   = clientId;
        this.clientSecret= clientSecret;
        this.redirectUri = redirectUri;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(Field.GRANT_TYPE,  grantType);
        json.put(Field.CODE,        code);
        json.put(Field.CLIENT_ID,   clientId);
        json.put(Field.CLIENT_SECRET, clientSecret);
        json.put(Field.REDIRECT_URI, redirectUri);
        return json;
    }

    class Field {
        public static final String GRANT_TYPE   = "grant_type";
        public static final String CODE         = "code";
        public static final String CLIENT_ID    = "client_id";
        public static final String CLIENT_SECRET= "client_secret";
        public static final String REDIRECT_URI = "redirect_uri";
    }
}
