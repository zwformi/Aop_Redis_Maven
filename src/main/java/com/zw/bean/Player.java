package com.zw.bean;

/**
 * Created by Administrator on 2017/5/18.
 */
public class Player {
    public String userName;
    public int age;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Player{" +
                "userName='" + userName + '\'' +
                ", age=" + age +
                '}';
    }
}
