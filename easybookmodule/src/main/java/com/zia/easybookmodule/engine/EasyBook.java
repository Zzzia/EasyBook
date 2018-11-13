package com.zia.easybookmodule.engine;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;
import com.zia.easybookmodule.net.NetUtil;
import com.zia.easybookmodule.rx.Disposable;
import com.zia.easybookmodule.rx.Observer;
import com.zia.easybookmodule.rx.Subscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zia on 2018/11/13.
 * Site包装类
 * search: 并发搜索 {@link #search(String bookName)}
 * getCatalog: 通过book获取目录 {@link #getCatalog(Book book)}
 * getContents: 通过book，catalog获取内容 {@link #getContent(Book book, Catalog catalog)}
 * <p>
 * 使用SiteCollection添加自定义解析站点: {@link com.zia.easybookmodule.engine.SiteCollection#addSite(Site)}
 */
public class EasyBook {

    private static Platform platform = Platform.get();

    private EasyBook() {
    }

    public static Observer<List<Book>> search(String bookName) {
        return search(bookName, SiteCollection.getInstance().getAllSites());
    }

    public static Observer<List<Book>> search(final String bookName, final List<Site> sites) {
        return new Observer<List<Book>>() {
            @Override
            public Disposable subscribe(final Subscriber<List<Book>> subscriber) {
                final ConcurrentLinkedQueue<List<Book>> bookListList = new ConcurrentLinkedQueue<>();
                final CountDownLatch countDownLatch = new CountDownLatch(sites.size());
                final ExecutorService service = Executors.newFixedThreadPool(sites.size());
                final Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //并发搜索
                        for (final Site site : sites) {
                            service.execute(new Runnable() {
                                @Override
                                public void run() {
                                    List<Book> results = null;
                                    platform.defaultCallbackExecutor().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            subscriber.onMessage("正在搜索" + site.getSiteName());
                                        }
                                    });
                                    try {
                                        results = site.search(bookName);
                                        for (Book book : results) {
                                            book.setBookName(book.getBookName().replaceAll("[《》]", ""));
                                        }
                                    } catch (final Exception e) {
                                        platform.defaultCallbackExecutor().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                subscriber.onMessage(e.toString());
                                            }
                                        });
                                    } finally {
                                        countDownLatch.countDown();
                                        platform.defaultCallbackExecutor().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                subscriber.onProgress(100 - (int) (countDownLatch.getCount() / (float) sites.size() * 100));
                                            }
                                        });
                                    }
                                    if (results == null) {
                                        platform.defaultCallbackExecutor().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                subscriber.onMessage(site.getSiteName() + "搜索结果错误，正在尝试其它网站");
                                            }
                                        });
                                        return;
                                    }
                                    bookListList.add(results);
                                }
                            });
                        }
                        try {
                            countDownLatch.await();
                        } catch (final InterruptedException e) {
                            platform.defaultCallbackExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    subscriber.onError(e);
                                }
                            });
                        } finally {
                            service.shutdown();
                        }
                        //获得所有结果，开始解析
                        int resultSize = 0;
                        for (List<Book> bookList : bookListList) {
                            resultSize += bookList.size();
                        }

                        if (resultSize == 0) {
                            platform.defaultCallbackExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    subscriber.onMessage("没有搜索到书籍");
                                }
                            });
                        }

                        //混合插入，让每个站点依次加入集合，有利于结果正确性
                        final ArrayList<Book> bookList = new ArrayList<>();
                        int index = 0;
                        while (bookList.size() < resultSize && bookList.size() <= 100) {
                            for (List<Book> bl : bookListList) {
                                if (index < bl.size()) {
                                    bookList.add(bl.get(index));
                                }
                            }
                            index++;
                        }
                        //微调排序，名字相同的在前，以搜索名开头的在前，长度相同的在前
                        Collections.sort(bookList, new Comparator<Book>() {
                            @Override
                            public int compare(Book o1, Book o2) {
                                //完全相同
                                if (o1.getBookName().equals(bookName) && !o2.getBookName().equals(bookName)) {
                                    return -1;
                                } else if (!o1.getBookName().equals(bookName) && o2.getBookName().equals(bookName)) {
                                    return 1;
                                }
                                //包含了字符
                                else if (o1.getBookName().contains(bookName) && !o2.getBookName().contains(bookName)) {
                                    return -1;
                                } else if (!o1.getBookName().contains(bookName) && o2.getBookName().contains(bookName)) {
                                    return 1;
                                } else if (o1.getBookName().contains(bookName) && o2.getBookName().contains(bookName)) {
                                    return o1.getBookName().indexOf(bookName) - o2.getBookName().indexOf(bookName);
                                }
                                //长度相同
                                else if (o1.getBookName().length() == bookName.length()
                                        && o2.getBookName().length() != bookName.length()) {
                                    return -1;
                                } else if (o1.getBookName().length() != bookName.length()
                                        && o2.getBookName().length() == bookName.length()) {
                                    return 1;
                                }
                                return 0;
                            }
                        });

                        //切换线程返回结果
                        platform.defaultCallbackExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                subscriber.onMessage("搜索到" + bookList.size() + "本相关书籍");
                                if (bookList.size() > 40) {
                                    subscriber.onFinish(new ArrayList<>(bookList.subList(0, 40)));
                                } else {
                                    subscriber.onFinish(bookList);
                                }
                            }
                        });
                    }
                });
                thread.start();
                return new Disposable() {
                    @Override
                    public void dispose() {
                        service.shutdown();
                        thread.interrupt();
                    }
                };
            }
        };
    }

    public static DownloadObserver download(Book book) {
        return new DownloadObserver(book);
    }

    public static Observer<List<Catalog>> getCatalog(final Book book) {
        return new Observer<List<Catalog>>() {
            @Override
            public Disposable subscribe(final Subscriber<List<Catalog>> subscriber) {
                final Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Site site = book.getSite();
                            String html = NetUtil.getHtml(book.getUrl(), site.getEncodeType());
                            final List<Catalog> list = site.parseCatalog(html, book.getUrl());
                            platform.defaultCallbackExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    subscriber.onFinish(list);
                                }
                            });
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                });
                thread.start();
                return new Disposable() {
                    @Override
                    public void dispose() {
                        thread.interrupt();
                    }
                };
            }
        };
    }

    public static Observer<List<String>> getContent(final Book book, final Catalog catalog) {
        return new Observer<List<String>>() {
            @Override
            public Disposable subscribe(final Subscriber<List<String>> subscriber) {
                final Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Site site = book.getSite();
                            String html = NetUtil.getHtml(catalog.getUrl(), site.getEncodeType());
                            final List<String> list = site.parseContent(html);
                            platform.defaultCallbackExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    subscriber.onFinish(list);
                                }
                            });
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                });
                thread.start();
                return new Disposable() {
                    @Override
                    public void dispose() {
                        thread.interrupt();
                    }
                };
            }
        };
    }
}
