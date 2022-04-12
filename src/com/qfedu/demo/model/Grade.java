package com.qfedu.demo.model;

/**
 * model/entity po（persistent object） 这个类和数据库中的表是一一对应的
 * dto（data transfer object） 用来转换 model 和 vo
 * vo（view object） 这个是前端要展示的数据，这个和前端是一一对应的
 * bo （business object）
 */
public class Grade {
    private Integer gid;
    private String gradeName;

    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }
}
