package com.summertaker.sakamichiguide.member;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseDataAdapter;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.data.GroupData;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.util.Util;

import java.util.ArrayList;

public class MemberListAdapter extends BaseDataAdapter {

    private Context mContext;
    private String mLocale;
    private LayoutInflater mLayoutInflater;
    private GroupData mGroupData;
    private ArrayList<MemberData> mDataList = null;

    private String mGeneralCaptain;
    private String mCaptain;
    private String mViceCaptain;

    public MemberListAdapter(Context context, GroupData groupData, ArrayList<MemberData> dataList) {
        this.mContext = context;
        this.mLocale = Util.getLocaleStrng(context);
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
            convertView = mLayoutInflater.inflate(R.layout.member_list_item, null);

            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvLocaleName = (TextView) convertView.findViewById(R.id.tvLocaleName);
            holder.tvGeneration = (TextView) convertView.findViewById(R.id.tvGeneration);
            holder.tvGeneralManager = (TextView) convertView.findViewById(R.id.tvGeneralManager);
            holder.tvGeneralCaptain = (TextView) convertView.findViewById(R.id.tvGeneralCaptain);
            holder.tvManager = (TextView) convertView.findViewById(R.id.tvManager);
            holder.tvCaptain = (TextView) convertView.findViewById(R.id.tvCaptain);
            holder.tvViceCaptain = (TextView) convertView.findViewById(R.id.tvViceCaptain);
            holder.tvConcurrentPosition = (TextView) convertView.findViewById(R.id.tvConcurrentPosition);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String name = memberData.getName();
        holder.tvName.setText(name);

        String localeName = memberData.getLocaleName();
        /*switch (mLocale) {
            case "KR":
                localeName = memberData.getNameKo();
                if (localeName == null || localeName.isEmpty()) {
                    localeName = memberData.getNameEn();
                }
                break;
            default:
                localeName = memberData.getNameEn();
        }*/
        if (localeName != null && !localeName.isEmpty()) {
            localeName = " " + localeName;
            holder.tvLocaleName.setText(localeName);
            holder.tvLocaleName.setVisibility(View.VISIBLE);
        } else {
            holder.tvLocaleName.setVisibility(View.GONE);
        }

        String generation = memberData.getGeneration();
        if (generation != null && !generation.isEmpty()) {
            generation = " (" + generation + ")";
            holder.tvGeneration.setText(generation);
            holder.tvGeneration.setVisibility(View.VISIBLE);
        } else {
            holder.tvGeneration.setVisibility(View.GONE);
        }

        // 총감독
        if (memberData.isGeneralManager()) {
            holder.tvGeneralManager.setVisibility(View.VISIBLE);
        } else {
            holder.tvGeneralManager.setVisibility(View.GONE);
        }

        // 그룹 캡틴
        if (memberData.isGeneralCaptain()) {
            holder.tvGeneralCaptain.setText(mGeneralCaptain);
            holder.tvGeneralCaptain.setVisibility(View.VISIBLE);
        } else {
            holder.tvGeneralCaptain.setVisibility(View.GONE);
        }

        // 지배인
        if (memberData.isManager()) {
            holder.tvManager.setVisibility(View.VISIBLE);
        } else {
            holder.tvManager.setVisibility(View.GONE);
        }

        // 캡틴,리더
        if (memberData.isCaptain()) {
            holder.tvCaptain.setText(mCaptain);
            holder.tvCaptain.setVisibility(View.VISIBLE);
        } else {
            holder.tvCaptain.setVisibility(View.GONE);
        }

        // 부캡틴,부리더
        if (memberData.isViceCaptain()) {
            holder.tvViceCaptain.setText(mViceCaptain);
            holder.tvViceCaptain.setVisibility(View.VISIBLE);
        } else {
            holder.tvViceCaptain.setVisibility(View.GONE);
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
        TextView tvName;
        TextView tvLocaleName;
        TextView tvGeneration;
        TextView tvGeneralManager;
        TextView tvGeneralCaptain;
        TextView tvManager;
        TextView tvCaptain;
        TextView tvViceCaptain;
        TextView tvConcurrentPosition;
    }
}
