package com.iwen.chat.pq.view.normal;

import android.view.LayoutInflater;
import android.view.View;

import com.hjq.bar.OnTitleBarListener;
import com.iwen.chat.pq.R;

public class NewGroupFragment extends PQBaseFragment {
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_new_group, null);
        super.view = root;
        initTitleBarBackgroundColorWithDark();
        titleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View v) {
                //模拟点击回退按钮事件
                NewGroupFragment.this.popBackStack();
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
