package com.thoughtworks.i0.test.helpers;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class HttpClientHelper {
    public static String get(String url) throws Exception {
        HttpClient client = new HttpClient(new SslContextFactory());
        client.start();
        try {
            return new String(client.GET(url).get().getContent());
        } finally {
            client.stop();
        }
    }
}
