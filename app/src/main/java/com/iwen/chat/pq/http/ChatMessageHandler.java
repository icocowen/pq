package com.iwen.chat.pq.http;



import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.iwen.chat.pq.dao.DataHelper;
import com.iwen.chat.pq.fun.Observable;
import com.iwen.chat.pq.util.GsonUtil;
import com.iwen.chat.pq.view.MainHomeActivity;

import java.util.HashMap;
import java.util.Map;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatMessageHandler extends WebSocketListener  {

    public ChatMessageHandlerObserver chatMessageHandlerObserver = new ChatMessageHandlerObserver();


    private WebSocket webSocket ;
    private volatile boolean isAlive;

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        this.webSocket = webSocket;
        isAlive = true;
        Log.i("ChatMessageHandler", "打开连接");
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//开启心跳检测

    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        if (text.equals("pong")) {
            isAlive = true;
        }else {
            JSONObject jsonObject = JSONUtil.parseObj(text);
            Log.e("onMessage : 测试发送信息",text);
            Message msg = new Message();
            msg.what = 1;
            msg.obj = jsonObject;
            chatMessageHandlerObserver.chatMessageArrival(msg);
        }


    }





    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
        Log.i("ChatMessageHandler", "关闭连接");
        this.webSocket = null;
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t,  Response response) {
        super.onFailure(webSocket, t, response);
    }

    public void sendToOther(com.iwen.chat.pq.dto.Message msg) {
        if (this.webSocket != null) {
            Map<String, String> m = new HashMap<>();
            m.put("toUserId" , String.valueOf(msg.getTargetId()));
            m.put("contentText", msg.getContentText());
            Gson gson = GsonUtil.getInstance();
            webSocket.send(gson.toJson(m));
        }
    }

    public static class ChatMessageHandlerObserver extends Observable {
        public void chatMessageArrival(Message msg) {
            notifyObservers(msg);
        }
    }


    private static final long HEART_BEAT_RATE = 10 * 1000;//每隔10秒进行一次对长连接的心跳检测
    private Handler mHandler = new Handler();
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            Log.e("ChatMessageHandler", "心跳包检测websocket连接状态");
            if (isAlive) {
                webSocket.send("ping");
                isAlive = false;
            }else {
                reconnectWs();
            }

            //每隔一定的时间，对长连接进行一次心跳检测
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };

    /**
     * 发布重连信号，开启重连
     */
    private void reconnectWs() {
        mHandler.removeCallbacks(heartBeatRunnable);
        Message msg = new Message();
        msg.obj = MainHomeActivity.UpdateObservable.NEED_AFRESH_WEBSOCKET;
        chatMessageHandlerObserver.chatMessageArrival(msg);
    }

}
