package com.iwen.chat.pq.view;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.iwen.chat.pq.dao.DataHelper;
import com.iwen.chat.pq.dto.Self;
import com.iwen.chat.pq.http.UserManagement;
import com.iwen.chat.pq.view.normal.HomeFragment;
import com.qmuiteam.qmui.arch.annotation.DefaultFirstFragment;

import java.util.Observable;


/**
 * 登录成功，
 * 1.需要获得所有未接受的信息
 * 2.获得所有好友
 * 3.获得所有群组
 * 4.本地需要保存所有好友和群组，
 * 5.加载所有本地存储的好友的或群组的消息
 *
 */
@DefaultFirstFragment(value = HomeFragment.class)
public class MainHomeActivity extends PQBaseActivity {

    // TODO: 2020/6/8 这里


    /**
     * friends 请求处理器
     */
    @SuppressLint("HandlerLeak")
    public final Handler friendsHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
                msg.arg1 = UpdateObservable.UPDATE_FRIENDS; //arg1 为更新的事件
                observable.initPQInfo(msg);
        }
    };



    /**
     * groups 请求处理器
     */
    @SuppressLint("HandlerLeak")
    public final Handler groupsHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            msg.arg1 = UpdateObservable.UPDATE_GROUPS; //arg1 为更新的事件
            observable.initPQInfo(msg);
        }
    };


    /**
     * message 请求处理器
     */
    @SuppressLint("HandlerLeak")
    public final Handler messageHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            msg.arg1 = UpdateObservable.UPDATE_MESSAGES; //arg1 为更新的事件
            observable.initPQInfo(msg);
        }
    };


    /**
     * 组message 请求处理器
     */
    @SuppressLint("HandlerLeak")
    public final Handler groupsMessageHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            msg.arg1 = UpdateObservable.UPDATE_MESSAGRS_FOR_GROUP; //arg1 为更新的事件
            observable.initPQInfo(msg);
        }
    };


    public final UpdateObservable observable = new UpdateObservable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Self self = (Self)intent.getSerializableExtra("self");

        initData(self);
    }

    private void initData(Self self) {

        DataHelper dataHelper = DataHelper.getInstance();
        long time = dataHelper.getFriendsUpdateTime(this);

        UserManagement instance = UserManagement.getInstance();

        //请求friends列表
        instance.friends(friendsHandler, self.getToken(), time);
        //请求组列表
         time = dataHelper.getGroupsUpdateTime(this);

        instance.groups(groupsHandler, self.getToken(), time);

        //传递self数据
        spreadSelfData(self);

        //获得所有未接受的消息（非组消息）
        instance.hotMessage(messageHandler, self.getToken());

    }

    public void spreadSelfData(Self self) {
        new Handler().postDelayed(()-> {
            Message msg = new Message();
            msg.arg1 = UpdateObservable.SPREAD_SELF_DATA; //arg1 为更新的事件
            msg.obj = self;
            observable.initPQInfo(msg);
        }, 100);
    }




    public static class UpdateObservable extends Observable {

        public static final int UPDATE_FRIENDS = 0x520;
        public static final int UPDATE_GROUPS = 0x521;
        public static final int UPDATE_MESSAGES = 0x522;
        public static final int UPDATE_MESSAGRS_FOR_GROUP = 0x523;
        public static final int SPREAD_SELF_DATA = 0x524;

        //观察者对象
        public void initPQInfo(Message message) {
            super.setChanged();
            super.notifyObservers(message);
        }
    }
}
