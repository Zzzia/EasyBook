package com.zia.easybookmodule.engine;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;

import java.util.List;

/**
 * Created by zia on 2018/11/12.
 * 网站解析基类，需要实现搜索，目录，文章内容的解析
 * 文章内容需要清理广告以及多余格式
 * 格式清理可以使用{@link com.zia.easybookmodule.util.TextUtil#cleanContent(String content)}
 */
public abstract class Site {

    protected abstract List<Book> search(String bookName) throws Exception;

    protected abstract List<Catalog> parseCatalog(String catalogHtml, String url);

    protected abstract List<String> parseContent(String chapterHtml);


    public abstract String getSiteName();

    public String getEncodeType() {
        return "gbk";
    }
}
