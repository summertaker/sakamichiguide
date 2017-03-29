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
import com.summertaker.sakamichiguide.util.Util;

import java.util.ArrayList;

public class MemberGridAdapter extends BaseDataAdapter {
    private String mTag;
    private Context mContext;
    //private Resources mResources;
    //private String mLocale;
    private LayoutInflater mLayoutInflater;
    private GroupData mGroupData;
    private ArrayList<MemberData> mDataList = null;

    private String mGeneralCaptain;
    private String mCaptain;
    private String mViceCaptain;

    public MemberGridAdapter(Context context, GroupData groupData, ArrayList<MemberData> dataList) {
        this.mTag = "===== " + this.getClass().getSimpleName();
        this.mContext = context;
        //this.mResources = mContext.getResources();
        //this.mLocale = Util.getLocaleStrng(context);
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mGroupData = groupData;
        this.mDataList = dataList;
        this.mCaptain = context.getResources().getString(R.string.captain);
        this.mViceCaptain = context.getResources().getString(R.string.vice_captain);
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
        final MemberData memberData = mDataList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.member_grid_item, null);

            holder.loLoading = (RelativeLayout) convertView.findViewById(R.id.loLoading);
            holder.pbLoading = (ProgressBar) convertView.findViewById(R.id.pbLoading);
            Util.setProgressBarColor(holder.pbLoading, 0, null);

            holder.ivPicture = (ImageView) convertView.findViewById(R.id.ivPicture);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            //holder.tvCount = (TextView) convertView.findViewById(R.id.tvCount);
            holder.tvGeneralManager = (TextView) convertView.findViewById(R.id.tvGeneralManager);
            holder.tvGeneralCaptain = (TextView) convertView.findViewById(R.id.tvGeneralCaptain);
            holder.tvManager = (TextView) convertView.findViewById(R.id.tvManager);
            holder.tvCaptain = (TextView) convertView.findViewById(R.id.tvCaptain);
            holder.tvViceCaptain = (TextView) convertView.findViewById(R.id.tvViceCaptain);
            holder.tvConcurrentPosition = (TextView) convertView.findViewById(R.id.tvConcurrentPosition);

            switch (memberData.getGroupId()) {
                case Config.GROUP_ID_NOGIZAKA46:
                    holder.tvName.setBackgroundColor(ContextCompat.getColor(mContext, R.color.nogizaka48background));
                    holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.nogizaka48text));
                    break;
                case Config.GROUP_ID_KEYAKIZAKA46:
                    holder.tvName.setBackgroundColor(ContextCompat.getColor(mContext, R.color.keyakizaka48background));
                    holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.keyakizaka48text));
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //MemberData memberData = teamData.getMemberData();
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

        String localeName = memberData.getLocaleName();
        if (localeName == null || localeName.isEmpty()) {
            localeName = memberData.getName();
        }
        holder.tvName.setText(localeName);
        holder.tvName.setVisibility(View.VISIBLE);

        /*int count = teamData.getMemberCount();
        String text = String.format(mResources.getString(R.string.s_people), count);
        text = " (" + text + ")";
        holder.tvCount.setText(text);*/

        // 총감독
        if (memberData.isGeneralManager()) {
            holder.tvGeneralManager.setVisibility(View.VISIBLE);
        } else {
            holder.tvGeneralManager.setVisibility(View.GONE);
        }

        // 지배인
        if (memberData.isManager()) {
            holder.tvManager.setVisibility(View.VISIBLE);
        } else {
            holder.tvManager.setVisibility(View.GONE);
        }

        // 부캡틴,부리더
        if (memberData.isViceCaptain()) {
            holder.tvViceCaptain.setText(mViceCaptain);
            holder.tvViceCaptain.setVisibility(View.VISIBLE);
        } else {
            holder.tvViceCaptain.setVisibility(View.GONE);
        }

        // 그룹 캡틴
        if (memberData.isGeneralCaptain()) {
            holder.tvGeneralCaptain.setText(mGeneralCaptain);
            holder.tvGeneralCaptain.setVisibility(View.VISIBLE);
        } else {
            holder.tvGeneralCaptain.setVisibility(View.GONE);
        }

        // 캡틴,리더
        if (memberData.isCaptain()) {
            holder.tvCaptain.setText(mCaptain);
            holder.tvCaptain.setVisibility(View.VISIBLE);
        } else {
            holder.tvCaptain.setVisibility(View.GONE);
        }

        // 겸임
        if (memberData.isConcurrentPosition()) {
            holder.tvConcurrentPosition.setVisibility(View.VISIBLE);
        } else {
            holder.tvConcurrentPosition.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder {
        RelativeLayout loLoading;
        ProgressBar pbLoading;
        ImageView ivPicture;
        TextView tvName;
        TextView tvCount;
        TextView tvGeneralManager;
        TextView tvGeneralCaptain;
        TextView tvManager;
        TextView tvCaptain;
        TextView tvViceCaptain;
        TextView tvConcurrentPosition;
    }
}
