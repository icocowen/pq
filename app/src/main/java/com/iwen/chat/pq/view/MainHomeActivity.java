package com.iwen.chat.pq.view;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.iwen.chat.pq.dao.DataHelper;
import com.iwen.chat.pq.dao.PQDatabases;
import com.iwen.chat.pq.dto.Self;
import com.iwen.chat.pq.fun.Observable;
import com.iwen.chat.pq.fun.Observer;
import com.iwen.chat.pq.http.ChatMessageHandler;
import com.iwen.chat.pq.http.ChatWebSocket;
import com.iwen.chat.pq.http.UserManagement;
import com.iwen.chat.pq.util.GsonUtil;
import com.iwen.chat.pq.view.normal.HomeFragment;
import com.qmuiteam.qmui.arch.annotation.DefaultFirstFragment;



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
public class MainHomeActivity extends PQBaseActivity implements Observer {

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
    private ChatWebSocket chatWebSocket;
    private Self self;
    private PQDatabases pqDatabases;

    @Override
    public void onNetChange(boolean netWorkState) {
        super.onNetChange(netWorkState);
        if (netWorkState && !networkFlag) {
            //重新连接websocket
            connectWebSocket();
            networkFlag = true;

            //通知其他，需要重新获取一下websocket
            Message message = new Message();
            message.arg1 = UpdateObservable.NEED_AFRESH_WEBSOCKET;
            observable.initPQInfo(message);
        }
    }

    /**
     * groups 请求处理器
     */
    @SuppressLint("HandlerLeak")
    public final Handler groupsHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Gson gson = GsonUtil.getInstance();


            Log.w("执行组的handler方法", gson.toJson(msg));
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
            msg.arg1 = UpdateObservable.UPDATE_MESSAGES_FOR_GROUP; //arg1 为更新的事件
            observable.initPQInfo(msg);
        }
    };


    public final UpdateObservable observable = new UpdateObservable();

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        self = (Self)intent.getSerializableExtra("self");
        //建立非组的websocket连接
        chatWebSocket = ChatWebSocket.getInstance();
        pqDatabases = new PQDatabases(this);

        connectWebSocket();

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

        //连接类

    }

    public void connectWebSocket() {
        //连接websocket（非组）
        chatWebSocket.connectToChatWebSocketNonGroup(self.getToken());
        ChatMessageHandler chatMessageHandler = chatWebSocket.getChatMessageHandlerNonGroup(self.getToken());
        chatMessageHandler.chatMessageHandlerObserver.addObserver(this);
    }


    public void spreadSelfData(Self self) {
        new Handler().postDelayed(()-> {
            Message msg = new Message();
            msg.arg1 = UpdateObservable.SPREAD_SELF_DATA; //arg1 为更新的事件
            msg.obj = self;
            observable.initPQInfo(msg);
        }, 100);
    }

    @Override
    public void update(Observable o, Object arg) {
        //通知信息
        messageHandler.sendMessage((Message) arg);
    }


    public void deleteFriend(String id) {
        android.os.Message message = new android.os.Message();
        message.arg1 = MainHomeActivity.UpdateObservable.DELETE_FRIEND_EVENT;
        message.obj = id;
//        pqDatabases.deleteInfoFriend(id, self.getId());
        observable.initPQInfo(message);
    }




    public static class UpdateObservable extends Observable {

        public static final int UPDATE_FRIENDS = 0x520;
        public static final int UPDATE_GROUPS = 0x521;
        public static final int UPDATE_MESSAGES = 0x522;
        public static final int UPDATE_MESSAGES_FOR_GROUP = 0x523;
        public static final int SPREAD_SELF_DATA = 0x524;
        public static final int UPDATE_TO_MESSAGE_LIST = 0x525;
        public static final int NEED_AFRESH_WEBSOCKET = 0x526;
        public static final int ENTERY_CHAT_FRAGMENT = 0x527;
        public static final int DELETE_FRIEND_EVENT = 0x528;

        private Object lock = new Object();

        //观察者对象
        public  void initPQInfo(Message message) {
            synchronized (lock) {

                Log.e("initPQInfo", GsonUtil.getInstance().toJson(message));
                super.notifyObservers(message);
            }

        }
    }
}
