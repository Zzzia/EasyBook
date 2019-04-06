package com.zia.easybookmodule.engine;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Catalog;

import java.util.List;

/**
 * Created by zia on 2018/11/12.
 * 网站解析基类，需要实现搜索，目录，文章内容的解析
 * 文章内容需要清理广告以及多余格式
 * 格式清理可以使用{@link com.zia.easybookmodule.util.TextUtil#cleanContent(String content)}
 *
 *
 * 很多同学喜欢使用在线正则来解析网站，实现实时更新的功能，也可以继承这个类来实现。
 *
 * 通过这个方法调整结果最大数量
 * @see com.zia.easybookmodule.engine.parser.SearchObserver#setMaxResult(int)
 *
 * 通过这个方法获取站点，删除原来的解析
 * @see SiteCollection#getAllSites()
 */
public abstract class Site {

    public abstract List<Book> search(String bookName) throws Exception;

    public abstract List<Catalog> parseCatalog(String catalogHtml, String rootUrl) throws Exception;

    public abstract List<String> parseContent(String chapterHtml) throws Exception;

    public abstract String getSiteName();

    public String getEncodeType() {
        return "gbk";
    }
}
