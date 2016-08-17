package com.summertaker.sakamichiguide.member;

import android.content.Context;
import android.util.Log;
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
import com.summertaker.sakamichiguide.data.WebData;
import com.summertaker.sakamichiguide.util.Util;

import java.util.ArrayList;

public class MemberDetailPedia48Adapter extends BaseDataAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<WebData> mDataList = null;

    public MemberDetailPedia48Adapter(Context context, ArrayList<WebData> dataList) {
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
        WebData webData = mDataList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = mLayoutInflater.inflate(R.layout.member_detail_pedia48_item, null);

            holder.loLoading = (RelativeLayout) convertView.findViewById(R.id.loLoading);
            holder.pbLoading = (ProgressBar) convertView.findViewById(R.id.pbLoading);
            Util.setProgressBarColor(holder.pbLoading, 0, null);

            holder.loData = (RelativeLayout) convertView.findViewById(R.id.loData);
            holder.ivPicture = (ImageView) convertView.findViewById(R.id.ivPicture);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final String imageUrl = webData.getThumbnailUrl();
        //Log.e(mTag, imageUrl);

        if (imageUrl == null || imageUrl.isEmpty()) {
            holder.loLoading.setVisibility(View.GONE);
        } else {
            //final String cacheId = Util.urlToId(imageUrl);
            //final String cacheUri = ImageUtil.getValidCacheUri(cacheId);
            //if (cacheUri != null) {
            //    imageUrl = cacheUri;
            //}

            Picasso.with(mContext).load(imageUrl).into(holder.ivPicture, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    holder.loLoading.setVisibility(View.GONE);
                    holder.loData.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    Log.e(mTag, "Picasso.ERROR: " + imageUrl);
                    holder.loLoading.setVisibility(View.GONE);
                }
            });

            /*// https://futurestud.io/blog/glide-callbacks-simpletarget-and-viewtarget-for-custom-view-classes
            Glide.with(mContext).load(imageUrl).asBitmap().dontAnimate().diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .override(120, 147)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                            holder.loLoading.setVisibility(View.GONE);
                            holder.ivPicture.setImageBitmap(bitmap);

                            if (cacheUri == null) {
                                ImageUtil.saveBitmapToPng(bitmap, cacheId); // 캐쉬 저장
                            }
                        }
                    });*/
        }

        holder.tvTitle.setText(webData.getTitle());

        return convertView;
    }

    static class ViewHolder {
        RelativeLayout loLoading;
        ProgressBar pbLoading;

        RelativeLayout loData;
        ImageView ivPicture;
        TextView tvTitle;
    }
}
