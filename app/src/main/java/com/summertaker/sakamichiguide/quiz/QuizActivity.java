package com.summertaker.sakamichiguide.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.squareup.picasso.Picasso;
import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseActivity;
import com.summertaker.sakamichiguide.common.BaseApplication;
import com.summertaker.sakamichiguide.common.CacheManager;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.common.Setting;
import com.summertaker.sakamichiguide.data.GroupData;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.data.TeamData;
import com.summertaker.sakamichiguide.parser.BaseParser;
import com.summertaker.sakamichiguide.parser.NamuwikiParser;
import com.summertaker.sakamichiguide.parser.WikipediaEnParser;
import com.summertaker.sakamichiguide.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QuizActivity extends BaseActivity {

    boolean mShowOfficialPhoto;

    GroupData mGroupData;
    MemberData mMemberData;

    ArrayList<TeamData> mTeamList = new ArrayList<>();
    ArrayList<MemberData> mMemberList = new ArrayList<>();
    ArrayList<MemberData> mWikiMemberList = new ArrayList<>();

    String mTitle;
    int mPosition = 0;
    int mValidCount = 0;

    LinearLayout mLoLoading;
    ProgressBar mPbLoading;
    ProgressBar mPbPictureLoading;

    LinearLayout mLoContent;
    ImageView mIvPicture;
    TextView mTvName;
    TextView mTvInfo;

    String mMemberName;
    String mRandomName;

    Button mBtInvalid;
    Button mBtValid;
    ImageView mIvPositive;
    ImageView mIvNegative;

    String mLocale;
    BaseParser mWikiParser;
    CacheManager mCacheManager;

    boolean isDataLoaded = false;
    boolean isWikiLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_activity);

        mContext = QuizActivity.this;

        Intent intent = getIntent();
        mGroupData = (GroupData) intent.getSerializableExtra("groupData");
        //mMemberData = (MemberData) intent.getSerializableExtra("memberData");

        Setting setting = new Setting(mContext);
        mShowOfficialPhoto = setting.get(Config.SETTING_DISPLAY_OFFICIAL_PHOTO).equals(Config.SETTING_DISPLAY_OFFICIAL_PHOTO_YES);
        //Log.e(mTag, "mShowOfficialPhoto: " + mShowOfficialPhoto);

        mTitle = mGroupData.getName();
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, mTitle);

        mLoLoading = (LinearLayout) findViewById(R.id.loLoading);
        mPbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        Util.setProgressBarColor(mPbLoading, Config.PROGRESS_BAR_COLOR_NORMAL, null);

        mLoContent = (LinearLayout) findViewById(R.id.loContent);

        mBtValid = (Button) findViewById(R.id.btValid);
        mBtInvalid = (Button) findViewById(R.id.btVallid);

        mIvPositive = (ImageView) findViewById(R.id.ivPositive);
        mIvNegative = (ImageView) findViewById(R.id.ivNegative);

        mLocale = Util.getLocaleStrng(mContext);
        switch (mLocale) {
            case "KR":
                mWikiParser = new NamuwikiParser();
                break;
            default:
                mWikiParser = new WikipediaEnParser();
                break;
        }

        mCacheManager = new CacheManager(mSharedPreferences);

        String url = mGroupData.getUrl();
        String userAgent = Config.USER_AGENT_WEB;

        switch (mGroupData.getId()) {
            //case Config.GROUP_ID_NGT48:
            case Config.GROUP_ID_NOGIZAKA46:
                url = mGroupData.getMobileUrl(); // desktop html은 thumbnail 이미지를 css의 sprite 사용함.
                userAgent = Config.USER_AGENT_MOBILE;
                break;
        }

        requestData(url, userAgent);
        requestWiki();
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
                Log.e(mTag, "url: " + url);
                //mCacheManager.save(cacheId, response);
                parseData(url, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(mTag, "url:" + url);
                if (url.contains("wiki")) {
                    parseData(url, "");
                } else {
                    alertNetworkErrorAndFinish(null);
                }
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
            isWikiLoaded = true;
        } else {
            boolean isMobile = url.equals(mGroupData.getMobileUrl());
            BaseParser baseParser = new BaseParser();
            baseParser.parseMemberList(response, mGroupData, mMemberList, mTeamList, isMobile);

            isDataLoaded = true;
        }

        if (isDataLoaded && isWikiLoaded) {
            mPbPictureLoading = (ProgressBar) findViewById(R.id.pbPictureLoading);

            if (mShowOfficialPhoto) {
                //--------------------------
                // 사진과 함께 보여주는 경우
                //--------------------------
                Util.setProgressBarColor(mPbPictureLoading, Config.PROGRESS_BAR_COLOR_LIGHT, null);

                RelativeLayout loWithPicture = (RelativeLayout) findViewById(R.id.loWithPicture);
                if (loWithPicture != null) {
                    loWithPicture.setVisibility(View.VISIBLE);
                    mIvPicture = (ImageView) findViewById(R.id.ivPictureFull);
                }

                mTvName = (TextView) findViewById(R.id.tvNameWithPicture);
                if (mTvName != null) {
                    mTvName.setVisibility(View.VISIBLE);
                }
            } else {
                //--------------------------
                // 텍스트로 보여주는 경우
                //--------------------------
                mPbPictureLoading.setVisibility(View.GONE);

                LinearLayout loWithoutPicture = (LinearLayout) findViewById(R.id.loWithoutPicture);
                if (loWithoutPicture != null) {
                    loWithoutPicture.setVisibility(View.VISIBLE);

                    mTvName = (TextView) findViewById(R.id.tvNameWithoutPicture);
                    mTvInfo = (TextView) findViewById(R.id.tvInfoWithoutPicture);
                    if (mTvInfo != null) {
                        mTvInfo.setVisibility(View.VISIBLE);
                    }
                }
            }

            renderData();
        }
    }

    private void renderData() {
        if (mPosition == 0) {
            String locale = Util.getLocaleStrng(mContext);
            for (MemberData memberData : mMemberList) {
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
            }

            Collections.shuffle(mMemberList);

            mLoLoading.setVisibility(View.GONE);
            mLoContent.setVisibility(View.VISIBLE);
        }

        if (mPosition >= mMemberList.size()) {
            //--------------------
            // 결과 화면으로 이동
            //--------------------
            startQuizResultActivity();
        } else {
            mBaseToolbar.setTitle(mTitle + " (" + (mPosition + 1) + "/" + mMemberList.size() + ")");

            mMemberData = mMemberList.get(mPosition);

            // 멤버 정보
            String name = mMemberData.getName();
            mTvName.setText(name);

            if (!mShowOfficialPhoto) {
                String info = mMemberData.getGroupName();
                String teamName = mMemberData.getTeamName();
                if (teamName != null && !teamName.isEmpty() && !teamName.equals(info)) {
                    info += " " + mMemberData.getTeamName();
                }
                mTvInfo.setText(info);
            }

            if (mShowOfficialPhoto) {
                String imageUrl = mMemberData.getImageUrl();
                if (imageUrl == null || imageUrl.isEmpty()) {
                    imageUrl = mMemberData.getThumbnailUrl();
                }
                //Log.e("#####", "imageUrl: " + mMemberData.getGroupId() + " - " +imageUrl);

                //final String cacheId = Util.urlToId(imageUrl);
                //final String cacheUri = ImageUtil.getValidCacheUri(cacheId);
                //if (cacheUri != null) {
                //    imageUrl = cacheUri;
                //}

                if (imageUrl == null || imageUrl.isEmpty()) {
                    mPbPictureLoading.setVisibility(View.GONE);
                    mIvPicture.setVisibility(View.GONE);
                } else {
                    mPbPictureLoading.setVisibility(View.VISIBLE);
                    mIvPicture.setVisibility(View.GONE);

                    Picasso.with(mContext).load(imageUrl).into(mIvPicture, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            mPbPictureLoading.setVisibility(View.GONE);
                            mIvPicture.setVisibility(View.VISIBLE);

                            mBtInvalid.setEnabled(true);
                            mBtValid.setEnabled(true);
                        }

                        @Override
                        public void onError() {
                            mPbPictureLoading.setVisibility(View.GONE);
                        }
                    });

                    /*// https://futurestud.io/blog/glide-callbacks-simpletarget-and-viewtarget-for-custom-view-classes
                    Glide.with(mContext).load(imageUrl).asBitmap()
                            //.override(Config.IMAGE_FULL_WIDTH, Config.IMAGE_FULL_HEIGHT)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                                    mPbPictureLoading.setVisibility(View.GONE);
                                    mIvPicture.setImageBitmap(bitmap);
                                    mIvPicture.setVisibility(View.VISIBLE);
                                    //if (cacheUri == null) {
                                    //    ImageUtil.saveBitmapToPng(bitmap, cacheId); // 캐쉬 저장
                                    //}

                                    mBtInvalid.setEnabled(true);
                                    mBtValid.setEnabled(true);
                                }
                            });*/
                }
            }

            //-------------------------------------------------
            // 현재 멤버 이름과 중복되지 않는 랜덤한 이름 선택하기
            //-------------------------------------------------
            ArrayList<Integer> indexList = new ArrayList<>();
            for (int i = 0; i < mMemberList.size(); i++) {
                if (i != mPosition) {
                    indexList.add(i);
                }
            }
            Collections.shuffle(indexList);
            int randomIndex = indexList.get(0);

            MemberData randomData = mMemberList.get(randomIndex);

            mMemberName = mMemberData.getLocaleName();
            mRandomName = randomData.getLocaleName();

            String validName;
            String invalidName;
            if (randomIndex % 2 == 0) { // even
                validName = mMemberName;
                invalidName = mRandomName;
            } else {
                validName = mRandomName;
                invalidName = mMemberName;
            }

            //Log.e(mTag, validName + " / " + invalidName);

            // Button 1
            mBtInvalid.setTag(invalidName);
            mBtInvalid.setText(invalidName);
            mBtInvalid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goNext(v);
                }
            });

            // Button 2
            mBtValid.setTag(validName);
            mBtValid.setText(validName);
            mBtValid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goNext(v);
                }
            });

            if (!mShowOfficialPhoto) {
                mBtInvalid.setEnabled(true);
                mBtValid.setEnabled(true);
            }

            //Log.e(mTag, "mValidCount: " + mValidCount);

            mPosition++;
        }
    }

    private void goNext(View v) {
        //mBtValid.setText(getString(R.string.blank));
        mBtValid.setEnabled(false);
        //mBtInvalid.setText(getString(R.string.blank));
        mBtInvalid.setEnabled(false);

        String tag = v.getTag().toString().trim();
        //Log.e(mTag, mMemberName + "/" + mRandomName + "/" + tag + "/");

        if (tag.equals(mMemberName)) {
            mIvPositive.setVisibility(View.VISIBLE);
            mValidCount++;
        } else {
            //Log.e(mTag, mMemberName + " / " + tag);
            mIvNegative.setVisibility(View.VISIBLE);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mIvPositive.setVisibility(View.GONE);
                mIvNegative.setVisibility(View.GONE);
                renderData();
            }
        }, 300);
    }

    private void startQuizResultActivity() {
        Intent intent = new Intent(this, QuizResultActivity.class);
        intent.putExtra("groupData", mGroupData);
        intent.putExtra("memberCount", mMemberList.size());
        intent.putExtra("validCount", mValidCount);
        startActivityForResult(intent, 0);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.i("AgeListActivity", "onActivityResult().resultCode: " + resultCode);

        Intent intent = new Intent();
        //intent.putExtra("articleId", mArticleId);
        setResult(Config.RESULT_CODE_FINISH, intent);
        finish();

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
