package com.zia.easybookmodule.test;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Chapter;
import com.zia.easybookmodule.bean.rule.XpathSiteRule;
import com.zia.easybookmodule.engine.EasyBook;
import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.engine.SiteCollection;
import com.zia.easybookmodule.rx.Subscriber;
import com.zia.easybookmodule.site.CustomXpathSite;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zia on 2019-06-02.
 */
class CustomTest {
    private static final String filePath = "easybook.json";
//    private static final String filePath = "tempCustomRule.json";
    private static final String savePath = "/Users/jiangzilai/Documents/book";

    public static void main(String[] args) throws Exception {
        AutoTest.trustAll();

        List<XpathSiteRule> rules = getXpathRuleFromFile(filePath);
        CustomXpathSite site = new CustomXpathSite(rules.get(12));
        site.setDebug(true);

        SiteCollection.getInstance().addSite(site);

        testSearch(site);
//        testDownload(site);
    }

    private static void testDownload(Site site) throws Exception {
        List<Book> books = site.search("天行");
        SiteCollection.getInstance().addSite(site);
        final Book book = books.get(0);

        EasyBook.download(book).setSavePath(savePath).subscribe(new Subscriber<File>() {
            public void onFinish(@NonNull File file) {
                System.out.println(file);
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

    private static void testSearch(Site site) throws Exception {
        List<Book> books = site.search("天行");
        final Book book = books.get(0);

        System.out.println(book.toString());
        System.out.println();

        // 解析更多信息
        tryMoreInfo(book);

        EasyBook.downloadPart(book, 0, 3)
                .subscribe(new Subscriber<ArrayList<Chapter>>() {
                    @Override
                    public void onFinish(@NonNull ArrayList<Chapter> chapters) {
                        System.out.println(chapters);
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

                    }
                });
    }

    private static List<XpathSiteRule> getXpathRuleFromFile(String filePath) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
        String str;
        StringBuilder sb = new StringBuilder();
        while ((str = in.readLine()) != null) {
            sb.append(str);
        }
        in.close();
        return new Gson().fromJson(sb.toString(), TypeToken.getParameterized(List.class, XpathSiteRule.class).getType());
    }

    private static void tryMoreInfo(final Book book) throws Exception {
        EasyBook.getCatalog(book).getSync();
        System.out.println("[more info]:" + book.toString());
    }

    private static void initEmptyJsonFile(String filePath) throws IOException {
        XpathSiteRule xpathSiteRule = new XpathSiteRule();
        xpathSiteRule.setCatalogExtraRule(new XpathSiteRule.ExtraRule());
        xpathSiteRule.setSearchExtraRule(new XpathSiteRule.ExtraRule());
        List<XpathSiteRule> rules = new ArrayList<>();
        rules.add(xpathSiteRule);
        String json = new Gson().toJson(rules);
        File file = new File(filePath);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        out.write(json);
        out.flush();
        out.close();
    }
}
