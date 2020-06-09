package com.iwen.chat.pq.base;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class BaseFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> list;

    public BaseFragmentStatePagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        list = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}