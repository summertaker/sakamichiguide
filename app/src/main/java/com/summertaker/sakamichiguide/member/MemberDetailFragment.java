package com.summertaker.sakamichiguide.member;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseApplication;
import com.summertaker.sakamichiguide.common.BaseFragment;
import com.summertaker.sakamichiguide.common.BaseFragmentInterface;
import com.summertaker.sakamichiguide.common.CacheManager;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.data.WebData;
import com.summertaker.sakamichiguide.parser.Akb48Parser;
import com.summertaker.sakamichiguide.parser.Hkt48Parser;
import com.summertaker.sakamichiguide.parser.Jkt48Parser;
import com.summertaker.sakamichiguide.parser.Keyakizaka46Parser;
import com.summertaker.sakamichiguide.parser.Ngt48Parser;
import com.summertaker.sakamichiguide.parser.Nmb48Parser;
import com.summertaker.sakamichiguide.parser.Nogizaka46Parser;
import com.summertaker.sakamichiguide.parser.Ske48Parser;
import com.summertaker.sakamichiguide.util.ImageUtil;
import com.summertaker.sakamichiguide.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemberDetailFragment extends BaseFragment implements BaseFragmentInterface {

    private String mTag = "========== MemberDetailFragment";
    private MemberData mMemberData;

    private ProgressBar mPbLoading;

    private Callback mCallback;

    private CacheManager mCacheManager;

    private ImageView mProfileImageView;
    private TextView mTvName;
    private TextView mTvSubname;
    private TextView mTvInfo;

    private ArrayList<WebData> mBlogList = new ArrayList<>();

    // Container Activity must implement this interface
    public interface Callback {
        void onMemberDeatilAction(String action, MemberData memberData);

        void onError(String message);
    }

    public static MemberDetailFragment newInstance(int position, MemberData memberData) {
        MemberDetailFragment fragment = new MemberDetailFragment();

        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putSerializable("memberData", memberData);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = container.getContext();
        View rootView = inflater.inflate(R.layout.member_detail_fragment, container, false);

        //mPosition = getArguments().getInt("position", 0);
        //mAction = getArguments().getString("action", "");
        mMemberData = (MemberData) getArguments().getSerializable("memberData");

        if (mMemberData != null) {
            mProfileImageView = (ImageView) rootView.findViewById(R.id.ivPictureFull);

            mTvName = (TextView) rootView.findViewById(R.id.tvName);
            mTvSubname = (TextView) rootView.findViewById(R.id.tvSubname);
            mTvInfo = (TextView) rootView.findViewById(R.id.tvInfo);

            //mBtFollow = (Button) findViewById(R.id.btFollow);
            //mBtFollowing = (Button) findViewById(R.id.btFollowing);

            mPbLoading = (ProgressBar) rootView.findViewById(R.id.pbLoading);
            Util.setProgressBarColor(mPbLoading, Config.PROGRESS_BAR_COLOR_NORMAL, null);

            // *** in Fragment...
            // http://stackoverflow.com/questions/11741270/android-sharedpreferences-in-fragment
            SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(Config.USER_PREFERENCE_KEY, Context.MODE_PRIVATE);
            mCacheManager = new CacheManager(sharedPreferences);

            String cacheData = mCacheManager.load(mMemberData.getId());
            if (cacheData == null) {
                String userAgent = Config.USER_AGENT_WEB;
                if (mMemberData.getGroupId().equals(Config.GROUP_ID_NOGIZAKA46)) {
                    userAgent = Config.USER_AGENT_MOBILE;
                }
                requestData(mMemberData.getProfileUrl(), userAgent, mMemberData.getId());
            } else {
                parseData(mMemberData.getProfileUrl(), cacheData);
            }
        }
        return rootView;
    }

    /**
     * 네트워크 데이터 - 가져오기
     */
    private void requestData(final String url, final String userAgent, final String cacheId) {
        //Log.e(mTag, ">>>>> requestData()");

        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(mTag, response);
                mCacheManager.save(cacheId, response); // 캐쉬 저장하기
                parseData(url, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(mTag, "Error: " + error.getMessage());
                error.printStackTrace();
                //mSnackbar.setText(getString(R.string.network_error_occurred));
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

        //showToolbarProgressBar();
        BaseApplication.getInstance().addToRequestQueue(strReq, "string_req");
    }

    /**
     * 네트워크 데이터 - 파싱 분기하기
     */
    private void parseData(String url, String response) {
        //Log.e(mTag, ">>>>> parseList()");
        //Log.e(mTag, "url: " + url);
        //Log.e(mTag, "mMemberData.getProfileUrl(): " + mMemberData.getProfileUrl());

        parseProfile(url, response);
    }

    /**
     * 공식 웹 사이트 데이터 - 파싱하기
     */
    private void parseProfile(String url, String response) {
        //Log.e(mTag, ">>>>> parseProfile()");

        HashMap<String, String> hashMap = new HashMap<>();

        switch (mMemberData.getGroupId()) {
            case Config.GROUP_ID_NOGIZAKA46:
                Nogizaka46Parser nogizaka46Parser = new Nogizaka46Parser();
                //hashMap = nogizaka46Parser.parseProfile(response);
                hashMap = nogizaka46Parser.parseMobileProfile(response);
                //nogizaka46Parser.parseBlog(response, mBlogDataList);
                //Log.e(mTag, "mBlogDataList.size(): " + mBlogDataList.size());
                break;
            case Config.GROUP_ID_KEYAKIZAKA46:
                Keyakizaka46Parser keyakizaka46Parser = new Keyakizaka46Parser();
                hashMap = keyakizaka46Parser.parseProfile(response);
                keyakizaka46Parser.parseBlogList(response, mBlogList);
                break;
        }
        //Log.e(mTag, hashMap.toString());
        updateMemberData(hashMap);
    }

    /**
     * 멤버 데이터 - 필드 업데이트하기
     */
    private void updateMemberData(HashMap<String, String> hashMap) {
        //Log.e(mTag, ">>>>> updateMemberData()");

        String url;

        String nameJa = hashMap.get("nameJa");
        if (mMemberData.getName() == null || mMemberData.getName().isEmpty()) {
            mMemberData.setName(nameJa);
        }

        String furigana = hashMap.get("furigana");
        if (furigana != null && !"null".equals(furigana) && !furigana.isEmpty()) {
            mMemberData.setFurigana(furigana);
        }

        String nameEn = hashMap.get("nameEn");
        if (nameEn != null && !nameEn.isEmpty()) {
            nameEn = Util.ucfirstAll(nameEn);
            mMemberData.setNameEn(nameEn);
        }

        url = hashMap.get("imageUrl");
        if (url != null && !url.isEmpty()) {
            mMemberData.setImageUrl(url);
        }

        url = hashMap.get("blogUrl");
        if (url != null && !url.isEmpty()) {
            mMemberData.setBlogUrl(url);
            mMemberData.setBlogName(hashMap.get("blogName"));
        }

        url = hashMap.get("googlePlusUrl");
        if (url != null && !url.isEmpty()) {
            String[] info = Util.getSnsInfo(url);
            mMemberData.setGooglePlusId(info[0]);
            mMemberData.setGooglePlusUrl(info[1]);
        }

        url = hashMap.get("twitterUrl");
        if (url != null && !url.isEmpty()) {
            String[] info = Util.getSnsInfo(url);
            mMemberData.setTwitterId(info[0]);
            mMemberData.setTwitterUrl(info[1]);
        }

        url = hashMap.get("facebookUrl");
        if (url != null && !url.isEmpty()) {
            String[] info = Util.getSnsInfo(url);
            mMemberData.setFacebookId(info[0]);
            mMemberData.setFacebookUrl(info[1]);
        }

        url = hashMap.get("instagramUrl");
        if (url != null && !url.isEmpty()) {
            String[] info = Util.getSnsInfo(url);
            mMemberData.setInstagramId(info[0]);
            mMemberData.setInstagramUrl(info[1]);
        }

        url = hashMap.get("nanagogoUrl");
        if (url != null && !url.isEmpty()) {
            mMemberData.setNanagogoUrl(url);
        }

        url = hashMap.get("weibo");
        if (url != null && !url.isEmpty()) {
            mMemberData.setWeiboUrl(url);
        }

        url = hashMap.get("qq");
        if (url != null && !url.isEmpty()) {
            mMemberData.setQqUrl(url);
        }

        url = hashMap.get("baidu");
        if (url != null && !url.isEmpty()) {
            mMemberData.setBaiduUrl(url);
        }

        String html = hashMap.get("html");
        if (html != null && !html.isEmpty()) {
            mMemberData.setInfo(html);
        }

        renderProfilePhoto();
    }

    private void renderProfilePhoto() {
        //Log.e(mTag, ">>>>> renderProfilePhoto()");

        String imageUrl = mMemberData.getImageUrl();
        //Log.e(mTag, "imageUrl:" + imageUrl);

        final String cacheId = Util.urlToId(imageUrl);
        final String cacheUri = ImageUtil.getValidCacheUri(cacheId);
        if (cacheUri != null) {
            imageUrl = cacheUri;
        }

        // https://futurestud.io/blog/glide-callbacks-simpletarget-and-viewtarget-for-custom-view-classes
        Glide.with(mContext).load(imageUrl).asBitmap().dontAnimate().diskCacheStrategy(DiskCacheStrategy.RESULT)
                .override(120, 147)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                        mPbLoading.setVisibility(View.GONE);
                        mProfileImageView.setVisibility(View.VISIBLE);

                        if (cacheUri == null) {
                            ImageUtil.saveBitmapToPng(bitmap, cacheId); // 캐쉬 저장
                        }
                    }
                });

        /*ImageLoader.getInstance().displayImage(imageUrl, mProfileImageView, mDisplayImageOptions, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mPbLoading.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);

                if (cacheUri == null) {
                    ImageUtil.saveBitmapToPng(loadedImage, cacheId); // 캐쉬 저장하기
                    //Log.e(mTag, "Image Seved... " + path);
                }
                renderProfileDetail();
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mPbLoading.setVisibility(View.GONE);
                //mSnackbar.setText(getString(R.string.network_error_occurred));
                renderProfileDetail();
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
            }
        });*/
    }

    /**
     * 공식 웹 사이트 데이터 - 출력하기
     */
    private void renderProfileDetail() {
        //Log.e(mTag, ">>>>> renderProfileDetail()");

        mTvName.setText(mMemberData.getName());

        String subName;

        String furigana = mMemberData.getFurigana();
        if (furigana != null && !furigana.isEmpty()) {
            subName = furigana;
        } else {
            subName = "";
        }

        String nameEn = mMemberData.getNameEn();
        if (nameEn != null && !nameEn.isEmpty()) {
            if (!subName.isEmpty()) {
                subName += " / ";
            }
            subName += nameEn;
        }

        if (!subName.isEmpty()) {
            mTvSubname.setVisibility(View.VISIBLE);
            mTvSubname.setText(subName);
        }

        String info = mMemberData.getInfo();
        if (info != null && !info.isEmpty()) {
            mTvInfo.setVisibility(View.VISIBLE);
            mTvInfo.setText(Html.fromHtml(info));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity;
        if (context instanceof Activity) {
            activity = (Activity) context;

            try {
                mCallback = (Callback) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement Listener for Fragment.");
            }
        }
    }

    @Override
    public void refresh(String articleId) {

    }

    @Override
    public boolean canGoBack() {
        return false;
    }

    @Override
    public void goBack() {

    }
}
