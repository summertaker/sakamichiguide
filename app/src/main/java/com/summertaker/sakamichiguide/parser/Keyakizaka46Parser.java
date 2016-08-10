package com.summertaker.sakamichiguide.parser;

import android.util.Log;

import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.data.GroupData;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.data.SiteData;
import com.summertaker.sakamichiguide.data.TeamData;
import com.summertaker.sakamichiguide.data.WebData;
import com.summertaker.sakamichiguide.util.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

public class Keyakizaka46Parser extends BaseParser {

    /*
    멤버 전체 목록 + 팀 대표로 표시할 멤버 찾기
    */
    public void parseMemberList(String response, GroupData groupData, ArrayList<MemberData> groupMemberList, ArrayList<TeamData> teamDataList) {
        /*
        <div class="sorted sort-default current">
            <div class="box-member">
                <h3>欅坂46</h3>
                <ul>
                    <div class="box-member">
                        <ul>
                            <li data-member="01">
                                <a href="/mob/arti/artiShw.php?site=k46o&ima=4118&cd=01">
                                    <img src="/img/14/orig/k46o/201/602/201602-01__400_320_102400_jpg.jpg" />
                                    <p class="name">石森 虹花</p>
                                    <p class="furigana">いしもり にじか</p>
                                </a>
                            </li>
                            ...
                        </ul>
                    </div>
                </ul>
            </div>
            ...
        </div>
        */

        response = clean(response);
        Document doc = Jsoup.parse(response);

        Element root = doc.select("div.sort-default").first();
        if (root == null) {
            return;
        }

        for (Element h3 : root.select("h3")) {

            Element ul = h3.nextElementSibling();
            if (ul == null) {
                continue;
            }
            Element div = ul.select("div.box-member").first();
            if (div == null) {
                continue;
            }
            Element rs = div.select("ul").first();
            if (rs == null) {
                continue;
            }

            for (Element row : rs.select("li")) {
                //Log.e(mTag, li.text());

                String id;
                String teamName = h3.text();
                String name;
                String furigana;
                String profileUrl;
                String thumbnailUrl;

                // http://www.keyakizaka46.com/mob/sear/artiLis.php?site=k46o&ima=3848
                // http://www.keyakizaka46.com/mob/arti/artiShw.php?site=k46o&ima=4118&cd=01
                Element a = row.select("a").first();
                if (a == null) {
                    continue;
                }
                profileUrl = a.attr("href");
                profileUrl = "http://www.keyakizaka46.com" + profileUrl;

                Element img = a.select("img").first();
                if (img == null) {
                    continue;
                }
                thumbnailUrl = img.attr("src");
                thumbnailUrl = "http://www.keyakizaka46.com" + thumbnailUrl;

                Element p_name = a.select("p.name").first();
                if (p_name == null) {
                    continue;
                }
                name = p_name.text().trim();

                Element p_furigana = a.select("p.furigana").first();
                if (p_furigana == null) {
                    continue;
                }
                furigana = p_furigana.text().trim();

                id = Util.urlToId(profileUrl);

                //Log.e(mTag, teamName + " / " + nameJa + " / " + furigana + " / " + profileUrl + " / " + thumbnailUrl);

                MemberData memberData = new MemberData();
                memberData.setGroupId(groupData.getId());
                memberData.setGroupName(groupData.getName());
                memberData.setId(id);
                memberData.setTeamName(teamName);
                memberData.setName(name);
                memberData.setFurigana(furigana);
                memberData.setNoSpaceName(Util.removeSpace(name));
                memberData.setProfileUrl(profileUrl);
                memberData.setThumbnailUrl(thumbnailUrl);

                groupMemberList.add(memberData);
            }
        }

        if (teamDataList != null) {
            super.findTeamMemberOne(groupMemberList, teamDataList);
        }
    }

