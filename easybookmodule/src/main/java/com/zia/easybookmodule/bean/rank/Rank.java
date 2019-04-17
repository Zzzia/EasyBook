package com.zia.easybookmodule.bean.rank;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zia on 2019/4/16.
 * 榜单内容
 */
public class Rank implements Serializable {
    //小说类型
    private List<RankClassify> rankClassifies;

    //小说内容
    private List<RankBook> rankBookList;

    //传入一个榜单自身信息
    private RankInfo rankInfo;

    //最大页数
    private int maxPage;

    //当前页数
    private int currentPage;

    public Rank(List<RankClassify> rankClassifies, List<RankBook> rankBookList, RankInfo rankInfo, int maxPage, int currentPage) {
        this.rankClassifies = rankClassifies;
        this.rankBookList = rankBookList;
        this.rankInfo = rankInfo;
        this.maxPage = maxPage;
        this.currentPage = currentPage;
    }

    @Override
    public String toString() {
        return "Rank{" +
                "rankClassifies=" + rankClassifies +
                ", rankBookList=" + rankBookList +
                ", rankInfo=" + rankInfo +
                ", maxPage=" + maxPage +
                ", currentPage=" + currentPage +
                '}';
    }

    public List<RankClassify> getRankClassifies() {
        return rankClassifies;
    }

    public void setRankClassifies(List<RankClassify> rankClassifies) {
        this.rankClassifies = rankClassifies;
    }

    public List<RankBook> getRankBookList() {
        return rankBookList;
    }

    public void setRankBookList(List<RankBook> rankBookList) {
        this.rankBookList = rankBookList;
    }

    public RankInfo getRankInfo() {
        return rankInfo;
    }

    public void setRankInfo(RankInfo rankInfo) {
        this.rankInfo = rankInfo;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
