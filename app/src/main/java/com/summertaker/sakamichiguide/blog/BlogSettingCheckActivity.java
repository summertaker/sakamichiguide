package com.summertaker.sakamichiguide.blog;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseActivity;
import com.summertaker.sakamichiguide.common.CacheManager;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.common.Setting;
import com.summertaker.sakamichiguide.data.SiteData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class BlogSettingCheckActivity extends BaseActivity implements BlogAdapterInterface {

    SiteData mSiteData;

    String mTitle;
    Snackbar mSnackbar;

    String mCacheId;
    CacheManager mCacheManager;

    ArrayList<SiteData> mBlogList = new ArrayList<>();
    BlogSettingCheckAdapter mAdapter;

    JSONArray mCheckSettings;

    boolean isAllChecked = false;
    Button mBtCheckAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_setting_check_activity);

        mContext = BlogSettingCheckActivity.this;

        Intent intent = getIntent();
        mSiteData = (SiteData) intent.getSerializableExtra("siteData");
        mBlogList = (ArrayList<SiteData>) intent.getSerializableExtra("blogList");

        mTitle = getString(R.string.select_the_blog);
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, mTitle);

        RelativeLayout content = (RelativeLayout) findViewById(R.id.content);
        mSnackbar = Snackbar.make(content, "", Snackbar.LENGTH_SHORT);
        View view = mSnackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
        //tv.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        mCacheManager = new CacheManager(mSharedPreferences);
        mCacheId = mSiteData.getId();

        JSONObject jsonObject = mCacheManager.loadJsonObject(mCacheId + Config.CACHE_ID_BLOG_CHECK_SUFFIX, 0); // Minutes, 0 = No Expire
        if (jsonObject != null) {
            try {
                //Log.e(mTag, "jsonObject: " + jsonObject.toString());
                mCheckSettings = jsonObject.getJSONArray("data");
                //Log.e(mTag, "mOldDateArray.length(): " + mOldDateArray.length());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Button btSave = (Button) findViewById(R.id.btSave);
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSave();
            }
        });

        mBtCheckAll = (Button) findViewById(R.id.btCheckAll);
        mBtCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doCheckAll();
            }
        });

        renderData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.blog_member_check, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            doSave();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void renderData() {
        try {
            for (SiteData siteData : mBlogList) {
                boolean checked = false;

                if (mCheckSettings != null) {
                    for (int i = 0; i < mCheckSettings.length(); i++) {
                        JSONObject obj = mCheckSettings.getJSONObject(i);
                        String url = obj.getString("url");
                        if (url.equals(siteData.getUrl())) {
                            checked = true;
                            break;
                        }
                    }
                }
                siteData.setChecked(checked);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        updateTitle();

        LinearLayout loLoading = (LinearLayout) findViewById(R.id.loLoading);
        loLoading.setVisibility(View.GONE);

        GridView gridView = (GridView) findViewById(R.id.gridView);
        if (gridView != null) {
            gridView.setVisibility(View.VISIBLE);
            mAdapter = new BlogSettingCheckAdapter(this, mBlogList, this);
            gridView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onPictureClick(int position) {

    }

    @Override
    public void onCheckboxClick(int position) {
        //Log.e(mTag, "position: " + position);
        SiteData siteData = mBlogList.get(position);
        siteData.setChecked(!siteData.isChecked());
        mAdapter.notifyDataSetChanged();
        updateTitle();
    }

    @Override
    public void onContentClick(int position) {

    }

    private void updateTitle() {
        int count = 0;
        for (SiteData data : mBlogList) {
            if (data.isChecked()) {
                count++;
            }
        }
        //String title = mTitle + " (" + String.format(getString(R.string.s_people), count) + ")";
        String title = mTitle + " (" + count + ")";
        mBaseToolbar.setTitle(title);
    }

    private void doCheckAll() {
        isAllChecked = !isAllChecked;
        for (SiteData site : mBlogList) {
            site.setChecked(isAllChecked);
        }
        mAdapter.notifyDataSetChanged();

        String text = isAllChecked ? getString(R.string.select_none) : getString(R.string.select_all);
        mBtCheckAll.setText(text);

        updateTitle();
    }

    private void doSave() {
        //String oldstring = "2011-01-18 00:00:00.0";
        //Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(oldstring);

        //String newstring = new SimpleDateFormat("yyyy-MM-dd").format(date);
        //System.out.println(newstring); // 2011-01-18

        String format = "yyyy/MM/dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        String now = sdf.format(calendar.getTime());
        //Log.e(mTag, "now: " + now);

        JSONArray jsonArray = new JSONArray();
        try {
            for (SiteData site : mBlogList) {
                if (site.isChecked()) {
                    String date = now;

                    if (mCheckSettings != null) {
                        for (int i = 0; i < mCheckSettings.length(); i++) {
                            JSONObject obj = mCheckSettings.getJSONObject(i);
                            if (obj.getString("url").equals(site.getUrl())) {
                                date = obj.getString("date");
                            }
                        }
                    }
                    //Log.e(mTag, site.getName() + " / " + date);

                    JSONObject object = new JSONObject();
                    object.put("name", site.getName());
                    object.put("url", site.getUrl());
                    object.put("date", date);
                    jsonArray.put(object);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.e(mTag, "jSONArray.length(): " + jSONArray.length());
        mCacheManager.save(mCacheId + Config.CACHE_ID_BLOG_CHECK_SUFFIX, jsonArray);

        Setting setting = new Setting(mContext);
        setting.set(mCacheId + Config.CACHE_ID_BLOG_LIST_CHANGED, "y");

        //mSnackbar.setText(getString(R.string.saved)).show();

        Intent intent = new Intent();
        intent.putExtra("siteData", mSiteData);
        setResult(Config.RESULT_CODE_FINISH, intent);
        finish();
    }
}
