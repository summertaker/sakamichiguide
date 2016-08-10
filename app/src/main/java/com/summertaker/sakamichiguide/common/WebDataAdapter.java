package com.summertaker.sakamichiguide.common;

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
import com.summertaker.sakamichiguide.data.WebData;
import com.summertaker.sakamichiguide.util.ImageUtil;
import com.summertaker.sakamichiguide.util.Util;

import java.util.ArrayList;

public class WebDataAdapter extends BaseDataAdapter {

    private String mTag;

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<WebData> mDataList;
    private int mItemLayout;
    private int mImageHeight;

    public WebDataAdapter(Context context, int layout, int imageHeight, ArrayList<WebData> dataList) {
        this.mTag = "==========" + this.getClass().getSimpleName();
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mItemLayout = layout;
        this.mImageHeight = imageHeight;
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

            convertView = mLayoutInflater.inflate(mItemLayout, null);

            holder.loLoading = (RelativeLayout) convertView.findViewById(R.id.loLoading);
            holder.pbLoading = (ProgressBar) convertView.findViewById(R.id.pbLoading);
            if (holder.pbLoading != null) {
                Util.setProgressBarColor(holder.pbLoading, 0, null);
            }

            holder.ivPicture = (ImageView) convertView.findViewById(R.id.ivPicture);
            //holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String imageUrl = webData.getThumbnailUrl();
        //Log.e(mTag, imageUrl);

        if (imageUrl == null || imageUrl.isEmpty()) {
            holder.loLoading.setVisibility(View.GONE);
            //holder.ivPicture.setImageResource(R.drawable.transparent);
        } else {
            final String cacheId = Util.urlToId(imageUrl);
            final String cacheUri = ImageUtil.getValidCacheUri(cacheId);
            if (cacheUri != null) {
                imageUrl = cacheUri;
            }

            // https://futurestud.io/blog/glide-callbacks-simpletarget-and-viewtarget-for-custom-view-classes
            if (mImageHeight > 0) {
                Glide.with(mContext).load(imageUrl).asBitmap().dontAnimate().diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .override(mImageHeight, mImageHeight)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                                if (holder.loLoading != null) {
                                    holder.loLoading.setVisibility(View.GONE);
                                }
                                holder.ivPicture.setImageBitmap(bitmap);

                                if (cacheUri == null) {
                                    ImageUtil.saveBitmapToPng(bitmap, cacheId); // 캐쉬 저장
                                }
                            }
                        });
            } else {
                Glide.with(mContext).load(imageUrl).asBitmap().dontAnimate().diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                                if (holder.loLoading != null) {
                                    holder.loLoading.setVisibility(View.GONE);
                                }
                                holder.ivPicture.setImageBitmap(bitmap);

                                if (cacheUri == null) {
                                    ImageUtil.saveBitmapToPng(bitmap, cacheId); // 캐쉬 저장
                                }
                            }
                        });
            }

            /*
            Picasso.with(mContext).load(imageUrl).resize(100, 100).centerCrop().noFade().into(holder.ivPicture, new com.squareup.picasso.Callback() {
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
            */

            /*
            Picasso.with(mContext).load(imageUrl).resize(100, 100).centerCrop().noFade().into(new Target() {
                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    holder.loLoading.setVisibility(View.GONE);
                    holder.ivPicture.setImageBitmap(bitmap);
                    holder.ivPicture.setVisibility(View.VISIBLE);
                }
                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    holder.loLoading.setVisibility(View.GONE);
                }
            });
            */

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
                        ImageUtil.saveBitmapToPng(loadedImage, cacheId); // 캐쉬 저장
                    }
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    holder.loLoading.setVisibility(View.GONE);
                }
            });
            */
        }

        return convertView;
    }

    static class ViewHolder {
        RelativeLayout loLoading;
        ProgressBar pbLoading;
        ImageView ivPicture;
        TextView tvCaption;
    }
}
