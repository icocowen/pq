package com.iwen.chat.pq.view.normal;

import android.view.LayoutInflater;
import android.view.View;

import com.hjq.bar.OnTitleBarListener;
import com.iwen.chat.pq.R;
import com.iwen.chat.pq.fun.FunGroupListView;


public class AboutFragment extends PQBaseFragment {

    @Override
    protected View onCreateView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_about, null);
//        ButterKnife.bind(this, view);
        this.view = view;

        initTitleBarBackgroundColorWithDark();

        titleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View v) {
                //模拟点击回退按钮事件
                AboutFragment.this.popBackStack();
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

}
