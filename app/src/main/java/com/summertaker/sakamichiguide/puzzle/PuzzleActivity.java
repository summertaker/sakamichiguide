package com.summertaker.sakamichiguide.puzzle;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.squareup.picasso.Picasso;
import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseActivity;
import com.summertaker.sakamichiguide.common.BaseApplication;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.data.GroupData;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.data.TeamData;
import com.summertaker.sakamichiguide.parser.BaseParser;
import com.summertaker.sakamichiguide.parser.NamuwikiParser;
import com.summertaker.sakamichiguide.parser.WikipediaEnParser;
import com.summertaker.sakamichiguide.util.ImageUtil;
import com.summertaker.sakamichiguide.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PuzzleActivity extends BaseActivity {

    Snackbar mSnackbar;
    ProgressBar mPbLoading;

    String mTitle;

    String mAction;
    String mLevel;
    GroupData mGroupData;
    boolean mIsMobile = false;
    ArrayList<MemberData> mGroupMemberList = new ArrayList<>();
    ArrayList<MemberData> mMemberList = new ArrayList<>();
    ArrayList<MemberData> mWikiMemberList = new ArrayList<>();
    ArrayList<TeamData> mTeamDataList = new ArrayList<>();

    String mLocale;
    BaseParser mWikiParser;

    boolean mIsDataLoaded = false;
    boolean mIsWikiLoaded = false;

    RelativeLayout mResultLayout;
    CountDownTimer mResultTimer;
    TextView mTvResultTimer;
    Button mBtnResultOk;

    int mCardCount = 0;
    int mCardTotal = 30;
    int mTimeCount = 0;
    int mCardBackground = 0;

    int[] mCardViewIndexes = {R.id.cv1, R.id.cv2, R.id.cv3, R.id.cv4, R.id.cv5, R.id.cv6, R.id.cv7, R.id.cv8, R.id.cv9, R.id.cv10,
            R.id.cv11, R.id.cv12, R.id.cv13, R.id.cv14, R.id.cv15, R.id.cv16, R.id.cv17, R.id.cv18, R.id.cv19, R.id.cv20,
            R.id.cv21, R.id.cv22, R.id.cv23, R.id.cv24, R.id.cv25, R.id.cv26, R.id.cv27, R.id.cv28, R.id.cv29, R.id.cv30};

    TextView[] mTextViews = new TextView[mCardTotal];
    ImageView[] mImageViews = new ImageView[mCardTotal];
    TransitionDrawable[] mTransitions = new TransitionDrawable[mCardTotal];

    Handler mHandler;
    Runnable mRunnable;
    boolean mIsProcessing = true;

    int mFirstIndex = -1;
    int mSecondIndex = -1;
    String mFirstText;
    String mSecondText;

    ArrayList<Integer> mFounds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = PuzzleActivity.this;
        mResources = mContext.getResources();

        //Setting setting = new Setting(mContext);
        //mShowOfficialPhoto = setting.get(Config.SETTING_DISPLAY_OFFICIAL_PHOTO).equals(Config.SETTING_DISPLAY_OFFICIAL_PHOTO_YES);
        //Log.e(mTag, "mShowOfficialPhoto: " + mShowOfficialPhoto);

        Intent intent = getIntent();
        mAction = intent.getStringExtra("action");
        mLevel = intent.getStringExtra("level");
        mGroupData = (GroupData) intent.getSerializableExtra("groupData");

        switch (mLevel) {
            case Config.PUZZLE_LEVEL_EASY:
                setContentView(R.layout.puzzle_easy_activity);
                mCardTotal = 12;
                break;
            case Config.PUZZLE_LEVEL_HARD:
                setContentView(R.layout.puzzle_hard_activity);
                mCardTotal = 30;
                break;
            default:
                setContentView(R.layout.puzzle_normal_activity);
                mCardTotal = 20;
                break;
        }

        mTitle = getString(R.string.puzzle) + " / " + mGroupData.getName();
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, mTitle);

        mPbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        Util.setProgressBarColor(mPbLoading, Config.PROGRESS_BAR_COLOR_NORMAL, null);

        RelativeLayout content = (RelativeLayout) findViewById(R.id.content);
        if (content != null) {
            mSnackbar = Snackbar.make(content, "", Snackbar.LENGTH_SHORT);
            View view = mSnackbar.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
            //tv.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }

        mResultLayout = (RelativeLayout) findViewById(R.id.result);
        mTvResultTimer = (TextView) findViewById(R.id.tvResultTimer);
        mBtnResultOk = (Button) findViewById(R.id.btnResultOk);
        mBtnResultOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doFinish();
            }
        });

        RelativeLayout.LayoutParams mm = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        RelativeLayout.LayoutParams ww = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams mw = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mw.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        int padding = mLevel.equals(Config.PUZZLE_LEVEL_NORMAL) ? 0 : 3;
        float density = mResources.getDisplayMetrics().density;
        int dp = (int) (padding * density);

        for (int i = 0; i < mCardTotal; i++) {

            CardView cv = (CardView) findViewById(mCardViewIndexes[i]);

            /*RelativeLayout pl = new RelativeLayout(mContext);
            pl.setLayoutParams(match);
            pl.setBackgroundColor(ContextCompat.getColor(mContext, R.color.puzzle_card_loading_background));
            pl.setGravity(Gravity.CENTER);
            mLoadings[i] = pl;

            ProgressBar pb = new ProgressBar(mContext);
            pb.setLayoutParams(wrap);
            pb.setBackground(ContextCompat.getDrawable(mContext, R.drawable.progress_bar_circle));
            Util.setProgressBarColor(pb, Config.PROGRESS_BAR_COLOR_LIGHT, null);

            pl.addView(pb);
            cv.addView(pl);*/

            RelativeLayout rl = new RelativeLayout(mContext);
            rl.setLayoutParams(mm);
            //pl.setBackgroundColor(ContextCompat.getColor(mContext, R.color.puzzle_card_loading_background));
            //pl.setGravity(Gravity.CENTER);

            ImageView iv = new ImageView(mContext);
            iv.setLayoutParams(mm);
            //iv.setVisibility(View.GONE);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mImageViews[i] = iv;
            rl.addView(iv);

            if (!mLevel.equals(Config.PUZZLE_LEVEL_HARD)) {
                TextView tv = new TextView(mContext);
                tv.setLayoutParams(mw);
                tv.setGravity(Gravity.CENTER);
                tv.setTextColor(Color.WHITE);
                tv.setPadding(0, dp, 0, dp);
                tv.setText(getString(R.string.app_name));
                tv.setBackgroundColor(Color.parseColor("#88000000"));
                tv.setVisibility(View.GONE);
                mTextViews[i] = tv;
                rl.addView(tv);
            }

            cv.addView(rl);
        }

        String url = mGroupData.getUrl();
        String userAgent = Config.USER_AGENT_WEB;
        mCardBackground = R.drawable.card_keyakizaka46;

        switch (mGroupData.getId()) {
            case Config.GROUP_ID_NOGIZAKA46:
                url = mGroupData.getMobileUrl();
                userAgent = Config.USER_AGENT_MOBILE;
                mIsMobile = true;
                mCardBackground = R.drawable.card_nokizaka46;
                break;
        }

        requestData(url, userAgent);

        mLocale = Util.getLocaleStrng(mContext);
        switch (mLocale) {
            case "KR":
                mWikiParser = new NamuwikiParser();
                break;
            default:
                mWikiParser = new WikipediaEnParser();
                break;
        }
        String mWikiUrl = mWikiParser.getUrl(mGroupData.getId());
        if (mWikiUrl == null || mWikiUrl.isEmpty()) {
            mIsWikiLoaded = true;
        } else {
            requestData(mWikiUrl, Config.USER_AGENT_WEB);
        }
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
                Log.e(mTag, "NETWORK ERROR: " + url);
                mErrorMessage = Util.getErrorMessage(error);
                parseData(url, "");
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
        if (url.contains("wiki")) {
            mWikiParser.parse46List(response, mGroupData, mWikiMemberList);
            mIsWikiLoaded = true;
        } else {
            BaseParser baseParser = new BaseParser();
            baseParser.parseMemberList(response, mGroupData, mGroupMemberList, mTeamDataList, mIsMobile);
            mIsDataLoaded = true;
        }
        renderData();
    }

    private void updateData() {
        for (MemberData memberData : mMemberList) {
            if (mWikiMemberList.size() == 0) {
                memberData.setLocaleName(memberData.getName()); //memberData.getNameEn();
            } else {
                for (MemberData wikiData : mWikiMemberList) {
                    //Log.e(mTag, memberData.getNoSpaceName() + " = " + wikiData.getNoSpaceName());
                    if (Util.isEqualString(memberData.getNoSpaceName(), wikiData.getNoSpaceName())) {
                        String localeName;
                        switch (mLocale) {
                            case "KR":
                                localeName = wikiData.getNameKo();
                                break;
                            default:
                                localeName = wikiData.getNameEn();
                                break;
                        }
                        if (localeName == null || localeName.isEmpty()) {
                            localeName = memberData.getName();
                        }
                        //Log.e(mTag, "localeName: " + localeName);
                        memberData.setLocaleName(localeName);
                        break;
                    } else {
                        memberData.setLocaleName(memberData.getName()); //memberData.getNameEn();
                    }
                }
            }
        }
    }

    private void renderData() {
        if (!mIsWikiLoaded || !mIsDataLoaded) {
            return;
        }

        if (mGroupMemberList.size() == 0) {
            alertNetworkErrorAndFinish(mErrorMessage);
        } else {
            Collections.shuffle(mGroupMemberList);

            int count = 0;
            int total = mCardTotal / 2;
            for (int i = 0; i < total; i++) {
                MemberData oldData = mGroupMemberList.get(i);

                MemberData data1 = new MemberData();
                data1.setName(oldData.getName());
                data1.setNoSpaceName(oldData.getNoSpaceName());
                data1.setThumbnailUrl(oldData.getThumbnailUrl());
                mMemberList.add(data1);
                count++;

                MemberData data2 = new MemberData();
                data2.setName(oldData.getName());
                data2.setNoSpaceName(oldData.getNoSpaceName());
                data2.setThumbnailUrl(oldData.getThumbnailUrl());
                mMemberList.add(data2);
                count++;
            }

            Collections.shuffle(mMemberList);
            Collections.shuffle(mMemberList);
            Collections.shuffle(mMemberList);

            updateData();

            for (int i = 0; i < mCardTotal; i++) {

                final int index = i;
                MemberData memberData = mMemberList.get(i);
                memberData.setId(index + "");
                //Log.e(mTag, index + ". " + memberData.getName());

                mImageViews[index].setTag(memberData);

                if (!mLevel.equals(Config.PUZZLE_LEVEL_HARD)) {
                    mTextViews[index].setText(memberData.getLocaleName());
                }

                String imageUrl = memberData.getThumbnailUrl();

                Picasso.with(mContext).load(imageUrl).into(mImageViews[index], new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) mImageViews[index].getDrawable()).getBitmap();
                        mTransitions[index] = new TransitionDrawable(new Drawable[]{
                                ContextCompat.getDrawable(mContext, mCardBackground),
                                new BitmapDrawable(getResources(), bitmap)
                        });
                        mImageViews[index].setImageDrawable(mTransitions[index]);

                        mCardCount++;
                        onImageLoaded();
                    }

                    @Override
                    public void onError() {
                        alertNetworkErrorAndFinish(null);
                    }
                });

                /*final String cacheId = Util.urlToId(imageUrl);
                final String cacheUri = ImageUtil.getValidCacheUri(cacheId);
                if (cacheUri != null) {
                    imageUrl = cacheUri;
                }

                Glide.with(mContext).load(imageUrl).asBitmap().dontAnimate() //.diskCacheStrategy(DiskCacheStrategy.RESULT)
                        //.override(Config.IMAGE_GRID3_WIDTH, Config.IMAGE_GRID3_HEIGHT)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {

                                //mLoadings[index].setVisibility(View.GONE);

                                mTransitions[index] = new TransitionDrawable(new Drawable[]{
                                        ContextCompat.getDrawable(mContext, mCardBackground),
                                        new BitmapDrawable(getResources(), bitmap)
                                });
                                mImageViews[index].setImageDrawable(mTransitions[index]);
                                //mImageViews[index].setVisibility(View.VISIBLE);

                                mCardCount++;
                                if (mCardCount == mCardTotal) {
                                    onImageLoaded();
                                }

                                if (cacheUri == null) {
                                    ImageUtil.saveBitmapToPng(bitmap, cacheId); // 캐쉬 저장
                                }
                            }
                        });*/

                /*Picasso.with(mContext).load(imageUrl).into(mImageViews[index], new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        pl.setVisibility(View.GONE);
                        pb.setVisibility(View.GONE);
                        mImageViews[index].setVisibility(View.VISIBLE);
                        mCounter++;
                        onImageLoaded();
                    }

                    @Override
                    public void onError() {
                        pb.setVisibility(View.GONE);
                    }
                });*/
            }
        }
    }

    private void onImageLoaded() {
        mHandler = new Handler();
        mRunnable = new Runnable() {
            public void run() {
                removeCallback();
                renderPuzzle();
            }
        };
        mHandler.postDelayed(mRunnable, 500);
    }

    private void renderPuzzle() {
        for (int i = 0; i < mCardTotal; i++) {
            setEvent(mImageViews[i]);
        }

        LinearLayout grid = (LinearLayout) findViewById(R.id.grid);
        grid.setVisibility(View.VISIBLE);
        mPbLoading.setVisibility(View.GONE);

        mResultTimer = new CountDownTimer(1000000000, 1000) {

            public void onTick(long millisUntilFinished) {
                //Calendar c = Calendar.getInstance();
                //String time = c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);

                if (mTvResultTimer != null) {
                    int min = mTimeCount / 60;
                    int sec = mTimeCount % 60;
                    String text = String.format(getString(R.string.s_second), sec);
                    if (min > 0) {
                        text = String.format(getString(R.string.s_minute), min) + " " + text;
                    }
                    mTvResultTimer.setText(text);
                }
                mTimeCount++;
            }

            public void onFinish() {

            }
        };
        mResultTimer.start();

        mIsProcessing = false;
    }

    private void setEvent(ImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (!mIsProcessing) {

                    MemberData memberData = (MemberData) view.getTag();
                    int index = Integer.parseInt(memberData.getId());
                    //Log.e(mTag, index + " / " + mFirstIndex + "." + mFirstText + " / " + mSecondIndex + "." + mSecondText);

                    boolean exist = false;
                    for (int i : mFounds) {
                        if (i == index) {
                            exist = true;
                            break;
                        }
                    }

                    if (!exist) {
                        if (mFirstIndex == -1) {
                            flip(index);
                            mFirstIndex = index;
                            mFirstText = memberData.getName();
                        } else if (mSecondIndex == -1) {
                            if (mFirstIndex == index) {
                                flip(index);
                                mTextViews[index].setVisibility(View.GONE);
                                mFirstIndex = -1;
                            } else {
                                mSecondIndex = index;
                                mSecondText = memberData.getName();
                                flip(index);

                                mIsProcessing = true;
                                mHandler = new Handler();
                                mRunnable = new Runnable() {
                                    public void run() {
                                        removeCallback();
                                        mIsProcessing = false;

                                        if (mFirstText.equals(mSecondText)) {
                                            //mSnackbar.setText("정답").show();
                                            setCorrect();
                                        } else {
                                            //mSnackbar.setText("오답").show();
                                            setWrong();
                                        }
                                        mFirstIndex = -1;
                                        mSecondIndex = -1;

                                    }
                                };
                                mHandler.postDelayed(mRunnable, 500);
                            }
                        }
                    }
                }
            }
        });
    }

    private void flip(int i) {
        AnimatorSet ani = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.flip);
        ani.setTarget(mImageViews[i]);
        ani.setDuration(300);
        ani.start();
        mTransitions[i].reverseTransition(300);

        if (!mLevel.equals(Config.PUZZLE_LEVEL_HARD)) {
            mTextViews[i].setVisibility(View.VISIBLE);
        }
    }

    private void setCorrect() {
        mFounds.add(mFirstIndex);
        mFounds.add(mSecondIndex);

        float dest = 360;
        //if (aniView.getRotation() == 360) {
        //    System.out.println(aniView.getAlpha());
        //    dest = 0;
        //}
        ObjectAnimator animation1 = ObjectAnimator.ofFloat(mImageViews[mFirstIndex], "rotation", dest);
        animation1.setDuration(400);
        animation1.start();

        ObjectAnimator animation2 = ObjectAnimator.ofFloat(mImageViews[mSecondIndex], "rotation", dest);
        animation2.setDuration(400);
        animation2.start();

        //Log.e(mTag, "mFounds.size(): " + mFounds.size());
        if (mFounds.size() == mCardTotal) {
            mResultTimer.cancel();
            mResultLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setWrong() {
        AnimatorSet ani1 = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.flip);
        ani1.setTarget(mImageViews[mFirstIndex]);
        ani1.setDuration(400);
        ani1.start();
        mTransitions[mFirstIndex].reverseTransition(0);

        AnimatorSet ani2 = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.flip);
        ani2.setTarget(mImageViews[mSecondIndex]);
        ani2.setDuration(400);
        ani2.start();
        mTransitions[mSecondIndex].reverseTransition(0);

        if (!mLevel.equals(Config.PUZZLE_LEVEL_HARD)) {
            mTextViews[mFirstIndex].setVisibility(View.GONE);
            mTextViews[mSecondIndex].setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        //Log.e(mTag, "onDestroy()...");
        super.onDestroy();

        if (mResultTimer != null) {
            mResultTimer.cancel();
        }
        removeCallback();
    }

    protected void doFinish() {
        removeCallback();
        finish();
    }

    private void removeCallback() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }
}
