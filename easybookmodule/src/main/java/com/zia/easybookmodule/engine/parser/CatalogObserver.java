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
public class CatalogObserver implements Observer<List<Catalog>>, Disposable {

    private Book book;

    private Platform platform;
    volatile private boolean attachView = true;
    private ExecutorService service = Executors.newCachedThreadPool();

    public CatalogObserver(Book book) {
        this.book = book;
    }

    @Override
    public void dispose() {
        attachView = false;
        service.shutdownNow();
    }

    @Override
    public Disposable subscribe(final Subscriber<List<Catalog>> subscriber) {
        platform = Platform.get();
        service.execute(new Runnable() {
            @Override
            public void run() {
                String html = "";
                Site site = book.getSite();
                try {
                    html = NetUtil.getHtml(book.getUrl(), site.getEncodeType());
                    final List<Catalog> list = site.parseCatalog(html, book.getUrl());
                    post(new Runnable() {
                        @Override
                        public void run() {
                            subscriber.onFinish(list);
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

    private void post(Runnable runnable) {
        if (attachView) {
            platform.defaultCallbackExecutor().execute(runnable);
        }
    }
}
