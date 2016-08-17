package com.summertaker.sakamichiguide.member;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightGridView;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.squareup.picasso.Picasso;
import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseActivity;
import com.summertaker.sakamichiguide.common.BaseApplication;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.common.Setting;
import com.summertaker.sakamichiguide.common.WebDataAdapter;
import com.summertaker.sakamichiguide.data.GroupData;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.data.MenuData;
import com.summertaker.sakamichiguide.data.TeamData;
import com.summertaker.sakamichiguide.data.WebData;
import com.summertaker.sakamichiguide.parser.BaseParser;
import com.summertaker.sakamichiguide.parser.NamuwikiParser;
import com.summertaker.sakamichiguide.parser.Pedia48ProfileParser;
import com.summertaker.sakamichiguide.parser.YahooParser;
import com.summertaker.sakamichiguide.util.Translator;
import com.summertaker.sakamichiguide.util.Util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemberDetailActivity extends BaseActivity {

    //Snackbar mSnackbar;

    boolean mShowOfficialPhoto;

    //private String mFrom;
    private GroupData mGroupData;
    private TeamData mTeamData;
    private MemberData mMemberData;

    private boolean mIsMobile = false;

    //private Button mBtFollow;
    //private Button mBtFollowing;

    private LinearLayout mLoLoading;
    private ProgressBar mPbPictureLoading;
    private RelativeLayout mLoYahooLoading;
    private RelativeLayout mLoPedia48Loading;

    private ArrayList<WebData> mYahooList = new ArrayList<>();
    private ArrayList<WebData> mPedia48List = new ArrayList<>();

    //private CacheManager mCacheManager;
    //private OshimenManager mOshimenManager;

    boolean isDataLoaded = false;
    boolean isWikiLoaded = false;

    String mLocalGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_detail_activity);

        mContext = MemberDetailActivity.this;

        Setting setting = new Setting(mContext);
        mShowOfficialPhoto = setting.get(Config.SETTING_DISPLAY_OFFICIAL_PHOTO).equals(Config.SETTING_DISPLAY_OFFICIAL_PHOTO_YES);
        //Log.e(mTag, "mShowOfficialPhoto: " + mShowOfficialPhoto);

        //CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        //mSnackbar = Snackbar.make(coordinatorLayout, "...", Snackbar.LENGTH_SHORT).setAction("Action", null);

        Intent intent = getIntent();
        //mFrom = (String) intent.getStringExtra("from");
        mGroupData = (GroupData) intent.getSerializableExtra("groupData");
        mTeamData = (TeamData) intent.getSerializableExtra("teamData");
        mMemberData = (MemberData) intent.getSerializableExtra("memberData");

        String title = mGroupData.getName();
        if (mTeamData != null) {
            String teamName = mTeamData.getName();
            if (!title.equals(teamName)) {
                Translator translator = new Translator(mContext);
                teamName = translator.translateTeam(mGroupData.getId(), teamName);
                title = title + " " + teamName;
            }
        }
        title = mMemberData.getLocaleName() + " / " + title;
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, title);

        //mBtFollow = (Button) findViewById(R.id.btFollow);
        //mBtFollowing = (Button) findViewById(R.id.btFollowing);

        mLoLoading = (LinearLayout) findViewById(R.id.loLoading);
        ProgressBar pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        Util.setProgressBarColor(pbLoading, Config.PROGRESS_BAR_COLOR_NORMAL, null);

        mPbPictureLoading = (ProgressBar) findViewById(R.id.pbPictureLoading);
        Util.setProgressBarColor(mPbPictureLoading, Config.PROGRESS_BAR_COLOR_NORMAL, null);

        //mCacheManager = new CacheManager(mSharedPreferences);
        //mOshimenManager = new OshimenManager(mSharedPreferences);

        mLoYahooLoading = (RelativeLayout) findViewById(R.id.loYahooLoading);
        ProgressBar pbYahooLoading = (ProgressBar) findViewById(R.id.pbYahooLoading);
        Util.setProgressBarColor(pbYahooLoading, Config.PROGRESS_BAR_COLOR_LIGHT, null);

        mLoPedia48Loading = (RelativeLayout) findViewById(R.id.loPedia48Loading);
        ProgressBar pbPedia48Loading = (ProgressBar) findViewById(R.id.pbPedia48Loading);
        Util.setProgressBarColor(pbPedia48Loading, Config.PROGRESS_BAR_COLOR_LIGHT, null);

        switch (mGroupData.getId()) {
            case Config.GROUP_ID_NOGIZAKA46:
                mLocalGroupName = "乃木坂46";
                break;
            case Config.GROUP_ID_KEYAKIZAKA46:
                mLocalGroupName = "欅坂46";
                break;
        }

        loadProfile();
        loadNamuwiki();
    }

    private void loadProfile() {
        //Log.e(mTag, ">>>>> loadOfficialProfile()");

        String url = mMemberData.getProfileUrl();
        String userAgent = Config.USER_AGENT_WEB;

        switch (mMemberData.getGroupId()) {
            case Config.GROUP_ID_NOGIZAKA46:
                mIsMobile = true;
                userAgent = Config.USER_AGENT_MOBILE;
                break;
        }
        requestData(url, userAgent);
    }

    private void loadNamuwiki() {
        String namuwikiUrl = mMemberData.getNamuwikiUrl();
        //Log.e(mTag, "namuwikiUrl: " + namuwikiUrl);

        if (namuwikiUrl == null || namuwikiUrl.isEmpty()) {
            isWikiLoaded = true;
            renderProfile();
        } else {
            requestData(namuwikiUrl, Config.USER_AGENT_WEB);
        }
    }

    private void requestData(final String url, final String userAgent) {
        //Log.e(mTag, "url = " + url);

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
                //Log.e(mTag, "ERROR: " + error.getMessage());
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
            /*//showToolbarProgressBar();
            int SOCKET_TIMEOUT_MS = 30000; // 30초
            if (!url.contains("wiki") && mGroupData.getGroupId().equals(Config.GROUP_ID_JKT48)) {
                //Log.e(mTag, "SOCKET_TIMEOUT_MS: " + SOCKET_TIMEOUT_MS);
                strReq.setRetryPolicy(new DefaultRetryPolicy(SOCKET_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            }*/
        BaseApplication.getInstance().addToRequestQueue(strReq, "string_req");
        //} else {
        //    parseData(url, cacheData);
        //}
    }

    private void parseData(String url, String response) {
        //Log.e(mTag, "url: " + url);
        //Log.e(mTag, "response: " + response);
        //Log.e(mTag, "mMemberData.getProfileUrl(): " + mMemberData.getProfileUrl());

        if (url.contains("namu.wiki")) {
            parseNamuwiki(url, response);
        } else if (url.contains("48pedia.org")) {
            parsePedia48(url, response);
        } else if (url.contains("yahoo.co.jp")) {
            parseYahoo(url, response);
        } else {
            parseProfile(url, response);
        }
    }

    private void parseProfile(String url, String response) {
        //Log.e(mTag, "parseProfile()");

        BaseParser baseParser = new BaseParser();
        HashMap<String, String> hashMap = baseParser.parseProfile(mGroupData, url, response, mIsMobile);
        //Log.e(mTag, hashMap.toString());

        //--------------------------------------------
        // 공식 사이트의 이름 정보 가져오기
        // 오시멘 캐쉬 데이터가 유효한지 체크하기 위해서
        //--------------------------------------------
        /*String webPageName;
        switch (mMemberData.getGroupId()) {
            case Config.GROUP_ID_JKT48:
                webPageName = hashMap.get("nameId");
                break;
            default:
                webPageName = hashMap.get("nameJa");
                break;
        }*/
        //Log.e(mTag, "webPageName: " + webPageName);

        String isOk = null;
        if (hashMap != null) {
            isOk = hashMap.get("isOk");
        }
        if (isOk == null || !isOk.equals("ok")) {
            //----------------------------------------------------------------------------------
            // 공식 사이트 상세 페이지의 이름 (가져오지 못한 경우 웹 페이지가 없거나 구조가 변경된 경우)
            //----------------------------------------------------------------------------------
            Log.e(mTag, "ERROR: " + url);
            mLoLoading.setVisibility(View.GONE);
            //mOshimenManager.save(mMemberData); // 오시멘 지우기
            alertNetworkErrorAndFinish(mErrorMessage);
        } else {
            isDataLoaded = true;
            updateMemberData(hashMap);
        }
    }

    private void parseNamuwiki(String url, String response) {
        //Log.e(mTag, "parseNamuwiki(): " + response.substring(0, 100));

        NamuwikiParser namuwikiParser = new NamuwikiParser();
        HashMap<String, String> hashMap = namuwikiParser.parseProfile(response);

        isWikiLoaded = true;
        updateMemberData(hashMap);
    }

    private void updateMemberData(HashMap<String, String> hashMap) {
        //Log.e(mTag, "updateMemberData()");

        String url;

        String furigana = hashMap.get("furigana");
        if (furigana != null && !"null".equals(furigana) && !furigana.isEmpty()) {
            mMemberData.setFurigana(furigana);
        }

        /*String nameEn = hashMap.get("nameEn");
        if (nameEn != null && !nameEn.isEmpty()) {
            nameEn = Util.ucfirstAll(nameEn);
            mMemberData.setNameEn(nameEn);
        }*/

        url = hashMap.get("imageUrl");
        if (url != null && !url.isEmpty()) {
            mMemberData.setImageUrl(url);
        }

        url = hashMap.get(Config.SITE_ID_GOOGLE_PLUS);
        if (url != null && !url.isEmpty()) {
            String[] info = Util.getSnsInfo(url);
            mMemberData.setGooglePlusId(info[0]);
            mMemberData.setGooglePlusUrl(info[1]);
        }

        url = hashMap.get(Config.SITE_ID_TWITTER);
        if (url != null && !url.isEmpty()) {
            String[] info = Util.getSnsInfo(url);
            mMemberData.setTwitterId(info[0]);
            mMemberData.setTwitterUrl(info[1]);
        }

        url = hashMap.get(Config.SITE_ID_FACEBOOK);
        if (url != null && !url.isEmpty()) {
            String[] info = Util.getSnsInfo(url);
            mMemberData.setFacebookId(info[0]);
            mMemberData.setFacebookUrl(info[1]);
        }

        url = hashMap.get(Config.SITE_ID_INSTAGRAM);
        if (url != null && !url.isEmpty()) {
            //Log.e(mTag, url);
            String[] info = Util.getSnsInfo(url);
            mMemberData.setInstagramId(info[0]);
            mMemberData.setInstagramUrl(info[1]);
        }

        url = hashMap.get(Config.SITE_ID_BLOG);
        if (url != null && !url.isEmpty()) {
            mMemberData.setBlogUrl(url);
            mMemberData.setBlogName(hashMap.get("blogName"));
        }

        url = hashMap.get(Config.SITE_ID_NANAGOGO);
        if (url != null && !url.isEmpty()) {
            mMemberData.setNanagogoUrl(url);
        }

        url = hashMap.get(Config.SITE_ID_WEIBO);
        if (url != null && !url.isEmpty()) {
            mMemberData.setWeiboUrl(url);
        }

        url = hashMap.get(Config.SITE_ID_QQ);
        if (url != null && !url.isEmpty()) {
            mMemberData.setQqUrl(url);
        }

        url = hashMap.get(Config.SITE_ID_BAIDU);
        if (url != null && !url.isEmpty()) {
            mMemberData.setBaiduUrl(url);
        }

        String html = hashMap.get("html");
        if (html != null && !html.isEmpty()) {
            mMemberData.setInfo(html);
        }

        renderProfile();
    }

    private void renderProfile() {
        if (!isDataLoaded || !isWikiLoaded) {
            return;
        }
        //Log.e(mTag, ">>>>> renderProfile()");

        mLoLoading.setVisibility(View.GONE);
        //mPbLoading.setVisibility(View.GONE);

        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        if (scrollView == null) {
            return;
        }
        scrollView.setVisibility(View.VISIBLE);

        TextView tvName = (TextView) findViewById(R.id.tvName);
        if (tvName != null) {
            tvName.setText(mMemberData.getName());
            tvName.setVisibility(View.VISIBLE);
        }

        String furigana = mMemberData.getFurigana();
        if (furigana != null && !furigana.isEmpty()) {
            TextView tvFurigana = (TextView) findViewById(R.id.tvFurigana);
            if (tvFurigana != null) {
                tvFurigana.setText(furigana);
                tvFurigana.setVisibility(View.VISIBLE);
            }
        }

        /*String nameEn = mMemberData.getNameEn();
        if (nameEn != null && !nameEn.isEmpty()) {
            TextView tvNameEn = (TextView) findViewById(R.id.tvNameEn);
            if (tvNameEn != null) {
                tvNameEn.setText(nameEn);
                tvNameEn.setVisibility(View.VISIBLE);
            }
        }*/

        String localeName = mMemberData.getLocaleName();
        if (localeName != null && !localeName.isEmpty()) {
            TextView tvLocaleName = (TextView) findViewById(R.id.tvLocaleName);
            if (tvLocaleName != null) {
                tvLocaleName.setText(localeName);
                tvLocaleName.setVisibility(View.VISIBLE);
            }
        }

        String info = mMemberData.getInfo();
        if (info != null && !info.isEmpty()) {
            TextView tvInfo = (TextView) findViewById(R.id.tvInfo);
            if (tvInfo != null) {
                tvInfo.setVisibility(View.VISIBLE);
                tvInfo.setText(Html.fromHtml(info));
            }
        }

        boolean hasRole = false;
        float textSize = 14f;

        // 총감독
        if (mMemberData.isGeneralManager()) {
            TextView tvGeneralManager = (TextView) findViewById(R.id.tvGeneralManager);
            if (tvGeneralManager != null) {
                tvGeneralManager.setVisibility(View.VISIBLE);
                tvGeneralManager.setTextSize(textSize);
                hasRole = true;
            }
        }

        // 그룹 캡틴
        if (mMemberData.isGeneralCaptain()) {
            TextView tvGeneralCaptain = (TextView) findViewById(R.id.tvGeneralCaptain);
            if (tvGeneralCaptain != null) {
                String text = mMemberData.getGroupName() + " " + getString(R.string.general_captain);
                tvGeneralCaptain.setText(text);
                tvGeneralCaptain.setVisibility(View.VISIBLE);
                tvGeneralCaptain.setTextSize(textSize);
                hasRole = true;
            }
        }

        // 캡틴, 부캡틴, 리더, 부리더, 매니저
        String captain = getString(R.string.captain);
        String viceCaptain = getString(R.string.vice_captain);

        // 캡틴,리더
        if (mMemberData.isCaptain()) {
            TextView tvCaptain = (TextView) findViewById(R.id.tvCaptain);
            if (tvCaptain != null) {
                tvCaptain.setText(captain);
                tvCaptain.setVisibility(View.VISIBLE);
                tvCaptain.setTextSize(textSize);
                hasRole = true;
            }
        }

        // 부캡틴,부리더
        if (mMemberData.isViceCaptain()) {
            TextView tvViceCaptain = (TextView) findViewById(R.id.tvViceCaptain);
            if (tvViceCaptain != null) {
                tvViceCaptain.setText(viceCaptain);
                tvViceCaptain.setVisibility(View.VISIBLE);
                tvViceCaptain.setTextSize(textSize);
                hasRole = true;
            }
        }

        // 지배인
        if (mMemberData.isManager()) {
            TextView tvManager = (TextView) findViewById(R.id.tvManager);
            if (tvManager != null) {
                tvManager.setVisibility(View.VISIBLE);
                tvManager.setTextSize(textSize);
                hasRole = true;
            }
        }

        // 겸임
        if (mMemberData.isConcurrentPosition()) {
            TextView tvConcurrentPosition = (TextView) findViewById(R.id.tvConcurrentPosition);
            if (tvConcurrentPosition != null) {
                tvConcurrentPosition.setVisibility(View.VISIBLE);
                tvConcurrentPosition.setTextSize(textSize);
                hasRole = true;
            }
        }

        if (hasRole) {
            LinearLayout loRole = (LinearLayout) findViewById(R.id.loRole);
            if (loRole != null) {
                loRole.setVisibility(View.VISIBLE);
            }
        }

        /*ArrayList<MemberData> oshimenList = mOshimenManager.load();
        boolean exist = false;
        for (MemberData data : oshimenList) {
            if (data.getGroupId().equals(mMemberData.getGroupId())) {
                exist = true;
                break;
            }
        }

        if (exist) {
            mBtFollowing.setVisibility(View.VISIBLE);
        } else {
            mBtFollow.setVisibility(View.VISIBLE);
        }

        mBtFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                mBtFollowing.setVisibility(View.VISIBLE);
                mOshimenManager.save(mMemberData);
            }
        });

        mBtFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                mBtFollow.setVisibility(View.VISIBLE);
                mOshimenManager.save(mMemberData);
            }
        });*/

        if (mShowOfficialPhoto) {
            renderProfilePicture();
        }

        renderMenu();
    }

    private void renderProfilePicture() {
        final ImageView imageView;

        imageView = (ImageView) findViewById(R.id.ivPictureFull);
        if (imageView == null) {
            return;
        }

        String imageUrl = mMemberData.getImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = mMemberData.getThumbnailUrl();
        }
        //Log.e(mTag, "imageUrl: " + imageUrl);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            mPbPictureLoading.setVisibility(View.VISIBLE);

            Picasso.with(mContext).load(imageUrl).into(imageView, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    mPbPictureLoading.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    Log.e(mTag, "Picasso.ERROR...");
                    mPbPictureLoading.setVisibility(View.GONE);
                }
            });

            // Glide는 AKB48 쪽 사진이 깨진다.
            /*imageView.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(imageUrl).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    mPbPictureLoading.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    mPbPictureLoading.setVisibility(View.GONE);
                    return false;
                }
            }).into(imageView);*/

            /*
            Glide.with(mContext).load(imageUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                    mPbPictureLoading.setVisibility(View.GONE);
                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(View.VISIBLE);
                    //if (cacheUri == null) {
                    //    ImageUtil.saveBitmapToPng(bitmap, cacheId); // 캐쉬 저장
                    //}
                    //renderProfileDetail();
                }
            });*/
        }
    }

    private void renderMenu() {
        //Log.e(mTag, ">>>>> renderMenu()");

        ArrayList<MenuData> menuDataList = new ArrayList<>();

        MemberDetailMenu memberViewMenu = new MemberDetailMenu(mContext);
        memberViewMenu.load(mGroupData, mMemberData, menuDataList);

        if (menuDataList.size() > 0) {
            LinearLayout loMenu = (LinearLayout) findViewById(R.id.loMenu);
            if (loMenu != null) {
                loMenu.setVisibility(View.VISIBLE);
                MemberDetailMenuAdapter listAdapter = new MemberDetailMenuAdapter(mContext, menuDataList);
                ExpandableHeightListView listView = (ExpandableHeightListView) findViewById(R.id.lvMenu);
                if (listView != null) {
                    listView.setExpanded(true);
                    listView.setAdapter(listAdapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            MenuData menuData = (MenuData) parent.getItemAtPosition(position);
                            onMenuItemClick(menuData);
                        }
                    });
                }
            }
        }

        loadYahoo();
        loadPedia48();
    }

    public void onMenuItemClick(MenuData menuData) {

        String url = menuData.getUrl();
        //Log.e(mTag, "url: " + url);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        //startActivity(intent);
        startActivityForResult(intent, 100);

        showToolbarProgressBar();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        /*switch (menuData.getKey()) {
            case Config.SITE_ID_GOOGLE_PLUS:
            case Config.SITE_ID_GOOGLE_IMAGE_SEARCH:
            case Config.SITE_ID_YAHOO_IMAGE_SEARCH:
                //showToolbarProgressBar();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(menuData.getUrl())));
                //goWebView(menuData.getUrl(), menuData.getTitle());
                break;
            case Config.SITE_ID_TWITTER:
                try {
                    //showToolbarProgressBar();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + mMemberData.getTwitterId())));
                } catch (ActivityNotFoundException e) {
                    goWebView(menuData.getUrl(), menuData.getTitle());
                }
                break;
            default:
                goWebView(menuData.getUrl(), menuData.getTitle());
                break;
        }*/
    }

    /*private void goWebView(String url, String title) {
        //Log.e(mTag, "goWebView().url: " + url);
        //mVolleyManager.cancel();

        Intent intent = new Intent(mContext, WebViewActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        startActivityForResult(intent, 100);
        //showToolbarProgressBar();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }*/

    private void loadYahoo() {
        String query = mLocalGroupName + " " + mMemberData.getName().replace(" ", "");
        try {
            query = URLEncoder.encode(query, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = "http://image.search.yahoo.co.jp/search?ei=UTF-8&dim=medium&p=" + query; // dim=large

        requestData(url, Config.USER_AGENT_WEB);
    }

    private void parseYahoo(String url, String response) {
        YahooParser yahooParser = new YahooParser();
        yahooParser.parseImage(response, mYahooList);
        //Log.e(mTag, "mYahooList.size(): " + mYahooList.size());

        renderYahoo(url);
    }

    private void renderYahoo(final String url) {
        mLoYahooLoading.setVisibility(View.GONE);

        if (mYahooList.size() == 0) {
            findViewById(R.id.loYahoo).setVisibility(View.GONE);
        } else {
            LinearLayout loYahooHeader = (LinearLayout) findViewById(R.id.loYahooHeader);
            loYahooHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivityForResult(intent, 100);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

            WebDataAdapter adapter = new WebDataAdapter(mContext, R.layout.member_detail_yahoo_item, 100, mYahooList);
            ExpandableHeightGridView gridView = (ExpandableHeightGridView) findViewById(R.id.gvYahoo);
            gridView.setExpanded(true);
            gridView.setFocusable(false);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*WebData webData = (WebData) parent.getItemAtPosition(position);
                Intent intent = new Intent(mContext, ImageViewActivity.class);
                intent.putExtra("title", webData.getTitle());
                intent.putExtra("url", webData.getUrl());
                intent.putExtra("thumbnailUrl", webData.getThumbnailUrl());
                intent.putExtra("imageUrl", webData.getImageUrl());
                startActivityForResult(intent, 100);

                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //goWebView(webData.getUrl(), mMemberData.getName());*/
                }
            });
        }

        /*mIsYahooLoaded = true;
        if (mIsPedia48Loaded) {
            loadProfile();
            loadNamuwiki();
        }*/
    }

    private void loadPedia48() {
        String url = "http://48pedia.org/" + Util.removeSpace(mMemberData.getName());
        requestData(url, Config.USER_AGENT_WEB);
    }

    private void parsePedia48(String url, String response) {
        Pedia48ProfileParser pedia48ProfileParser = new Pedia48ProfileParser();
        pedia48ProfileParser.parseProfileImage(response, mPedia48List);

        renderPedia48(url);
    }

    private void renderPedia48(final String url) {
        mLoPedia48Loading.setVisibility(View.GONE);

        if (mPedia48List.size() == 0) {
            findViewById(R.id.loPedia48).setVisibility(View.GONE);
        } else {
            LinearLayout loPedia48Header = (LinearLayout) findViewById(R.id.loPedia48Header);
            loPedia48Header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivityForResult(intent, 100);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

            MemberDetailPedia48Adapter adapter = new MemberDetailPedia48Adapter(mContext, mPedia48List);
            ExpandableHeightGridView gridView = (ExpandableHeightGridView) findViewById(R.id.gvPedia48);
            if (gridView != null) {
                gridView.setExpanded(true);
                gridView.setFocusable(false);
                gridView.setAdapter(adapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //MenuData menuData = (MenuData) parent.getItemAtPosition(position);
                        //onMenuItemClick(menuData);
                    }
                });
            }
        }

        /*mIsPedia48Loaded = true;
        if (mIsYahooLoaded) {
            loadProfile();
            loadNamuwiki();
        }*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.i("AgeListActivity", "onActivityResult().resultCode: " + resultCode);

        hideToolbarProgressBar();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
