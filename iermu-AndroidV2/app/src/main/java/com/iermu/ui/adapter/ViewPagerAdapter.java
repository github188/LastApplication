package com.iermu.ui.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by zhoushaopei on 15/6/25.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    List<Fragment> mFragments;

    public ViewPagerAdapter(FragmentManager fm, List<Fragment> fms) {
        super(fm);
        this.mFragments = fms;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
