package com.summertaker.sakamichiguide.puzzle;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseActivity;
import com.summertaker.sakamichiguide.common.BaseApplication;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.data.GroupData;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.data.TeamData;
import com.summertaker.sakamichiguide.member.MemberListActivity;
import com.summertaker.sakamichiguide.parser.BaseParser;
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
    ArrayList<TeamData> mTeamDataList = new ArrayList<>();

    RelativeLayout mResultLayout;
    CountDownTimer mResultTimer;
    TextView mTvResultTimer;
    Button mBtnResultOk;

    int mCount = 0;
    int mTotal = 30;
    int mTimeCount = 0;

    ImageView[] mImageViews = new ImageView[mTotal];
    TransitionDrawable[] mTransitions = new TransitionDrawable[mTotal];

    int[] mIvIndexes = {R.id.iv1, R.id.iv2, R.id.iv3, R.id.iv4, R.id.iv5, R.id.iv6, R.id.iv7, R.id.iv8, R.id.iv9, R.id.iv10,
            R.id.iv11, R.id.iv12, R.id.iv13, R.id.iv14, R.id.iv15, R.id.iv16, R.id.iv17, R.id.iv18, R.id.iv19, R.id.iv20,
            R.id.iv21, R.id.iv22, R.id.iv23, R.id.iv24, R.id.iv25, R.id.iv26, R.id.iv27, R.id.iv28, R.id.iv29, R.id.iv30};
    int[] mPlIndexes = {R.id.pl1, R.id.pl2, R.id.pl3, R.id.pl4, R.id.pl5, R.id.pl6, R.id.pl7, R.id.pl8, R.id.pl9, R.id.pl10,
            R.id.pl11, R.id.pl12, R.id.pl13, R.id.pl14, R.id.pl15, R.id.pl16, R.id.pl17, R.id.pl18, R.id.pl19, R.id.pl20,
            R.id.pl21, R.id.pl22, R.id.pl23, R.id.pl24, R.id.pl25, R.id.pl26, R.id.pl27, R.id.pl28, R.id.pl29, R.id.pl30};
    int[] mPbIndexes = {R.id.pb1, R.id.pb2, R.id.pb3, R.id.pb4, R.id.pb5, R.id.pb6, R.id.pb7, R.id.pb8, R.id.pb9, R.id.pb10,
            R.id.pb11, R.id.pb12, R.id.pb13, R.id.pb14, R.id.pb15, R.id.pb16, R.id.pb17, R.id.pb18, R.id.pb19, R.id.pb20,
            R.id.pb21, R.id.pb22, R.id.pb23, R.id.pb24, R.id.pb25, R.id.pb26, R.id.pb27, R.id.pb28, R.id.pb29, R.id.pb30};

    int mCardBackground = 0;

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
                mTotal = 12;
                break;
            case Config.PUZZLE_LEVEL_HARD:
                setContentView(R.layout.puzzle_hard_activity);
                mTotal = 30;
                break;
            default:
                setContentView(R.layout.puzzle_normal_activity);
                mTotal = 20;
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
        BaseParser baseParser = new BaseParser();
        baseParser.parseMemberList(response, mGroupData, mGroupMemberList, mTeamDataList, mIsMobile);

        renderData();
    }

    private void renderData() {
        mPbLoading.setVisibility(View.GONE);

        //String team = getString(R.string.team);
        //String trainee = getString(R.string.trainee);

        if (mGroupMemberList.size() == 0) {
            alertNetworkErrorAndFinish(mErrorMessage);
        } else {
            LinearLayout grid = (LinearLayout) findViewById(R.id.grid);
            grid.setVisibility(View.VISIBLE);

            Collections.shuffle(mGroupMemberList);

            int count = 0;
            int total = mTotal / 2;
            for (int i = 0; i < total; i++) {
                MemberData oldData = mGroupMemberList.get(i);

                MemberData data1 = new MemberData();
                data1.setId(count + "");
                data1.setName(oldData.getName());
                data1.setThumbnailUrl(oldData.getThumbnailUrl());
                mMemberList.add(data1);
                count++;

                MemberData data2 = new MemberData();
                data2.setId(count + "");
                data2.setName(oldData.getName());
                data2.setThumbnailUrl(oldData.getThumbnailUrl());
                mMemberList.add(data2);
                count++;
            }

            Collections.shuffle(mMemberList);
            Collections.shuffle(mMemberList);
            Collections.shuffle(mMemberList);

            for (int i = 0; i < mTotal; i++) {

                MemberData memberData = mMemberList.get(i);
                final int index = Integer.parseInt(memberData.getId());
                //Log.e(mTag, "index: " + index);

                final RelativeLayout pl = (RelativeLayout) findViewById(mPlIndexes[i]);
                final ProgressBar pb = (ProgressBar) findViewById(mPbIndexes[i]);
                Util.setProgressBarColor(pb, Config.PROGRESS_BAR_COLOR_LIGHT, null);

                mImageViews[index] = (ImageView) findViewById(mIvIndexes[i]);
                mImageViews[index].setTag(memberData);

                String imageUrl = memberData.getThumbnailUrl();
                Glide.with(mContext).load(imageUrl).asBitmap().dontAnimate() //.diskCacheStrategy(DiskCacheStrategy.RESULT)
                        //.override(Config.IMAGE_GRID3_WIDTH, Config.IMAGE_GRID3_HEIGHT)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                                //holder.loLoading.setVisibility(View.GONE);
                                //holder.ivPicture.setImageBitmap(bitmap);

                                pl.setVisibility(View.GONE);
                                pb.setVisibility(View.GONE);

                                mTransitions[index] = new TransitionDrawable(new Drawable[]{
                                        ContextCompat.getDrawable(mContext, mCardBackground),
                                        new BitmapDrawable(getResources(), bitmap)
                                });
                                mImageViews[index].setImageDrawable(mTransitions[index]);
                                mImageViews[index].setScaleType(ImageView.ScaleType.CENTER_CROP);
                                mImageViews[index].setVisibility(View.VISIBLE);

                                mCount++;
                                onImageLoaded();

                                //if (cacheUri == null) {
                                //    ImageUtil.saveBitmapToPng(bitmap, cacheId); // 캐쉬 저장
                                //}
                            }
                        });

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
        if (mCount == mTotal) {

            mHandler = new Handler();
            mRunnable = new Runnable() {
                public void run() {
                    removeCallback();
                    renderPuzzle();
                }
            };
            mHandler.postDelayed(mRunnable, 500);
        }
    }

    private void renderPuzzle() {
        for (int i = 0; i < mTotal; i++) {

            ImageView imageView = mImageViews[i];

            /*
            MemberData memberData = (MemberData) imageView.getTag();
            int index = Integer.parseInt(memberData.getId());

            mTransitions[index] = new TransitionDrawable(new Drawable[]{
                    ContextCompat.getDrawable(mContext, R.drawable.pattern_blossom),
                    imageView.getDrawable()
            });
            imageView.setImageDrawable(mTransitions[index]);
            //mTransitions[index].startTransition(0);
            //mTransitions[index].reverseTransition(3000);
            */

            /*AnimatorSet ani = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.flip);
            ani.setTarget(imageView);
            ani.setDuration(500);
            ani.start();*/

            setEvent(imageView);
        }

        mResultTimer = new CountDownTimer(1000000000, 1000) {

            public void onTick(long millisUntilFinished) {
                //Calendar c = Calendar.getInstance();
                //String time = c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);

                if (mTvResultTimer != null) {
                    int min = mTimeCount / 60;
                    int sec = mTimeCount % 60;
                    String text = sec + "초";
                    if (min > 0) {
                        text = min + "분 " + text;
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
        if (mFounds.size() == mTotal) {
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

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TeamData teamData = (TeamData) parent.getItemAtPosition(position);
            //MemberData memberData = (MemberData) teamData.getMemberData();
            //Log.e(mTag, "memberData.getTeamName(): " + memberData.getTeamName());

            ArrayList<MemberData> memberList = new ArrayList<>();
            for (MemberData memberData : mGroupMemberList) {
                if (memberData.getTeamName().equals(teamData.getName())) {
                    //Log.e(mTag, ">> " + md.getTeamName());
                    memberData.setGroupId(mGroupData.getId());
                    memberList.add(memberData);
                }
            }
            startMemberListActivity(teamData, memberList);
        }
    };

    public void startMemberListActivity(TeamData teamData, ArrayList<MemberData> memberList) {
        Intent intent = new Intent(this, MemberListActivity.class);
        //Intent intent = new Intent(this, MemberRecyclerActivity.class);
        intent.putExtra("groupData", mGroupData);
        intent.putExtra("teamData", teamData);
        intent.putExtra("memberList", memberList);

        showToolbarProgressBar();

        startActivityForResult(intent, 0);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.e(mTag, "onActivityResult().resultCode: " + resultCode);

        hideToolbarProgressBar();

        if (resultCode == Config.RESULT_CODE_FINISH) {
            Intent intent = new Intent();
            //intent.putExtra("articleId", mArticleId);
            setResult(Config.RESULT_CODE_FINISH, intent);
            finish();
        }

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
