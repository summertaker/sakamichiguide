package com.summertaker.sakamichiguide.parser;

import com.summertaker.sakamichiguide.data.GroupData;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.data.TeamData;
import com.summertaker.sakamichiguide.util.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;

public class Ske48Parser extends BaseParser {

    private String mTag = "##### Ske48Parser";

    /*
    멤버 전체 목록 + 팀 대표로 표시할 멤버 찾기
    */
    public void parseMemberList(String response, GroupData groupData, ArrayList<MemberData> groupMemberList, ArrayList<TeamData> teamDataList) {
        /*
        <span id="s">
            <h3 class="team">チームS</h3>
        </span>
        <ul class="list clearfix">
            <li>
                <dl>
                    <dt><a href="./?id=rion_azuma"><img src="http://sp.ske48.co.jp/img/120x150/rion_azuma.jpg" alt="東李苑" /></a></dt>
                    <dd>
                        <h3><a href="./?id=rion_azuma">東李苑</a></h3>
                        <h3 class="en">RION AZUMA</h3>
                        <ul class="profBtn">
                            <li><a href="./?id=rion_azuma" class="btn profile"><span></span></a></li>
                            <li><a href="../blog/?writer=rion_azuma" class="btn blog"><span></span></a></li>
                            <li class="textPlus"></li>
                        </ul>
                    </dd>
                </dl>
            </li>
            ...
        </ul>
        */
        response = clean(response);
        response = Util.getJapaneseString(response, "8859_1");

        Document doc = Jsoup.parse(response);

        for (Element row : doc.select("h3.team")) {
            //Log.e(mTag, row.html());

            Element parent = row.parent();
            if (parent == null) {
                continue;
            }

            Element ul = parent.nextElementSibling();
            for (int i = 0; i < 10; i++) {
                if (ul == null) {
                    break;
                }
                //Log.e(mTag, ul.tagName());

                if (!ul.tagName().equals("ul")) {
                    break;
                }

                for (Element li : ul.select("li")) {
                    //Log.e(mTag, li.html());

                    //String id;
                    String teamName = row.text();
                    String name;
                    String nameEn;
                    String thumbnailUrl;
                    String imageUrl;
                    String profileUrl;

                    Element dl = li.select("dl").first();
                    if (dl == null) {
                        continue;
                    }
                    //Log.e(mTag, dl.html());

                    Element dt = dl.select("dt").first();
                    if (dt == null) {
                        continue;
                    }
                    //Log.e(mTag, dt.html());

                    Element a = dt.select("a").first();
                    if (a == null) {
                        continue;
                    }
                    //Log.e("...", a.attr("href"));

                    // ./?id=rion_azuma
                    profileUrl = a.attr("href");
                    profileUrl = profileUrl.replace("./", "/");
                    profileUrl = "http://www.ske48.co.jp/profile" + profileUrl;

                    Element img = a.select("img").first();
                    if (img == null) {
                        continue;
                    }
                    thumbnailUrl = img.attr("src");
                    imageUrl = thumbnailUrl.replace("120x150", "300x365");

                    Element dd = dl.select("dd").first();
                    if (dd == null) {
                        continue;
                    }

                    Element h3 = dd.select("h3").first();
                    name = h3.text();
                    name = Util.replaceJapaneseWhiteSpace(name);

                    Element h3en = dd.select("h3.en").first();
                    nameEn = h3en.text();
                    nameEn = Util.ucfirstAll(nameEn);

                    //id = Util.urlToId(profileUrl);

                    //Log.e(mTag, teamName + " / " + nameJa + " / " + nameEn + " / " + thumbnailUrl + " / " + profileUrl);

                    MemberData memberData = new MemberData();
                    //memberData.setGroupId(id);
                    memberData.setGroupId(groupData.getId());
                    memberData.setGroupName(groupData.getName());
                    memberData.setTeamName(teamName);
                    memberData.setName(name);
                    memberData.setNameEn(nameEn);
                    memberData.setNoSpaceName(Util.removeSpace(name));
                    memberData.setThumbnailUrl(thumbnailUrl);
                    memberData.setImageUrl(imageUrl);
                    memberData.setProfileUrl(profileUrl);
                    groupMemberList.add(memberData);
                }

                ul = ul.nextElementSibling();
            }
        }

        if (teamDataList != null) {
            super.findTeamMemberOne(groupMemberList, teamDataList);
        }
    }

