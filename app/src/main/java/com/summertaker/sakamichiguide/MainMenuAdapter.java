package com.summertaker.sakamichiguide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.summertaker.sakamichiguide.common.BaseDataAdapter;
import com.summertaker.sakamichiguide.data.MenuData;
import com.summertaker.sakamichiguide.util.Translator;

import java.util.ArrayList;

public class MainMenuAdapter extends BaseDataAdapter {
    private String mTag;

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    ArrayList<MenuData> mDataList;

    private Translator mTranslator;

    public MainMenuAdapter(Context context, ArrayList<MenuData> dataList) {
        this.mTag = "===== " + this.getClass().getSimpleName();
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
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
        final MenuData menuData = mDataList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.main_item, null);

            holder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Picasso.with(mContext).load(menuData.getDrawable()).into(holder.ivIcon);

        String title = menuData.getTitle();
        holder.tvTitle.setText(title);

        return convertView;
    }

    static class ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
    }
}
