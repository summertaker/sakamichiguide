package com.summertaker.sakamichiguide;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.summertaker.sakamichiguide.common.BaseDataAdapter;
import com.summertaker.sakamichiguide.data.GroupData;

import java.util.ArrayList;

public class GroupSelectGridAdapter extends BaseDataAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private ArrayList<GroupData> mDataList = new ArrayList<>();

    public GroupSelectGridAdapter(Context context, ArrayList<GroupData> dataList) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;

        if (view == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = mLayoutInflater.inflate(R.layout.group_select_grid_item, null);

            holder = new ViewHolder();
            holder.image = (ImageView) view.findViewById(R.id.groupImage);
            holder.name = (TextView) view.findViewById(R.id.groupName);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        GroupData item = mDataList.get(position);
        holder.image.setImageResource(item.getImage());
        holder.name.setText(item.getName());

        //Log.e(mTag, "item.getName(): " + item.getName());

        return view;
    }

    static class ViewHolder {
        ImageView image;
        TextView name;
    }
}
