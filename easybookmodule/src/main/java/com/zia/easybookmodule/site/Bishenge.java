package com.zia.easybookmodule.site;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.net.NetUtil;
import com.zia.easybookmodule.util.BookGriper;
import com.zia.easybookmodule.util.RegexUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created By zia on 2018/10/30.
 * 笔神阁  http://www.bishenge.com
 * 测试约1.5m/s
 */
public class Bishenge extends Site {
    @Override
    public String getSiteName() {
        return "笔神阁";
    }

    @Override
    public List<Book> search(String bookName) throws Exception {
        return BookGriper.baidu( bookName,getSiteName(), "7751645214184726687");
    }

    @Override
    public List<Catalog> parseCatalog(String catalogHtml, String url) {
        return BookGriper.parseBqgCatalogs(catalogHtml, url);
    }

    @Override
    public List<String> parseContent(String chapterHtml) {
        String content = RegexUtil.regexExcept("<div id=\"content\">", "</div>", chapterHtml).get(0);
        return BookGriper.getContentsByBR(content);
    }

    public static void main(String[] args) throws Exception {
        Site site = new Bishenge();
        String url = "https://www.bimo.cc/id6167/";
        System.out.println(site.search("天行"));
        System.out.println(site.parseCatalog(NetUtil.getHtml(url, site.getEncodeType()), url));
        System.out.println(site.parseContent(NetUtil.getHtml("https://www.bimo.cc/id6167/4723726.html", site.getEncodeType())));
    }
}
