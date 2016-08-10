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

public class Snh48Parser extends BaseParser {

    public void parseMemberList(String response, GroupData groupData, ArrayList<MemberData> groupMemberList, ArrayList<TeamData> teamDataList) {
        /*
        <div class="ny_team_s" id="s_team_s">
            <i class="s_i_star"></i> S队-TEAM SII
        </div>
            <div class="ny_tn">
                <div class="member_h">
                    <div class="mh_pop_zx22">
                        <a href="member_detail.php?sid=1"></a>
                    </div>
                    <div class="mh_p"><img src="http://www.snh48.com/images/member/mp_1.jpg" /></div>
                    <div class="mh_w1"><img src="images/member/xz_6.jpg" width="34" height="28" align="absmiddle" /> 陈观慧</div>
                    <div class="mh_w2">Chen GuanHui</div>
                    <div class="mh_w2"></div>
                    <div class="mh_w2">第二届总选举 第<span class="blue_1">22</span>名</div>
                    <div class="mh_l mh_l_s"><a href="member_detail.php?sid=1">成员资料</a></div>
                </div>

                <div class="member_h">
                    <div class="mh_pop_s">
					    <a href="member_detail.php?sid=142"></a>
					</div>
                    <div class="mh_p"><img src="http://www.snh48.com/images/member/mp_142.jpg" /></div>
                    <div class="mh_w1"><img src="images/member/xz_10.jpg" width="34" height="28" align="absmiddle" /> 成珏</div>
                    <div class="mh_w2">Cheng Jue</div>
                    <div class="mh_w2"></div>
                    <div class="mh_w2">&nbsp;</div>
                    <div class="mh_l mh_l_s"><a href="member_detail.php?sid=142">成员资料</a></div>
                </div>
                ...
        */
        //Log.e(mTag, response);
        response = clean(response);

        response = Util.getJapaneseString(response, null);
        //Log.e(mTag, response);

        Document doc = Jsoup.parse(response);

        for (Element section : doc.select("div.ny_tn")) {
            //Log.e(mTag, section.text());

            Element header = section.previousElementSibling();
            //Log.e(mTag, header.text());

            String teamName = header.text();
            if (teamName.contains("-")) {
                teamName = teamName.split("-")[0].trim();
                //Log.e(mTag, "team: " + team);
            }

            for (Element element : section.children()) {
                //Log.e(mTag, element.text());

                String id;
                String name;
                String nameEn;
                String profileUrl;
                String thumbnailUrl;
                String imageUrl;
                //String stage48Url;

                Element el;

                el = element.select("a").first();
                if (el == null) {
                    continue;
                }
                profileUrl = "http://www.snh48.com/" + el.attr("href").trim(); // http://www.snh48.com/jp/member_23.html

                //String[] array = profileUrl.split("member_");
                //id = "snh48-" + array[1].replace(".html", "").trim();

                el = element.select("img").first();
                if (el == null) {
                    continue;
                }
                thumbnailUrl = el.attr("src").trim(); // http://www.snh48.com/images/member/mp_23.jpg
                if (thumbnailUrl.contains("../")) { // ../images/member/4qi/mp_98.jpg
                    thumbnailUrl = "http://www.snh48.com" + thumbnailUrl.replace("../", "/");
                }
                imageUrl = thumbnailUrl.replace("/mp_", "/zp_"); // http://www.snh48.com/images/member/zp_3.jpg

                el = element.select(".mh_w1").first();
                if (el == null) {
                    continue;
                }
                name = el.text().trim();

                Elements els = element.select(".mh_w2");
                if (els == null) {
                    continue;
                }

                el = els.get(0);
                if (el == null) {
                    continue;
                }
                nameEn = el.text().trim();

                //el = els.get(1);
                //if (el == null) {
                //    continue;
                //}
                //nameEn = el.text().trim();
                //Log.e(mTag, "nameEn: " + nameEn);

                //stage48Url = "http://stage48.net/wiki/index.php/" + nameEn.replace(" ", "_");

                id = Util.urlToId(profileUrl);

                //Log.e(mTag, teamName + " / " + nameCn + " / " + nameEn + " / " + profileUrl + " / " + thumbnailUrl);

                MemberData memberData = new MemberData();
                memberData.setId(id);
                //memberData.setGroupId(Config.GROUP_ID_SNH48);
                memberData.setGroupName(groupData.getName());
                memberData.setTeamName(teamName);
                memberData.setName(name);
                memberData.setNameEn(nameEn);
                memberData.setNoSpaceName(Util.removeSpace(name));
                memberData.setProfileUrl(profileUrl);
                memberData.setThumbnailUrl(imageUrl);
                memberData.setImageUrl(imageUrl);
                //memberData.setStage48Url(stage48Url);

                groupMemberList.add(memberData);
            }
        }

        if (teamDataList != null) {
            super.findTeamMemberOne(groupMemberList, teamDataList);
        }
    }

