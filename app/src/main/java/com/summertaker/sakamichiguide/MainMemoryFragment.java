package com.summertaker.sakamichiguide;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.summertaker.sakamichiguide.common.BaseFragment;
import com.summertaker.sakamichiguide.common.BaseFragmentInterface;
import com.summertaker.sakamichiguide.common.Config;

import java.util.Random;

public class MainMemoryFragment extends BaseFragment implements BaseFragmentInterface {

    private int mPosition;

    private Callback mCallback;

    public interface Callback {
        void onMemorySelected(String action);

        void onError(String message);
    }

    public static MainMemoryFragment newInstance(int position, String action) {
        MainMemoryFragment fragment = new MainMemoryFragment();

        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("action", action);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = container.getContext();
        View rootView = inflater.inflate(R.layout.main_memory_fragment, container, false);

        mPosition = getArguments().getInt("position", 0);

        int[] pics = {R.drawable.pic_1, R.drawable.pic_2, R.drawable.pic_3, R.drawable.pic_4};
        Random random = new Random();
        int max = 3;
        int min = 0;
        int selected = random.nextInt(max - min + 1) + min;

        ImageView ivHeader = (ImageView) rootView.findViewById(R.id.ivHeader);
        ivHeader.setImageResource(pics[selected]);

        LinearLayout slide = (LinearLayout) rootView.findViewById(R.id.loSlide);
        slide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onMemorySelected(Config.MAIN_ACTION_SLIDE);
            }
        });

        LinearLayout memory = (LinearLayout) rootView.findViewById(R.id.loMemory);
        memory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onMemorySelected(Config.MAIN_ACTION_MEMORY);
            }
        });

        LinearLayout quiz = (LinearLayout) rootView.findViewById(R.id.loQuiz);
        quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onMemorySelected(Config.MAIN_ACTION_QUIZ);
            }
        });

        return rootView;
    }

    public int getPosition() {
        return mPosition;
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
    public void refresh(String articleId) {

    }

    @Override
    public boolean canGoBack() {
        return false;
    }

    @Override
    public void goBack() {

    }
}

