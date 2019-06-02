package com.zia.easybookmodule.bean.rule;

import java.io.Serializable;

/**
 * Created by zia on 2019-05-31.
 * 根据Xpath规则解析小说的bean类
 */
public class XpathSiteRule implements Serializable {
    //小说网站规则的基本信息
    private String siteName = "";//网站名
    private String baseUrl = "";//网站根目录
    private boolean enable = true;//是否使用这个解析
    private String siteClassify = "";//网站分类，非必要
    private String ruleUpdateTime = "";//解析更新时间，非必要
    private String author = "";//解析作者，非必要

    //搜索页面基本信息
    private String searchUrl = "";//搜索的地址，可能是get请求，也可能是post，二选一
    private String searchMethod = "GET";
    //get请求的key，格式是  如q={keyword} ，keyword由用户输入，会自动拼接成https://www.xx.com/searchUrl&q=keyword
    //post请求的params，格式如  key={keyword} ，keyword会由用户输入
    private String searchParam = "";
    private String searchEncode = "UTF-8";//请求的编码

    private String searchBookList = "";//搜索也小说列表解析
    private String searchBookName = "";//书籍名字解析规则
    private String searchBookUrl = "";//目录页url解析规则
    private String searchAuthor = "";//作者解析规则
    //非必要，可以在目录页解析时补充
    private ExtraRule searchExtraRule = null;

    //目录页面基本信息
    private String catalogChapterList = "";//目录列表解析规则
    private String catalogChapterName = "";//单个章节的名字解析规则
    private String catalogChapterUrl = "";//单个章节的url解析规则
    //非必要，用于补充book信息
    private ExtraRule catalogExtraRule = null;

    //章节页面基本信息
    private String chapterLines = "";//内容行的解析规则
    private String chapterEncodeType = "GBK";//编码
    private String cleaner = "";//广告信息的清除规则

    public static class ExtraRule implements Serializable {
        private String imageUrl = "";//封面地址解析规则
        private String bookSize = "";//小说大小解析规则
        private String lastUpdateTime = "";//最后更新时间解析
        private String lastChapterName = "";//最新章节名解析
        private String introduce = "";//简介解析
        private String classify = "";//分类解析
        private String status = "";//小说状态，连载，完结等

        @Override
        public String toString() {
            return "ExtraRule{" +
                    "imageUrl='" + imageUrl + '\'' +
                    ", bookSize='" + bookSize + '\'' +
                    ", lastUpdateTime='" + lastUpdateTime + '\'' +
                    ", lastChapterName='" + lastChapterName + '\'' +
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

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getBookSize() {
            return bookSize;
        }

        public void setBookSize(String bookSize) {
            this.bookSize = bookSize;
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

        public String getIntroduce() {
            return introduce;
        }

        public void setIntroduce(String introduce) {
            this.introduce = introduce;
        }

        public String getClassify() {
            return classify;
        }

        public void setClassify(String classify) {
            this.classify = classify;
        }
    }

    @Override
    public String toString() {
        return "XpathSiteRule{" +
                "siteName='" + siteName + '\'' +
                ", baseUrl='" + baseUrl + '\'' +
                ", enable=" + enable +
                ", siteClassify='" + siteClassify + '\'' +
                ", ruleUpdateTime='" + ruleUpdateTime + '\'' +
                ", author='" + author + '\'' +
                ", searchUrl='" + searchUrl + '\'' +
                ", searchMethod='" + searchMethod + '\'' +
                ", searchParam='" + searchParam + '\'' +
                ", searchEncode='" + searchEncode + '\'' +
                ", searchBookList='" + searchBookList + '\'' +
                ", searchBookName='" + searchBookName + '\'' +
                ", searchBookUrl='" + searchBookUrl + '\'' +
                ", searchAuthor='" + searchAuthor + '\'' +
                ", searchExtraRule=" + searchExtraRule +
                ", catalogChapterList='" + catalogChapterList + '\'' +
                ", catalogChapterName='" + catalogChapterName + '\'' +
                ", catalogChapterUrl='" + catalogChapterUrl + '\'' +
                ", catalogExtraRule=" + catalogExtraRule +
                ", chapterLines='" + chapterLines + '\'' +
                ", chapterEncodeType='" + chapterEncodeType + '\'' +
                ", cleaner='" + cleaner + '\'' +
                '}';
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSearchBookList() {
        return searchBookList;
    }

    public void setSearchBookList(String searchBookList) {
        this.searchBookList = searchBookList;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getSiteClassify() {
        return siteClassify;
    }

    public void setSiteClassify(String siteClassify) {
        this.siteClassify = siteClassify;
    }

    public String getRuleUpdateTime() {
        return ruleUpdateTime;
    }

    public void setRuleUpdateTime(String ruleUpdateTime) {
        this.ruleUpdateTime = ruleUpdateTime;
    }

    public String getChapterEncodeType() {
        return chapterEncodeType;
    }

    public void setChapterEncodeType(String chapterEncodeType) {
        this.chapterEncodeType = chapterEncodeType;
    }

    public String getSearchUrl() {
        return searchUrl;
    }

    public void setSearchUrl(String searchUrl) {
        this.searchUrl = searchUrl;
    }

    public String getSearchMethod() {
        return searchMethod;
    }

    public void setSearchMethod(String searchMethod) {
        this.searchMethod = searchMethod;
    }

    public String getSearchParam() {
        return searchParam;
    }

    public void setSearchParam(String searchParam) {
        this.searchParam = searchParam;
    }

    public String getSearchEncode() {
        return searchEncode;
    }

    public void setSearchEncode(String searchEncode) {
        this.searchEncode = searchEncode;
    }

    public String getSearchBookName() {
        return searchBookName;
    }

    public void setSearchBookName(String searchBookName) {
        this.searchBookName = searchBookName;
    }

    public String getSearchBookUrl() {
        return searchBookUrl;
    }

    public void setSearchBookUrl(String searchBookUrl) {
        this.searchBookUrl = searchBookUrl;
    }

    public String getSearchAuthor() {
        return searchAuthor;
    }

    public void setSearchAuthor(String searchAuthor) {
        this.searchAuthor = searchAuthor;
    }

    public ExtraRule getSearchExtraRule() {
        return searchExtraRule;
    }

    public void setSearchExtraRule(ExtraRule searchExtraRule) {
        this.searchExtraRule = searchExtraRule;
    }

    public String getCatalogChapterList() {
        return catalogChapterList;
    }

    public void setCatalogChapterList(String catalogChapterList) {
        this.catalogChapterList = catalogChapterList;
    }

    public String getCatalogChapterName() {
        return catalogChapterName;
    }

    public void setCatalogChapterName(String catalogChapterName) {
        this.catalogChapterName = catalogChapterName;
    }

    public String getCatalogChapterUrl() {
        return catalogChapterUrl;
    }

    public void setCatalogChapterUrl(String catalogChapterUrl) {
        this.catalogChapterUrl = catalogChapterUrl;
    }

    public ExtraRule getCatalogExtraRule() {
        return catalogExtraRule;
    }

    public void setCatalogExtraRule(ExtraRule catalogExtraRule) {
        this.catalogExtraRule = catalogExtraRule;
    }

    public String getChapterLines() {
        return chapterLines;
    }

    public void setChapterLines(String chapterLines) {
        this.chapterLines = chapterLines;
    }

    public String getCleaner() {
        return cleaner;
    }

    public void setCleaner(String cleaner) {
        this.cleaner = cleaner;
    }
}
