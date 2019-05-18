package com.zia.easybookmodule.engine.strategy;

import androidx.annotation.Nullable;
import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Chapter;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by zia on 2019/4/6.
 */
public interface ParseStrategy {

    //生成文本
    String parseContent(Chapter chapter);

    String parseContent(@Nullable String chapterName, List<String> contents);

    //保存到指定目录
    File save(List<Chapter> chapters, Book book, String savePath) throws IOException;
}
