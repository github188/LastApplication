package com.iermu.client.business.api;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

/**
 * 基础Http请求(以后更换)
 *
 * Created by wcy on 15/6/24.
 */
public class BaseHttpApi {

    /**
     * 构造Get请求URL
     * @param baseUrl
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     */
    protected static URL buildURL(String baseUrl, Map<String, String> params) throws UnsupportedEncodingException, MalformedURLException {
        StringBuilder buf = new StringBuilder(baseUrl);
        Set<Map.Entry<String, String>> entrys;
        if (params != null && !params.isEmpty()) {
            buf.append("?");
            entrys = params.entrySet();
            for (Map.Entry<String, String> entry : entrys) {
                buf.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                        .append("&");
            }
            buf.deleteCharAt(buf.length() - 1);
        }
        return new URL(buf.toString());
    }

//    /**
//     * 发送HTTP GET请求
//     * @param url	请求URL地址
//     * @param request	请求参数
//     * @return
//     * @throws JSONException
//     * @throws HttpException
//     * @throws UnsupportedEncodingException
//     * @throws IOException
//     */
//    protected static String get(String url, Request request) throws JSONException, HttpException, UnsupportedEncodingException, IOException {
//        JSONObject args = request.toJson();
//        //RequestParams params = new RequestParams();
//        //params.setContentType(AppConfig.CONTENTTYPE);
//        //params.setBodyEntity(new StringEntity(args.toString(), HTTP.UTF_8));
//        RequestParams params = new RequestParams();
//        params.setContentType(AppConfig.CONTENTTYPE);
//        Iterator<?> keys = args.keys();
//        while(keys.hasNext()) {
//            String next = (String) keys.next();
//            Object value = args.get(next);
//            if(value instanceof JSONObject) {
//                JSONObject json = (JSONObject)value;
//                Iterator<?> keys2 = json.keys();
//                while(keys2.hasNext()) {
//                    String next2 = (String) keys2.next();
//                    Object value2 = json.get(next2);
//                    params.addQueryStringParameter(next2, value2.toString());
//                }
//            } else {
//                params.addQueryStringParameter(next, value.toString());
//            }
//        }
//        Logger.oInfo("Get: url=" + url + " param=" + args);
//        return get(url, params);
//    }
//
//    /**
//     * 发送HTTP GE请求
//     * @param url
//     * @return
//     * @throws HttpException
//     * @throws IOException
//     */
//    protected String get(String url) throws HttpException, IOException {
//        AppHttpClient client	= AppHttpClient.newInstance();
//        //client.configUserAgent()
//        //client.configCookieStore(cookieStore);
//        String response = client.executeGet(url);
//        Logger.oInfo("Get: url="+ url +" response="+ response);
//        return response;
//    }
//
//    /**
//     * 发送HTTP GE请求
//     * @param url	请求URL地址
//     * @param param	GET请求参数
//     * @return
//     * @throws HttpException
//     * @throws IOException
//     */
//    private static String get(String url, RequestParams param) throws HttpException, IOException {
//        AppHttpClient client	= AppHttpClient.newInstance();
//        String response = client.executeGet(url, param);
//        Logger.oInfo("Get: url="+ url +" response="+ response);
//        return response;
//    }
//
//    /**
//     * 发送HTTP POST请求
//     * @param url	请求URL地址
//     * @param request	请求参数
//     * @return
//     * @throws JSONException
//     * @throws HttpException
//     * @throws UnsupportedEncodingException
//     * @throws IOException
//     */
//    protected static String post(String url, Request request) throws JSONException, HttpException, UnsupportedEncodingException, IOException {
//        JSONObject args = request.toJson();
//        RequestParams params = new RequestParams();
//        params.setContentType(AppConfig.CONTENTTYPE);
//        Iterator<String> keys = args.keys();
//        while(keys.hasNext()) {
//            String next = keys.next();
//            params.addBodyParameter(next, args.getString(next));
//        }
//        //TODO 修改
//        //params.addBodyParameter();
//        //params.setBodyEntity(new StringEntity(args.toString(), HTTP.UTF_8));
//        return post(url, params);
//    }
//
//    /**
//     * 发送HTTP POST请求
//     * @param url	请求URL地址
//     * @param request	请求参数
//     * @return
//     * @throws JSONException
//     * @throws HttpException
//     * @throws UnsupportedEncodingException
//     * @throws IOException
//     */
//    protected static String post(String url, Requests request) throws JSONException, HttpException, UnsupportedEncodingException, IOException {
//        JSONArray args = request.toJson();
//        RequestParams params = new RequestParams();
//        params.setContentType(AppConfig.CONTENTTYPE);
//        params.setBodyEntity(new StringEntity(args.toString(), HTTP.UTF_8));
//        return post(url, params);
//    }
//
//    /**
//     * 发送HTTP POST请求
//     * @param url
//     * @return
//     * @throws HttpException
//     * @throws IOException
//     */
//    protected String post(String url) throws HttpException, IOException {
//        AppHttpClient client	= AppHttpClient.newInstance();
//        String response = client.executePost(url);
//        Logger.oInfo("Post: url="+ url +" response="+ response);
//        return response;
//    }
//
//    /**
//     * 发送HTTP POST请求
//     * @param url	  请求URL地址
//     * @param params POST请求参数
//     * @return
//     * @throws HttpException
//     * @throws IOException
//     */
//    protected static String post(String url, RequestParams params) throws HttpException, IOException {
//        AppHttpClient client	= AppHttpClient.newInstance();
//        String response = client.executePost(url, params);
//        Logger.oInfo("Post: url="+ url +" response="+ response);
//        return response;
//    }

}
