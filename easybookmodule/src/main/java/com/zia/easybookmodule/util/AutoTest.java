package com.zia.easybookmodule.util;

import androidx.annotation.NonNull;
import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Chapter;
import com.zia.easybookmodule.bean.Type;
import com.zia.easybookmodule.engine.EasyBook;
import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.rx.StepSubscriber;
import com.zia.easybookmodule.rx.Subscriber;
import com.zia.easybookmodule.site.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zia on 2019/3/10.
 */
public class AutoTest {
    public static void main(String[] args) throws Exception {
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
//        testPart(new Biquge());
//        testSearch("天行");
        testMoreInfo(new Zhuishu());
    }

    private static void testMoreInfo(final Site site) throws Exception {
        Book book = site.search("斗破苍穹").get(0);
        site.getMoreBookInfo(book);
        System.out.println(book);
    }

    private static void testPart(final Site site) throws Exception {
        List<Book> books = site.search("修真聊天群");
        Book book = books.get(0);

        System.out.println(book.toString());

        EasyBook.downloadPart(book, 0, 100).setThreadCount(150).subscribe(new Subscriber<ArrayList<Chapter>>() {
            @Override
            public void onFinish(@NonNull ArrayList<Chapter> chapters) {
                System.out.println("下载完成," + "size = " + chapters.size());
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

    private static void testSearch(String name) {
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
}
