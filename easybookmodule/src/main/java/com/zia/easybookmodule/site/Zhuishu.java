package com.zia.easybookmodule.site;

import androidx.annotation.Nullable;

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
 * https://www.bimo.cc/
 * www.zhuishu.tw会跳转到这里
 * 追书网
 */
public class Zhuishu extends Site {

    private static final String baseUrl = "https://www.mangg.net/";

    @Override
    public List<Book> search(String bookName) throws Exception {
        String url = baseUrl + "search.php?q=" + URLEncoder.encode(bookName, "utf-8");
        System.out.println(url);
        String html = NetUtil.getHtml(url, getEncodeType());
        System.out.println(html);
        return BookGriper.parseBaiduBooks(baseUrl, html, getSiteName());
    }

    @Override
    public List<Catalog> parseCatalog(String catalogHtml, String rootUrl) {
        return BookGriper.parseBqgCatalogs(catalogHtml, baseUrl);
    }

    @Override
    public List<String> parseContent(String chapterHtml) {
        String content = RegexUtil.regexExcept("<div id=\"content\">", "</div>", chapterHtml).get(0);
        return BookGriper.getContentsByBR(content, new BookGriper.CustomCleaner() {
            @Nullable
            @Override
            public String clean(String line) {
                if (line.contains("追书网")) return null;
                return line;
            }
        });
    }

    @Override
    public String getEncodeType() {
        return "utf-8";
    }

    @Override
    public String getSiteName() {
        return "追书网";
    }

    @Override
    public Book getMoreBookInfo(Book book, String catalogHtml) throws Exception {
        return BookGriper.getBqgMoreInfo(book, catalogHtml, baseUrl);
    }
}
