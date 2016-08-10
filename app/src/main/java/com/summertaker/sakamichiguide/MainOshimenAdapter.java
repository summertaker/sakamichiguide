package com.summertaker.sakamichiguide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.summertaker.sakamichiguide.common.BaseDataAdapter;
import com.summertaker.sakamichiguide.data.MemberData;

import java.util.ArrayList;

public class MainOshimenAdapter extends BaseDataAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<MemberData> mDataList;

    public MainOshimenAdapter(Context context, ArrayList<MemberData> dataList) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mDataList = dataList;
    }

    @Override
    public int getCount() {
        //Log.e(mTag, "getCount(): " + getCount());
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
        MemberData memberData = mDataList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = mLayoutInflater.inflate(R.layout.main_oshimen_item, null);

            //holder.loLoading = (RelativeLayout) convertView.findViewById(R.id.loLoading);
            //holder.pbLoading = (ProgressBar) convertView.findViewById(R.id.pbLoading);
            //Util.setProgressBarColor(holder.pbLoading, 0, null);

            //holder.ivPicture = (ImageView) convertView.findViewById(R.id.ivPicture);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvGroupName = (TextView) convertView.findViewById(R.id.tvGroupName);
            holder.tvTeamName = (TextView) convertView.findViewById(R.id.tvTeamName);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        /*String imageUrl = memberData.getThumbnailUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            holder.loLoading.setVisibility(View.GONE);
        } else {
            final String cacheId = Util.urlToId(imageUrl);
            //Log.e(mTag, cacheId);

            final String cacheUri = ImageUtil.getValidCacheUri(cacheId);
            if (cacheUri != null) {
                imageUrl = cacheUri;
            }

            // https://futurestud.io/blog/glide-callbacks-simpletarget-and-viewtarget-for-custom-view-classes
            Glide.with(mContext).load(imageUrl).dontAnimate().diskCacheStrategy(DiskCacheStrategy.RESULT)
                    //.override(Config.IMAGE_GRID2_WIDTH, Config.IMAGE_GRID2_HEIGHT)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.loLoading.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(holder.ivPicture);
        }*/

        holder.tvName.setText(memberData.getName());

        String groupName = memberData.getGroupName();
        groupName = " " + groupName;
        holder.tvGroupName.setText(groupName);

        String teamName = memberData.getTeamName();
        if (teamName != null && ! teamName.isEmpty()) {
            teamName = " " + teamName;
        } else {
            teamName = "";
        }
        holder.tvTeamName.setText(teamName);

        return convertView;
    }

    static class ViewHolder {
        //RelativeLayout loLoading;
        //ProgressBar pbLoading;
        //ImageView ivPicture;
        TextView tvName;
        TextView tvGroupName;
        TextView tvTeamName;
    }

    public void refresh(ArrayList<MemberData> dataList) {
        this.mDataList = dataList;
        notifyDataSetChanged();
    }
}

