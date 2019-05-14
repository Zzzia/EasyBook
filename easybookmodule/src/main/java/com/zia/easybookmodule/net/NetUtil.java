package com.zia.easybookmodule.net;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created By zia on 2018/10/21.
 */
public class NetUtil {
    public static OkHttpClient okHttpClient = new OkHttpClient.Builder()
//            .sslSocketFactory(SSLSocketClient.getSSLSocketFactory())
//            .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            //请设置代理，否则会被小说网站ban的...量小没关系
//            .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("106.75.226.36", 808)))
            .build();

    /**
     * 同步获取html文件，默认编码gbk
     */
    public static String getHtml(String url) throws IOException {
        return getHtml(url, "gbk");
    }

    public static String getHtml(String url, String encodeType) throws IOException {
        try {
            return getHtml(url, null, encodeType);
        } catch (IOException e) {
            System.err.print(url);
            throw e;
        }
    }

    public static String getHtml(String url, RequestBody requestBody, String encodeType) throws IOException {
        Request.Builder builder = new Request.Builder()
                .addHeader("accept", "*/*")
                .addHeader("connection", "Keep-Alive")
                .addHeader("Charsert", encodeType)
                .addHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");

        if (requestBody != null) {
            builder.post(requestBody);
        }

        Request request = builder
                .url(url)
                .build();

        ResponseBody body = okHttpClient.newCall(request).execute().body();
        if (body == null) {
            return "";
        } else {
            return new String(body.bytes(), encodeType);
        }
    }

//    private static Random mRandom = new Random();
//
//    /**
//     * 获取随机ip地址
//     *
//     * @return random ip
//     */
//    private static String getRandomIPAddress() {
//        return String.valueOf(mRandom.nextInt(255)) + "." + String.valueOf(mRandom.nextInt(255)) + "." + String.valueOf(mRandom.nextInt(255)) + "." + String.valueOf(mRandom.nextInt(255));
//    }
}
