package com.summertaker.sakamichiguide.parser;

import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.data.GroupData;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.data.TeamData;
import com.summertaker.sakamichiguide.util.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;

public class Nmb48Parser extends BaseParser {

    /*
    멤버 전체 목록 + 팀 대표로 표시할 멤버 찾기
     */
    public void parseMemberList(String response, GroupData groupData, ArrayList<MemberData> groupMemberList, ArrayList<TeamData> teamDataList) {
        /*
        <div class="memberList">
            <h3>
                <img src="../images/member/ttl_teamn.jpg" width="194" height="84" alt="チームN" />
            </h3>
            <ul class="team-section clearfix">
                <!-- メンバー -->
                <li class="member-box">
                    <dl>
                        <dt>
                            <a href="akashi_natsuko/">
                                <img src="akashi_natsuko/images/img_photo_small.jpg" alt="" width="141" height="172" oncontextmenu="return false;" onmousemove="return false;" onmousedown="return false;" />
                            </a>
                        </dt>
                        <dd>
                            <h4><a href="akashi_natsuko/">明石　奈津子</a></h4>
                            <p>NATSUKO AKASHI</p>
                        </dd>
                    </dl>
                </li>
                ...
            </ul>
        </div>
        */
        response = clean(response);
        response = Util.getJapaneseString(response, "8859_1");
        Document doc = Jsoup.parse(response);

        for (Element h3 : doc.select("h3")) {

            Element ul = h3.nextElementSibling();

            for (Element row : ul.select("li")) {
                //Log.e(mTag, li.text());

                String teamName = h3.select("img").first().attr("alt");
                String id;
                String name;
                String nameEn;
                String profileUrl;
                String thumbnailUrl;
                String imageUrl;

                Element el = null;

                el = row.select("a").first();
                if (el == null) {
                    continue;
                }
                profileUrl = "http://www.nmb48.com/member/" + el.attr("href").trim(); // http://www.nmb48.com/member/akashi_natsuko/

                String[] array = profileUrl.split("/");
                array = profileUrl.split("/member/");
                if (array.length != 2) {
                    continue;
                }
                //id = "nmb48-" + array[1].replace("/", "").trim();
                //mobileUrl = "http://spn2.nmb48.com/profile/?id=" + array[4];

                el = el.select("img").first();
                if (el == null) {
                    continue;
                }
                thumbnailUrl = "http://www.nmb48.com/member/" + el.attr("src").trim(); // http://www.nmb48.com/member/akashi_natsuko/images/img_photo_small.jpg
                imageUrl = thumbnailUrl.replace("_small.", "_large."); // // http://www.nmb48.com/member/akashi_natsuko/images/img_photo_large.jpg

                el = row.select("h4 > a").first();
                if (el == null) {
                    continue;
                }
                name = el.text().trim();
                name = Util.replaceJapaneseWhiteSpace(name);

                el = row.select("p").first();
                if (el == null) {
                    continue;
                }
                nameEn = el.html().trim().replace("<br/>", "<br>").replace("<br />", "<br>");
                if (nameEn.contains("<br>")) {
                    nameEn = nameEn.split("<br>")[0].trim();
                } else {
                    nameEn = el.text().trim();
                }
                nameEn = Util.ucfirstAll(nameEn);

                id = Util.urlToId(profileUrl);

                //Log.e(mTag, teamName + " / " + id + " / " + nameJa + " / " + nameEn + " / " + profileUrl + " / " + thumbnailUrl);

                MemberData memberData = new MemberData();
                memberData.setGroupId(groupData.getId());
                memberData.setGroupName(groupData.getName());
                memberData.setTeamName(teamName);
                memberData.setId(id);
                memberData.setName(name);
                memberData.setNameEn(nameEn);
                memberData.setNoSpaceName(Util.removeSpace(name));
                memberData.setProfileUrl(profileUrl);
                memberData.setThumbnailUrl(imageUrl);
                memberData.setImageUrl(imageUrl);

                groupMemberList.add(memberData);
            }
        }

        if (teamDataList != null) {
            super.findTeamMemberOne(groupMemberList, teamDataList);
        }
    }

