package com.summertaker.sakamichiguide.quiz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseActivity;
import com.summertaker.sakamichiguide.common.BaseApplication;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.common.Setting;
import com.summertaker.sakamichiguide.data.GroupData;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.data.TeamData;
import com.summertaker.sakamichiguide.parser.BaseParser;
import com.summertaker.sakamichiguide.parser.NamuwikiParser;
import com.summertaker.sakamichiguide.parser.WikipediaEnParser;
import com.summertaker.sakamichiguide.util.ProportionalImageView;
import com.summertaker.sakamichiguide.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SlideActivity extends BaseActivity {

    boolean mShowOfficialPhoto;

    GroupData mGroupData;

    ArrayList<TeamData> mTeamList = new ArrayList<>();
    ArrayList<MemberData> mMemberList = new ArrayList<>();
    ArrayList<MemberData> mWikiMemberList = new ArrayList<>();

    String mTitle;

    LinearLayout mLoLoading;

    String mLocale;
    BaseParser mWikiParser;
    //CacheManager mCacheManager;

    boolean isDataLoaded = false;
    boolean isWikiLoaded = false;

    private MemberData mMemberData;

    private ViewFlipper mViewFlipper;
    private float mDensity;
    private LinearLayout.LayoutParams mParams;

    private LinearLayout mLoProfile;
    private TextView mTvName;
    private TextView mTvLocaleName;
    private TextView mTvInfo;
    //private ImageView mIvIndicator1;
    //private ImageView mIvIndicator2;

    private boolean mIsFirstData = true;
    private boolean mIsAllDataLoaded = false;
    private int mMemberPosition = 0;

    private Handler mHandler;
    private Runnable mRunnable;
    //private int mIndicatoerCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_activity);

        mContext = SlideActivity.this;

        Setting setting = new Setting(mContext);
        mShowOfficialPhoto = setting.get(Config.SETTING_DISPLAY_OFFICIAL_PHOTO).equals(Config.SETTING_DISPLAY_OFFICIAL_PHOTO_YES);
        //Log.e(mTag, "mShowOfficialPhoto: " + mShowOfficialPhoto);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        mGroupData = (GroupData) intent.getSerializableExtra("groupData");

        mTitle = mGroupData.getName();
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, mTitle);

        mLoLoading = (LinearLayout) findViewById(R.id.loLoading);
        ProgressBar pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        Util.setProgressBarColor(pbLoading, Config.PROGRESS_BAR_COLOR_NORMAL, null);

        //mHandler = new Handler();

        mLocale = Util.getLocaleStrng(mContext);
        switch (mLocale) {
            case "KR":
                mWikiParser = new NamuwikiParser();
                break;
            default:
                mWikiParser = new WikipediaEnParser();
                break;
        }

        //mCacheManager = new CacheManager(mSharedPreferences);

        String url = mGroupData.getUrl();
        String userAgent = Config.USER_AGENT_WEB;

        switch (mGroupData.getId()) {
            //case Config.GROUP_ID_NGT48:
            case Config.GROUP_ID_NOGIZAKA46:
                userAgent = Config.USER_AGENT_MOBILE;
                url = mGroupData.getMobileUrl(); // desktop html은 thumbnail 이미지를 css의 sprite 사용함.
                break;
        }

        requestData(url, userAgent);
        requestWiki();

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    private void requestData(final String url, final String userAgent) {
        //Log.e(mTag, url);
        //Log.e(mTag, userAgent);

        //final String cacheId = Util.urlToId(url);
        //String cacheData = mCacheManager.load(cacheId);

        //if (cacheData == null) {
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e(mTag, "response: " + response);
                //mCacheManager.save(cacheId, response);
                parseData(url, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(mTag, "onErrorResponse(): " + url);
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

        String tag_string_req = "string_req";
        BaseApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
        //} else {
        //    parseData(url, cacheData);
        //}
    }

    private void requestWiki() {
        String url = mWikiParser.getUrl(mGroupData.getId());
        if (url == null) {
            alertAndFinish("URL is empty.");
        } else {
            requestData(url, Config.USER_AGENT_WEB);
        }
    }

    private void parseData(String url, String response) {
        if (url.contains("wiki")) {
            //Log.e(mTag, response);
            if (!response.isEmpty()) {
                mWikiMemberList = new ArrayList<>();
                switch (mGroupData.getId()) {
                    case Config.GROUP_ID_NOGIZAKA46:
                    case Config.GROUP_ID_KEYAKIZAKA46:
                        mWikiParser.parse46List(response, mGroupData, mWikiMemberList);
                        break;
                    default:
                        mWikiParser.parse48List(response, mGroupData, mWikiMemberList);
                        break;
                }
            }
            isWikiLoaded = true;
        } else {
            if (!response.isEmpty()) {
                boolean isMobile = url.equals(mGroupData.getMobileUrl());
                switch (mGroupData.getId()) {
                    default:
                        BaseParser baseParser = new BaseParser();
                        baseParser.parseMemberList(response, mGroupData, mMemberList, mTeamList, isMobile);
                        break;
                }
            }
            isDataLoaded = true;
        }

        updateData();
    }

    private void updateData() {
        //Log.e(mTag, "renderData()...");
        //Log.e(mTag, "isDataLoaded: " + isDataLoaded);
        //Log.e(mTag, "isWikiLoaded: " + isWikiLoaded);

        if (!isDataLoaded || !isWikiLoaded) {
            return;
        }

        mLoLoading.setVisibility(View.GONE);

        if (mMemberList.size() == 0) {
            alertNetworkErrorAndFinish(null);
        } else {
            //Log.e(mTag, "mMemberList.size(): " + mMemberList.size());
            //final int memberSize = mMemberList.size();

            final int wikiSize = mWikiMemberList.size();

            /*if (wikiSize > 0) {
                final ProgressBar pb = (ProgressBar) findViewById(R.id.progress);
                if (pb != null) {
                    pb.setMax(memberSize);
                    mThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (mProgressValue < memberSize) {
                                pb.setProgress(mProgressValue);
                            }
                        }
                    });
                    mThread.start();
                }
            }*/

            String locale = Util.getLocaleStrng(mContext);

            for (MemberData memberData : mMemberList) {
                //Log.e(mTag, "memberData.getNameEn(): " + memberData.getNameEn());

                String localeName = null;
                for (MemberData wikiData : mWikiMemberList) {
                    if (Util.isEqualString(memberData.getNoSpaceName(), wikiData.getNoSpaceName())) {
                        switch (locale) {
                            case "KR":
                                localeName = wikiData.getNameKo();
                                break;
                            default:
                                localeName = wikiData.getNameEn();
                                break;
                        }
                        memberData.setGeneration(wikiData.getGeneration());
                        memberData.setBirthday(wikiData.getBirthday());
                        break;
                    }
                }
                if (localeName == null || localeName.isEmpty()) {
                    localeName = memberData.getNameEn();
                }
                if (localeName == null || localeName.isEmpty()) {
                    localeName = memberData.getName();
                }
                memberData.setLocaleName(localeName);

                //mProgressValue++;
            }

            //Log.e(mTag, "...........");

            LinearLayout loContent = (LinearLayout) findViewById(R.id.loContent);
            if (loContent != null) {
                loContent.setVisibility(View.VISIBLE);

                mLoProfile = (LinearLayout) findViewById(R.id.loProfile);

                /*switch (mGroupData.getGroupId()) {
                    case Config.GROUP_ID_AKB48:
                        mIvPicture = (ImageView) findViewById(R.id.ivPictureFixed);
                        break;
                    case Config.GROUP_ID_JKT48:
                        mIvPicture = (ImageView) findViewById(R.id.ivPictureBordered);
                        break;
                    default:
                        mIvPicture = (ImageView) findViewById(R.id.ivPictureFull);
                        break;
                }*/

                mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
                mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
                mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));

                mDensity = mContext.getResources().getDisplayMetrics().density;
                int width = 0;
                //int margin = 0;

                mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                //mParams.setMargins(0, margin, 0, 0);
                mTvName = (TextView) findViewById(R.id.tvName);
                mTvInfo = (TextView) findViewById(R.id.tvInfo);
                mTvLocaleName = (TextView) findViewById(R.id.tvLocaleName);
                //mIvIndicator1 = (ImageView) findViewById(R.id.ivIndicator1);
                //mIvIndicator2 = (ImageView) findViewById(R.id.ivIndicator2);

                Collections.shuffle(mMemberList);

                goNext();
            }
        }
    }

    private void goNext() {
        goNextFlipper();
    }

    private void goNextFlipper() {
        //Log.e(mTag, "goNext().mMemberPosition" + mMemberPosition);

        if (mMemberPosition >= mMemberList.size()) {
            mMemberPosition = 0;
            mIsAllDataLoaded = true;
        }

        mBaseToolbar.setTitle(mTitle + " (" + (mMemberPosition + 1) + "/" + mMemberList.size() + ")");

        mMemberData = mMemberList.get(mMemberPosition);

        renderPicture();

        /*mRunnable = new Runnable() {
            public void run() {
                goNext();
            }
        };
        mHandler.postDelayed(mRunnable, mInterval);*/
    }

    private void goNextFragment() {
        //Log.e(mTag, "goNext().mMemberPosition" + mMemberPosition);

        if (mMemberPosition >= mMemberList.size()) {
            mMemberPosition = 0;
        }

        try {
            Fragment newFragment = SlideFragment.newInstance(mMemberPosition, mMemberList.get(mMemberPosition));
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (mIsFirstData) {
                mIsFirstData = false;
            } else {
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            }
            transaction.replace(R.id.loContent, newFragment);
            transaction.commitAllowingStateLoss();

            mMemberPosition++;
            mBaseToolbar.setTitle(mTitle + " (" + mMemberPosition + "/" + mMemberList.size() + ")");

            /*mRunnable = new Runnable() {
                public void run() {
                    goNext();
                }
            };
            mHandler.postDelayed(mRunnable, mInterval);*/

        } catch (IllegalStateException e) {
            e.printStackTrace();
            doFinish();
        }
    }

    private void renderPicture() {
        //Log.e(mTag, "renderPicture()...");

        String imageUrl = mMemberData.getImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = mMemberData.getThumbnailUrl();
        }
        //Log.e(mTag, "imageUrl: " + imageUrl);

        if (imageUrl == null || imageUrl.isEmpty()) {
            //mPbPictureLoading.setVisibility(View.GONE);
            renderProfile();
        } else {
            if (mIsAllDataLoaded) {
                mViewFlipper.showNext();
                renderProfile();
            } else {
                /*
                4.2에서 setAdjustViewBounds() 적용이 안되어 이미지뷰 객체의 높이 계산이 제대로 안됨.
                이미지뷰 객체 생성 시 ImageView가 아닌 ProportionalImageView를 사용한다.
                 */
                ProportionalImageView iv = new ProportionalImageView(mContext);
                iv.setLayoutParams(mParams);
                iv.setAdjustViewBounds(true);
                /*if (mGroupData.getGroupId().equals(Config.GROUP_ID_JKT48)) {
                    int padding = (int) (8 * mDensity);
                    iv.setPadding(padding, padding, padding, padding);
                    //iv.setBackgroundResource(R.drawable.polaroid);
                }*/
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

                LinearLayout ll = new LinearLayout(mContext);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                ll.setLayoutParams(params);
                ll.setGravity(Gravity.CENTER);
                ll.addView(iv);

                mViewFlipper.addView(ll);

                showToolbarProgressBar();

                // Glide는 이미지뷰 크기를 제대로 계산하지 못 한다.

                if (mIsFirstData) {
                    Picasso.with(mContext).load(imageUrl).placeholder(R.drawable.transparent).error(R.drawable.anonymous).into(iv, new Callback() {
                        @Override
                        public void onSuccess() {
                            renderProfile();
                        }

                        @Override
                        public void onError() {
                            //Log.e(mTag, "Picasso.onError()... " + imageUrl);
                            renderProfile();
                        }
                    });
                } else {
                    Picasso.with(mContext).load(imageUrl).placeholder(R.drawable.transparent).error(R.drawable.anonymous).noFade().into(iv, new Callback() {
                        @Override
                        public void onSuccess() {
                            mViewFlipper.showNext();
                            renderProfile();
                        }

                        @Override
                        public void onError() {
                            //Log.e(mTag, "Picasso.onError()... " + imageUrl);
                            mViewFlipper.showNext();
                            renderProfile();
                        }
                    });
                }
            }

            /*Glide.with(mContext).load(imageUrl).asBitmap().dontAnimate().diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                            //mIvPicture.setVisibility(View.VISIBLE);
                            //if (cacheUri == null) {
                            //    ImageUtil.saveBitmapToPng(bitmap, cacheId); // 캐쉬 저장
                            //}
                            renderProfile();
                        }
                    });*/

            /*Picasso.with(mContext).load(imageUrl).noFade().into(mIvPicture, new Callback() {
                @Override
                public void onSuccess() {
                    //mPbPictureLoading.setVisibility(View.GONE);
                    mIvPicture.setVisibility(View.VISIBLE);
                    renderProfile();
                }

                @Override
                public void onError() {
                    Log.e(mTag, "Picasso.onError()...");
                    mIvPicture.setVisibility(View.GONE);
                    //mPbPictureLoading.setVisibility(View.GONE);
                }
            });*/
        }

        /*Glide.with(getActivity()).load(imageUrl).listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        Log.e(mTag, "onException()...");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        mPbPictureLoading.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(imageView);*/
    }

    private void renderProfile() {

        // 이름
        mTvName.setText(mMemberData.getName());

        // 그룹 이름
        String groupName = mMemberData.getGroupName();
        String groupTeam = groupName;
        String teamName = mMemberData.getTeamName();
        if (teamName != null && !teamName.isEmpty() && !teamName.equals(groupName)) {
            groupTeam += " " + mMemberData.getTeamName();
        }
        mTvInfo.setVisibility(View.VISIBLE);
        mTvInfo.setText(groupTeam);

        // 사용자 언어 이름
        String localeName = mMemberData.getLocaleName();
        if (localeName == null || localeName.isEmpty()) {
            mTvLocaleName.setVisibility(View.GONE);
        } else {
            mTvLocaleName.setVisibility(View.VISIBLE);
            mTvLocaleName.setText(localeName);
        }

        mLoProfile.setVisibility(View.VISIBLE);

        //mIvIndicator1.setImageResource(R.drawable.ic_dot_gray);
        //mIvIndicator2.setImageResource(R.drawable.ic_dot_gray);

        hideToolbarProgressBar();

        mHandler = new Handler();
        mRunnable = new Runnable() {
            public void run() {
                mMemberPosition++;
                mIsFirstData = false;
                removeCallback();
                goNext();
            }
        };
        mHandler.postDelayed(mRunnable, 3000);

        //rotateIndicator();
    }

    /*private void rotateIndicator() {
        //Log.e(mTag, "mCount: " + mCount);
        mHandler = new Handler();
        mRunnable = new Runnable() {
            public void run() {
                mIndicatoerCount++;
                //Log.e(mTag, "mCount: " + mCount);

                if (mIndicatoerCount > 2) {
                    mIndicatoerCount = 0;
                    mMemberPosition++;
                    mIsFirstData = false;
                    removeCallback();
                    goNext();
                } else {
                    setIndicator();
                    rotateIndicator();
                }
            }
        };
        mHandler.postDelayed(mRunnable, 1000);
    }

    private void setIndicator() {
        switch (mIndicatoerCount) {
            case 1:
                mIvIndicator1.setImageResource(R.drawable.ic_dot_light_red);
                break;
            case 2:
                mIvIndicator2.setImageResource(R.drawable.ic_dot_light_red);
                break;
        }
    }*/

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Log.e(mTag, "onKeyDown().keyCode: " + keyCode);
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    doFinish();
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }*/

    @Override
    public void onDestroy() {
        //Log.e(mTag, "onDestroy()...");
        super.onDestroy();
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
