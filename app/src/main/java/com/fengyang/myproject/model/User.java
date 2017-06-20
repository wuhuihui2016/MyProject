package com.fengyang.myproject.model;

/**
 * Created by wuhuihui on 2017/4/28.
 */
public class User {

    private String name;
    private String pwd;
    private String time;

    public User(String name, String pwd, String time) {
        this.name = name;
        this.pwd = pwd;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + 
                ", pwd='" + pwd + 
                ", time='" + time + 
                '}';
    }
}
