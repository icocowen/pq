package com.iwen.chat.pq.view.normal;

import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.iwen.chat.pq.R;
import com.iwen.chat.pq.fun.Observable;
import com.iwen.chat.pq.fun.Observer;
import com.iwen.chat.pq.util.StatusBarUtil;
import com.iwen.chat.pq.view.MainHomeActivity;
import com.next.easynavigation.view.EasyNavigationBar;

import java.util.ArrayList;
import java.util.List;

//@FirstFragments(
//        value = {
//                MessageFragmentPQ.class,
//                FriendsFragmentPQ.class,
//                MeFragmentPQ.class,
//                MeInfoFragment.class,
//                GroupsFragmentPQ.class,
//        })
//@LatestVisitRecord
//public class HomeFragment extends PQBaseActivity {
public class HomeFragment extends PQBaseFragment  {

    private EasyNavigationBar navigationBar;

    private String[] tabText = {"消息", "联系人", "群组", "我"};
    //未选中icon
    private int[] normalIcon = {R.mipmap.msg, R.mipmap.friends, R.mipmap.groups, R.mipmap.me};
    //选中时icon
    private int[] selectIcon = {R.mipmap.msg1, R.mipmap.friends1, R.mipmap.groups1, R.mipmap.me1};

    private List<Fragment> fragments = new ArrayList<>();


    public EasyNavigationBar getNavigationBar() {
        return navigationBar;
    }



    @Override
    protected View onCreateView() {

        View root = LayoutInflater.from(getActivity()).inflate(R.layout.home_activity, null);
        super.view = root;

        navigationBar = view.findViewById(R.id.navigationBar);

        fragments.add(new MessageFragmentPQ());
        fragments.add(new FriendsFragmentPQ());
        fragments.add(new GroupsFragmentPQ());
        fragments.add(new MeFragmentPQ());

        navigationBar.titleItems(tabText)
                .normalIconItems(normalIcon)
                .selectIconItems(selectIcon)
                .fragmentList(fragments)
                .fragmentManager(getActivity().getSupportFragmentManager())
                .canScroll(true)
                .build();



        StatusBarUtil.setRootViewFitsSystemWindows(getActivity(),true);
        StatusBarUtil.setTranslucentStatus(getActivity());

        fragments.forEach(f -> {
            ((MainHomeActivity)getActivity()).observable.addObserver((Observer) f);
        });


        return view;
    }




}
