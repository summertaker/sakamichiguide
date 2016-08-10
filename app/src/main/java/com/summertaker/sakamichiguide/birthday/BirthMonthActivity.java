package com.summertaker.sakamichiguide.birthday;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightGridView;
import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseActivity;
import com.summertaker.sakamichiguide.common.BaseApplication;
import com.summertaker.sakamichiguide.common.CacheManager;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.common.Setting;
import com.summertaker.sakamichiguide.data.BirthMonthData;
import com.summertaker.sakamichiguide.data.GroupData;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.parser.Pedia48Parser;
import com.summertaker.sakamichiguide.util.Util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BirthMonthActivity extends BaseActivity {

    boolean mShowOfficialPhoto;

    private ProgressBar mPbLoading;

    GroupData mGroupData;
    ArrayList<MemberData> mMemberDataList;
    ArrayList<BirthMonthData> mBirthMonthDatas;
    int mMaxPeople = 0;

    CacheManager mCacheManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.birth_month_activity);

        mContext = BirthMonthActivity.this;

        Setting setting = new Setting(mContext);
        mShowOfficialPhoto = setting.get(Config.SETTING_DISPLAY_OFFICIAL_PHOTO).equals(Config.SETTING_DISPLAY_OFFICIAL_PHOTO_YES);
        //Log.e(mTag, "mShowOfficialPhoto: " + mShowOfficialPhoto);

        Intent intent = getIntent();
        mGroupData = (GroupData) intent.getSerializableExtra("groupData");

        String title = getString(R.string.birthday);
        title += " / " + mGroupData.getName();
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, title);

        mPbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        Util.setProgressBarColor(mPbLoading, Config.PROGRESS_BAR_COLOR_NORMAL, null);

        mMemberDataList = new ArrayList<>();

        mCacheManager = new CacheManager(mSharedPreferences);

        String url = "";
        switch (mGroupData.getId()) {
            case Config.GROUP_ID_NOGIZAKA46:
                url = "http://48pedia.org/%E4%B9%83%E6%9C%A8%E5%9D%8246%E3%83%A1%E3%83%B3%E3%83%90%E3%83%BC%E4%B8%80%E8%A6%A7";
                break;
            case Config.GROUP_ID_KEYAKIZAKA46:
                url = "http://48pedia.org/%E6%AC%85%E5%9D%8246%E3%83%A1%E3%83%B3%E3%83%90%E3%83%BC%E4%B8%80%E8%A6%A7";
                break;
            default:
                url = "http://48pedia.org/" + mGroupData.getId() + "%E3%83%A1%E3%83%B3%E3%83%90%E3%83%BC%E4%B8%80%E8%A6%A7";
                break;
        }

        requestData(url, Config.USER_AGENT_WEB);
    }

    private void requestData(final String url, final String userAgent) {
        //Log.e(mTag, "url: " + url);
        //Log.e(mTag, "userAgent: " + userAgent);

        //final String cacheId = Util.urlToId(url);
        //String cacheData = mCacheManager.load(cacheId, 0);

        //if (cacheData == null) {
            StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Log.e(mTag, "response: " + response.substring(0, 100));
                    //mCacheManager.save(cacheId, response);
                    parseData(url, response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(mTag, "requestData().url: " + url);
                    //mSnackbar.setText(getString(R.string.network_error_occurred)).show();
                    mErrorMessage = Util.getErrorMessage(error);
                    alertAndFinish(mErrorMessage);
                    //parseData(url, "");
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    //headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("User-agent", userAgent);
                    return headers;
                }
            };

            String tag_string_req = "string_req";
            BaseApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
        //} else {
        //    parseData(url, cacheData);
        //}
    }

    private void parseData(String url, String response) {
        Pedia48Parser pedia48Parser = new Pedia48Parser();
        pedia48Parser.parseList(response, mGroupData, mMemberDataList);

        refineData();
    }

    private void refineData() {

        //ArrayList<String> monthList = new ArrayList<>();
        int[] counts = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        for (MemberData memberData : mMemberDataList) {
            String birthday = memberData.getBirthday();
            String month = birthday.substring(4, 6);
            //monthList.add(month);

            int monthNumber = Integer.parseInt(month) - 1;
            counts[monthNumber]++;
        }

        //Arrays.sort(counts);

        ArrayList<MemberData> memberDatas = new ArrayList<>();
        mBirthMonthDatas = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            //Log.e(mTag, i + ": " + counts[i]);
            //if (counts[i] == 0) {
            //    continue;
            //}

            int month = i + 1;
            String monthString = month + "";
            String monthText = (month < 10) ? "0" + month : month + "";

            memberDatas.clear();
            for (MemberData memberData : mMemberDataList) {
                String birthday = memberData.getBirthday();
                if (birthday.substring(4, 6).equals(monthText)) {
                    memberDatas.add(memberData);
                }
            }
            //Log.e(mTag, "memberDatas.size(): " + memberDatas.size());

            if (mMaxPeople < counts[i]) {
                mMaxPeople = counts[i];
            }

            BirthMonthData birthMonthData = new BirthMonthData();
            birthMonthData.setMonth(monthString);
            birthMonthData.setCount(counts[i]);
            if (memberDatas.size() > 0) {
                Collections.shuffle(memberDatas, new Random(System.nanoTime()));
                birthMonthData.setMemberData(memberDatas.get(0));
            }
            mBirthMonthDatas.add(birthMonthData);
        }

        mMaxPeople = mMaxPeople + (mMaxPeople / 7);

        mPbLoading.setVisibility(View.GONE);

        drawVoteChart();
    }

    private void drawVoteChart() {
        CardView cardView = (CardView) findViewById(R.id.cardViewChart);
        if (cardView != null) {
            cardView.setVisibility(View.VISIBLE);
        }

        HorizontalBarChart barChart = (HorizontalBarChart) findViewById(R.id.barChartVote);
        if (barChart == null) {
            return;
        }
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setDescription("");
        barChart.setNoDataTextDescription("You need to provide data for the chart.");
        barChart.setDragEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setExtraRightOffset(30f);
        //barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);

        Legend l = barChart.getLegend();
        l.setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setYOffset(10f);

        YAxis rightAxis = barChart.getAxisLeft();
        rightAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
        rightAxis.setAxisMaxValue(mMaxPeople);
        rightAxis.setValueFormatter(new voteYAxisValueFormatter());
        rightAxis.setYOffset(5f);

        ArrayList<String> xVals = new ArrayList<>();
        for (int i = mBirthMonthDatas.size() - 1; i >= 0; i--) {
            xVals.add((i + 1) + "월");
        }

        int index = 0;
        ArrayList<BarEntry> yVals = new ArrayList<>();
        for (int i = mBirthMonthDatas.size() - 1; i >= 0; i--) {
            int count = mBirthMonthDatas.get(i).getCount();
            yVals.add(new BarEntry(count, index++));
        }

        BarDataSet dataSet = new BarDataSet(yVals, "DataSet 1");
        dataSet.setColor(ContextCompat.getColor(mContext, R.color.light_salmon));
        dataSet.setBarSpacePercent(85f);
        dataSet.setHighLightAlpha(0);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(ContextCompat.getColor(mContext, R.color.sea_green));

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet); // add the datasets

        BarData lineData = new BarData(xVals, dataSets);
        lineData.setValueFormatter(new voteValueFormatter());

        barChart.setData(lineData);
        barChart.animateX(1000, Easing.EasingOption.EaseInOutQuart);

        renderData();
    }

    public class rankingValueFormatter implements ValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            DecimalFormat mFormat = new DecimalFormat("###,###"); // use one decimal
            return mFormat.format(value) + "위";
        }
    }

    // https://github.com/PhilJay/MPAndroidChart/wiki/The-YAxisValueFormatter-interface
    public class rankingYAxisValueFormatter implements YAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            DecimalFormat mFormat = new DecimalFormat("###,###"); // use one decimal
            if (value == 0f) {
                return "순위";
            } else {
                return mFormat.format(value) + "위";
            }
        }
    }

    public class voteValueFormatter implements ValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            DecimalFormat mFormat = new DecimalFormat("#,###,###"); // use one decimal
            return mFormat.format(value) + "명";
        }
    }

    // https://github.com/PhilJay/MPAndroidChart/wiki/The-YAxisValueFormatter-interface
    public class voteYAxisValueFormatter implements YAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            DecimalFormat mFormat = new DecimalFormat("#,###,###"); // use one decimal
            return mFormat.format(value) + "명";
        }
    }

    private void renderData() {
        BirthMonthAdapter dataAdapter = new BirthMonthAdapter(mContext, mBirthMonthDatas);

        /*ListView listView = (ListView) findViewById(R.id.listView);
        if (listView != null) {
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(dataAdapter);
            listView.setOnItemClickListener(onItemClickListener);
        }*/

        ExpandableHeightGridView gridView = (ExpandableHeightGridView) findViewById(R.id.gridView);
        if (gridView != null) {
            gridView.setExpanded(true);
            gridView.setAdapter(dataAdapter);
            gridView.setFocusable(false);
            gridView.setOnItemClickListener(onItemClick);
            gridView.setVisibility(View.VISIBLE);
        }
    }

    AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BirthMonthData birthMonthData = (BirthMonthData) parent.getItemAtPosition(position);

            if (birthMonthData.getCount() > 0) {
                Intent intent = new Intent(mContext, BirthDayActivity.class);
                intent.putExtra("groupData", mGroupData);
                intent.putExtra("birthMonthData", birthMonthData);
                intent.putExtra("memberDataList", mMemberDataList);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //hideToolbarProgressBar();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
