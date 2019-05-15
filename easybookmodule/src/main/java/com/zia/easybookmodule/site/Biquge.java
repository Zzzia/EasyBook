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

import java.util.ArrayList;
import java.util.List;

/**
 * Created By zia on 2018/10/30.
 * 笔趣阁  http://www.biquge.com.tw
 * 不太稳定，测试可达到4.5m/s，偶尔1m/s
 */
public class Biquge extends Site {

    private final static String root = "http://www.biquge.tw";

    @Override
    public String getSiteName() {
        return "笔趣阁";
    }

    @Override
    public List<Book> search(String bookName) throws Exception {
//        String url = "https://sou.xanbhx.com/search?siteid=xsla&q="
//                + URLEncoder.encode(bookName, "gbk");
        String url = "https://sou.xanbhx.com/search?siteid=xsla&q="
                + bookName;
        String html = NetUtil.getHtml(url, getEncodeType());
        List<Element> lis = Jsoup.parse(html).getElementById("search-main").getElementsByTag("li");
        List<Book> bookList = new ArrayList<>(lis.size() - 1);
        for (int i = 1; i < lis.size(); i++) {
            Element li = lis.get(i);
            Elements as = li.getElementsByTag("a");
            Elements spans = li.getElementsByTag("span");
            String bkName = as.get(0).text();
            String bkUrl = as.get(0).attr("href");
            String lastChapter = as.get(1).text();
            String author = spans.get(3).text();
            String lastTime = BookGriper.formatTime(spans.get(5).text());
            Book book = new Book(bkName, author, bkUrl, "", "", lastTime, lastChapter, getSiteName());
            bookList.add(book);
        }
        return bookList;
//        String title = RegexUtil.regexExcept("<title>", "</title>", html).get(0);
//        if (!title.equals("笔趣阁_书友最值得收藏的网络小说阅读网")) {
//            String info = RegexUtil.regexExcept("<div id=\"info\">", "</div>", html).get(0);
//            info = TextUtil.cleanContent(info);
//            List<String> ps = RegexUtil.regexInclude("<p>", "</p>", info);
//            String author = new RegexUtil.Tag(ps.get(0)).getText().replaceAll("作者|：", "");
//            String lastChapterName = RegexUtil.regexExcept("\">", "</a>", ps.get(3)).get(0);
//            String lastUpdateTime = new RegexUtil.Tag(ps.get(2)).getText();
//            Book book = new Book(bookName, author, url, "未知", lastUpdateTime, lastChapterName, getSiteName());
//            return Collections.singletonList(book);
//        } else {
//            List<String> trs = RegexUtil.regexExcept("<tr", "</tr>", html);
//            if (trs.size() == 0) {
//                throw new IOException();
//            }
//            trs.remove(0);
//            List<Book> bookList = new ArrayList<>(trs.size());
//            for (String tr : trs) {
//                List<String> tds = RegexUtil.regexInclude("<td", "</td>", tr);
//                List<String> as = RegexUtil.regexInclude("<a", "</a>", tr);
//                if (tds.size() == 0 || as.size() == 0) throw new IOException();
//                String bkName = new RegexUtil.Tag(as.get(0)).getText();
//                String bkUrl = new RegexUtil.Tag(as.get(0)).getValue("href");
//                String lastChapterName = new RegexUtil.Tag(as.get(1)).getText();
//                String author = new RegexUtil.Tag(tds.get(2)).getText();
//                String size = new RegexUtil.Tag(tds.get(3)).getText();
//                String lastUpdateTime = new RegexUtil.Tag(tds.get(4)).getText();
//                Book book = new Book(bkName, author, bkUrl, size, lastUpdateTime, lastChapterName, getSiteName());
//                bookList.add(book);
//            }
//            return bookList;
//        }
    }

    @Override
    public List<Catalog> parseCatalog(String catalogHtml, String rootUrl) {
        List<Catalog> list = BookGriper.parseBqgCatalogs(catalogHtml, rootUrl);
        System.out.println(list.get(0));
        return list;
    }

    @Override
    public List<String> parseContent(String chapterHtml) {
        String content = RegexUtil.regexExcept("<div id=\"content\">", "</div>", chapterHtml).get(0);
        return BookGriper.getContentsByBR(content);
    }

    @Override
    public String getEncodeType() {
        return "UTF-8";
    }
}
