package com.fengyang.myproject.model;

/**
 * Created by wuhuihui on 2017/4/28.
 */
public class User {

    private String name;
    private String age;
    private String time;
    private String jobdesc;
    private String message;

    public User(String name, String age, String time, String jobdesc, String message) {
        this.name = name;
        this.age = age;
        this.time = time;
        this.jobdesc = jobdesc;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getJobdesc() {
        return jobdesc;
    }

    public void setJobdesc(String jobdesc) {
        this.jobdesc = jobdesc;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + 
                ", age='" + age + 
                ", time='" + time + 
                ", jobdesc='" + jobdesc + 
                ", message='" + message + 
                '}';
    }
}
