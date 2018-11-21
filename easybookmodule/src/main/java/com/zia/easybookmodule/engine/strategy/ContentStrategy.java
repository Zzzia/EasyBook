package com.zia.easybookmodule.engine.strategy;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Chapter;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;

import java.io.*;
import java.util.List;

/**
 * Created by zia on 2018/11/19.
 * 文本格式策略，用来自定义小说空行等格式
 * 继承该类可修改
 */
public class ContentStrategy {

    private static final String cssName = "bookCss";

    /**
     * 生成一章Epub格式的小说
     * 可通过继承{@link #getHtml(String, String)}修改html格式
     * 可通过继承{@link #getCss()}修改css样式
     * @param chapter 章节，包含章节名(chapterName)和章节内容(contents)
     * @return 章节完整Html
     */
    public String parseEpubContent(Chapter chapter) {
        StringBuilder content = new StringBuilder();
        for (String line : chapter.getContents()) {
            if (!line.isEmpty()) {
                content.append("<p>");
                content.append("    ");
                content.append(line);
                content.append("</p>");
            }
        }
        return getHtml(chapter.getChapterName(), content.toString());
    }

    public String parseTxtContent(Chapter chapter) {
        StringBuilder sb = new StringBuilder();
        sb.append(chapter.getChapterName());
        sb.append("\n");
        for (String line : chapter.getContents()) {
            //4个空格+正文+换行
            sb.append("        ");
            sb.append(line);
            sb.append("\n");
        }
        //章节结束空一行，用来分割下一章节
        sb.append("\n");
        return sb.toString();
    }

    final public File saveEpub(List<Chapter> chapters, Book book, String savePath) throws IOException {
        String bookName = book.getBookName() + "-" + book.getSiteName();
        File file = new File(savePath + File.separator + bookName + ".epub");
        nl.siegmann.epublib.domain.Book epub = new nl.siegmann.epublib.domain.Book();
        epub.getResources().add(new Resource(getCss().getBytes(), cssName + ".css"));
        Metadata metadata = epub.getMetadata();
        metadata.addTitle(bookName);
        metadata.addAuthor(new Author(book.getAuthor()));
        for (Chapter chapter : chapters) {
            epub.addSection(chapter.getChapterName(),
                    new Resource(parseEpubContent(chapter).getBytes(), chapter.getChapterName() + ".html"));
        }
        EpubWriter epubWriter = new EpubWriter();
        epubWriter.write(epub, new FileOutputStream(file));
        return file;
    }

    final public File saveTxt(List<Chapter> chapters, Book book, String savePath) throws IOException {
        String bookName = book.getBookName() + "-" + book.getSiteName();
        savePath += File.separator + bookName + ".txt";
        File file = new File(savePath);
        BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file)));
        for (Chapter chapter : chapters) {
            bufferedWriter.write(parseTxtContent(chapter));
        }
        return file;
    }

    protected String getHtml(String title, String content) {
        return "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"zh-CN\">\n<head>\n\t<title>" +
                title +
                "</title>\n\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n\t<link rel=\"stylesheet\" type=\"text/css\" href=\"../" +
                cssName +
                ".css\" />\n</head>\n<body>\n<h2><span style=\"border-bottom:1px solid\">" +
                title +
                "</span></h2>\n<div>\n" +
                content +
                "\n</div>\n</body>\n</html>\n";
    }

    protected String getCss() {
        return "body{\n" +
                " margin:10px;\n" +
                " font-size: 1.0em;word-wrap:break-word;\n" +
                "}\n" +
                "ul,li{list-style-type:none;margin:0;padding:0;}\n" +
                "p{text-indent:2em; line-height:1.5em; margin-top:0; margin-bottom:1.5em;}\n" +
                ".catalog{padding: 1.5em 0;font-size: 0.8em;}\n" +
                "li{border-bottom: 1px solid #D5D5D5;}\n" +
                "h1{font-size:1.6em; font-weight:bold;}\n" +
                "h2 {\n" +
                "    display: block;\n" +
                "    font-size: 1.2em;\n" +
                "    font-weight: bold;\n" +
                "    margin-bottom: 0.83em;\n" +
                "    margin-left: 0;\n" +
                "    margin-right: 0;\n" +
                "    margin-top: 1em;\n" +
                "}\n" +
                ".mbppagebreak {\n" +
                "    display: block;\n" +
                "    margin-bottom: 0;\n" +
                "    margin-left: 0;\n" +
                "    margin-right: 0;\n" +
                "    margin-top: 0 }\n" +
                "a {\n" +
                "    color: inherit;\n" +
                "    text-decoration: none;\n" +
                "    cursor: default\n" +
                "    }\n" +
                "a[href] {\n" +
                "    color: blue;\n" +
                "    text-decoration: none;\n" +
                "    cursor: pointer\n" +
                "    }\n" +
                "\n" +
                ".italic {\n" +
                "    font-style: italic\n" +
                "    }\n";
    }
}
