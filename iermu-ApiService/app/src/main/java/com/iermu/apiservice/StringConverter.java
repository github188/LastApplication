package com.iermu.apiservice;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.MimeUtil;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Created by wcy on 15/7/8.
 */
public class StringConverter implements Converter {

    private final String CHARSET = "UTF-8";
    private final Gson gson;
    private String encoding;

    public StringConverter(Gson gson) {
        this(gson, "UTF-8");
    }

    public StringConverter(Gson gson, String encoding) {
        this.gson = gson;
        this.encoding = encoding;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        String charset = CHARSET;
        if (body.mimeType() != null) {
            charset = MimeUtil.parseCharset(body.mimeType());
        }
        InputStream in = null;
        BufferedReader reader;
        StringBuilder sb = new StringBuilder();
        try {
            in = body.in();
            reader = new BufferedReader(new InputStreamReader(in, charset));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    @Override
    public TypedOutput toBody(Object object) {
        try {
            return new JsonTypedOutput(gson.toJson(object).getBytes(encoding),
                    encoding);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    private static class JsonTypedOutput implements TypedOutput {
        private final byte[] jsonBytes;
        private final String mimeType;

        JsonTypedOutput(byte[] jsonBytes, String encode) {
            this.jsonBytes = jsonBytes;
            this.mimeType = "application/json; charset=" + encode;
        }

        @Override
        public String fileName() {
            return null;
        }

        @Override
        public String mimeType() {
            return mimeType;
        }

        @Override
        public long length() {
            return jsonBytes.length;
        }

        @Override
        public void writeTo(OutputStream out) throws IOException {
            out.write(jsonBytes);
        }
    }
}
