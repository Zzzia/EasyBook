package com.zia.easybookmodule.engine.strategy;

import androidx.annotation.Nullable;

import com.zia.easybookmodule.bean.Book;
import com.zia.easybookmodule.bean.Chapter;
import com.zia.easybookmodule.net.NetUtil;
import com.zia.easybookmodule.util.TextUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zia on 2019/4/6.
 */
public class EpubParser implements ParseStrategy {

    private static final String cssName = "bookCss";
    private static final String TAG = "EpubParser";

    /**
     * 生成一章Epub格式的小说
     * 可通过继承{@link #getHtml(String, String)}修改html格式
     * 可通过继承{@link #getCss()}修改css样式
     *
     * @param chapter 章节，包含章节名(chapterName)和章节内容(contents)
     * @return 章节完整Html
     */
    @Override
    public String parseContent(Chapter chapter) {
        return parseContent(chapter.getChapterName(), chapter.getContents());
    }

    @Override
    public String parseContent(@Nullable String chapterName, List<String> contents) {
        StringBuilder content = new StringBuilder();
        for (String line : contents) {
            line = TextUtil.removeSpaceStart(line);
            if (!line.isEmpty()) {
                content.append("<p>");
//                content.append("    ");
                content.append(line);
                content.append("</p>");
                if (!line.endsWith("\n")) {
                    content.append("\n");
                }
            }
        }
        return getHtml(chapterName, content.toString());
    }

    public static String encode = "UTF-8";

    @Override
    public File save(List<Chapter> chapters, Book book, String savePath) throws IOException {
        String bookName = book.getBookName() + "-" + book.getSiteName();

        nl.siegmann.epublib.domain.Book epub = new nl.siegmann.epublib.domain.Book();
        //添加css
        epub.getResources().add(new Resource(getCss().getBytes(), cssName + ".css"));

        //添加书籍信息
        Metadata metadata = epub.getMetadata();
        metadata.addTitle(bookName);
        metadata.addAuthor(new Author(book.getAuthor()));

        //下载封面，懒癌，不复用之前获得的了
        if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
            Request request = new Request.Builder()
                    .get()
                    .url(book.getImageUrl())
                    .build();
            Response response = NetUtil.okHttpClient.newCall(request).execute();
            String end = book.getImageUrl().substring(book.getImageUrl().lastIndexOf('.'));
            if (end.length() > 5) {
                end = ".jpg";
            }
            if (response.body() != null) {
                epub.setCoverImage(new Resource(response.body().bytes(), "cover" + end));
            }
        }

