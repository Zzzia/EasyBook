package com.zia.easybookmodule.engine.parser;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.bean.Chapter;
import com.zia.easybookmodule.bean.Type;
import com.zia.easybookmodule.engine.Platform;
import com.zia.easybookmodule.engine.Site;
import com.zia.easybookmodule.engine.strategy.ContentStrategy;
import com.zia.easybookmodule.net.NetUtil;
import com.zia.easybookmodule.rx.Disposable;
import com.zia.easybookmodule.rx.Observer;
import com.zia.easybookmodule.rx.Subscriber;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

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
    private Type type = Type.EPUB;
    private Book book;
    private Platform platform = Platform.get();
    private ContentStrategy strategy = new ContentStrategy();

    private LinkedBlockingQueue<Chapter> chapters;
    private LinkedBlockingQueue<Catalog> catalogQueue;
    //    private final Object bufferLock = new Object();
//    private final Object queueLock = new Object();
    private ExecutorService threadPool;
    private Timer timer;

    volatile private boolean needFreshProcess = true;
    volatile private int tempProgress = 0;
    volatile private boolean attachView = true;

    public DownloadObserver(Book book) {
        this.book = book;
    }

    @Override
    public Disposable subscribe(final Subscriber<File> subscriber) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(savePath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    concurrentDownload(subscriber);
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

    void concurrentDownload(final Subscriber<File> subscriber) {
        post(new Runnable() {
            @Override
            public void run() {
                subscriber.onProgress(0);
                subscriber.onMessage("正在解析目录...");
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
                    subscriber.onMessage("获取目录页面失败");
                    subscriber.onError(e);
                }
            });
            return;
        }
        final List<Catalog> catalogs;
        try {
            catalogs = site.parseCatalog(catalogHtml, book.getUrl());
            //添加序号
            for (int i = 0; i < catalogs.size(); i++) {
                catalogs.get(i).setIndex(i + 1);
            }
        } catch (final Exception e) {
            post(new Runnable() {
                @Override
                public void run() {
                    subscriber.onMessage("网站目录结构更改，请联系作者修复");
                    subscriber.onError(e);
                }
            });
            return;
        }

        if (catalogs.size() == 0) {
            post(new Runnable() {
                @Override
                public void run() {
                    subscriber.onMessage("没有解析到目录...");
                    subscriber.onError(new IOException("没有解析到目录"));
                }
            });
//            System.err.println(catalogHtml);
            return;
        }

        chapters = new LinkedBlockingQueue<>(catalogs.size() + 1);
        //x2是为了防止扩容，应该不会全部都下载失败吧=_=
        catalogQueue = new LinkedBlockingQueue<>(catalogs.size() * 2);
        catalogQueue.addAll(catalogs);

        post(new Runnable() {
            @Override
            public void run() {
                subscriber.onMessage("一共" + catalogs.size() + "张，开始下载...");
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
                            subscriber.onMessage("下载时发生并发错误");
                            subscriber.onError(e);
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
                                            subscriber.onProgress(tempProgress);
                                        }
                                    });
                                }
                                post(new Runnable() {
                                    @Override
                                    public void run() {
                                        subscriber.onMessage(finalCatalog.getChapterName());
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
                                        subscriber.onMessage(e.getMessage() + "  重试章节 ： " + finalCatalog.getChapterName());
                                    }
                                });
                                if (needFreshProcess) {
                                    tempProgress = (int) (((errorBook.get() - leftBook.get()) / (float) (2 * catalogSize - errorBook.get())) * 100);
                                    needFreshProcess = false;
                                    post(new Runnable() {
                                        @Override
                                        public void run() {
                                            subscriber.onProgress(tempProgress);
                                        }
                                    });
                                }
                                errorBook.decrementAndGet();
                                catalogQueue.add(finalCatalog);//重新加入队列，等待下载
//                                    addQueue(finalCatalog);//重新加入队列，等待下载
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
                subscriber.onMessage("下载完成(" + chapters.size() + "章)，等待保存");
                subscriber.onProgress(100);
            }
        });
        ArrayList<Chapter> newChapters = new ArrayList<>(chapters);
        Collections.sort(newChapters, new Comparator<Chapter>() {
            @Override
            public int compare(Chapter o1, Chapter o2) {
                return o1.getIndex() - o2.getIndex();
            }
        });
        try {
            File resultFile = null;
            switch (type) {
                case TXT:
                    resultFile = strategy.saveTxt(newChapters, book, savePath);
                    break;
                case EPUB:
                    resultFile = strategy.saveEpub(newChapters, book, savePath);
                    break;
            }
            final File finalResultFile = resultFile;
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
        if (threadPool != null) {
            threadPool.shutdownNow();
        }
        if (timer != null) {
            timer.cancel();
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
        this.type = type;
        return this;
    }

    public DownloadObserver setStrategy(ContentStrategy strategy) {
        this.strategy = strategy;
        return this;
    }
}
