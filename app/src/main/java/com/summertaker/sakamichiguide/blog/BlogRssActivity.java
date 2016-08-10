package com.summertaker.sakamichiguide.blog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseActivity;
import com.summertaker.sakamichiguide.common.BaseApplication;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.data.SiteData;
import com.summertaker.sakamichiguide.data.WebData;
import com.summertaker.sakamichiguide.parser.BaseParser;
import com.summertaker.sakamichiguide.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BlogRssActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener { //}, BlogAdapterInterface {

    private SiteData mSiteData;

    private Snackbar mSnackbar;
    private SwipeRefreshLayout mSwipeRefresh;

    private ArrayList<WebData> mWebDataList;
    private ArrayList<WebData> mNewDataList;
    private BlogArticleListAdapter mAdapter;
    private ListView mListView;

    private boolean mIsLoading = false;
    private boolean mIsRefreshMode = false;

    boolean isLastMessageDisplayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_rss_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        mSiteData = (SiteData) intent.getSerializableExtra("blogData");

        String title = mSiteData.getName();
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, title);

        mWebDataList = new ArrayList<>();
        mNewDataList = new ArrayList<>();

        RelativeLayout content = (RelativeLayout) findViewById(R.id.content);
        if (content != null) {
            mSnackbar = Snackbar.make(content, "", Snackbar.LENGTH_SHORT);
            View view = mSnackbar.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
            //tv.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }

        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setOnRefreshListener(this);

            mListView = (ListView) findViewById(R.id.listView);
            if (mListView != null) {
                mAdapter = new BlogArticleListAdapter(this, mSiteData, mWebDataList); //, this);
                mListView.setAdapter(mAdapter);
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        onContentClick(position);
                    }
                });
                mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        int count = totalItemCount - visibleItemCount;
                        if (firstVisibleItem >= count && totalItemCount != 0) {
                            if (!isLastMessageDisplayed) {
                                //Log.e(mTag, "Loading next items");
                                mSnackbar.setText(getString(R.string.this_is_the_last_of_the_data));
                                mSnackbar.show();
                                isLastMessageDisplayed = true;
                            }
                        }
                    }
                });
            }
        }

        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blog_rss, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_open_in_web_browser) {
            goBlogSite();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        if (mIsLoading) {
            return;
        }

        mIsLoading = true;

        if (!mIsRefreshMode) {
            showToolbarProgressBar();
        }
        requestData(mSiteData.getRssUrl(), Config.USER_AGENT_WEB);
    }

    private void requestData(final String url, final String userAgent) {
        //Log.e(mTag, "url: " + url);
        //Log.e(mTag, "userAgent: " + userAgent);

        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e(mTag, "response: " + response.substring(0, 100));
                parseData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = Util.getErrorMessage(error);
                alertNetworkErrorAndFinish(message);
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

        // Adding request to request queue
        String tag_string_req = "string_req";
        BaseApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void parseData(String response) {
        //Log.e(mTag, "parseList()... " + mGroupData.getGroupId());

        BaseParser parser = new BaseParser();
        parser.parseAmebaRss(response, mNewDataList);

        //WebData webData = new WebData();
        //webData.setContent(getString(R.string.last));
        //mNewDataList.add(webData);

        renderData();
    }

    private void renderData() {
        //Log.e(mTag, "renderData()... mNewDataList.size(): " + mNewDataList.size());

        mWebDataList.clear();
        mWebDataList.addAll(mNewDataList);
        mNewDataList.clear();
        mAdapter.notifyDataSetChanged();

        if (mIsRefreshMode) {
            mSwipeRefresh.setRefreshing(false);
            mIsRefreshMode = false;
        } else {
            hideToolbarProgressBar();
        }

        mIsLoading = false;
    }

    private void goTop() {
        mListView.setSelection(0);
    }

    protected void onToolbarClick() {
        goTop();
    }

    @Override
    public void onRefresh() {
        //Log.e(mTag, "onRefresh().....");
        mIsRefreshMode = true;
        loadData();
    }

    //@Override
    public void onPictureClick(int position) {
        //Log.e(mTag, "onPictureClick(" + position + ")");

        onContentClick(position);

        /*WebData webData = mWebDataList.get(position);
        String imageUrl = webData.getImageUrl();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl));
        startActivityForResult(intent, 100);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
    }

    //@Override
    public void onContentClick(int position) {
        //Log.e(mTag, "onContentClick(" + position + ")");

        WebData webData = mWebDataList.get(position);
        String url = webData.getUrl();
        if (url != null && !url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            //Intent intent = new Intent(mContext, WebViewActivity.class);
            //intent.putExtra("title", webData.getTitle());
            //intent.putExtra("url", webData.getUrl());
            startActivityForResult(intent, 100);
            showToolbarProgressBar();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    public void goBlogSite() {
        //Log.e(mTag, "onContentClick(" + position + ")");

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mSiteData.getUrl()));
        //intent.putExtra("url", webData.getUrl());
        startActivityForResult(intent, 100);
        //showToolbarProgressBar();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        hideToolbarProgressBar();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

