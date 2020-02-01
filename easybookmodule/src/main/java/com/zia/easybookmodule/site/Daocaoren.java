package com.zia.easybookmodule.site;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.net.NetUtil;
import com.zia.easybookmodule.util.BookGriper;
import com.zia.easybookmodule.util.TextUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By zia on 2018/11/1.
 * 稻草人书屋 http://www.daocaorenshuwu.com
 * 巨慢
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

    @Override
    public Book getMoreBookInfo(Book book, String catalogHtml) throws Exception {
        Document document = Jsoup.parse(catalogHtml);
        Element media = document.getElementsByClass("media").first();
        String imgUrl = media.getElementsByClass("book-img-middel").first().attr("src");
        imgUrl = BookGriper.mergeUrl(root, imgUrl);
        Elements divs = media.getElementsByClass("row").first().getElementsByClass("col-sm-6");
        String classify = divs.get(2).text();
        classify = classify.substring(classify.indexOf("：") + 1);
        String latestChapterName = divs.get(6).getElementsByTag("a").first().attr("href");
        latestChapterName = BookGriper.mergeUrl(root, latestChapterName);
        String latestUpdateTime = divs.get(7).text();
        String introduce = document.getElementsByClass("book-detail").first().text();
        book.setImageUrl(imgUrl);
        book.setClassify(classify);
        book.setLastChapterName(latestChapterName);
        book.setLastUpdateTime(latestUpdateTime);
        book.setIntroduce(introduce);
        return book;
    }
}
