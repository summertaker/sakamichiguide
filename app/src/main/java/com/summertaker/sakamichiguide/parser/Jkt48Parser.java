package com.summertaker.sakamichiguide.parser;

import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.data.GroupData;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.data.TeamData;
import com.summertaker.sakamichiguide.util.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

public class Jkt48Parser extends BaseParser {

    private String mBaseUrl = "http://www.jkt48.com";

    /*
    멤버 전체 목록 + 팀 대표로 표시할 멤버 찾기
     */
    public void parseMemberList(String response, GroupData groupData, ArrayList<MemberData> groupMemberList, ArrayList<TeamData> teamDataList) {
        /*
        <div id="mainContent">
            <a name="gen1"></a>
            <div class="pagetitle">
                <h2>Team J</h2>
            </div>
            <div class="post">
                <div class="profileWrap">
                    <div class="profilepic">
                        <a href="/member/detail/id/3?lang=id"><img src="/profile/ayana_shahab_s.jpg?r=20150906" alt="Ayana Shahab"/></a>
                    </div>
                    <div class="profilename">
                        <a href="/member/detail/id/3?lang=id">Ayana<br>Shahab</a>
                    </div>
                </div><!--/loop-->
        */
        response = clean(response);
        //Log.e(mTag, response);

        Document doc = Jsoup.parse(response);

        Element root = doc.getElementById("mainContent");
        //Log.e(mTag, root.html());

        if (root != null) {
            for (Element section : root.select(".pagetitle")) {

                Element post = section.nextElementSibling();
                //Log.e(mTag, post.html());

                for (Element row : post.select(".profileWrap")) {

                    Element el;

                    String id;
                    String teamName;
                    String name;
                    String thumbnailUrl;
                    String imageUrl;
                    String profileUrl;

                    el = section.select("h2").first();
                    teamName = el.text();
                    //Log.e(mTag, "teamName: " + teamName);

                    el = row.select(".profilepic").first();
                    if (el == null) {
                        continue;
                    }

                    el = el.select("a").first();
                    if (el == null) {
                        continue;
                    }

                    // http://www.jkt48.com/member/detail/id/3?lang=id
                    profileUrl = el.attr("href");
                    profileUrl = "http://www.jkt48.com" + profileUrl;
                    //Log.e(mTag, "profileUrl: " + profileUrl);

                    el = el.select("img").first();
                    if (el == null) {
                        continue;
                    }
                    // http://www.jkt48.com/profile/ayana_shahab_s.jpg?r=20150906
                    // /profile/ayana_shahab_s.jpg?r=20150906
                    thumbnailUrl = el.attr("src");
                    thumbnailUrl = "http://www.jkt48.com" + thumbnailUrl;
                    //Log.e(mTag, "thumbnailUrl: " + thumbnailUrl);

                    imageUrl = thumbnailUrl.replace("_s", "");

                    el = row.select(".profilename").first();
                    if (el == null) {
                        continue;
                    }

                    el = el.select("a").first();
                    if (el == null) {
                        continue;
                    }
                    name = el.text().trim();

                    id = Util.urlToId(profileUrl);

                    //Log.e(mTag, teamName + " / " + nameId + " / " + thumbnailUrl + " / " + profileUrl);

                    MemberData memberData = new MemberData();
                    memberData.setGroupId(groupData.getId());
                    memberData.setGroupName(groupData.getName());
                    memberData.setId(id);
                    memberData.setTeamName(teamName);
                    memberData.setName(name);
                    memberData.setNoSpaceName(Util.removeSpace(name));
                    memberData.setThumbnailUrl(thumbnailUrl);
                    memberData.setImageUrl(imageUrl);
                    memberData.setProfileUrl(profileUrl);
                    groupMemberList.add(memberData);
                }
            }
        }

        if (teamDataList != null) {
            super.findTeamMemberOne(groupMemberList, teamDataList);
        }
    }

