package com.zia.easybookmodule.net;

import java.io.IOException;
import java.util.Map;

import okhttp3.RequestBody;

/**
 * Created by jiangzilai on 2019-09-30.
 */
public interface EasyBookNet {
    //GET请求
    String getHtml(String url, String encodeType) throws IOException;

    //POST请求，传入post参数是RequestBody，可instance of强转为FormBody获取参数
    String getHtml(String url, Map<String, String> header, RequestBody requestBody, String encodeType) throws IOException;
}
