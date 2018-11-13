package com.zia.easybookmodule.site;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.engine.Site;
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
        String sub = RegexUtil.regexExcept("<div id=\"list\">", "</div>", catalogHtml).get(0);
        String ssub = sub.split("正文</dt>")[1];
        List<String> as = RegexUtil.regexInclude("<a", "</a>", ssub);
        List<Catalog> list = new ArrayList<>();
        for (String s : as) {
            RegexUtil.Tag tag = new RegexUtil.Tag(s);
            String name = tag.getText();
            String href = url + tag.getValue("href");
            list.add(new Catalog(name, href));
        }
        return list;
    }

    @Override
    public List<String> parseContent(String chapterHtml) {
        String content = RegexUtil.regexExcept("<div id=\"content\">", "</div>", chapterHtml).get(0);
        return BookGriper.getContentsByBR(content);
    }
}