    public HashMap<String, String> parseProfile(String url, String response) {
        /*
        <div id="detail-box">
            <img src="images/img_photo_large.jpg" width="298" height="363" alt="" class="member-photo" oncontextmenu="return false;" onmousemove="return false;" onmousedown="return false;" />
            <div id="detail-data">
                <h3>太田　夢莉</h3>
                <h3 class="ruby">YUURI OTA</h3>
                <!-- DATA -->
                <ul id="detail-list">
                    <li><span>ニックネーム&nbsp;:&nbsp;</span>ゆーり</li>
                    <li><span>生年月日&nbsp;:&nbsp;</span>1999年12月1日</li>
                    <li><span>血液型&nbsp;:&nbsp;</span>AB型</li>
                    <li><span>出身地&nbsp;:&nbsp;</span>奈良県</li>
                    <li><span>身長&nbsp;:&nbsp;</span>160 cm</li>
                    <li><span>好きな食べ物&nbsp;:&nbsp;</span>アサイーボウル</li>
                    <li><span>趣味&nbsp;:&nbsp;</span>ケータイ小説、買い物、美味しい物を食べる</li>
                    <li><span>特技&nbsp;:&nbsp;</span>文字の早打ち、遊ぶ予定を立てるときの計画性</li>
                    <li><span>将来の夢&nbsp;:&nbsp;</span>モデルさん</li>
        */
        response = clean(response);
        response = Util.getJapaneseString(response, "8859_1");
        //Log.i("##### response", response);

        //Log.e(mTag, "url: " + url);
        url = url.replace("/index.html", "/");

        HashMap<String, String> hashMap = new HashMap<>();

        Document doc = Jsoup.parse(response); // http://jsoup.org/

        Element root = doc.getElementById("detail-box");
        if (root == null) {
            return hashMap;
        }

        Element img = root.select("img").first();
        if (img == null) {
            return hashMap;
        }
        String imageUrl = "";
        String src = img.attr("src").trim();
        //Log.e(mTag, "src: " + src);
        imageUrl = url + src;
        hashMap.put("imageUrl", imageUrl);

        Element detail = root.select("#detail-data").first();
        if (detail == null) {
            return hashMap;
        }
        Element ja = detail.select("h3").first();
        if (ja == null) {
            return hashMap;
        }
        String name = ja.text().trim();
        name = name.replace("　", " ");
        hashMap.put("name", name);

        Element en = detail.select(".ruby").first();
        if (en == null) {
            return hashMap;
        }
        String nameEn = en.text().trim();
        nameEn = nameEn.replace("　", " ");
        hashMap.put("nameEn", nameEn);

        //Log.e(mTag, nameJa + " / " + nameEn);

        Element ul = root.select("ul").first();
        if (ul == null) {
            return hashMap;
        }
        String html = "";
        int count = 0;
        for (Element li : ul.select("li")) {
            String text = li.text().trim();
            String[] array = text.split(":");
            if (count > 0) {
                html += "<br>";
            }
            if (array.length == 2) {
                String title = array[0].trim();
                String value = array[1].trim();
                html += title + "：" + value;
            } else {
                html += text;
            }
            count++;
        }
        hashMap.put("html", html);

        // SNS 링크
        Element p = root.select("p.btn").first();
        if (p != null) {
            //Log.e(mTag, p.outerHtml());

            for (Element a : p.select("a")) {
                String href = a.absUrl("href");
                //Log.e(mTag, "href: " + href);

                if (href.contains("plus.google.com")) {
                    hashMap.put(Config.SITE_ID_GOOGLE_PLUS, href);
                } else if (href.contains("ameblo.jp")) { // http://ameblo.jp/nmb48/theme-10030696610.html
                    hashMap.put(Config.SITE_ID_BLOG, href);
                }
            }
        }

        hashMap.put("isOk", "ok");

        return hashMap;
    }
}
