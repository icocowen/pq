package com.iwen.chat.pq.view.normal;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.iwen.chat.pq.R;
import com.iwen.chat.pq.dao.DataHelper;
import com.iwen.chat.pq.dao.PQDatabases;
import com.iwen.chat.pq.dto.Friend;
import com.iwen.chat.pq.dto.Self;
import com.iwen.chat.pq.fun.FunGroupListView;
import com.iwen.chat.pq.view.MainHomeActivity;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import static com.iwen.chat.pq.http.UserManagement.FAILURE;
import static com.iwen.chat.pq.http.UserManagement.SUCCESS;

/*
 *
 * 最多每个聊天显示40条信息，多余40条需要加载
fromUserId: "8"
contentText: "测试信息"
toUserId: "7"
sendTime: "1591676207436"
*
* 组
"{\"fromUserId\":\"7\",
* \"contentText\":\"hello websocket\",
* \"toGroup\":\"1\",
* \"sendTime\":\"1591692255320\"}"
* 单条信息最多100个字
 * */
public class MessageFragmentPQ extends PQBaseFragment implements Observer {

    @BindView(R.id.groupListView)
    FunGroupListView mGroupListView;
    private JSONArray data;

    private ConcurrentHashMap<Object, String> messageItemData = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Object> userIdForItemData = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Friend> formIdWithFromInfo = new ConcurrentHashMap<>();
    //time -> user id,同一个人同一时刻只能存在一条消息
    private Set<String> preventRepeat = new HashSet<>();
    private Set<String> updatedItem = new HashSet<>();
    private ConcurrentHashMap<String, ArrayList<com.iwen.chat.pq.dto.Message>> messageDataList = new ConcurrentHashMap<>();
    private PQDatabases pqDatabases;
    private List<com.iwen.chat.pq.dto.Message> messages;
    private Self info;
    private boolean needShowRedDot = true;
    private String[] whoOpened = {"self"};
    //消息展示的顺序和 消息索引的映射
    private List<QMUICommonListItemView> ordered = new ArrayList<>();



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_message, null);
        super.view = root;
        ButterKnife.bind(this, root);

        initTitleBarBackgroundColorWithDark();
