package com.zia.easybookmodule.util;

import androidx.annotation.Nullable;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.net.NetUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
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
        return parseBaiduBooks("http://zhannei.baidu.com/", html, siteName);
    }

    public static List<Book> parseBaiduBooks(String baseUrl, String html, String siteName) throws IOException {
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
            String introduce = detail.getElementsByTag("p").get(0).text();

            bkUrl = mergeUrl(baseUrl, bkUrl);

            Element infoDiv = item.getElementsByClass("result-game-item-info").first();

            Elements ps = infoDiv.getElementsByTag("p");
            String author = ps.get(0).text().replaceAll("作者：|作者:| ", "");
            String classify = ps.get(1).getElementsByTag("span").get(1).text();
            if (classify.length() > 3 && classify.endsWith("小说")) {
                classify = classify.replaceAll("小说", "");
            }
            String lastUpdateTime = ps.get(2).text().replaceAll("更新时间：|更新时间:| ", "");
            String lastChapterName = ps.get(3).text().replaceAll("最新章节：|最新章节:| ", "");
            String imageUrl = item.getElementsByTag("img").get(0).attr("src");
            Book book = new Book(bkName, author, bkUrl, imageUrl, "未知", lastUpdateTime, lastChapterName, siteName, introduce, classify);
            bookList.add(book);
        }
        return bookList;
    }

    public static List<Catalog> parseBqgCatalogs(String catalogHtml, String url) {
        if (!url.endsWith("/")) {
            //保证url合并的效果
            url += "/";
        }
        String sub = RegexUtil.regexExcept("<div id=\"list\">", "</div>", catalogHtml).get(0);
        String[] subs = sub.split("正文</dt>|正文卷</dt>");
        if (subs.length == 2) {
            sub = subs[1];
        }
        List<String> as = RegexUtil.regexInclude("<a", "</a>", sub);
        List<Catalog> list = new ArrayList<>();
        for (String s : as) {
            RegexUtil.Tag tag = new RegexUtil.Tag(s);
            String name = tag.getText();
            String href = tag.getValue("href");
            href = href.replace("http://", "").replace("https://", "");
            href = mergeUrl(url, href);
//            String href = mergeUrl(url, tag.getValue("href"));
            list.add(new Catalog(name, href));
        }
        return list;
    }

    public static String mergeUrl(String root, String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        if (root.endsWith(".html") || root.endsWith(".htm")) {
            // https://www.hkslg8.com/430/430082/index.html + /430/430082/86638627.html
            String r = root.substring(0, root.lastIndexOf("/"));
            return r + url.substring(getUrlSameLength(r, url));
        } else {
            // https://www.230book.com/book/8996/ + 123.html
            return root + url.substring(getUrlSameLength(root, url));
        }
    }

    private static int getUrlSameLength(String root, String s) {
        if (root.length() == 0 || s.length() == 0) return 0;
        char s1 = s.charAt(0);
        for (int i = 0; i < root.length(); i++) {
            char a = root.charAt(i);
            if (s1 != a) continue;
            //如果第一位匹配上了，继续匹配
            int index = 1;
            for (; index < root.length() - i; index++) {
                //如果超出第二个字符串范围，跳出
                if (index >= s.length() - 1) {
                    break;
                }
                //如果有一个没匹配上，跳出
                if (root.charAt(i + index) != s.charAt(index)) {
                    break;
                }
            }
            //全部匹配上了，返回下标
            if (index == root.length() - i) {
                return index;
            }
        }
        //没有匹配
        return 0;
    }

    public interface CustomCleaner {
        /**
         * 清理文本广告等
         *
         * @param line 一行文字内容
         * @return 返回字符串，或null删除该行
         */
        @Nullable
        String clean(String line);
    }

    public static List<String> getContentsByBR(String content) {
        return getContentsByBR(content, null);
    }

    public static List<String> getContentsByBR(String content, CustomCleaner cleaner) {
        String[] lines = content.split("<br>|<br/>|<br />");
        return getContents(Arrays.asList(lines), cleaner);
    }


    public static List<String> getContentsByTextNodes(List<TextNode> textNodes) {
        return getContentsByTextNodes(textNodes, null);
    }

    public static List<String> getContentsByTextNodes(List<TextNode> textNodes, CustomCleaner cleaner) {
        List<String> lines = new ArrayList<>();
        for (TextNode textNode : textNodes) {
            lines.add(textNode.text());
        }
        return getContents(lines, cleaner);
    }

    public static List<String> getContents(List<String> lines, CustomCleaner cleaner) {
        List<String> contents = new ArrayList<>();
        for (String line : lines) {
            //自定义清除
            if (cleaner != null) {
                line = cleaner.clean(line);
            }
            //如果返回为null，那么删除这一行
            if (line == null) continue;
            //清除首尾空格以及特殊字符
            line = TextUtil.cleanContent(line);
            if (!line.trim().isEmpty()) {
                contents.add(line);
            }
        }
        return contents;
    }

    public static Book getBqgMoreInfo(Book book, String catalogHtml, String rootUrl) {
        Document document = Jsoup.parse(catalogHtml);
        String intro = document.getElementById("intro").text();
        String imgUrl = mergeUrl(rootUrl, document.getElementById("fmimg").getElementsByTag("img").first().attr("src"));
        Elements ps = document.getElementById("info").getElementsByTag("p");
        try {
            String lastUpdateTime = ps.get(2).text();
            int i = lastUpdateTime.indexOf("：");
            if (i > 0) {
                lastUpdateTime = lastUpdateTime.substring(i + 1);
            }
            String lastChapterName = ps.get(3).getElementsByTag("a").first().text();
            book.setLastUpdateTime(lastUpdateTime);
            book.setLastChapterName(lastChapterName);
        } catch (Exception ignore) {
            //有可能没有章节名和最新时间
        }
        book.setIntroduce(intro);
        book.setImageUrl(imgUrl);

        return book;
    }

    public static String formatTime(String time) {
        int index = time.indexOf(" ");
        if (index <= 0) {
            return time;
        }
        return time.substring(0, index);
    }
}
