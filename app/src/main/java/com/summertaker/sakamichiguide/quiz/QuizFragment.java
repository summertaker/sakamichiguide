package com.summertaker.sakamichiguide.quiz;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseFragment;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.util.Util;

public class QuizFragment extends BaseFragment {

    private static final String ARG_POSITION = "section_number";

    private Callback mCallback;

    public interface Callback {
        void onNameClick();

        void onError(String message);
    }

    public QuizFragment() {
    }

    public static QuizFragment newInstance(int position, MemberData memberData, String memberName, String randomName) {
        QuizFragment fragment = new QuizFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putSerializable("memberData", memberData);
        args.putString("memberName", memberName);
        args.putString("randomName", randomName);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.quiz_fragment, container, false);

        mContext = inflater.getContext();
        //String locale = Util.getLocaleStrng(mContext);

        //int position = getArguments().getInt(ARG_POSITION, 0);
        MemberData memberData = (MemberData) getArguments().getSerializable("memberData");

        String mMemberName = getArguments().getString("memberName");
        String mRandomName = getArguments().getString("randomName");

        ProgressBar pbLoading = (ProgressBar) rootView.findViewById(R.id.pbLoading);
        Util.setProgressBarColor(pbLoading, Config.PROGRESS_BAR_COLOR_NORMAL, null);

        if (memberData == null) {
            pbLoading.setVisibility(View.GONE);
            //mCallback.onError(getString(R.string.network_error_occurred));
        } else {
            LinearLayout loFooter = (LinearLayout) rootView.findViewById(R.id.loFooter);
            loFooter.setVisibility(View.VISIBLE);

            // No Button
            Button btNo = (Button) rootView.findViewById(R.id.btVallid);
            btNo.setText(mRandomName);
            btNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onNameClick();
                }
            });

            // Yes Button
            Button btYes = (Button) rootView.findViewById(R.id.btValid);
            btYes.setText(mMemberName);
            btYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onNameClick();
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

