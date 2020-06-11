package com.iwen.chat.pq.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

class PQDatabaseHelper extends SQLiteOpenHelper {


    public PQDatabaseHelper(@Nullable Context context) {
        super(context, "pq.db", null, 2);
    }

    /*
            {
                "nickName": "烽火1",
                "id": "2",
                "email": "123@qq.com"
            }

     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table friends(" +
                "id int primary key" +
                ",owner int" +
                ",nickName varchar(20)" +
                ",email varchar(100))";
        //输出创建数据库的日志信息
        Log.i(PQDatabaseHelper.class.getName(), "create Database---------friends---->");
        //execSQL函数用于执行SQL语句
        db.execSQL(sql);
        sql = "CREATE INDEX owner_id ON friends (owner)";
        db.execSQL(sql);


        //group 表
        sql = "create table groups(" +
                "id int primary key" +
                ",owner int" +
                ",groupName varchar(20)" +
                ",createTime INTEGER" +
                ",groupSize int)";
        //输出创建数据库的日志信息
        Log.i(PQDatabaseHelper.class.getName(), "create Database-------groups------>");
        //execSQL函数用于执行SQL语句
        db.execSQL(sql);
        sql = "CREATE INDEX groups_owner_id ON groups (owner)";
        db.execSQL(sql);

        //消息表

        sql = "create table messages(" +
                "targetId int ," +
                "fromUserId int ," +
                "owner int ," +
                "sendTime INTEGER ," +
                "contentText varchar(100)," +
                "primary key (targetId, sendTime))";
        db.execSQL(sql);

        sql = "CREATE INDEX fromUserId_id ON messages (fromUserId)";
        db.execSQL(sql);
        sql = "CREATE INDEX sendTime_id ON messages (sendTime)";
        db.execSQL(sql);
        sql = "CREATE INDEX message_owner_id ON messages (owner)";
        db.execSQL(sql);


//        sql = "create table stu_table(id int,sname varchar(20),sage int,ssex varchar(10))";
//        //输出创建数据库的日志信息
//        Log.i(PQDatabaseHelper.class.getName(), "create Database------------->");
//        //execSQL函数用于执行SQL语句
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
