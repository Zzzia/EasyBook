package com.zia.easybookmodule.site;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.net.NetUtil;
import com.zia.easybookmodule.util.BookGriper;
import com.zia.easybookmodule.util.RegexUtil;

import java.net.URLEncoder;
import java.util.List;

/**
 * Created by zia on 2019/4/11.
 * https://www.biquge.biz/
 */
public class BiqugeBiz extends Site {

    private final String baseUrl = "https://www.biquge.biz/";

    @Override
    public List<Book> search(String bookName) throws Exception {
        String url = baseUrl + "search.php?q=" + URLEncoder.encode(bookName, "utf-8");
        String html = NetUtil.getHtml(url, "utf-8");
//        Elements items = Jsoup.parse(html).getElementsByClass("result-list").first().getElementsByClass("result-item");
//        List<Book> results = new ArrayList<>();
//        for (Element item : items) {
//            Element imgItem = item.getElementsByClass("result-game-item-pic").first().getElementsByTag("a").first();
//            String bkUrl = imgItem.attr("href");
//            String imgUrl = imgItem.getElementsByTag("img").first().attr("src");
//            String bkName = item.getElementsByClass("result-game-item-detail").first().getElementsByTag("a").first().attr("title");
//            Element infoItem = item.getElementsByClass("result-game-item-info").first();
//            Elements ps = infoItem.getElementsByTag("p");
//            String author = ps.get(0).getElementsByTag("span").get(1).text();
//            String updateTime = BookGriper.formatTime(ps.get(2).getElementsByTag("span").get(1).text());
//            String lastChapter = ps.get(3).getElementsByTag("a").first().text();
//            Book book = new Book(bkName, author, bkUrl, imgUrl, "未知", updateTime, lastChapter, getSiteName());
//            results.add(book);
//        }
//        return results;
        return BookGriper.parseBaiduBooks(baseUrl, html, getSiteName());
    }

    @Override
    public List<Catalog> parseCatalog(String catalogHtml, String rootUrl) throws Exception {
//        System.out.println(BookGriper.parseBqgCatalogs(catalogHtml, "https://www.biquge.biz"));
        return BookGriper.parseBqgCatalogs(catalogHtml, baseUrl);
    }

    @Override
    public List<String> parseContent(String chapterHtml) throws Exception {
        String content = RegexUtil.regexExcept("<div id=\"content\">", "</div>", chapterHtml).get(0);
        return BookGriper.getContentsByBR(content);
    }

    @Override
    public String getSiteName() {
        return "笔趣阁biz";
    }

    @Override
    public Book getMoreBookInfo(Book book, String catalogHtml) throws Exception {
        return BookGriper.getBqgMoreInfo(book, catalogHtml, baseUrl);
    }
}
