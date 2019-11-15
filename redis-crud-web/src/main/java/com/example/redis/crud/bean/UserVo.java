package com.example.redis.crud.bean;

import java.util.Date;

/**
 * @author jackie wang
 * @Title: UserVo
 * @ProjectName redis-spring-boot-starter-master
 * @Description: TODO
 * @date 2019/10/31 19:40
 */
public class UserVo {
    private Integer id;
    private String name;
    private Date date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
