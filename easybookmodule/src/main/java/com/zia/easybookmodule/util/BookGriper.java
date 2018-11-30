package com.zia.easybookmodule.util;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.net.NetUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By zia on 2018/10/30.
 * 一些重复的网站解析方法
 */
public class BookGriper {
    /**
     * 百度站内搜索
     *
     * @param bookName 小说名字
     * @param s        网站识别码
     * @return
     */
    public static List<Book> baidu(String bookName, String siteName, String s) throws Exception {
        String url = "http://zhannei.baidu.com/cse/search?q="
                + URLEncoder.encode(bookName, "utf-8")
                + "&s=" + s;
        String html = NetUtil.getHtml(url, "utf-8");
        return parseBaiduBooks(html, siteName);
    }

    public static List<Book> parseBaiduBooks(String html, String siteName) throws IOException {
        Element body = Jsoup.parse(html).body();
        Elements items = body.getElementsByClass("result-item result-game-item");
        if (items.size() == 0) {
            throw new IOException();
        }
        List<Book> bookList = new ArrayList<>(items.size());
        for (Element item : items) {
            Element detail = item.getElementsByClass("result-game-item-detail").get(0);
            Element titleElement = detail.getElementsByClass("result-game-item-title-link").get(0);
            String bkName = titleElement.getElementsByAttribute("title").get(0).text();
            String bkUrl = titleElement.attr("href");
            Element infoDiv = item.getElementsByClass("result-game-item-info").first();
            Elements ps = infoDiv.getElementsByTag("p");
            String author = ps.get(0).text().replaceAll("作者：|作者:| ", "");
            String lastUpdateTime = ps.get(2).text().replaceAll("更新时间：|更新时间:| ", "");
            String lastChapterName = ps.get(3).text().replaceAll("最新章节：|最新章节:| ", "");
            String imageUrl = item.getElementsByTag("img").get(0).attr("src");
            Book book = new Book(bkName, author, bkUrl, imageUrl, "未知", lastUpdateTime, lastChapterName, siteName);
            bookList.add(book);
        }
        return bookList;
    }

    public static List<Catalog> parseBqgCatalogs(String catalogHtml, String url){
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

    public static List<String> getContentsByBR(String content) {
        String lines[] = content.split("<br>|<br/>|<br />");
        List<String> contents = new ArrayList<>();
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                contents.add(TextUtil.cleanContent(line));
            }
        }
        return contents;
    }

    public static List<String> getContentsByTextNodes(List<TextNode> textNodes) {
        List<String> contents = new ArrayList<>();
        for (TextNode textNode : textNodes) {
            String line = textNode.text();
            if (!line.trim().isEmpty()) {
                contents.add(TextUtil.cleanContent(line));
            }
        }
        return contents;
    }
}
