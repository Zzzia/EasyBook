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
 * Created by zia on 2018/11/30.
 * 文学迷
 * https://www.wenxuemi6.com/
 */
public class Wenxuemi extends Site {

    private final String baseUrl = "https://www.wenxuemi6.com/";

    @Override
    public List<Book> search(String bookName) throws Exception {
        String html = NetUtil.getHtml(baseUrl + "search.php?keyword=" + URLEncoder.encode(bookName, "utf-8")
                , "utf-8");
        return BookGriper.parseBaiduBooks(baseUrl, html, getSiteName());
    }

    @Override
    public List<Catalog> parseCatalog(String catalogHtml, String rootUrl) {
        return BookGriper.parseBqgCatalogs(catalogHtml, baseUrl);
    }

    @Override
    public List<String> parseContent(String chapterHtml) {
        String content = RegexUtil.regexExcept("<div id=\"content\">", "</div>", chapterHtml).get(0);
        return BookGriper.getContentsByBR(content);
    }

    @Override
    public String getSiteName() {
        return "文学迷";
    }

    @Override
    public Book getMoreBookInfo(Book book, String catalogHtml) throws Exception {
        return BookGriper.getBqgMoreInfo(book, catalogHtml, baseUrl);
    }
}
