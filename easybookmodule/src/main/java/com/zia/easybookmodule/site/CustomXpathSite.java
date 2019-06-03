package com.zia.easybookmodule.site;

import androidx.annotation.Nullable;
import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.bean.rule.XpathSiteRule;
import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.engine.SiteCollection;
import com.zia.easybookmodule.net.NetUtil;
import com.zia.easybookmodule.util.BookGriper;
import com.zia.easybookmodule.util.TextUtil;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.jsoup.parser.Parser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zia on 2019-05-31.
 * 自定义站点，通过解析xpath爬取数据
 */
public class CustomXpathSite extends Site {

    private XpathSiteRule xpathRule;

    private BookGriper.CustomCleaner cleaner;

    private HtmlCleaner htmlCleaner = new HtmlCleaner();

    private boolean debug = false;

    public CustomXpathSite(final XpathSiteRule xpathRule) {
        this.xpathRule = xpathRule;
        cleaner = new BookGriper.CustomCleaner() {
            @Nullable
            @Override
            public String clean(String line) {
                String cleanRule = xpathRule.getCleaner();
                if (cleanRule != null && !cleanRule.isEmpty() && line.contains(cleanRule)) {
                    return null;
                } else {
                    return line;
                }
            }
        };
        htmlCleaner.getProperties().setTranslateSpecialEntities(false);
    }

    @Override
    public List<Book> search(String bookName) throws Exception {
        if (xpathRule.getBaseUrl().isEmpty() || xpathRule.getSearchParam().isEmpty()) {
            return new ArrayList<>();
        }

        boolean doGet = true;
        if (!xpathRule.getSearchMethod().toUpperCase().equals("GET")) {
            doGet = false;
        }

        String param = xpathRule.getSearchParam().replace("{keyword}", URLEncoder.encode(bookName, xpathRule.getSearchEncode()));

        String url = xpathRule.getSearchUrl();
        if (doGet) {
            url += "?" + param;
        }

        //请求网页html
        String html;
        if (doGet) {
            html = NetUtil.getHtml(url, getEncodeType());
        } else {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, param);
            html = NetUtil.getHtml(url, requestBody, getEncodeType());
        }

