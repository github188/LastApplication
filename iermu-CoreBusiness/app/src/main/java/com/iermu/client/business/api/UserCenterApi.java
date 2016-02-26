package com.iermu.client.business.api;

import android.text.TextUtils;
import android.util.Log;

import com.baidu.pcs.BaiduPCSActionInfo;
import com.baidu.pcs.BaiduPCSClient;
import com.iermu.apiservice.ApiRoute;
import com.iermu.apiservice.service.UserCenterService;
import com.iermu.client.business.api.response.LiveMediaResponse;
import com.iermu.client.business.api.response.Response;
import com.iermu.client.util.Logger;
import com.iermu.client.util.LoggerUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

/**
 * Created by zhoushaopei on 16/1/19.
 */
public class UserCenterApi {

    @Inject
    static UserCenterService mApiService;

    public static Response apiGetClip(String accessToken) {
        Response response;
        String method = "list";
        String path = "/apps/iermu/clip";
        try {
            String res = mApiService.getPhotoClip(method, path, accessToken);
            response = Response.parseResponse(res);
        } catch (Exception e) {
            LoggerUtil.e("apiGetClip", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    public static String apiGetBaiduClip(String baiduAK) {
        try {
            StringBuilder filmJson = new StringBuilder();
            HttpClient client = new DefaultHttpClient();// 新建http客户端
            HttpParams httpParams = client.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 3000);// 设置连接超时范围
            HttpConnectionParams.setSoTimeout(httpParams, 5000);
            // serverPath是version.json的路径
            HttpResponse response = client.execute(new HttpGet(ApiRoute.V2_CLIPS+baiduAK));
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        entity.getContent(), "UTF-8"), 8192);
                String line = null;
                while ((line = reader.readLine()) != null) {
                    filmJson.append(line + "\n");// 按行读取放入StringBuilder中
                }
                reader.close();
            }
            Logger.i("apiGetBaiduClip", filmJson.toString());
            return filmJson.toString();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.i("apiGetBaiduClip", e);
            return "";
        }
    }
}
