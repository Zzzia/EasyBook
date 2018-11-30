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

    @Override
    public List<Book> search(String bookName) throws Exception {
        String html = NetUtil.getHtml("https://www.wenxuemi6.com/search.php?keyword=" + URLEncoder.encode(bookName, "utf-8")
                , "utf-8");
        return BookGriper.parseBaiduBooks(html, getSiteName());
    }

    @Override
    public List<Catalog> parseCatalog(String catalogHtml, String url) {
        return BookGriper.parseBqgCatalogs(catalogHtml, "https://www.wenxuemi6.com");
    }

    @Override
    public List<String> parseContent(String chapterHtml) {
        String content = RegexUtil.regexExcept("<div id=\"content\">", "</div>", chapterHtml).get(0);
        return BookGriper.getContentsByBR(content);
    }

    public static void main(String[] args) throws Exception {
        Site site = new Wenxuemi();
        String url = "https://www.wenxuemi6.com/files/article/html/9/9551/";
        System.out.println(site.search("天行"));
        System.out.println(site.parseCatalog(NetUtil.getHtml(url, site.getEncodeType()), url));
        System.out.println(site.parseContent(NetUtil.getHtml("https://www.wenxuemi6.com/files/article/html/9/9551/12020560.html", site.getEncodeType())));
    }

    @Override
    public String getSiteName() {
        return "文学迷";
    }
}
