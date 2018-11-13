package com.zia.easybookmodule.util;

/**
 * Created By zia on 2018/10/30.
 */
public class TextUtil {
    public static String cleanContent(String content) {
        return content.replaceAll("\n|\t|\r|&nbsp;|<br>|<br/>|<br />|p&gt;|&gt;|&hellip;", "").trim();
    }
}
