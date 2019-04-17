package com.zia.easybookmodule.bean.rank;

import java.io.Serializable;

/**
 * Created by zia on 2019/4/16.
 * 榜单信息，可以用这个来生成榜单网址
 * 如https://www.qidian.com/rank?style=1&chn=21&page=1&dateType
 */
public class RankInfo implements Serializable {
    //url
    private String url;
    //榜单名字，如原创风云榜，24小时热榜等
    private String rankName;
    //不知道是啥
    private String data_eid;
    //分类名字，如玄幻，奇幻
    private String classifyName = "全部分类";
    //网页板式，1有图片和简介，2没有
    private int style = 1;
    //分类id，如玄幻，武侠，-1是全部分类
    private int chn = -1;
    //分页，起始为1
    private int page = 1;
    //排行时间，周榜1，月榜2，总榜3
    private int dateType = 1;

    public RankInfo(String url, String rankName, String data_eid) {
        this.url = url;
        this.rankName = rankName;
        this.data_eid = data_eid;
    }

    public RankInfo(String url, String rankName) {
        this.url = url;
        this.rankName = rankName;
    }

    @Override
    public String toString() {
        return "RankInfo{" +
                "url='" + url + '\'' +
                ", rankName='" + rankName + '\'' +
                ", data_eid='" + data_eid + '\'' +
                ", classifyName='" + classifyName + '\'' +
                ", style=" + style +
                ", chn=" + chn +
                ", page=" + page +
                ", dateType=" + dateType +
                '}';
    }

    public int getDateType() {
        return dateType;
    }

    public void setDateType(int dateType) {
        this.dateType = dateType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRankName() {
        return rankName;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
    }

    public String getData_eid() {
        return data_eid;
    }

    public void setData_eid(String data_eid) {
        this.data_eid = data_eid;
    }

    public String getClassifyName() {
        return classifyName;
    }

    public void setClassifyName(String classifyName) {
        this.classifyName = classifyName;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public int getChn() {
        return chn;
    }

    public void setChn(int chn) {
        this.chn = chn;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
