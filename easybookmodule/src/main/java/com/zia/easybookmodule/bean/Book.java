package com.zia.easybookmodule.bean;

import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.engine.SiteCollection;

import java.io.Serializable;

/**
 * Created By zia on 2018/10/30.
 */
public class Book implements Serializable {
    private String bookName = "";
    private String author = "";
    //小说目录页地址
    private String url = "";
    private String imageUrl = "";
    //章节数量
    private String chapterSize = "";
    //最后更新时间
    private String lastUpdateTime = "";
    //最新章节名
    private String lastChapterName = "";
    //小说网站名字
    private String siteName = "";
    //小说简介
    private String introduce = "";
    //小说分类
    private String classify = "";
    //小说状态，连载，完结等
    private String status = "";

    public Site getSite() {
        for (Site site : SiteCollection.getInstance().getAllSites()) {
            if (site.getSiteName().equals(siteName)) {
                return site;
            }
        }
        return null;
    }

    //这个类应该由书名和小说网站名、作者来唯一确定
    @Deprecated
    public Book() {
    }

    public Book(String bookName, String author, String url,String siteName) {
        this.bookName = bookName;
        this.author = author;
        this.url = url;
        this.siteName = siteName;
    }

    public Book(String bookName, String author, String url, String chapterSize, String lastUpdateTime, String lastChapterName, String siteName) {
        this(bookName, author, url, "", chapterSize, lastUpdateTime, lastChapterName, siteName);
    }

    public Book(String bookName, String author, String url, String imageUrl, String chapterSize, String lastUpdateTime, String lastChapterName, String siteName) {
        this.bookName = bookName;
        this.author = author;
        this.url = url;
        this.imageUrl = imageUrl;
        this.chapterSize = chapterSize;
        this.lastUpdateTime = lastUpdateTime;
        this.lastChapterName = lastChapterName;
        this.siteName = siteName;
    }

    public Book(String bookName, String author, String url, String imageUrl, String chapterSize, String lastUpdateTime, String lastChapterName, String siteName, String introduce) {
        this.bookName = bookName;
        this.author = author;
        this.url = url;
        this.imageUrl = imageUrl;
        this.chapterSize = chapterSize;
        this.lastUpdateTime = lastUpdateTime;
        this.lastChapterName = lastChapterName;
        this.siteName = siteName;
        this.introduce = introduce;
    }

    public Book(String bookName, String author, String url, String imageUrl, String chapterSize, String lastUpdateTime, String lastChapterName, String siteName, String introduce, String classify) {
        this.bookName = bookName;
        this.author = author;
        this.url = url;
        this.imageUrl = imageUrl;
        this.chapterSize = chapterSize;
        this.lastUpdateTime = lastUpdateTime;
        this.lastChapterName = lastChapterName;
        this.siteName = siteName;
        this.introduce = introduce;
        this.classify = classify;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookName='" + bookName + '\'' +
                ", author='" + author + '\'' +
                ", url='" + url + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", chapterSize='" + chapterSize + '\'' +
                ", lastUpdateTime='" + lastUpdateTime + '\'' +
                ", lastChapterName='" + lastChapterName + '\'' +
                ", siteName='" + siteName + '\'' +
                ", introduce='" + introduce + '\'' +
                ", classify='" + classify + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getChapterSize() {
        return chapterSize;
    }

    public void setChapterSize(String chapterSize) {
        this.chapterSize = chapterSize;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getLastChapterName() {
        return lastChapterName;
    }

    public void setLastChapterName(String lastChapterName) {
        this.lastChapterName = lastChapterName;
    }

    public static int compare(String targetName, Book o1, Book o2) {
        if (o1.getBookName().equals(targetName) && !o2.getBookName().equals(targetName)) {
            return -1;
        } else if (!o1.getBookName().equals(targetName) && o2.getBookName().equals(targetName)) {
            return 1;
        }
        //包含了字符
        else if (o1.getBookName().contains(targetName) && !o2.getBookName().contains(targetName)) {
            return -1;
        } else if (!o1.getBookName().contains(targetName) && o2.getBookName().contains(targetName)) {
            return 1;
        } else if (o1.getBookName().contains(targetName) && o2.getBookName().contains(targetName)) {
            return o1.getBookName().indexOf(targetName) - o2.getBookName().indexOf(targetName);
        }
        //长度相同
        else if (o1.getBookName().length() == targetName.length()
                && o2.getBookName().length() != targetName.length()) {
            return -1;
        } else if (o1.getBookName().length() != targetName.length()
                && o2.getBookName().length() == targetName.length()) {
            return 1;
        }
        return 0;
    }
}
