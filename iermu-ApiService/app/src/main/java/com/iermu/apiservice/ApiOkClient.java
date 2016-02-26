package com.iermu.apiservice;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okio.BufferedSink;
import retrofit.client.Header;
import retrofit.client.OkClient;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Api层 OkHttpClient
 *
 * Created by wcy on 15/8/12.
 */
public class ApiOkClient extends OkClient {

    private static final int CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
    private static final int READ_TIMEOUT_MILLIS = 20 * 1000; // 20s
    private static final int DEFAULT_RETRY_TIMES		= 5;
    private Interceptor responseInterceptor;

    private static OkHttpClient generateDefaultOkHttp() {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setReadTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setHostnameVerifier(DO_NOT_VERIFY);
        client.setRetryOnConnectionFailure(true);
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
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
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

    public ApiOkClient() {
        this(generateDefaultOkHttp());
    }

    public ApiOkClient(OkHttpClient client) {
        if (client == null) throw new NullPointerException("client == null");
        this.client = client;
    }

    @Override public Response execute(Request request) throws IOException {
        return parseResponse(client.newCall(createRequest(request)).execute());
    }

    static com.squareup.okhttp.Request createRequest(Request request) {
        com.squareup.okhttp.Request.Builder builder = new com.squareup.okhttp.Request.Builder()
                .url(request.getUrl())
                .method(request.getMethod(), createRequestBody(request.getBody()));

        List<Header> headers = request.getHeaders();
        for (int i = 0, size = headers.size(); i < size; i++) {
            Header header = headers.get(i);
            builder.addHeader(header.getName(), header.getValue());
        }

        return builder.build();
    }

    static Response parseResponse(com.squareup.okhttp.Response  response) throws IOException {
        return new Response(response.request().urlString(), response.code(), response.message(),
                createHeaders(response.headers()), createResponseBody(response.body()));
    }

    private static RequestBody createRequestBody(final TypedOutput body) {
        if (body == null) {
            return null;
        }
        final MediaType mediaType = MediaType.parse(body.mimeType());
        return new RequestBody() {
            @Override public MediaType contentType() {
                return mediaType;
            }

            @Override public void writeTo(BufferedSink sink) throws IOException {
                body.writeTo(sink.outputStream());
            }

            @Override public long contentLength() {
                return body.length();
            }
        };
    }

    private static TypedInput createResponseBody(final ResponseBody body) throws IOException {
        if (body.contentLength() == 0) {
            return null;
        }
        return new TypedInput() {
            @Override public String mimeType() {
                MediaType mediaType = body.contentType();
                return mediaType == null ? null : mediaType.toString();
            }

            @Override public long length() {
                try {
                    return body.contentLength();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return 0;
            }

            @Override public InputStream in() throws IOException {
                return body.byteStream();
            }
        };
    }

    private static List<Header> createHeaders(Headers headers) {
        int size = headers.size();
        List<Header> headerList = new ArrayList<Header>(size);
        for (int i = 0; i < size; i++) {
            headerList.add(new Header(headers.name(i), headers.value(i)));
        }
        return headerList;
    }

    /**
     * 增加拦截器
     * @param interceptor
     */
    public void addInterceptor(Interceptor interceptor) {
        client.interceptors().add(interceptor);
    }
}
