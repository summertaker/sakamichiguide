package com.summertaker.sakamichiguide.blog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.summertaker.sakamichiguide.common.CacheManager;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.common.Setting;
import com.summertaker.sakamichiguide.data.SiteData;
import com.summertaker.sakamichiguide.data.WebData;
import com.summertaker.sakamichiguide.parser.BaseParser;
import com.summertaker.sakamichiguide.parser.Keyakizaka46Parser;
import com.summertaker.sakamichiguide.parser.Nogizaka46Parser;
import com.summertaker.sakamichiguide.util.EndlessScrollListener;
import com.summertaker.sakamichiguide.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BlogArticleListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener { //}, BlogAdapterInterface {

    private String mTitle;
    private SiteData mSiteData;

    private Snackbar mSnackbar;
    private ProgressBar mPbLoading;
    private SwipeRefreshLayout mSwipeRefresh;

    private ArrayList<WebData> mWebDataList;
    private ArrayList<WebData> mNewDataList;
    private BlogArticleListAdapter mAdapter;
    private ListView mListView;

    private String mRssUrl;

    private int mMaxPage = 0;
    private int mCurrentPage = 1;
    //private int mVisibleThreshold = 5;

    private boolean mIsLoading = false;
    private boolean mIsRefreshMode = false;
    private String mLatestDataId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_article_activity);

        mContext = BlogArticleListActivity.this;

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Intent intent = getIntent();
        mSiteData = (SiteData) intent.getSerializableExtra("siteData");
        mRssUrl = mSiteData.getRssUrl();

        mTitle = mSiteData.getName();
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, mTitle);

        RelativeLayout content = (RelativeLayout) findViewById(R.id.content);
        if (content != null) {
            mPbLoading = (ProgressBar) findViewById(R.id.pbLoading);
            Util.setProgressBarColor(mPbLoading, Config.PROGRESS_BAR_COLOR_NORMAL, null);

            mSnackbar = Snackbar.make(content, "", Snackbar.LENGTH_SHORT);
            View view = mSnackbar.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
            //tv.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            // 최종 업데이트 날짜 저장
            saveUpdateCheckDate();

            mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
            if (mSwipeRefresh != null) {
                mSwipeRefresh.setOnRefreshListener(this);

                if (mRssUrl != null && !mRssUrl.isEmpty()) {
                    mMaxPage = 1;
                } else {
                    switch (mSiteData.getId()) {
                        case Config.BLOG_ID_NOGIZAKA46_MEMBER:
                            mMaxPage = 15;
                            break;
                        case Config.BLOG_ID_KEYAKIZAKA46_MEMBER:
                            break;
                    }
                }

                mWebDataList = new ArrayList<>();
                mNewDataList = new ArrayList<>();

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
                    mListView.setOnScrollListener(new EndlessScrollListener() {
                        @Override
                        public boolean onLoadMore(int page, int totalItemsCount) {
                            //Log.e(mTag, "onLoadMore().page: " + page + " / " + mMaxPage);
                            if (mMaxPage == 1) {
                                showLastDataMessage();
                            } else if (mMaxPage > 1 && page > mMaxPage) {
                                showLastDataMessage();
                            } else {
                                mIsRefreshMode = false;
                                loadData();
                            }
                            return true; // ONLY if more data is actually being loaded; false otherwise.
                        }
                    });
                }

                loadData();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blog_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_open_in_web_browser) {
            goWebSite();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLastDataMessage() {
        mSnackbar.setText(getString(R.string.this_is_the_last_of_the_data));
        mSnackbar.show();
    }

    private void loadData() {
        //Log.e(mTag, "loadData()... mCurrentPage: " + mCurrentPage);

        if (mIsLoading) {
            return;
        }

        String url = mRssUrl == null || mRssUrl.isEmpty() ? mSiteData.getUrl() : mRssUrl;

        if (!mIsRefreshMode) {
            //if (mMaxPage > 0 && mCurrentPage > mMaxPage) {
            //    return;
            //}
            //Log.e(mTag, "mMaxPage: " + mMaxPage);

            if (mCurrentPage > 1) {
                switch (mSiteData.getId()) {
                    case Config.BLOG_ID_NOGIZAKA46_MEMBER:
                        url = url + "?p=" + mCurrentPage;
                        break;
                    case Config.BLOG_ID_KEYAKIZAKA46_MEMBER:
                        url = url + "&page=" + (mCurrentPage - 1);
                        break;
                }
                showToolbarProgressBar();
            }
        }

        String userAgent = Config.USER_AGENT_WEB;

        mIsLoading = true;
        requestData(url, userAgent);
    }

    /*private void setYearMonth() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, mMonthCount);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1; // beware of month indexing from zero

        mYearMonth = year + "";
        mYearMonth += (month < 10) ? "0" + month : month;
    }*/

    /*private String getNextBlogUrl() {
        setYearMonth();
        return "http://ameblo.jp/" + mAmebaId + "/imagelist-" + mYearMonth + ".html";
    }*/

    /*private String getNextJsonUrl() {
        setYearMonth();
        String url = "http://blogimgapi.ameba.jp/image_list/get.jsonp";
        url += "?limit=18&sp=false&page=2&ameba_id=" + mAmebaId + "&target_ym=" + mYearMonth;
        return url;
    }*/

    private void requestData(final String url, final String userAgent) {
        //Log.e(mTag, "requestData(): " + url);
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
                Log.e(mTag, "onErrorResponse(): " + url);
                mSnackbar.setText(getString(R.string.network_error_occurred)).show();
                mErrorMessage = Util.getErrorMessage(error);
                parseData("");
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
        //Log.e(mTag, "response: " + response.substring(0, 100));
        //Log.e(mTag, "parseList()... " + mSiteData.getGroupId());

        if (!response.isEmpty()) {
            if (mRssUrl != null && !mRssUrl.isEmpty()) {
                BaseParser parser = new BaseParser();
                parser.parseAmebaRss(response, mNewDataList);
            } else {
                switch (mSiteData.getId()) {
                    case Config.BLOG_ID_NOGIZAKA46_MEMBER:
                        Nogizaka46Parser nogizaka46Parser = new Nogizaka46Parser();
                        nogizaka46Parser.parseBlogList(response, mNewDataList);
                        break;
                    case Config.BLOG_ID_KEYAKIZAKA46_MEMBER:
                        Keyakizaka46Parser keyakizaka46Parser = new Keyakizaka46Parser();
                        keyakizaka46Parser.parseBlogList(response, mNewDataList);
                        break;
                }
            }
        }

        mPbLoading.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);

        renderData();
    }

    private void renderData() {
        //Log.e(mTag, "renderData()... mNewDataList.size(): " + mNewDataList.size());

        //String title = mTitle;
        //if (!mSiteData.getBlogUrl().contains(".xml")) {
        //    title += " (" + mCurrentPage + "/" + mMaxPage + ")";
        //}
        //mBaseToolbar.setTitle(title);

        if (mNewDataList.size() == 0 && mCurrentPage == 1) {
            //Log.e(mTag, "mSiteData.getUrl(): " + mSiteData.getUrl());
            //Log.e(mTag, "mNewDataList.size(): " + mNewDataList.size());
            alertNetworkErrorAndFinish(mErrorMessage);
        } else {
            if (mIsRefreshMode) {
                //Log.e(mTag, "mNewDataList.size(): " + mNewDataList.size());

                int newCount = 0;
                ArrayList<WebData> uniqueList = new ArrayList<>();
                for (WebData webData : mNewDataList) {
                    if (webData.getId().equals(mLatestDataId)) {
                        break;
                    }
                    uniqueList.add(0, webData);
                    newCount++;
                }
                //Log.e(mTag, "uniqueList.size(): " + uniqueList.size());

                if (newCount == 0) {
                    mSnackbar.setText(getString(R.string.no_new_data)).show();
                } else {
                    Collections.reverse(uniqueList);
                    for (WebData webData : uniqueList) {
                        mWebDataList.add(0, webData);
                    }
                }
                mSwipeRefresh.setRefreshing(false);
                mIsRefreshMode = false;
                //goTop();
            } else {
                if (mNewDataList.size() == 0) {
                    showLastDataMessage();
                } else {
                    mWebDataList.addAll(mNewDataList);
                    mAdapter.notifyDataSetChanged();
                    mCurrentPage++;
                }
            }
        }

        if (mWebDataList.size() > 0) {
            WebData latestData = mWebDataList.get(0);
            mLatestDataId = latestData.getId();
        }
        //Log.e(mTag, "mLatestDataId: " + mLatestDataId);

        mNewDataList.clear();
        mIsLoading = false;
        hideToolbarProgressBar();
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

    private void saveUpdateCheckDate() {
        //String oldstring = "2011-01-18 00:00:00.0";
        //Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(oldstring);

        //String newstring = new SimpleDateFormat("yyyy-MM-dd").format(date);
        //System.out.println(newstring); // 2011-01-18

        CacheManager cacheManager = new CacheManager(mSharedPreferences);
        String cacheId = mSiteData.getId() + Config.CACHE_ID_BLOG_CHECK_SUFFIX;

        JSONObject jsonObject = cacheManager.loadJsonObject(cacheId, 0); // Minutes, 0 = No Expire
        //Log.e(mTag, "jsonObject: " + jsonObject.toString());

        if (jsonObject == null || jsonObject.toString().isEmpty()) {
            return;
        }

        try {
            JSONArray cacheDatas = jsonObject.getJSONArray("data");
            //Log.e(mTag, "cacheDatas.length(): " + cacheDatas.length());

            String format = "yyyy/MM/dd HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());

            Calendar calendar = Calendar.getInstance();
            String now = sdf.format(calendar.getTime());
            //Log.e(mTag, "now: " + now);

            //Log.e(mTag, "url: " + mSiteData.getUrl());

            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < cacheDatas.length(); i++) {
                JSONObject cache = cacheDatas.getJSONObject(i);

                String name = cache.getString("name");
                String url = cache.getString("url");
                String date = cache.getString("date");
                //Log.e(mTag, name + " / " + " / " + url + " / " + date);

                if (url.equals(mSiteData.getUrl())) {
                    date = now;
                }

                JSONObject object = new JSONObject();
                object.put("name", name);
                object.put("url", url);
                object.put("date", date);
                jsonArray.put(object);
            }

            //Log.e(mTag, "jSONArray.length(): " + jSONArray.length());
            cacheManager.save(cacheId, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            //startActivity(intent);
            startActivityForResult(intent, 100);
            //showToolbarProgressBar();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    public void goWebSite() {
        //Log.e(mTag, "onContentClick(" + position + ")");

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mSiteData.getUrl()));
        //intent.putExtra("url", webData.getUrl());
        //startActivity(intent);
        startActivityForResult(intent, 100);
        //showToolbarProgressBar();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //hideToolbarProgressBar();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
