package com.summertaker.sakamichiguide.quiz;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.summertaker.sakamichiguide.data.MemberData;

import java.util.ArrayList;
import java.util.Collections;

public class QuizAdapter extends FragmentPagerAdapter {

    ArrayList<MemberData> mMemberList = new ArrayList<>();
    String mMemberName;
    String mRandomName;

    public QuizAdapter(FragmentManager fm, ArrayList<MemberData> memberList) {
        super(fm);
        this.mMemberList = memberList;
    }
    @Override
    public Fragment getItem(int position) {
        setName(position);
        return QuizFragment.newInstance(position, mMemberList.get(position), mMemberName, mRandomName);
    }

    @Override
    public int getCount() {
        return mMemberList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mMemberList.get(position).getName();
    }

    /**
     * 현재 멤버 이름과 중복되지 않는 랜덤한 이름 선택하기
     */
    private void setName(int position) {
        MemberData memberData = mMemberList.get(position);
        ArrayList<Integer> indexList = new ArrayList<>();

        for (int i = 0; i < mMemberList.size(); i++) {
            if (i != position) {
                indexList.add(i);
            }
        }
        Collections.shuffle(indexList);
        int index = indexList.get(0);

        if (index % 2 == 0) { // even
            mRandomName = mMemberList.get(index).getName();
            mMemberName = memberData.getName();
        } else {
            mMemberName = mMemberList.get(index).getName();
            mRandomName = memberData.getName();
        }
    }
}

