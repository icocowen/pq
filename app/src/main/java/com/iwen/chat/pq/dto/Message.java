package com.iwen.chat.pq.dto;

import java.io.Serializable;

public class Message implements Serializable {
    //targetId可以使组id或者是用户id
    private int targetId;
    private int fromUserId;
    private long sendTime;
    private String contentText;


    public Message() {
    }

    public Message(int targetId, int fromUserId, long sendTime, String contentText) {
        this.targetId = targetId;
        this.fromUserId = fromUserId;
        this.sendTime = sendTime;
        this.contentText = contentText;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public int getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(int fromUserId) {
        this.fromUserId = fromUserId;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }
}
