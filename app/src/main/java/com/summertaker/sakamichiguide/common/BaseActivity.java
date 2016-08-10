package com.summertaker.sakamichiguide.common;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;

import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.util.Util;

public abstract class BaseActivity extends AppCompatActivity {

    protected String mTag = "== " + getClass().getSimpleName();

    protected Context mContext;
    protected Resources mResources;
    protected SharedPreferences mSharedPreferences;
    //protected SharedPreferences.Editor mSharedEditor;

    protected Toolbar mBaseToolbar;
    protected ProgressBar mBaseProgressBar;

    protected String mErrorMessage;

    protected boolean mIsEnglishLocale = false;

    protected void initBaseToolbar(String icon, String title) {
        mContext = BaseActivity.this;
        mResources = mContext.getResources();
        mSharedPreferences = getSharedPreferences(Config.USER_PREFERENCE_KEY, 0);
        //mSharedEditor = mSharedPreferences.edit();

        mBaseToolbar = (Toolbar) findViewById(R.id.toolbar);
        //mBaseToolbar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.nogizaka48background));
        mBaseProgressBar = (ProgressBar) findViewById(R.id.toolbar_progressbar);
        if (mBaseProgressBar != null) {
            //mBaseProgressBar.getIndeterminateDrawable().setColorFilter(0xfff8bbd0, PorterDuff.Mode.SRC_ATOP);
            Util.setProgressBarColor(mBaseProgressBar, 0x779c27b0, PorterDuff.Mode.SRC_ATOP);
        }

        if (icon != null) {
            if (icon.equalsIgnoreCase(Config.TOOLBAR_ICON_LOGO)) {
                mBaseToolbar.setNavigationIcon(R.drawable.ic_female_pink_48);
            }
        }

        if (title != null) {
            mBaseToolbar.setTitle(title);
        }
        mBaseToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToolbarClick();
            }
        });

        setSupportActionBar(mBaseToolbar); // 여기서 먼저 설정하고 아래에 이벤트 설정할 것

        if (icon != null && icon.equalsIgnoreCase(Config.TOOLBAR_ICON_BACK)) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                //actionBar.setDisplayShowHomeEnabled(true);
            }
        }

        if (icon != null && icon.equalsIgnoreCase(Config.TOOLBAR_ICON_BACK)) {
            mBaseToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doFinish();
                }
            });
        }

        /*
        mBaseToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return false;
            }
        });
        */

        mIsEnglishLocale = Util.isEnglishLocale(BaseActivity.this);
    }

    protected void onToolbarClick() {
        //Util.alert(mContext, "Toolbar");
    }

    protected void showToolbarProgressBar() {
        mBaseProgressBar.setVisibility(View.VISIBLE);
    }

    protected void hideToolbarProgressBar() {
        mBaseProgressBar.setVisibility(View.GONE);
    }

    protected void alertNetworkErrorAndFinish(String message) {
        if (message == null) {
            message = "";
        } else {
            message += "\n\n";
        }
        message += getString(R.string.website_access_problem_occurred);
        //message += "\n" + getString(R.string.failed_to_crawl_the_html_document);
        message += "\n" + getString(R.string.try_again_later);

        alertAndFinish(message);
    }

    protected void alertParseErrorAndFinish(String message) {
        if (message == null) {
            message = getString(R.string.parsing_error_occurred);
        }
        alertAndFinish(message);
    }

    protected void alertAndFinish(String message) {
        if (message == null) {
            message = getString(R.string.error_occurred);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(message); //.setTitle(R.string.app_name);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                doFinish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    finish();
                }
                return true;
            }
        });
        dialog.show();
    }

    protected void doFinish() {
        //Intent intent = new Intent();
        //intent.putExtra("pictureId", mData.getGroupId());
        //setResult(ACTIVITY_RESULT_CODE, intent);

        finish();
    }
}
