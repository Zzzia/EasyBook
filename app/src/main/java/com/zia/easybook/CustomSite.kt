package com.zia.easybook

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zia.easybookmodule.bean.rule.XpathSiteRule
import com.zia.easybookmodule.engine.Site
import com.zia.easybookmodule.engine.SiteCollection
import com.zia.easybookmodule.site.CustomXpathSite

/**
 * Created by zia on 2019-06-02.
 */
class CustomSite {
    companion object {
        fun addCustomSite() {
            val json = getJson()
            val rules = Gson().fromJson<List<XpathSiteRule>>(
                json, TypeToken.getParameterized(List::class.java, XpathSiteRule::class.java).type
            )
            val sites = ArrayList<Site>()
            rules.forEach {
                sites.add(CustomXpathSite(it))
            }
            SiteCollection.getInstance().addSites(sites)
        }


        private fun getJson(): String {
            return """[{
    "siteName": "官术网",
    "baseUrl": "https://www.biyuwu.cc/",
    "enable": true,
    "siteClassify": "普通",
    "ruleUpdateTime": "1580548851000",
    "author": "zzzia",
    "searchUrl": "https://www.biyuwu.cc/search.php",
    "searchMethod": "GET",
    "searchParam": "q={keyword}",
    "searchEncode": "UTF-8",
    "searchBookList": "/html/body/div[3]/div",
    "searchBookName": ".//div[2]/h3/a/@title",
    "searchBookUrl": ".//div[2]/h3/a/@href",
    "searchAuthor": ".//div[2]/div/p[1]/span[2]/text()",
    "searchExtraRule": {
      "imageUrl": ".//div[1]/a/img/@src",
      "bookSize": "",
      "lastUpdateTime": ".//div[2]/div/p[3]/span[2]/text()",
      "lastChapterName": ".//div[2]/div/p[4]/a/text()",
      "introduce": ".//div[2]/p/text()",
      "classify": ".//div[2]/div/p[2]/span[2]/text()",
      "status": ""
    },
    "catalogChapterList": "//*[@id='list']/dl/dd",
    "catalogChapterName": ".//a/text()",
    "catalogChapterUrl": ".//a/@href",
    "catalogExtraRule": {
      "imageUrl": "//*[@id='fmimg']/img/@src",
      "bookSize": "",
      "lastUpdateTime": "//*[@id='info']/p[3]/text()",
      "lastChapterName": "//*[@id='info']/p[4]/a/text()",
      "introduce": "//*[@id='intro']/p[1]/text()",
      "classify": "",
      "status": "//*[@id='info']/p[2]/text()[1]"
    },
    "chapterLines": "//*[@id='content']/text()",
    "chapterEncodeType": "UTF-8",
    "cleaner": "官术网|biyuwu.cc"
  }]"""
        }
    }
}
