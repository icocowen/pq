package com.iwen.chat.pq.view.normal;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.iwen.chat.pq.R;
import com.iwen.chat.pq.dao.DataHelper;
import com.iwen.chat.pq.dto.Friend;
import com.iwen.chat.pq.dto.Self;
import com.iwen.chat.pq.fun.FunGroupListView;
import com.iwen.chat.pq.view.MainHomeActivity;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONObject;

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

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_message, null);
        super.view = root;
        ButterKnife.bind(this, root);

        initTitleBarBackgroundColorWithDark();
        initGroupListView();
        //注册监听事件
        ((MainHomeActivity) requireActivity()).observable.addObserver(this);

        return root;
    }

    private void initGroupListView() {

//        QMUICommonListItemView normalItem = mGroupListView.createItemView(
//                ContextCompat.getDrawable(getContext(), R.mipmap.groups),
//                "Item 1",
//                "",
//                QMUICommonListItemView.VERTICAL,
//                QMUICommonListItemView.ACCESSORY_TYPE_NONE);
//        normalItem.setOrientation(QMUICommonListItemView.VERTICAL);

        QMUICommonListItemView itemWithDetailBelow = mGroupListView.createItemView("Item 1");
        itemWithDetailBelow.setOrientation(QMUICommonListItemView.VERTICAL);
        itemWithDetailBelow.setDetailText("在标题下方的详细信息");
        itemWithDetailBelow.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.msg));
        itemWithDetailBelow.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_NONE);


        QMUICommonListItemView itemWithDetailBelow2 = mGroupListView.createItemView("Item 2");
        itemWithDetailBelow2.setOrientation(QMUICommonListItemView.VERTICAL);
        itemWithDetailBelow2.setDetailText("在标题下方的详细信息");
        itemWithDetailBelow2.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.msg));
        itemWithDetailBelow2.setTipPosition(QMUICommonListItemView.TIP_POSITION_LEFT);
        itemWithDetailBelow2.showRedDot(true);

        QMUICommonListItemView itemWithDetailBelow3 = mGroupListView.createItemView("最新3");
        itemWithDetailBelow3.setOrientation(QMUICommonListItemView.VERTICAL);
        itemWithDetailBelow3.setDetailText("在标题下方的详细信息");
        itemWithDetailBelow3.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.msg));
        itemWithDetailBelow3.setTipPosition(QMUICommonListItemView.TIP_POSITION_LEFT);
        itemWithDetailBelow3.showRedDot(true);


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof QMUICommonListItemView) {
                    CharSequence text = ((QMUICommonListItemView) v).getText();
                    Toast.makeText(getActivity(), text + " is Clicked", Toast.LENGTH_SHORT).show();
                    if (((QMUICommonListItemView) v).getAccessoryType() == QMUICommonListItemView.ACCESSORY_TYPE_SWITCH) {
                        ((QMUICommonListItemView) v).getSwitch().toggle();
                    }
                }
            }
        };

        FunGroupListView.Section section = FunGroupListView.newSection(getContext());

        View.OnLongClickListener onLongClickListener = v -> {
//            mGroupListView.removeView(v);
//            section.removeFrom(mGroupListView);

//            section.addItemView(itemWithDetailBelow2, onClickListener);
//            mGroupListView.addView(itemWithDetailBelow2, 0);
//            mGroupListView.addView(itemWithDetailBelow3, 0);


//            section.addTo(mGroupListView);

//            section.removeFrom(mGroupListView);
//            section.addItemViewToHead(itemWithDetailBelow2, onClickListener);
//            section.addItemViewToHead(itemWithDetailBelow3, onClickListener);
//            section.addTo(mGroupListView);
            return true;
        };


        int size = QMUIDisplayHelper.dp2px(getContext(), 20);

        section.setUseTitleViewForSectionSpace(false);
        section.setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT);
        section.addItemView(itemWithDetailBelow, onClickListener, onLongClickListener, 0);
        section.addItemViewToTail(itemWithDetailBelow2, v -> {
            ChatFragment chatFragment = new ChatFragment();
            super.startFragment(chatFragment);
        });
        section.setMiddleSeparatorInset(QMUIDisplayHelper.dp2px(getContext(), 16), 0);
//        section.addItemView(itemWithDetailBelow2, onClickListener,onLongClickListener);
        section.addTo(mGroupListView);


//        section


    }

    @Override
    public void update(Observable o, Object arg) {

        Message msg = (Message) arg;
        //这里接受到通知
        if (msg.arg1 == MainHomeActivity.UpdateObservable.UPDATE_MESSAGES) {
            LoadTask loadTask = new LoadTask();
            switch (msg.what) {
                case SUCCESS:
                    JSONObject obj =  (JSONObject) msg.obj;
                    if (Objects.equals(obj.get("code"), 200)) {//200代表成功
                        //200
                        Object rawObj = obj.get("data");
                        if (!(rawObj instanceof JSONNull)) {//200
                            this.data = (JSONArray) rawObj;
                            //异步保存，更新消息
                            new SaveTask().execute("");

                        }else {
                            loadTask.execute("");
                        }

                    }else {
                        Log.e(MessageFragmentPQ.this.getClass().getName(), Objects.requireNonNull((String) obj.get("message")));
                    }
                    break;
                case FAILURE:
                    //从本地加载
                    loadTask.execute("");
                    Bundle data = msg.getData();
                    Log.e(MessageFragmentPQ.this.getClass().getName(), Objects.requireNonNull(data.getString("message")));
            }


        }


    }

    class LoadTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            //异步更新friends组
            initGroupListView();
        }

        @Override
        protected String doInBackground(String... strings) {

            asyncLoadData();
            friends.forEach(d -> friendsData.put(d.getId(), d));
            Log.i("com.iwen.chat.pq.view.normal.FriendsFragmentPQ.LoadTask", "后台加载任务执行完成");
            return null;
        }
    }

    public void asyncLoadData() {
        DataHelper instance = DataHelper.getInstance();
        Self info = instance.getSelfInfo(requireContext());
        friends = pqDatabases.selectFriends(Integer.valueOf(info.getId()));
    }


    class SaveTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            //异步更新friends组
            initGroupListView();
        }

        @Override
        protected String doInBackground(String... strings) {

            //异步保存数据
            DataHelper instance = DataHelper.getInstance();
            instance.saveFriendsUpdateTime(requireContext(), lastFriendsUpdateTime);
            Self info = instance.getSelfInfo(requireContext());

            if (!data.isEmpty()) {
                asyncLoadData();

                final List<Friend> friendList = new ArrayList<>(data.size());
                data.forEach(d -> {
                    JSONObject jObj = (JSONObject) d;
                    Friend friend = new Friend();
                    String id = (String)jObj.get("id");
                    friend.setId(id);
                    friend.setEmail((String)jObj.get("email"));
                    friend.setNickName((String)jObj.get("nickName"));

                    friendList.add(friend);
                    friendsData.put(id, friend);
                });
                List<Integer> needRemove = new ArrayList<>();

                for (int i = 0; i < friends.size(); i++) {
                    if (!friendsData.containsKey(friends.get(i).getId())) {
                        needRemove.add(i);
                    }
                }
                needRemove.forEach(d -> {
                    //删除没有的好友
                    pqDatabases.deleteInfoFriend(friends.get(d).getId(), info.getId());
                });

                //保存friend数据到数据库
                pqDatabases.saveFriendsList(friendList.toArray(),Integer.valueOf(info.getId()));

            }

            Log.i("com.iwen.chat.pq.view.normal.FriendsFragmentPQ.SaveTask", "后台保存任务执行完成");
            return null;
        }
    }
}

