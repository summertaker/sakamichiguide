package com.summertaker.sakamichiguide;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightGridView;
import com.summertaker.sakamichiguide.blog.BlogGroupSelectActivity;
import com.summertaker.sakamichiguide.common.BaseActivity;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.common.Setting;
import com.summertaker.sakamichiguide.data.MenuData;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private int mNavItemSelected = 0;

    private ArrayList<MenuData> mMenuDatas;

    private ImageView mIvHeader;
    private Snackbar mSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mContext = MainActivity.this;

        // 공식 사이트 사진 사용하기 설정
        Setting setting = new Setting(mContext);
        setting.set(Config.SETTING_DISPLAY_OFFICIAL_PHOTO, Config.SETTING_DISPLAY_OFFICIAL_PHOTO_YES); // for ONE Store
        //setting.set(Config.SETTING_DISPLAY_OFFICIAL_PHOTO, Config.SETTING_DISPLAY_OFFICIAL_PHOTO_NO); // for Google Play

        initBaseToolbar(null, getString(R.string.app_name));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mBaseToolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                onDrawerMenuSelected();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                String version = getString(R.string.version_s, Config.VERSION);
                TextView tvNavSubtitle = (TextView) findViewById(R.id.tvNavSubtitle);
                if (tvNavSubtitle != null) {
                    tvNavSubtitle.setText(version);
                }
            }
        };
        if (drawer != null) {
            drawer.addDrawerListener(toggle);

            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            if (navigationView != null) {
                navigationView.setNavigationItemSelectedListener(this);

                mIvHeader = (ImageView) findViewById(R.id.ivHeader);

                mMenuDatas = new ArrayList<>();
                mMenuDatas.add(new MenuData(Config.MAIN_ACTION_MEMBER, getString(R.string.member), R.drawable.main_girl));
                mMenuDatas.add(new MenuData(Config.MAIN_ACTION_BLOG, getString(R.string.blog), R.drawable.main_rss));
                mMenuDatas.add(new MenuData(Config.MAIN_ACTION_BIRTHDAY, getString(R.string.birthday), R.drawable.main_heart));
                mMenuDatas.add(new MenuData(Config.MAIN_ACTION_SLIDE, getString(R.string.slide), R.drawable.main_play));
                mMenuDatas.add(new MenuData(Config.MAIN_ACTION_MEMORY, getString(R.string.memory), R.drawable.main_check));
                mMenuDatas.add(new MenuData(Config.MAIN_ACTION_QUIZ, getString(R.string.quiz), R.drawable.main_target));
                mMenuDatas.add(new MenuData(Config.MAIN_ACTION_RAW_PHOTO, getString(R.string.raw_photo), R.drawable.main_camera));

                MainMenuAdapter adapter = new MainMenuAdapter(mContext, mMenuDatas);
                ExpandableHeightGridView gridView = (ExpandableHeightGridView) findViewById(R.id.gridView);
                if (gridView != null) {
                    gridView.setExpanded(true);
                    gridView.setAdapter(adapter);

                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            MenuData menuData = mMenuDatas.get(position);
                            String menuId = menuData.getId();
                            Intent intent = null;
                            switch (menuId) {
                                case Config.MAIN_ACTION_BLOG:
                                    intent = new Intent(mContext, BlogGroupSelectActivity.class);
                                    break;
                                default:
                                    intent = new Intent(mContext, GroupSelectActivity.class);
                                    intent.putExtra("action", menuData.getId());
                                    intent.putExtra("title", menuData.getTitle());
                                    break;
                            }
                            runActivity(intent, false);
                        }
                    });

                    ScrollView content = (ScrollView) findViewById(R.id.content);
                    if (content != null) {
                        mSnackbar = Snackbar.make(content, "", Snackbar.LENGTH_SHORT);
                        View view = mSnackbar.getView();
                        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                        //tv.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    }
                }
            }
        }

        // 공식 사이트 사진 사용하기 여부 - Google Play에서 컨텐츠 무단 도용으로 거부됨 2016-06-05
        /*Setting setting = new Setting(mContext);
        String photoSetting = setting.get(Config.SETTING_DISPLAY_OFFICIAL_PHOTO);
        if (photoSetting == null || photoSetting.isEmpty()) {
            alertSettings();
        }*/
    }

    private void alertSettings() {
        String message = getString(R.string.are_you_sure_to_use_official_photos) + "\n\n" + getString(R.string.you_can_change_it_later);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(getString(R.string.member_photo));
        builder.setMessage(message);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                saveSettings(Config.SETTING_DISPLAY_OFFICIAL_PHOTO_YES);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                saveSettings(Config.SETTING_DISPLAY_OFFICIAL_PHOTO_NO);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveSettings(String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Config.SETTING_DISPLAY_OFFICIAL_PHOTO, value);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            goSettings();
            //alertSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        showToolbarProgressBar();

        mNavItemSelected = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    public void onDrawerMenuSelected() {
        Intent intent = null;
        switch (mNavItemSelected) {
            //case R.id.navSettings:
            //    intent = new Intent(mContext, SettingActivity.class);
            //    break;
            case R.id.navAbout:
                intent = new Intent(mContext, AboutActivity.class);
                break;
        }
        mNavItemSelected = 0;
        hideToolbarProgressBar();
        runActivity(intent, true);
    }

    @Override
    public void onResume() {
        super.onResume();

        int[] pics = {R.drawable.pic_1, R.drawable.pic_2, R.drawable.pic_3, R.drawable.pic_4};
        Random random = new Random();
        int max = 3;
        int min = 0;
        int selected = random.nextInt(max - min + 1) + min;
        mIvHeader.setImageResource(pics[selected]);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*
    @Override
    public void onYoutubeSelected(String action, SiteData websiteData) {
        //Log.e(mTag, websiteData.getUrl());
        Intent intent = new Intent(mContext, YoutubeListActivity.class);
        intent.putExtra("websiteData", websiteData);
        runActivity(intent, true);
    }

    @Override
    public void onRawPhotoSiteSelected(String action, SiteData websiteData) {
        Intent intent = new Intent(mContext, RawPhotoListActivity.class);
        intent.putExtra("websiteData", websiteData);
        runActivity(intent, true);
    }*/

    private void goSettings() {
        Intent intent = new Intent(mContext, SettingActivity.class);
        runActivity(intent, false);
    }

    public void runActivity(Intent intent, boolean showLoading) {
        if (intent != null) {
            if (showLoading) {
                showToolbarProgressBar();
            }
            startActivityForResult(intent, 0);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        hideToolbarProgressBar();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
