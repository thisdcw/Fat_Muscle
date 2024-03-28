package com.mxsella.fatmuscle.entity;

import java.io.Serializable;

public class ApiMemberInfo implements Serializable {
    private Integer age;
    private String avatar;
    private String birthday;
    private String height;
    private Integer id;
    private Integer sex;
    private String sexLable;
    private String userName;
    private String weight;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer num) {
        this.id = num;
    }

    public Integer getAge() {
        return this.age;
    }

    public void setAge(Integer num) {
        this.age = num;
    }

    public String getSexLable() {
        return this.sexLable;
    }

    public void setSexLable(String str) {
        this.sexLable = str;
    }

    public String toString() {
        return "ApiMemberInfo{id=" + this.id + ", sex=" + this.sex + ", sexLable='" + this.sexLable + "', weight='" + this.weight + "', height='" + this.height + "', birthday='" + this.birthday + "', userName='" + this.userName + "', avatar='" + this.avatar + "'}";
    }

    public Integer getSex() {
        return this.sex;
    }

    public void setSex(Integer num) {
        this.sex = num;
    }

    public String getWeight() {
        return this.weight;
    }

    public void setWeight(String str) {
        this.weight = str;
    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(String str) {
        this.height = str;
    }

    public String getBirthday() {
        return this.birthday;
    }

    public void setBirthday(String str) {
        this.birthday = str;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String str) {
        this.userName = str;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public void setAvatar(String str) {
        this.avatar = str;
    }

}
