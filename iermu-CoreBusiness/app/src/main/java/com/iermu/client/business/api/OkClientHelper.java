package com.iermu.client.business.api;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class OkClientHelper {

    private static final int CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
    private static final int READ_TIMEOUT_MILLIS = 20 * 1000; // 20s

    private static OkHttpClient generateDefaultOkHttp() {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setReadTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setHostnameVerifier(DO_NOT_VERIFY);
        trustAllHosts(client);
        return client;
    }

    public final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
    public static void trustAllHosts(OkHttpClient client) {
        // Create a trust manager that does not validate certificate chains
        // Android use X509 cert
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        } };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            client.setSslSocketFactory(sc.getSocketFactory());
            //HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final OkHttpClient client;

    public OkClientHelper() {
        this(generateDefaultOkHttp());
    }

    public OkClientHelper(OkHttpClient client) {
        if (client == null) throw new NullPointerException("client == null");
        this.client = client;
    }

    public OkHttpClient buildOkHttpClient() {
        return client;
    }

    /**
     * 增加拦截器
     * @param interceptor
     */
    public void addInterceptor(Interceptor interceptor) {
        client.interceptors().add(interceptor);
    }
}
