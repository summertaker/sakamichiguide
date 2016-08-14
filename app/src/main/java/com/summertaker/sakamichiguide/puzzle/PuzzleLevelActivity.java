package com.summertaker.sakamichiguide.puzzle;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseActivity;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.data.GroupData;

public class PuzzleLevelActivity extends BaseActivity {

    String mTitle;
    String mAction;
    GroupData mGroupData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puzzle_level_activity);

        mContext = PuzzleLevelActivity.this;
        mResources = mContext.getResources();

        Intent intent = getIntent();
        mAction = intent.getStringExtra("action");
        mGroupData = (GroupData) intent.getSerializableExtra("groupData");

        mTitle = getString(R.string.puzzle) + " / " + mGroupData.getName();
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, mTitle);

        Button btnEasy = (Button) findViewById(R.id.btnEasy);
        btnEasy.setOnClickListener(btOnClick);

        Button btnNormal = (Button) findViewById(R.id.btnNormal);
        btnNormal.setOnClickListener(btOnClick);

        Button btnHard = (Button) findViewById(R.id.btnHard);
        btnHard.setOnClickListener(btOnClick);
    }

    private View.OnClickListener btOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String level;
            switch (view.getId()) {
                case R.id.btnEasy:
                    level = Config.PUZZLE_LEVEL_EASY;
                    break;
                case R.id.btnHard:
                    level = Config.PUZZLE_LEVEL_HARD;
                    break;
                default:
                    level = Config.PUZZLE_LEVEL_NORMAL;
                    break;
            }
            goActivity(level);
        }
    };

    public void goActivity(String level) {
        Intent intent = new Intent(this, PuzzleActivity.class);
        intent.putExtra("action", mAction);
        intent.putExtra("level", level);
        intent.putExtra("groupData", mGroupData);

        startActivityForResult(intent, 0);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.e(mTag, "onActivityResult().resultCode: " + resultCode);

        //if (resultCode == Config.RESULT_CODE_FINISH) {
        //    finish();
        //}
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
