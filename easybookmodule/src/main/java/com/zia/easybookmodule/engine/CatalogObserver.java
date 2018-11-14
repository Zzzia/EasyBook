package com.zia.easybookmodule.engine;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.net.NetUtil;
import com.zia.easybookmodule.rx.Disposable;
import com.zia.easybookmodule.rx.Observer;
import com.zia.easybookmodule.rx.Subscriber;

import java.util.List;

/**
 * Created by zia on 2018/11/15.
 */
public class CatalogObserver implements Observer<List<Catalog>>, Disposable {

    private Book book;

    private Platform platform = Platform.get();
    volatile private boolean attachView = true;

    public CatalogObserver(Book book) {
        this.book = book;
    }

    @Override
    public void dispose() {
        attachView = false;
    }

    @Override
    public Disposable subscribe(final Subscriber<List<Catalog>> subscriber) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Site site = book.getSite();
                    String html = NetUtil.getHtml(book.getUrl(), site.getEncodeType());
                    final List<Catalog> list = site.parseCatalog(html, book.getUrl());
                    post(new Runnable() {
                        @Override
                        public void run() {
                            subscriber.onFinish(list);
                        }
                    });
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).start();
        return this;
    }

    private void post(Runnable runnable) {
        if (attachView) {
            platform.defaultCallbackExecutor().execute(runnable);
        }
    }
}
