package com.zia.easybookmodule.engine.parser;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Chapter;
import com.zia.easybookmodule.rx.Disposable;
import com.zia.easybookmodule.rx.Observer;
import com.zia.easybookmodule.rx.Subscriber;

import java.util.ArrayList;

/**
 * Created by zia on 2019-05-14.
 * 分段下载工具
 */
public class PartDownloadObserver implements Observer<ArrayList<Chapter>>, Disposable {

    private Book book;
    private int from, to;
    private int threadCount = 50;

    private DownloadEngine downloadEngine;

    public PartDownloadObserver(Book book, int from, int to) {
        this.book = book;
        this.from = from;
        this.to = to;
    }

    @Override
    public void dispose() {
        if (downloadEngine != null) {
            downloadEngine.dispose();
        }
    }

    @Override
    public Disposable subscribe(final Subscriber<ArrayList<Chapter>> subscriber) {
        downloadEngine = new DownloadEngine(book, threadCount, from, to);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Chapter> chapters = downloadEngine.download(subscriber);
                subscriber.onFinish(chapters);
            }
        }).start();
        return this;
    }

    public PartDownloadObserver setThreadCount(int threadCount) {
        this.threadCount = threadCount;
        return this;
    }
}
