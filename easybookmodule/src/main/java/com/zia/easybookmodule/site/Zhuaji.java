package com.zia.easybookmodule.site;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.net.NetUtil;
import com.zia.easybookmodule.util.BookGriper;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created By zia on 2018/11/5.
 * 爪机书屋 https://www.zhuaji.org/
 * 巨慢
 */
public class Zhuaji extends Site {

    private static final String root = "https://www.zhuaji.org/";

    @Override
    public List<Catalog> parseCatalog(String catalogHtml, String rootUrl) {
        List<Catalog> catalogs = new ArrayList<>();

        Elements dds = Jsoup.parse(catalogHtml).getElementsByTag("dd");
        for (Element dd : dds) {
            String title = dd.getElementsByTag("a").first().text();
            String href = root + dd.getElementsByTag("a").first().attr("href");
            catalogs.add(new Catalog(title, href));
        }
        return catalogs;
    }

    @Override
    public List<String> parseContent(String chapterHtml) throws Exception {
        return BookGriper.getContentsByTextNodes(Jsoup.parse(chapterHtml).getElementById("content").textNodes());
    }

    @Override
    public List<Book> search(String bookName) throws Exception {
        RequestBody requestBody = new FormBody.Builder()
                .add("searchkey", bookName)
                .build();
        String html = NetUtil.getHtml("https://www.zhuaji.org/search.html", requestBody, getEncodeType());
        Elements lis = Jsoup.parse(html).getElementById("sitebox").getElementsByTag("li");

        List<Book> bookList = new ArrayList<>();
        for (Element li : lis) {
            String lastUpdateTime = li.getElementsByTag("h3").first().getElementsByTag("span").first().text();
            lastUpdateTime = BookGriper.formatTime(lastUpdateTime);
            String bkName = li.getElementsByTag("h3").first().getElementsByTag("a").first().text();
            String bkUrl = li.getElementsByTag("h3").first().getElementsByTag("a").first().attr("href");
            Elements spans = li.getElementsByTag("p").first().getElementsByTag("span");
            String author = spans.first().text();
            String chapterSize = spans.get(2).text();
            String intro = li.getElementsByTag("dd").first().text();
            String lastChapterName = li.getElementsByTag("p").get(1).getElementsByTag("a").first().text();
            String imageUrl = li.getElementsByTag("img").first().attr("src");
            bookList.add(new Book(bkName, author, bkUrl, imageUrl, chapterSize, lastUpdateTime, lastChapterName, getSiteName(), intro));
        }
        return bookList;
    }

    @Override
    public String getEncodeType() {
        return "utf-8";
    }

    @Override
    public String getSiteName() {
        return "爪机书屋";
    }

    @Override
    public Book getMoreBookInfo(Book book, String catalogHtml) throws Exception {
        Document document = Jsoup.parse(catalogHtml);
        String intro = document.getElementsByClass("intro").first().getElementsByTag("p").first().text();
        String imgUrl = BookGriper.mergeUrl(root, document.getElementsByClass("cover").first().getElementsByTag("img").first().attr("src"));
        Element haoshu = document.getElementsByClass("haoshu").first();
        String lastChapterName = haoshu.getElementsByTag("a").first().text();
        String lastUpdateTime = haoshu.textNodes().get(1).text();
        lastUpdateTime = lastUpdateTime.replaceAll("\\(|\\)","");
        book.setIntroduce(intro);
        book.setImageUrl(imgUrl);
        book.setLastUpdateTime(lastUpdateTime);
        book.setLastChapterName(lastChapterName);
        return book;
    }
}
