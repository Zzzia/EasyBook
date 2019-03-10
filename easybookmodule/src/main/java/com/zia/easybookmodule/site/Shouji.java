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

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By zia on 2018/10/31.
 * 手机看书 http://www.shoujikanshu.cc/
 * 需要vpn
 */
public class Shouji extends Site {

    private static final String root = "http://www.sjks.cc";

    @Override
    public String getSiteName() {
        return "手机看书";
    }

    @Override
    public List<Book> search(String bookName) throws Exception {
        int count = 5;
        RequestBody requestBody = new FormBody.Builder()
                .addEncoded("keyboard", URLEncoder.encode(bookName, "gbk"))
                .add("show", "title")
                .build();
        String url = "http://www.sjks.cc/e/search/index.php";
        String html = "";
        while (count-- > 0) {
            try {
                String tempHtml = NetUtil.getHtml(url, requestBody, getEncodeType());
                if (!tempHtml.contains("提示信息")) {
                    html = tempHtml;
                    break;
                }
            } catch (Exception ignore) {
            }
        }
        if (html.equals("")) {
            throw new IOException();
        }
        Elements lis = Jsoup.parse(html).getElementsByClass("list_content").first().getElementsByTag("li");
        List<Book> bookList = new ArrayList<>();
        for (Element li : lis) {
            String href = root + li.getElementsByTag("a").first().attr("href");
            String bkName = li.getElementsByTag("a").first().text();
            List<TextNode> textNodes = li.getElementsByClass("info").first().textNodes();
            String lastUpdateTime = textNodes.get(textNodes.size() - 1).text();
            bookList.add(new Book(bkName, "未知", href, "未知", lastUpdateTime, "未知", getSiteName()));
        }
        return bookList;
    }

    @Override
    public List<Catalog> parseCatalog(String catalogHtml, String rootUrl) {
        Elements as = Jsoup.parse(catalogHtml).getElementsByClass("list").first().getElementsByTag("a");
        List<Catalog> catalogs = new ArrayList<>();
        for (Element a : as) {
            String href = root + a.attr("href");
            String name = a.text();
            catalogs.add(new Catalog(name, href));
        }
        System.out.println(catalogs);
        return catalogs;
    }

    @Override
    public List<String> parseContent(String chapterHtml) {
        List<TextNode> textNodes = Jsoup.parse(chapterHtml).getElementsByClass("content").first().textNodes();
        return BookGriper.getContentsByTextNodes(textNodes);
    }
}