        List<Book> result = new ArrayList<>();
        Document dom = new DomSerializer(htmlCleaner.getProperties()).createDOM(htmlCleaner.clean(html));
        XPath xPath = SiteCollection.getInstance().getxPath();
        Object bookHtmlList = xPath.evaluate(xpathRule.getSearchBookList(), dom, XPathConstants.NODESET);
        if (bookHtmlList instanceof NodeList) {
            NodeList nodeList = (NodeList) bookHtmlList;
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                String searchBookName = xPath.evaluate(xpathRule.getSearchBookName(), node).trim();
                searchBookName = Parser.unescapeEntities(searchBookName, true);
                String searchBookUrl = xPath.evaluate(xpathRule.getSearchBookUrl(), node).trim();
                searchBookUrl = BookGriper.mergeUrl(xpathRule.getBaseUrl(), searchBookUrl);
                String searchAuthor = xPath.evaluate(xpathRule.getSearchAuthor(), node).trim();
                if (searchBookName.isEmpty() || searchBookUrl.isEmpty()) {
                    continue;
                }
                Book book = new Book(searchBookName, searchAuthor, searchBookUrl, getSiteName());
                getExtraInfo(book, node, xpathRule.getSearchExtraRule());
                result.add(book);
            }
        }
        if (debug) {
            System.out.println(result.get(0));
        }
        return result;
    }

    @Override
    public List<Catalog> parseCatalog(String catalogHtml, String rootUrl) throws Exception {
        Document dom = new DomSerializer(htmlCleaner.getProperties()).createDOM(htmlCleaner.clean(catalogHtml));
        XPath xPath = SiteCollection.getInstance().getxPath();
        Object catalogHtmlList = xPath.evaluate(xpathRule.getCatalogChapterList(), dom, XPathConstants.NODESET);
        if (catalogHtmlList instanceof NodeList) {
            NodeList nodeList = (NodeList) catalogHtmlList;
            ArrayList<Catalog> catalogList = new ArrayList<>(nodeList.getLength());
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                String catalogChapterName = xPath.evaluate(xpathRule.getCatalogChapterName(), node).trim();
                catalogChapterName = Parser.unescapeEntities(catalogChapterName, true);
                String catalogChapterUrl = xPath.evaluate(xpathRule.getCatalogChapterUrl(), node).trim();
                catalogChapterUrl = BookGriper.mergeUrl(rootUrl, catalogChapterUrl);
                Catalog catalog = new Catalog(catalogChapterName, catalogChapterUrl);
                catalogList.add(catalog);
            }
            if (debug) {
                System.out.println(catalogList.get(0));
            }
            return catalogList;
        }
        return new ArrayList<>(0);
    }

    @Override
    public List<String> parseContent(String chapterHtml) throws Exception {
        Document dom = new DomSerializer(htmlCleaner.getProperties()).createDOM(htmlCleaner.clean(chapterHtml));
        XPath xPath = SiteCollection.getInstance().getxPath();
        Object lineHtmlList = xPath.evaluate(xpathRule.getChapterLines(), dom, XPathConstants.NODESET);
        if (lineHtmlList instanceof NodeList) {
            NodeList nodeList = (NodeList) lineHtmlList;
            ArrayList<String> lines = new ArrayList<>(nodeList.getLength());
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                String line = node.getNodeValue();
                line = cleaner.clean(line);
                //如果返回为null，那么删除这一行
                if (line == null) continue;
                //清除首尾空格以及特殊字符
                line = TextUtil.cleanContent(line);
                if (!line.trim().isEmpty()) {
                    line = Parser.unescapeEntities(line, true);
                    lines.add(line);
                }
            }
            return lines;
        }
        return new ArrayList<>(0);
    }

    @Override
    public String getSiteName() {
        return xpathRule.getSiteName();
    }

    @Override
    public Book getMoreBookInfo(Book book, String catalogHtml) throws Exception {
        Document dom = new DomSerializer(htmlCleaner.getProperties()).createDOM(htmlCleaner.clean(catalogHtml));
        getExtraInfo(book, dom, xpathRule.getCatalogExtraRule());
        return book;
    }

    @Override
    public String getEncodeType() {
        return xpathRule.getChapterEncodeType();
    }

    private void getExtraInfo(Book book, Node node, XpathSiteRule.ExtraRule extraRule) {
        XPath xPath = SiteCollection.getInstance().getxPath();
        try {
            String imageUrl = xPath.evaluate(extraRule.getImageUrl(), node).trim();
            imageUrl = BookGriper.mergeUrl(xpathRule.getBaseUrl(), imageUrl);
            book.setImageUrl(imageUrl);
        } catch (Exception ignore) {
        }
        try {
            String bookSize = xPath.evaluate(extraRule.getBookSize(), node).trim();
            bookSize = Parser.unescapeEntities(bookSize, true);
            book.setChapterSize(bookSize);
        } catch (Exception ignore) {
        }
        try {
            String lastUpdateTime = xPath.evaluate(extraRule.getLastUpdateTime(), node).trim();
            book.setLastUpdateTime(lastUpdateTime);
        } catch (Exception ignore) {
        }
        try {
            String lastChapterName = xPath.evaluate(extraRule.getLastChapterName(), node).trim();
            lastChapterName = Parser.unescapeEntities(lastChapterName, true);
            book.setLastChapterName(lastChapterName);
        } catch (Exception ignore) {
        }
        try {
            String introduce = xPath.evaluate(extraRule.getIntroduce(), node).trim();
            introduce = Parser.unescapeEntities(introduce, true);
            book.setIntroduce(introduce);
        } catch (Exception ignore) {
        }
        try {
            String classify = xPath.evaluate(extraRule.getClassify(), node).trim();
            book.setClassify(classify);
        } catch (Exception ignore) {
        }
        try {
            String status = xPath.evaluate(extraRule.getStatus(), node).trim();
            book.setStatus(status);
        } catch (Exception ignore) {
        }
        if (debug) {
            System.out.println(book);
        }
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}