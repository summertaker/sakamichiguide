package com.summertaker.sakamichiguide.quiz;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseFragment;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.util.Util;

public class SlideFragment extends BaseFragment {

    private static final String ARG_POSITION = "section_number";

    private ImageView mIvIndicator1;
    private ImageView mIvIndicator2;

    private int mCount = 0;

    private MemberData mMemberData;

    ProgressBar mPbPictureLoading;

    Handler mHandler;
    Runnable mRunnable;

    private Callback mCallback;

    public interface Callback {
        void onFinished();

        void onError(String message);
    }

    public SlideFragment() {
    }

    public static SlideFragment newInstance(int position, MemberData memberData) {
        SlideFragment fragment = new SlideFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putSerializable("memberData", memberData);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.slide_fragment, container, false);

        mContext = inflater.getContext();

        //int position = getArguments().getInt(ARG_POSITION);
        mMemberData = (MemberData) getArguments().getSerializable("memberData");
        //Log.e(mTag, "mMemberData.getName(): " + mMemberData.getName());

        //ProgressBar pbLoading = (ProgressBar) rootView.findViewById(R.id.pbLoading);
        //Util.setProgressBarColor(pbLoading, Config.PROGRESS_BAR_COLOR_NORMAL, null);

        if (mMemberData == null) {
            Log.e(mTag, "mMemberData is null.");
            //pbLoading.setVisibility(View.GONE);
            //mCallback.onError(getString(R.string.error_occurred));
        } else {
            //mCallback.onImageLoaded(memberData);
            //pbLoading.setVisibility(View.GONE);

            mPbPictureLoading = (ProgressBar) rootView.findViewById(R.id.pbPictureLoading);
            Util.setProgressBarColor(mPbPictureLoading, Config.PROGRESS_BAR_COLOR_NORMAL, null);

            renderPicture(rootView);
        }

        return rootView;
    }

    private void renderPicture(final View rootView) {
        //Log.e(mTag, "renderPicture()...");

        final ImageView imageView = (ImageView) rootView.findViewById(R.id.ivPictureFull);
        String imageUrl = mMemberData.getImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = mMemberData.getThumbnailUrl();
        }
        //Log.e(mTag, "imageUrl: " + imageUrl);

        if (imageUrl == null || imageUrl.isEmpty()) {
            mPbPictureLoading.setVisibility(View.GONE);
            renderProfile(rootView);
        } else {
            imageView.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(imageUrl).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    mPbPictureLoading.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    mPbPictureLoading.setVisibility(View.GONE);
                    return false;
                }
            }).into(imageView);

            /*Picasso.with(getContext()).load(imageUrl).noFade().into(imageView, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    mPbPictureLoading.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    renderProfile(rootView);
                }

                @Override
                public void onError() {
                    Log.e(mTag, "Picasso.onError()...");
                    mPbPictureLoading.setVisibility(View.GONE);
                }
            });*/
        }

        /*Glide.with(getActivity()).load(imageUrl).listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        Log.e(mTag, "onException()...");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        mPbPictureLoading.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(imageView);*/

        /*Glide.with(mContext).load(imageUrl).asBitmap().dontAnimate().diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                        mPbPictureLoading.setVisibility(View.GONE);
                        imageView.setImageBitmap(bitmap);
                        imageView.setVisibility(View.VISIBLE);

                        //if (cacheUri == null) {
                        //    ImageUtil.saveBitmapToPng(bitmap, cacheId); // 캐쉬 저장
                        //}
                        renderProfile(rootView);
                    }
                });*/
    }

    private void renderProfile(View rootView) {
        RelativeLayout loContent = (RelativeLayout) rootView.findViewById(R.id.loContent);
        loContent.setVisibility(View.VISIBLE);

        // 이름
        TextView tvName = (TextView) rootView.findViewById(R.id.tvName);
        tvName.setText(mMemberData.getName());

        // 그룹 이름
        String groupName = mMemberData.getGroupName();
        String groupTeam = groupName;

        // 팀 이름
        String teamName = mMemberData.getTeamName();
        if (teamName != null && !teamName.isEmpty() && !teamName.equals(groupName)) {
            groupTeam += " " + mMemberData.getTeamName();
        }

        TextView tvGroup = (TextView) rootView.findViewById(R.id.tvInfo);
        tvGroup.setVisibility(View.VISIBLE);
        tvGroup.setText(groupTeam);

        // 사용자 언어 이름
        TextView tvLocaleName = (TextView) rootView.findViewById(R.id.tvLocaleName);
        String localeName = mMemberData.getLocaleName();
        if (localeName == null || localeName.isEmpty()) {
            tvLocaleName.setVisibility(View.GONE);
        } else {
            tvLocaleName.setVisibility(View.VISIBLE);
            tvLocaleName.setText(localeName);
        }

        mIvIndicator1 = (ImageView) rootView.findViewById(R.id.ivIndicator1);
        mIvIndicator2 = (ImageView) rootView.findViewById(R.id.ivIndicator2);

        rotateIndicator();
    }

    private void rotateIndicator() {
        //Log.e(mTag, "mCount: " + mCount);
        mHandler = new Handler();
        mRunnable = new Runnable() {
            public void run() {
                mCount++;
                //Log.e(mTag, "mCount: " + mCount);

                if (mCount > 2) {
                    doFinish();
                    mCallback.onFinished();
                } else {
                    setIndicator();
                    rotateIndicator();
                }
            }
        };
        mHandler.postDelayed(mRunnable, 1000);
    }

    private void setIndicator() {
        switch (mCount) {
            case 1:
                mIvIndicator1.setImageResource(R.drawable.ic_dot_light_red);
                break;
            case 2:
                mIvIndicator2.setImageResource(R.drawable.ic_dot_light_red);
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity;
        if (context instanceof Activity) {
            activity = (Activity) context;

            try {
                mCallback = (Callback) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement Listener for Fragment.");
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        doFinish();
    }

    private void doFinish() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }
}

