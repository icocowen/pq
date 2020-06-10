package com.iwen.chat.pq.http;

import android.os.Message;
import android.util.Log;

import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatGroupMessageHandler extends WebSocketListener {

    private WebSocket webSocket ;

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        //保存服务器连接
        this.webSocket = webSocket;
        Log.i("ChatGroupMessageHandler", "打开连接");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
    }

    public static class ChatMessageHandlerObserver extends Observable {
        public void chatMessageArrival(Message msg) {
            setChanged();
            notifyObservers(msg);
        }
    }
}
