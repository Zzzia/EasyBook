package com.zia.easybookmodule.site;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.net.NetUtil;
import com.zia.easybookmodule.util.BookGriper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By zia on 2018/10/31.
 * 新笔趣阁 https://www.xbiquge6.com
 */
public class Xbiquge extends Site {

    private static final String root = "https://www.xbiquge6.com";

    @Override
    public String getSiteName() {
        return "新笔趣阁";
    }

    @Override
    public Book getMoreBookInfo(Book book, String catalogHtml) throws Exception {
        return BookGriper.getBqgMoreInfo(book, catalogHtml, "https://www.xbiquge6.com/");
    }

    @Override
    public String getEncodeType() {
        return "utf-8";
    }

    @Override
    public List<Book> search(String bookName) throws Exception {
        String html = NetUtil.getHtml("https://www.xbiquge6.com/search.php?keyword="
                + URLEncoder.encode(bookName, getEncodeType()), getEncodeType());
//
//        Elements items = Jsoup.parse(html).getElementsByClass("result-item result-game-item");
//        if (items == null || items.size() <= 0) {
//            throw new IOException();
//        }
//
//        List<Book> bookList = new ArrayList<>();
//        for (Element item : items) {
//            Element detail = item.getElementsByClass("result-game-item-detail").first();
//            String bkName = detail.getElementsByTag("a").first().attr("title");
//            String url = detail.getElementsByTag("a").first().attr("href");
//            Elements tags = detail.getElementsByClass("result-game-item-info-tag");
//            String author = tags.get(0).getElementsByTag("span").get(1).text();
//            String lastUpdateTime = BookGriper.formatTime(tags.get(2).getElementsByTag("span").get(1).text());
//            String lastChapterName = tags.get(3).getElementsByTag("a").text();
//            String imageUrl = item.getElementsByTag("img").get(0).attr("src");
//            bookList.add(new Book(bkName, author, url, imageUrl, "未知", lastUpdateTime, lastChapterName, getSiteName()));
//        }
        return BookGriper.parseBaiduBooks(html, getSiteName());
    }

    @Override
    public List<Catalog> parseCatalog(String catalogHtml, String rootUrl) {
        Element listElement = Jsoup.parse(catalogHtml).getElementById("list");
        List<Catalog> catalogs = new ArrayList<>();
        for (Element a : listElement.getElementsByTag("a")) {
            String bkUrl = BookGriper.mergeUrl(rootUrl, a.attr("href"));
            String name = a.text();
            catalogs.add(new Catalog(name, bkUrl));
        }
        return catalogs;
    }

    @Override
    public List<String> parseContent(String chapterHtml) {
        List<TextNode> textNodes = Jsoup.parse(chapterHtml).getElementById("content").textNodes();
        return BookGriper.getContentsByTextNodes(textNodes);
    }
}
