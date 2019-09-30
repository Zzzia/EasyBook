package com.zia.easybookmodule.site;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.net.NetUtil;
import com.zia.easybookmodule.util.BookGriper;
import com.zia.easybookmodule.util.RegexUtil;

import java.util.List;

/**
 * Created by jiangzilai on 2019-09-30.
 * 笔趣阁
 * https://www.biduo.cc/
 */
public class Biduo extends Site {
    @Override
    public List<Book> search(String bookName) throws Exception {
        String url = "https://www.biduo.cc/search.php?keyword=" + bookName;
        String html = NetUtil.getHtml(url, "utf-8");
        return BookGriper.parseBaiduBooks(html, getSiteName());
    }

    @Override
    public List<Catalog> parseCatalog(String catalogHtml, String rootUrl) throws Exception {
        return BookGriper.parseBqgCatalogs(catalogHtml, rootUrl);
    }

    @Override
    public List<String> parseContent(String chapterHtml) throws Exception {
        String content = RegexUtil.regexExcept("<div id=\"content\">", "</div>", chapterHtml).get(0);
        return BookGriper.getContentsByBR(content);
    }

    @Override
    public String getSiteName() {
        return "笔趣阁duo";
    }

    @Override
    public Book getMoreBookInfo(Book book, String catalogHtml) throws Exception {
        return BookGriper.getBqgMoreInfo(book, catalogHtml, "https://www.biduo.cc");
    }
}
