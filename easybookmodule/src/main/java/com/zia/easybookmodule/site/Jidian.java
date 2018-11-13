package com.zia.easybookmodule.site;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.net.NetUtil;
import com.zia.easybookmodule.util.BookGriper;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By zia on 2018/10/31.
 * 极点小说网 https://www.toptxtb.com/
 */
public class Jidian extends Site {
    @Override
    public String getSiteName() {
        return "极点小说网";
    }

    @Override
    public List<Book> search(String bookName) throws Exception {
        String url = "https://www.toptxtb.com/modules/article/search.php";
        RequestBody requestBody = new FormBody.Builder()
                .addEncoded("searchkey", URLEncoder.encode(bookName, "gbk"))
                .build();
        String html = NetUtil.getHtml(url, requestBody, getEncodeType());
        Elements trs = Jsoup.parse(html).getElementsByClass("grid").first().getElementsByTag("tr");
        trs.remove(0);

        List<Book> bookList = new ArrayList<>();
        for (Element tr : trs) {
            Elements tds = tr.getElementsByTag("td");
            String bkName = tds.get(0).getElementsByTag("a").first().text();
            String bkUrl = tds.get(1).getElementsByTag("a").first().attr("href");
            String lastChapterName = tds.get(1).getElementsByTag("a").first().text();
            String author = tds.get(2).text();
            String size = tds.get(3).text();
            String lastUpdateTime = tds.get(4).text();
            bookList.add(new Book(bkName, author, bkUrl, size, lastUpdateTime, lastChapterName, getSiteName()));
        }
        return bookList;
    }

    @Override
    public List<Catalog> parseCatalog(String catalogHtml, String url) {
        Elements lists = Jsoup.parse(catalogHtml).getElementsByClass("novel_list");
        List<Catalog> catalogs = new ArrayList<>();
        String root = url.replace("index.html", "");
        for (Element list : lists) {
            Elements as = list.getElementsByTag("a");
            for (Element a : as) {
                String href = root + a.attr("href");
                String name = a.text();
                catalogs.add(new Catalog(name, href));
            }
        }
        return catalogs;
    }

    @Override
    public List<String> parseContent(String chapterHtml) {
        List<TextNode> textNodes = Jsoup.parse(chapterHtml).getElementById("novel_content").textNodes();
        return BookGriper.getContentsByTextNodes(textNodes);
    }
}
