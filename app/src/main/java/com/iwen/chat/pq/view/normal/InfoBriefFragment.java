package com.iwen.chat.pq.view.normal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjq.bar.OnTitleBarListener;
import com.iwen.chat.pq.R;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    //类型是个人还是群组
    private String clazz;

    @Override
    protected View onCreateView() {
        Bundle arguments = getArguments();
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragmemt_info_brief, null);
        super.view = root;

        ButterKnife.bind(this, root);


        initTitleBarBackgroundColorWithDark();


        assert arguments != null;

        this.clazz = (String) arguments.get("class");
        String title = (String) arguments.get("title");
        String subTitle = (String) arguments.get("subTitle");
        targetId = (String) arguments.get("id");

        this.title.setText(title);
        this.subTitle.setText(subTitle);

        String tip;
        int avartarId ;
        if (this.clazz.equals("group")) {
            tip = "退出群组";
            avartarId = R.mipmap.groups1;
        }else {
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
}
