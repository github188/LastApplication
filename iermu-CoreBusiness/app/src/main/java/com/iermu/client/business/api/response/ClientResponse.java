package com.iermu.client.business.api.response;

import android.text.TextUtils;

import com.iermu.client.business.api.converter.ClientConverter;
import com.iermu.client.model.NewPoster;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhoushaopei on 15/11/26.
 */
public class ClientResponse extends Response {

    public NewPoster client;

    /**
     * 解析服务端响应
     * @param str
     * @return
     * @throws org.json.JSONException
     */
    public static ClientResponse parseResponse(String str) throws JSONException {
        ClientResponse response = new ClientResponse();
        if(!TextUtils.isEmpty(str)) {
            JSONObject json = new JSONObject(str);
            response.parseJson(json);
        }
        return response;
    }

    /**
     * 解析服务端响应错误信息
     * @param exception
     * @return
     */
    public static ClientResponse parseResponseError(Exception exception) {
        ClientResponse response = new ClientResponse();
        response.parseError(exception);
        return response;
    }

    protected void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        client = ClientConverter.fromJson(json);
    }

    public NewPoster getClient() {
        return client;
    }

    public void setClient(NewPoster client) {
        this.client = client;
    }


}
