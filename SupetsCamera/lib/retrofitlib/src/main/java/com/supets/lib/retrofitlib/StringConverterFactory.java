package com.supets.lib.retrofitlib;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;


/**
 * @ 使用说明
 * @ <p>@TypeString <p>
 * @ <p> @GET("applist/mini-appcenter/") <p>
 * @ <p>Call<MiniAppCenterPojo> getMiniApp(@Query("offsets") String offsets);</p>
 */

public class StringConverterFactory extends Converter.Factory {

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        if (!(type instanceof Class<?>)) {
            return null;
        }

        for (Annotation annotation : annotations) {
            if (annotation instanceof TypeString) {
                return new StringResponseConverter();
            }
        }

        return null;
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        if (!(type instanceof Class<?>)) {
            return null;
        }
        for (Annotation annotation : parameterAnnotations) {
            if (annotation instanceof TypeString) {
                return new StringRequestConverter();
            }
        }
        return null;
    }

    public static class StringResponseConverter implements Converter<ResponseBody, String> {

        @Override
        public String convert(ResponseBody value) throws IOException {
            return value.string();
        }
    }

    public static class StringRequestConverter implements Converter<String, RequestBody> {
        @Override
        public RequestBody convert(String value) throws IOException {
            return RequestBody.create(MediaType.parse("application/octet-stream"), value);
        }
    }
}