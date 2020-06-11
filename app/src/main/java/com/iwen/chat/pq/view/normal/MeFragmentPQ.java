package com.iwen.chat.pq.view.normal;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.iwen.chat.pq.R;
import com.iwen.chat.pq.dao.DataHelper;
import com.iwen.chat.pq.dto.Self;
import com.iwen.chat.pq.fun.FunGroupListView;
import com.iwen.chat.pq.fun.Observable;
import com.iwen.chat.pq.fun.Observer;
import com.iwen.chat.pq.view.MainHomeActivity;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MeFragmentPQ extends PQBaseFragment implements Observer {




    @BindView(R.id.groupListView)
    FunGroupListView mGroupListView;

    private HashMap<String, QMUICommonListItemView> preventRepeat = new HashMap<>();


    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_me, null);
        super.view = root;
        ButterKnife.bind(this, root);

        initTitleBarBackgroundColorWithDark();


        new Handler().postDelayed(()-> {
            DataHelper helper = DataHelper.getInstance();
            initGroupListView(helper.getSelfInfo(requireContext()));
        }, 1);

        return root;
    }

    @Override
    public void update(Observable o, Object arg) {

        Message msg = (Message) arg;
        if (msg.arg1 == MainHomeActivity.UpdateObservable.SPREAD_SELF_DATA) {
            initGroupListView((Self) msg.obj);
        }


    }



    private void initGroupListView(Self self) {

        QMUICommonListItemView info = null;
        if (preventRepeat.containsKey(self.getId())) {
            info = preventRepeat.get(self.getId());
            info.setDetailText("email: "+self.getUser());
            return;
        }

        info = mGroupListView.createItemView(
                ContextCompat.getDrawable(requireContext(), R.mipmap.me1),
                self.getNickName(),
                "email: "+self.getUser(),
                QMUICommonListItemView.VERTICAL,
                QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON
        );
        preventRepeat.put(self.getId(),info );

        QMUICommonListItemView setting = mGroupListView.createItemView(
                ContextCompat.getDrawable(requireContext(), R.mipmap.setting),
                "设置",
                null,
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON
        );

        QMUICommonListItemView about = mGroupListView.createItemView(
                ContextCompat.getDrawable(requireContext(), R.mipmap.about),
                "关于",
                null,
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON
        );

        info.setTipPosition(QMUICommonListItemView.TIP_POSITION_LEFT);



        int size = QMUIDisplayHelper.dp2px(requireContext(), 40);

        FunGroupListView.Section section = FunGroupListView.newSection(getContext());
        section.setUseTitleViewForSectionSpace(false);
        section.setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT);
        section.addItemViewToTail(info, (v) -> {
            //启动info设置
            Bundle bundle = new Bundle();
            bundle.putSerializable("self", self);
            MeInfoFragment meInfoFragment = new MeInfoFragment();
            meInfoFragment.setArguments(bundle);
            super.startFragment(meInfoFragment);
        });
        section.setShowSeparator(false);
        section.setOnlyShowStartEndSeparator(false);
        section.addTo(mGroupListView);


        size = QMUIDisplayHelper.dp2px(requireContext(), 20);
        FunGroupListView.Section sectionSetting = FunGroupListView.newSection(getContext());
        sectionSetting.setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT);
        sectionSetting.addItemViewToTail(setting, v -> {

            SettingFragment settingFragment = new SettingFragment();
            super.startFragment(settingFragment);
        });
        sectionSetting.addItemViewToTail(about, v -> {
            AboutFragment aboutFragment = new AboutFragment();
            super.startFragment(aboutFragment);

        });
        sectionSetting.addTo(mGroupListView);
    }


}