    public HashMap<String, String> parseProfile(String response) {
        //Log.e(mTag, response);

        /*
        <div id="bioHolder">
            <div class="bioWrap" itemscope itemtype="http://data-vocabulary.org/Person">
                <div class="photo">
                    <img src="/profile/shinta_naomi.jpg?r=20150906" alt="Shinta Naomi" itemprop="photo" />
                </div>
                <div class="bio">
                    <div class="biodata">
                        <div class="bioleft">Nama</div>
                        <div class="bioright"><span itemprop="name">Shinta Naomi</span></div>
                        <div class="clear"></div>
                    </div>
                    <div class="biodata">
                        <div class="bioleft">Tanggal Lahir</div>
                        <div class="bioright"><time itemprop="birthday" datetime="1994-06-04">4 Juni 1994</time></div>
                        <div class="clear"></div>
                    </div>
                    <div class="biodata">
                        <div class="bioleft">Golongan Darah</div>
                        <div class="bioright"><span itemprop="role">A</span></div>
                        <div class="clear"></div>
                    </div>
                    <div class="biodata">
                        <div class="bioleft">Horoskop</div>
                        <div class="bioright"><span itemprop="org">Gemini</span></div>
                        <div class="clear"></div>
                    </div>
                    <div class="biodata">
                        <div class="bioleft">Tinggi Badan</div>
                        <div class="bioright">154cm</div>
                        <div class="clear"></div>
                    </div>
                    <div class="biodata">
                        <div class="bioleft">Nama Panggilan</div>
                        <div class="bioright"><span itemprop="nickname">Naomi</span></div>
                        <div class="clear"></div>
                    </div>
                </div>
                <div class="clear"></div>
            </div>

            <div class="greybox">
                <div class="videoHolder">
                    <h4>Video</h4>

                    <div class="videoWrap">
                        <iframe width="560" height="315" src="http://www.youtube.com/embed/pFxg9DCkJJo?showinfo=0" frameborder="0" allowfullscreen></iframe>
                    </div><!--loop-->

                    <div class="clear"></div>
                </div>
            </div><!--end #greybox-->

            <div id="twitterprofile">
                <a class="twitter-timeline" href="https://twitter.com/Naomi_JKT48" data-widget-id="353081882323341312">@Naomi_JKT48 からのツイート</a>
                <script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+"://platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script>
            </div><!--end #twitterprofile-->

            <div id="googleprofile">
                <iframe id="widgetFrame" src="/gplus/iframe/id/108592770443662005613" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:620px; height:362px;"></iframe>
            </div><!--end #googleprofile-->

        </div>
        */

        response = clean(response);

        HashMap<String, String> hashMap = new HashMap<String, String>();

        Document doc = Jsoup.parse(response); // http://jsoup.org/

        Element root = doc.getElementById("bioHolder");
        if (root == null) {
            return hashMap;
        }

        Element photo = root.select(".photo").first();
        if (photo == null) {
            return hashMap;
        }
        Element img = photo.select("img").first();
        if (img == null) {
            return hashMap;
        }
        String imageUrl = mBaseUrl + img.attr("src").trim();
        //Log.e(mTag, "imageUrl: " + imageUrl);
        hashMap.put("imageUrl", imageUrl);

        String name = "";
        //String nameJa = "";
        String html = "";

        //Element bio = root.select(".bio").first();

        for (Element el : root.select(".biodata")) {
            //Log.e(mTag, "el.html(): " + el.html());

            Element left = el.select(".bioleft").first();
            Element right = el.select(".bioright").first();
            if (left != null) {
                String title = left.text().trim();
                //Log.e(mTag, "title: " + title);

                if (title.equals("Nama")) {
                    name = right.text().trim();
                    //Log.e(mTag, "nameId: " + nameId);
                    continue;
                }
                if (right != null) {
                    String value = right.text().trim();
                    html += title + ": " + value;
                    //if (title.equals("名前")) {
                    //    nameJa = value;
                    //}
                } else {
                    html += title;
                }
                html += "<br>";
            }
        }
        if (!html.isEmpty()) {
            html = (html + "<br>").replace("<br><br>", "");
            //Log.e(mTag, "html" + html);
        }
        hashMap.put("name", name);
        //hashMap.put("nameJa", nameJa);
        hashMap.put("html", html);

        hashMap.put("isOk", "ok");

        return hashMap;
    }

    public HashMap<String, String> parseOfficialProfileMobile(String url, String response) {
        //Log.i(mTag, response);
        response = clean(response);

        HashMap<String, String> hashMap = new HashMap<String, String>();

        String html = "";
        String src = "";
        String href = "";
        String text = "";

        Document doc = Jsoup.parse(response); // http://jsoup.org/
        /*
        <img src="/profile/ayana_shahab.jpg" alt="アヤナ・シャハブ" class="img-responsive" />
        */
        Element img = doc.select("img.img-responsive").first();
        //Log.i(mTag, img.toString());
        if (img != null) {
            src = mBaseUrl + img.attr("src");
            hashMap.put("imageUrl", src);
        }

        /*
        <table class="table table-striped table-condensed">
            <tr>
                <th>Tanggal Lahir</th>
                <td>3 Juni 1997</td>
            </tr>
            <tr>
                <th>Golongan Darah</th>
                <td>O</td>
            </tr>
            <tr>
                <th>Horoskop</th>
                <td>Gemini</td>
            </tr>
            <tr>
                <th>Tinggi Badan</th>
                <td>154cm</td>
            </tr>
            <tr>
                <th>Nama Panggilan</th>
                <td>Ayana / A-chan</td>
            </tr>
        </table>
        */

        Element root = doc.select("table").first();
        Elements keys = root.select("th");
        Elements values = root.select("td");
        for (int i = 1; i < keys.size(); i++) {
            html += "<small>" + keys.get(i).text() + ": </small> " + values.get(i).text() + "<br>";
        }

        hashMap.put("html", html);

        /*
        <h3>LINKS</h3>
        <div class="list-group">
            <a href="http://mobile.twitter.com/achanJKT48" target="_blank" class="list-group-item"><i class="glyphicon glyphicon-new-window"></i> Twitter</a>
            <a href="https://plus.google.com/106201966404533491693" target="_blank" class="list-group-item"><i class="glyphicon glyphicon-new-window"></i> Google+</a>
        </div>
         */
        for (Element a : doc.select(".list-group > a")) {
            text = a.text().trim().toLowerCase();
            href = a.attr("href").trim();
            //Log.e(mTag, text + " / " + href);
            if (text.contains("google+")) {
                hashMap.put(Config.SITE_ID_GOOGLE_PLUS, href);
            } else if (text.toLowerCase().contains("facebook")) {
                hashMap.put(Config.SITE_ID_FACEBOOK, href);
            } else if (text.toLowerCase().contains("twitter")) {
                hashMap.put(Config.SITE_ID_TWITTER, href);
            } else if (text.contains("blog")) {
                hashMap.put(Config.SITE_ID_BLOG, href);
            }
        }

        hashMap.put("isOk", "ok");

        return hashMap;
    }
}
