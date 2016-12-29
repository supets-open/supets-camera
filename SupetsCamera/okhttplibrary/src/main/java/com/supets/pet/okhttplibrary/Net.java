package com.supets.pet.okhttplibrary;

import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class Net {

    public static OkHttpClient createOkHttp3() {
        return Holder.getSingleton();
    }

    private static class Holder {

        private  static  final String HTTPS_SCHEMA="TLS";

        private static OkHttpClient.Builder singleton;

        public static OkHttpClient getSingleton() {
            if (singleton == null) {
                synchronized (Holder.class) {
                    if (singleton == null) {
                        singleton = new OkHttpClient.Builder()
                                .addNetworkInterceptor(new StethoInterceptor())
                                .addInterceptor(new HttpLoggingInterceptor(
                                        new HttpLoggingInterceptor.Logger() {
                                            @Override
                                            public void log(String message) {
                                                Log.v("OkHttp3", message);
                                            }
                                        }).setLevel(HttpLoggingInterceptor.Level.BODY))
                                .connectTimeout(10, TimeUnit.SECONDS)
                                .writeTimeout(10, TimeUnit.SECONDS)
                                .readTimeout(10, TimeUnit.SECONDS);
                        try {
                            TrustManager[] trustManagers = new TrustManager[]{new HTTPSTrustManager()};
                            SSLContext  context = SSLContext.getInstance(HTTPS_SCHEMA);
                            context.init(null, trustManagers, new SecureRandom());
                            singleton.hostnameVerifier(new HostnameVerifier() {
                                @Override
                                public boolean verify(String hostname, SSLSession session) {
                                    return true;
                                }
                            }).sslSocketFactory(context.getSocketFactory());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return singleton.build();
        }

    }
}