    public HashMap<String, String> parseProfile(String response) {
        /*
        <div class="detail">
            <dl class="profile clearfix">
                <dt>
                    <!-- PHOTO -->
                    <img src="http://sp.ske48.co.jp/img/300x365/ryoha_kitagawa.jpg" alt="北川綾巴" oncontextmenu="return false" />
                    <!-- /PHOTO -->
                </dt>
                <dd>
                    <h3>北川綾巴</h3>
                    <h3 class="en">RYOHA KITAGAWA</h3>
                    SKE48チームS / AKB48チーム4兼任<!-- DATA -->
                    <!-- DATA -->
                    <ul>
                        <li>ニックネーム：うは</li>
                        <li>6期生</li>
                        <li>生年月日：1998年10月9日</li>
                        <li>血液型：B型</li>
                        <li>出身地：愛知県</li>
                        <li>身長：160cm</li>
                        <li>キャッチフレーズ：あなたに必要なざい"りょうは?"(りょうは)<br />あなたに必要とされたいな<br />ちょっぴりツンデレ北川綾巴です。</li>
                        <li>趣味：寝ること、食べること、メンバーと喋ること。</li>
                        <li>特技：どこでも寝られること</li>
                        <li>将来の夢：たくさんの人を笑顔にすること。</li>
                        <li>好きな食べ物：アイス、フルーツ</li>
                        <li>好きな言葉：自分の夢にだけは素直でいたいんだ！</li>
                        <li>一言メッセージ：これからもっと努力して成長していきますので応援よろしくお願いします。<br /></li>
                   </ul>
        */
        response = clean(response);
        response = Util.getJapaneseString(response, "8859_1");
        //Log.i("##### response", response);

        HashMap<String, String> hashMap = new HashMap<>();

        Document doc = Jsoup.parse(response); // http://jsoup.org/

        Element root = doc.select(".detail").first();
        if (root == null) {
            return hashMap;
        }

        Element profile = root.select(".profile").first();
        if (profile == null) {
            return hashMap;
        }

        String imageUrl = "";
        Element dl = profile.select("dl").first();
        if (dl == null) {
            return hashMap;
        }
        Element img = dl.select("img").first();
        if (img == null) {
            return hashMap;
        }
        imageUrl = img.attr("src").trim();
        hashMap.put("imageUrl", imageUrl);

        Element dd = profile.select("dd").first();
        if (dd == null) {
            return hashMap;
        }
        Element ja = dd.select("h3").first();
        if (ja == null) {
            return hashMap;
        }
        String name = ja.text().trim();
        hashMap.put("name", name);

        Element en = dd.select(".en").first();
        if (en == null) {
            return hashMap;
        }
        String nameEn = en.text().trim();
        hashMap.put("nameEn", nameEn);

        //Log.e(mTag, nameJa + " / " + nameEn);

        Element ul = profile.select("ul").first();
        if (ul == null) {
            return hashMap;
        }
        String html = "";
        for (Element li : ul.select("li")) {
            String text = li.text().trim();
            String[] array = text.split("：");
            if (array.length == 2) {
                String title = array[0].trim();
                String value = array[1].trim();
                html += title + ": " + value;
            } else {
                html += text;
            }
            html += "<br>";
        }
        html = (html + "<br>").replace("<br><br>", "");

        hashMap.put("html", html);

        hashMap.put("isOk", "ok");

        return hashMap;
    }
}
