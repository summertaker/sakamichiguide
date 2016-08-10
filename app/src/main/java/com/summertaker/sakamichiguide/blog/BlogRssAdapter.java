package com.summertaker.sakamichiguide.blog;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseDataAdapter;
import com.summertaker.sakamichiguide.data.SiteData;
import com.summertaker.sakamichiguide.data.WebData;
import com.summertaker.sakamichiguide.util.Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class BlogRssAdapter extends BaseDataAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private SiteData mSiteData;
    private ArrayList<WebData> mWebDataList;

    private LinearLayout.LayoutParams mParams;
    private LinearLayout.LayoutParams mParamsNoMargin;

    //private BlogAdapterInterface mCallback;

    public BlogRssAdapter(Context context, SiteData siteData, ArrayList<WebData> webDataList) { //}, BlogAdapterInterface blogAdapterInterface) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);

        this.mSiteData = siteData;
        this.mWebDataList = webDataList;
        //this.mCallback = blogAdapterInterface;

        float density = mContext.getResources().getDisplayMetrics().density;
        int height = (int) (192 * density);
        int margin = (int) (1 * density);

        mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
        mParams.setMargins(0, 0, margin, 0);

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
            convertView = mLayoutInflater.inflate(R.layout.blog_rss_item, null);

            holder.loItem = (LinearLayout) convertView.findViewById(R.id.loItem);
            holder.loSvContainer = (RelativeLayout) convertView.findViewById(R.id.loSvContainer);
            holder.loPicture = (LinearLayout) convertView.findViewById(R.id.loPicture);
            holder.tvPictureCount = (TextView) convertView.findViewById(R.id.tvPictureCount);
            //holder.ivPicture = (ImageView) convertView.findViewById(R.id.ivPicture);
            holder.loContent = (LinearLayout) convertView.findViewById(R.id.loContent);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String imageUrl = webData.getThumbnailUrl();;
        //Log.e(mTag, "imageUrl: " + imageUrl);

        //holder.loPictureLoading.setVisibility(View.GONE);
        holder.loSvContainer.setVisibility(View.GONE);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            holder.loSvContainer.setVisibility(View.VISIBLE);
            holder.loPicture.removeAllViews();

            String[] imageArray = imageUrl.split("\\*");
            //Log.e(mTag, "array.length: " + array.length);

            String pictureCount = "x " + imageArray.length;
            holder.tvPictureCount.setText(pictureCount);

            for (int i = 0; i < imageArray.length; i++) {
                //Log.e(mTag, "url[" + i + "]: " + array[i]);
                String url = imageArray[i];
                if (url.isEmpty()) {
                    continue;
                }

                ImageView iv = new ImageView(mContext);
                if (i == imageArray.length - 1) {
                    iv.setLayoutParams(mParamsNoMargin);
                } else {
                    iv.setLayoutParams(mParams);
                }
                iv.setAdjustViewBounds(true);
                iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                holder.loPicture.addView(iv);

                Picasso.with(mContext).load(url).into(iv);
            }

            /*Picasso.with(mContext).load(imageUrl).noFade().into(holder.ivPicture, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    holder.ivPicture.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    holder.ivPicture.setVisibility(View.GONE);
                }
            });*/
        }

        // 제목
        String title = webData.getTitle();
        title = title.replace("  ", " ");
        holder.tvTitle.setText(title);

        // 날짜
        String pubDate = webData.getDate();
        //Log.e(mTag, "pubDate: " + pubDate);
        try {
            DateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
            Date date = sdf.parse(pubDate);
            String dateString = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(date);
            holder.tvDate.setText(dateString);
        } catch (ParseException e) {
            //e.printStackTrace();
            Log.e(mTag, "ERROR: " + e.getMessage());
            holder.tvDate.setText(pubDate);
        }

        // 내용
        String content = webData.getContent();
        content = Util.replaceJapaneseWhiteSpace(content);
        if (content.length() > 140) {
            content = content.substring(0, 140) + "...";
        }
        content = content.replace("  ", " ");
        content = content.replace("  ", " ");
        //content = content.replaceAll("\\s\\s", " ");
        //Log.e(mTag, content);
        holder.tvContent.setText(content);

        /*holder.loItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onPictureClick(position);
            }
        });*/

        return convertView;
    }

    static class ViewHolder {
        LinearLayout loItem;
        RelativeLayout loSvContainer;
        LinearLayout loPicture;
        TextView tvPictureCount;
        //ImageView ivPicture;
        LinearLayout loContent;
        TextView tvTitle;
        TextView tvDate;
        TextView tvContent;
    }
}

