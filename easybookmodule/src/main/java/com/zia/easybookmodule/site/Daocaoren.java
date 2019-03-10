package com.zia.easybookmodule.site;

import android.support.annotation.NonNull;
import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.bean.Type;
import com.zia.easybookmodule.engine.EasyBook;
import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.net.NetUtil;
import com.zia.easybookmodule.rx.Subscriber;
import com.zia.easybookmodule.util.TextUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By zia on 2018/11/1.
 * 稻草人书屋 http://www.daocaorenshuwu.com
 */
public class Daocaoren extends Site {

    private static final String root = "http://www.daocaorenshuwu.com";

    @Override
    public List<Catalog> parseCatalog(String catalogHtml, String rootUrl) {
        Elements as = Jsoup.parse(catalogHtml).getElementById("all-chapter").getElementsByTag("a");
        List<Catalog> catalogs = new ArrayList<>();
        for (Element a : as) {
            String name = a.text();
            String href = "https:" + a.attr("href");
            catalogs.add(new Catalog(name, href));
        }
        return catalogs;
    }

    @Override
    public List<String> parseContent(String chapterHtml) {
        List<String> contents = new ArrayList<>();
        Element cont = Jsoup.parse(chapterHtml).getElementById("cont-text");

        for (Element element : cont.children()) {
            if (element.tagName().equals("div") || element.tagName().equals("p")) {
                if (element.attr("class").isEmpty()) {
                    String s = element.html().trim();
                    if (!s.isEmpty()) {
                        contents.add(TextUtil.cleanContent(s.replaceAll("<.*?>.*?</.*?>", "")));
                    }
                }
            }
        }
        return contents;
    }

    @Override
    public List<Book> search(String bookName) throws Exception {
        String url = "http://www.daocaorenshuwu.com/plus/search.php?q=" + URLEncoder.encode(bookName, getEncodeType());
        String html = NetUtil.getHtml(url, getEncodeType());
        Elements trs = Jsoup.parse(html).getElementsByClass("table table-condensed").first()
                .getElementsByTag("tbody").first().getElementsByTag("tr");

        List<Book> bookList = new ArrayList<>();
        for (Element tr : trs) {
            String bkUrl = root + tr.getElementsByTag("a").first().attr("href");
            String bkName = tr.getElementsByTag("a").first().text();
            String author = tr.getElementsByTag("td").get(1).text();
            bookList.add(new Book(bkName, author, bkUrl, "未知", "未知", "未知", getSiteName()));
        }
        return bookList;
    }

    @Override
    public String getEncodeType() {
        return "utf-8";
    }

    @Override
    public String getSiteName() {
        return "稻草人书屋";
    }
}
