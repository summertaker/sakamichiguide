package com.summertaker.sakamichiguide.member;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseDataAdapter;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.data.GroupData;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.data.TeamData;
import com.summertaker.sakamichiguide.util.Translator;
import com.summertaker.sakamichiguide.util.Util;

import java.util.ArrayList;

public class TeamGridAdapter extends BaseDataAdapter {
    private String mTag;

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private GroupData mGroupData;
    private ArrayList<TeamData> mDataList = null;

    private Translator mTranslator;

    public TeamGridAdapter(Context context, GroupData groupData, ArrayList<TeamData> dataList) {
        this.mTag = "===== " + this.getClass().getSimpleName();
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mGroupData = groupData;
        this.mDataList = dataList;

        mTranslator = new Translator(context);
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
        final TeamData teamData = mDataList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.team_grid_item, null);

            holder.loLoading = (RelativeLayout) convertView.findViewById(R.id.loLoading);
            holder.pbLoading = (ProgressBar) convertView.findViewById(R.id.pbLoading);
            Util.setProgressBarColor(holder.pbLoading, 0, null);

            holder.ivPicture = (ImageView) convertView.findViewById(R.id.ivPicture);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            //holder.tvCount = (TextView) convertView.findViewById(R.id.tvCount);

            switch (teamData.getGroupId()) {
                case Config.GROUP_ID_KEYAKIZAKA46:
                    holder.tvName.setBackgroundColor(ContextCompat.getColor(mContext, R.color.keyakizaka48background));
                    holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.keyakizaka48text));
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MemberData memberData = teamData.getMemberData();
        // http://cdn.akb48.co.jp/cache/image/?path=%2Fmembers%2Fprofile20150511%2Fteam_B_png%2Fogasawara_mayu.png
        String imageUrl = memberData.getThumbnailUrl();
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

        //String name = memberData.getNameJa();
        //if (mLocale.equals("EN") && memberData.getNameEn() != null && !memberData.getNameEn().isEmpty()) {
        //    name = memberData.getNameEn();
        //}
        String name = teamData.getName();
        name = mTranslator.translateTeam(teamData.getGroupId(), name);
        holder.tvName.setText(name);

        /*int count = teamData.getMemberCount();
        String text = String.format(mResources.getString(R.string.s_people), count);
        text = " (" + text + ")";
        holder.tvCount.setText(text);*/

        return convertView;
    }

    static class ViewHolder {
        RelativeLayout loLoading;
        ProgressBar pbLoading;
        ImageView ivPicture;
        TextView tvName;
        TextView tvCount;
    }
}
