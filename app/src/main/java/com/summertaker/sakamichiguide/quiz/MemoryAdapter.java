package com.summertaker.sakamichiguide.quiz;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.summertaker.sakamichiguide.data.MemberData;

import java.util.ArrayList;

public class MemoryAdapter extends FragmentPagerAdapter {

    ArrayList<MemberData> mMemberList = new ArrayList<>();

    public MemoryAdapter(FragmentManager fm, ArrayList<MemberData> memberList) {
        super(fm);
        this.mMemberList = memberList;
    }
    @Override
    public Fragment getItem(int position) {
        return MemoryFragment.newInstance(position, mMemberList.get(position));
    }

    @Override
    public int getCount() {
        return mMemberList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mMemberList.get(position).getName();
    }
}
