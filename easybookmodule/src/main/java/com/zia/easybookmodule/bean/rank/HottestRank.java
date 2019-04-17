package com.zia.easybookmodule.bean.rank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zia on 2019/4/16.
 * https://www.qidian.com/rank
 * 人气榜单，入口
 */
public class HottestRank implements Serializable {

    //排行榜类型
    private List<RankClassify> rankClassifies;

    //人气榜单最近更新时间
    private String updateTime;

    //人气榜单中的某一小说类型的书籍排行
    private List<HottestRankClassify> hottestRankClassifies;

    //其他排行榜
    private List<RankInfo> rankInfos;

    public HottestRank(List<RankClassify> rankClassifies, String updateTime, List<HottestRankClassify> hottestRankClassifies, List<RankInfo> rankInfos) {
        this.rankClassifies = rankClassifies;
        this.updateTime = updateTime;
        this.hottestRankClassifies = hottestRankClassifies;
        this.rankInfos = rankInfos;
    }

    @Override
    public String toString() {
        return "HottestRank{" +
                "\nrankClassifies=" + new ArrayList<>(rankClassifies) +
                "\n, updateTime='" + updateTime + '\'' +
                "\n, hottestRankClassifies=" + new ArrayList<>(hottestRankClassifies) +
                ", rankInfos=" + new ArrayList<>(rankInfos) +
                '}';
    }

    public List<RankClassify> getRankClassifies() {
        return rankClassifies;
    }

    public void setRankClassifies(List<RankClassify> rankClassifies) {
        this.rankClassifies = rankClassifies;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public List<HottestRankClassify> getHottestRankClassifies() {
        return hottestRankClassifies;
    }

    public void setHottestRankClassifies(List<HottestRankClassify> hottestRankClassifies) {
        this.hottestRankClassifies = hottestRankClassifies;
    }

    public List<RankInfo> getRankInfos() {
        return rankInfos;
    }

    public void setRankInfos(List<RankInfo> rankInfos) {
        this.rankInfos = rankInfos;
    }
}
