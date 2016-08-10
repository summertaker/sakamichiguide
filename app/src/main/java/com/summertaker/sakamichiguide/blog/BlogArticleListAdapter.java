package com.summertaker.sakamichiguide.blog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseDataAdapter;
import com.summertaker.sakamichiguide.data.SiteData;
import com.summertaker.sakamichiguide.data.WebData;
import com.summertaker.sakamichiguide.util.ProportionalImageView;
import com.summertaker.sakamichiguide.util.Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BlogArticleListAdapter extends BaseDataAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private SiteData mSiteData;
    private ArrayList<WebData> mWebDataList;

    private LinearLayout.LayoutParams mParams;
    private LinearLayout.LayoutParams mParamsNoMargin;

    //private BlogAdapterInterface mCallback;

    public BlogArticleListAdapter(Context context, SiteData siteData, ArrayList<WebData> webDataList) { //}, BlogAdapterInterface blogAdapterInterface) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);

        this.mSiteData = siteData;
        this.mWebDataList = webDataList;

        //this.mCallback = blogAdapterInterface;

        float density = mContext.getResources().getDisplayMetrics().density;
        int height = (int) (272 * density);
        int margin = (int) (1 * density);

        mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
        mParams.setMargins(0, margin, 0, 0);

        mParamsNoMargin = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
    }

    @Override
    public int getCount() {
        return mWebDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mWebDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        WebData webData = mWebDataList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.blog_article_list_item, null);

            holder.loPicture = (LinearLayout) convertView.findViewById(R.id.loPicture);
            holder.loContent = (LinearLayout) convertView.findViewById(R.id.loContent);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            holder.tvToday = (TextView) convertView.findViewById(R.id.tvToday);
            holder.tvYesterday = (TextView) convertView.findViewById(R.id.tvYesterday);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String imageUrl = webData.getThumbnailUrl();
        //Log.e(mTag, "imageUrl: " + imageUrl);

        if (imageUrl == null || imageUrl.isEmpty()) {
            holder.loPicture.setVisibility(View.GONE);
        } else {
            holder.loPicture.removeAllViews();
            holder.loPicture.setVisibility(View.VISIBLE);

            String[] imageArray = imageUrl.split("\\*");
            for (int i = 0; i < imageArray.length; i++) {
                //Log.e(mTag, "url[" + i + "]: " + imageArray[i]);
                String url = imageArray[i];
                if (url.isEmpty()) {
                    continue;
                }

                final ProportionalImageView iv = new ProportionalImageView(mContext);
                //if (i == imageArray.length - 1) {
                if (i == 0) {
                    iv.setLayoutParams(mParamsNoMargin);
                } else {
                    iv.setLayoutParams(mParams);
                }
                //iv.setAdjustViewBounds(true);
                //iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.loPicture.addView(iv);

                Picasso.with(mContext).load(url).into(iv);
                /*Picasso.with(mContext).load(url).into(iv, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        holder.loPicture.addView(iv);
                    }

                    @Override
                    public void onError() {

                    }
                });*/
            }
        }

        // 제목
        String title = webData.getTitle();
        //Log.e(mTag, "title: " + title);
        if (title == null || title.isEmpty()) {
            holder.tvTitle.setVisibility(View.GONE);
        } else {
            title = title.replace("  ", " ");
            holder.tvTitle.setVisibility(View.VISIBLE);
            holder.tvTitle.setText(title);
        }

        // 이름
        String name = webData.getName();
        if (name == null || name.isEmpty()) {
            holder.tvName.setVisibility(View.GONE);
        } else {
            holder.tvName.setVisibility(View.VISIBLE);
            holder.tvName.setText(name);
        }

        // 날짜
        String pubDate = webData.getDate();
        if (pubDate == null || pubDate.isEmpty()) {
            holder.tvDate.setVisibility(View.GONE);
        } else {
            //Log.e(mTag, "date: " + date);
            holder.tvDate.setVisibility(View.VISIBLE);

            Date date = null;
            try {
                DateFormat sdf = null;
                if (pubDate.contains("+")) {
                    sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                    date = sdf.parse(pubDate);
                    pubDate = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(date);
                } else if (pubDate.contains("/")) {
                    sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);
                    date = sdf.parse(pubDate);
                    pubDate = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(date);
                } else if (pubDate.contains("-")) {
                    sdf = new SimpleDateFormat("yyyy-MM-dd E", Locale.ENGLISH);
                    date = sdf.parse(pubDate);
                    pubDate = DateFormat.getDateInstance(DateFormat.FULL).format(date);
                } else if (pubDate.contains(".") && pubDate.length() <= 10) {
                    sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
                    date = sdf.parse(pubDate);
                    pubDate = DateFormat.getDateInstance(DateFormat.FULL).format(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.tvDate.setText(pubDate);

            holder.tvToday.setVisibility(View.GONE);
            holder.tvYesterday.setVisibility(View.GONE);
            if (date != null) {
                Date today = new Date();
                if (Util.isSameDay(today, date)) {
                    holder.tvToday.setVisibility(View.VISIBLE);
                }

                Calendar c = Calendar.getInstance();
                c.setTime(today);
                c.add(Calendar.DATE, -1);
                Date yesterday = c.getTime();
                if (Util.isSameDay(yesterday, date)) {
                    holder.tvYesterday.setVisibility(View.VISIBLE);
                }
            }
        }

        // 내용
        String content = webData.getContent();
        if (content == null || content.isEmpty()) {
            holder.tvContent.setVisibility(View.GONE);
        } else {
            content = Util.replaceJapaneseWhiteSpace(content);
            content = content.replace("&nbsp;", " ");
            content = content.replace("  ", " ");
            content = content.replace("  ", " ");
            if (content.length() > 140) {
                content = content.substring(0, 140) + "...";
            }
            //content = content.replaceAll("\\s\\s", " ");
            //Log.e(mTag, content);

            holder.tvContent.setVisibility(View.VISIBLE);
            holder.tvContent.setText(content);
        }

        /*holder.loItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onPictureClick(position);
            }
        });*/

        return convertView;
    }

    static class ViewHolder {
        LinearLayout loPicture;
        LinearLayout loContent;
        TextView tvTitle;
        TextView tvToday;
        TextView tvYesterday;
        TextView tvName;
        TextView tvDate;
        TextView tvContent;
    }
}
