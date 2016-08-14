package com.summertaker.sakamichiguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightGridView;
import com.summertaker.sakamichiguide.birthday.BirthMonthActivity;
import com.summertaker.sakamichiguide.blog.BlogSiteListActivity;
import com.summertaker.sakamichiguide.common.BaseActivity;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.common.Setting;
import com.summertaker.sakamichiguide.data.DataManager;
import com.summertaker.sakamichiguide.data.GroupData;
import com.summertaker.sakamichiguide.member.MemberListActivity;
import com.summertaker.sakamichiguide.member.TeamListActivity;
import com.summertaker.sakamichiguide.puzzle.PuzzleLevelActivity;
import com.summertaker.sakamichiguide.quiz.MemoryActivity;
import com.summertaker.sakamichiguide.quiz.QuizActivity;
import com.summertaker.sakamichiguide.quiz.SlideActivity;
import com.summertaker.sakamichiguide.quiz.SlideTextActivity;
import com.summertaker.sakamichiguide.rawphoto.RawPhotoSelectActivity;

import java.util.ArrayList;

public class GroupSelectActivity extends BaseActivity {

    private String mAction;
    ArrayList<GroupData> mGroupDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_select_activity);

        mContext = GroupSelectActivity.this;

        Intent intent = getIntent();
        mAction = intent.getStringExtra("action");
        String title = intent.getStringExtra("title");

        switch (mAction) {
            case Config.MAIN_ACTION_SLIDE:
                title = getString(R.string.slide);
                break;
            case Config.MAIN_ACTION_MEMORY:
                title = getString(R.string.memory);
                break;
            case Config.MAIN_ACTION_QUIZ:
                title = getString(R.string.quiz);
                break;
        }
        if (title == null) {
            title = getString(R.string.app_name);
        }
        //title = title + " / " + getString(R.string.select_a_group);

        initBaseToolbar(Config.TOOLBAR_ICON_BACK, title);

        //TextView tvHeaderTitle = (TextView) findViewById(R.id.tvHeaderTitle);
        //if (tvHeaderTitle != null) {
        //    tvHeaderTitle.setText(title);
        //}

        DataManager dataManager = new DataManager(mContext);
        mGroupDataList = dataManager.getGroupList(mAction);

        //initGridText();
        initGridView();
    }

    private void initGridText() {
        LinearLayout loListView = (LinearLayout) findViewById(R.id.loListView);
        if (loListView != null) {
            //loListView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.card_background));
            loListView.setVisibility(View.VISIBLE);
            GridView gridText = (GridView) findViewById(R.id.gridText);
            if (gridText != null) {
                gridText.setAdapter(new GroupSelectTextAdapter(mContext, mGroupDataList));
                gridText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        GroupData groupData = (GroupData) parent.getItemAtPosition(position);
                        runActivity(groupData);
                    }
                });
            }
        }
    }

    private void initGridView() {
        ScrollView loGridView = (ScrollView) findViewById(R.id.loGridView);
        if (loGridView != null) {
            loGridView.setVisibility(View.VISIBLE);
            //loGridView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            ExpandableHeightGridView gridView = (ExpandableHeightGridView) findViewById(R.id.gridView);
            if (gridView != null) {
                gridView.setExpanded(true);
                gridView.setAdapter(new GroupSelectGridAdapter(mContext, mGroupDataList));
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        GroupData groupData = (GroupData) parent.getItemAtPosition(position);
                        runActivity(groupData);
                    }
                });
            }
        }
    }

    public void runActivity(GroupData groupData) {

        Intent intent = null;

        Setting setting = new Setting(mContext);
        String displayProfilePhoto = setting.get(Config.SETTING_DISPLAY_OFFICIAL_PHOTO);

        switch (mAction) {
            case Config.MAIN_ACTION_MEMBER:
                switch (groupData.getId()) {
                    case Config.GROUP_ID_NOGIZAKA46:
                        intent = new Intent(this, MemberListActivity.class);
                        break;
                    default:
                        intent = new Intent(this, TeamListActivity.class);
                        break;
                }
                break;
            case Config.MAIN_ACTION_BLOG:
                intent = new Intent(this, BlogSiteListActivity.class);
                break;
            case Config.MAIN_ACTION_SLIDE:
                if (displayProfilePhoto.equals(Config.SETTING_DISPLAY_OFFICIAL_PHOTO_YES)) {
                    intent = new Intent(this, SlideActivity.class);
                } else {
                    intent = new Intent(this, SlideTextActivity.class);
                }
                break;
            case Config.MAIN_ACTION_MEMORY:
                intent = new Intent(this, MemoryActivity.class);
                break;
            case Config.MAIN_ACTION_QUIZ:
                intent = new Intent(this, QuizActivity.class);
                break;
            case Config.MAIN_ACTION_BIRTHDAY:
                intent = new Intent(this, BirthMonthActivity.class);
                break;
            case Config.MAIN_ACTION_RAW_PHOTO:
                intent = new Intent(this, RawPhotoSelectActivity.class);
                break;
            case Config.MAIN_ACTION_PUZZLE:
                intent = new Intent(this, PuzzleLevelActivity.class);
                break;
        }
        //intent = new Intent(this, QuizResultActivity.class);

        if (intent != null) {
            intent.putExtra("action", mAction);
            intent.putExtra("groupData", groupData);
            //showToolbarProgressBar();
            startActivityForResult(intent, 0);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.e(mTag, "onActivityResult().resultCode: " + resultCode);

        if (resultCode == Config.RESULT_CODE_FINISH) {
            finish();
        }

        //hideToolbarProgressBar();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
