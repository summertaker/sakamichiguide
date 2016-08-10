package com.summertaker.sakamichiguide.birthday;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseDataAdapter;
import com.summertaker.sakamichiguide.data.BirthDayData;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.util.Util;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BirthDayAdapter extends BaseDataAdapter {

    private Context mContext;
    private Resources mResources;
    private LayoutInflater mLayoutInflater;
    private Locale mLocale;

    ArrayList<BirthDayData> mDataList = new ArrayList<>();

    private boolean mShowOfficialPhoto;

    public BirthDayAdapter(Context context, ArrayList<BirthDayData> dataList, boolean showOfficialPhoto) {
        this.mContext = context;
        this.mResources = mContext.getResources();
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mLocale = Util.getLocale(mContext);

        this.mDataList = dataList;
        this.mShowOfficialPhoto = showOfficialPhoto;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        BirthDayData birthDayData = mDataList.get(position);
        MemberData memberData = birthDayData.getMemberData();

        if (view == null) {
            holder = new ViewHolder();

            if (mShowOfficialPhoto) {
                view = mLayoutInflater.inflate(R.layout.birth_day_item, null);

                holder.pbPictureLoading = (ProgressBar) view.findViewById(R.id.pbPictureLoading);
                Util.setProgressBarColor(holder.pbPictureLoading, 0, null);
                holder.ivPicture = (ImageView) view.findViewById(R.id.ivPicture);
            } else {
                view = mLayoutInflater.inflate(R.layout.birth_day_item_text, null);
            }

            holder.tvDate = (TextView) view.findViewById(R.id.tvDate);
            holder.tvDateSuffix = (TextView) view.findViewById(R.id.tvDateSuffix);
            holder.tvDay = (TextView) view.findViewById(R.id.tvDay);
            holder.tvYesterday = (TextView) view.findViewById(R.id.tvYesterday);
            holder.tvToday = (TextView) view.findViewById(R.id.tvToday);
            holder.tvTomorrow = (TextView) view.findViewById(R.id.tvTomorrow);

            holder.tvName = (TextView) view.findViewById(R.id.tvName);
            holder.tvTeam = (TextView) view.findViewById(R.id.tvTeam);
            holder.tvBirthday = (TextView) view.findViewById(R.id.tvBirthday);
            holder.tvAge = (TextView) view.findViewById(R.id.tvAge);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        String imageUrl = memberData.getThumbnailUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            holder.pbPictureLoading.setVisibility(View.GONE);
            holder.ivPicture.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(R.drawable.anonymous).into(holder.ivPicture);
        } else {
            Picasso.with(mContext).load(imageUrl).into(holder.ivPicture, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    holder.pbPictureLoading.setVisibility(View.GONE);
                    holder.ivPicture.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    holder.pbPictureLoading.setVisibility(View.GONE);
                }
            });
        }

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int today = cal.get(Calendar.DATE);
        int day = birthDayData.getDay();
        //Log.e(mTag, "month: " + month + " / today: " + today + " / day: " + day);

        String dayText = day + "";
        holder.tvDate.setText(dayText);
        //if (mLocale.equals(Locale.KOREA) || mLocale.equals(Locale.JAPAN) || mLocale.equals(Locale.CHINA)) {
        //    holder.tvDateSuffix.setVisibility(View.VISIBLE);
        //}

        // 요일
        int birthMonth = Integer.parseInt(memberData.getBirthday().substring(4, 6));
        int birthDate = Integer.parseInt(memberData.getBirthday().substring(6, 8));
        Calendar weekCal = Calendar.getInstance();
        weekCal.set(Calendar.YEAR, year);
        weekCal.set(Calendar.MONTH, birthMonth - 1);
        weekCal.set(Calendar.DATE, birthDate);

        int dayOfWeek = weekCal.get(Calendar.DAY_OF_WEEK);
        DateFormatSymbols dfs = new DateFormatSymbols(mLocale);
        String weekdays[] = dfs.getWeekdays(); // Long
        //String weekdays[] = dfs.getShortWeekdays(); // Short
        String weekday = weekdays[dayOfWeek];
        holder.tvDay.setText(weekday);
        //Log.e(mTag, "dayOfWeek: " + dayOfWeek);
        switch (dayOfWeek) {
            case 1:
                holder.tvDate.setTextColor(Color.parseColor("#ef5350"));
                holder.tvDay.setTextColor(Color.parseColor("#ef5350"));
                break;
            case 7:
                holder.tvDate.setTextColor(Color.parseColor("#42a5f5"));
                holder.tvDay.setTextColor(Color.parseColor("#42a5f5"));
                break;
            default:
                holder.tvDate.setTextColor(Color.parseColor("#757575"));
                holder.tvDay.setTextColor(Color.parseColor("#757575"));
                break;
        }

        holder.tvYesterday.setVisibility(View.GONE);
        holder.tvToday.setVisibility(View.GONE);
        holder.tvTomorrow.setVisibility(View.GONE);
        int realMonth = month + 1;
        if (realMonth == birthDayData.getMonth()) {
            if (day - today == -1) {
                holder.tvYesterday.setVisibility(View.VISIBLE);
            } else if (today == day) {
                holder.tvToday.setVisibility(View.VISIBLE);
            } else if (day - today == 1) {
                holder.tvTomorrow.setVisibility(View.VISIBLE);
            }
        }

        String name = memberData.getName();
        holder.tvName.setText(name);
        holder.tvName.setVisibility(View.VISIBLE);

        // 팀
        //String team = String.format(mResources.getString(R.string.team_s), memberData.getTeamName());
        //holder.tvTeam.setText(team);
        //holder.tvTeam.setVisibility(View.VISIBLE);

        try {
            DateFormat sdf = new SimpleDateFormat("yyyyMMdd", mLocale);
            Date date = sdf.parse(memberData.getBirthday());
            String birthdayText = DateFormat.getDateInstance(DateFormat.LONG).format(date); // 2001년 5월 23일
            holder.tvBirthday.setText(birthdayText);

            Calendar birthdayCal = Calendar.getInstance();
            birthdayCal.setTime(date);
            int age = cal.get(Calendar.YEAR) - birthdayCal.get(Calendar.YEAR);
            if (cal.get(Calendar.DAY_OF_YEAR) < birthdayCal.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
            String ageText = String.format(mResources.getString(R.string.s_years_old), age);
            holder.tvAge.setText(ageText);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /*
        String dayOfTheWeek = (String) android.text.format.DateFormat.format("EEEE", date);//Thursday
        String stringMonth = (String) android.text.format.DateFormat.format("MMM", date); //Jun
        String intMonth = (String) android.text.format.DateFormat.format("MM", date); //06
        String year = (String) android.text.format.DateFormat.format("yyyy", date); //2013
        String day = (String) android.text.format.DateFormat.format("dd", date); //20
        */

        //String monthString = (String) android.text.format.DateFormat.format("MMM", date);
        //holder.tvYearMonth.setText(monthString);

        return view;
    }

    static class ViewHolder {
        ProgressBar pbPictureLoading;
        ImageView ivPicture;

        TextView tvDate;
        TextView tvDateSuffix;
        TextView tvDay;
        TextView tvYesterday;
        TextView tvToday;
        TextView tvTomorrow;

        TextView tvName;
        TextView tvTeam;
        TextView tvBirthday;
        TextView tvAge;
    }
}
