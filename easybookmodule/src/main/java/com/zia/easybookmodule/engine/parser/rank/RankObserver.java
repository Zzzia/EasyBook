package com.zia.easybookmodule.engine.parser.rank;

import com.zia.easybookmodule.bean.rank.Rank;
import com.zia.easybookmodule.bean.rank.RankBook;
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
 */
public class RankObserver implements Observer<Rank>, Disposable {

    private RankInfo rankInfo;

    private Platform platform = Platform.get();
    volatile private boolean attachView = true;
    private ExecutorService service = Executors.newCachedThreadPool();

    public RankObserver(RankInfo rankInfo) {
        this.rankInfo = rankInfo;
    }

    @Override
    public void dispose() {
        attachView = false;
        service.shutdownNow();
    }

    @Override
    public Disposable subscribe(final Subscriber<Rank> subscriber) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Rank rank = getSync();
                    post(new Runnable() {
                        @Override
                        public void run() {
                            subscriber.onFinish(rank);
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
        return null;
    }

    @Override
    public Rank getSync() throws Exception {
        String url = RankUtil.getUrl(rankInfo);
        String html = NetUtil.getHtml(url, "utf-8");
        Document document = Jsoup.parse(html);
        List<RankClassify> rankClassifies = RankUtil.getRankClassifyList(document);
        List<RankBook> rankBookList = RankUtil.getRankBookList(document);
        int maxPageSize = RankUtil.getMaxPageSize(document);
        int currentPage = RankUtil.getCurrentPage(document);
        return new Rank(rankClassifies, rankBookList, rankInfo, maxPageSize, currentPage);
    }

    private void post(Runnable runnable) {
        service.shutdownNow();
        if (attachView) {
            platform.defaultCallbackExecutor().execute(runnable);
        }
    }
}