    public HashMap<String, String> parseProfile(String response) {
        //Log.e(mTag, response);
        /*
        <div class="box-profile">
            <div class="box-profile_img">
                <img src="/img/14/orig/k46o/201/602/201602-02__400_320_102400_jpg.jpg" />
            </div>
            <ul class="tag"><li>選抜</li></ul>
        </div>
        <div class="box-profile_text">
            <p class="furigana">
                いまいずみ ゆい
            </p>
            <p class="name">
                今泉 佑唯
            </p>
            <span class="en">
                YUI　IMAIZUMI
            </span>
            <div class="box-info">
                <dl>
                    <dd>
                        生年月日:
                    </dd>
                    <dt>
                        1998年9月30日
                    </dt>
                </dl>
            </div>
        </div>
        ...
        <p class="btn-more">
            <a href="/mob/news/diarKiji.php?site=k46o&ima=2629&cd=member&ct=02">MORE</a>
        </p>
        */

        HashMap<String, String> hashMap = new HashMap<>();

        response = clean(response);
        Document doc = Jsoup.parse(response); // http://jsoup.org/

        Element el;

        el = doc.select("div.box-profile_img").first();
        //Log.e(mTag, "root: " + root.html());
        if (el == null) {
            return hashMap;
        }
        Element img = el.select("img").first();
        if (img == null) {
            return hashMap;
        }
        String imageUrl = "http://www.keyakizaka46.com" + img.attr("src");
        //Log.e(mTag, "imageUrl: " + imageUrl);
        hashMap.put("imageUrl", imageUrl);

        Element root = doc.select(".box-profile_text").first();
        if (root == null) {
            return hashMap;
        }
        el = root.select(".furigana").first();
        if (el == null) {
            return hashMap;
        }
        hashMap.put("furigana", el.text().trim());

        el = root.select(".name").first();
        if (el == null) {
            return hashMap;
        }
        hashMap.put("name", el.text().trim());

        el = root.select(".en").first();
        if (el == null) {
            return hashMap;
        }
        String nameEn = el.text().trim();
        nameEn = nameEn.replace("　", " ");
        nameEn = Util.ucfirstAll(nameEn);
        hashMap.put("nameEn", nameEn);

        el = root.select(".box-info").first();
        if (el == null) {
            return hashMap;
        }
        String html = "";
        int count = 0;
        for (Element dl : el.select("dl")) {
            String title = dl.select("dd").first().text().trim();
            String value = dl.select("dt").first().text().trim();
            if (count > 0) {
                html += "<br>";
            }
            html += title + "：" + value;
            count++;
        }
        hashMap.put("html", html);

        //Log.e(mTag, "#############");
        el = doc.select(".btn-more").first();
        if (el != null) {
            el = el.select("a").first();
            String blogUrl = "http://www.keyakizaka46.com" + el.attr("href");
            //Log.e(mTag, "blogUrl: " + blogUrl);
            hashMap.put(Config.SITE_ID_BLOG, blogUrl);
        }

        hashMap.put("isOk", "ok");

        return hashMap;
    }

    public String parseBlogSiteList(String response, ArrayList<SiteData> dataList) {
        /*
        <div class="box-memberBlog">
            <div class="headArea">
                <h3>
                    メンバー別ブログ
                </h3>
            </div>
            <ul class="thumb">
                <li class="border-06h" data-member="01">
                    <a href="/mob/news/diarKiji.php?site=k46o&ima=1948&cd=member&ct=01">
                        <p>
                            <img src="/img/14/orig/k46o/201/604/201604-01_jpg.jpg" />
                        </p>
                        <p class="name">
                            石森 虹花
                        </p>
                    </a>
                </li>
                ...
            </ul>
        </div>
        */
        response = clean(response);
        //Log.e(mTag, response);

        Document doc = Jsoup.parse(response);

        Element root = doc.select(".box-memberBlog").first();
        if (root == null) {
            return "";
        }

        Element ul = root.select("ul.thumb").first();
        if (ul == null) {
            return "";
        }

        for (Element row : ul.select("li")) {
            String serial = row.attr("data-member");
            if (serial.isEmpty()) {
                continue;
            }
            //Log.e(mTag, "number: " + number);

            Element a = row.select("a").first();
            if (a == null) {
                continue;
            }
            //String href = a.attr("href").trim();

            //http://www.keyakizaka46.com/mob/news/diarKiji.php?site=k46o&ima=1948&cd=member&ct=02
            String url = "http://www.keyakizaka46.com/mob/news/diarKiji.php?site=k46o&cd=member&ct=" + serial; // &ima=1948 파라미터가 변한다.

            Element img = a.select("img").first();
            String src = img.attr("src").trim();

            // http://www.keyakizaka46.com/img/14/orig/k46o/201/604/201604-02_jpg.jpg
            // http://www.keyakizaka46.com/img/14/orig/k46o/201/604/201604-02__400_320_102400_jpg.jpg
            src = src.replace("_jpg.jpg", "__400_320_102400_jpg.jpg");
            String imageUrl = "http://www.keyakizaka46.com" + src;

            Element p = a.select(".name").first();
            String name = p.text().trim();

            //Log.e(mTag, name + " / " + url);

            SiteData data = new SiteData();
            data.setId(Config.BLOG_ID_KEYAKIZAKA46_MEMBER);
            data.setGroupId(Config.GROUP_ID_KEYAKIZAKA46);
            data.setSerial(serial);
            data.setName(name);
            data.setLocaleName(name);
            data.setUrl(url);
            data.setImageUrl(imageUrl);

            dataList.add(data);
        }

        Element script = doc.select("script").last();
        //Log.e(mTag, "script: " + script.text());
        String text = "";
        for (DataNode node : script.dataNodes()) {
            text = node.getWholeData();
        }
        text = text.replace("var blogUpdate = ", "");
        //Log.e(mTag, text);
        return text;
    }

