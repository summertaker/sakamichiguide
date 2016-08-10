package com.summertaker.sakamichiguide.parser;

import android.util.Log;

import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.data.GroupData;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.data.TeamData;
import com.summertaker.sakamichiguide.data.WebData;
import com.summertaker.sakamichiguide.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class BaseParser {

    protected String mTag;

    public BaseParser() {
        mTag = "===== " + this.getClass().getSimpleName();
    }

    protected String clean(String response) {
        return response;
    }

    public String getUrl(String groupId) {
        return null;
    }

    public void parse48List(String response, GroupData groupData, ArrayList<MemberData> memberList) {

    }

    public void parse46List(String response, GroupData groupData, ArrayList<MemberData> memberList) {

    }

    public void parseMemberList(String response, GroupData groupData, ArrayList<MemberData> groupMemberList, ArrayList<TeamData> teamDataList, boolean isMobile) {
        switch (groupData.getId()) {
            case Config.GROUP_ID_NOGIZAKA46:
                Nogizaka46Parser nogizaka46Parser = new Nogizaka46Parser();
                //nogizaka46Parser.parseList(response, groupData, groupMemberList, teamDataList);
                nogizaka46Parser.parseMobileMemberList(response, groupData, groupMemberList, teamDataList);
                break;
            case Config.GROUP_ID_KEYAKIZAKA46:
                Keyakizaka46Parser keyakizaka46Parser = new Keyakizaka46Parser();
                keyakizaka46Parser.parseMemberList(response, groupData, groupMemberList, teamDataList);
                break;
        }
    }

    public HashMap<String, String> parseProfile(GroupData groupData, String url, String response, boolean isMobile) {
        HashMap<String, String> hashMap = new HashMap<>();

        switch (groupData.getId()) {
            case Config.GROUP_ID_NOGIZAKA46:
                Nogizaka46Parser nogizaka46Parser = new Nogizaka46Parser();
                if (isMobile) {
                    hashMap = nogizaka46Parser.parseMobileProfile(response);
                } else {
                    hashMap = nogizaka46Parser.parseProfile(response);
                }
                //nogizaka46Parser.parseBlog(response, mBlogDataList);
                //Log.e(mTag, "mBlogDataList.size(): " + mBlogDataList.size());
                break;
            case Config.GROUP_ID_KEYAKIZAKA46:
                Keyakizaka46Parser keyakizaka46Parser = new Keyakizaka46Parser();
                hashMap = keyakizaka46Parser.parseProfile(response);
                //keyakizaka46Parser.parseBlogList(response, mBlogList, false);
                break;
        }

        return hashMap;
    }


    protected void findTeamMemberOne(ArrayList<MemberData> mGroupMemberList, ArrayList<TeamData> teamDataList) {
        // 팀 목록 만들기
        ArrayList<String> teamNameList = new ArrayList<>();
        String name;
        boolean exist;
        for (int i = 0; i < mGroupMemberList.size(); i++) {
            name = mGroupMemberList.get(i).getTeamName();
            exist = false;
            for (int j = 0; j < teamNameList.size(); j++) {
                if (name.equals(teamNameList.get(j))) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                //Log.e(mTag, teamList.get(i));
                teamNameList.add(name);
            }
        }

        // 팀 대표로 표시할 멤버 한명 찾기
        int memberCount = 0;
        //ArrayList<TeamData> teamDataList = new ArrayList<>();
        ArrayList<MemberData> memberList = new ArrayList<>();
        for (String teamName : teamNameList) {
            memberList.clear();
            memberCount = 0;
            for (MemberData data : mGroupMemberList) {
                if (teamName.equals(data.getTeamName())) {
                    memberList.add(data);
                    memberCount++;
                }
            }

            Collections.shuffle(memberList);
            //Log.e(mTag, tempList.get(0).getTeamName() + " / " + tempList.get(0).getNameJa());
            MemberData member = memberList.get(0);
            //mTeamMemberList.add(member);

            TeamData teamData = new TeamData();
            teamData.setGroupId(member.getGroupId());
            teamData.setGroupName(member.getGroupName());
            teamData.setName(teamName);
            teamData.setMemberCount(memberCount);
            teamData.setMemberData(member);
            teamDataList.add(teamData);
        }
    }

    public void parseAmebaRss(String response, ArrayList<WebData> webDataList) {
        //Log.e(mTag, "parseBlogList()...");
        /*
        <?xml version="1.0" encoding="utf-8" ?>
        <rss version="2.0" xmlns:atom="http://www.w3.org/2005/Atom">
            <channel>
                <title>AKB48 Official Blog 〜1830ｍから～ powered by アメブロ　　</title>
                <link>http://ameblo.jp/akihabara48/</link>
                <atom:link href="http://rssblog.ameba.jp/akihabara48/rss20.xml" rel="self" type="application/rss+xml" />
                <atom:link rel="hub" href="http://pubsubhubbub.appspot.com" />
                <description>
                    ファンのみなさまのおかげで「全国区のアイドルグループとして東京ドームでコンサートを開く」とい...
                </description>
                <language>ja</language>
                <item>
                    <title>抽選及び当選通知配信完了のお知らせ</title>
                    <description>
                        <![CDATA[
                            以下の抽選及び当選通知配信が完了しましたので、お知らせ致します。<br /><br />
                            2016/04/19（火）18:30 「ただいま　恋愛中」公演<br />→ AKB48Mobile会員枠、二本柱の会会員枠<br /><br />
                            2016/04/20（水）18:30 「夢を死なせるわけにいかない」公演<br />→ 100発98中権利、100発100中権利<br /><br />
                            ...
                        ]]>
                    </description>
                    <link>http://ameblo.jp/akihabara48/entry-12058365438.html</link>
                    <pubDate>Sat, 16 Apr 2016 22:04:40 +0900</pubDate>
                </item>
                ...
        */

        response = Util.getJapaneseString(response, "8859_1");
        response = clean(response);
        //Log.e(mTag, "response: " + response);

        Document doc = Jsoup.parse(response);

        for (Element item : doc.select("item")) {
            //Log.e(mTag, item.text());

            String id;
            String title;
            String content;
            String url;
            String date;
            String thumbnailUrl = "";
            String imageUrl = "";

            Element el;

            el = item.select("title").first();
            if (el == null) {
                continue;
            }
            title = el.text().trim();

            el = item.select("description").first();
            if (el == null) {
                continue;
            }

            Document description = Jsoup.parse(el.text());
            content = description.text().trim();
            for (Element img : description.select("img")) {
                String src = img.attr("src");
                if (src.contains(".gif")) {
                    continue;
                }

                thumbnailUrl += img.attr("src") + "*";
                //Log.e(mTag, thumbnailUrl);
                //break;
            }

            // http://stackoverflow.com/questions/15991511/cannot-extract-data-from-an-xml
            url = item.select("link").first().nextSibling().toString().trim();

            // 모바일 페이지 URL로 바로 연결
            url = url.replace("http://ameblo.jp/", "http://s.ameblo.jp/");

            if (url.contains("ske48.co.jp/blog/?id=")) {
                // PC용 URL이 모바일 페이지로 Redirect되면서 파라미터를 잃어버려서 모바일 URL로 바로 연결
                // From: http://www.ske48.co.jp/blog/?id=20151109145725256&writer=secretariat
                // To: http://www2.ske48.co.jp/blog/detail/id:20151109145725256
                String[] array = url.split("id=");
                array = array[1].split("&");
                url = "http://www2.ske48.co.jp/blog/detail/id:" + array[0];
            }

            el = item.select("pubDate").first();
            if (el == null) {
                continue;
            }
            date = el.text().trim();

            id = Util.urlToId(url);

            //Log.e(mTag, title + " / " + url + " / " + thumbnailUrl);

            WebData webData = new WebData();
            webData.setId(id);
            webData.setTitle(title);
            webData.setContent(content);
            webData.setUrl(url);
            webData.setDate(date);
            webData.setThumbnailUrl(thumbnailUrl);

            webDataList.add(webData);
        }
    }

    public void parseAmebaList(String response, ArrayList<WebData> webDataList) {
        //Log.e(mTag, "parseBlogList()...");
        /*
        <ul id="imgList" class="clearFix">
            <li class="imgBox">
                <div class="thumBox">
                    <p class="imgTitle">
                        <a href="http://ameblo.jp/nmb48/entry-12145734531.html" class="titLink">太田夢莉 14thシングル</a>
                    </p>
                    <a href="http://ameblo.jp/nmb48/image-12145734531-13608759581.html" class="imgLink">
                        <img src ="http://imgstat.ameba.jp/view/d/215/stat001.ameba.jp/user_images/20160402/01/nmb48/07/3b/j/o0240023913608759581.jpg" width="215" height="215" class="imgItem">
                    </a>
                </div>
            </li>
            ...
        */

        response = clean(response);
        //Log.e(mTag, response.substring(0, 100));

        Document doc = Jsoup.parse(response);
        Element root = doc.getElementById("imgList");

        for (Element row : root.select(".imgBox")) {
            //Log.e(mTag, blog.html());

            String id;
            String title;
            String url;
            String thumbnailUrl;
            String imageUrl;

            Element el;

            Element base = row.select(".thumBox").first();

            el = base.select(".imgTitle").first();
            el = el.select("a").first();
            url = el.attr("href");
            title = el.text();

            el = base.select(".imgLink").first();
            el = el.select("img").first();
            thumbnailUrl = el.attr("src");

            String[] array = thumbnailUrl.split("stat001.ameba.jp");
            //Log.e(mTag, array[0]);
            //Log.e(mTag, array[1]);
            imageUrl = "http://stat001.ameba.jp" + array[1];

            id = Util.urlToId(thumbnailUrl);

            Log.e(mTag, title + " / " + thumbnailUrl + " / " + imageUrl);

            WebData webData = new WebData();
            webData.setId(id);
            webData.setTitle(title);
            webData.setUrl(url);
            webData.setThumbnailUrl(thumbnailUrl);
            webData.setImageUrl(imageUrl);

            webDataList.add(webData);
        }
    }

    public String parseAmebaJson(String response, ArrayList<WebData> webDataList) {
        //Log.e(mTag, "parseAmebaJson()...");
        /*
        Amb.Ameblo.image.Callback({
            "success":true,
            "hasErrors":false,
            "nextUrl":"http://blogimgapi.ameba.jp/image_list/get.jsonp?ameba_id=nmb48&target_ym=201603&limit=20&page=35&sp=false",
            // or "nextUrl":null,
            "imgList":[{
                "imgUrl":"/user_images/20160301/22/nmb48/ad/21/j/o0480064013581406825.jpg",
                "pageUrl":"http://ameblo.jp/nmb48/image-12134614414-13581406825.html",
                "title":"柴田優衣「はじめてのツア…",
                "entryUrl":"http://ameblo.jp/nmb48/entry-12134614414.html"
            },{
                "imgUrl":"/user_images/20160301/22/nmb48/d1/8b/j/o0240018113581390253.jpg",
                "pageUrl":"http://ameblo.jp/nmb48/image-12134607967-13581390253.html",
                "title":"ゆきつん。。teamM",
                "entryUrl":"http://ameblo.jp/nmb48/entry-12134607967.html"
            },
            ...
        */

        response = clean(response);
        response = response.replace("Amb.Ameblo.image.Callback(", "");
        response = response.replace(");", "");
        //Log.e(mTag, response);

        String nextUrl = null;

        try {
            JSONObject jsonObject = new JSONObject(response);
            nextUrl = jsonObject.getString("nextUrl");
            //Log.e(mTag, "nextUrl: " + nextUrl);

            String imgList = jsonObject.getString("nextUrl");
            JSONArray jsonArray = null;
            if (imgList != null && !imgList.equals("null") && !imgList.isEmpty()) {
                jsonArray = jsonObject.getJSONArray("imgList");
            }
            //Log.e(mTag, "jsonArray.toString(): " + jsonArray.toString());

            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    WebData webData = new WebData();

                    webData.setTitle(Util.getString(object, "title"));
                    webData.setUrl(Util.getString(object, "entryUrl"));
                    //webData.setUrl(Util.getString(object, "pageUrl")); // 이미지 페이지

                    String imgUrl = Util.getString(object, "imgUrl");

                    String thumbnailUrl = "http://stat.ameba.jp" + imgUrl;
                    //Log.e(mTag, thumbnailUrl);
                    webData.setThumbnailUrl(thumbnailUrl);

                    String imageUrl = "http://stat001.ameba.jp" + imgUrl;
                    //Log.e(mTag, imageUrl);
                    webData.setImageUrl(imageUrl);

                    webDataList.add(webData);
                    //Log.e(mTag, "- dataList.size(): " + dataList.size());
                }
            }
        } catch (JSONException e) {
            Log.e(mTag, e.getMessage());
        }

        return nextUrl;
    }

    /**
     * 정렬: 날짜 오름차순 (ASC)
     */
    protected static class DateAscCompare implements Comparator<WebData> {
        @Override
        public int compare(WebData arg0, WebData arg1) {
            return arg0.getDate().compareTo(arg1.getDate());
        }
    }

    /**
     * 정렬: 날짜 내림차순 (DESC)
     */
    protected static class DateDescCompare implements Comparator<WebData> {
        @Override
        public int compare(WebData arg0, WebData arg1) {
            return arg1.getDate().compareTo(arg0.getDate());
        }
    }
}
