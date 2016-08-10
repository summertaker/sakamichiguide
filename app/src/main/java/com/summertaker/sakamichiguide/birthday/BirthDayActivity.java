package com.summertaker.sakamichiguide.birthday;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseActivity;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.common.Setting;
import com.summertaker.sakamichiguide.data.BirthDayData;
import com.summertaker.sakamichiguide.data.BirthMonthData;
import com.summertaker.sakamichiguide.data.GroupData;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.util.Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class BirthDayActivity extends BaseActivity {

    boolean mShowOfficialPhoto;

    private ProgressBar mPbLoading;

    GroupData mGroupData;
    BirthMonthData mBirthMonthData;
    ArrayList<MemberData> mMemberDataList;
    ArrayList<BirthDayData> mBirthDayDataList;

    BirthDayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.birth_day_activity);

        mContext = BirthDayActivity.this;

        Setting setting = new Setting(mContext);
        mShowOfficialPhoto = setting.get(Config.SETTING_DISPLAY_OFFICIAL_PHOTO).equals(Config.SETTING_DISPLAY_OFFICIAL_PHOTO_YES);
        //Log.e(mTag, "mShowOfficialPhoto: " + mShowOfficialPhoto);

        mPbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        Util.setProgressBarColor(mPbLoading, Config.PROGRESS_BAR_COLOR_NORMAL, null);

        Intent intent = getIntent();
        mGroupData = (GroupData)  intent.getSerializableExtra("groupData");
        mBirthMonthData = (BirthMonthData) intent.getSerializableExtra("birthMonthData");

        if (mBirthMonthData.getCount() == 0) {
            mPbLoading.setVisibility(View.GONE);
        } else {
            ArrayList<MemberData> memberDataList = (ArrayList<MemberData>) intent.getSerializableExtra("memberDataList");
            //Log.e(mTag, "memberDataList.size(): " + memberDataList.size());

            int month = Integer.parseInt(mBirthMonthData.getMonth());
            mBirthDayDataList = new ArrayList<>();
            for (MemberData memberData : memberDataList) {
                int birthMonth = Integer.parseInt(memberData.getBirthday().substring(4, 6));
                //Log.e(mTag, month + " / " + birthMonth);
                if (month == birthMonth) {
                    int day = Integer.parseInt(memberData.getBirthday().substring(6, 8));
                    BirthDayData birthDayData = new BirthDayData();
                    birthDayData.setMonth(month);
                    birthDayData.setDay(day);
                    birthDayData.setMemberData(memberData);
                    mBirthDayDataList.add(birthDayData);
                }
            }

            Collections.sort(mBirthDayDataList);

            String monthString = "";
            try {
                DateFormat sdf = new SimpleDateFormat("yyyyMMdd", Util.getLocale(mContext));
                Date date = sdf.parse(mBirthDayDataList.get(0).getMemberData().getBirthday());
                monthString = (String) android.text.format.DateFormat.format("MMM", date); //Jun
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String title = getString(R.string.birthday) + " / " + monthString + " / " + mGroupData.getName();
            initBaseToolbar(Config.TOOLBAR_ICON_BACK, title);

            mAdapter = new BirthDayAdapter(mContext, mBirthDayDataList, mShowOfficialPhoto);
            mPbLoading.setVisibility(View.GONE);

            renderList();
            //renderGrid();
        }
    }

    private void renderList() {
        ListView listView = (ListView) findViewById(R.id.listView);
        if (listView != null) {
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(mAdapter);
        }
    }

    private void renderGrid() {
        GridView gridView = (GridView) findViewById(R.id.gridView);
        if (gridView != null) {
            gridView.setVisibility(View.VISIBLE);
            gridView.setAdapter(mAdapter);
        }
    }
}
