package com.zia.easybookmodule.engine.parser.rank;

import com.zia.easybookmodule.bean.rank.HottestRank;
import com.zia.easybookmodule.bean.rank.HottestRankClassify;
import com.zia.easybookmodule.bean.rank.RankClassify;
import com.zia.easybookmodule.bean.rank.RankInfo;
import com.zia.easybookmodule.engine.Platform;
import com.zia.easybookmodule.net.NetUtil;
import com.zia.easybookmodule.rx.Disposable;
import com.zia.easybookmodule.rx.Observer;
import com.zia.easybookmodule.rx.Subscriber;
import com.zia.easybookmodule.util.RankUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zia on 2019/4/16.
 * 起点人气榜单
 */
public class HottestRankObserver implements Observer<HottestRank>, Disposable {

    private final static RankInfo hottestRankInfo = new RankInfo("https://www.qidian.com/rank", "人气榜单", "qd_C01");

    private Platform platform = Platform.get();
    volatile private boolean attachView = true;
    private ExecutorService service = Executors.newCachedThreadPool();

    @Override
    public void dispose() {
        attachView = false;
        service.shutdownNow();
    }

    @Override
    public Disposable subscribe(final Subscriber<HottestRank> subscriber) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final HottestRank hottestRank = getSync();
                    post(new Runnable() {
                        @Override
                        public void run() {
                            subscriber.onFinish(hottestRank);
                        }
                    });
                } catch (final Exception e) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            subscriber.onError(e);
                        }
                    });
                }
            }
        });
        return this;
    }

    @Override
    public HottestRank getSync() throws Exception {
        String url = RankUtil.getUrl(hottestRankInfo);
        String html = NetUtil.getHtml(url, "utf-8");
        Document document = Jsoup.parse(html);
        //其他排行榜
        List<RankInfo> rankInfos = RankUtil.getRankInfoList(document);
        List<RankClassify> rankClassifyList = RankUtil.getRankClassifyList(document);
        String updateTime = RankUtil.getHottestUpdateTime(document);
        List<HottestRankClassify> hottestRankClassifyList = RankUtil.getHottestRankClassifyList(document);
        return new HottestRank(rankClassifyList, updateTime, hottestRankClassifyList, rankInfos);
    }

    private void post(Runnable runnable) {
        service.shutdownNow();
        if (attachView) {
            platform.defaultCallbackExecutor().execute(runnable);
        }
    }
}
