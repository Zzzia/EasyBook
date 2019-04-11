package com.zia.easybookmodule.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by zia on 2017/3/10.
 * 正则解析工具类
 */
public class RegexUtil {

    private final static int TYPE_INCLUDE = 1;
    private final static int TYPE_EXCEPT = 2;
    public static boolean openLog = false;

    //包含正则表达式
    private static String Include(String start, String end) {
        return start + "[\\s\\S]*?" + end;
    }

    //不包含正则表达式
    private static String Except(String start, String end) {
        return "(?<=" + start + ")[\\s\\S]*?(?=" + end + ")";
    }

    //解析逻辑
    private static List<String> regex(String start, String end, String source, int type) {
        List<String> list = new ArrayList<>();
        String regEx;
        //设置解析方式
        if (type == TYPE_EXCEPT) {
            regEx = Except(start, end);
        } else regEx = Include(start, end);
        Pattern pattern = Pattern.compile(regEx);
        //将目标集合里的所有内容解析出来，放到这里的list里
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            if (matcher.group() != null) {
                list.add(matcher.group());
            }
        }
        return list;
    }

    public static List<String> regexInclude(String start, String end, String source) {
        if (source == null || source.length() == 0) return new ArrayList<>();
        return regex(start, end, source, TYPE_INCLUDE);
    }

    public static List<String> regexExcept(String start, String end, String source) {
        if (source == null || source.length() == 0) return new ArrayList<>();
        return regex(start, end, source, TYPE_EXCEPT);
    }

    public static List<Tag> getTags(String tagName, String source) {
        Matcher matcher = Pattern
                .compile("<" + tagName + ".*?>[\\s\\S]*?</" + tagName + ">|<" + tagName + "[\\s\\S]*?>")
                .matcher(source);
        List<Tag> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(new Tag(matcher.group()));
        }
        return list;
    }


    /**
     * 用于解析类似<a...>..</a>的工具
     * <p>
     * String html = "<a href=\"15818515.html\">第21章 我很大，活很好，很持久，还能旋转</a>";
     * util.RegexUtil.Tag tag = new util.RegexUtil.Tag(html);
     * //获取15818515.html
     * System.out.println(tag.getValue("href"));
     * //获取第21章 我很大，活很好，很持久，还能旋转
     * System.out.println(tag.getText());
     */
    public static class Tag {
        private String tagName;
        private String text;
        private String html;
        private String prefix;//前缀
        private String suffix;//后缀

        @Override
        public String toString() {
            return "html: " + getHtml() + "\n"
                    + "tagName: " + getTagName() + "\n"
                    + "text: " + getText() + "\n"
                    + "prefix: " + getPrefix() + "\n"
                    + "suffix: " + getSuffix() + "\n";
        }

        public Tag(String html) {
            this.html = html;
        }

        public String getTagName() {
            if (tagName == null)
                setTagName();
            return tagName;
        }

        private void setTagName() {
            Matcher matcher = Pattern.compile("<.*?/(.*?)>").matcher(getSuffix());
            if (matcher.find())
                tagName = matcher.group(1);
            if (tagName == null) tagName = "";
        }

        public String getText() {
            if (text == null)
                setText();
            return text;
        }

        private void setText() {
            Matcher matcher = Pattern.compile("<.*?>([\\s\\S]*)<.*?>").matcher(getHtml());
            if (matcher.find())
                text = matcher.group(1);
            if (text == null) text = "";
        }

        public String getHtml() {
            return html;
        }

        public String getPrefix() {
            if (prefix == null)
                setSPfix();
            return prefix;
        }

        //设置前后缀
        private void setSPfix() {
            Matcher matcher = Pattern.compile("<.*?>").matcher(html);
            if (matcher.find()) {
                prefix = matcher.group();
            }
            while (matcher.find()) {
                suffix = matcher.group();
            }

            if (prefix == null) prefix = "";
            if (suffix == null) suffix = "";
        }

        public String getSuffix() {
            if (suffix == null)
                setSPfix();
            return suffix;
        }

        //通过关键字获取属性
        public String getValue(String key) {
            Matcher matcher = Pattern.compile(key + " *= *'(.*?)'|" + key + " *= *\"(.*?)\"").matcher(getPrefix());
            while (matcher.find()) {
                if (matcher.group(1) != null)
                    return matcher.group(1);
                if (matcher.group(2) != null)
                    return matcher.group(2);
            }
            return "";
        }
    }

}



