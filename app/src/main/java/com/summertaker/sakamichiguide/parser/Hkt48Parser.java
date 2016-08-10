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

public class Hkt48Parser extends BaseParser {

    public void parseMemberList(String response, GroupData groupData, ArrayList<MemberData> groupMemberList, ArrayList<TeamData> teamDataList) {

        /*
        <h3>Team H</h3>
        <div class="contsbox">
            <div class="profile_list">
                <ul class="cf">
                    <li>
                        <a href="http://www.hkt48.jp/profile/chihiro_anai.html">
                            <img src="http://www.hkt48.jp/profile/images/0001_120.jpg?cache=150511" width="120" height="150" alt="穴井 千尋" />
                            <span class="name_j">穴井 千尋</span>
                            <br />
                            <span class="name_e">CHIHIRO ANAI</span>
                        </a>
                        <br />
                        <div class="team_j">HKT48 Team H キャプテン</div>
                    </li>
                    ...
                </ul>
            </div>
        </div>
        */
        response = clean(response);
        Document doc = Jsoup.parse(response);

        for (Element h3 : doc.select("h3")) {

            Element next = h3.nextElementSibling();
            Element ul = next.select("div > ul").first();

            for (Element row : ul.select("li")) {
                //Log.e(mTag, li.text());

                String teamName = h3.text();
                String id;
                String name;
                String nameEn;
                String profileUrl;
                String thumbnailUrl;
                String imageUrl;

                Element a = row.select("a").first();
                if (a == null) {
                    continue;
                }
                profileUrl = a.attr("href");

                //String[] array = profileUrl.split("/profile/");
                //id = "hkt48-" + array[1].replace(".html", "").trim();

                Element el = a.select("img").first();
                if (el == null) {
                    continue;
                }
                thumbnailUrl = el.attr("src"); // http://www.hkt48.jp/profile/images/0023_120.jpg?cache=150511
                imageUrl = thumbnailUrl.replace("_120.", "_320."); // http://www.hkt48.jp/profile/images/0023_320.jpg?cache=150511

                el = a.select("span.name_j").first();
                if (el == null) {
                    continue;
                }
                name = el.text().trim();
                name = Util.replaceJapaneseWhiteSpace(name);

                el = a.select("span.name_e").first();
                if (el == null) {
                    continue;
                }
                nameEn = el.text().trim();
                nameEn = Util.replaceJapaneseWhiteSpace(nameEn);
                nameEn = Util.ucfirstAll(nameEn);

                id = Util.urlToId(profileUrl);

                //Log.e(mTag, teamName + " / " + id + " / " + nameJa + " / " + nameEn + " / " + profileUrl + " / " + thumbnailUrl);

                MemberData memberData = new MemberData();
                memberData.setId(id);
                memberData.setGroupId(groupData.getId());
                memberData.setGroupName(groupData.getName());
                memberData.setTeamName(teamName);
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

    public void parseMobileMemberList(String response, GroupData groupData, ArrayList<MemberData> groupMemberList, ArrayList<TeamData> teamDataList) {
        //Log.e(mTag, "parseMobileMemberList(): " + response);
        /*
        <article id="main">
            <h1 id="h" class="key">Team H</h1>
            <ul>
                <li>
					<a href="qhkt48_profile?id=23">
						<div class="thumb"><img src="/img/profile/member/23-thumb.jpg?cache=20160521123359" alt="秋吉 優花"></div>
						<div class="info">
							<h2 class="name">秋吉 優花</h2>
							<p class="name-en">YUKA AKIYOSHI</p>
                        </div><!-- End .info -->
                    </a>
				</li>
				...
        */
        response = clean(response);
        Document doc = Jsoup.parse(response);

        Element root = doc.getElementById("main");
        if (root == null) {
            return;
        }

        for (Element h1 : root.select("h1")) {

            String teamName = h1.text();

            Element ul = h1.nextElementSibling();

            for (Element row : ul.select("li")) {
                //Log.e(mTag, li.text());

                String id;
                String name;
                String nameEn;
                String profileUrl;
                String thumbnailUrl;
                String imageUrl;

                Element a = row.select("a").first();
                if (a == null) {
                    continue;
                }
                profileUrl = a.attr("href");
                profileUrl = "http://sp.hkt48.jp/" + profileUrl;

                Element el;

                el = a.select(".thumb").first();
                el = el.select("img").first();
                if (el == null) {
                    continue;
                }
                thumbnailUrl = el.attr("src");

                thumbnailUrl = thumbnailUrl.replace("-thumb.jpg", "-medium.jpg");
                //Log.e(mTag, "thumbnailUrl: " + thumbnailUrl);

                imageUrl = thumbnailUrl.replace("-medium.jpg", "-large.jpg");
                //Log.e(mTag, "imageUrl: " + imageUrl);

                el = a.select(".info").first();
                if (el == null) {
                    continue;
                }
                el = el.select("h2.name").first();
                name = el.text().trim();
                name = Util.replaceJapaneseWhiteSpace(name);

                el = a.select("p.name-en").first();
                if (el == null) {
                    continue;
                }
                nameEn = el.text().trim();
                nameEn = Util.replaceJapaneseWhiteSpace(nameEn);
                nameEn = Util.ucfirstAll(nameEn);

                id = Util.urlToId(profileUrl);

                //Log.e(mTag, teamName + " / " + id + " / " + nameJa + " / " + nameEn + " / " + profileUrl + " / " + thumbnailUrl);

                MemberData memberData = new MemberData();
                memberData.setId(id);
                memberData.setGroupId(groupData.getId());
                memberData.setGroupName(groupData.getName());
                memberData.setTeamName(teamName);
                memberData.setName(name);
                memberData.setNameEn(nameEn);
                memberData.setNoSpaceName(Util.removeSpace(name));
                memberData.setProfileUrl(profileUrl);
                memberData.setThumbnailUrl(thumbnailUrl);
                memberData.setImageUrl(imageUrl);

                groupMemberList.add(memberData);
            }
        }

        if (teamDataList != null) {
            super.findTeamMemberOne(groupMemberList, teamDataList);
        }
    }

    public HashMap<String, String> parseProfile(String response) {
        /*
        <div class="contsbox">
            <div class="profile_parson cf">
                <div class="profile_picts">
                    <img src="http://www.hkt48.jp/profile/images/0004_320.jpg?cache=150511" width="320" height="400" alt="兒玉 遥"  />
                    <!-- / .profile_picts -->
                </div>
                <div class="profile_detail">
                    <p class="name_j">兒玉 遥</p>
                    <p class="name_e">HARUKA KODAMA</p>
                    <p class="team_j">HKT48 Team H / AKB48 Team K 兼任</p>
                    <p class="team">
                        <img src="http://www.hkt48.jp/profile/images/profile_teamH.png" width="38" height="68" border="0" alt="Team H" />
                    </p>
                    <dl>
                        <dt>ニックネーム</dt>
                        <dd>はるっぴ</dd>
                        <dt>生年月日</dt>
                        <dd>1996年9月19日</dd>
                        <dt>血液型</dt>
                        <dd>O</dd>
                        <dt>出身地</dt>
                        <dd>福岡県</dd>
                        <dt>身長</dt>
                        <dd>158cm</dd>
                        <dt>趣味</dt>
                        <dd>映画鑑賞・食べること・ストレッチ</dd>
                        <dt>特技</dt>
                        <dd>毎日楽しむこと</dd>
                        <dt>チャームポイント</dt>
                        <dd>瞳の色</dd>
                    </dl>
                    ...
        */

        //Log.e(mTag, response);
        response = clean(response);

        HashMap<String, String> hashMap = new HashMap<>();

        Document doc = Jsoup.parse(response); // http://jsoup.org/

        Element root = doc.select(".profile_picts").first();
        //Log.e(mTag, "root: " + root.html());

        String imageUrl = "";
        if (root == null) {
            return hashMap;
        }
        Element img = root.select("img").first();
        if (img != null) {
            imageUrl = img.attr("src").trim();
            //Log.e(mTag, "imageUrl: " + imageUrl);
        }
        hashMap.put("imageUrl", imageUrl);

        root = doc.select(".profile_detail").first();
        if (root == null) {
            return hashMap;
        }

        Element el;

        el = root.select(".name_j").first();
        if (el == null) {
            return hashMap;
        }
        String name = el.text().trim();
        hashMap.put("name", name);

        el = root.select(".name_e").first();
        if (el == null) {
            return hashMap;
        }
        String nameEn = el.text().trim();
        hashMap.put("nameEn", nameEn);

        //Log.e(mTag, nameJa + " / " + nameEn);

        String html = "";
        Element dl = root.select("dl").first();
        //Log.e(mTag, "dl.html(): " + dl.html());
        if (dl != null) {
            int count = 0;
            for (Element dt : dl.select("dt")) {
                String title = dt.text().trim();
                Element dd = dt.nextElementSibling();
                if (count > 0) {
                    html += "<br>";
                }
                if (dd != null) {
                    String value = dd.text().trim();
                    html += title + "：" + value;
                } else {
                    html += title;
                }
                count++;
            }
        }
        hashMap.put("html", html);

        Element ul = doc.getElementById("othemedias");
        if (ul == null) {
            return hashMap;
        }
        for (Element li : ul.select("li")) {
            Element a = li.select("a").first();
            if (a != null) {
                String href = a.attr("href").trim();
                //Log.e(mTag, href);
                if (href.contains("plus.google.com")) {
                    hashMap.put(Config.SITE_ID_GOOGLE_PLUS, href);
                } else if (href.contains("twitter.com")) {
                    hashMap.put(Config.SITE_ID_TWITTER, href);
                } else if (href.contains("7gogo.jp")) {
                    hashMap.put(Config.SITE_ID_NANAGOGO, href);
                } else if (href.contains("ameblo.jp")) {
                    hashMap.put(Config.SITE_ID_BLOG, href);
                } else if (href.contains("akb48teamogi.jp")) {
                    hashMap.put(Config.SITE_ID_BLOG, href);
                }
            }
        }

        hashMap.put("isOk", "ok");

        return hashMap;
    }

    public HashMap<String, String> parseMobileProfile(String response) {
        /*
        <article id="main">
            <section class="nav clearfix">
		        <div id="fn">
			        <h1>秋吉 優花</h1>
			        <p class="name-en">YUKA AKIYOSHI</p>
		        </div><!-- End #fn -->
                <div class="tab">
                    <ul>
                        <li>
                            <a href="#contents">コンテンツ</a>
                        </li>
                        <li>
                            <a href="#goods">グッズ</a>
                        </li>
                        <li>
                            <a href="#mail">
                                <img src="/img/profile/mail-icon.png" width="22" alt="メール" style="margin-top:-4px;">
                            </a>
                        </li>
                    </ul>
                </div><!-- End.tab -->
	        </section><!-- .nav -->
        	<div class="detail clearfix">
                <div class="alpha">
                    <p class="figure">
                        <a href="/img/profile/member/23-large.jpg?cache=20160521125709" class="colorbox">
                            <img src="/img/profile/member/23-medium.jpg?cache=20160521125709" alt="HKT48 秋吉 優花">
                        </a>
                    </p>
                    <div class="sns">
                        <a href="https://plus.google.com/104773980789079999630" target="_blank">
                            <img src="/img/profile/gplus-icon.png" alt="Google+">
                        </a>
                        <a href="http://7gogo.jp/akiyoshi-yuka" target="_blank">
                            <img src="/img/profile/755-icon.png" alt="755">
                        </a>
                    </div><!-- End .sns -->
                </div><!-- End .alpha -->
                <div class="info">
                    <p class="team">Team&nbsp;H</p>
                    <dl class="clearfix ">
                        <dt>ニックネーム : </dt>
                        <dd>ゆかちゃん</dd>
                        <dt>生年月日 : </dt>
                        <dd>2000年10月24日</dd>
                        <dt>血液型 : </dt>
                        <dd>B型</dd>
                        <dt>出身地 : </dt>
                        <dd>福岡県</dd>
                        <dt>身長 : </dt>
                        <dd>155cm</dd>
                        <dt>趣味 : </dt>
                        <dd>お菓子作り・習字・温泉に入る・世界の音楽を聴く・食べる・ゲーム</dd>
                        <dt>特技 : </dt>
                        <dd>みかんの名前をたくさん言える・おにぎりの具をたくさん言える・ちょっとタイピングが早い・どこでも寝れる</dd>
                        <dt>チャームポイント : </dt>
                        <dd>目</dd>
                    </dl>
                </div><!-- End .info -->
            </div><!-- End .detail -->
        */

        //Log.e(mTag, response);
        response = clean(response);

        HashMap<String, String> hashMap = new HashMap<>();

        Document doc = Jsoup.parse(response); // http://jsoup.org/

        Element root = doc.getElementById("main");
        //Log.e(mTag, "root: " + root.html());

        Element el = root.select(".nav").first();
        if (el == null) {
            return hashMap;
        }

        Element fn = el.getElementById("fn");
        if (fn == null) {
            return hashMap;
        }
        el = fn.select("h1").first();
        String name = el.text().trim();
        hashMap.put("name", name);

        el = fn.select(".name-en").first();
        String nameEn = el.text().trim();
        hashMap.put("nameEn", nameEn);

        //Log.e(mTag, nameJa + " / " + nameEn);

        Element detail = root.select(".detail").first();

        el = detail.select(".alpha").first();
        el = el.select(".figure").first();
        el = el.select("a").first();
        String imageUrl = el.attr("href");
        hashMap.put("imageUrl", imageUrl);

        Element info = detail.select(".info").first();

        String html = "";
        Element dl = info.select("dl").first();
        //Log.e(mTag, "dl.html(): " + dl.html());
        if (dl != null) {
            int count = 0;
            for (Element dt : dl.select("dt")) {
                String title = dt.text().trim();
                Element dd = dt.nextElementSibling();
                if (count > 0) {
                    html += "<br>";
                }
                if (dd != null) {
                    String value = dd.text().trim();
                    html += title + "：" + value;
                } else {
                    html += title;
                }
                count++;
            }
        }
        hashMap.put("html", html);

        hashMap.put("isOk", "ok");

        return hashMap;
    }
}
