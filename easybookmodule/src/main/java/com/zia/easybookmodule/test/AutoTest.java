package com.zia.easybookmodule.test;

import androidx.annotation.NonNull;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Chapter;
import com.zia.easybookmodule.bean.Type;
import com.zia.easybookmodule.engine.EasyBook;
import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.net.NetUtil;
import com.zia.easybookmodule.rx.StepSubscriber;
import com.zia.easybookmodule.rx.Subscriber;
import com.zia.easybookmodule.site.Zhuishu;

import java.io.File;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * Created by zia on 2019/3/10.
 */
class AutoTest {
    public static void main(String[] args) throws Exception {
        trustAll();
//        test(new Biquge());
//        EasyBook.getRank(RankConstants.vipreward).subscribe(new Subscriber<Rank>() {
//            @Override
//            public void onFinish(@NonNull Rank hottestRank) {
//                System.out.println(new ArrayList<>(hottestRank.getRankClassifies()));
//            }
//
//            @Override
//            public void onError(@NonNull Exception e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onMessage(@NonNull String message) {
//
//            }
//
//            @Override
//            public void onProgress(int progress) {
//
//            }
//        });
        testDownloadPart(new Zhuishu());
//        testSearch("天行");
//        testMoreInfo(new Biduo());
    }

    private static void testMoreInfo(final Site site) throws Exception {
        Book book = site.search("斗破苍穹").get(0);
        site.getMoreBookInfo(book);
        System.out.println(book);
    }

    private static void testDownloadPart(final Site site) throws Exception {
        List<Book> books = site.search("斗破苍穹");
        System.out.println(books);
        Book book = books.get(0);

        System.out.println(book.toString());

        EasyBook.downloadPart(book, 0, 2).setThreadCount(150).subscribe(new Subscriber<ArrayList<Chapter>>() {
            @Override
            public void onFinish(@NonNull ArrayList<Chapter> chapters) {
                System.out.println("下载完成," + "size = " + chapters.size());
                System.out.println(chapters.get(0).getContents());
            }

            @Override
            public void onError(@NonNull Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onMessage(@NonNull String message) {
                System.out.println(message);
            }

            @Override
            public void onProgress(int progress) {
                System.out.println(progress);
            }
        });
    }

    public static void testSearch(String name) {
        EasyBook.search(name).subscribe(new StepSubscriber<List<Book>>() {
            @Override
            public void onFinish(@NonNull List<Book> books) {
                int size = 9;
                if (books.size() < 10) {
                    size = books.size();
                }
                System.out.println("finish");
                System.out.println(new ArrayList<>(books.subList(0, size)));
            }

            @Override
            public void onError(@NonNull Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onMessage(@NonNull String message) {

            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onPart(@NonNull List<Book> books) {
                int size = 9;
                if (books.size() < 10) {
                    size = books.size();
                }
                System.out.println(new ArrayList<>(books.subList(0, size)));
                System.out.println();
                System.out.println();
            }
        });
    }

    private static void test(final Site site) throws Exception {
        List<Book> books = site.search("天行");
        System.out.println(new ArrayList<>(books).toString());
        Book book = books.get(0);
        EasyBook.download(book)
                .setType(Type.TXT)
//                .setThreadCount(50)
                .setSavePath("/Users/jiangzilai/Documents/book")
                .subscribe(new Subscriber<File>() {
                    @Override
                    public void onFinish(@NonNull File file) {
                        System.out.println(site.getSiteName() + "下载成功");
                    }

                    @Override
                    public void onError(@NonNull Exception e) {
                        System.out.println(site.getSiteName() + "出现错误");
                        e.printStackTrace();
                    }

                    @Override
                    public void onMessage(@NonNull String message) {
                        System.out.println(message);
                    }

                    @Override
                    public void onProgress(int progress) {
                        System.out.println(progress);
                    }
                });
    }

    static void trustAll() {
        NetUtil.okHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory())
                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Created by Anonymous on 2017/6/13.
     */

    private static class SSLSocketClient {

        //获取这个SSLSocketFactory
        public static SSLSocketFactory getSSLSocketFactory() {
            try {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, getTrustManager(), new SecureRandom());
                return sslContext.getSocketFactory();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        //获取TrustManager
        private static TrustManager[] getTrustManager() {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };
            return trustAllCerts;
        }

        //获取HostnameVerifier
        public static HostnameVerifier getHostnameVerifier() {
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            };
            return hostnameVerifier;
        }
    }
}
