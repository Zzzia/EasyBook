package com.zia.easybookmodule.engine.parser;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.bean.Chapter;
import com.zia.easybookmodule.engine.Platform;
import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.net.NetUtil;
import com.zia.easybookmodule.rx.Disposable;
import com.zia.easybookmodule.rx.EmptySubscriber;
import com.zia.easybookmodule.rx.Subscriber;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zia on 2019-05-14.
 * 并发下载核心方法，返回一个有序的Chapter集合
 */
public class DownloadEngine implements Disposable {

    private int threadCount;
    private int from = -1, to = -1;
    private Book book;
    private Platform platform = Platform.get();

    private LinkedBlockingQueue<Chapter> chapters;
    private LinkedBlockingQueue<Catalog> catalogQueue;
    private ExecutorService threadPool;
    private Timer timer;

    volatile private boolean needFreshProcess = true;
    volatile private int tempProgress = 0;
    volatile private boolean attachView = true;

    public DownloadEngine(Book book, int threadCount, int from, int to) {
        this.threadCount = threadCount;
        this.from = from;
        this.to = to;
        this.book = book;
    }

    public DownloadEngine(Book book, int threadCount) {
        this.threadCount = threadCount;
        this.book = book;
    }

    /**
     * 下载时的异常会通过subscriber分发出来，如果不传入subscriber不能捕获异常
     *
     * @param subscriber
     * @return
     */
    public ArrayList<Chapter> download(Subscriber subscriber) {
        final Subscriber mSubscriber;
        if (subscriber == null) {
            mSubscriber = new EmptySubscriber();
        } else {
            mSubscriber = subscriber;
        }
        post(new Runnable() {
            @Override
            public void run() {
                mSubscriber.onProgress(0);
                mSubscriber.onMessage("正在解析目录...");
            }
        });

        final Site site = book.getSite();
        //从目录页获取有序章节
        String catalogHtml = null;
        try {
            catalogHtml = NetUtil.getHtml(book.getUrl(), site.getEncodeType());
        } catch (final IOException e) {
            post(new Runnable() {
                @Override
                public void run() {
                    mSubscriber.onMessage("获取目录页面失败");
                    mSubscriber.onError(e);
                }
            });
            return new ArrayList<>();
        }
        final List<Catalog> catalogs;
        try {
            List<Catalog> temp = site.parseCatalog(catalogHtml, book.getUrl());
            //添加序号
            for (int i = 0; i < temp.size(); i++) {
                temp.get(i).setIndex(i + 1);
            }
            if (to != -1 && from != -1) {
                catalogs = temp.subList(from, to);
            } else {
                catalogs = temp;
            }
        } catch (final Exception e) {
            post(new Runnable() {
                @Override
                public void run() {
                    mSubscriber.onMessage("网站目录结构更改，请联系作者修复");
                    mSubscriber.onError(e);
                }
            });
            return new ArrayList<>();
        }

        if (catalogs.size() == 0) {
            post(new Runnable() {
                @Override
                public void run() {
                    mSubscriber.onMessage("没有解析到目录...");
                    mSubscriber.onError(new IOException("没有解析到目录"));
                }
            });
//            System.err.println(catalogHtml);
            return new ArrayList<>();
        }

        chapters = new LinkedBlockingQueue<>(catalogs.size() + 1);
        //x2是为了防止扩容，应该不会全部都下载失败吧=_=
        catalogQueue = new LinkedBlockingQueue<>(catalogs.size() * 2);
        catalogQueue.addAll(catalogs);

        post(new Runnable() {
            @Override
            public void run() {
                mSubscriber.onMessage("一共" + catalogs.size() + "张，开始下载...");
            }
        });

        final int catalogSize = catalogs.size();
        final AtomicInteger leftBook = new AtomicInteger(catalogSize);
        final AtomicInteger errorBook = new AtomicInteger(catalogSize);

        timer = new Timer();//用来传递下载进度的计时器
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                needFreshProcess = true;
            }
        }, 0, 300);

        threadPool = Executors.newFixedThreadPool(threadCount);
        //自动探测是否全部下载
        while (chapters.size() < catalogs.size()) {
            Catalog catalog = catalogQueue.poll();
            if (catalog == null) {//如果队列为空，释放锁，等待唤醒或者超时后继续探测
                try {
                    //不用wait了，感觉会影响性能
                    Thread.sleep(500);
//                    queueLock.wait(1000);
                } catch (final InterruptedException e) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            mSubscriber.onMessage("下载时发生并发错误");
                            mSubscriber.onError(e);
                        }
                    });
                }
            } else {//队列有章节，下载所有
                while (catalog != null) {
                    final Catalog finalCatalog = catalog;
                    threadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String chapterHtml = NetUtil.getHtml(finalCatalog.getUrl(), site.getEncodeType());
                                List<String> contents = site.parseContent(chapterHtml);
                                if (needFreshProcess) {
                                    tempProgress = (int) (((errorBook.get() - leftBook.get()) / (float) (2 * catalogSize - errorBook.get())) * 100);
                                    needFreshProcess = false;
                                    post(new Runnable() {
                                        @Override
                                        public void run() {
                                            mSubscriber.onProgress(tempProgress);
                                        }
                                    });
                                }
                                post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mSubscriber.onMessage(finalCatalog.getChapterName());
                                    }
                                });
                                Chapter chapter = new Chapter(finalCatalog.getChapterName(), finalCatalog.getIndex(), contents);
                                leftBook.decrementAndGet();
                                chapters.add(chapter);
//                                addChapter(chapter);
                            } catch (final IOException e) {
                                post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mSubscriber.onMessage(e.getMessage() + "  重试章节 ： " + finalCatalog.getChapterName());
                                    }
                                });
                                if (needFreshProcess) {
                                    tempProgress = (int) (((errorBook.get() - leftBook.get()) / (float) (2 * catalogSize - errorBook.get())) * 100);
                                    needFreshProcess = false;
                                    post(new Runnable() {
                                        @Override
                                        public void run() {
                                            mSubscriber.onProgress(tempProgress);
                                        }
                                    });
                                }
                                errorBook.decrementAndGet();
                                catalogQueue.add(finalCatalog);//重新加入队列，等待下载
//                                    addQueue(finalCatalog);//重新加入队列，等待下载
                            } catch (Exception e) {
                                mSubscriber.onError(e);
                                dispose();
                            }
                        }
                    });
                    catalog = catalogQueue.poll();
                }
            }
        }
        threadPool.shutdown();
        timer.cancel();
        post(new Runnable() {
            @Override
            public void run() {
                mSubscriber.onMessage("下载完成(" + chapters.size() + "章)，等待保存");
                mSubscriber.onProgress(100);
            }
        });
        ArrayList<Chapter> newChapters = new ArrayList<>(chapters);
        Collections.sort(newChapters, new Comparator<Chapter>() {
            @Override
            public int compare(Chapter o1, Chapter o2) {
                return o1.getIndex() - o2.getIndex();
            }
        });
        return newChapters;
    }

    private void post(Runnable runnable) {
        if (attachView) {
            platform.defaultCallbackExecutor().execute(runnable);
        }
    }

    @Override
    public void dispose() {
        attachView = false;
        if (threadPool != null) {
            threadPool.shutdownNow();
        }
        if (timer != null) {
            timer.cancel();
        }
    }
}
