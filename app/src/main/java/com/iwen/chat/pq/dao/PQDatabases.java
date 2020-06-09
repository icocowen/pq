package com.iwen.chat.pq.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iwen.chat.pq.dto.Friend;
import com.iwen.chat.pq.dto.Group;

import java.util.ArrayList;
import java.util.List;

public class PQDatabases {

    public static final String FRIENDS = "friends";
    public static final String ID = "id";
    public static final String NICK_NAME = "nickName";
    public static final String EMAIL = "email";
    public static final String OWNER = "owner";
    public static final String GROUPS = "groups";
    public static final String GROUP_NAME = "groupName";
    public static final String CREATE_TIME = "createTime";
    public static final String GROUP_SIZE = "groupSize";
    private final PQDatabaseHelper pqDatabaseHelper;

    public PQDatabases(Context context) {
        //创建数据库连接
        pqDatabaseHelper = new PQDatabaseHelper(context);
    }

//    "=======================friend========================="
    public void saveFriendsList(Object[] list, int owner) {

        for (int i = 0; i < list.length; i++) {
            Friend friend = (Friend)list[i];
            //先更新数据库，如果不存在，再去插入数据
            //先获得全部数据
            if (updateInfoFriend(friend, owner) != 1) {
                insertInfoFriends(friend, owner);
            }
        }

    }

    public void insertInfoFriends(Friend friend, int owner) {
        SQLiteDatabase writableDatabase = pqDatabaseHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(ID, Integer.valueOf(friend.getId()));
        cv.put(NICK_NAME, friend.getNickName());
        cv.put(EMAIL, friend.getEmail());
        cv.put(OWNER, owner);
        writableDatabase.insert(FRIENDS, null, cv);
    }


    public int updateInfoFriend(Friend friend, int owner) {
        SQLiteDatabase writableDatabase = pqDatabaseHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NICK_NAME, friend.getNickName());
        cv.put(EMAIL, friend.getEmail());
        return writableDatabase.update(FRIENDS,cv, ID + "=? and "+ OWNER +"=?", new String[]{friend.getId(), String.valueOf(owner)});
    }


    public void deleteInfoFriend(String id, String owner) {
        SQLiteDatabase writableDatabase = pqDatabaseHelper.getWritableDatabase();
        writableDatabase.delete(FRIENDS
                , ID+"=? and " + OWNER + "= ?"
                , new String[]{id, owner}
        );
    }


    public List<Friend> selectFriends(int owner) {
        SQLiteDatabase readableDatabase = pqDatabaseHelper.getReadableDatabase();
        Cursor cursor = readableDatabase.query(FRIENDS
                , new String[]{ID, NICK_NAME, EMAIL}
                , OWNER + "= ?"
                , new String[]{String.valueOf(owner)}
                , null, null, "id");

        List<Friend> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            Friend friend = new Friend(cursor.getString(1),cursor.getString(0),cursor.getString(2));
            list.add(friend);
        }

        return list;
    }

//    "=======================group========================="
    public void saveGroupsList(Object[] list, int owner) {

        for (int i = 0; i < list.length; i++) {
            Group group = (Group)list[i];
            //先更新数据库，如果不存在，再去插入数据
            if (updateInfoGroup(group, owner) != 1) {
                insertInfoGroups(group, owner);
            }
        }

    }


    public void insertInfoGroups(Group group, int owner) {
        SQLiteDatabase writableDatabase = pqDatabaseHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(ID, group.getId());
        cv.put(OWNER, owner);
        cv.put(GROUP_NAME, group.getGroupName());
        cv.put(CREATE_TIME, group.getCreateTime());
        cv.put(GROUP_SIZE, group.getGroupSize());
        writableDatabase.insert(GROUPS, null, cv);
    }


    public int updateInfoGroup(Group group, int owner) {
        SQLiteDatabase writableDatabase = pqDatabaseHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(GROUP_NAME, group.getGroupName());
        cv.put(GROUP_SIZE, group.getGroupSize());
        return writableDatabase.update(GROUPS,cv, ID + "=? and "+ OWNER +"=?", new String[]{String.valueOf(group.getId()), String.valueOf(owner)});
    }


    public void deleteInfoGroup(String id, String owner) {
        SQLiteDatabase writableDatabase = pqDatabaseHelper.getWritableDatabase();
        writableDatabase.delete(GROUPS
                , ID+"=? and " + OWNER + "= ?"
                , new String[]{id, owner}
        );
    }


    public List<Group> selectGroups(int owner) {
        SQLiteDatabase readableDatabase = pqDatabaseHelper.getReadableDatabase();
        Cursor cursor = readableDatabase.query(GROUPS
                , new String[]{ID, GROUP_NAME, CREATE_TIME, GROUP_SIZE}
                , OWNER + "= ?"
                , new String[]{String.valueOf(owner)}
                , null, null, "id");

        List<Group> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            Group group = new Group(
                    cursor.getString(1)
                    ,cursor.getInt(0)
                    ,cursor.getInt(3)
                    ,cursor.getInt(2)
                  );
            list.add(group);
        }

        return list;
    }


//    "=======================group========================="



//    "=======================friend========================="

//    public void selectInfoFriend(int id) {
//        SQLiteDatabase readableDatabase = pqDatabaseHelper.getReadableDatabase();
//        readableDatabase.
//    }



//    "=======================message========================="
//    public
//    "=======================message========================="







}
