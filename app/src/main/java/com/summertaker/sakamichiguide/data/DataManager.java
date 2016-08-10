package com.summertaker.sakamichiguide.data;

import android.content.Context;
import android.content.res.Resources;

import com.summertaker.sakamichiguide.R;
import com.summertaker.sakamichiguide.common.Config;

import java.util.ArrayList;

public class DataManager {

    private String mTag;
    private Context mContext;
    private Resources mRes;

    public DataManager(Context context) {
        this.mTag = this.getClass().getSimpleName();
        this.mContext = context;
        this.mRes = context.getResources();
        //mSharedPreferences = context.getSharedPreferences(Config.USER_PREFERENCE_KEY, 0);
    }

    public ArrayList<GroupData> getGroupList(String action) {
        ArrayList<GroupData> dataList = new ArrayList<>();

        dataList.add(new GroupData(Config.GROUP_ID_NOGIZAKA46,
                mRes.getString(R.string.nogizaka46),
                R.drawable.logo_nogizaka46,
                "http://www.nogizaka46.com/member/",
                "http://www.nogizaka46.com/smph/member/",
                "http://nogisen.shop-pro.jp/" // "http://suzukazedayori.com/"
        ));
        dataList.add(new GroupData(Config.GROUP_ID_KEYAKIZAKA46,
                mRes.getString(R.string.keyakizaka46),
                R.drawable.logo_keyakizaka46,
                "http://www.keyakizaka46.com/mob/sear/artiLis.php?site=k46o&ima=3848",
                "http://www.keyakizaka46.com/mob/news/diarShw.php?site=k46o&ima=0916&cd=member",
                "http://keyaking.shop-pro.jp/" // "http://suzukazedayori.com/"
        ));

        return dataList;
    }

    public ArrayList<SiteData> getBlogList() {
        ArrayList<SiteData> dataList = new ArrayList<>();

        dataList.add(new SiteData(Config.BLOG_ID_NOGIZAKA46_MEMBER, Config.GROUP_ID_NOGIZAKA46,
                mRes.getString(R.string.nogizaka46),
                R.drawable.logo_nogizaka46,
                "http://blog.nogizaka46.com/",
                "http://blog.nogizaka46.com/smph/",
                null
        ));
        dataList.add(new SiteData(Config.BLOG_ID_KEYAKIZAKA46_MEMBER, Config.GROUP_ID_KEYAKIZAKA46,
                mRes.getString(R.string.keyakizaka46),
                R.drawable.logo_keyakizaka46,
                "http://www.keyakizaka46.com/mob/news/diarShw.php?site=k46o&ima=1948&cd=member",
                null,
                null
        ));

        return dataList;
    }
}