//        initGroupListView();
        //注册监听事件
        ((MainHomeActivity) requireActivity()).observable.addObserver(this);

        pqDatabases = new PQDatabases(requireContext());

        //加载本地信息,防止出现信息列表空白
        asyncLoadData();
        initGroupListView();

        return root;
    }

    /**
     *
     * @param nickName 对象昵称
     * @param brief 最新的消息简介
     * @return
     */
    public QMUICommonListItemView createMessageItem(String nickName, String brief, int icon) {
        QMUICommonListItemView itemMessage = mGroupListView.createItemView(nickName);
        itemMessage.setOrientation(QMUICommonListItemView.VERTICAL);
        itemMessage.setDetailText(brief);
        itemMessage.setImageDrawable(ContextCompat.getDrawable(requireContext(), icon));
        itemMessage.setTipPosition(QMUICommonListItemView.TIP_POSITION_LEFT);
        return itemMessage;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initGroupListView( ) {


        View.OnClickListener onClickListener = v -> {

            String fromId = messageItemData.get(v);
            QMUICommonListItemView t = (QMUICommonListItemView) userIdForItemData.get(fromId);
            t.showRedDot(false);
            updatedItem.remove(fromId);
            ChatFragment chatFragment = new ChatFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("self", info);
            bundle.putSerializable("messages", messageDataList.get(fromId));
            bundle.putSerializable("fromUser", formIdWithFromInfo.get(fromId));
            bundle.putStringArray("whoOpened", whoOpened);
            whoOpened[0] = fromId;
            chatFragment.setArguments(bundle);
            startFragment(chatFragment);
        };

        FunGroupListView.Section section = FunGroupListView.newSection(getContext());

        int size = QMUIDisplayHelper.dp2px(requireContext(), 20);

        section.setUseTitleViewForSectionSpace(false);
        section.setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT);

        for (int i = messages.size() - 1; i >= 0; i--) {
            com.iwen.chat.pq.dto.Message m = messages.get(i);
            Friend f = null;

            if (String.valueOf(m.getTargetId()).equals(info.getId())) {

                f = formIdWithFromInfo.get(String.valueOf(m.getFromUserId()));
            }else {
                f = formIdWithFromInfo.get(String.valueOf(m.getTargetId()));
            }

            assert f != null;
            QMUICommonListItemView mItem = null;
            if (userIdForItemData.containsKey(f.getId())) {

                mItem= (QMUICommonListItemView)userIdForItemData.get(f.getId());
                assert mItem != null;
                mItem.setDetailText(m.getContentText());

                ordered.remove(mItem);
                ordered.add(mItem);


            }else  {

                mItem = createMessageItem(f.getNickName()
                        , m.getContentText(), R.mipmap.friends);
                messageItemData.put(mItem, f.getId());
                userIdForItemData.put(f.getId(), mItem);
//                section.addItemViewToHead(mItem, onClickListener);

                ordered.add(mItem);

            }


        }

        synchronized (ordered) {

            mGroupListView.removeAllViews();


            for (int i = 0; i < ordered.size(); i++) {
                section.addItemViewToHead(ordered.get(i), onClickListener);
            }



            updatedItem.forEach(k -> {
                QMUICommonListItemView item = (QMUICommonListItemView)userIdForItemData.get(k);
                if(item != null ) {
                    String targetId = messageItemData.get(item);
                    if(!whoOpened[0].equals(targetId)) {
                        item.showRedDot(true);

                    }
                }
            });
            updatedItem.clear();


            section.setMiddleSeparatorInset(QMUIDisplayHelper.dp2px(getContext(), 16), 0);
            section.addTo(mGroupListView);

        }

        ordered.clear();



    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void update(Observable o, Object arg) {

        Message msg = (Message) arg;
        //这里接受到通知
        if (msg.arg1 == MainHomeActivity.UpdateObservable.UPDATE_MESSAGES) {
//            LoadTask loadTask = new LoadTask();
            switch (msg.what) {
                case SUCCESS:
                    if (msg.arg2 == ChatFragment.RECEIVED) {
                        needShowRedDot = false;
                    }
                    JSONObject obj =  (JSONObject) msg.obj;
                    if (Objects.equals(obj.get("code"), 200)) {//200代表成功
                        //200
                        Object rawObj = obj.get("data");


                        if (!(rawObj instanceof JSONNull)) {//200
                            this.data = (JSONArray) rawObj;
                            //异步保存，更新消息
                            new SaveTask().execute("");

                        }


                    }else {
                        Log.e(MessageFragmentPQ.this.getClass().getName(), Objects.requireNonNull((String) obj.get("message")));
                    }
                    break;
                case FAILURE:
                    Bundle data = msg.getData();
                    Log.e(MessageFragmentPQ.this.getClass().getName(), Objects.requireNonNull(data.getString("message")));
            }


        }else if (msg.arg1 == MainHomeActivity.UpdateObservable.UPDATE_TO_MESSAGE_LIST) {
            com.iwen.chat.pq.dto.Message message = (com.iwen.chat.pq.dto.Message)msg.obj;
            messageDataList.get(String.valueOf(message.getTargetId())).add(message);
//            QMUICommonListItemView mitem = (QMUICommonListItemView)userIdForItemData.get(String.valueOf(message.getTargetId()));
//            mitem.setDetailText(message.getContentText());
            preventRepeat.add(String.valueOf(message.getSendTime()) + message.getTargetId());
            messages.add(0, message);
            new Handler().postDelayed(() -> {
                initGroupListView();
            }, 500);
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void asyncLoadData() {
        DataHelper instance = DataHelper.getInstance();
        info = instance.getSelfInfo(requireContext());
        messages = pqDatabases.selectMessages(Integer.parseInt(info.getId()));
        List<Friend> friends = pqDatabases.selectFriends(Integer.parseInt(info.getId()));


        //把信息数据和目标用户相对应
        // a的 id -> message
        for (int i = messages.size() - 1; i >= 0; i--) {
            com.iwen.chat.pq.dto.Message m = messages.get(i);

            if (String.valueOf(m.getTargetId()).equals(info.getId())) {

                if (preventRepeat.contains(String.valueOf(m.getSendTime()) + m.getFromUserId())) {
                    continue;
                }

                preventRepeat.add(String.valueOf(m.getSendTime()) + m.getFromUserId());

                if (messageDataList.containsKey(String.valueOf(m.getFromUserId()))) {
                    messageDataList.get(String.valueOf(m.getFromUserId())).add(m);
                }else {
                    ArrayList<com.iwen.chat.pq.dto.Message> list = new ArrayList<>();
                    list.add(m);
                    messageDataList.put(String.valueOf(m.getFromUserId()), list);
                }
            }else{
                if (preventRepeat.contains(String.valueOf(m.getSendTime()) + m.getTargetId())) {
                    continue;
                }

                preventRepeat.add(String.valueOf(m.getSendTime()) + m.getTargetId());
                if (messageDataList.containsKey(String.valueOf(m.getTargetId()))) {
                    messageDataList.get(String.valueOf(m.getTargetId())).add(m);
                }else {
                    ArrayList<com.iwen.chat.pq.dto.Message> list = new ArrayList<>();
                    list.add(m);
                    messageDataList.put(String.valueOf(m.getTargetId()), list);
                }
            }
        }



        friends.forEach(f -> {
            if (messageDataList.containsKey(f.getId())) {
                formIdWithFromInfo.put(f.getId(), f);
            }
        });


    }


    class SaveTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(String s) {
            //异步更新friends组
            initGroupListView();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("LongLogTag")
        @Override
        protected String doInBackground(String... strings) {

            if (!data.isEmpty()) {


                final List<com.iwen.chat.pq.dto.Message> messagesList = new ArrayList<>(data.size());
                data.forEach(d -> {

                    JSONObject jObj  = JSONUtil.parseObj(d);
                    com.iwen.chat.pq.dto.Message message = new com.iwen.chat.pq.dto.Message();
                    message.setTargetId(jObj.getInt("toUserId"));
                    message.setContentText(jObj.getStr("contentText"));
                    Integer fromUserId = jObj.getInt("fromUserId");
                    message.setFromUserId(fromUserId);
                    message.setSendTime(jObj.getLong("sendTime"));
                    messagesList.add(message);

                    updatedItem.add(String.valueOf(message.getFromUserId()));
                    updatedItem.add(String.valueOf(message.getTargetId()));

                });

                //保存friend数据到数据库
                pqDatabases.saveMessageList(messagesList.toArray());



            }

            asyncLoadData();
            Log.i("com.iwen.chat.pq.view.normal.MessageFragmentPQ.SaveTask.doInBackground", "后台保存任务执行完成");
            return null;
        }
    }
}

