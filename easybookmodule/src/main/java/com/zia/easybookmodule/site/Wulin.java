package com.zia.easybookmodule.site;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.net.NetUtil;
import com.zia.easybookmodule.util.BookGriper;
import com.zia.easybookmodule.util.RegexUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zia on 2018/11/30.
 * http://www.50zw.la/
 * 武林中文网
 */
public class Wulin extends Site {

    @Override
    public List<Book> search(String bookName) throws Exception {
        String url = "https://www.50zw.la/modules/article/search.php?searchkey="
                + URLEncoder.encode(bookName, "gbk");
        String html = NetUtil.getHtml(url, getEncodeType());
        Element grid = Jsoup.parse(html).getElementsByClass("grid").first();
        Elements trs = grid.getElementsByTag("tr");
        List<Book> bookList = new ArrayList<>(trs.size());
        for (int i = 0; i < trs.size(); i++) {
            if (i == 0) continue;
            Elements tds = trs.get(i).getElementsByTag("td");
            String bkName = tds.get(0).getElementsByTag("a").text();
            String href = tds.get(0).getElementsByTag("a").attr("href");
            String lastCharpter = tds.get(1).text();
            String author = tds.get(2).text();
            String chapterSize = tds.get(3).text();
            String lastUpdateTime = BookGriper.formatTime(tds.get(4).text());
            Book book = new Book(bkName, author, href, "", chapterSize, lastUpdateTime, lastCharpter, getSiteName());
            bookList.add(book);
        }
        return bookList;
    }

    @Override
    public List<Catalog> parseCatalog(String catalogHtml, String rootUrl) {
        String sub = RegexUtil.regexExcept("<ul class=\"chapterlist\">", "</ul>", catalogHtml).get(0);
        String ssub = sub.split("正文</h5>")[1];
        List<String> as = RegexUtil.regexInclude("<a", "</a>", ssub);
        List<Catalog> list = new ArrayList<>();
        for (String s : as) {
            RegexUtil.Tag tag = new RegexUtil.Tag(s);
            String name = tag.getText();
            String href = rootUrl + tag.getValue("href");
            list.add(new Catalog(name, href));
        }
        return list;
    }

    @Override
    public List<String> parseContent(String chapterHtml) {
        String content = RegexUtil.regexExcept("<div id=\"htmlContent\" class=\"contentbox clear\">", "</div>", chapterHtml).get(0);
        List<String> contents = BookGriper.getContentsByBR(content);
        if (contents.size() > 0 && contents.get(0).contains("武林中文网")) {
            contents.remove(0);
        }
        if (contents.size() > 0 && contents.get(0).replaceAll("第.*章.*", "").isEmpty()) {
            contents.remove(0);
        }
        return contents;
    }

    @Override
    public String getSiteName() {
        return "武林中文网";
    }
}
