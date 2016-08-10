package com.summertaker.sakamichiguide.blog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseActivity;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.data.SiteData;

import java.util.ArrayList;

public class BlogSettingActivity extends BaseActivity {

    SiteData mSiteData;
    ArrayList<SiteData> mBlogList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_setting_activity);

        mContext = BlogSettingActivity.this;

        Intent intent = getIntent();
        mSiteData = (SiteData) intent.getSerializableExtra("siteData");
        mBlogList = (ArrayList<SiteData>) intent.getSerializableExtra("blogList");

        String title = getString(R.string.settings) + " / "+ getString(R.string.blog);
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, title);

        Button btnSelectMember = (Button) findViewById(R.id.btSelectMember);
        btnSelectMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSelectMember();
            }
        });
    }

    private void goToSelectMember() {
        Intent intent = new Intent(mContext, BlogSettingCheckActivity.class);
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
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