        for (int i = 0; i < chapters.size(); i++) {
            Chapter chapter = chapters.get(i);
            String content = parseContent(chapter);
//            if (i < 3) {
//                Log.d(TAG, i + ":\n" + content);
//            }
            epub.addSection(chapter.getChapterName(),
                    new Resource(content.getBytes(encode), i + ".xhtml"));
        }
        EpubWriter epubWriter = new EpubWriter();
        File file = new File(savePath + File.separator + bookName + ".epub");
        epubWriter.write(epub, new FileOutputStream(file));
        return file;
    }

    /*
<html>

<head>
    <title>title</title>
    <meta charset="gbk">
    <link rel="stylesheet" type="text/css" href="easybook.css" />
</head>

<body>
    <h2>
        <span style="border-bottom:1px solid">
            title
        </span>
    </h2>
    <div>
        content
    </div>
</body>

</html>
     */
    protected String getHtml(String title, String content) {
        String html = "<?xml version=\"1.0\" encoding=\"" + encode + "\"?>\n" +
                "<!DOCTYPE html PUBLIC \"//W3C//DTD XHTML 1.0 Transitional//EN\"\n" +
                "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head>\n" +
                "    <title>" + title + "</title>\n" +
//                "    <meta charset=\"utf-8\">\n" +
                "    <link rel=\"stylesheet\" type=\"text/css\" href=\"" + cssName + ".css" + "\" />\n" +
                "</head>\n" +
                "\n" +
                "<body>\n";
        if (title != null && title.length() != 0) {
            html += "    <h2>\n" +
                    "        <span style=\"border-bottom:1px solid\">\n" +
                    "            " + title + "\n" +
                    "        </span>\n" +
                    "    </h2>\n";
        }
        html += "    <div>\n" +
                "        " + content + "\n" +
                "    </div>\n" +
                "</body>\n" +
                "\n" +
                "</html>";
//        return "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"zh-CN\">\n<head>\n\t<title>" +
//                title +
//                "</title>\n\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n\t<link rel=\"stylesheet\" type=\"text/css\" href=\"../" +
//                cssName +
//                ".css\" />\n</head>\n<body>\n<h2><span style=\"border-bottom:1px solid\">" +
//                title +
//                "</span></h2>\n<div>\n" +
//                content +
//                "\n</div>\n</body>\n</html>\n";
        return html;
    }


    /*
body {
    margin: 10px;
    font-size: 1.0em;
    word-wrap: break-word;
}

ul {
    list-style-type: none;
    margin: 0;
    padding: 0;
}

p {
    text-indent: 2em;
    line-height: 1.5em;
    margin-top: 0;
    margin-bottom: 1.5em;
}

.catalog {
    padding: 1.5em 0;
    font-size: 0.8em;
}

li {
    border-bottom: 1px solid #D5D5D5;
}

h1 {
    font-size: 1.6em;
    font-weight: bold;
}

h2 {
    display: block;
    font-size: 1.2em;
    font-weight: bold;
    margin-bottom: 0.83em;
    margin-left: 0;
    margin-right: 0;
    margin-top: 1em;

}

.mbppagebreak {
    display: block;
    margin-bottom: 0;
    margin-left: 0;
    margin-right: 0;
    margin-top: 0
}

a {
    color: inherit;
    text-decoration: none;
    cursor: default
}

a[href] {
    color: blue;
    text-decoration: none;
    cursor: pointer
}

.italic {
    font-style: italic
}
     */
    protected String getCss() {
        return "body {\n" +
                "    margin: 10px;\n" +
                "    font-size: 1.0em;\n" +
                "    word-wrap: break-word;\n" +
                "}\n" +
                "\n" +
                "ul {\n" +
                "    list-style-type: none;\n" +
                "    margin: 0;\n" +
                "    padding: 0;\n" +
                "}\n" +
                "\n" +
                "p {\n" +
                "    text-indent: 2em;\n" +
                "    line-height: 1.5em;\n" +
                "    margin-top: 0;\n" +
                "    margin-bottom: 1em;\n" +
                "}\n" +
                "\n" +
                ".catalog {\n" +
                "    padding: 1.5em 0;\n" +
                "    font-size: 0.8em;\n" +
                "}\n" +
                "\n" +
                "li {\n" +
                "    border-bottom: 1px solid #D5D5D5;\n" +
                "}\n" +
                "\n" +
                "h1 {\n" +
                "    font-size: 1.6em;\n" +
                "    font-weight: bold;\n" +
                "}\n" +
                "\n" +
                "h2 {\n" +
                "    display: block;\n" +
                "    font-size: 1.2em;\n" +
                "    font-weight: bold;\n" +
                "    margin-bottom: 0.83em;\n" +
                "    margin-left: 0;\n" +
                "    margin-right: 0;\n" +
//                "    margin-top: 1em;\n" +
                "    margin-top: 0;\n" +
                "\n" +
                "}\n" +
                "\n" +
                ".mbppagebreak {\n" +
                "    display: block;\n" +
                "    margin-bottom: 0;\n" +
                "    margin-left: 0;\n" +
                "    margin-right: 0;\n" +
                "    margin-top: 0\n" +
                "}\n" +
                "\n" +
                "a {\n" +
                "    color: inherit;\n" +
                "    text-decoration: none;\n" +
                "    cursor: default\n" +
                "}\n" +
                "\n" +
                "a[href] {\n" +
                "    color: blue;\n" +
                "    text-decoration: none;\n" +
                "    cursor: pointer\n" +
                "}\n" +
                "\n" +
                ".italic {\n" +
                "    font-style: italic\n" +
                "}";
    }
}
