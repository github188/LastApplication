package com.http;

import android.text.TextUtils;

import com.http.entity.GZipDecompressingEntity;
import com.http.exception.HttpException;
import com.http.handler.HttpHandler;
import com.http.handler.HttpRedirectHandler;
import com.http.handler.RetryHandler;
import com.http.util.SimpleSSLSocketFactory;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


@SuppressWarnings("unused")
public class AppHttpClient implements IHttpClient {

    private static final ThreadFactory sThreadFactory	= new CustomThreadFactory();
    private static final HttpContext httpContext		= new BasicHttpContext();
    private static final String HEADER_ACCEPT_ENCODING	= "Accept-Encoding";
    private static final String ENCODING_GZIP			= "gzip";
    private static final int DEFAULT_CONN_TIMEOUT		= 1000 * 15;// 15s
    private static final int DEFAULT_RETRY_TIMES		= 5;
    private static int threadPoolSize					= 3;
    private static Executor executor					= Executors.newFixedThreadPool(threadPoolSize, sThreadFactory);
    private final DefaultHttpClient	 httpClient;
    //TODO delete	private static final HttpCache sHttpCache		= new HttpCache();

    private int connTimeout							= 1000 * 6;// 6s
    private String responseCharset					= HTTP.UTF_8;
	private HttpRedirectHandler httpRedirectHandler;
    //TODO delete	private long currentRequestExpiry				= HttpCache.getDefaultExpiryTime();

	public static AppHttpClient newInstance() {
		return new AppHttpClient(); 
	}

	public AppHttpClient() {
		HttpParams params = new BasicHttpParams();

        ConnManagerParams.setTimeout(params, connTimeout);
        HttpConnectionParams.setSoTimeout(params, connTimeout);
        HttpConnectionParams.setConnectionTimeout(params, connTimeout);

        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(10));
        ConnManagerParams.setMaxTotalConnections(params, 10);

        HttpConnectionParams.setTcpNoDelay(params, true);
        HttpConnectionParams.setSocketBufferSize(params, 1024 * 8);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SimpleSSLSocketFactory.getSocketFactory(), 443));

        httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params, schemeRegistry), params);
        httpClient.setHttpRequestRetryHandler(new RetryHandler(DEFAULT_RETRY_TIMES));
        httpClient.addRequestInterceptor(httpRequestInterceptor);
        httpClient.addResponseInterceptor(httpResponseInterceptor);
	}

	@Override
	public String executeGet(String url) throws HttpException, IOException {
		HttpRequest request = new HttpRequest(HttpMethod.GET, url);
		HttpHandler handler = new HttpHandler(httpClient, httpContext, responseCharset);
        handler.setHttpRedirectHandler(httpRedirectHandler);
        ResponseStream response = handler.sendRequest(request);
        return response.readString();
	}

	@Override
	public String executePost(String url) throws HttpException, IOException {
		HttpRequest request = new HttpRequest(HttpMethod.POST, url);
		HttpHandler handler = new HttpHandler(httpClient, httpContext, responseCharset);
        handler.setHttpRedirectHandler(httpRedirectHandler);
        ResponseStream response = handler.sendRequest(request);
        return response.readString();
	}

	@Override
	public String executeGet(String url, RequestParams params) throws HttpException, IOException {
		HttpRequest request = new HttpRequest(HttpMethod.GET, url);
		HttpHandler handler = new HttpHandler(httpClient, httpContext, responseCharset);
        handler.setHttpRedirectHandler(httpRedirectHandler);
        request.setRequestParams(params);
        ResponseStream response = handler.sendRequest(request);
        return response.readString();
	}

	@Override
	public String executePost(String url, RequestParams params) throws HttpException, IOException {
		HttpRequest request = new HttpRequest(HttpMethod.POST, url);
		HttpHandler handler = new HttpHandler(httpClient, httpContext, responseCharset);
        handler.setHttpRedirectHandler(httpRedirectHandler);
        request.setRequestParams(params);
        ResponseStream response = handler.sendRequest(request);
        return response.readString();
	}

	@Override
	public IHttpClient configResponseCharset(String charset) {
		if(!TextUtils.isEmpty(charset)) {
			this.responseCharset = charset;
		}
		return this;
	}

	@Override
	public IHttpClient configCookieStore(CookieStore cookieStore) {
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	    return this;
	}

	@Override
	public IHttpClient configUserAgent(String userAgent) {
		HttpProtocolParams.setUserAgent(httpClient.getParams(), userAgent);
		return this;
	}

	@Override
	public IHttpClient configTimeout(int timeout) {
		HttpParams httpParams = httpClient.getParams();
		ConnManagerParams.setTimeout(httpParams, timeout);
		HttpConnectionParams.setSoTimeout(httpParams, timeout);
		HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
		return this;
	}

	@Override
	public IHttpClient configRegisterScheme(Scheme scheme) {
		httpClient.getConnectionManager().getSchemeRegistry().register(scheme);
        return this;
	}

	@Override
	public IHttpClient configSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
		Scheme scheme = new Scheme("https", sslSocketFactory, 443);
        httpClient.getConnectionManager().getSchemeRegistry().register(scheme);
        return this;
	}

	@Override
	public IHttpClient configRequestRetryCount(int count) {
		httpClient.setHttpRequestRetryHandler(new RetryHandler(count));
        return this;
	}

	@Override
	public IHttpClient configRequestThreadPoolSize(int threadPoolSize) {
		if (threadPoolSize > 0 && threadPoolSize != AppHttpClient.threadPoolSize) {
			AppHttpClient.threadPoolSize = threadPoolSize;
			AppHttpClient.executor = Executors.newFixedThreadPool(threadPoolSize, sThreadFactory);
        }
        return this;
	}

	HttpRequestInterceptor httpRequestInterceptor = new HttpRequestInterceptor() {
        @Override
        public void process(org.apache.http.HttpRequest httpRequest, HttpContext httpContext) throws org.apache.http.HttpException, IOException {
            if (!httpRequest.containsHeader(HEADER_ACCEPT_ENCODING)) {
                httpRequest.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
            }
        }
    };

	HttpResponseInterceptor httpResponseInterceptor = new HttpResponseInterceptor() {
        @Override
        public void process(HttpResponse response, HttpContext httpContext) throws org.apache.http.HttpException, IOException {
            final HttpEntity entity = response.getEntity();
            if (entity == null) {
                return;
            }
            final Header encoding = entity.getContentEncoding();
            if (encoding != null) {
                for (HeaderElement element : encoding.getElements()) {
                    if (element.getName().equalsIgnoreCase("gzip")) {
                        response.setEntity(new GZipDecompressingEntity(response.getEntity()));
                        return;
                    }
                }
            }
        }
    };

    static class CustomThreadFactory implements ThreadFactory {
    	 private final AtomicInteger mCount = new AtomicInteger(1);
         @Override
         public Thread newThread(Runnable r) {
             Thread thread = new Thread(r, "AppHttpClient #" + mCount.getAndIncrement());
             thread.setPriority(Thread.NORM_PRIORITY - 1);
             return thread;
         }
    }
}