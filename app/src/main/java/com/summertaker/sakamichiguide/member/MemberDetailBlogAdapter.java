package com.summertaker.sakamichiguide.member;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseDataAdapter;
import com.summertaker.sakamichiguide.data.WebData;
import com.summertaker.sakamichiguide.util.ImageUtil;
import com.summertaker.sakamichiguide.util.Util;

import java.util.ArrayList;

public class MemberDetailBlogAdapter extends BaseDataAdapter {

    private String mTag = "### MemberViewBlogAdapter";

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<WebData> mDataList = null;

    public MemberDetailBlogAdapter(Context context, ArrayList<WebData> dataList) {
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

            convertView = mLayoutInflater.inflate(R.layout.grid4_image_item, null);

            holder.loLoading = (RelativeLayout) convertView.findViewById(R.id.loLoading);
            holder.pbLoading = (ProgressBar) convertView.findViewById(R.id.pbLoading);
            Util.setProgressBarColor(holder.pbLoading, 0, null);

            holder.ivPicture = (ImageView) convertView.findViewById(R.id.ivPicture);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String imageUrl = webData.getImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {

        } else {
            final String cacheId = Util.urlToId(imageUrl);
            final String cacheUri = ImageUtil.getValidCacheUri(cacheId);
            if (cacheUri != null) {
                imageUrl = cacheUri;
            }

            // https://futurestud.io/blog/glide-callbacks-simpletarget-and-viewtarget-for-custom-view-classes
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
                    });
            /*
            ImageLoader.getInstance().displayImage(imageUrl, holder.ivPicture, mDisplayImageOptions, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                }
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    holder.loLoading.setVisibility(View.GONE);
                    view.setVisibility(View.VISIBLE);

                    if (cacheUri == null) {
                        ImageUtil.saveBitmapToPng(loadedImage, cacheId);
                    }
                }
                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    holder.loLoading.setVisibility(View.GONE);
                }
            });*/
        }

        /*
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = mLayoutInflater.inflate(R.layout.member_detail_blog_item, null);

            holder.pbLoading = (ProgressBar) convertView.findViewById(R.id.pbLoading);
            Util.setProgressBarColor(holder.pbLoading, 0, null);

            holder.ivPicture = (ImageView) convertView.findViewById(R.id.ivPicture);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String imageUrl = webData.getImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            holder.loLoading.setVisibility(View.GONE);
            // 감추면 XML에 설정된 높이 값이 달라지므로 ExpandableListView가 제대로 높이 계산을 못함
            //holder.ivPicture.setVisibility(View.GONE);
        } else {
            final String cacheId = Util.urlToId(imageUrl);
            final String cacheUri = ImageUtil.getValidCacheUri(cacheId);
            if (cacheUri != null) {
                imageUrl = cacheUri;
            }

            ImageLoader.getInstance().displayImage(imageUrl, holder.ivPicture, mDisplayImageOptions, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                }
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    holder.loLoading.setVisibility(View.GONE);
                    view.setVisibility(View.VISIBLE);

                    if (cacheUri == null) {
                        ImageUtil.saveBitmapToPng(loadedImage, cacheId);
                    }
                }
                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    holder.loLoading.setVisibility(View.GONE);
                }
            });
        }

        holder.tvTitle.setText(webData.getTitle());
        holder.tvDate.setText(webData.getDate());

        String content = webData.getContent();
        if (content != null && !content.isEmpty()) {
            content = content.replace("\n", " ");
        }
        holder.tvContent.setText(content);
        */

        return convertView;
    }

    static class ViewHolder {
        RelativeLayout loLoading;
        ProgressBar pbLoading;
        ImageView ivPicture;
        TextView tvTitle;
        TextView tvDate;
        TextView tvContent;
    }
}
