package com.iwen.chat.pq.http;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocketListener;

/*
* websocket的连接类
* */
public class ChatWebSocket {

    public static final String PQSERVER = "/pqserver";
    public static final String PQSERVER_GROUP = "/pqserver/group";
    public static final String NON_GROUP = "non-group:";
    public static final String GROUP = "group:";

    private ConcurrentHashMap<String, WebSocketListener> m = new ConcurrentHashMap<>();

    private ChatWebSocket(){}
    private static class ChatWebSocketInner {
         final static ChatWebSocket chatWebSocket = new ChatWebSocket();
    }

    public static ChatWebSocket getInstance() {
        return ChatWebSocketInner.chatWebSocket;
    }

    public void connectToChatWebSocket(String url, WebSocketListener webSocketListener){
        OkHttpClient client = new OkHttpClient.Builder()
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newWebSocket(request, webSocketListener);
    }

    public void connectToChatWebSocketNonGroup(String token) {
        ChatMessageHandler chatMessageHandler = new ChatMessageHandler();
        connectToChatWebSocket(UserManagement.BASE_URL + PQSERVER + "/" + token
                , chatMessageHandler);
        m.put(NON_GROUP+token, chatMessageHandler);
    }

    public void connectToChatWebSocketGroup(String token) {
        ChatGroupMessageHandler chatGroupMessageHandler = new ChatGroupMessageHandler();
        connectToChatWebSocket(UserManagement.BASE_URL + PQSERVER_GROUP + "/" + token
                , chatGroupMessageHandler);
        m.put(GROUP+token, chatGroupMessageHandler);
    }

    public ChatMessageHandler getChatMessageHandlerNonGroup(String token) {
        WebSocketListener webSocketListener = m.get(NON_GROUP + token);
        return (ChatMessageHandler) webSocketListener;
}

    public ChatGroupMessageHandler getChatMessageHandler(String token) {
        WebSocketListener webSocketListener = m.get(GROUP + token);
        return (ChatGroupMessageHandler) webSocketListener;
    }



}
