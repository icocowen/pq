package com.iwen.chat.pq.view.normal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.hjq.bar.OnTitleBarListener;
import com.iwen.chat.pq.R;
import com.iwen.chat.pq.fun.FunGroupListView;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;

public class AddFragment extends PQBaseFragment {

    FunGroupListView mGroupListView;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragmemt_add, null);
        super.view = root;
        initTitleBarBackgroundColorWithDark();
        initGroupListView();

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

            }
        });
        return root;
    }


    private void initGroupListView() {
        mGroupListView = (FunGroupListView)view.findViewById(R.id.groupListView);

        QMUICommonListItemView itemWithDetailBelow4 = mGroupListView.createItemView(
                ContextCompat.getDrawable(getContext(), R.mipmap.friends),
                "iwen",
                null,
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_NONE
        );

        QMUICommonListItemView itemWithDetailBelow = mGroupListView.createItemView(
                ContextCompat.getDrawable(getContext(), R.mipmap.friends),
                "张三",
                null,
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_NONE
        );

        View.OnClickListener onClickListener = v -> {
            InfoBriefFragment infoBriefFragment = new InfoBriefFragment();
            super.startFragment(infoBriefFragment);
        };

        FunGroupListView.Section section = FunGroupListView.newSection(getContext());

        View.OnLongClickListener onLongClickListener = v -> {
            section.removeFrom(mGroupListView);
//            section.addItemViewToHead(itemWithDetailBelow2, onClickListener);
//            section.addItemViewToHead(itemWithDetailBelow3, onClickListener);
            section.addTo(mGroupListView);
            return true;
        };



        int size = QMUIDisplayHelper.dp2px(getContext(), 20);

        section.setUseTitleViewForSectionSpace(false);
        section.setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT);
        section.addItemView(itemWithDetailBelow, onClickListener,onLongClickListener, 0);
        section.addItemViewToTail(itemWithDetailBelow4, onClickListener);
        section.setMiddleSeparatorInset(QMUIDisplayHelper.dp2px(getContext(), 16), 0);

        section.addTo(mGroupListView);


    }
}
