package com.zia.easybookmodule.net;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * Created By zia on 2018/10/21.
 */
public class NetUtil {

    //把网络请求暴露出去
    private static EasyBookNet net = new OkHttpNet();

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
        return net.getHtml(url, "gbk");
    }

    public static String getHtml(String url, String encodeType) throws IOException {
        return net.getHtml(url, null, null, encodeType);
    }

    public static String getHtml(String url, RequestBody requestBody, String encodeType) throws IOException {
        return net.getHtml(url, null, requestBody, encodeType);
    }

    public static String getHtml(String url, Map<String, String> header, RequestBody requestBody, String encodeType) throws IOException {
        return net.getHtml(url, header, requestBody, encodeType);
    }

    public static void setNet(EasyBookNet net) {
        NetUtil.net = net;
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
