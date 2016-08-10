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
import android.widget.TextView;

import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseFragment;
import com.summertaker.sakamichiguide.data.MemberData;

public class SlideTextFragment extends BaseFragment {

    private static final String ARG_POSITION = "section_number";

    private int mIndicatorCount = 0;
    private ImageView mIvIndicator1;
    private ImageView mIvIndicator2;

    private MemberData mMemberData;

    private Handler mHandler;
    private Runnable mRunnable;

    private Callback mCallback;

    public interface Callback {
        void onFinished();

        void onError(String message);
    }

    public SlideTextFragment() {
    }

    public static SlideTextFragment newInstance(int position, MemberData memberData) {
        SlideTextFragment fragment = new SlideTextFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putSerializable("memberData", memberData);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.slide_text_fragment, container, false);

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

            renderProfile(rootView);
        }

        return rootView;
    }

    private void renderProfile(View rootView) {

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
        //Log.e(mTag, "mIndicatorCount: " + mIndicatorCount);
        mHandler = new Handler();
        mRunnable = new Runnable() {
            public void run() {
                mIndicatorCount++;
                //Log.e(mTag, "mIndicatorCount: " + mIndicatorCount);

                if (mIndicatorCount > 2) {
                    removeRunnable();
                    mIndicatorCount = 0;
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
        switch (mIndicatorCount) {
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
    public void onDestroy() {
        super.onDestroy();
        removeRunnable();
    }

    private void removeRunnable() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }
}
