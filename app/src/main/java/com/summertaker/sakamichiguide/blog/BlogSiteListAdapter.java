package com.summertaker.sakamichiguide.blog;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseDataAdapter;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.data.SiteData;
import com.summertaker.sakamichiguide.util.Util;

import java.util.ArrayList;

public class BlogSiteListAdapter extends BaseDataAdapter {
    private String mTag;

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<SiteData> mDataList = null;
    //private Translator mTranslator;

    public BlogSiteListAdapter(Context context, ArrayList<SiteData> dataList) {
        this.mTag = "===== " + this.getClass().getSimpleName();
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mDataList = dataList;
        //mTranslator = new Translator(context);
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

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final SiteData siteData = mDataList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.blog_site_list_item, null);

            holder.loLoading = (RelativeLayout) convertView.findViewById(R.id.loLoading);
            holder.pbLoading = (ProgressBar) convertView.findViewById(R.id.pbLoading);
            Util.setProgressBarColor(holder.pbLoading, 0, null);

            holder.ivPicture = (ImageView) convertView.findViewById(R.id.ivPicture);
            holder.tvNew = (TextView) convertView.findViewById(R.id.tvNew);
            holder.loCaption = (LinearLayout) convertView.findViewById(R.id.loCaption);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvOldDate = (TextView) convertView.findViewById(R.id.tvUpdateCheckDate);
            holder.tvNewDate = (TextView) convertView.findViewById(R.id.tvUpdateDate);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // http://cdn.akb48.co.jp/cache/image/?path=%2Fmembers%2Fprofile20150511%2Fteam_B_png%2Fogasawara_mayu.png
        String imageUrl = siteData.getImageUrl();
        //Log.e(mTag, "imageUrl: " + imageUrl);

        if (imageUrl == null || imageUrl.isEmpty()) {
            holder.loLoading.setVisibility(View.GONE);
            //holder.ivPicture.setImageResource(R.drawable.anonymous);
        } else {
            Picasso.with(mContext).load(imageUrl).into(holder.ivPicture, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    holder.loLoading.setVisibility(View.GONE);
                    holder.ivPicture.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    holder.loLoading.setVisibility(View.GONE);
                }
            });
        }

        /*
        String updateDate = siteData.getUpdateDate();
        if (updateDate == null) {
            updateDate = "";
        }
        holder.tvNewDate.setText(updateDate);
        holder.tvNewDate.setVisibility(View.VISIBLE);

        String updateCheckDate = siteData.getUpdateCheckDate();
        if (updateCheckDate == null) {
            updateCheckDate = "";
        }
        holder.tvOldDate.setText(updateCheckDate);
        holder.tvOldDate.setVisibility(View.VISIBLE);
        */

        holder.tvNew.setVisibility(View.GONE);
        if (siteData.isUpdated()) {
            //Log.e(mTag, siteData.getName() + " updated....");
            holder.tvNew.setVisibility(View.VISIBLE);
        }

        if (siteData.getUpdateCheckDate() == null || siteData.getUpdateCheckDate().isEmpty()) {
            holder.loCaption.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray_transparent));
        } else {
            switch (siteData.getGroupId()) {
                case Config.GROUP_ID_NOGIZAKA46:
                    holder.loCaption.setBackgroundColor(ContextCompat.getColor(mContext, R.color.nogizaka48background));
                    //holder.tvName.setBackgroundColor(ContextCompat.getColor(mContext, R.color.nogizaka48background));
                    //holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.nogizaka48text));
                    break;
                case Config.GROUP_ID_KEYAKIZAKA46:
                    holder.loCaption.setBackgroundColor(ContextCompat.getColor(mContext, R.color.keyakizaka48background));
                    //holder.tvName.setBackgroundColor(ContextCompat.getColor(mContext, R.color.keyakizaka48background));
                    //holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.keyakizaka48text));
                    break;
            }
        }

        String name = siteData.getName();
        holder.tvName.setText(name);

        return convertView;
    }

    static class ViewHolder {
        RelativeLayout loLoading;
        ProgressBar pbLoading;
        ImageView ivPicture;
        TextView tvNew;

        LinearLayout loCaption;
        TextView tvName;
        TextView tvOldDate;
        TextView tvNewDate;
    }
}
