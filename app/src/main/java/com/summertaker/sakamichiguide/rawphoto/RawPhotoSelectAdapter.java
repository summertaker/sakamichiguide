package com.summertaker.sakamichiguide.rawphoto;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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
import com.summertaker.sakamichiguide.data.GroupData;
import com.summertaker.sakamichiguide.data.WebData;
import com.summertaker.sakamichiguide.util.Util;

import java.util.ArrayList;

public class RawPhotoSelectAdapter extends BaseDataAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private GroupData mGroupData;
    private ArrayList<WebData> mDataList = null;
    //private Translator mTranslator;

    public RawPhotoSelectAdapter(Context context, GroupData groupData, ArrayList<WebData> dataList) {
        this.mTag = "===== " + this.getClass().getSimpleName();
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mGroupData = groupData;
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
        final WebData webData = mDataList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.raw_photo_select_item, null);

            holder.loLoading = (RelativeLayout) convertView.findViewById(R.id.loLoading);
            holder.pbLoading = (ProgressBar) convertView.findViewById(R.id.pbLoading);
            Util.setProgressBarColor(holder.pbLoading, 0, null);

            holder.ivPicture = (ImageView) convertView.findViewById(R.id.ivPicture);
            holder.loCaption = (LinearLayout) convertView.findViewById(R.id.loCaption);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);

            switch (mGroupData.getId()) {
                case Config.GROUP_ID_NOGIZAKA46:
                    holder.loCaption.setBackgroundColor(ContextCompat.getColor(mContext, R.color.nogizaka48background));
                    holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.nogizaka48text));
                    break;
                case Config.GROUP_ID_KEYAKIZAKA46:
                    holder.loCaption.setBackgroundColor(ContextCompat.getColor(mContext, R.color.keyakizaka48background));
                    holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.keyakizaka48text));
                    break;
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // http://cdn.akb48.co.jp/cache/image/?path=%2Fmembers%2Fprofile20150511%2Fteam_B_png%2Fogasawara_mayu.png
        String imageUrl = webData.getImageUrl();
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

        String name = webData.getName();
        holder.tvName.setText(name);

        return convertView;
    }

    static class ViewHolder {
        RelativeLayout loLoading;
        ProgressBar pbLoading;

        ImageView ivPicture;
        LinearLayout loCaption;
        TextView tvName;
    }
}
