package com.zia.easybookmodule.engine.parser;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Chapter;
import com.zia.easybookmodule.bean.Type;
import com.zia.easybookmodule.engine.Platform;
import com.zia.easybookmodule.engine.strategy.EpubParser;
import com.zia.easybookmodule.engine.strategy.ParseStrategy;
import com.zia.easybookmodule.engine.strategy.TxtParser;
import com.zia.easybookmodule.rx.Disposable;
import com.zia.easybookmodule.rx.Observer;
import com.zia.easybookmodule.rx.Subscriber;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by zia on 2018/11/13.
 * 带重试队列和进度监听的并发下载工具
 * 进度监听会严重影响性能，使用了一个新线程每隔0.5s检查一次
 */

/**
 * Created by zia on 2018/11/25.
 * 关于性能，最开始自己实现了同步方法，后来发现会有性能问题
 * Jrt和Art对于同步的优化是不太一样的
 * art虚拟机下，lock性能远强于synchronized，jrt里相反，所以这里用lock
 * 最快的始终还是并发集合，所以能用自带的就用自带的
 * https://blog.csdn.net/ganyao939543405/article/details/52486316
 */
public class DownloadObserver implements Observer<File>, Disposable {

    private int threadCount = 150;
    private String savePath = ".";
    private Book book;
    private Platform platform = Platform.get();

    private DownloadEngine downloadEngine;

    private ParseStrategy parser = new TxtParser();
    volatile private boolean attachView = true;

    public DownloadObserver(Book book) {
        this.book = book;
    }

    @Override
    public Disposable subscribe(final Subscriber<File> subscriber) {
        downloadEngine = new DownloadEngine(book, threadCount);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(savePath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    saveFile(subscriber);
                } catch (final Exception e) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            subscriber.onError(e);
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
        return this;
    }

    @Override
    public void dispose() {
        attachView = false;
        shutdown();
    }

    private void post(Runnable runnable) {
        if (attachView) {
            platform.defaultCallbackExecutor().execute(runnable);
        }
    }

    //重试次数过多可能产生异常
    private void saveFile(final Subscriber<File> subscriber) {
        ArrayList<Chapter> chapters = downloadEngine.download(subscriber);
        try {
            final File finalResultFile = parser.save(chapters, book, savePath);
            post(new Runnable() {
                @Override
                public void run() {
                    subscriber.onFinish(finalResultFile);
                }
            });
        } catch (final IOException e) {
            post(new Runnable() {
                @Override
                public void run() {
                    subscriber.onMessage("保存文件时发生错误");
                    subscriber.onError(e);
                }
            });
        }
    }

    private void shutdown() {
        if (downloadEngine != null) {
            downloadEngine.dispose();
        }
    }

//    private void addChapter(Chapter chapter) {
//        synchronized (bufferLock) {
//            chapters.add(chapter);
//        }
//    }

//    private int getBufferSize() {
////        synchronized (bufferLock) {
////            return chapters.size();
////        }
//        return chapters.size();
//    }
//
//    private void addQueue(Catalog catalog) {
//        synchronized (queueLock) {
//            catalogQueue.offer(catalog);
//            //唤醒线程，添加所有章节到下载队列
//            queueLock.notify();
//        }
//    }

    public DownloadObserver setThreadCount(int threadCount) {
        this.threadCount = threadCount;
        return this;
    }

    public DownloadObserver setSavePath(String savePath) {
        this.savePath = savePath;
        return this;
    }

    public DownloadObserver setType(Type type) {
        switch (type) {
            case TXT:
                parser = new TxtParser();
                break;
            case EPUB:
                parser = new EpubParser();
                break;
        }
        return this;
    }

    //用于扩充格式
    public DownloadObserver setStrategy(ParseStrategy parser) {
        this.parser = parser;
        return this;
    }
}
