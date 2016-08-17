package com.summertaker.sakamichiguide.member;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.data.GroupData;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.data.MenuData;
import com.summertaker.sakamichiguide.util.Util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MemberDetailMenu {

    private String mTag = "##### MemberDetailMenu";

    private Context mContext;
    private Resources mResources;

    public MemberDetailMenu(Context context) {
        this.mContext = context;
        this.mResources = context.getResources();
    }

    public void load(GroupData groupData, MemberData memberData, ArrayList<MenuData> menuDataList) {

        MenuData menuData = null;
        String url = "";

        // 공식 프로필 사이트
        String profileUrl = memberData.getMobileUrl();
        if (profileUrl == null || profileUrl.isEmpty()) {
            profileUrl = memberData.getProfileUrl();
        }
        //Log.e(mTag, "profileUrl: " + profileUrl);

        menuData = new MenuData();
        menuData.setId(Config.SITE_ID_OFFICIAL_PROFILE);
        menuData.setTitle(mResources.getString(R.string.official_site_profile));
        menuData.setUrl(memberData.getProfileUrl());
        menuData.setDrawable(R.drawable.ic_female_green_48);
        menuDataList.add(menuData);

        // 48pedia.org
        url = memberData.getPedia48Url();
        if (url != null && !url.isEmpty()) {
            String array[] = url.split("/");
            int lenth = array.length;
            if (lenth > 1) {
                //String id = array[lenth - 1];
                //try {
                //    id = URLDecoder.decode(id, "UTF-8");
                //} catch (UnsupportedEncodingException e) {
                //    e.printStackTrace();
                //}
                menuData = new MenuData();
                menuData.setId(Config.SITE_ID_PEDIA48);
                menuData.setDrawable(R.drawable.ic_wikipedia_48);
                menuData.setTitle(mResources.getString(R.string.pedia48)); // + " (" + id + ")");
                menuData.setUrl(url);
                menuDataList.add(menuData);
            }
        }

        // stage48.net
        url = memberData.getStage48Url();
        //Log.e(mTag, "url: " + url);
        if (url != null && !url.isEmpty()) {
            String array[] = url.split("/");
            int lenth = array.length;
            if (lenth > 1) {
                //String id = array[lenth - 1];
                menuData = new MenuData();
                menuData.setId(Config.SITE_ID_STAGE48);
                menuData.setDrawable(R.drawable.ic_wikipedia_48);
                menuData.setTitle(mResources.getString(R.string.stage48)); // + " (" + id + ")");
                menuData.setUrl(url);
                menuDataList.add(menuData);
            }
        }

        // 블로그
        //if (!memberData.getGroupId().equals(Config.GROUP_ID_KEYAKIZAKA46)) {
            url = memberData.getBlogUrl();
            //Log.e(mTag, "url: " + url);
            if (url != null && !url.isEmpty()) {
                int icon = R.drawable.ic_rss_64;
                if (url.contains("ameblo.jp")) {
                    icon = R.drawable.ic_ameblo_64;
                }
                String title = memberData.getBlogName();
                if (title == null || title.isEmpty()) {
                    title = mResources.getString(R.string.blog);
                }
                menuData = new MenuData();
                menuData.setId(Config.SITE_ID_BLOG);
                menuData.setTitle(title);
                menuData.setUrl(url);
                menuData.setDrawable(icon);
                menuDataList.add(menuData);
            }
        //}

        // 구글+
        url = memberData.getGooglePlusUrl();
        if (url != null && !url.isEmpty()) {
            //Log.e(mTag, "url: " + url);
            menuData = new MenuData();
            menuData.setId(Config.SITE_ID_GOOGLE_PLUS);
            menuData.setTitle(mResources.getString(R.string.google_plus));
            menuData.setUrl(memberData.getGooglePlusUrl());
            menuData.setDrawable(R.drawable.ic_googleplus_64);
            menuDataList.add(menuData);
        }

        // 트위터
        url = memberData.getTwitterUrl();
        if (url != null && !url.isEmpty()) {
            menuData = new MenuData();
            menuData.setId(Config.SITE_ID_TWITTER);
            menuData.setTitle(mResources.getString(R.string.twitter)); // + " (@" + memberData.getTwitterId() + ")");

            try {
                // get the Twitter app if possible
                mContext.getPackageManager().getPackageInfo("com.twitter.android", 0);
                url = "twitter://user?screen_name=" + memberData.getTwitterId();
                //Log.e(mTag, "url: " + url);
            } catch (Exception e) {
                Log.e("===", "ERROR" + e.getMessage());
            }
            menuData.setUrl(url);
            menuData.setDrawable(R.drawable.ic_twitter_64);
            menuDataList.add(menuData);
        }

        // 페이스북
        url = memberData.getFacebookUrl();
        if (url != null && !url.isEmpty()) {
            menuData = new MenuData();
            menuData.setId(Config.SITE_ID_FACEBOOK);
            menuData.setTitle(mResources.getString(R.string.facebook)); // + " (@" + memberData.getFacebookId() + ")");
            menuData.setUrl(url);
            menuData.setDrawable(R.drawable.ic_facebook_64);
            menuDataList.add(menuData);
        }

        // 인스타그램
        url = memberData.getInstagramUrl();
        if (url != null && !url.isEmpty()) {
            menuData = new MenuData();
            menuData.setId(Config.SITE_ID_INSTAGRAM);
            menuData.setTitle(mResources.getString(R.string.instagram)); // + " (@" + memberData.getInstagramId() + ")");
            try {
                // get the Twitter app if possible
                mContext.getPackageManager().getPackageInfo("com.instagram.android", 0);
                url = "http://instagram.com/_u/" + memberData.getInstagramId();
                //Log.e(mTag, "url: " + url);
            } catch (Exception e) {
                //Log.e("===", "ERROR" + e.getMessage());
            }
            menuData.setUrl(url);
            menuData.setDrawable(R.drawable.ic_instagram_64);
            menuDataList.add(menuData);
        }

        // 755
        url = memberData.getNanagogoUrl();
        if (url != null && !url.isEmpty()) {
            //Log.e(mTag, "url: " + url);
            menuData = new MenuData();
            menuData.setId(Config.SITE_ID_NANAGOGO);
            menuData.setTitle(mResources.getString(R.string.nanagogo));
            menuData.setUrl(url);
            menuData.setDrawable(R.drawable.ic_nanagogo_76);
            menuDataList.add(menuData);
        }

        // 웨이보
        url = memberData.getWeiboUrl();
        if (url != null && !url.isEmpty()) {
            menuData = new MenuData();
            menuData.setId(Config.SITE_ID_WEIBO);
            menuData.setTitle(mResources.getString(R.string.weibo));
            menuData.setUrl(url);
            menuData.setDrawable(R.drawable.ic_weibo_64);
            menuDataList.add(menuData);
        }

        // QQ
        url = memberData.getQqUrl();
        if (url != null && !url.isEmpty()) {
            menuData = new MenuData();
            menuData.setId(Config.SITE_ID_QQ);
            menuData.setTitle(mResources.getString(R.string.qq));
            menuData.setUrl(url);
            menuData.setDrawable(R.drawable.ic_qq_64);
            menuDataList.add(menuData);
        }

        // 바이두
        url = memberData.getBaiduUrl();
        if (url != null && !url.isEmpty()) {
            menuData = new MenuData();
            menuData.setId(Config.SITE_ID_BAIDU);
            menuData.setTitle(mResources.getString(R.string.baidu_tieba));
            menuData.setUrl(url);
            menuData.setDrawable(R.drawable.ic_baidu_64);
            menuDataList.add(menuData);
        }

        // 나무위키
        if (Util.getLocaleStrng(mContext).equals("KR")) {
            url = memberData.getNamuwikiUrl();
            if (url != null && !url.isEmpty()) {
                menuData = new MenuData();
                menuData.setId(Config.SITE_ID_NAMUWIKI);
                menuData.setTitle(mResources.getString(R.string.namuwiki));
                menuData.setUrl(url);
                menuData.setDrawable(R.drawable.ic_namuwiki);
                menuDataList.add(menuData);
            }
        }

        String query = groupData.getName() + " " + memberData.getName(); //.replace(" ", "");

        // 구글 이미지 검색
        try {
            query = URLEncoder.encode(query, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        url = "https://www.google.com/search?tbm=isch&q=" + query; // Google Image
        menuData = new MenuData();
        menuData.setId(Config.SITE_ID_GOOGLE_IMAGE_SEARCH);
        menuData.setDrawable(R.drawable.ic_google_64);
        menuData.setTitle(mResources.getString(R.string.google_image_search));
        menuData.setUrl(url);
        menuDataList.add(menuData);

        // 야후 이미지 검색
        /*url = "http://image.search.yahoo.co.jp/search?ei=UTF-8&dim=medium&p=" + query; // dim=large
        menuData = new MenuData();
        menuData.setId(Config.SITE_ID_YAHOO_IMAGE_SEARCH);
        menuData.setDrawable(R.drawable.ic_yahoo_japan_64);
        menuData.setTitle(mResources.getString(R.string.yahoo_image_search));
        menuData.setUrl(url);
        menuDataList.add(menuData);*/
    }
}
