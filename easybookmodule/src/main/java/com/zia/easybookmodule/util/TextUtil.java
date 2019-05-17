package com.zia.easybookmodule.util;

/**
 * Created By zia on 2018/10/30.
 */
public class TextUtil {
    public static String cleanContent(String content) {
        content = removeSpaceStart(content);
        return content.replaceAll("\n|\t|\r|&nbsp;|<br>|<br/>|<br />|p&gt;|&gt;|&hellip;", "").trim();
    }

    //删除最开始的空格
    public static String removeSpaceStart(String content) {
        int i = 0;
        for (; i < content.length(); ) {
            char c = content.charAt(i);
            //如果是全角空格或半角空格
            if (c == 12288 || c == 32 || c == '\uFEFF') {
                i++;
            } else {
                break;
            }
        }
        return content.substring(i);
    }
}
