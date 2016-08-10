package com.summertaker.sakamichiguide.quiz;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseFragment;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.common.Setting;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.util.Util;

public class MemoryFragment extends BaseFragment {

    private static final String ARG_POSITION = "section_number";

    private String mLocale;

    boolean mShowOfficialPhoto;

    private Callback mCallback;

    public interface Callback {
        void onPrevSelected();

        void onNextSelected();

        void onError(String message);
    }

    public MemoryFragment() {
    }

    public static MemoryFragment newInstance(int position, MemberData memberData) {
        MemoryFragment fragment = new MemoryFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putSerializable("memberData", memberData);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.memory_fragment, container, false);

        mContext = inflater.getContext();
        mLocale = Util.getLocaleStrng(mContext);

        Setting setting = new Setting(mContext);
        mShowOfficialPhoto = setting.get(Config.SETTING_DISPLAY_OFFICIAL_PHOTO).equals(Config.SETTING_DISPLAY_OFFICIAL_PHOTO_YES);
        //Log.e(mTag, "mShowOfficialPhoto: " + mShowOfficialPhoto);

        //int position = getArguments().getInt(ARG_POSITION);
        final MemberData memberData = (MemberData) getArguments().getSerializable("memberData");

        if (memberData == null) {
            //pbLoading.setVisibility(View.GONE);
            mCallback.onError(getString(R.string.network_error_occurred));
        } else {
            // 멤버 정보
            String groupName = memberData.getGroupName();
            String groupTeam = groupName;
            String teamName = memberData.getTeamName();
            if (teamName != null && !teamName.isEmpty() && !teamName.equals(groupName)) {
                groupTeam += " " + memberData.getTeamName();
            }

            if (mShowOfficialPhoto) {
                //--------------------------
                // 사진과 함께 보여주는 경우
                //--------------------------
                RelativeLayout loWithPicture = (RelativeLayout) rootView.findViewById(R.id.loWithPicture);
                loWithPicture.setVisibility(View.VISIBLE);

                // 이름
                TextView tvNameWithPicture = (TextView) rootView.findViewById(R.id.tvNameWithPicture);
                tvNameWithPicture.setText(memberData.getName());
                tvNameWithPicture.setVisibility(View.VISIBLE);

                // 정보
                /*TextView tvInfoWithPicture = (TextView) rootView.findViewById(R.id.tvInfoWithPicture);
                tvInfoWithPicture.setVisibility(View.VISIBLE);
                tvInfoWithPicture.setText(groupTeam);*/
            } else {
                //--------------------------
                // 텍스트로 보여주는 경우
                //--------------------------
                LinearLayout loWithoutPicture = (LinearLayout) rootView.findViewById(R.id.loWithoutPicture);
                loWithoutPicture.setVisibility(View.VISIBLE);

                // 이름
                TextView tvNameWithoutPicture = (TextView) rootView.findViewById(R.id.tvNameWithoutPicture);
                tvNameWithoutPicture.setText(memberData.getName());
                tvNameWithoutPicture.setVisibility(View.VISIBLE);

                // 정보
                TextView tvInfoWithoutPicture = (TextView) rootView.findViewById(R.id.tvInfoWithoutPicture);
                tvInfoWithoutPicture.setVisibility(View.VISIBLE);
                tvInfoWithoutPicture.setText(groupTeam);
            }

            // 물음표 이름
            final Button btAnswer = (Button) rootView.findViewById(R.id.btAnswer);
            String localeName = memberData.getLocaleName();
            /*
            switch (mLocale) {
                case "KR":
                    localName = memberData.getNameKo();
                    break;
                default:
                    localName = memberData.getNameEn();
                    break;
            }*/
            if (localeName == null || localeName.isEmpty()) {
                localeName = "No Translation...";
            }
            btAnswer.setText(localeName);

            // 물음표 클릭
            Button btQustion = (Button) rootView.findViewById(R.id.btQuestion);
            btQustion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setVisibility(View.GONE);
                    btAnswer.setVisibility(View.VISIBLE);
                }
            });

            if (mShowOfficialPhoto) {
                final ProgressBar pbPictureLoading = (ProgressBar) rootView.findViewById(R.id.pbPictureLoading);
                Util.setProgressBarColor(pbPictureLoading, Config.PROGRESS_BAR_COLOR_LIGHT, null);

                ImageView ivPicture = (ImageView) rootView.findViewById(R.id.ivPictureFull);
                final ImageView imageView = ivPicture;

                String imageUrl = memberData.getImageUrl();
                if (imageUrl == null || imageUrl.isEmpty()) {
                    imageUrl = memberData.getThumbnailUrl();
                }
                //Log.e("#####", "imageUrl: " + memberData.getGroupId() + " - " +imageUrl);

                //final String cacheId = Util.urlToId(imageUrl);
                //final String cacheUri = ImageUtil.getValidCacheUri(cacheId);
                //if (cacheUri != null) {
                //    imageUrl = cacheUri;
                //}

                Picasso.with(mContext).load(imageUrl).into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        pbPictureLoading.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
                        pbPictureLoading.setVisibility(View.GONE);
                    }
                });

                //Glide.with(mContext).load(imageUrl).crossFade().into(imageView);

                /*imageView.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(imageUrl).listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        pbPictureLoading.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        pbPictureLoading.setVisibility(View.GONE);
                        return false;
                    }
                }).into(imageView);*/

                /*// https://futurestud.io/blog/glide-callbacks-simpletarget-and-viewtarget-for-custom-view-classes
                Glide.with(mContext).load(imageUrl).asBitmap().dontAnimate() //.diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        //.override(Config.IMAGE_FULL_WIDTH, Config.IMAGE_FULL_HEIGHT)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                                pbPictureLoading.setVisibility(View.GONE);
                                imageView.setImageBitmap(bitmap);
                                imageView.setVisibility(View.VISIBLE);

                                //if (cacheUri == null) {
                                //    ImageUtil.saveBitmapToPng(bitmap, cacheId); // 캐쉬 저장
                                //}
                            }
                        });*/
            }

            // 이전으로 가기
            ImageView ivPrev = (ImageView) rootView.findViewById(R.id.ivPrev);
            ivPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onPrevSelected();
                }
            });

            // 다음으로 가기
            ImageView ivNext = (ImageView) rootView.findViewById(R.id.ivNext);
            ivNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onNextSelected();
                }
            });
        }
        return rootView;
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
}
