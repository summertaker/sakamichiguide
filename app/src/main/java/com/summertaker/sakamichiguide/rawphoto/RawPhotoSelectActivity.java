package com.summertaker.sakamichiguide.rawphoto;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
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
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.data.GroupData;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.data.WebData;
import com.summertaker.sakamichiguide.parser.BaseParser;
import com.summertaker.sakamichiguide.parser.RawPhotoParser;
import com.summertaker.sakamichiguide.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RawPhotoSelectActivity extends BaseActivity {

    GroupData mGroupData;

    String mTitle;

    String mMemberListUrl = "";

    ArrayList<WebData> mWebDataList = new ArrayList<>();
    ArrayList<MemberData> mMemberDataList = new ArrayList<>();
    RawPhotoSelectAdapter mAdapter;

    LinearLayout mLoLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.raw_photo_select_activity);

        mContext = RawPhotoSelectActivity.this;
        mResources = mContext.getResources();

        Intent intent = getIntent();
        mGroupData = (GroupData) intent.getSerializableExtra("groupData");
        //Log.e(mTag, "id: " + mSiteData.getId());

        mTitle = getString(R.string.raw_photo) + " / " + mGroupData.getName();
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, mTitle);

        mLoLoading = (LinearLayout) findViewById(R.id.loLoading);
        ProgressBar pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        Util.setProgressBarColor(pbLoading, Config.PROGRESS_BAR_COLOR_LIGHT, null);

        String url = mGroupData.getRawPhotoUrl();
        String userAgent = Config.USER_AGENT_WEB;
        requestData(url, userAgent);
    }

    private void requestData(final String url, final String userAgent) {
        //Log.e(mTag, "url: " + url);
        //Log.e(mTag, "userAgent: " + userAgent);

        //final String cacheId = Util.urlToId(url);
        //String cacheData = mCacheManager.load(cacheId);

        //if (cacheData == null) {
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e(mTag, response.substring(0, 100));
                //mCacheManager.save(cacheId, response);
                parseData(url, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(mTag, "onErrorResponse(): " + url);
                mErrorMessage = Util.getErrorMessage(error);
                alertNetworkErrorAndFinish(mErrorMessage);
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

        BaseApplication.getInstance().addToRequestQueue(strReq, "strReq");
        //} else {
        //    parseData(url, cacheData);
        //}
    }

    private void parseData(String url, String response) {
        if (response.isEmpty()) {
            alertNetworkErrorAndFinish(mErrorMessage);
        } else {
            if (url.equals(mGroupData.getRawPhotoUrl())) {
                RawPhotoParser rawPhotoParser = new RawPhotoParser();
                //rawPhotoParser.parseSuzukazedayoriMember(response, mGroupData.getId(), mWebDataList);
                rawPhotoParser.parseShopproMember(response, mGroupData.getId(), mWebDataList);

                mMemberListUrl = mGroupData.getUrl();
                String userAgent = Config.USER_AGENT_WEB;
                switch (mGroupData.getId()) {
                    case Config.GROUP_ID_NOGIZAKA46:
                        // pc 사이트 html은 멤버 목록에서 thumbnail 이미지를 css의 sprite 사용함.
                        // PC 사이트 상세화면은 네트웍 타임아웃 에러 발생하니 모바일 사이트 사용한다.
                        mMemberListUrl = mGroupData.getMobileUrl();
                        userAgent = Config.USER_AGENT_MOBILE;
                        break;
                }
                requestData(mMemberListUrl, userAgent);
            }

            if (url.equals(mMemberListUrl)) {
                mMemberDataList = new ArrayList<>();
                BaseParser baseParser = new BaseParser();
                baseParser.parseMemberList(response, mGroupData, mMemberDataList, null, true);
                //Log.e(mTag, "mMemberDataList.size(): " + mMemberDataList.size());

                updateData();
                renderData();
            }
        }
    }

    private void updateData() {
        ArrayList<WebData> newDataList = new ArrayList<>();

        for (MemberData memberData : mMemberDataList) {
            String name = memberData.getName();
            name = Util.removeSpace(name);
            for (WebData webData : mWebDataList) {
                //Log.e(mTag, "memberData.getName(): " + memberData.getName());
                if (webData.getName().equals(name)) {
                    webData.setImageUrl(memberData.getThumbnailUrl());
                    newDataList.add(webData);
                    break;
                }
            }
        }
        //Log.e(mTag, "newDataList.size(): " + newDataList.size());

        mWebDataList.clear();
        mWebDataList = newDataList;
    }

    private void renderData() {
        mLoLoading.setVisibility(View.GONE);

        if (mWebDataList.size() == 0) {
            alertAndFinish(mErrorMessage);
        } else {
            GridView gridView = (GridView) findViewById(R.id.gridView);
            if (gridView != null) {
                gridView.setVisibility(View.VISIBLE);

                mAdapter = new RawPhotoSelectAdapter(this, mGroupData, mWebDataList);
                gridView.setAdapter(mAdapter);

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        WebData webData = (WebData) parent.getItemAtPosition(position);
                        goActivity(webData);
                    }
                });
            }
        }
    }

    private void goActivity(WebData webData) {
        Intent intent = new Intent(mContext, RawPhotoListActivity.class);
        intent.putExtra("groupData", mGroupData);
        intent.putExtra("webData", webData);

        startActivityForResult(intent, 100);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
