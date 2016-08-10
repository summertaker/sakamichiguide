package com.summertaker.sakamichiguide.quiz;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseActivity;
import com.summertaker.sakamichiguide.common.BaseApplication;
import com.summertaker.sakamichiguide.common.CacheManager;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.data.GroupData;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.data.TeamData;
import com.summertaker.sakamichiguide.parser.BaseParser;
import com.summertaker.sakamichiguide.parser.NamuwikiParser;
import com.summertaker.sakamichiguide.parser.WikipediaEnParser;
import com.summertaker.sakamichiguide.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SlideTextActivity extends BaseActivity implements SlideTextFragment.Callback {

    GroupData mGroupData;

    ArrayList<TeamData> mTeamList = new ArrayList<>();
    ArrayList<MemberData> mMemberList = new ArrayList<>();
    ArrayList<MemberData> mWikiMemberList = new ArrayList<>();

    String mTitle;

    LinearLayout mLoLoading;

    String mLocale;
    BaseParser mWikiParser;
    CacheManager mCacheManager;

    boolean isDataLoaded = false;
    boolean isWikiLoaded = false;

    private int mMemberPosition = 0;
    private boolean mIsFirstData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_text_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        mGroupData = (GroupData) intent.getSerializableExtra("groupData");

        mTitle = mGroupData.getName();
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, mTitle);

        mLoLoading = (LinearLayout) findViewById(R.id.loLoading);
        ProgressBar pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        Util.setProgressBarColor(pbLoading, Config.PROGRESS_BAR_COLOR_NORMAL, null);

        //mHandler = new Handler();

        mLocale = Util.getLocaleStrng(mContext);
        switch (mLocale) {
            case "KR":
                mWikiParser = new NamuwikiParser();
                break;
            default:
                mWikiParser = new WikipediaEnParser();
                break;
        }

        mCacheManager = new CacheManager(mSharedPreferences);

        String url = mGroupData.getUrl();
        String userAgent = Config.USER_AGENT_WEB;

        switch (mGroupData.getId()) {
            case Config.GROUP_ID_NOGIZAKA46:
                userAgent = Config.USER_AGENT_MOBILE;
                url = mGroupData.getMobileUrl(); // desktop html은 thumbnail 이미지를 css의 sprite 사용함.
                break;
        }

        requestData(url, userAgent);
        requestWiki();

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    private void requestData(final String url, final String userAgent) {
        //Log.e(mTag, url);
        //Log.e(mTag, userAgent);

        final String cacheId = Util.urlToId(url);
        String cacheData = mCacheManager.load(cacheId);

        if (cacheData == null) {
            StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Log.e(mTag, "response: " + response);
                    mCacheManager.save(cacheId, response);
                    parseData(url, response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(mTag, "url: " + url);
                    mErrorMessage = Util.getErrorMessage(error);
                    parseData(url, "");
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    //headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("User-agent", userAgent);
                    return headers;
                }
            };

            String tag_string_req = "string_req";
            BaseApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
        } else {
            parseData(url, cacheData);
        }
    }

    private void requestWiki() {
        String url = mWikiParser.getUrl(mGroupData.getId());
        if (url == null) {
            alertAndFinish("URL is empty.");
        } else {
            requestData(url, Config.USER_AGENT_WEB);
        }
    }

    private void parseData(String url, String response) {
        if (url.contains("wiki")) {
            //Log.e(mTag, response);
            if (!response.isEmpty()) {
                mWikiMemberList = new ArrayList<>();
                switch (mGroupData.getId()) {
                    case Config.GROUP_ID_NOGIZAKA46:
                    case Config.GROUP_ID_KEYAKIZAKA46:
                        mWikiParser.parse46List(response, mGroupData, mWikiMemberList);
                        break;
                    default:
                        mWikiParser.parse48List(response, mGroupData, mWikiMemberList);
                        break;
                }
            }
            isWikiLoaded = true;
        } else {
            if (!response.isEmpty()) {
                boolean isMobile = url.equals(mGroupData.getMobileUrl());
                switch (mGroupData.getId()) {
                    default:
                        BaseParser baseParser = new BaseParser();
                        baseParser.parseMemberList(response, mGroupData, mMemberList, mTeamList, isMobile);
                        break;
                }
            }
            isDataLoaded = true;
        }

        updateData();
    }

    private void updateData() {
        //Log.e(mTag, "renderData()...");
        //Log.e(mTag, "isDataLoaded: " + isDataLoaded);
        //Log.e(mTag, "isWikiLoaded: " + isWikiLoaded);

        if (!isDataLoaded || !isWikiLoaded) {
            return;
        }

        mLoLoading.setVisibility(View.GONE);

        if (mMemberList.size() == 0) {
            alertNetworkErrorAndFinish(null);
        } else {
            //Log.e(mTag, "mMemberList.size(): " + mMemberList.size());
            //final int memberSize = mMemberList.size();

            final int wikiSize = mWikiMemberList.size();

            /*if (wikiSize > 0) {
                final ProgressBar pb = (ProgressBar) findViewById(R.id.progress);
                if (pb != null) {
                    pb.setMax(memberSize);
                    mThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (mProgressValue < memberSize) {
                                pb.setProgress(mProgressValue);
                            }
                        }
                    });
                    mThread.start();
                }
            }*/

            String locale = Util.getLocaleStrng(mContext);

            for (MemberData memberData : mMemberList) {
                //Log.e(mTag, "memberData.getNameEn(): " + memberData.getNameEn());

                String localeName = null;
                for (MemberData wikiData : mWikiMemberList) {
                    if (Util.isEqualString(memberData.getNoSpaceName(), wikiData.getNoSpaceName())) {
                        switch (locale) {
                            case "KR":
                                localeName = wikiData.getNameKo();
                                break;
                            default:
                                localeName = wikiData.getNameEn();
                                break;
                        }
                        memberData.setGeneration(wikiData.getGeneration());
                        memberData.setBirthday(wikiData.getBirthday());
                        break;
                    }
                }
                if (localeName == null || localeName.isEmpty()) {
                    localeName = memberData.getNameEn();
                }
                if (localeName == null || localeName.isEmpty()) {
                    localeName = memberData.getName();
                }
                memberData.setLocaleName(localeName);

                //mProgressValue++;
            }

            //Log.e(mTag, "...........");

            Collections.shuffle(mMemberList);

            LinearLayout loContent = (LinearLayout) findViewById(R.id.loContent);
            if (loContent != null) {
                loContent.setVisibility(View.VISIBLE);
                goNext();
            }
        }
    }

    private void goNext() {
        //Log.e(mTag, "goNext().mMemberPosition" + mMemberPosition);

        if (mMemberPosition >= mMemberList.size()) {
            mMemberPosition = 0;
        }

        try {
            Fragment newFragment = SlideTextFragment.newInstance(mMemberPosition, mMemberList.get(mMemberPosition));
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (mIsFirstData) {
                mIsFirstData = false;
            } else {
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            }
            transaction.replace(R.id.loContent, newFragment);
            transaction.commitAllowingStateLoss();

            mMemberPosition++;
            mBaseToolbar.setTitle(mTitle + " (" + mMemberPosition + "/" + mMemberList.size() + ")");

            /*mRunnable = new Runnable() {
                public void run() {
                    goNext();
                }
            };
            mHandler.postDelayed(mRunnable, mInterval);*/

        } catch (IllegalStateException e) {
            e.printStackTrace();
            doFinish();
        }
    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Log.e(mTag, "onKeyDown().keyCode: " + keyCode);
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    doFinish();
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }*/

    @Override
    public void onFinished() {
        goNext();
    }

    @Override
    public void onError(String message) {

    }
}
