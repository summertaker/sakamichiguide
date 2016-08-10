package com.summertaker.sakamichiguide.member;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseActivity;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.util.Util;

public class MemberPagerActivity extends BaseActivity implements MemberDetailFragment.Callback, MemberGoogleFragment.Callback {

    private MemberData mMemberData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_pager_activity);

        Intent intent = getIntent();
        mMemberData = (MemberData) intent.getSerializableExtra("memberData");

        String locale = Util.getLocaleStrng(MemberPagerActivity.this);
        String title = mMemberData.getName();
        if (mMemberData.getTeamName() != null) {
            title = mMemberData.getTeamName() + " / " + title;
        }
        String name = null;
        if ("KR".equals(locale)) {
            name = mMemberData.getNameKo();
        } else if ("JP".equals(locale)) {
            name = mMemberData.getNameJa();
        } else if ("ID".equals(locale)) {
            name = mMemberData.getNameId();
        } else if ("CN".equals(locale)) {
            name = mMemberData.getNameCn();
        }
        if (name == null || name.isEmpty()) {
            name = mMemberData.getName();
        }
        title = name + " / " + title;

        initBaseToolbar(Config.TOOLBAR_ICON_BACK, title);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.profile)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.blog)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.yahoo)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.google)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.member_pager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onMemberDeatilAction(String action, MemberData memberData) {

    }

    @Override
    public void showToolbarLoading() {
        showToolbarProgressBar();
    }

    @Override
    public void hideToolbarLoading() {
        hideToolbarProgressBar();
    }

    @Override
    public void onError(String message) {

    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        int mNumOfTabs;

        public SectionsPagerAdapter(FragmentManager fm, int numOfTabs) {
            super(fm);
            this.mNumOfTabs = numOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MemberDetailFragment.newInstance(position, mMemberData);
                case 1:
                    return MemberBlogFragment.newInstance(position, mMemberData);
                case 2:
                    return MemberYahooFragment.newInstance(position, mMemberData);
                case 3:
                    return MemberGoogleFragment.newInstance(position, mMemberData);
                default:
                    return MemberYahooFragment.newInstance(position, mMemberData);
            }
        }

        @Override
        public int getCount() {
            return this.mNumOfTabs;
        }
    }
}
