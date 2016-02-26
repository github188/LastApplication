package com.http;

import com.http.exception.HttpException;

import org.apache.http.client.CookieStore;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.IOException;

public interface IHttpClient {

	/**
	 * 执行GET请求
	 * @param url	请求URL地址
	 * @return
	 */
	public String executeGet(String url) throws HttpException, IOException, HttpException;

	/**
	 * 执行POST请求
	 * @param url	请求URL地址
	 * @return
	 */
	public String executePost(String url) throws HttpException, IOException;

	/**
	 * 执行GET请求
	 * @param url		请求URL地址
	 * @param params	请求参数
	 * @return
	 */
	public String executeGet(String url, RequestParams params) throws HttpException, IOException;

	/**
	 * 执行POST请求
	 * @param url		请求URL地址
	 * @param params	请求参数
	 * @return
	 */
	public String executePost(String url, RequestParams params) throws HttpException, IOException;

	/**
	 * 配置HTTP响应字符集
	 * 
	 * @param charset
	 * @return
	 */
	public IHttpClient configResponseCharset(String charset);

	//public IHttpClient configHttpRedirectHandler(HttpRedirectHandler httpRedirectHandler);
    //public IHttpClient configDefaultHttpCacheExpiry(long defaultExpiry);
    //public IHttpClient configCurrentHttpCacheExpiry(long currRequestExpiry);

	/**
	 * 配置Cookie缓存
	 * 
	 * @param cookieStore
	 * @return
	 */
    public IHttpClient configCookieStore(CookieStore cookieStore);

    /**
     * 配置UserAgent
     * 
     * @param userAgent
     * @return
     */
    public IHttpClient configUserAgent(String userAgent);

    /**
     * 配置HTTP请求超时时间
     * 
     * @param timeout
     * @return
     */
    public IHttpClient configTimeout(int timeout);

    /**
     * 配置HTTP协议Scheme
     * @param scheme
     * @return
     */
    public IHttpClient configRegisterScheme(Scheme scheme);

    /**
     * 配置SSL协议
     * 
     * @param sslSocketFactory
     * @return
     */
    public IHttpClient configSSLSocketFactory(SSLSocketFactory sslSocketFactory);

    /**
     * 配置请求重试次数
     * 
     * @param count
     * @return
     */
    public IHttpClient configRequestRetryCount(int count);

    /**
     * 配置请求线程池大小
     * 
     * @param threadPoolSize
     * @return
     */
    public IHttpClient configRequestThreadPoolSize(int threadPoolSize);

}
