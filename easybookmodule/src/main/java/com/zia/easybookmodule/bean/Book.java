package com.zia.easybookmodule.bean;

import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.engine.SiteCollection;

import java.io.Serializable;

/**
 * Created By zia on 2018/10/30.
 */
public class Book implements Serializable {
    private String bookName;
    private String author;
    private String url;
    private String imageUrl;
    private String chapterSize;
    private String lastUpdateTime;
    private String lastChapterName;
    private String siteName;

    public Site getSite() {
        for (Site site : SiteCollection.getInstance().getAllSites()) {
            if (site.getSiteName().equals(siteName)) {
                return site;
            }
        }
        return null;
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
                '}';
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
}
