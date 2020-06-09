package com.iwen.chat.pq.dto;

import java.io.Serializable;

public class Group implements Serializable {
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    private String groupName;
    private int id;
    private int groupSize;
    private long createTime;


    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Group() { }

    public Group(String groupName, int id, int groupSize, long createTime) {
        this.groupName = groupName;
        this.id = id;
        this.groupSize = groupSize;
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupSize() {
        return groupSize;
    }

    public void setGroupSize(int groupSize) {
        this.groupSize = groupSize;
    }
}
