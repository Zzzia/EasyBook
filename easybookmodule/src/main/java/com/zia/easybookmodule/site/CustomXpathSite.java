package com.zia.easybookmodule.site;

import androidx.annotation.Nullable;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.bean.rule.XpathSiteRule;
import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.net.NetUtil;
import com.zia.easybookmodule.util.BookGriper;
import com.zia.easybookmodule.util.TextUtil;

import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.jsoup.parser.Parser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by zia on 2019-05-31.
 * 自定义站点，通过解析xpath爬取数据
 */
public class CustomXpathSite extends Site {

    private XpathSiteRule xpathRule;

    private XPath xPath = XPathFactory.newInstance().newXPath();

    private BookGriper.CustomCleaner cleaner;

    private HtmlCleaner htmlCleaner = new HtmlCleaner();

    private boolean debug = false;

    public CustomXpathSite(final XpathSiteRule xpathRule) {
        this.xpathRule = xpathRule;
        String cleanRule = xpathRule.getCleaner();
        if (cleanRule != null && !cleanRule.isEmpty()) {
            StringBuilder regex = new StringBuilder();
            for (String s : cleanRule.split("\\|")) {
                regex.append(".*").append(s).append(".*").append("|");
            }
            regex.delete(regex.length() - 1, regex.length());

            final Pattern pattern = Pattern.compile(regex.toString());
            cleaner = new BookGriper.CustomCleaner() {
                @Nullable
                @Override
                public String clean(String line) {
                    if (pattern.matcher(line).matches()) {
                        return null;
                    } else {
                        return line;
                    }
                }
            };
        } else {
            cleaner = new BookGriper.CustomCleaner() {
                @Nullable
                @Override
                public String clean(String line) {
                    return line;
                }
            };
        }
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

        //请求网页html
        String html;
        if (doGet) {
            url += "?" + param;
            html = NetUtil.getHtml(url, getEncodeType());
        } else {
            MediaType JSON = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody requestBody = RequestBody.create(JSON, param);
            html = NetUtil.getHtml(url, requestBody, getEncodeType());
        }
        if (debug) {
            System.out.println(url);
            System.out.println(html);
        }
        List<Book> result = new ArrayList<>();
        Document dom = new DomSerializer(htmlCleaner.getProperties()).createDOM(htmlCleaner.clean(html));
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
                if (debug) {
                    System.out.println(book);
                }
            }
        }
        return result;
    }

    @Override
    public List<Catalog> parseCatalog(String catalogHtml, String rootUrl) throws Exception {
        Document dom = new DomSerializer(htmlCleaner.getProperties()).createDOM(htmlCleaner.clean(catalogHtml));
        return parseCatalog(dom, rootUrl);
    }

    public List<Catalog> parseCatalog(Document dom, String rootUrl) throws Exception {
        Object catalogHtmlList = xPath.evaluate(xpathRule.getCatalogChapterList(), dom, XPathConstants.NODESET);
        if (catalogHtmlList instanceof NodeList) {
            NodeList nodeList = (NodeList) catalogHtmlList;
            if (nodeList.getLength() == 0) {
                throw new XPathException("Xpath获取node个数为0");
            }
            ArrayList<Catalog> catalogList = new ArrayList<>(nodeList.getLength());
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                String catalogChapterName = xPath.evaluate(xpathRule.getCatalogChapterName(), node).trim();
                catalogChapterName = Parser.unescapeEntities(catalogChapterName, true);
                String catalogChapterUrl = xPath.evaluate(xpathRule.getCatalogChapterUrl(), node).trim();
                if (catalogChapterName.isEmpty() && catalogChapterUrl.isEmpty()) {
                    continue;
                }
                catalogChapterUrl = BookGriper.mergeUrl(rootUrl, catalogChapterUrl);
                Catalog catalog = new Catalog(catalogChapterName, catalogChapterUrl);
                catalogList.add(catalog);
            }
            if (debug) {
                if (catalogList.isEmpty()) {
                    System.out.println("catalogList is empty!");
                } else {
                    System.out.println(catalogList.get(0));
                }
            }
            return catalogList;
        }
        return new ArrayList<>(0);
    }

    @Override
    public List<String> parseContent(String chapterHtml) throws Exception {
        Document dom = new DomSerializer(htmlCleaner.getProperties()).createDOM(htmlCleaner.clean(chapterHtml));
        return parseContent(dom);
    }

    public List<String> parseContent(Document dom) throws Exception {
        Object lineHtmlList = xPath.evaluate(xpathRule.getChapterLines(), dom, XPathConstants.NODESET);
        if (lineHtmlList instanceof NodeList) {
            NodeList nodeList = (NodeList) lineHtmlList;
            ArrayList<String> lines = new ArrayList<>(nodeList.getLength());
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                String line = node.getNodeValue();
                //清除首尾空格以及特殊字符
                line = TextUtil.cleanContent(line).trim();
                line = cleaner.clean(line);
                //如果返回为null，那么删除这一行
                if (line == null) continue;
                if (!line.isEmpty()) {
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
        getMoreBookInfo(book, dom);
        return book;
    }

    public Book getMoreBookInfo(Book book, Document dom) {
        getExtraInfo(book, dom, xpathRule.getCatalogExtraRule());
        return book;
    }

    @Override
    public String getEncodeType() {
        return xpathRule.getChapterEncodeType();
    }

    private void getExtraInfo(Book book, Node node, XpathSiteRule.ExtraRule extraRule) {
        if (!extraRule.getImageUrl().isEmpty()) {
            try {
                String imageUrl = xPath.evaluate(extraRule.getImageUrl(), node).trim();
                imageUrl = BookGriper.mergeUrl(xpathRule.getBaseUrl(), imageUrl);
                book.setImageUrl(imageUrl);
            } catch (Exception e) {
                if (debug) {
                    e.printStackTrace();
                }
            }
        }

        if (!extraRule.getBookSize().isEmpty()) {
            try {
                String bookSize = xPath.evaluate(extraRule.getBookSize(), node).trim();
//                bookSize = Parser.unescapeEntities(bookSize, true);
                book.setChapterSize(bookSize);
            } catch (Exception e) {
                if (debug) {
                    e.printStackTrace();
                }
            }
        }

        if (!extraRule.getLastUpdateTime().isEmpty()) {
            try {
                String lastUpdateTime = xPath.evaluate(extraRule.getLastUpdateTime(), node).trim();
                book.setLastUpdateTime(lastUpdateTime);
            } catch (Exception e) {
                if (debug) {
                    e.printStackTrace();
                }
            }
        }

        if (!extraRule.getLastChapterName().isEmpty()) {
            try {
                String lastChapterName = xPath.evaluate(extraRule.getLastChapterName(), node).trim();
                lastChapterName = Parser.unescapeEntities(lastChapterName, true);
                book.setLastChapterName(lastChapterName);
            } catch (Exception e) {
                if (debug) {
                    e.printStackTrace();
                }
            }
        }

        if (!extraRule.getIntroduce().isEmpty()) {
            try {
                String introduce = xPath.evaluate(extraRule.getIntroduce(), node).trim();
                introduce = Parser.unescapeEntities(introduce, true);
                book.setIntroduce(introduce);
            } catch (Exception e) {
                if (debug) {
                    e.printStackTrace();
                }
            }
        }

        if (!extraRule.getClassify().isEmpty()) {
            try {
                String classify = xPath.evaluate(extraRule.getClassify(), node).trim();
                book.setClassify(classify);
            } catch (Exception e) {
                if (debug) {
                    e.printStackTrace();
                }
            }
        }

        if (!extraRule.getStatus().isEmpty()) {
            try {
                String status = xPath.evaluate(extraRule.getStatus(), node).trim();
                book.setStatus(status);
            } catch (Exception e) {
                if (debug) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public HtmlCleaner getHtmlCleaner() {
        return htmlCleaner;
    }
}