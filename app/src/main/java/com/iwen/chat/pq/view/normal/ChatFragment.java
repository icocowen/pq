package com.iwen.chat.pq.view.normal;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.RequiresApi;

import com.hjq.bar.OnTitleBarListener;
import com.iwen.chat.pq.R;
import com.iwen.chat.pq.adapter.Adapter_ChatMessage;
import com.iwen.chat.pq.dao.PQDatabases;
import com.iwen.chat.pq.dto.Friend;
import com.iwen.chat.pq.dto.Message;
import com.iwen.chat.pq.dto.Self;
import com.iwen.chat.pq.fun.Observable;
import com.iwen.chat.pq.fun.Observer;
import com.iwen.chat.pq.http.ChatMessageHandler;
import com.iwen.chat.pq.http.ChatWebSocket;
import com.iwen.chat.pq.modle.ChatMessage;
import com.iwen.chat.pq.util.Util;
import com.iwen.chat.pq.view.MainHomeActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import static com.iwen.chat.pq.http.UserManagement.FAILURE;
import static com.iwen.chat.pq.http.UserManagement.SUCCESS;


public class ChatFragment extends PQBaseFragment implements View.OnClickListener {

    public static final int RECEIVED = 0x410;

    private Context mContext;
    private EditText et_content;
    private ListView listView;
    private Button btn_send;
    private List<ChatMessage> chatMessageList = new ArrayList<>();//消息列表
    private Adapter_ChatMessage adapter_chatMessage;
    private Self self;
    private ArrayList<Message> messages;
    private Friend friend;
    private PQDatabases pqDatabases;
    private ChatMessageHandler chatMessageHandlerNonGroup;

    private MsgObserver observer = new MsgObserver();
    private String[] whoOpened;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected View onCreateView() {

        Bundle arguments = getArguments();
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.chat_fragment, null);
        super.view = root;
        mContext = getContext();


        initTitleBarBackgroundColorWithDark();
        findViewById();

        messages = ( ArrayList<Message>)arguments.getSerializable("messages");
        self = (Self)arguments.getSerializable("self");
        friend = (Friend)arguments.getSerializable("fromUser");
        whoOpened = (String[])arguments.getSerializable("whoOpened");

        //注册观察者


        initChatMsglist();

        titleBar.setTitle(friend.getNickName());

        titleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View v) {
                //模拟点击回退按钮事件
                ChatFragment.this.popBackStack();
            }

            @Override
            public void onTitleClick(View v) {

            }

            @Override
            public void onRightClick(View v) {

            }
        });

        ChatWebSocket webSocket = ChatWebSocket.getInstance();
        chatMessageHandlerNonGroup = webSocket.getChatMessageHandlerNonGroup(self.getToken());

        ((MainHomeActivity) requireActivity()).observable.addObserver(observer);


        pqDatabases = new PQDatabases(requireContext());
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainHomeActivity) requireActivity()).observable.deleteObserver(observer);
        whoOpened[0] = "self";
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initChatMsglist() {
        messages.forEach(m -> {
            ChatMessage chatMessage = null;
            if (String.valueOf(m.getTargetId()).equals(self.getId())) {
                chatMessage = new ChatMessage(m.getContentText()
                        ,String.valueOf(m.getSendTime()), 0, friend.getNickName() );
            }else  {
                chatMessage = new ChatMessage(m.getContentText()
                        ,String.valueOf(m.getSendTime()), 1, friend.getNickName() );

            }
            chatMessageList.add(chatMessage);
        });

        initChatMsgListView();
    }


    private void findViewById() {
        listView = view.findViewById(R.id.chatmsg_listView);
        btn_send = view.findViewById(R.id.btn_send);
        et_content = view.findViewById(R.id.et_content);
        btn_send.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                String content = et_content.getText().toString();
                if (content.length() <= 0) {
                    Util.showToast(mContext, "消息不能为空哟");
                    return;
                }else  if (content.length() >= 100) {
                    Util.showToast(mContext, "发送的文字不能超过100个哦");
                    return;
                }
                Message msg = new Message(
                        Integer.valueOf(friend.getId())
                        , Integer.valueOf(self.getId())
                        , System.currentTimeMillis()
                        , content
                        ,self.getId());

                pqDatabases.insertMessage(msg);
                //更新到自己的消息列表
                android.os.Message message = new android.os.Message();
                message.arg1 = MainHomeActivity.UpdateObservable.UPDATE_TO_MESSAGE_LIST;
                message.obj = msg;
                ((MainHomeActivity) requireActivity()).observable.initPQInfo(message);
                chatMessageHandlerNonGroup.sendToOther(msg);
                updateChatItem(msg);
                break;
            default:
                break;
        }
    }


    public void updateChatItem(Message msg) {
//        pqDatabases.insertMessage(msg);
//        chatMessageHandlerNonGroup.sendToOther(msg);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(msg.getContentText());
        chatMessage.setIsMeSend(String.valueOf(msg.getFromUserId()).equals(self.getId()) ? 1 : 0);
        chatMessage.setTime(System.currentTimeMillis()+"");
        chatMessageList.add(chatMessage);
        initChatMsgListView();
        et_content.setText("");
    }

    private void initChatMsgListView(){
        adapter_chatMessage = new Adapter_ChatMessage(mContext, chatMessageList);
        listView.setAdapter(adapter_chatMessage);
        listView.setSelection(chatMessageList.size());
    }


    public class MsgObserver implements Observer {



        @Override
        public void update(Observable o, Object arg) {
            android.os.Message msg = (android.os.Message) arg;
            //这里接受到通知
            if (msg.arg1 == MainHomeActivity.UpdateObservable.UPDATE_MESSAGES) {
//            LoadTask loadTask = new LoadTask();
                switch (msg.what) {
                    case SUCCESS:

                        JSONObject obj =  (JSONObject) msg.obj;
                        if (Objects.equals(obj.get("code"), 200)) {//200代表成功
                            //200
                            Object rawObj = obj.get("data");


                            if (!(rawObj instanceof JSONNull)) {//200
                                msg.arg2 = RECEIVED;
                                JSONArray array = (JSONArray) rawObj;
                                if (array != null && !array.isEmpty()) {

                                    JSONObject jObj  = (JSONObject) array.get(0);
                                    com.iwen.chat.pq.dto.Message message = new com.iwen.chat.pq.dto.Message();
                                    message.setTargetId(Integer.valueOf((String)jObj.get("toUserId")));
                                    message.setContentText((String)jObj.get("contentText"));
                                    String fromUserId = (String)jObj.get("fromUserId");
                                    message.setFromUserId(Integer.valueOf(fromUserId));
                                    message.setSendTime(Long.parseLong((String)jObj.get("sendTime")));
                                    message.setOwner(fromUserId);

                                    //chatMessageHandlerNonGroup.sendToOther(message);
                                    updateChatItem(message);
                                }

                            }


                        }else {
                            Log.e(ChatFragment.this.getClass().getName(), Objects.requireNonNull((String) obj.get("message")));
                        }
                        break;
                    case FAILURE:
                        Bundle data = msg.getData();
                        Log.e(ChatFragment.this.getClass().getName(), Objects.requireNonNull(data.getString("message")));
                }


            }else if (msg.arg1 == MainHomeActivity.UpdateObservable.NEED_AFRESH_WEBSOCKET) {
                ChatWebSocket webSocket = ChatWebSocket.getInstance();
                chatMessageHandlerNonGroup = webSocket.getChatMessageHandlerNonGroup(self.getToken());
            }


        }
    }

}
