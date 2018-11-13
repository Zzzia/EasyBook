package com.zia.easybookmodule.site;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.net.NetUtil;
import com.zia.easybookmodule.util.TextUtil;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zia on 2018/11/5.
 * 书农小说 http://www.shunong.com/
 */
public class Shunong extends Site {

    private static final String root = "http://www.shunong.com/";

    @Override
    public List<Catalog> parseCatalog(String catalogHtml, String url) {
        Elements lis = Jsoup.parse(catalogHtml).getElementsByClass("book_list").first().getElementsByTag("li");

        List<Catalog> catalogs = new ArrayList<>();
        for (Element li : lis) {
            String title = li.getElementsByTag("a").first().text();
            String href = root + li.getElementsByTag("a").first().attr("href");
            catalogs.add(new Catalog(title, href));
        }
        return catalogs;
    }

    @Override
    public List<String> parseContent(String chapterHtml) {
        Elements ps = Jsoup.parse(chapterHtml).getElementById("htmlContent").getElementsByTag("p");
        if (ps.first().text().contains("shunong.com")) {
            ps.remove(0);
        }
        List<String> contents = new ArrayList<>();
        for (Element p : ps) {
            String s = p.text().trim();
            if (!s.isEmpty()) {
                contents.add(TextUtil.cleanContent(s));
            }
        }
        return contents;
    }

    @Override
    public List<Book> search(String bookName) throws Exception {
        RequestBody requestBody = new FormBody.Builder()
                .add("keyboard", bookName)
                .add("show", "title")
                .build();
        String html = NetUtil.getHtml("http://www.shunong.com/e/search/index.php", requestBody, getEncodeType());

        List<Book> books = new ArrayList<>();

        Elements lis = Jsoup.parse(html).getElementsByClass("listbox").first().getElementsByTag("li");

        for (Element li : lis) {
            String bkName = li.getElementsByTag("font").first().text().replace("在线阅读", "");
            String href = root + li.getElementsByTag("a").first().attr("href");
            String author = li.getElementsByTag("div").first().getElementsByTag("a").first().text();
            books.add(new Book(bkName, author, href, "未知", "未知", "未知", getSiteName()));
        }
        return books;
    }

    @Override
    public String getEncodeType() {
        return "utf-8";
    }

    @Override
    public String getSiteName() {
        return "书农小说";
    }
}
