package com.summertaker.sakamichiguide;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.summertaker.sakamichiguide.common.BaseActivity;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.common.Setting;

public class SettingActivity extends BaseActivity {

    private Snackbar mSnackbar;

    Button mBtYes;
    Button mBtYesChecked;
    Button mBtNo;
    Button mBtNoChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);

        initBaseToolbar(Config.TOOLBAR_ICON_BACK, getString(R.string.settings));

        ScrollView content = (ScrollView) findViewById(R.id.content);
        if (content != null) {
            mSnackbar = Snackbar.make(content, "", Snackbar.LENGTH_SHORT);
            View view = mSnackbar.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
            //tv.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            mBtYes = (Button) findViewById(R.id.btYes);
            mBtYesChecked = (Button) findViewById(R.id.btYesChecked);
            if (mBtYes != null) {
                mBtYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBtYes.setVisibility(View.GONE);
                        mBtYesChecked.setVisibility(View.VISIBLE);
                        mBtNo.setVisibility(View.VISIBLE);
                        mBtNoChecked.setVisibility(View.GONE);

                        save(Config.SETTING_DISPLAY_OFFICIAL_PHOTO_YES);
                    }
                });
            }

            mBtNo = (Button) findViewById(R.id.btNo);
            mBtNoChecked = (Button) findViewById(R.id.btNoChecked);
            if (mBtNo != null) {
                mBtNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBtYes.setVisibility(View.VISIBLE);
                        mBtYesChecked.setVisibility(View.GONE);
                        mBtNo.setVisibility(View.GONE);
                        mBtNoChecked.setVisibility(View.VISIBLE);

                        save(Config.SETTING_DISPLAY_OFFICIAL_PHOTO_NO);
                    }
                });
            }

            String value = mSharedPreferences.getString(Config.SETTING_DISPLAY_OFFICIAL_PHOTO, "");
            //Log.e(mTag, "value: " + value);

            //value = "";

            if (value.isEmpty()) {
                mBtYes.setVisibility(View.VISIBLE);
                mBtNo.setVisibility(View.VISIBLE);
            } else if (value.equals(Config.SETTING_DISPLAY_OFFICIAL_PHOTO_YES)) {
                mBtYesChecked.setVisibility(View.VISIBLE);
                mBtNo.setVisibility(View.VISIBLE);
            } else if (value.equals(Config.SETTING_DISPLAY_OFFICIAL_PHOTO_NO)) {
                mBtYes.setVisibility(View.VISIBLE);
                mBtNoChecked.setVisibility(View.VISIBLE);
            }
        }
    }

    private void save(String value) {
        //Log.e(mTag, "value: " + value);

        Setting setting = new Setting(mContext);
        setting.set(Config.SETTING_DISPLAY_OFFICIAL_PHOTO, value);

        /*SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Config.SETTING_DISPLAY_OFFICIAL_PHOTO, value);
        editor.apply();*/

        mSnackbar.setText(getString(R.string.saved)).show();
        //finish();
    }
}
