package com.zia.easybookmodule.site;

import android.support.annotation.NonNull;
import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.bean.Type;
import com.zia.easybookmodule.engine.EasyBook;
import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.net.NetUtil;
import com.zia.easybookmodule.rx.Subscriber;
import com.zia.easybookmodule.util.BookGriper;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


/**
 * Created By zia on 2018/11/1.
 * 冰火中文 https://www.bhzw.cc/
 */
public class Binhuo extends Site {

    private static final String root = "https://www.bhzw.cc/";

    @Override
    public List<Catalog> parseCatalog(String catalogHtml, String rootUrl) {
        Elements as = Jsoup.parse(catalogHtml).getElementsByClass("float-list fill-block").first().getElementsByTag("a");
        List<Catalog> catalogs = new ArrayList<>();
        for (Element a : as) {
            String href = rootUrl + a.attr("href");
            String name = a.text();
            catalogs.add(new Catalog(name, href));
        }
        return catalogs;
    }

    @Override
    public List<String> parseContent(String chapterHtml) {
        List<TextNode> textNodes = Jsoup.parse(chapterHtml).getElementById("ChapterContents").textNodes();
        return BookGriper.getContentsByTextNodes(textNodes);
    }

    @Override
    public List<Book> search(String bookName) throws Exception {
        String url = "https://www.bhzw.cc/modules/article/search.php";
        RequestBody requestBody = new FormBody.Builder()
                .addEncoded("searchkey", URLEncoder.encode(bookName, getEncodeType()))
                .build();
        String html = NetUtil.getHtml(url, requestBody, getEncodeType());
        Elements trs = Jsoup.parse(html).getElementsByClass("bd").first()
                .getElementsByTag("tbody").first().getElementsByTag("tr");

        List<Book> bookList = new ArrayList<>();
        for (Element tr : trs) {
            Elements as = tr.getElementsByTag("td").get(2).getElementsByTag("a");
            String bkName = as.get(0).text();
            String href = root + as.get(0).attr("href");
            String lastChapterName = as.get(1).text();
            String author = tr.getElementsByTag("td").get(3).text();
            String size = tr.getElementsByTag("td").get(4).text();
            String lastUpdateTime = tr.getElementsByTag("td").get(5).text();
            bookList.add(new Book(bkName, author, href, size, lastUpdateTime, lastChapterName, getSiteName()));
        }
        return bookList;
    }

    @Override
    public String getSiteName() {
        return "冰火中文";
    }
}
