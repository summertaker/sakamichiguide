package com.summertaker.sakamichiguide.member;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.BaseApplication;
import com.summertaker.sakamichiguide.common.BaseFragment;
import com.summertaker.sakamichiguide.common.BaseFragmentInterface;
import com.summertaker.sakamichiguide.common.CacheManager;
import com.summertaker.sakamichiguide.common.ImageViewActivity;
import com.summertaker.sakamichiguide.common.WebDataAdapter;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.data.WebData;
import com.summertaker.sakamichiguide.parser.GoogleImageParser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemberGoogleFragment extends BaseFragment implements BaseFragmentInterface {

    private String mTag = "========== MemberGoogleFragment";

    private MemberData mMemberData;

    private Callback mCallback;

    //private ProgressBar mPbLoading;

    private WebView mWebView;

    private GridView mGridView;

    private CacheManager mCacheManager;

    private ArrayList<WebData> mWebDataList = new ArrayList<>();

    public static MemberGoogleFragment newInstance(int position, MemberData memberData) {
        MemberGoogleFragment fragment = new MemberGoogleFragment();

        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putSerializable("memberData", memberData);
        fragment.setArguments(args);

        return fragment;
    }

    // Container Activity must implement this interface
    public interface Callback {
        void showToolbarLoading();
        void hideToolbarLoading();
        void onError(String message);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = container.getContext();
        View rootView = inflater.inflate(R.layout.member_google_fragment, container, false);

        //mPosition = getArguments().getInt("position", 0);
        //mAction = getArguments().getString("action", "");
        mMemberData = (MemberData) getArguments().getSerializable("memberData");

        if (mMemberData != null) {
            //mPbLoading = (ProgressBar) rootView.findViewById(R.id.pbLoading);
            //Util.setProgressBarColor(mPbLoading, Config.PROGRESS_BAR_COLOR_NORMAL, null);

            String query = mMemberData.getGroupName() + " " + mMemberData.getName().replace(" ", "");
            try {
                query = URLEncoder.encode(query, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String url = "https://www.google.com/search?tbm=isch&q=" + query;
            //Log.e(mTag, url);

            mWebView = (WebView) rootView.findViewById(R.id.webView);
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    mCallback.showToolbarLoading();
                }

                public void onPageFinished(WebView view, String url) {
                    mCallback.hideToolbarLoading();
                }
            });
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);

            mWebView.loadUrl(url);

            /*
            //mGridView = (ExpandableHeightGridView) rootView.findViewById(R.id.gridView);
            mGridView = (GridView) rootView.findViewById(R.id.gridView);

            // *** in Fragment...
            // http://stackoverflow.com/questions/11741270/android-sharedpreferences-in-fragment
            SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(Config.USER_PREFERENCE_KEY, Context.MODE_PRIVATE);
            mCacheManager = new CacheManager(sharedPreferences);

            String cacheId = mMemberData.getGroupId() + "googleimage";
            String cacheData = mCacheManager.load(cacheId);

            //if (cacheData == null) {
                requestData(url, Config.USER_AGENT_WEB, cacheId);
            //} else {
            //    parseList(url, cacheData);
            //}
            */
        }
        return rootView;
    }

    private void requestData(final String url, final String userAgent, final String cacheId) {
        //Log.e(mTag, ">>>>> requestData()");

        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e(mTag, "response: " + response);
                mCacheManager.save(cacheId, response); // 캐쉬 저장하기
                parseData(url, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.e(mTag, "Error: " + error.getMessage());
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

    private void parseData(String url, String response) {
        //Log.e(mTag, ">>>>> parseList()");
        //Log.e(mTag, "url: " + url);

        GoogleImageParser googleImageParser = new GoogleImageParser();
        googleImageParser.parse(response, mWebDataList);

        renderData();
    }

    private void renderData() {
        //Log.e(mTag, ">>>>> renderYahooImageSearch()");

        //mPbLoading.setVisibility(View.GONE);

        WebDataAdapter listAdapter = new WebDataAdapter(mContext, R.layout.member_google_item, 100, mWebDataList);
        mGridView.setVisibility(View.VISIBLE);
        //mGridView.setExpanded(true);
        mGridView.setAdapter(listAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WebData webData = (WebData) parent.getItemAtPosition(position);
                Intent intent = new Intent(mContext, ImageViewActivity.class);
                intent.putExtra("title", webData.getTitle());
                intent.putExtra("url", webData.getUrl());
                intent.putExtra("thumbnailUrl", webData.getThumbnailUrl());
                intent.putExtra("imageUrl", webData.getImageUrl());
                startActivityForResult(intent, 100);

                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //goWebView(webData.getUrl(), mMemberData.getName());
            }
        });
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

