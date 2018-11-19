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
public class ContentObserver implements Observer<List<String>>, Disposable {

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
    }

    @Override
    public Disposable subscribe(final Subscriber<List<String>> subscriber) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Site site = book.getSite();
                    String html = NetUtil.getHtml(catalog.getUrl(), site.getEncodeType());
                    final List<String> list = site.parseContent(html);
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
        service.shutdownNow();
        if (attachView) {
            platform.defaultCallbackExecutor().execute(runnable);
        }
    }
}
