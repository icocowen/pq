package com.iwen.chat.pq.dto;

import java.io.Serializable;

public class Friend implements Serializable {
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    private String nickName;
    private String id;
    private String email;

    public Friend() { }

    public Friend(String nickName, String id, String email) {
        this.nickName = nickName;
        this.id = id;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
