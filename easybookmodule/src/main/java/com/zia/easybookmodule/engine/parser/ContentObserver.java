package com.zia.easybookmodule.engine.parser;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.engine.Platform;
import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.net.NetUtil;
import com.zia.easybookmodule.rx.Disposable;
import com.zia.easybookmodule.rx.Observer;
import com.zia.easybookmodule.rx.Subscriber;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zia on 2018/11/15.
 */
public class ContentObserver implements Observer<List<String>> {

    private Book book;
    private Catalog catalog;

    private Platform platform = Platform.get();
    volatile private boolean attachView = true;
    private ExecutorService service = Executors.newCachedThreadPool();

    public ContentObserver(Book book, Catalog catalog) {
        this.book = book;
        this.catalog = catalog;
    }

    @Override
    public void dispose() {
        attachView = false;
        service.shutdownNow();
    }

    @Override
    public Disposable subscribe(final Subscriber<List<String>> subscriber) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<String> lines = getSync();
                    post(new Runnable() {
                        @Override
                        public void run() {
                            subscriber.onFinish(lines);
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
    public List<String> getSync() throws Exception {
        Site site = book.getSite();
        String html = NetUtil.getHtml(catalog.getUrl(), site.getEncodeType());
        return site.parseContent(html);
    }

    private void post(Runnable runnable) {
        service.shutdownNow();
        if (attachView) {
            platform.defaultCallbackExecutor().execute(runnable);
        }
    }
}
