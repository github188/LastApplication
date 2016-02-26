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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

//import com.lidroid.xutils.http.callback.HttpRedirectHandler;

public class DefaultHttpRedirectHandler implements HttpRedirectHandler {
    @Override
    public HttpRequestBase getDirectRequest(HttpResponse response) {
        if (response.containsHeader("Location")) {
            String location = response.getFirstHeader("Location").getValue();
            HttpGet request = new HttpGet(location);
            if (response.containsHeader("Set-Cookie")) {
                String cookie = response.getFirstHeader("Set-Cookie").getValue();
                request.addHeader("Cookie", cookie);
            }
            return request;
        }
        return null;
    }
}
