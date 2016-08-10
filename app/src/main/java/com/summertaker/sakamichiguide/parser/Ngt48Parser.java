package com.summertaker.sakamichiguide.parser;

import com.summertaker.sakamichiguide.data.GroupData;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.data.TeamData;
import com.summertaker.sakamichiguide.data.WebData;
import com.summertaker.sakamichiguide.util.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Ngt48Parser extends BaseParser {

    /*
    멤버 전체 목록 + 팀 대표로 표시할 멤버 찾기
    */
    public void parseMemberList(String response, GroupData groupData, ArrayList<MemberData> groupMemberList, ArrayList<TeamData> teamDataList) {
        /*
        <h4 style="font-size: 1.4375rem; line-height: 1.4;" data-idx="0" >
            Team NⅢ
        </h4>
        <div class="clearfix profile2_wrapper">
            <div class="profile2">
                <figure>
                    <a href="/ogino_yuka">
                        <span style="" data-idx="2" >
                            <img alt="荻野 由佳" src="/resources/000/170/093/processed_media/170093-ogino_yuka_s.jpg" />
                        </span>
                    </a>
                </figure>
                <div class="profile2_name">荻野 由佳</div>
                <div class="profile2_roman">OGINO YUKA</div>
            </div>
            ...
        </div>
        */
        response = clean(response);
        Document doc = Jsoup.parse(response);

        for (Element tn : doc.select("h4")) {

            Element next = tn.nextElementSibling();
            Element rs = next.select("div").first();

            for (Element row : rs.select("div.profile2")) {
                //Log.e(mTag, li.text());

                String id;
                String teamName = tn.text();
                String name;
                String nameEn;
                String profileUrl;
                String thumbnailUrl;

                Element a = row.select("figure").first().select("a").first();
                if (a == null) {
                    continue;
                }
                profileUrl = a.attr("href");
                profileUrl = "http://ngt48.jp" + profileUrl;

                Element el = a.select("span").first().select("img").first();
                if (el == null) {
                    continue;
                }

                thumbnailUrl = el.attr("src");
                //thumbnailUrl = thumbnailUrl.replace("093", "278").replace("_s.jpg", ".jpg");
                //thumbnailUrl = "http://ngt48.jp" + thumbnailUrl;

                el = row.select("div.profile2_name").first();
                if (el == null) {
                    continue;
                }
                name = el.text().trim();

                el = row.select("div.profile2_roman").first();
                if (el == null) {
                    continue;
                }
                nameEn = el.text().trim();
                if (nameEn.contains("(")) {
                    String[] array = el.text().trim().split("\\(");
                    nameEn = array[0].trim();
                }
                nameEn = Util.ucfirstAll(nameEn);

                id = Util.urlToId(profileUrl);

                //Log.e(mTag, teamName + " / " + " / " + nameEn + " / " + profileUrl + " / " + thumbnailUrl);

                MemberData memberData = new MemberData();
                memberData.setGroupId(groupData.getId());
                memberData.setGroupName(groupData.getName());
                memberData.setId(id);
                memberData.setTeamName(teamName);
                memberData.setName(name);
                memberData.setNameEn(nameEn);
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

    public void parseMobileMemberList(String response, GroupData groupData, ArrayList<MemberData> groupMemberList, ArrayList<TeamData> teamDataList) {
        //Log.e(mTag, response);
        /*
        <section class="profile">
            <a name="100000001"></a>
            <h2 class="h2_border">
                <span class="genericon genericon-edit"></span> Team NⅢ
            </h2>
            <ul class="slick col4">
			    <li>
			        <a href="https://ngt48.com/profile/detail/102">
				        <figure>
				            <img src="https://ngt48.com/ngt48/artist/102/ngt_1454261870826.jpg" class="imgprotect">
				        </figure>
				        <p>荻野 由佳</p>
			        </a>
		        </li>
		        ...
        */
        response = clean(response);
        //response = Util.getJapaneseString(response, "SHIFT-JIS");
        //Log.e(mTag, response);

        Document doc = Jsoup.parse(response);

        for (Element section : doc.select(".profile")) {

            Element h2 = section.select(".h2_border").first();
            if (h2 == null) {
                continue;
            }
            String teamName = h2.text().trim();
            if (teamName.equals("スタッフ")) {
                continue;
            }

            Element ul = section.select(".slick").first();
            if (ul == null) {
                continue;
            }

            for (Element li : ul.select("li")) {
                //Log.e(mTag, li.text());

                String id;
                String name;
                String profileUrl;
                String thumbnailUrl;
                String imageUrl;

                Element el;

                Element a = li.select("a").first();
                if (a == null) {
                    continue;
                }
                profileUrl = a.attr("href");

                Element figure = a.select("figure").first();
                if (figure == null) {
                    continue;
                }
                el = figure.select("img").first();
                if (el == null) {
                    continue;
                }
                thumbnailUrl = el.attr("src");
                imageUrl = thumbnailUrl;

                el = a.select("p").first();
                if (el == null) {
                    continue;
                }
                name = el.text().trim();

                id = Util.urlToId(profileUrl);

                //Log.e(mTag, teamName + " / " + name + " / " + profileUrl + " / " + thumbnailUrl + " / " + imageUrl);

                MemberData memberData = new MemberData();
                memberData.setId(id);
                memberData.setGroupId(groupData.getId());
                memberData.setGroupName(groupData.getName());
                memberData.setTeamName(teamName);
                memberData.setName(name);
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
        HashMap<String, String> hashMap = new HashMap<>();

        /*
        <div class="ngt48-memder">
            <figure class="main-img">
                <img alt="加藤 美南" src="/resources/000/170/212/processed_media/170212-kato_minami.jpg" />
            </figure>
            <div class="prof">
                <h2 style="font-size: 2.3125rem; line-height: 1.4;" data-idx="0" >加藤 美南</h2>
                <p>KATO MINAMI</p>
                <dl>
                    <dt>ニックネーム</dt>
                    <dd>かとみな</dd>
                    <dt>生年月日</dt>
                    <dd>1999/1/15</dd>
                    <dt>出身地</dt>
                    <dd>新潟県</dd>
                    <dt>血液型</dt>
                    <dd>O型</dd>
                    <dt>特技</dt>
                    <dd>アクロバットパフォーマンス（側宙、転宙できます！）</dd>
                    <dt>趣味</dt>
                    <dd>バトントワーリング</dd>
                    <dt>好きな食べ物</dt>
                    <dd>マカロン、プチトマト</dd>
                    <dt>好きな言葉</dt>
                    <dd>敵は自分にあり</dd>
                    <dt>メッセージ</dt>
                    <dd>１人でも多くのファンの方に会いたーい!!</dd>
                </dl>
                ...
            </div>
        */

        response = clean(response);
        Document doc = Jsoup.parse(response); // http://jsoup.org/

        Element root = doc.select(".ngt48-memder").first();
        //Log.e(mTag, "root: " + root.html());

        if (root != null) {
            Element el;

            String imageUrl = "";
            el = root.select("figure").first();
            if (el != null) {
                el = el.select("img").first();
                if (el != null) {
                    imageUrl = el.absUrl("src").trim();
                    //Log.e(mTag, "imageUrl: " + imageUrl);
                }
            }
            hashMap.put("imageUrl", imageUrl);

            el = root.select(".prof").first();
            if (el == null) {
                return hashMap;
            }
            Element h2 = el.select("h2").first();
            if (h2 == null) {
                return hashMap;
            }
            String name = h2.text();
            hashMap.put("name", name);

            Element p = el.select("p").first();
            if (p == null) {
                return hashMap;
            }
            String nameEn = p.text();
            hashMap.put("nameEn", nameEn);

            //Log.e(mTag, name + " / " + nameEn);

            String html = "";
            Element dl = root.select("dl").first();
            if (dl != null) {
                int count = 0;
                for (Element dt : dl.select("dt")) {
                    String title = dt.text().trim();
                    String value = "";
                    Element dd = dt.nextElementSibling();
                    if (dd != null) {
                        value = "：" + dd.text().trim();
                    }
                    if (count > 0) {
                        html += "<br>";
                    }
                    html += title + value;
                    count++;
                }
            }
            hashMap.put("html", html);
        }

        hashMap.put("isOk", "ok");

        return hashMap;
    }

    public HashMap<String, String> parseMobileProfile(String response) {
        //Log.e(mTag, response);
        HashMap<String, String> hashMap = new HashMap<>();

        /*
        <section class="profile_detail">
		    <h2 class="img_reset">
		        <img src="https://ngt48.com/assets/img/title_profile.png" alt="PROFILE">
		    </h2>
		    <figure>
		        <img src="https://ngt48.com/ngt48/artist/105/ngt_1453826707227.jpg" class="imgprotect">
		    </figure>
            <p class="name">加藤 美南 (カトウ ミナミ)</p>
            ...
		    <table class="prof_table">
			<tr>
				<th>ニックネーム</th>
				<td>かとみな</td>
			</tr>
			...
		    </table>
		    ...
        </section>
        */

        response = clean(response);
        Document doc = Jsoup.parse(response); // http://jsoup.org/

        Element root = doc.select("section.profile_detail").first();
        //Log.e(mTag, "root: " + root.html());

        if (root == null) {
            return hashMap;
        }
        Element figure = root.select("figure").first();
        if (figure == null) {
            return hashMap;
        }

        /*Element img = figure.select("img").first();
        if (img == null) {
            return hashMap;
        }
        String imageUrl = img.attr("src").trim();
        //Log.e(mTag, "imageUrl: " + imageUrl);
        hashMap.put("imageUrl", imageUrl);*/

        Element p = root.select(".name").first();
        if (p == null) {
            return hashMap;
        }
        //Log.e(mTag, "name: " + p.text());

        String delimiter = "(";
        String[] array = p.text().split(Pattern.quote(delimiter));
        String name = array[0].trim();
        hashMap.put("name", name);

        //Log.e(mTag, nameJa);

        if (array.length > 1) {
            String furigana = array[1].replace("(", "").replace(")", "").trim();
            hashMap.put("furigana", furigana);
            //Log.e(mTag, "furigana: " + furigana);
        }

        Element table = doc.select("table.prof_table").first();
        String html = "";
        if (table != null) {
            int count = 0;
            for (Element tr : table.select("tr")) {
                String title = tr.select("th").text().trim();
                String value = tr.select("td").text().trim();
                if (count > 0) {
                    html += "<br>";
                }
                html += title + "：" + value;
                count++;
            }
        }
        hashMap.put("html", html);

        hashMap.put("isOk", "ok");

        return hashMap;
    }

    public void parseLineBlogList(String response, ArrayList<WebData> webDataList) {
        //Log.e(mTag, "parseBlogList()...");
        /*
        <section class="section-box">
            <ul class="article-list-outer article-list-basic2013 box box-margin box-padding box-border">
                <li class="article-list box-margin" data-datetime="{postedAt:'Nov 27, 2015 09:42:33'}">
                    <a href="http://lineblog.me/ngt48/archives/48819908.html" class="article-list-link-block" data-ga-clickcount="article_6">
                        <span class="newmark"></span>
                        <div class="article-list-inner box box-border">
                            <p class="article-image">
                                <img src="http://resize.blogsys.jp/2632eeb1ea1537459fb2256be28197d619221245/crop8/600x400/http://line.blogimg.jp/ngt48/imgs/0/3/0325e49b.jpg" width="100%">
                            </p>
                            <h2 class="article-title">来年に向けて今からやれる事。</h2>
                            <div class="article-datetime"><time datetime="2015-11-27 09:42:33">2015/11/27 09:42</time></div>
                            <div class="article-description">
                                みなさま
                                おはようございます
                                昨夜、NHK紅白歌合戦に、AKB48・NMB48・乃木坂46の出場が発表されました。 おめでとう<span class="article-description-ellipsis sub-color">...</span>
                            </div>
                            <div class="article-comment-count sub-color"><span class="article-comment-count-num">47</span></div>
                        </div>
                    </a>
                </li>
                ...
        */

        response = clean(response);
        //Log.e(mTag, response);

        Document doc = Jsoup.parse(response);
        Element root = doc.select(".article-list-outer").first();

        for (Element li : root.select("li")) {
            //Log.e(mTag, blog.html());

            String id;
            String title;
            String date;
            String content;
            String url;
            String thumbnailUrl = null;
            String imageUrl = null;

            Element el;

            el = li.select("a").first();
            if (el == null) {
                continue;
            }
            url = el.attr("href");

            el = li.select(".article-image").first();
            if (el != null) {
                el = el.select("img").first();
                if (el != null) {
                    thumbnailUrl = el.attr("src");
                    //Log.e(mTag, "thumbnailUrl: " + thumbnailUrl);

                    String deli = "http://line.blogimg.jp/";
                    if (thumbnailUrl.contains(deli)) {
                        String[] array = thumbnailUrl.split(deli);
                        imageUrl = deli + array[1];
                    }
                }
            }

            el = li.select(".article-title").first();
            if (el == null) {
                continue;
            }
            title = el.text();

            el = li.select(".article-datetime").first();
            if (el == null) {
                continue;
            }
            date = el.text();

            el = li.select(".article-description").first();
            if (el == null) {
                continue;
            }
            content = el.text();
            //content = Util.removeSpace(content);

            id = Util.urlToId(url);

            //Log.e(mTag, title + " " + thumbnailUrl + " " + imageUrl);

            WebData webData = new WebData();
            webData.setId(id);
            webData.setTitle(title);
            webData.setDate(date);
            webData.setContent(content);
            webData.setUrl(url);
            webData.setThumbnailUrl(thumbnailUrl);
            webData.setImageUrl(imageUrl);

            webDataList.add(webData);
        }
    }

    public void parseMemberBlogList(String response, ArrayList<WebData> webDataList) {
        //Log.e(mTag, "parseBlogList()...");
        /*
        <section class="blog">
            <h2 class="h2_border">
		        <span class="genericon genericon-heart"></span>
		        人気記事 お気に入りの記事には<span class="genericon genericon-heart"></span>を押してね！
		    </h2>
            <div class="list_b">
                <div class="contents_box ico_new">
                    <a href="https://ngt48.com/photolog/detail/100024469">
                        <figure>
                            <span class="image_bg" style="background-image:url(https://ngt48.com/ngt48/artist_photo_stream/100024469/ngt_1464523137925.jpeg);"></span>
                            <figcaption>
                                <time>2016.05.29</time>
                                <span class="talent">荻野 由佳</span>
                                <p>２回公演ありがとうございました😄😄</p>
                            </figcaption>
                        </figure>
                    </a>
                </div>
        */

        response = clean(response);
        //Log.e(mTag, response);

        Document doc = Jsoup.parse(response);

        for (Element blog : doc.select("section.blog")) {
            //Log.e(mTag, blog.html());

            Element list = blog.select(".list_b").first();
            if (list == null) {
                continue;
            }
            //Log.e(mTag, list.html());

            for (Element row : list.select(".contents_box")) {

                String id;
                String title;
                String name;
                String date;
                String content;
                String url;
                String thumbnailUrl;
                String imageUrl;

                Element el;

                Element base = row.select("a").first();
                url = base.attr("href");

                base = base.select("figure").first();

                el = base.select("span.image_bg").first();
                thumbnailUrl = el.attr("style");
                thumbnailUrl = thumbnailUrl.replace("background-image:url(", "");
                thumbnailUrl = thumbnailUrl.replace(");", "");

                base = base.select("figcaption").first();
                el = base.select("time").first();
                date = el.text();

                el = base.select("span.talent").first();
                name = el.text();

                el = base.select("p").first();
                title = el.text();

                content = "";

                imageUrl = thumbnailUrl;

                id = Util.urlToId(thumbnailUrl);

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
}
