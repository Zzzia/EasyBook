package com.zia.easybookmodule.util;

import com.zia.easybookmodule.bean.rank.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zia on 2019/4/16.
 */
public class RankUtil {

    private volatile static List<RankInfo> rankClassifies = null;
    private static final String HTTPS = "https:";

    public static String getUrl(RankInfo rankInfo) {
        return rankInfo.getUrl() + "?style=" + rankInfo.getStyle() + "&chn=" + rankInfo.getChn()
                + "&page=" + rankInfo.getPage() + "&dateType=" + rankInfo.getDateType();
    }

    //获取各个排行榜，这玩意可以复用
    public static List<RankInfo> getRankInfoList(Document document) throws Exception {
        if (rankClassifies != null) {
            return rankClassifies;
        }
        synchronized (RankUtil.class) {
            if (rankClassifies != null) {
                return rankClassifies;
            }
            Elements lis = document.getElementsByClass("list_type_detective").first().getElementsByTag("li");
            rankClassifies = new ArrayList<>();
            for (Element li : lis) {
                Element a = li.getElementsByTag("a").first();
                String href = HTTPS + a.attr("href");
                String data_eid = a.attr("data-eid");
                String rankName = a.text();
                RankInfo rankInfo = new RankInfo(href, rankName, data_eid);
                rankClassifies.add(rankInfo);
            }
            return rankClassifies;
        }
    }

    public static List<RankClassify> getRankClassifyList(Document document) throws Exception {
        Elements as = document.getElementsByClass("type-list").first().getElementsByTag("a");
        List<RankClassify> results = new ArrayList<>();
        for (Element a : as) {
            String chanid = a.attr("data-chanid");
            String eid = a.attr("data-eid");
            String typeName = a.text();
            results.add(new RankClassify(typeName, chanid, eid));
        }
        return results;
    }

    public static String getHottestUpdateTime(Document document) {
        try {
            return document.getElementsByClass("rank-header").first().getElementsByTag("em").first().text();
        } catch (Exception e) {
            return "";
        }
    }

    public static List<HottestRankClassify> getHottestRankClassifyList(Document document) throws Exception {
        Elements ranks = document.getElementsByClass("rank-list");
        List<HottestRankClassify> result = new ArrayList<>();
        for (Element rank : ranks) {
            String rankName = rank.getElementsByClass("wrap-title").first().ownText();
            Element bookListDiv = rank.getElementsByClass("book-list").first();
            Elements lis = bookListDiv.getElementsByTag("li");
            List<RankBook> rankBookList = new ArrayList<>(10);
            for (int i = 0; i < lis.size(); i++) {
                Element li = lis.get(i);
                //榜首单独解析
                if (i == 0) {
                    Element info = li.getElementsByClass("book-info").first();
                    Element a1 = info.getElementsByTag("a").first();
                    String bkUrl = HTTPS + a1.attr("href");
                    String eid = a1.attr("data-eid");
                    String bid = a1.attr("data-bid");
                    String bkName = a1.text();
                    String viewInfo = info.getElementsByTag("p").first().text();
                    Element a2 = info.getElementsByTag("a").get(1);
                    String classify = a2.text();
                    String classifyUrl = HTTPS + a2.attr("href");
                    Element a3 = info.getElementsByTag("a").get(2);
                    String author = a3.text();
                    String authorUrl = HTTPS + a3.attr("href");
                    String imgUrl = HTTPS + li.getElementsByTag("img").first().attr("src");
                    RankBook topBook = new RankBook(bkName, eid, bid, bkUrl, imgUrl, author, authorUrl, classify, classifyUrl,
                            "", "", "", "", i + 1, viewInfo);
                    rankBookList.add(topBook);
                } else {
                    Element box = li.getElementsByClass("name-box").first();
                    Element a = box.getElementsByTag("a").first();
                    String bkUrl = HTTPS + a.attr("href");
                    String eid = a.attr("data-eid");
                    String bid = a.attr("data-bid");
                    String bkName = a.text();
                    String viewInfo = "";
                    Element infoTag = box.getElementsByTag("i").first();
                    if (infoTag != null) {
                        viewInfo = infoTag.text();
//                        if (isNumeric(viewInfo)) {
//                            viewInfo = viewInfo + "月票";
//                        }
                    }
                    RankBook rankBook = new RankBook(bkName, eid, bid, bkUrl, "", "", "",
                            "", "", "", "", "", "", i + 1, viewInfo);
                    rankBookList.add(rankBook);
                }
            }
            result.add(new HottestRankClassify(rankName, rankBookList));
        }
        return result;
    }

    public static List<RankBook> getRankBookList(Document document) throws Exception {
        Elements lis = document.getElementById("rank-view-list").getElementsByTag("li");
        List<RankBook> result = new ArrayList<>(20);
        for (Element li : lis) {
            Element imgBox = li.getElementsByClass("book-img-box").first();
            String rankNum = imgBox.getElementsByTag("span").text();
            rankNum = rankNum.replaceAll("\"", "");
            int r = 0;
            try {
                r = Integer.parseInt(rankNum);
            } catch (Exception ignore) {
            }
            Element aImg = imgBox.getElementsByTag("a").first();
            String bkUrl = HTTPS + aImg.attr("href");
            String eid = aImg.attr("data-eid");
            String bid = aImg.attr("data-bid");
            String imgUrl = HTTPS + aImg.getElementsByTag("img").first().attr("src");
            Element infoDiv = li.getElementsByClass("book-mid-info").first();
            String bkName = infoDiv.getElementsByTag("a").first().text();
            Elements ps = infoDiv.getElementsByTag("p");
            String authorName = ps.get(0).getElementsByTag("a").get(0).text();
            String authorUrl = HTTPS + ps.get(0).getElementsByTag("a").get(0).attr("href");
            String classify = ps.get(0).getElementsByTag("a").get(1).text();
            String classifyUrl = HTTPS + ps.get(0).getElementsByTag("a").get(1).attr("href");
            String status = ps.get(0).getElementsByTag("span").first().text();
            String intro = ps.get(1).text();
            String lastChapter = ps.get(2).getElementsByTag("a").first().text();
            lastChapter = lastChapter.replaceAll("最新更新 |最新更新", "");
            String lastUpdateTime = ps.get(2).getElementsByTag("span").text();
            RankBook rankBook = new RankBook(bkName, eid, bid, bkUrl, imgUrl, authorName, authorUrl, classify, classifyUrl
                    , status, intro, lastChapter, lastUpdateTime, r, "");
            result.add(rankBook);
        }
        return result;
    }

    public static int getMaxPageSize(Document document) {
        try {
            String max = document.getElementById("page-container").attr("data-pagemax");
            return Integer.parseInt(max);
        } catch (Exception e) {
            return 5;
        }
    }

    public static int getCurrentPage(Document document) {
        try {
            String max = document.getElementById("page-container").attr("data-page");
            return Integer.parseInt(max);
        } catch (Exception e) {
            return 1;
        }
    }

    private static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