    public HashMap<String, String> parseProfile(String response) {
        response = clean(response);
        response = Util.getJapaneseString(response, null);
        //Log.i(mTag, response);

        HashMap<String, String> hashMap = new HashMap<>();

        Document doc = Jsoup.parse(response); // http://jsoup.org/
        //Log.i(mTag, doc.toString());

        /*
        <div class="mem_p"><img src="images/member/zp_41.jpg" width="300" height="400" /></div>
        */
        // http://www.snh48.com/images/member/zp_41.jpg
        // images/member/zp_41.jpg
        Element img = doc.select("div.mem_p > img").first();
        String imageUrl = "";
        if (img != null) {
            imageUrl = img.attr("src");
        }
        //Log.e(mTag, "imageUrl: " + imageUrl);
        hashMap.put("imageUrl", imageUrl);

        /*
        <div class="ny_tn">
            <div class="mem_p"><img src="images/member/zp_41.jpg" width="300" height="400" /></div>
            <div class="mem_t1">丁紫妍 Ding ZiYan</div>
                <div class="mem_w">
                    <ul>
                        <li class="l1">昵称：</li>
                        <li class="l2">丹丹</li>
                        <li class="l1">个人特长：</li>
                        <li class="l3">播音、朗诵</li>
                        <li class="l1">身高：</li>
                        <li class="l2">160</li>
                        <li class="l1">兴趣爱好：</li>
                        <li class="l3">-</li>
                        <li class="l1">血型：</li>
                        <li class="l2">-</li>
                        <li class="l1">加入时间：</li>
                        <li class="l3">2012-10-14</li>
                        <li class="l1">生日：</li>
                        <li class="l2">10.6</li>
                        <li class="l1">加入所属：</li>
                        <li class="l3">SNH48 一期生</li>
                        <li class="l1">星座：</li>
                        <li class="l2">天秤座</li>
                        <li class="l1">最终所属：</li>
                        <li class="l3">原SNH48成员</li>
                        <li class="l1">出生地：</li>
                        <li class="l2">中国 安徽省</li>
                        <li class="l1">所属公司：</li>
                        <li class="l3">上海久尚演艺经纪有限公司</li>
                        <li class="l1">经历备注：</li>
                        <li class="l4">2012.10.14 加入SNH48一期生<br />2013.04.17 一期生审查通过 认可为SNH48正式一期生</li>
                    </ul>
                </div>
                <div class="mem_ew"></div>
                <div class="mem_t2">你可以通过如下方式与成员亲密互动哦</div>
                <div class="mem_nb"><img src="images/member/member_sina.jpg" width="122" height="36" style="filter:gray;"/><img src="images/member/member_qq_1.jpg" width="122" height="36" style="filter:gray;"/><img src="images/member/member_baidu.jpg" width="122" height="36" style="filter:gray;"/></div>
                <div class="mem_share">
                    <div id="ckepop"> <span class="jiathis_txt"><span class="white">分享到：</span></span> <a class="jiathis_button_tsina"></a> <a class="jiathis_button_tqq"></a> <a class="jiathis_button_tsohu"></a> <a class="jiathis_button_renren"></a> <a class="jiathis_button_kaixin001"></a> <a class="jiathis_button_qzone"></a> <a class="jiathis_button_baidu"></a> <a class="jiathis_button_douban"></a> <a href="http://www.jiathis.com/share" class="jiathis jiathis_txt jtico jtico_jiathis" target="_blank"></a> </div>
                    <script type="text/javascript" src="http://v2.jiathis.com/code/jia.js" charset="utf-8"></script>
                </div>
            </div>
        </div>
        */

        Element root = doc.select(".mem_w").first();
        if (root ==null) {
            return hashMap;
        }

        int count = 0;
        String html = "";
        for (Element li : root.select("li.l1")) {
            String title = li.text();//.replace("：", "").trim();
            Element el = li.nextElementSibling();

            if (count > 0) {
                html += "<br>";
            }
            if (el != null) {
                String value = el.text().trim();
                if (title.equals("経歴")) {
                    value = el.html().trim();
                }
                value = value.replace("　", " ");
                html += title + value;
            } else {
                html += title;
            }
            count++;
        }
        /*
        Elements el1s = root.select("li.l1");
        Elements el2s = root.select("li.l2");
        Elements el3s = root.select("li.l3");
        Elements el4s = root.select("li.l4");

        boolean isOdd = true;
        int el2count = 0;
        int el3count = 0;
        int el4count = 0;

        for (int i = 0; i < el1s.size(); i++) {
            String key = el1s.get(i).text();

            String value = "";
            if (isOdd) {
                if (el2count < el2s.size()) {
                    value = el2s.get(el2count).text();
                    el2count++;
                }
            } else {
                if (el3count < el3s.size()) {
                    value = el3s.get(el3count).text();
                    el3count++;
                }
            }

            if (value.isEmpty()) {
                if (el4count < el4s.size()) {
                    value = el4s.get(el4count).text();
                    el4count++;
                }
            }

            if (!value.isEmpty()) {
                html += "<small>" + key + "</small> " + value + "<br>";
            }
            isOdd = !isOdd;
        }
        */

        hashMap.put("html", html);

        Element div = doc.select("div.mem_nb").first();
        if (div != null) {
            for (Element a : div.select("a")) {
                String href = a.attr("href");
                //Log.e(mTag, "href: " + href);

                if (href.contains("weibo.com")) {
                    hashMap.put(Config.SITE_ID_WEIBO, href);
                } else if (href.contains("qq.com")) {
                    hashMap.put(Config.SITE_ID_QQ, href);
                } else if (href.contains("baidu.com")) {
                    hashMap.put(Config.SITE_ID_BAIDU, href);
                }
            }
        }

        hashMap.put("isOk", "ok");

        return hashMap;
    }
}
