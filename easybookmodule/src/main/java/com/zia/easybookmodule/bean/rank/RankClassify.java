package com.zia.easybookmodule.bean.rank;

import java.io.Serializable;

/**
 * Created by zia on 2019/4/16.
 * 排行分类，如玄幻，奇幻，武侠等
 */
public class RankClassify implements Serializable {
    private String typeName;
    private String data_chanid;
    private String data_eid;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getData_chanid() {
        return data_chanid;
    }

    public void setData_chanid(String data_chanid) {
        this.data_chanid = data_chanid;
    }

    public String getData_eid() {
        return data_eid;
    }

    public void setData_eid(String data_eid) {
        this.data_eid = data_eid;
    }

    public RankClassify(String typeName, String data_chanid, String data_eid) {
        this.typeName = typeName;
        this.data_chanid = data_chanid;
        this.data_eid = data_eid;
    }

    @Override
    public String toString() {
        return "RankClassify{" +
                "typeName='" + typeName + '\'' +
                ", data_chanid='" + data_chanid + '\'' +
                ", data_eid='" + data_eid + '\'' +
                '}';
    }
}
