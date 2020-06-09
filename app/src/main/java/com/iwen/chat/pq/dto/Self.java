package com.iwen.chat.pq.dto;

import java.io.Serializable;

public class Self implements Serializable {
    private String tokenHead;
    private String nickName;
    private String id;
    private String user;
    private String token;

    public Self() {}

    public Self(String tokenHead, String nickName, String id, String user, String token) {
        this.tokenHead = tokenHead;
        this.nickName = nickName;
        this.id = id;
        this.user = user;
        this.token = token;
    }

    public String getTokenHead() {
        return tokenHead;
    }

    public void setTokenHead(String tokenHead) {
        this.tokenHead = tokenHead;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Self{" +
                "tokenHead='" + tokenHead + '\'' +
                ", nickName='" + nickName + '\'' +
                ", id='" + id + '\'' +
                ", user='" + user + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
