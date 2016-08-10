package com.summertaker.sakamichiguide.quiz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseActivity;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.data.GroupData;
import com.summertaker.sakamichiguide.util.Util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

public class QuizResultActivity extends BaseActivity {

    GroupData mGroupData;
    int mMemberCount = 0;
    int mValidCount = 0;

    private String mPeople = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_result_activity);

        Intent intent = getIntent();
        mGroupData = (GroupData) intent.getSerializableExtra("groupData");
        mMemberCount = intent.getIntExtra("memberCount", 30);
        mValidCount = intent.getIntExtra("validCount", 10);

        initBaseToolbar(Config.TOOLBAR_ICON_BACK, getString(R.string.quiz_result));

        mPeople = getString(R.string.people);
        if (Util.isEnglishLocale(mContext)) {
            mPeople = " " + mPeople;
        }

        String title = mGroupData.getName();
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(title);

        Date now = new Date();
        String dateTime = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(now);
        TextView tvSubtitle = (TextView) findViewById(R.id.tvSubtitle);
        tvSubtitle.setText(dateTime);

        String info = mMemberCount + mPeople + " - " + (mMemberCount - mValidCount) + mPeople + " = " + mValidCount + mPeople;
        TextView tvInfo = (TextView) findViewById(R.id.tvInfo);
        tvInfo.setText(info);

//        Button btFinish = (Button) findViewById(R.id.btFinish);
//        btFinish.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                doFinish();
//            }
//        });

        //mValidCount = mMemberCount;

        if (mValidCount == 0) {
            ImageView ivPerfect = (ImageView) findViewById(R.id.ivZero);
            ivPerfect.setVisibility(View.VISIBLE);
        } else if (mMemberCount == mValidCount) {
            ImageView ivPerfect = (ImageView) findViewById(R.id.ivHundred);
            ivPerfect.setVisibility(View.VISIBLE);
        } else {
            PieChart pieChart = (PieChart) findViewById(R.id.pieChart1);
            pieChart.setVisibility(View.VISIBLE);
            pieChart.setDescription("");

            pieChart.setCenterText(generateCenterText());
            pieChart.setCenterTextSize(13f);

            // radius of the center hole in percent of maximum radius
            pieChart.setHoleRadius(40f);
            pieChart.setTransparentCircleRadius(50f);

            //Paint p1 = mChart.getPaint(Chart.PAINT_HOLE);
            //p1.setColor(ContextCompat.getColor(mContext, R.color.primary));
            //Paint p2 = mChart.getPaint(Chart.PAINT_CENTER_TEXT);
            //p2.setColor(ContextCompat.getColor(mContext, R.color.primary_dark));

            Legend l = pieChart.getLegend();
            //l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            l.setEnabled(false); // Hide Legend

            pieChart.setData(generatePieData());
        }
    }

    private SpannableString generateCenterText() {
        //String locale = Util.getLocaleStrng(mContext);
        //Log.e(mTag, "locale: " + locale);

        String str = getString(R.string.total) + "\n" + mMemberCount + mPeople;
        SpannableString s = new SpannableString(str);
        //SpannableString s = new SpannableString("Total\nResult");
        //s.setSpan(new RelativeSizeSpan(2f), 0, 5, 0); // 첫줄 글자 크기
        //s.setSpan(new RelativeSizeSpan(2f), 5, s.length(), 0); // 둘째줄 글자 크기
        //s.setSpan(new ForegroundColorSpan(Color.GRAY), 5, s.length(), 0);
        return s;
    }

    private PieData generatePieData() {
        ArrayList<Entry> entries1 = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();

        //int count = 2;
        //xVals.add("정답");
        //xVals.add("오답");

        xVals.add("(" + getString(R.string.correct) + ")");
        entries1.add(new Entry(mValidCount, 0));
        xVals.add("(" + getString(R.string.incorrect) + ")");
        entries1.add(new Entry(mMemberCount - mValidCount, 1));

        //for(int i = 0; i < count; i++) {
        //    xVals.add("entry" + (i+1));
        //    entries1.add(new Entry((float) (Math.random() * 60) + 40, i));
        //}

        PieDataSet ds1 = new PieDataSet(entries1, getString(R.string.quiz_result));
        ds1.setColors(ColorTemplate.JOYFUL_COLORS);
        ds1.setSliceSpace(2f);
        ds1.setValueTextColor(Color.WHITE);
        ds1.setValueTextSize(13f);

        PieData d = new PieData(xVals, ds1);

        // usage on individual dataset object
        //ds1.setValueFormatter(new MyValueFormatter());
        // usage on whole data object
        d.setValueFormatter(new MyValueFormatter());

        return d;
    }

    // https://github.com/PhilJay/MPAndroidChart/wiki/The-ValueFormatter-interface
    public class MyValueFormatter implements ValueFormatter {
        private DecimalFormat mFormat;

        public MyValueFormatter() {
            //mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
            mFormat = new DecimalFormat("###,###"); // use one decimal
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            // write your logic here
            //return mFormat.format(value) + " $"; // e.g. append a dollar-sign
            return mFormat.format(value) + mPeople; // e.g. append a dollar-sign
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Intent intent = new Intent();
        //intent.putExtra("pictureId", mData.getGroupId());
        setResult(Config.RESULT_CODE_FINISH, intent);
    }
}
