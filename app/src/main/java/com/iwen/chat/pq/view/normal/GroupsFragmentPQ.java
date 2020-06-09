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

import com.hjq.bar.OnTitleBarListener;
import com.iwen.chat.pq.R;
import com.iwen.chat.pq.dao.DataHelper;
import com.iwen.chat.pq.dao.PQDatabases;
import com.iwen.chat.pq.dto.Group;
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
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONObject;

import static com.iwen.chat.pq.http.UserManagement.FAILURE;
import static com.iwen.chat.pq.http.UserManagement.SUCCESS;


public class GroupsFragmentPQ extends PQBaseFragment implements Observer {

    public static final String LAST_FRIENDS_UPDATE_TIME = "lastGroupsUpdateTime";
    @BindView(R.id.groupListView)
    FunGroupListView mGroupListView;

    private long lastGroupsUpdateTime;
    private JSONArray data;
    private PQDatabases pqDatabases;

    //itemObject -> id
    private ConcurrentHashMap<Object, String> groupsItemData = new ConcurrentHashMap<>();
    //id -> friendInfo
    private ConcurrentHashMap<String, Group> groupsData = new ConcurrentHashMap<>();
    private List<Group> groups;


    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_groups, null);
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
                NewGroupFragment newGroupFragment = new NewGroupFragment();


                startFragment(newGroupFragment);
            }
        });

        ((MainHomeActivity) requireActivity()).observable.addObserver(this);
        pqDatabases = new PQDatabases(requireContext());
        return root;
    }


    private QMUICommonListItemView createItem(String groupName) {
        return mGroupListView.createItemView(
                ContextCompat.getDrawable(getContext(), R.mipmap.groups),
                groupName,
                null,
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_NONE
        );
    }

    private void initGroupListView() {


        FunGroupListView.Section section = FunGroupListView.newSection(getContext());

        View.OnClickListener onClickListener = v -> {
            if (groupsItemData.containsKey(v)) {
                Group group = groupsData.get(Objects.requireNonNull(groupsItemData.get(v)));
                Bundle bundle = new Bundle();
                bundle.putString("class", "group");
                assert group != null;
                bundle.putString("title", group.getGroupName());
                bundle.putString("subTitle", "群号："+group.getId());
                bundle.putString("id", String.valueOf(group.getId()));
                InfoBriefFragment infoBriefFragment = new InfoBriefFragment();
                infoBriefFragment.setArguments(bundle);
                super.startFragment(infoBriefFragment);
            }
        };


        int size = QMUIDisplayHelper.dp2px(getContext(), 20);

        section.setUseTitleViewForSectionSpace(false);
        section.setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT);

        groupsData.values().forEach(g -> {
            QMUICommonListItemView item = createItem(g.getGroupName());
            groupsItemData.put(item, String.valueOf(g.getId()));
            section.addItemViewToTail(item, onClickListener);
        });

        section.setMiddleSeparatorInset(QMUIDisplayHelper.dp2px(getContext(), 16), 0);
        section.addTo(mGroupListView);


    }

    @Override
    public void update(Observable o, Object arg) {
        Message msg = (Message) arg;
        //这里接受到通知
        if (msg.arg1 == MainHomeActivity.UpdateObservable.UPDATE_GROUPS) {
            LoadTask loadTask = new LoadTask();

            switch (msg.what) {
                case SUCCESS:
                    JSONObject obj = (JSONObject) msg.obj;
                    if (Objects.equals(obj.get("code"), 200)) {//200代表成功
                        Object rawObj = obj.get("data");
                        if (!(rawObj instanceof JSONNull)) {//200
                            JSONObject tObj = (JSONObject) rawObj;
                            this.lastGroupsUpdateTime = (long) tObj.get(LAST_FRIENDS_UPDATE_TIME);
                            this.data = (JSONArray) tObj.get("groups");
                            new SaveTask().execute("");
                            //保存联系人最后的更新时间
                            // 异步更新UI
                        } else {
                            //从数据库加载数据
                            //从本地加载
                            loadTask.execute("");
                        }
                        //异步的保存数据

                    } else {
                        Log.e(GroupsFragmentPQ.this.getClass().getName(), Objects.requireNonNull((String) obj.get("message")));
                    }
                    break;
                case FAILURE:
                    //从本地加载
                    loadTask.execute("");
                    Bundle data = msg.getData();
                    Log.e(GroupsFragmentPQ.this.getClass().getName(), Objects.requireNonNull(data.getString("message")));
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
            groups.forEach(d -> groupsData.put(String.valueOf(d.getId()), d));

            Log.i("com.iwen.chat.pq.view.normal.GroupsFragmentPQ.LoadTask", "后台加载任务执行完成");
            return null;
        }
    }

    public void asyncLoadData() {
        DataHelper instance = DataHelper.getInstance();
        Self info = instance.getSelfInfo(requireContext());
        groups = pqDatabases.selectGroups(Integer.valueOf(info.getId()));
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
            instance.saveGroupsUpdateTime(requireContext(), lastGroupsUpdateTime);
            Self info = instance.getSelfInfo(requireContext());


            if (!data.isEmpty()) {
                asyncLoadData();

                final List<Group> groupList = new ArrayList<>(data.size());
                data.forEach(d -> {
                    JSONObject jObj = (JSONObject) d;
                    Group group = new Group();
                    Integer id = jObj.getInt("id");
                    group.setId(id);
                    group.setCreateTime(jObj.getLong("createTime"));
                    group.setGroupName(jObj.getStr("groupName"));
                    group.setGroupSize(jObj.getInt("groupSize"));

                    groupList.add(group);

                    groupsData.put(id.toString(), group);
                });

                List<Integer> needRemove = new ArrayList<>();

                for (int i = 0; i < groups.size(); i++) {
                    if (!groupsData.containsKey(String.valueOf(groups.get(i).getId()))) {
                        needRemove.add(i);
                    }
                }
                needRemove.forEach(d -> {
                    //删除没有的好友
                    pqDatabases.deleteInfoGroup(String.valueOf(groups.get(d).getId()), info.getId());
                });

                //保存friend数据到数据库
                pqDatabases.saveGroupsList(groupList.toArray(), Integer.valueOf(info.getId()));

            }

            Log.i("com.iwen.chat.pq.view.normal.FriendsFragmentPQ.SaveTask", "后台保存任务执行完成");
            return null;
        }
    }


}
