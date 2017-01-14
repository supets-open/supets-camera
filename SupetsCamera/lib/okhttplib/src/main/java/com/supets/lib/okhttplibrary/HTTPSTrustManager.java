package com.supets.lib.okhttplibrary;

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class HTTPSTrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(
            X509Certificate[] x509Certificates, String s)
            throws java.security.cert.CertificateException {
    }

    @Override
    public void checkServerTrusted(
            X509Certificate[] x509Certificates, String s)
            throws java.security.cert.CertificateException {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}
