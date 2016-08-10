package com.summertaker.sakamichiguide.blog;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseActivity;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.data.DataManager;
import com.summertaker.sakamichiguide.data.SiteData;

import java.util.ArrayList;

public class BlogGroupSelectActivity extends BaseActivity {

    private Snackbar mSnackbar;

    ArrayList<SiteData> mSiteDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_group_select_activity);

        mContext = BlogGroupSelectActivity.this;

        initBaseToolbar(Config.TOOLBAR_ICON_BACK, getString(R.string.blog));

        RelativeLayout content = (RelativeLayout) findViewById(R.id.content);
        mSnackbar = Snackbar.make(content, "", Snackbar.LENGTH_SHORT);
        View view = mSnackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
        //tv.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        DataManager dataManager = new DataManager(mContext);
        mSiteDatas = dataManager.getBlogList();

        GridView gridView = (GridView) findViewById(R.id.gridView);
        if (gridView != null) {
            BlogGroupSelectGridAdapter adapter = new BlogGroupSelectGridAdapter(mContext, mSiteDatas);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SiteData siteData = (SiteData) parent.getItemAtPosition(position);
                    //Log.e(mTag, "id: " + siteData.getId());
                    itemClick(siteData);
                }
            });
        }
    }

    private void itemClick(SiteData siteData) {
        //if (siteData.getId().equals(Config.BLOG_ID_KEYAKIZAKA46_MEMBER)) {
        //    mSnackbar.setText(getString(R.string.working)).show();
        //} else {
            Intent intent = new Intent(mContext, BlogSiteListActivity.class);
            intent.putExtra("siteData", siteData);
            startActivityForResult(intent, 0);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        //}
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Config.RESULT_CODE_FINISH) {
            SiteData siteData = (SiteData) data.getSerializableExtra("siteData");
            itemClick(siteData);
        }
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
