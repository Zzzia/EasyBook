package com.zia.easybookmodule.site;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.net.NetUtil;
import com.zia.easybookmodule.util.BookGriper;
import com.zia.easybookmodule.util.RegexUtil;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By zia on 2018/10/30.
 * 笔神阁  http://www.bishenge.com
 * 测试约1.5m/s
 */
public class Bishenge extends Site {
    @Override
    public String getSiteName() {
        return "笔神阁";
    }

    @Override
    public Book getMoreBookInfo(Book book, String catalogHtml) throws Exception {
        return BookGriper.getBqgMoreInfo(book, catalogHtml, "http://www.bishenge.com/");
    }

    @Override
    public List<Book> search(String bookName) throws Exception {
        String url = "http://www.bishenge.com/pc/so.php";
        RequestBody requestBody = new FormBody.Builder()
                .addEncoded("searchkey", URLEncoder.encode(bookName, "gbk"))
                .add("searchtype", "articlename")
                .build();
        String html = NetUtil.getHtml(url, requestBody, getEncodeType());
        Elements elements = Jsoup.parse(html).getElementById("main").getElementsByTag("tr");
        //移除表格标题
        elements.remove(0);
        List<Book> books = new ArrayList<>();
        for (Element element : elements) {
            Elements tds = element.getElementsByTag("td");
            Element a = tds.first().getElementsByTag("a").first();
            String bkName = a.text();
            String bkUrl = "http://www.bishenge.com" + a.attr("href");
            String author = tds.get(1).text();
            String updateTime = BookGriper.formatTime(tds.get(2).text());
            books.add(new Book(bkName, author, bkUrl, "", "", updateTime, "", getSiteName()));
        }
        return books;
    }

    @Override
    public List<Catalog> parseCatalog(String catalogHtml, String rootUrl) {
        return BookGriper.parseBqgCatalogs(catalogHtml, rootUrl);
    }

    @Override
    public List<String> parseContent(String chapterHtml) {
        String content = RegexUtil.regexExcept("<div id=\"content\">", "</div>", chapterHtml).get(0);
        return BookGriper.getContentsByBR(content, new BookGriper.CustomCleaner() {
            @Override
            public String clean(String line) {
                if (line.contains("笔神阁中文")) {
                    return null;
                } else {
                    return line;
                }
            }
        });
    }
}
