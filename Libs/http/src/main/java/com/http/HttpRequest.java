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

package com.http;

import com.http.callback.RequestCallBackHandler;
import com.http.entity.UploadEntity;
import com.http.util.OtherUtils;
import com.http.util.URIBuilder;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.CloneUtils;
import org.apache.http.protocol.HTTP;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;

public class HttpRequest extends HttpRequestBase implements HttpEntityEnclosingRequest {

    private HttpEntity entity;

    private HttpMethod method;

    private URIBuilder uriBuilder;

    private Charset uriCharset;

    public HttpRequest(HttpMethod method) {
        super();
        this.method = method;
    }

    public HttpRequest(HttpMethod method, String uri) {
        super();
        this.method = method;
        setURI(uri);
    }

    public HttpRequest(HttpMethod method, URI uri) {
        super();
        this.method = method;
        setURI(uri);
    }

    public HttpRequest addQueryStringParameter(String name, String value) {
        uriBuilder.addParameter(name, value);
        return this;
    }

    public HttpRequest addQueryStringParameter(NameValuePair nameValuePair) {
        uriBuilder.addParameter(nameValuePair.getName(), nameValuePair.getValue());
        return this;
    }

    public HttpRequest addQueryStringParams(List<NameValuePair> nameValuePairs) {
        if (nameValuePairs != null) {
            for (NameValuePair nameValuePair : nameValuePairs) {
                uriBuilder.addParameter(nameValuePair.getName(), nameValuePair.getValue());
            }
        }
        return this;
    }

    public void setRequestParams(RequestParams param) {
        if (param != null) {
            if (uriCharset == null) {
                uriCharset = Charset.forName(param.getCharset());
            }
            List<RequestParams.HeaderItem> headerItems = param.getHeaders();
            if (headerItems != null) {
                for (RequestParams.HeaderItem headerItem : headerItems) {
                    if (headerItem.overwrite) {
                        this.setHeader(headerItem.header);
                    } else {
                        this.addHeader(headerItem.header);
                    }
                }
            }
            this.addQueryStringParams(param.getQueryStringParams());
            this.setEntity(param.getEntity());
        }
    }

    public void setRequestParams(RequestParams param, RequestCallBackHandler callBackHandler) {
        if (param != null) {
            if (uriCharset == null) {
                uriCharset = Charset.forName(param.getCharset());
            }
            List<RequestParams.HeaderItem> headerItems = param.getHeaders();
            if (headerItems != null) {
                for (RequestParams.HeaderItem headerItem : headerItems) {
                    if (headerItem.overwrite) {
                        this.setHeader(headerItem.header);
                    } else {
                        this.addHeader(headerItem.header);
                    }
                }
            }
            this.addQueryStringParams(param.getQueryStringParams());
            HttpEntity entity = param.getEntity();
            if (entity != null) {
                if (entity instanceof UploadEntity) {
                    ((UploadEntity) entity).setCallBackHandler(callBackHandler);
                }
                this.setEntity(entity);
            }
        }
    }

    @Override
    public URI getURI() {
        try {
            if (uriCharset == null) {
                uriCharset = OtherUtils.getCharsetFromHttpRequest(this);
            }
            if (uriCharset == null) {
                uriCharset = Charset.forName(HTTP.UTF_8);
            }
            return uriBuilder.build(uriCharset);
        } catch (URISyntaxException e) {
            //Logger.e(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void setURI(URI uri) {
        this.uriBuilder = new URIBuilder(uri);
    }

    public void setURI(String uri) {
        this.uriBuilder = new URIBuilder(uri);
    }

    @Override
    public String getMethod() {
        return this.method.toString();
    }

    @Override
    public HttpEntity getEntity() {
        return this.entity;
    }

    @Override
    public void setEntity(final HttpEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean expectContinue() {
        Header expect = getFirstHeader(HTTP.EXPECT_DIRECTIVE);
        return expect != null && HTTP.EXPECT_CONTINUE.equalsIgnoreCase(expect.getValue());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        HttpRequest clone = (HttpRequest) super.clone();
        if (this.entity != null) {
            clone.entity = (HttpEntity) CloneUtils.clone(this.entity);
        }
        return clone;
    }
}