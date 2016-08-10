package com.summertaker.sakamichiguide.member;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseDataAdapter;
import com.summertaker.sakamichiguide.data.GroupData;
import com.summertaker.sakamichiguide.data.TeamData;
import com.summertaker.sakamichiguide.util.Util;

import java.util.ArrayList;

public class TeamListAdapter extends BaseDataAdapter {
    private String mTag;

    private Context mContext;
    private Resources mResources;
    private String mLocale;
    private LayoutInflater mLayoutInflater;
    private GroupData mGroupData;
    private ArrayList<TeamData> mDataList = null;

    public TeamListAdapter(Context context, GroupData groupData, ArrayList<TeamData> dataList) {
        this.mTag = "===== " + this.getClass().getSimpleName();
        this.mContext = context;
        this.mResources = mContext.getResources();
        this.mLocale = Util.getLocaleStrng(context);
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mGroupData = groupData;
        this.mDataList = dataList;

        //if (groupData.getGroupId().equals(Config.GROUP_ID_NGT48)) {
        //    this.mDisplayImageOptions = ImageUtil.getDisplayImageOptionsSize(200, 250);
        //}
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

            //if (memberData.getGroupId().equals(Config.GROUP_ID_AKB48)) {
            //    convertView = mLayoutInflater.inflate(R.layout.team_grid_item_akb48, null);
            //} else {
                convertView = mLayoutInflater.inflate(R.layout.team_list_item, null);
            //}

            //holder.loLoading = (RelativeLayout) convertView.findViewById(R.id.loLoading);
            //holder.pbLoading = (ProgressBar) convertView.findViewById(R.id.pbLoading);
            //Util.setProgressBarColor(holder.pbLoading, 0, null);

            //holder.ivPicture = (ImageView) convertView.findViewById(R.id.ivPicture);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvCount = (TextView) convertView.findViewById(R.id.tvCount);

            /*switch (memberData.getGroupId()) {
                case Config.GROUP_ID_AKB48:
                    //holder.ivPicture.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    break;
                case Config.GROUP_ID_SKE48:
                    holder.tvName.setBackgroundColor(ContextCompat.getColor(mContext, R.color.ske48background));
                    holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.ske48text));
                    break;
                case Config.GROUP_ID_NMB48:
                    //holder.tvName.setBackgroundResource(R.drawable.bg_nmb48_team);
                    holder.tvName.setBackgroundColor(ContextCompat.getColor(mContext, R.color.nmb48background));
                    holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.nmb48text));
                    break;
                case Config.GROUP_ID_HKT48:
                    holder.tvName.setBackgroundColor(ContextCompat.getColor(mContext, R.color.hkt48background));
                    holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.hkt48text));
                    break;
                case Config.GROUP_ID_NGT48:
                    holder.tvName.setBackgroundColor(ContextCompat.getColor(mContext, R.color.ngt48background));
                    holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.ngt48text));
                    break;
                case Config.GROUP_ID_JKT48:
                    holder.tvName.setBackgroundColor(ContextCompat.getColor(mContext, R.color.jkt48background));
                    holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.jkt48text));
                    break;
                case Config.GROUP_ID_KEYAKIZAKA46:
                    holder.tvName.setBackgroundColor(ContextCompat.getColor(mContext, R.color.keyakizaka48background));
                    holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.keyakizaka48text));
                    break;
            }*/
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // http://cdn.akb48.co.jp/cache/image/?path=%2Fmembers%2Fprofile20150511%2Fteam_B_png%2Fogasawara_mayu.png
        //String imageUrl = data.getThumbnailUrl();
        //Log.e(mTag, "imageUrl: " + imageUrl);

/*
        if (imageUrl == null || imageUrl.isEmpty()) {
            holder.loLoading.setVisibility(View.GONE);
            //holder.ivPicture.setImageResource(R.drawable.anonymous);
        } else {
            if (memberData.getGroupId().equals(Config.GROUP_ID_JKT48)) {
                holder.ivPicture.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
*/

            //final String cacheId = Util.urlToId(imageUrl);
            //final String cacheUri = ImageUtil.getValidCacheUri(cacheId);
            //if (cacheUri != null) {
            //    imageUrl = cacheUri;
            //}

/*
            // https://futurestud.io/blog/glide-callbacks-simpletarget-and-viewtarget-for-custom-view-classes
            Glide.with(mContext).load(imageUrl).asBitmap().dontAnimate() //.diskCacheStrategy(DiskCacheStrategy.RESULT)
                    //.override(Config.IMAGE_GRID3_WIDTH, Config.IMAGE_GRID3_HEIGHT)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                            holder.loLoading.setVisibility(View.GONE);
                            holder.ivPicture.setImageBitmap(bitmap);

                            //if (cacheUri == null) {
                            //    ImageUtil.saveBitmapToPng(bitmap, cacheId); // 캐쉬 저장
                            //}
                        }
                    });
*/
            /*
            // NGT48의 이미지 크기(1,280px × 1,600px)가 커서 발생하는 메모리 오류를 Piccaso로 해결
            Picasso.with(mContext).load(imageUrl).resize(200, 250).noFade().into(holder.ivPicture, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    holder.loLoading.setVisibility(View.GONE);
                    //holder.ivPicture.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
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
            }, new ImageLoadingProgressListener() {
                @Override
                public void onProgressUpdate(String imageUri, View view, int current, int total) {
                }
            });
            */
//        }

        //String name = memberData.getNameJa();
        //if (mLocale.equals("EN") && memberData.getNameEn() != null && !memberData.getNameEn().isEmpty()) {
        //    name = memberData.getNameEn();
        //}
        String name = teamData.getName();
        holder.tvName.setText(name);

        int count = teamData.getMemberCount();
        String text = String.format(mResources.getString(R.string.s_people), count);
        text = " (" + text + ")";
        holder.tvCount.setText(text);

        return convertView;
    }

    static class ViewHolder {
        //RelativeLayout loLoading;
        //ProgressBar pbLoading;
        //ImageView ivPicture;
        TextView tvName;
        TextView tvCount;
    }
}
