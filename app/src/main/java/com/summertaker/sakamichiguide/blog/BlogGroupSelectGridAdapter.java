package com.summertaker.sakamichiguide.blog;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseDataAdapter;
import com.summertaker.sakamichiguide.data.SiteData;

import java.util.ArrayList;

public class BlogGroupSelectGridAdapter extends BaseDataAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    ArrayList<SiteData> mDataList = new ArrayList<>();

    public BlogGroupSelectGridAdapter(Context context, ArrayList<SiteData> dataList) {
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
            view = mLayoutInflater.inflate(R.layout.blog_group_select_grid_item, null);

            holder = new ViewHolder();
            holder.logo = (ImageView) view.findViewById(R.id.ivLogo);
            holder.title = (TextView) view.findViewById(R.id.tvTitle);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        SiteData item = mDataList.get(position);
        holder.logo.setImageResource(item.getImage());
        holder.title.setText(item.getName());

        //Log.e(mTag, "item.getName(): " + item.getName());

        return view;
    }

    static class ViewHolder {
        ImageView logo;
        TextView title;
    }
}
