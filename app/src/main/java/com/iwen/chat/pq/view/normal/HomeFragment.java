package com.iwen.chat.pq.view.normal;

import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.iwen.chat.pq.R;
import com.iwen.chat.pq.util.StatusBarUtil;
import com.iwen.chat.pq.view.MainHomeActivity;
import com.next.easynavigation.view.EasyNavigationBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

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


//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.home_activity);
//
//        navigationBar = findViewById(R.id.navigationBar);
//
//        fragments.add(new MessageFragmentPQ());
//        fragments.add(new FriendsFragmentPQ());
//        fragments.add(new GroupsFragmentPQ());
//        fragments.add(new MeFragmentPQ());
//
//        navigationBar.titleItems(tabText)
//                .normalIconItems(normalIcon)
//                .selectIconItems(selectIcon)
//                .fragmentList(fragments)
//                .fragmentManager(getSupportFragmentManager())
//                .canScroll(true)
//                .build();
//
//
//
//        StatusBarUtil.setRootViewFitsSystemWindows(this,true);
//        StatusBarUtil.setTranslucentStatus(this);
//
//    }
//
//
    public EasyNavigationBar getNavigationBar() {
        return navigationBar;
    }

    private InnerObserver innerObserver = new InnerObserver();


    @Override
    protected View onCreateView() {

        View root = LayoutInflater.from(getActivity()).inflate(R.layout.home_activity, null);
        super.view = root;

        navigationBar = view.findViewById(R.id.navigationBar);
//
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
//
//        //注册观察者
//        ((MainHomeActivity)getActivity()).observable.addObserver(innerObserver);


        return view;
    }


    private static class InnerObserver implements Observer {
        @Override
        public void update(Observable o, Object arg) {
            Message msg = (Message)arg;
            System.out.println(msg.arg1 + ":被通知的类 " + this.getClass().toString());
        }
    }


}
