package com.zia.easybookmodule.site;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.net.NetUtil;
import com.zia.easybookmodule.util.BookGriper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By zia on 2018/11/1.
 * 宅阅读 https://www.zhaiyuedu.com/
 * 需要js渲染
 */
@Deprecated
public class Zhai extends Site {

    private static final String root = "https://www.zhaiyuedu.com";

    @Override
    public List<Catalog> parseCatalog(String catalogHtml, String rootUrl) {
        Elements as1 = Jsoup.parse(catalogHtml).getElementsByClass("col xs-4 chapter-table").get(0).getElementsByTag("a");
        Elements as2 = Jsoup.parse(catalogHtml).getElementsByClass("col xs-4 chapter-table").get(1).getElementsByTag("a");
        Elements as3 = Jsoup.parse(catalogHtml).getElementsByClass("col xs-4 chapter-table").get(2).getElementsByTag("a");
        List<Catalog> catalogs = new ArrayList<>();
        for (int i = 0; ; i++) {
            if (i >= as1.size()) break;
            catalogs.add(getCatalog(as1.get(i)));
            if (i >= as2.size()) break;
            catalogs.add(getCatalog(as2.get(i)));
            if (i >= as3.size()) break;
            catalogs.add(getCatalog(as3.get(i)));
        }
        return catalogs;
    }

    private Catalog getCatalog(Element a) {
        String url = root + a.attr("href");
        String name = a.text();
        return new Catalog(name, url);
    }

    @Override
    public List<String> parseContent(String chapterHtml) {
        List<TextNode> textNodes = Jsoup.parse(chapterHtml).getElementById("chapter-content").textNodes();
        return BookGriper.getContentsByTextNodes(textNodes);
    }

    @Override
    public List<Book> search(String bookName) throws Exception {
        String url = "https://www.zhaiyuedu.com/search/?key=" + URLEncoder.encode(bookName, getEncodeType());
        String html = NetUtil.getHtml(url, getEncodeType());
        Elements divs = Jsoup.parse(html).getElementsByClass("row index-box").first().getElementsByTag("div");
        if (divs.size() <= 3) {
            throw new Exception("");
        }
        divs.subList(0, 4).clear();
        List<Book> bookList = new ArrayList<>();
        for (int i = 0; i < divs.size(); i = i + 3) {
            String bkName = divs.get(i).getElementsByTag("a").get(0).text();
            String author = divs.get(i).getElementsByTag("a").get(1).text();
            String lastChapterName = divs.get(i + 1).text();
            String lastUpdateTime = divs.get(i + 2).getElementsByTag("span").first().text();
            String bkUrl = root + divs.get(i + 2).getElementsByTag("a").first().attr("href");
            bookList.add(new Book(bkName, author, bkUrl, "未知", lastUpdateTime, lastChapterName, getSiteName()));
        }
        return bookList;
    }

    @Override
    public String getEncodeType() {
        return "utf-8";
    }

    @Override
    public String getSiteName() {
        return "宅阅读";
    }
}
