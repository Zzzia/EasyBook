package com.zia.easybookmodule.site;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.net.NetUtil;
import com.zia.easybookmodule.util.BookGriper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.TextNode;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By zia on 2018/10/31.
 * 顶点小说 https://www.booktxt.net/
 */
public class Dingdian extends Site {
    @Override
    public String getSiteName() {
        return "顶点小说";
    }

    @Override
    public List<Book> search(String bookName) throws Exception {
//        String url = "https://www.booktxt.net/search?searchkey="
//                + URLEncoder.encode(bookName, "gbk");
//        String html = NetUtil.getHtml(url, "utf-8");
//        System.out.println(html);
//        List<Book> list = new ArrayList<>();
        return BookGriper.baidu(bookName, getSiteName(), "5334330359795686106");
    }

    @Override
    public List<Catalog> parseCatalog(String catalogHtml, String rootUrl) {
        return BookGriper.parseBqgCatalogs(catalogHtml, rootUrl);
    }

    @Override
    public List<String> parseContent(String chapterHtml) {
        List<TextNode> textNodes = Jsoup.parse(chapterHtml).getElementById("content").textNodes();
        return BookGriper.getContentsByTextNodes(textNodes);
    }
}
