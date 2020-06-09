package com.iwen.chat.pq.view.normal;

import android.os.Handler;
import android.os.Process;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.hjq.bar.OnTitleBarListener;
import com.iwen.chat.pq.R;
import com.iwen.chat.pq.dao.DataHelper;
import com.iwen.chat.pq.fun.FunGroupListView;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;




public class SettingFragment extends PQBaseFragment {

    FunGroupListView mGroupListView;

    @Override
    protected View onCreateView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_setting, null);
//        ButterKnife.bind(this, view);
        this.view = view;

        initTitleBarBackgroundColorWithDark();
        initGroupListView();

        titleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View v) {
                //模拟点击回退按钮事件
                SettingFragment.this.popBackStack();
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

    private void initGroupListView() {
        mGroupListView = view.findViewById(R.id.groupListView);



        //退出登录
        QMUICommonListItemView exit = mGroupListView.createItemView(
                null,
                null,
                null,
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        exit.setDetailText("退出登录");



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
        section.setUseTitleViewForSectionSpace(false);

        section.addItemViewToTail(exit, this::exitLogin);

        section.setShowSeparator(false);
        section.setOnlyShowStartEndSeparator(false);
        section.addTo(mGroupListView);


    }

    private void exitLogin(View view) {
        DataHelper instance = DataHelper.getInstance();
        QMUITipDialog dialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在退出中")
                .create();
        dialog.show();
        instance.clearSelfInfo(getContext());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                Process.killProcess(android.os.Process.myPid());
            }
        }, 1000);


    }

}
