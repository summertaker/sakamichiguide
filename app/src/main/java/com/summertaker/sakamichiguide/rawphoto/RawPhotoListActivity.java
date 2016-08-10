package com.summertaker.sakamichiguide.rawphoto;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
import com.summertaker.sakamichiguide.data.WebData;
import com.summertaker.sakamichiguide.parser.RawPhotoParser;
import com.summertaker.sakamichiguide.util.EndlessScrollListener;
import com.summertaker.sakamichiguide.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RawPhotoListActivity extends BaseActivity {

    GroupData mGroupData;
    WebData mWebData;

    String mTitle;

    ProgressBar mPbLoading;
    //LinearLayout mLoLoadingMore;

    ArrayList<WebData> mWebDataList = new ArrayList<>();
    ArrayList<WebData> mNewDataList = new ArrayList<>();

    ListView mListView;
    RawPhotoListAdapter mAdapter;

    int mCurrentPage = 1;
    int mTotalItem = 0;
    int mTotalPage = 0;

    //CacheManager mCacheManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.raw_photo_list_activity);

        mContext = RawPhotoListActivity.this;
        mResources = mContext.getResources();

        Intent intent = getIntent();
        mGroupData = (GroupData) intent.getSerializableExtra("groupData");
        mWebData = (WebData) intent.getSerializableExtra("webData");

        mTitle = mWebData.getName();
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, mTitle);

        mPbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        Util.setProgressBarColor(mPbLoading, Config.PROGRESS_BAR_COLOR_NORMAL, null);

        //mLoLoadingMore = (LinearLayout) findViewById(R.id.loLoadingMore);

        mListView = (ListView) findViewById(R.id.listView);
        if (mListView != null) {
            mListView.setVisibility(View.VISIBLE);
            mAdapter = new RawPhotoListAdapter(this, mWebDataList);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    WebData webData = (WebData) adapterView.getItemAtPosition(i);
                    goWebSite(webData.getUrl());
                }
            });

            mListView.setOnScrollListener(new EndlessScrollListener() {
                @Override
                public boolean onLoadMore(int page, int totalItemsCount) {
                    //Log.e(mTag, "onLoadMore().page: " + page + " / " + mMaxPage);
                    loadData();
                    return true; // ONLY if more data is actually being loaded; false otherwise.
                }
            });
        }

        loadData();
    }

    private void loadData() {
        String url = mWebData.getUrl(); //"http://suzukazedayori.com/";
        String userAgent = Config.USER_AGENT_WEB;

        if (mCurrentPage > 1) {
            // http://suzukazedayori.com/?mode=cate&cbid=1939271&csid=20&sort=n&page=2
            url = url + "&page=" + mCurrentPage;
            showToolbarProgressBar();
            //mLoLoadingMore.setVisibility(View.VISIBLE);
        }

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
                //Log.e(mTag, response);
                //mCacheManager.save(cacheId, response);
                parseData(url, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(mTag, "onErrorResponse(): " + url);
                mErrorMessage = Util.getErrorMessage(error);
                //parseData(url, "");
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

        String tag_string_req = "string_req";
        BaseApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
        //} else {
        //    parseData(url, cacheData);
        //}
    }

    private void parseData(String url, String response) {
        RawPhotoParser rawPhotoParser = new RawPhotoParser();
        //rawPhotoParser.parseSuzukazedayoriList(response, mGroupData.getId(), mNewDataList);
        mTotalItem = rawPhotoParser.parseShopproList(response, mGroupData.getId(), mNewDataList);
        //Log.e(mTag, "mNewDataList.size(): " + mNewDataList.size());
        //Log.e(mTag, "mTotalItem: " + mTotalItem);

        renderData();
    }

    private void renderData() {
        mPbLoading.setVisibility(View.GONE);

        if (mNewDataList.size() == 0 && mWebDataList.size() == 0) {
            alertNetworkErrorAndFinish(mErrorMessage);
        } else {
            if (mNewDataList.size() > 0) {
                mWebDataList.addAll(mNewDataList);
                mAdapter.notifyDataSetChanged();

                mTotalPage = (int) Math.round((float) mTotalItem / 24.0);
                int currentPage = mCurrentPage;
                if (mCurrentPage > mTotalPage) currentPage = mTotalPage;
                String title = mTitle + " (" + currentPage + "/" + mTotalPage + ")";
                mBaseToolbar.setTitle(title);
            }

            hideToolbarProgressBar();
            //mLoLoadingMore.setVisibility(View.GONE);
            mNewDataList.clear();
            mCurrentPage++;
        }
    }

    private void goTop() {
        mListView.setSelection(0);
    }

    protected void onToolbarClick() {
        goTop();
    }

    public void goWebSite(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivityForResult(intent, 100);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
