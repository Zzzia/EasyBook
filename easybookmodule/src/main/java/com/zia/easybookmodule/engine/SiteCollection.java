package com.zia.easybookmodule.engine;

import com.zia.easybookmodule.site.Biduo;
import com.zia.easybookmodule.site.Binhuo;
import com.zia.easybookmodule.site.Biquge;
import com.zia.easybookmodule.site.BiqugeBiz;
import com.zia.easybookmodule.site.Daocaoren;
import com.zia.easybookmodule.site.Shunong;
import com.zia.easybookmodule.site.Wenxuemi;
import com.zia.easybookmodule.site.Zhuaji;
import com.zia.easybookmodule.site.Zhuishu;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

/**
 * Created By zia on 2018/10/30.
 * 单例模式，获取所有站点的类，可以添加自定义站点
 */
public class SiteCollection {

    private List<Site> sites = new ArrayList<>();

    /**
     * 默认添加这些站点解析，需要更改时对集合进行remove就行
     */
    private SiteCollection() {
        //normal
        sites.add(new Zhuishu());
        sites.add(new Biquge());
        sites.add(new BiqugeBiz());
        sites.add(new Zhuaji());
        sites.add(new Shunong());
        sites.add(new Biduo());
        sites.add(new Wenxuemi());

        //anim
        sites.add(new Binhuo());
        sites.add(new Daocaoren());

        //h
//        sites.add(new Shouji());
//        sites.add(new Jidian());
//        sites.add(new Zhai());
    }

    public void addSites(List<Site> list) {
        for (Site site : list) {
            addSite(site);
        }
    }

    public void addSite(Site site) {
        if (!sites.contains(site)) {
            sites.add(site);
        }
    }

    @Nullable
    public Site findSite(String siteName) {
        for (Site site : sites) {
            if (site.getSiteName().equals(siteName)) {
                return site;
            }
        }
        return null;
    }

    public List<Site> getAllSites() {
        return sites;
    }

    public static SiteCollection getInstance() {
        return Holder.INSTANCE;
    }

    public static class Holder {
        private static SiteCollection INSTANCE = new SiteCollection();
    }
}
