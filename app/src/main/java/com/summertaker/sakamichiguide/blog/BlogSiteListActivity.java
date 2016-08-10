package com.summertaker.sakamichiguide.blog;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
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
import com.summertaker.sakamichiguide.parser.Keyakizaka46Parser;
import com.summertaker.sakamichiguide.parser.Nogizaka46Parser;
import com.summertaker.sakamichiguide.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class BlogSiteListActivity extends BaseActivity {

    SiteData mSiteData;

    Snackbar mSnackbar;
    String mTitle;
    MenuItem mMenuItemSetting;

    //int mBlogTotal = 0;
    int mCheckCount = 0;
    BlogSiteListAdapter mAdapter;
    ArrayList<SiteData> mBlogList = new ArrayList<>();

    LinearLayout mLoLoading;
    ProgressBar mPbLoading;
    TextView tvLoadingName;
    TextView tvLoadingCount;
    ProgressBar mPbLoadingHorizontal;

    CacheManager mCacheManager;
    String mCacheId;
    boolean mIsCacheValid = false;

    ArrayList<SiteData> mCheckSettings = new ArrayList<>();
    ArrayList<SiteData> mBlogDateList = new ArrayList<>();

    String mClickUrl = "";

    boolean mIsLoadFinished = false;

    //JSONArray mCheckSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_site_list_activity);

        mContext = BlogSiteListActivity.this;
        mResources = mContext.getResources();

        Intent intent = getIntent();
        mSiteData = (SiteData) intent.getSerializableExtra("siteData");
        //Log.e(mTag, "id: " + mSiteData.getId());

        RelativeLayout content = (RelativeLayout) findViewById(R.id.content);
        mSnackbar = Snackbar.make(content, "", Snackbar.LENGTH_SHORT);
        View view = mSnackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
        //tv.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        mTitle = mSiteData.getName();
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, mTitle);

        mLoLoading = (LinearLayout) findViewById(R.id.loLoading);
        mPbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        Util.setProgressBarColor(mPbLoading, Config.PROGRESS_BAR_COLOR_NORMAL, null);
        tvLoadingName = (TextView) findViewById(R.id.tvLoadingName);
        tvLoadingCount = (TextView) findViewById(R.id.tvLoadingCount);
        mPbLoadingHorizontal = (ProgressBar) findViewById(R.id.pbLoadingHorizontal);
        if (mPbLoadingHorizontal != null) {
            mPbLoadingHorizontal.setProgress(0);
        }

        mCacheManager = new CacheManager(mSharedPreferences);
        mCacheId = mSiteData.getId();

        // 캐쉬 데이터: 사용자가 선택한 체크할 블로그 URL 목록
        String cacheId = mCacheId + Config.CACHE_ID_BLOG_CHECK_SUFFIX;
        JSONObject checkObject = mCacheManager.loadJsonObject(cacheId, 0); // Minutes, 0 = No Expire
        if (checkObject != null) {
            try {
                //Log.e(mTag, "object: " + checkObject.toString());
                JSONArray jsonArray = checkObject.getJSONArray("data");
                //Log.e(mTag, "mCheckSettings.length(): " + mCheckSettings.length());

                //Log.e(mTag, "== SETTING.....................");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String name = obj.getString("name");
                    String url = obj.getString("url");
                    String date = obj.getString("date");
                    //Log.e(mTag, name + " / " + date);

                    SiteData data = new SiteData();
                    data.setName(name);
                    data.setUrl(url);
                    data.setUpdateCheckDate(date);
                    mCheckSettings.add(data);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 사용자가 업데이터 체크 설정을 변경한 경우 캐쉬 데이터를 무시하고 체크를 진행한다.
        String listChanged = mSharedPreferences.getString(mCacheId + Config.CACHE_ID_BLOG_LIST_CHANGED, "");
        //Log.e(mTag, "listChanged : " + listChanged);
        if (!listChanged.isEmpty()) {
            Setting setting = new Setting(mContext);
            setting.set(mCacheId + Config.CACHE_ID_BLOG_LIST_CHANGED, "");
            mIsCacheValid = false;
        }

        String url = mSiteData.getUrl();
        String userAgent = Config.USER_AGENT_WEB;
        requestData(url, userAgent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blog_member_list, menu);
        mMenuItemSetting = menu.findItem(R.id.action_settings);
        //mMenuItemSetting.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            if (mIsLoadFinished) {
                goSettings();
            } else {
                mSnackbar.setText(getString(R.string.please_wait)).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 네트워크 데이터 - 가져오기
     */
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
                alertNetworkErrorAndFinish(mErrorMessage);
                //parseData(url, "");
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
        if (response.isEmpty() && url.equals(mSiteData.getUrl())) {
            renderData();
        } else {
            // 블로그 목록 파싱하기
            if (url.equals(mSiteData.getUrl())) {
                switch (mSiteData.getId()) {
                    case Config.BLOG_ID_NOGIZAKA46_MEMBER:
                        Nogizaka46Parser nogizaka46Parser = new Nogizaka46Parser();
                        nogizaka46Parser.parseBlogSiteList(response, mBlogList);
                        break;
                    case Config.BLOG_ID_KEYAKIZAKA46_MEMBER:
                        Keyakizaka46Parser keyakizaka46Parser = new Keyakizaka46Parser();
                        String text = keyakizaka46Parser.parseBlogSiteList(response, mBlogList);
                        parseKeyakizaka46UpdateDate(text);
                        renderData();
                        break;
                }
            } else {
                if (mErrorMessage != null && !mErrorMessage.isEmpty()) {
                    mSnackbar.setText(mErrorMessage + " - " + mCheckSettings.get(mCheckCount).getName()).show();
                }
            }
            //Log.e(mTag, "mBlogTotal: " + mBlogTotal);

            if (mSiteData.getId().equals(Config.BLOG_ID_NOGIZAKA46_MEMBER)) {
                // 노기자카46인 경우 블로그 하나씩 접속해서 업데이트 날짜 가져오기
                if (mIsCacheValid || mCheckSettings.size() == 0) {
                    updateData();
                    renderData();
                } else {
                    if (mCheckCount > 0) {
                        Nogizaka46Parser nogizaka46Parser = new Nogizaka46Parser();
                        String date = nogizaka46Parser.parseBlogUpdateDate(response);
                        mCheckSettings.get(mCheckCount - 1).setUpdateDate(date);
                    }

                    if (mCheckCount == mCheckSettings.size()) {
                        saveCache();
                        updateData();
                        renderData();
                    } else {
                        SiteData site = mCheckSettings.get(mCheckCount);
                        String siteName = site.getName();
                        String siteUrl = site.getUrl();
                        String userAgent = Config.USER_AGENT_WEB;
                        //Log.e(mTag, "Request... " + siteName);

                        updateProgress(siteName);
                        requestData(siteUrl, userAgent);

                        mCheckCount++;
                    }
                }
            }
        }
    }

    private void updateProgress(String name) {
        if (mCheckCount == 0) {
            LinearLayout loLoadingHorizontal = (LinearLayout) findViewById(R.id.loLoadingHorizontal);
            loLoadingHorizontal.setVisibility(View.VISIBLE);
        }

        tvLoadingName.setText(name);

        int count = mCheckCount + 1;

        String text = "( " + count + " / " + mCheckSettings.size() + " )";
        tvLoadingCount.setText(text);

        float progress = (float) count / (float) mCheckSettings.size();
        int progressValue = (int) (progress * 100.0);
        //Log.e(mTag, mUrlCount + " / " + mUrlTotal + " = " + progressValue);
        mPbLoadingHorizontal.setProgress(progressValue);
    }

    private void parseKeyakizaka46UpdateDate(String text) {
        //Log.e(mTag, text);

        if (mCheckSettings.size() == 0) {
            return;
        };

        String checkFormat = "yyyy/MM/dd HH:mm:ss";
        SimpleDateFormat checkSdf = new SimpleDateFormat(checkFormat, Locale.getDefault());

        String updateFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
        //Log.e(mTag, format);
        SimpleDateFormat updateSdf = new SimpleDateFormat(updateFormat, Locale.getDefault());
        updateSdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        //Calendar calendar = Calendar.getInstance();

        try {
            //Log.e(mTag, "mCheckSettings.size(): " + mCheckSettings.size());
            //Log.e(mTag, "mBlogList.size(): " + mBlogList.size());

            for (int i = 0; i < mCheckSettings.size(); i++) {
                String settingUrl = mCheckSettings.get(i).getUrl();
                String checkDate = mCheckSettings.get(i).getUpdateCheckDate();
                //Log.e(mTag, "= setting: " + mCheckSettings.get(i).getName() + " / " + settingUrl);

                for (int j = 0; j < mBlogList.size(); j++) {
                    String serial = mBlogList.get(j).getSerial();
                    String url = mBlogList.get(j).getUrl();
                    //Log.e(mTag, "= blog: " + mBlogList.get(j).getName() + " / " + url);
                    //Log.e(mTag, "serial: " + serial);

                    //今泉 佑唯 / http://www.keyakizaka46.com/mob/news/diarKiji.php?site=k46o&ima=2144&cd=member&ct=02
                    //今泉 佑唯 / http://www.keyakizaka46.com/mob/news/diarKiji.php?site=k46o&ima=2149&cd=member&ct=02

                    if (url.equals(settingUrl)) {
                        //Log.e(mTag, "Found................");

                        JSONArray jsonArray = new JSONArray(text);
                        //Log.e(mTag, "jsonArray.length(): " + jsonArray.length());

                        for (int k = 0; k < jsonArray.length(); k++) {
                            JSONObject object = jsonArray.getJSONObject(k);
                            String updateSerial = object.getString("member");
                            //Log.e(mTag, "updateSerial: " + updateSerial);

                            if (updateSerial.equals(serial)) {
                                String updateDate = object.getString("update"); // 2016-07-17T14:34+09:00
                                updateDate = updateDate.replace("+09", ":00+09");
                                //Log.e(mTag, updateDate);

                                Date d1 = updateSdf.parse(updateDate);
                                Date d2 = checkSdf.parse(checkDate);
                                long diff = d1.getTime() - d2.getTime();
                                //Log.e(mTag, "diff: " + diff);

                                boolean updated = (diff > 0);

                                mBlogList.get(j).setUpdateDate(updateDate);
                                mBlogList.get(j).setUpdateCheckDate(checkDate);
                                mBlogList.get(j).setUpdated(updated);
                                //Log.e(mTag, mBlogList.get(j).getName());
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
            Log.e(mTag, e.getMessage());
        }
    }

    private void saveCache() {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray();
            for (SiteData site : mCheckSettings) {
                JSONObject object = new JSONObject();
                object.put("url", site.getUrl());
                object.put("date", site.getUpdateDate());
                jsonArray.put(object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //if (mOldDateArray == null) {
        //    mOldDateArray = jsonArray;
        //}
        //Log.e(mTag, "mNewDateArray.length(): " + mNewDateArray.length());
        mCacheManager.save(mCacheId, jsonArray);
    }

    private void updateData() {
        /*String oldstring = "2011-01-18 00:00:00.0";
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(oldstring);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        String format = "yyyy/MM/dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());

        try {
            //Log.e(mTag, "mBlogList.size(): " + mBlogList.size());

            for (int i = 0; i < mBlogList.size(); i++) {
                String url = mBlogList.get(i).getUrl();

                for (SiteData setting : mCheckSettings) {

                    if (url.equals(setting.getUrl())) {
                        String updateDate = setting.getUpdateDate();
                        String checkDate = setting.getUpdateCheckDate();
                        //Log.e(mTag, updateDate + " / " + checkDate);

                        Date d1 = sdf.parse(updateDate);
                        Date d2 = sdf.parse(checkDate);
                        long diff = d1.getTime() - d2.getTime();
                        //Log.e(mTag, "diff: " + diff);

                        boolean updated = (diff > 0);

                        mBlogList.get(i).setUpdateDate(updateDate);
                        mBlogList.get(i).setUpdateCheckDate(checkDate);
                        mBlogList.get(i).setUpdated(updated);
                        break;
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void renderData() {
        //mPbLoading.setVisibility(View.GONE);
        mLoLoading.setVisibility(View.GONE);

        //String team = getString(R.string.team);
        //String trainee = getString(R.string.trainee);

        if (mBlogList.size() == 0) {
            alertNetworkErrorAndFinish(mErrorMessage);
        } else {
            String title = " (" + mBlogList.size() + ")"; //getResources().getString(R.string.s_people, mBlogList.size());
            mBaseToolbar.setTitle(mTitle + title);

            // http://stackoverflow.com/questions/10692755/how-do-i-hide-a-menu-item-in-the-actionbar
            //invalidateOptionsMenu();
            //mMenuItemSetting.setVisible(true);

            GridView gridView = (GridView) findViewById(R.id.gridView);
            if (gridView != null) {
                gridView.setVisibility(View.VISIBLE);
                mAdapter = new BlogSiteListAdapter(this, mBlogList);
                gridView.setAdapter(mAdapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SiteData siteData = (SiteData) parent.getItemAtPosition(position);
                        mClickUrl = siteData.getUrl();
                        //siteData.setId(mSiteData.getId());

                        Intent intent = new Intent(mContext, BlogArticleListActivity.class);
                        intent.putExtra("siteData", siteData);

                        showToolbarProgressBar();
                        startActivityForResult(intent, 0);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
            }

            mIsLoadFinished = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mAdapter != null) {
            //Log.e(mTag, "mClickUrl: " + mClickUrl);

            for (int i = 0; i < mBlogList.size(); i++) {
                String url = mBlogList.get(i).getUrl();

                if (url.equals(mClickUrl)) {
                    //Log.e(mTag, "url: " + url);
                    mBlogList.get(i).setUpdated(false);
                    break;
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    private void goSettings() {
        Intent intent = new Intent(mContext, BlogSettingActivity.class);
        intent.putExtra("siteData", mSiteData);
        intent.putExtra("blogList", mBlogList);

        startActivityForResult(intent, 0);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Config.RESULT_CODE_FINISH) {
            mSiteData = (SiteData) data.getSerializableExtra("siteData");

            Intent intent = new Intent();
            intent.putExtra("siteData", mSiteData);
            setResult(Config.RESULT_CODE_FINISH, intent);
            finish();
        }
        hideToolbarProgressBar();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
