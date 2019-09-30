package com.zia.easybookmodule.site;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.net.NetUtil;
import com.zia.easybookmodule.util.BookGriper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created By zia on 2018/10/31.
 * 棉花糖小说 http://www.mianhuatang2.com
 */
@Deprecated
//过分了，设置cookies反扒..
public class Mianhuatang extends Site {

    private String root = "http://www.mianhuatang2.com";

    @Override
    public String getSiteName() {
        return "棉花糖小说";
    }

    @Override
    public Book getMoreBookInfo(Book book, String catalogHtml) throws Exception {
        return BookGriper.getBqgMoreInfo(book, catalogHtml, "https://");
    }

    @Override
    public List<Book> search(String bookName) throws Exception {
        String url = "http://www.mianhuatang2.com/search.aspx?bookname=" + URLEncoder.encode(bookName, getEncodeType());
        Map<String, String> cookies = new HashMap<>(1);
        cookies.put("cookie", "cuid=www.mianhuatang2.com");
        String html = NetUtil.getHtml(url, cookies, null, getEncodeType());
        System.out.println(html);
        Element newscontent = Jsoup.parse(html).getElementById("newscontent");
        Elements liElements = newscontent.getElementsByClass("l").first().getElementsByTag("li");
        List<Book> bookList = new ArrayList<>();
        for (Element liElement : liElements) {
            Elements spans = liElement.getElementsByTag("span");
            String bkName = spans.get(1).getElementsByTag("a").first().text();
            String bkUrl = spans.get(1).getElementsByTag("a").first().attr("href");
            bkUrl = BookGriper.mergeUrl(root, bkUrl);
            System.out.println(bkUrl);
            String lastChapterName = spans.get(2).getElementsByTag("a").first().text();
            String author = spans.get(3).text();
            String lastUpdateTime = BookGriper.formatTime(spans.get(4).text());
            bookList.add(new Book(bkName, author, bkUrl, "未知", lastUpdateTime, lastChapterName, getSiteName()));
        }
        return bookList;
    }

    @Override
    public List<Catalog> parseCatalog(String catalogHtml, String rootUrl) {
        System.out.println(catalogHtml);
        System.out.println(rootUrl);
        Elements dds = Jsoup.parse(catalogHtml).getElementsByTag("dd");
        List<Catalog> catalogs = new ArrayList<>();
        for (Element dd : dds) {
            Element a = dd.getElementsByTag("a").first();
            String href = a.attr("href");
            String name = a.text();
            catalogs.add(new Catalog(name, href));
        }
        return catalogs;
    }

    @Override
    public List<String> parseContent(String chapterHtml) {
        List<TextNode> textNodes = Jsoup.parse(chapterHtml, "", Parser.xmlParser()).getElementById("zjneirong").textNodes();
        return BookGriper.getContentsByTextNodes(textNodes);
    }
}
