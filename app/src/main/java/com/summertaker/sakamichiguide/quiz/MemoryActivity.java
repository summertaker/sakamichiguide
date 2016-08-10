package com.summertaker.sakamichiguide.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
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
import com.summertaker.sakamichiguide.common.Setting;
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

public class MemoryActivity extends BaseActivity implements MemoryFragment.Callback {

    boolean mShowOfficialPhoto;

    GroupData mGroupData;

    ArrayList<TeamData> mTeamList = new ArrayList<>();
    ArrayList<MemberData> mMemberList = new ArrayList<>();
    ArrayList<MemberData> mWikiMemberList = new ArrayList<>();

    String mTitle;

    LinearLayout mLoLoading;
    MemoryAdapter mPagerAdapter;
    ViewPager mViewPager;

    String mLocale;
    BaseParser mWikiParser;
    CacheManager mCacheManager;

    boolean isDataLoaded = false;
    boolean isWikiLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memory_activity);

        mContext = MemoryActivity.this;

        Setting setting = new Setting(mContext);
        mShowOfficialPhoto = setting.get(Config.SETTING_DISPLAY_OFFICIAL_PHOTO).equals(Config.SETTING_DISPLAY_OFFICIAL_PHOTO_YES);
        //Log.e(mTag, "mShowOfficialPhoto: " + mShowOfficialPhoto);

        Intent intent = getIntent();
        mGroupData = (GroupData) intent.getSerializableExtra("groupData");

        mTitle = mGroupData.getName();
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, mTitle);

        mLoLoading = (LinearLayout) findViewById(R.id.loLoading);
        ProgressBar pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        Util.setProgressBarColor(pbLoading, Config.PROGRESS_BAR_COLOR_NORMAL, null);

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
            //case Config.GROUP_ID_NGT48:
            case Config.GROUP_ID_NOGIZAKA46:
                userAgent = Config.USER_AGENT_MOBILE;
                url = mGroupData.getMobileUrl(); // desktop html은 thumbnail 이미지를 css의 sprite 사용함.
                break;
        }

        requestData(url, userAgent);
        requestWiki();
    }

    private void requestData(final String url, final String userAgent) {
        //Log.e(mTag, url);
        //Log.e(mTag, userAgent);

        //final String cacheId = Util.urlToId(url);
        //String cacheData = mCacheManager.load(cacheId);

        //if (cacheData == null) {
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e(mTag, "response: " + response);
                //mCacheManager.save(cacheId, response);
                parseData(url, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(mTag, "url: " + url);
                if (url.contains("wiki")) {
                    parseData(url, "");
                } else {
                    String errorMessage = Util.getErrorMessage(error);
                    alertNetworkErrorAndFinish(errorMessage);
                }
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
        //} else {
        //    parseData(url, cacheData);
        //}
    }

    private void requestWiki() {
        String url = mWikiParser.getUrl(mGroupData.getId());
        if (url == null) {
            alertAndFinish("Namuwiki URL is empty.");
        } else {
            requestData(url, Config.USER_AGENT_WEB);
        }
    }

    private void parseData(String url, String response) {
        if (url.contains("wiki")) {
            //Log.e(mTag, response);
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
            isWikiLoaded = true;
        } else {
            boolean isMobile = url.equals(mGroupData.getMobileUrl());
            BaseParser baseParser = new BaseParser();
            baseParser.parseMemberList(response, mGroupData, mMemberList, mTeamList, isMobile);

            isDataLoaded = true;
        }
        renderData();
    }

    private void renderData() {
        if (!isDataLoaded || !isWikiLoaded) {
            return;
        }
        mLoLoading.setVisibility(View.GONE);

        if (mMemberList.size() == 0) {
            alertParseErrorAndFinish(null);
        } else {
            initBaseToolbar(Config.TOOLBAR_ICON_BACK, mTitle + " (1/" + mMemberList.size() + ")");

            String locale = Util.getLocaleStrng(mContext);
            for (MemberData memberData : mMemberList) {
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
            }

            Collections.shuffle(mMemberList);

            mPagerAdapter = new MemoryAdapter(getSupportFragmentManager(), mMemberList);
            mViewPager = (ViewPager) findViewById(R.id.viewPager);
            if (mViewPager != null) {
                mViewPager.setVisibility(View.VISIBLE);
                mViewPager.setAdapter(mPagerAdapter);
                mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        mBaseToolbar.setTitle(mTitle + " (" + (position + 1) + "/" + mMemberList.size() + ")");
                        //setAnswer(position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });
            }
        }
    }

    @Override
    public void onPrevSelected() {
        int item = mViewPager.getCurrentItem() - 1;
        if (item >= 0) {
            mViewPager.setCurrentItem(item, true);
        }
    }

    @Override
    public void onNextSelected() {
        int item = mViewPager.getCurrentItem() + 1;
        if (item < mPagerAdapter.getCount()) {
            mViewPager.setCurrentItem(item, true);
        }
    }

    @Override
    public void onError(String message) {
        alertAndFinish(message);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
