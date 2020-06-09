package com.iwen.chat.pq.view.normal;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.hjq.bar.OnTitleBarListener;
import com.iwen.chat.pq.R;
import com.iwen.chat.pq.dto.Self;
import com.iwen.chat.pq.fun.FunGroupListView;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MeInfoFragment extends PQBaseFragment {


    @BindView(R.id.groupListView)
    FunGroupListView mGroupListView;

    @Override
    protected View onCreateView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_me_info, null);
        ButterKnife.bind(this, view);
        this.view = view;

        Bundle bundle = getArguments();
        assert bundle != null;
        Self self = (Self)bundle.getSerializable("self");

        initTitleBarBackgroundColorWithDark();

        assert self != null;
        initGroupListView(self);

        titleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View v) {
                //模拟点击回退按钮事件
                MeInfoFragment.this.popBackStack();
            }

            @Override
            public void onTitleClick(View v) {

            }

            @Override
            public void onRightClick(View v) {

            }
        });
        return view;
    }

    private void initGroupListView(Self self) {

        //头像
        QMUICommonListItemView avatar = mGroupListView.createItemView("头像");
        avatar.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.me1));
        avatar.addAccessoryCustomView(imageView);

        //昵称
        QMUICommonListItemView nickname = mGroupListView.createItemView(
                null,
                "昵称",
                null,
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_NONE);
        nickname.setDetailText(self.getNickName());

        //邮箱
        QMUICommonListItemView email = mGroupListView.createItemView(
                null,
                "邮箱",
                null,
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_NONE);
        email.setDetailText(self.getUser());




        FunGroupListView.Section section = FunGroupListView.newSection(getContext());
        section.setUseTitleViewForSectionSpace(false);
        section.addItemViewToTail(avatar, (v) -> {
        });
        section.setShowSeparator(false);
        section.setOnlyShowStartEndSeparator(false);
        section.addTo(mGroupListView);


        FunGroupListView.Section sectionSetting = FunGroupListView.newSection(getContext());
        sectionSetting.setUseTitleViewForSectionSpace(false);
        sectionSetting.addItemViewToTail(nickname, v -> {});
        sectionSetting.addItemViewToTail(email, v -> {});
        sectionSetting.addTo(mGroupListView);
    }

}
