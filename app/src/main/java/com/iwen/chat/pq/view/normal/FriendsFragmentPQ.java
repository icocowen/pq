package com.iwen.chat.pq.view.normal;

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

import com.hjq.bar.OnTitleBarListener;
import com.iwen.chat.pq.R;
import com.iwen.chat.pq.dao.DataHelper;
import com.iwen.chat.pq.dao.PQDatabases;
import com.iwen.chat.pq.dto.Friend;
import com.iwen.chat.pq.dto.Self;
import com.iwen.chat.pq.fun.FunGroupListView;
import com.iwen.chat.pq.fun.Observable;
import com.iwen.chat.pq.fun.Observer;
import com.iwen.chat.pq.view.MainHomeActivity;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONObject;

import static com.iwen.chat.pq.http.UserManagement.FAILURE;
import static com.iwen.chat.pq.http.UserManagement.SUCCESS;

//todo 解决异步任务问题，不能调用更新UI线程
public class FriendsFragmentPQ extends PQBaseFragment implements Observer {


    public static final String LAST_FRIENDS_UPDATE_TIME = "lastFriendsUpdateTime";
    @BindView(R.id.groupListView)
    FunGroupListView mGroupListView;

    private JSONArray data;
    //itemObject -> id
    private ConcurrentHashMap<Object, String> friendsItemData = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Object> idForitemData = new ConcurrentHashMap<>();
    //id -> friendInfo
    private ConcurrentHashMap<String, Friend> friendsData = new ConcurrentHashMap<>();
    private Set<String> preventRepeat = new HashSet<>();
    private long lastFriendsUpdateTime;
    private PQDatabases pqDatabases;
    private List<Friend> friends;
    private Self info;
    private DataHelper helper;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_friends, null);
        super.view = root;
        ButterKnife.bind(this, root);

        initTitleBarBackgroundColorWithDark();
        titleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View v) {

            }

            @Override
            public void onTitleClick(View v) {

            }

            @Override
            public void onRightClick(View v) {

                AddFragment addFragment = new AddFragment();
                startFragment(addFragment);
            }
        });


        //todo 测试fragment从activity获得值

        pqDatabases = new PQDatabases(requireContext());
        helper = DataHelper.getInstance();
        info = helper.getSelfInfo(requireContext());
        new Handler().postDelayed(()-> {
            asyncLoadData();
            initGroupListView();
        }, 1);

        return root;
    }



    private QMUICommonListItemView createItem(String nickName) {
        return mGroupListView.createItemView(
                ContextCompat.getDrawable(requireContext(), R.mipmap.friends),
                nickName,
                null,
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_NONE
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initGroupListView() {

        FunGroupListView.Section section = FunGroupListView.newSection(getContext());

        View.OnClickListener onClickListener = v -> {
            if (friendsItemData.containsKey(v)) {
                Friend friend = friendsData.get(Objects.requireNonNull(friendsItemData.get(v)));
                Bundle bundle = new Bundle();
                bundle.putString("class", "friend");
                bundle.putString("title", friend.getNickName());
                bundle.putString("subTitle", "邮箱："+ friend.getEmail());
                bundle.putString("id", friend.getId());
                bundle.putSerializable("friend", friend);

                InfoBriefFragment infoBriefFragment = new InfoBriefFragment();
                infoBriefFragment.setArguments(bundle);
                super.startFragment(infoBriefFragment);
            }

        };




        int size = QMUIDisplayHelper.dp2px(requireContext(), 20);

        section.setUseTitleViewForSectionSpace(false);
        section.setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT);

        friendsData.values().forEach(f -> {
                if (!preventRepeat.contains(f.getId())) {
                    QMUICommonListItemView item = createItem(f.getNickName());
                    friendsItemData.put(item, f.getId());
                    idForitemData.put(f.getId(), item);
                    section.addItemViewToTail(item, onClickListener);
                    preventRepeat.add(f.getId());
                }
        });

        section.setMiddleSeparatorInset(QMUIDisplayHelper.dp2px(getContext(), 16), 0);
        section.addTo(mGroupListView);


    }

    @Override
    public void update(Observable o, Object arg) {
        Message msg = (Message) arg;
        //这里接受到通知
        if (msg.arg1 == MainHomeActivity.UpdateObservable.UPDATE_FRIENDS) {
            LoadTask loadTask = new LoadTask();
            switch (msg.what) {
                case SUCCESS:
                    JSONObject obj = (JSONObject) msg.obj;
                    if (Objects.equals(obj.get("code"), 200)) {//200代表成功
                        Object rawObj = obj.get("data");
                        if (!(rawObj instanceof JSONNull)) {//200
                            JSONObject tObj = (JSONObject) rawObj;
                            this.lastFriendsUpdateTime = (long) tObj.get(LAST_FRIENDS_UPDATE_TIME);
                            this.data = (JSONArray) tObj.get("friends");
                            new SaveTask().execute("");
                            //保存联系人最后的更新时间
                            // 异步更新UI
                        }else {
                            //从数据库加载数据
                            //从本地加载
                            loadTask.execute("");
                        }
                        //异步的保存数据

                    } else {
                        Log.e(FriendsFragmentPQ.this.getClass().getName(), Objects.requireNonNull((String) obj.get("message")));
                    }
                    break;
                case FAILURE:
                    //从本地加载
                    loadTask.execute("");
                    Bundle data = msg.getData();
                    Log.e(FriendsFragmentPQ.this.getClass().getName(), Objects.requireNonNull(data.getString("message")));
            }

        }else if (msg.arg1 == MainHomeActivity.UpdateObservable.DELETE_FRIEND_EVENT) {
            String friendId = (String)msg.obj;
            friendsData.remove(friendId);
            QMUICommonListItemView item = (QMUICommonListItemView)idForitemData.get(friendId);
            friendsItemData.remove(item);
            idForitemData.remove(friendId);
            preventRepeat.remove(friendId);

            mGroupListView.removeView(item);
            pqDatabases.deleteInfoFriend(friendId, info.getId());

        }
    }

    class LoadTask extends AsyncTask<String, Integer, String> {

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
        @Override
        protected String doInBackground(String... strings) {

            asyncLoadData();
            friends.forEach(d -> friendsData.put(d.getId(), d));
            Log.i("FriendsFragmentPQ", "后台加载任务执行完成");
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

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(String s) {
            //异步更新friends组
            initGroupListView();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected String doInBackground(String... strings) {

            //异步保存数据

            helper.saveFriendsUpdateTime(requireContext(), lastFriendsUpdateTime);


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
                    ((MainHomeActivity)requireActivity()).deleteFriend(friends.get(d).getId());
                });

                //保存friend数据到数据库
                pqDatabases.saveFriendsList(friendList.toArray(),Integer.valueOf(info.getId()));

            }

            Log.i("FriendsFragmentPQ", "后台保存任务执行完成");
            return null;
        }
    }





}
