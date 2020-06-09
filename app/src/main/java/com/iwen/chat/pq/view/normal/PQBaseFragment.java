package com.iwen.chat.pq.view.normal;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.hjq.bar.TitleBar;
import com.iwen.chat.pq.R;
import com.iwen.chat.pq.util.StatusBarUtil;
import com.qmuiteam.qmui.arch.QMUIFragment;

public abstract class PQBaseFragment extends QMUIFragment {
    protected View view;
    protected TitleBar titleBar;

    public void initTitleBarBackgroundColorWithDark() {
        StatusBarUtil.setStatusBarDarkTheme(getActivity(), true);
        titleBar = view.findViewById(R.id.titleBar);
        StatusBarUtil.setStatusBarWithTitleBarHasEqualColor(titleBar, getActivity());
    }

    public void initTitleBarBackgroundColorWithLight() {
        StatusBarUtil.setStatusBarDarkTheme(getActivity(), false);
        titleBar = view.findViewById(R.id.titleBar);
        StatusBarUtil.setStatusBarWithTitleBarHasEqualColor(titleBar, getActivity());
    }
//
//    @Override
//    public Object onLastFragmentFinish() {
//        return new MeFragmentPQ();
//
//    }


}
