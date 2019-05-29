package com.zia.easybookmodule.site;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.net.NetUtil;
import com.zia.easybookmodule.util.BookGriper;
import com.zia.easybookmodule.util.RegexUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zia on 2019/4/11.
 * 书阅屋
 * https://www.shuyuewu.co/
 */
@Deprecated
public class Shuyuewu extends Site {

    private static final String u = "https://www.shuyuewu.co";

    @Override
    public List<Book> search(String bookName) throws Exception {
        String url = "https://www.shuyuewu.co/s.php?q="
                + URLEncoder.encode(bookName, "gbk");
        String html = NetUtil.getHtml(url, "gbk");
        Elements boxes = Jsoup.parse(html).getElementsByClass("bookbox");
        List<Book> results = new ArrayList<>();
        for (Element box : boxes) {
            String imgUrl = u + box.getElementsByTag("img").first().attr("src");
            Element infoDiv = box.getElementsByClass("bookinfo").first();
            String bkName = infoDiv.getElementsByTag("a").first().text();
            String bkUrl = u + infoDiv.getElementsByTag("a").first().attr("href");
            String author = infoDiv.getElementsByClass("author").first().text();
            author = author.replace("作者：", "");
            String lastChapter = infoDiv.getElementsByClass("update").first().getElementsByTag("a").first().text();
            Book book = new Book(bkName, author, bkUrl, imgUrl, "未知", "未知", lastChapter, getSiteName());
            results.add(book);
        }
        return results;
    }

    @Override
    public List<Catalog> parseCatalog(String catalogHtml, String rootUrl) throws Exception {
        String sub = RegexUtil.regexExcept("<div class=\"listmain\">", "</div>", catalogHtml).get(0);
        return BookGriper.parseBqgCatalogs(sub, u);
    }

    @Override
    public List<String> parseContent(String chapterHtml) throws Exception {
        String content = RegexUtil.regexExcept("<div id=\"content\" class=\"showtxt\">", "</div>", chapterHtml).get(0);
        return BookGriper.getContentsByBR(content);
    }

    @Override
    public String getSiteName() {
        return "书阅屋";
    }

    @Override
    public Book getMoreBookInfo(Book book, String catalogHtml) throws Exception {
        return book;
    }
}
