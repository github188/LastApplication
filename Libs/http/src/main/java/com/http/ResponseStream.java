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

import com.http.util.IOUtils;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

public class ResponseStream extends InputStream {

    private HttpResponse baseResponse;
    private InputStream baseStream;

    private String charset;

    private String requestUrl;
    private String requestMethod;

    public ResponseStream(HttpResponse baseResponse, String requestUrl) throws IOException {
        this(baseResponse, HTTP.UTF_8, requestUrl);
    }

    public ResponseStream(HttpResponse baseResponse, String charset, String requestUrl) throws IOException {
        if (baseResponse == null) {
            throw new IllegalArgumentException("baseResponse may not be null");
        }

        this.baseResponse = baseResponse;
        this.baseStream = baseResponse.getEntity().getContent();
        this.charset = charset;
        this.requestUrl = requestUrl;
    }

    private String _directResult;

    public ResponseStream(String result) throws IOException {
        if (result == null) {
            throw new IllegalArgumentException("result may not be null");
        }

        _directResult = result;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public InputStream getBaseStream() {
        return baseStream;
    }

    public HttpResponse getBaseResponse() {
        return baseResponse;
    }

    public int getStatusCode() {
        if (_directResult != null) return 200;
        return baseResponse.getStatusLine().getStatusCode();
    }

    public Locale getLocale() {
        if (_directResult != null) return Locale.getDefault();
        return baseResponse.getLocale();
    }

    public String getReasonPhrase() {
        if (_directResult != null) return "";
        return baseResponse.getStatusLine().getReasonPhrase();
    }

    public String readString() throws IOException {
        if (_directResult != null) return _directResult;
        if (baseStream == null) return null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(baseStream, charset));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            _directResult = sb.toString();
            return _directResult;
        } finally {
            IOUtils.closeQuietly(baseStream);
        }
    }

    public void readFile(String savePath) throws IOException {
        if (_directResult != null) return;
        if (baseStream == null) return;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(savePath);
            BufferedInputStream ins = new BufferedInputStream(baseStream);
            byte[] buffer = new byte[4096];
            int len = 0;
            while ((len = ins.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(baseStream);
        }
    }

    @Override
    public int read() throws IOException {
        if (baseStream == null) return -1;
        return baseStream.read();
    }

    @Override
    public int available() throws IOException {
        if (baseStream == null) return 0;
        return baseStream.available();
    }

    @Override
    public void close() throws IOException {
        if (baseStream == null) return;
        baseStream.close();
    }

    @Override
    public void mark(int readLimit) {
        if (baseStream == null) return;
        baseStream.mark(readLimit);
    }

    @Override
    public boolean markSupported() {
        if (baseStream == null) return false;
        return baseStream.markSupported();
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        if (baseStream == null) return -1;
        return baseStream.read(buffer);
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException {
        if (baseStream == null) return -1;
        return baseStream.read(buffer, offset, length);
    }

    @Override
    public synchronized void reset() throws IOException {
        if (baseStream == null) return;
        baseStream.reset();
    }

    @Override
    public long skip(long byteCount) throws IOException {
        if (baseStream == null) return 0;
        return baseStream.skip(byteCount);
    }
}
