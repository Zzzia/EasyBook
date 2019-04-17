package com.zia.easybookmodule.bean.rank;

import java.io.Serializable;

/**
 * Created by zia on 2019/4/16.
 * 排行榜的书籍简介
 */
public class RankBook implements Serializable {
    //书名
    private String bookName;
    private String data_eid;
    private String data_bid;
    //网址
    private String href;
    //图片
    private String imgUrl;
    //作者
    private String author;
    private String authorUrl;
    //分类
    private String classify;
    private String classifyUrl;
    //连载情况
    private String status;
    //简介
    private String intro;
    //最新更新
    private String lastChapter;
    //更新时间
    private String lastUpdateTime;
    //排名
    private int rank;
    //一些其它信息，如月票数，点击数，起点有加密
    private String viewInfo;

    public RankBook(String bookName, String data_eid, String data_bid, String href, String imgUrl, String author, String authorUrl, String classify, String classifyUrl, String status, String intro, String lastChapter, String lastUpdateTime, int rank, String viewInfo) {
        this.bookName = bookName;
        this.data_eid = data_eid;
        this.data_bid = data_bid;
        this.href = href;
        this.imgUrl = imgUrl;
        this.author = author;
        this.authorUrl = authorUrl;
        this.classify = classify;
        this.classifyUrl = classifyUrl;
        this.status = status;
        this.intro = intro;
        this.lastChapter = lastChapter;
        this.lastUpdateTime = lastUpdateTime;
        this.rank = rank;
        this.viewInfo = viewInfo;
    }

    @Override
    public String toString() {
        return "RankBook{" +
                "bookName='" + bookName + '\'' +
                ", data_eid='" + data_eid + '\'' +
                ", data_bid='" + data_bid + '\'' +
                ", href='" + href + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", author='" + author + '\'' +
                ", authorUrl='" + authorUrl + '\'' +
                ", classify='" + classify + '\'' +
                ", classifyUrl='" + classifyUrl + '\'' +
                ", status='" + status + '\'' +
                ", intro='" + intro + '\'' +
                ", lastChapter='" + lastChapter + '\'' +
                ", lastUpdateTime='" + lastUpdateTime + '\'' +
                ", rank=" + rank +
                ", viewInfo='" + viewInfo + '\'' +
                '}';
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getData_eid() {
        return data_eid;
    }

    public void setData_eid(String data_eid) {
        this.data_eid = data_eid;
    }

    public String getData_bid() {
        return data_bid;
    }

    public void setData_bid(String data_bid) {
        this.data_bid = data_bid;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    public String getClassifyUrl() {
        return classifyUrl;
    }

    public void setClassifyUrl(String classifyUrl) {
        this.classifyUrl = classifyUrl;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
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

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(String lastChapter) {
        this.lastChapter = lastChapter;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getViewInfo() {
        return viewInfo;
    }

    public void setViewInfo(String viewInfo) {
        this.viewInfo = viewInfo;
    }
}
