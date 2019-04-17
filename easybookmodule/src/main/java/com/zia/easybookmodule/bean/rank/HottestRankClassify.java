package com.zia.easybookmodule.bean.rank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zia on 2019/4/16.
 * 人气榜单中的分类以及书籍信息
 */
public class HottestRankClassify implements Serializable {
    private String rankName;
    private List<RankBook> rankBookList;

    public HottestRankClassify(String rankName, List<RankBook> rankBookList) {
        this.rankName = rankName;
        this.rankBookList = rankBookList;
    }

    @Override
    public String toString() {
        return "HottestRankClassify{" +
                "rankName='" + rankName + '\'' +
                ", rankBookList=" + new ArrayList<>(rankBookList).toString() +
                '}';
    }

    public String getRankName() {
        return rankName;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
    }

    public List<RankBook> getRankBookList() {
        return rankBookList;
    }

    public void setRankBookList(List<RankBook> rankBookList) {
        this.rankBookList = rankBookList;
    }
}
