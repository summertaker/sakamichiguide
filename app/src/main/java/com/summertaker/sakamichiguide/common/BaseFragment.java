package com.summertaker.sakamichiguide.common;

import android.content.Context;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

    protected String mTag;
    protected Context mContext;

    public BaseFragment() {
        this.mTag = "===== " + this.getClass().getSimpleName();
    }
}
