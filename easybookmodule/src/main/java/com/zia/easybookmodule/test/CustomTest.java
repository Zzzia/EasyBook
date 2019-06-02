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

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zia on 2019-06-02.
 */
public class CustomTest {

    private static final String filePath = "/Users/jiangzilai/Downloads/easybook.json";
    private static final String savePath = "/Users/jiangzilai/Documents/book";

    public static void main(String[] args) throws Exception {
//        initEmptyJsonFile(filePath);
        List<XpathSiteRule> rules = getXpathRuleFromFile(filePath);
        Site site = new CustomXpathSite(rules.get(0));
        testSearch(site);
//        testDownload(site);
    }

    private static void testDownload(Site site) throws Exception{
        List<Book> books = site.search("天行");
        SiteCollection.getInstance().addSite(site);
        final Book book = books.get(0);

        System.out.println(book.toString());

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
        SiteCollection.getInstance().addSite(site);
        final Book book = books.get(0);

        System.out.println(book.toString());

        EasyBook.downloadPart(book, 0, 1)
                .subscribe(new Subscriber<ArrayList<Chapter>>() {
                    @Override
                    public void onFinish(@NonNull ArrayList<Chapter> chapters) {
                        System.out.println(chapters);
                        System.out.println(book.toString());
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
