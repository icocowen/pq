package com.iwen.chat.pq.view.normal;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.hjq.bar.OnTitleBarListener;
import com.iwen.chat.pq.R;
import com.iwen.chat.pq.dao.DataHelper;
import com.iwen.chat.pq.fun.FunGroupListView;
import com.iwen.chat.pq.http.UserManagement;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

import static com.iwen.chat.pq.http.UserManagement.FAILURE;
import static com.iwen.chat.pq.http.UserManagement.SUCCESS;

public class AddFragment extends PQBaseFragment {


    @BindView(R.id.key)
    EditText key;
    @BindView(R.id.groupListView)
    FunGroupListView mGroupListView;
    private UserManagement server;
    private DataHelper helper;

    private ConcurrentHashMap<String, Object> idWithItem = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Object, String> itemWithId = new ConcurrentHashMap<>();



    @SuppressLint("HandlerLeak")
    private Handler friendSearchHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:
                    JSONObject obj = (JSONObject) msg.obj;
                    if (Objects.equals(obj.get("code"), 200)) {//200代表成功
                        //200
                        JSONObject data = (JSONObject) obj.get("data");
                        friendList = (JSONArray) data.get("friends");
                        initGroupListView();


                    } else {
                        Log.e(AddFragment.this.getClass().getName(), Objects.requireNonNull((String) obj.get("message")));
                    }
                    break;
                case FAILURE:
                    Bundle data = msg.getData();
                    Log.e(AddFragment.this.getClass().getName(), Objects.requireNonNull(data.getString("message")));
            }
        }
    };
    private JSONArray friendList;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragmemt_add, null);
        super.view = root;
        ButterKnife.bind(this, view);

        initTitleBarBackgroundColorWithDark();
        server = UserManagement.getInstance();
        helper = DataHelper.getInstance();
//        initGroupListView();

        titleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View v) {
                //模拟点击回退按钮事件
                AddFragment.this.popBackStack();
            }

            @Override
            public void onTitleClick(View v) {

            }

            @Override
            public void onRightClick(View v) {
                //搜索事件
                String keyStr = key.getText().toString();
                server.search(friendSearchHandler, helper.getSelfInfo(requireContext()).getToken(), keyStr);
                mGroupListView.removeAllViews();
            }
        });
        return root;
    }


    private QMUICommonListItemView createItem(String nickName, String email, int icon) {
        return mGroupListView.createItemView(
                ContextCompat.getDrawable(getContext(), icon),
                nickName,
                email,
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_NONE
        );
    }


    private void initGroupListView() {



        View.OnClickListener onClickListener = v -> {

            InfoBriefFragment infoBriefFragment = new InfoBriefFragment();

            super.startFragment(infoBriefFragment);
        };

        FunGroupListView.Section section = FunGroupListView.newSection(getContext());

        int size = QMUIDisplayHelper.dp2px(getContext(), 20);

        section.setUseTitleViewForSectionSpace(false);
        section.setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT);

        friendList.forEach(m -> {
            JSONObject jObj = (JSONObject)m;
            String nickName = (String) jObj.get("nickName");
            String email = (String)jObj.get("email");
            String id = (String)jObj.get("id");
            QMUICommonListItemView item = createItem(nickName, email, R.mipmap.friends);
            section.addItemViewToTail(item, onClickListener);
            idWithItem.put(id+email, item);
            itemWithId.put(item, id+email);
        });


        section.setMiddleSeparatorInset(QMUIDisplayHelper.dp2px(getContext(), 16), 0);
        section.addTo(mGroupListView);


    }
}
