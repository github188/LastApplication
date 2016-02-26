package com.iermu.apiservice;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * Gzip压缩
 * Created by wcy on 15/8/8.
 */
public class GzipResponseInterceptor implements HttpResponseInterceptor {

    @Override
    public void process(HttpResponse response, HttpContext context) throws HttpException,
            IOException {
        final HttpEntity entity = response.getEntity();
        if (entity != null) {
            final Header ceheader = entity.getContentEncoding();
            if (ceheader != null) {
                final HeaderElement[] codecs = ceheader.getElements();
                for (HeaderElement codec : codecs) {
                    if (codec.getName().equalsIgnoreCase("gzip")) {
                        response.setEntity(new GzipDecompressingEntity(response.getEntity()));
                        return;
                    }
                }
            }
        }
    }

    private static class GzipDecompressingEntity extends HttpEntityWrapper {
        public GzipDecompressingEntity(final HttpEntity entity) {
            super(entity);
        }

        @Override
        public InputStream getContent() throws IOException, IllegalStateException {
            final InputStream wrappedin = wrappedEntity.getContent();
            return new GZIPInputStream(wrappedin);
        }

        @Override
        public long getContentLength() {
            return -1;
        }
    }


}
