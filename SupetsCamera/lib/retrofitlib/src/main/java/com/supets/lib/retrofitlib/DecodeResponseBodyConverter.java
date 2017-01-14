package com.supets.lib.retrofitlib;

import com.google.gson.TypeAdapter;
import com.supets.lib.json.JSonUtil;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class DecodeResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final TypeAdapter<T> adapter;

    DecodeResponseBodyConverter(TypeAdapter<T> adapter) {
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        return adapter.fromJson(JSonUtil.filterJson(value.string()));
    }
}

