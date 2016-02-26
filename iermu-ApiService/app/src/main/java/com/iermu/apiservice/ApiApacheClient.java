package com.iermu.apiservice;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import retrofit.client.ApacheClient;
import retrofit.client.Header;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedOutput;

/**
 * Api层 请求重试
 *
 * Created by wcy on 15/8/2.
 */
public class ApiApacheClient extends ApacheClient {

    /**
     * 最大连接数
     */
    public final static int MAX_TOTAL_CONNECTIONS = 800;
    /**
     * 获取连接的最大等待时间
     */
    public final static int WAIT_TIMEOUT = 60000;
    /**
     * 每个路由最大连接数
     */
    public final static int MAX_ROUTE_CONNECTIONS = 400;
    private static final int CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
    private static final int READ_TIMEOUT_MILLIS = 20 * 1000; // 20s
    private static final int DEFAULT_RETRY_TIMES		= 5;
    private int retriedTimes = 0;
    private static final HttpContext httpContext		= new BasicHttpContext();

    private static AbstractHttpClient createDefaultClient() {
        HttpParams params = new BasicHttpParams();
        // 设置最大连接数
        ConnManagerParams.setMaxTotalConnections(params, MAX_TOTAL_CONNECTIONS);
        // 设置获取连接的最大等待时间
        ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);
        // 设置每个路由最大连接数
        ConnPerRouteBean connPerRoute = new ConnPerRouteBean(MAX_ROUTE_CONNECTIONS);
        ConnManagerParams.setMaxConnectionsPerRoute(params,connPerRoute);
        HttpConnectionParams.setConnectionTimeout(params, CONNECT_TIMEOUT_MILLIS);
        HttpConnectionParams.setSoTimeout(params, READ_TIMEOUT_MILLIS);
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient(params);
        defaultHttpClient.setHttpRequestRetryHandler(new RetryHandler(DEFAULT_RETRY_TIMES));
        return defaultHttpClient;
    }

    private final AbstractHttpClient client;

    /** Creates an instance backed by {@link DefaultHttpClient}. */
    public ApiApacheClient() {
        this(createDefaultClient());
    }

    public ApiApacheClient(AbstractHttpClient client) {
        this.client = client;
    }

    @Override public Response execute(Request request) throws IOException {
        HttpUriRequest apacheRequest = createRequest(request);

        boolean retry = true;
        HttpRequestRetryHandler retryHandler = client.getHttpRequestRetryHandler();
        while (retry) {
            IOException exception = null;
            try {
                HttpResponse apacheResponse = execute(client, apacheRequest);
                return parseResponse(request.getUrl(), apacheResponse);
            } catch (UnknownHostException e) {
                exception = e;
                retry = retryHandler.retryRequest(exception, ++retriedTimes, httpContext);
            } catch (IOException e) {
                exception = e;
                retry = retryHandler.retryRequest(exception, ++retriedTimes, httpContext);
            } catch (NullPointerException e) {
                exception = new IOException(e.getMessage());
                exception.initCause(e);
                retry = retryHandler.retryRequest(exception, ++retriedTimes, httpContext);
            } catch (Throwable e) {
                exception = new IOException(e.getMessage());
                exception.initCause(e);
                retry = retryHandler.retryRequest(exception, ++retriedTimes, httpContext);
            }
            if (!retry && exception != null) {
                throw exception;
            }
        }
        return null;
    }

    /** Execute the specified {@code request} using the provided {@code client}. */
    protected HttpResponse execute(AbstractHttpClient client, HttpUriRequest request) throws IOException {
        return client.execute(request);
    }

    static HttpUriRequest createRequest(Request request) {
        if (request.getBody() != null) {
            return new GenericEntityHttpRequest(request);
        }
        return new GenericHttpRequest(request);
    }

    static Response parseResponse(String url, HttpResponse response) throws IOException {
        StatusLine statusLine = response.getStatusLine();
        int status = statusLine.getStatusCode();
        String reason = statusLine.getReasonPhrase();

        List<Header> headers = new ArrayList<Header>();
        String contentType = "application/octet-stream";
        for (org.apache.http.Header header : response.getAllHeaders()) {
            String name = header.getName();
            String value = header.getValue();
            if ("Content-Type".equalsIgnoreCase(name)) {
                contentType = value;
            }
            headers.add(new Header(name, value));
        }

        TypedByteArray body = null;
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            byte[] bytes = EntityUtils.toByteArray(entity);
            body = new TypedByteArray(contentType, bytes);
        }

        return new Response(url, status, reason, headers, body);
    }

    private static class GenericHttpRequest extends HttpRequestBase {
        private final String method;

        public GenericHttpRequest(Request request) {
            method = request.getMethod();
            setURI(URI.create(request.getUrl()));

            // Add all headers.
            for (Header header : request.getHeaders()) {
                addHeader(new BasicHeader(header.getName(), header.getValue()));
            }
        }

        @Override public String getMethod() {
            return method;
        }
    }

    private static class GenericEntityHttpRequest extends HttpEntityEnclosingRequestBase {
        private final String method;

        GenericEntityHttpRequest(Request request) {
            super();
            method = request.getMethod();
            setURI(URI.create(request.getUrl()));

            // Add all headers.
            for (Header header : request.getHeaders()) {
                addHeader(new BasicHeader(header.getName(), header.getValue()));
            }

            // Add the content body.
            setEntity(new TypedOutputEntity(request.getBody()));
        }

        @Override public String getMethod() {
            return method;
        }
    }

    /** Container class for passing an entire {@link TypedOutput} as an {@link HttpEntity}. */
    static class TypedOutputEntity extends AbstractHttpEntity {
        final TypedOutput typedOutput;

        TypedOutputEntity(TypedOutput typedOutput) {
            this.typedOutput = typedOutput;
            setContentType(typedOutput.mimeType());
        }

        @Override public boolean isRepeatable() {
            return true;
        }

        @Override public long getContentLength() {
            return typedOutput.length();
        }

        @Override public InputStream getContent() throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            typedOutput.writeTo(out);
            return new ByteArrayInputStream(out.toByteArray());
        }

        @Override public void writeTo(OutputStream out) throws IOException {
            typedOutput.writeTo(out);
        }

        @Override public boolean isStreaming() {
            return false;
        }
    }
}
