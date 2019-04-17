package com.zia.easybookmodule.bean.rank;

/**
 * Created by zia on 2019/4/16.
 */
public interface RankConstants {
    String rootUrl = "https://www.qidian.com/rank";
    RankInfo yuepiao = new RankInfo("https://www.qidian.com/rank/yuepiao", "月票榜");
    RankInfo hotsales = new RankInfo("https://www.qidian.com/rank/hotsales", "24小时热销榜");
    RankInfo newvipclick = new RankInfo("https://www.qidian.com/rank/newvipclick", "新锐会员周点击榜");
    RankInfo click = new RankInfo("https://www.qidian.com/rank/click", "会员点击榜");
    RankInfo recom = new RankInfo("https://www.qidian.com/rank/recom", "推荐票榜");
    RankInfo collect = new RankInfo("https://www.qidian.com/rank/collect", "收藏榜");
    RankInfo vipup = new RankInfo("https://www.qidian.com/rank/vipup", "VIP更新榜");
    RankInfo vipcollect = new RankInfo("https://www.qidian.com/rank/vipcollect", "VIP收藏榜");
    RankInfo vipreward = new RankInfo("https://www.qidian.com/rank/vipreward", "本周VIP精品打赏榜");
    RankInfo fin = new RankInfo("https://www.qidian.com/rank/fin", "完本榜");
    RankInfo signnewbook = new RankInfo("https://www.qidian.com/rank/signnewbook", "签约作家新书榜");
    RankInfo pubnewbook = new RankInfo("https://www.qidian.com/rank/pubnewbook", "公众作者新书榜");
    RankInfo newsign = new RankInfo("https://www.qidian.com/rank/newsign", "新人签约新书榜");
    RankInfo newauthor = new RankInfo("https://www.qidian.com/rank/newauthor", "新人作者新书榜");
}
