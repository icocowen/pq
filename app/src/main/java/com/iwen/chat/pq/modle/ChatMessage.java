package com.iwen.chat.pq.modle;

public class ChatMessage {
    private String content;
    private String time;
    private int isMeSend;//0是对方发送 1是自己发送
    private String nickName;


    public ChatMessage() {
    }

    public ChatMessage(String content, String time, int isMeSend , String nickName) {
        this.content = content;
        this.time = time;
        this.isMeSend = isMeSend;
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getIsMeSend() {
        return isMeSend;
    }

    public void setIsMeSend(int isMeSend) {
        this.isMeSend = isMeSend;
    }

}
