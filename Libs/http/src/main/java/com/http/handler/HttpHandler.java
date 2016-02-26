/*
 * Copyright (c) 2013. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.http.handler;

import com.http.ResponseStream;
import com.http.exception.HttpException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.UnknownHostException;

public class HttpHandler {

    private final AbstractHttpClient client;
    private final HttpContext context;

    private HttpRedirectHandler httpRedirectHandler;

    public void setHttpRedirectHandler(HttpRedirectHandler httpRedirectHandler) {
        this.httpRedirectHandler = httpRedirectHandler;
    }

    private String requestUrl;
    private String requestMethod;
    private String charset; // The default charset of response header info.

    private int retriedTimes = 0;

    public HttpHandler(AbstractHttpClient client, HttpContext context, String charset) {
        this.client = client;
        this.context = context;
        this.charset = charset;
    }

    public ResponseStream sendRequest(HttpRequestBase request) throws HttpException {

        boolean retry = true;
        HttpRequestRetryHandler retryHandler = client.getHttpRequestRetryHandler();
        while (retry) {
            IOException exception = null;
            try {
                requestUrl = request.getURI().toString();
                requestMethod = request.getMethod();
                HttpResponse response = client.execute(request, context);
                return handleResponse(response);
            } catch (UnknownHostException e) {
                exception = e;
                retry = retryHandler.retryRequest(exception, ++retriedTimes, context);
            } catch (IOException e) {
                exception = e;
                retry = retryHandler.retryRequest(exception, ++retriedTimes, context);
            } catch (NullPointerException e) {
                exception = new IOException(e.getMessage());
                exception.initCause(e);
                retry = retryHandler.retryRequest(exception, ++retriedTimes, context);
            } catch (HttpException e) {
                throw e;
            } catch (Throwable e) {
                exception = new IOException(e.getMessage());
                exception.initCause(e);
                retry = retryHandler.retryRequest(exception, ++retriedTimes, context);
            }
            if (!retry && exception != null) {
                throw new HttpException(exception);
            }
        }
        return null;
    }

    private ResponseStream handleResponse(HttpResponse response) throws HttpException, IOException {
        if (response == null) {
            throw new HttpException("response is null");
        }
        StatusLine status = response.getStatusLine();
        int statusCode = status.getStatusCode();
        if (statusCode == 301 || statusCode == 302) {
            if (httpRedirectHandler == null) {
                httpRedirectHandler = (HttpRedirectHandler) new DefaultHttpRedirectHandler();
            }
            HttpRequestBase request = httpRedirectHandler.getDirectRequest(response);
            if (request != null) {
                return this.sendRequest(request);
            }
        }
        //else if (statusCode < 300) {
        else {
            ResponseStream responseStream = new ResponseStream(response, charset, requestUrl);
            responseStream.setRequestMethod(requestMethod);
            return responseStream;
        }
//        else if (statusCode == 416) {
//            throw new HttpException(statusCode, "maybe the file has downloaded completely");
//        } else {
//            throw new HttpException(statusCode, status.getReasonPhrase());
//        }
//        if (statusCode < 300) {
//            ResponseStream responseStream = new ResponseStream(response, charset, requestUrl);
//            responseStream.setRequestMethod(requestMethod);
//            return responseStream;
//        } else if (statusCode == 301 || statusCode == 302) {
//            if (httpRedirectHandler == null) {
//                httpRedirectHandler = (HttpRedirectHandler) new DefaultHttpRedirectHandler();
//            }
//            HttpRequestBase request = httpRedirectHandler.getDirectRequest(response);
//            if (request != null) {
//                return this.sendRequest(request);
//            }
//        } else if (statusCode == 416) {
//            throw new HttpException(statusCode, "maybe the file has downloaded completely");
//        } else {
//            throw new HttpException(statusCode, status.getReasonPhrase());
//        }
        return null;
    }
}
