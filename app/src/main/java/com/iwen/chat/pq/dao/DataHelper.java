package com.iwen.chat.pq.dao;


import android.content.Context;
import android.content.SharedPreferences;

import com.iwen.chat.pq.dto.Self;

/**
 * 数据库相关操作类
 */
public class DataHelper {

    private static final String TOKEN_HEAD = "tokenHead";
    private static final String NICK_NAME = "nickName";
    private static final String ID = "id";
    private static final String USER = "user";
    private static final String TOKEN = "token";
    public static final String FRIENDS_UPDATE_TIME = "friendsUpdateTime";
    public static final String LAST_FRIENDS_UPDATE_TIME = "lastFriendsUpdateTime";
    public static final String LAST_GROUPS_UPDATE_TIME = "lastGroupsUpdateTime";

    private DataHelper() {}

    private static class DataHelperInner {
        static final DataHelper dataHelper = new DataHelper();
    }

    public static DataHelper getInstance() {
        return DataHelperInner.dataHelper;
    }

    private static final String SAVE_BASE_INFO = "SAVE_BASE_INFO";

    public void saveSelfInfo(Context context, Self self) {
        SharedPreferences preferences = context.getSharedPreferences(SAVE_BASE_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(TOKEN_HEAD, self.getTokenHead());
        edit.putString(NICK_NAME, self.getNickName());
        edit.putString(ID, self.getId());
        edit.putString(USER, self.getUser());
        edit.putString(TOKEN, self.getToken());
        edit.apply();
    }


    public void saveFriendsUpdateTime(Context context, long time) {
        SharedPreferences preferences = context.getSharedPreferences(SAVE_BASE_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putLong(FRIENDS_UPDATE_TIME, time);
        edit.apply();
    }

    public void saveGroupsUpdateTime(Context context, long time) {
        SharedPreferences preferences = context.getSharedPreferences(SAVE_BASE_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putLong(LAST_GROUPS_UPDATE_TIME, time);
        edit.apply();
    }

    public long getFriendsUpdateTime(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SAVE_BASE_INFO, Context.MODE_PRIVATE);
        return preferences.getLong(FRIENDS_UPDATE_TIME, 0);
    }

    public long getGroupsUpdateTime(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SAVE_BASE_INFO, Context.MODE_PRIVATE);
        return preferences.getLong(LAST_GROUPS_UPDATE_TIME, 0);
    }


    public Self getSelfInfo(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SAVE_BASE_INFO, Context.MODE_PRIVATE);
        Self self = new Self();
        self.setId(preferences.getString(ID, null));
        self.setNickName(preferences.getString(NICK_NAME, null));
        self.setTokenHead(preferences.getString(TOKEN_HEAD, null));
        self.setUser(preferences.getString(USER, null));
        self.setToken(preferences.getString(TOKEN, null));
        return self;
    }


    public void clearSelfInfo(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SAVE_BASE_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(ID, null);
        edit.putString(TOKEN, null);
        edit.putString(FRIENDS_UPDATE_TIME, null);
        edit.apply();
    }

}