    public void parseBlogList(String response, ArrayList<WebData> webDataList) {
        /*
        <article>
            <div class="innerHead">
                <div class="box-date">
                    <time>2016.4</time><time>01</time>
                </div>
                <div class="box-ttl">
                    <h3>
                        <a href="/mob/news/diarKijiShw.php?site=k46o&ima=4841&id=2349&cd=member">
                            日常のなかにプロレスあり～114～まさにこの通りです。
                        </a>
                    </h3>
                    <p class="name">
                        尾関 梨香
                    </p>
                </div>
                <div class="box-sns"></div>
            </div>
            <div class="box-article">
                <div><br><br>こんばんは〜</div><div><br></div><div><br></div><div><div>4月1日、新年度になりました!</div>
                <img src="/files/14/diary/k46/member/moblog/201604/mob8SaU44.jpg" alt="image1.JPG" id="2E79D24E-D688-4812-A8F1-09B6F552A594">
            </div>
            <div class="box-bottom">
                <ul>
                    <li>2016/04/01 23:40</li>
                    <li class="singlePage">
                        <a href="/mob/news/diarKijiShw.php?site=k46o&ima=4841&id=2349&cd=member">個別ページ</a>
                    </li>
                </ul>
            </div>
        </article>
        <article>
            ....
        </article>
        */

        response = clean(response);
        //Log.e(mTag, response);

        Document doc = Jsoup.parse(response);

        for (Element row : doc.select("article")) {

            String id;
            String title;
            String name;
            String date;
            String content;
            String url;
            String thumbnailUrl = "";
            String imageUrl = "";

            Element el;

            Elements times = row.select("time");
            if (times == null) {
                continue;
            }
            date = times.get(0).text();
            date += "." + times.get(1).text();

            Element ttl = row.select(".box-ttl").first();
            if (ttl == null) {
                continue;
            }

            el = ttl.select("h3").first();
            if (el == null) {
                continue;
            }
            title = el.text();

            el = el.select("a").first();
            url = el.attr("href");
            url = "http://www.keyakizaka46.com" + url;

            el = ttl.select("p").first();
            name = el.text();

            el = row.select(".box-article").first();
            content = el.text().trim();
            content = Util.removeSpace(content);

            //Log.e(mTag, "title: " + title);

            for (Element img : el.select("img")) {
                String src = img.attr("src");
                src = "http://www.keyakizaka46.com" + src;

                thumbnailUrl += src + "*";
                imageUrl += thumbnailUrl + "*";
            }

            id = Util.urlToId(url);

            //Log.e(mTag, title + " " + thumbnailUrl);

            WebData webData = new WebData();
            webData.setId(id);
            webData.setTitle(title);
            webData.setName(name);
            webData.setDate(date);
            webData.setContent(content);
            webData.setUrl(url);
            webData.setThumbnailUrl(thumbnailUrl);
            webData.setImageUrl(imageUrl);

            webDataList.add(webData);
        }
    }
}
