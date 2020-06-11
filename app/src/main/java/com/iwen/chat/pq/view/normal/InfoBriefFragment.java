package com.iwen.chat.pq.view.normal;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hjq.bar.OnTitleBarListener;
import com.iwen.chat.pq.R;
import com.iwen.chat.pq.dao.DataHelper;
import com.iwen.chat.pq.dao.PQDatabases;
import com.iwen.chat.pq.dto.Friend;
import com.iwen.chat.pq.http.UserManagement;
import com.iwen.chat.pq.view.MainHomeActivity;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hutool.json.JSONObject;

import static com.iwen.chat.pq.http.UserManagement.FAILURE;
import static com.iwen.chat.pq.http.UserManagement.SUCCESS;

/**
 * 群或个人简要信息
 * 需要传入title  subtitle class
 */
public class InfoBriefFragment extends PQBaseFragment {


    @BindView(R.id.avatar)
    ImageView avatar;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.subTitle)
    TextView subTitle;
    @BindView(R.id.sendMsg)
    TextView sendMsg;
    @BindView(R.id.delete)
    TextView delete;

    //可能是组的id或者个人的id
    private String targetId;
    private Friend friend;

    //类型是个人还是群组
    private String clazz;
    private UserManagement instance;
    private DataHelper helper;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:

                    JSONObject obj =  (JSONObject) msg.obj;
                    if (Objects.equals(obj.get("code"), 200)) {//200代表成功
                        onBackPressed();
                    }else {
                        Log.e(InfoBriefFragment.this.getClass().getName(), Objects.requireNonNull((String) obj.get("message")));
                    }
                    break;
                case FAILURE:
                    Bundle data = msg.getData();
                    Log.e(InfoBriefFragment.this.getClass().getName(), Objects.requireNonNull(data.getString("message")));
            }
        }
    };

    @Override
    protected View onCreateView() {
        Bundle arguments = getArguments();
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragmemt_info_brief, null);
        super.view = root;

        ButterKnife.bind(this, root);


        initTitleBarBackgroundColorWithDark();
        instance = UserManagement.getInstance();
        helper = DataHelper.getInstance();

        assert arguments != null;

        this.clazz = (String) arguments.get("class");
        String title = (String) arguments.get("title");
        String subTitle = (String) arguments.get("subTitle");

        friend = (Friend) arguments.get("friend");
        targetId = (String)arguments.get("id");

        this.title.setText(title);
        this.subTitle.setText(subTitle);

        String tip;
        int avartarId;
        if (this.clazz.equals("group")) {
            tip = "退出群组";
            avartarId = R.mipmap.groups1;
        } else {
            tip = "删除好友";
            avartarId = R.mipmap.me1;
        }
        this.delete.setText(tip);

        //设置头像
        this.avatar.setImageResource(avartarId);

        titleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View v) {
                onBackPressed();
            }

            @Override
            public void onTitleClick(View v) {

            }

            @Override
            public void onRightClick(View v) {

            }
        });
        return root;
    }

    @OnClick(R.id.sendMsg)
    public void onSendMsgClicked() {
        //发布事件
        if (clazz.equals("friend")) {
            android.os.Message message = new android.os.Message();
            message.arg1 = MainHomeActivity.UpdateObservable.ENTERY_CHAT_FRAGMENT;
            message.obj = friend;
            ((MainHomeActivity) requireActivity()).observable.initPQInfo(message);
        }


    }

    @OnClick(R.id.delete)
    public void onDeleteClicked() {
        if (clazz.equals("friend")) {


            new QMUIDialog.MessageDialogBuilder(getActivity())
            .setTitle("删除好友")
                    .setMessage("确定要删除吗？")
                    .setSkinManager(QMUISkinManager.defaultInstance(getContext()))
                    .addAction("取消", new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                        }
                    })
                    .addAction(0, "删除", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            instance.deleteFriend(handler
                            ,helper.getSelfInfo(requireContext()).getToken()
                            , targetId );

                            ((MainHomeActivity)requireActivity()).deleteFriend(targetId);

                            dialog.dismiss();
                        }
                    })
                    .create(com.qmuiteam.qmui.R.style.QMUI_Dialog)
                    .show();

//
        }
    }
}
