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
public class CatalogObserver implements Observer<List<Catalog>> {

    private Book book;

    private Platform platform;
    volatile private boolean attachView = true;
    private ExecutorService service = Executors.newCachedThreadPool();

    //默认在解析目录时更新book中的内容，如简介等
    private boolean openGetMoreInfo = true;

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
                try {
                    final List<Catalog> list = getSync();
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

    @Override
    public List<Catalog> getSync() throws Exception {
        Site site = book.getSite();
        String html = NetUtil.getHtml(book.getUrl(), site.getEncodeType());
        //解析更多内容
        if (openGetMoreInfo) {
            site.getMoreBookInfo(book, html);
        }
        return site.parseCatalog(html, book.getUrl());
    }

    private void post(Runnable runnable) {
        if (attachView) {
            platform.defaultCallbackExecutor().execute(runnable);
        }
    }

    //预留一个关闭解析更多的方法
    public void setOpenGetMoreInfo(boolean openGetMoreInfo) {
        this.openGetMoreInfo = openGetMoreInfo;
    }
}
