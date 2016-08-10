package com.summertaker.sakamichiguide.member;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

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
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.common.ImageViewActivity;
import com.summertaker.sakamichiguide.common.WebDataAdapter;
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
import com.summertaker.sakamichiguide.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemberBlogFragment extends BaseFragment implements BaseFragmentInterface {

    private String mTag = "========== MemberBlogFragment";

    private MemberData mMemberData;

    private ProgressBar mPbLoading;
    //private ExpandableHeightGridView mGridView;
    private GridView mGridView;

    private CacheManager mCacheManager;
    private String mCacheId;

    private ArrayList<WebData> mWebDataList = new ArrayList<>();

    public static MemberBlogFragment newInstance(int position, MemberData memberData) {
        MemberBlogFragment fragment = new MemberBlogFragment();

        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putSerializable("memberData", memberData);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = container.getContext();
        View rootView = inflater.inflate(R.layout.member_blog_fragment, container, false);

        //mPosition = getArguments().getInt("position", 0);
        //mAction = getArguments().getString("action", "");
        mMemberData = (MemberData) getArguments().getSerializable("memberData");

        if (mMemberData != null) {
            mPbLoading = (ProgressBar) rootView.findViewById(R.id.pbLoading);
            Util.setProgressBarColor(mPbLoading, Config.PROGRESS_BAR_COLOR_NORMAL, null);

            //mGridView = (ExpandableHeightGridView) rootView.findViewById(R.id.gridView);
            mGridView = (GridView) rootView.findViewById(R.id.gridView);

            // *** in Fragment...
            // http://stackoverflow.com/questions/11741270/android-sharedpreferences-in-fragment
            SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(Config.USER_PREFERENCE_KEY, Context.MODE_PRIVATE);
            mCacheManager = new CacheManager(sharedPreferences);
            mCacheId = mMemberData.getId();
            String cacheData = mCacheManager.load(mCacheId);

            //Log.e(mTag, "mCacheId: " + mCacheId);
            //Log.e(mTag, "mCacheId: " + mCacheId);

            if (cacheData == null) {
                String userAgent = Config.USER_AGENT_WEB;
                if (mMemberData.getGroupId().equals(Config.GROUP_ID_NOGIZAKA46)) {
                    userAgent = Config.USER_AGENT_MOBILE;
                }
                requestData(mMemberData.getProfileUrl(), userAgent, mCacheId);
            } else {
                //Log.e(mTag, cacheData);
                parseData(mMemberData.getProfileUrl(), cacheData);
            }
        }
        return rootView;
    }

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
                //VolleyLog.d(mTag, "Error: " + error.getMessage());
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

        BaseApplication.getInstance().addToRequestQueue(strReq, "string_req");
    }

    private void parseData(String url, String response) {
        switch (mMemberData.getGroupId()) {
            case Config.GROUP_ID_NOGIZAKA46:
                Nogizaka46Parser nogizaka46Parser = new Nogizaka46Parser();
                break;
            case Config.GROUP_ID_KEYAKIZAKA46:
                Keyakizaka46Parser keyakizaka46Parser = new Keyakizaka46Parser();
                keyakizaka46Parser.parseBlogList(response, mWebDataList);
                break;
        }

        renderData();
    }

    private void renderData() {
        mPbLoading.setVisibility(View.GONE);

        WebDataAdapter listAdapter = new WebDataAdapter(mContext, R.layout.member_blog_item, 100, mWebDataList);
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
