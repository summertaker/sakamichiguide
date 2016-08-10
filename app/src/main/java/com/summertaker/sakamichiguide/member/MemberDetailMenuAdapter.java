package com.summertaker.sakamichiguide.member;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseDataAdapter;
import com.summertaker.sakamichiguide.data.MenuData;

import java.util.ArrayList;

public class MemberDetailMenuAdapter extends BaseDataAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<MenuData> mDataList = null;

    public MemberDetailMenuAdapter(Context context, ArrayList<MenuData> dataList) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mDataList = dataList;
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
        MenuData menuData = mDataList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = mLayoutInflater.inflate(R.layout.member_detail_menu_item, null);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ivIcon.setImageResource(menuData.getDrawable());
        holder.tvTitle.setText(menuData.getTitle());

        return convertView;
    }

    static class ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
    }
}
