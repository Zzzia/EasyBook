package com.zia.easybookmodule.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created By zia on 2018/10/30.
 */
public class Chapter implements Serializable {
    private String chapterName;//章节名字
    private int index;//章节在小说中的顺序，最后排序需要用到
    private List<String> contents;//章节内容 按行分

    public Chapter(String chapterName, int index, List<String> contents) {
        this.chapterName = chapterName;
        this.index = index;
        this.contents = contents;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "chapterName='" + chapterName + '\'' +
                ", index='" + index + '\'' +
                ", contents=" + contents +
                '}';
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<String> getContents() {
        return contents;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }
}
