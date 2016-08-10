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

public class Bej48Parser extends BaseParser {

    public void parseMemberList(String response, GroupData groupData, ArrayList<MemberData> groupMemberList, ArrayList<TeamData> teamDataList) {
        /*
        <p class="team_fenge_a" style=" margin-top:20px">TEAM B</p>
        <div class="member_team bdui">
            <dl>
                <dt><a href="/member/b/8.html"></a><img src="/images/b/bej48_b_8.jpg"></dt>
                <dd class="name">陈美君</dd>
                <dd class="name_a">Chen MeiJun</dd>
                <dd class="data"><a href="/member/b/8.html">成员资料</a></dd>
            </dl>
            ...
        */
        if (response == null || response.isEmpty()) {
            return;
        }
        response = clean(response);
        Document doc = Jsoup.parse(response);

        for (Element row : doc.select(".member_team")) {

            Element p = row.previousElementSibling();
            //Log.e(mTag, p.text());
            String teamName = p.text().trim();

            for (Element dl : row.select("dl")) {
                String id;
                String name;
                String nameEn;
                String thumbnailUrl;
                String profileUrl;

                Element el;

                Element dt = dl.select("dt").first();
                if (dt == null) {
                    continue;
                }
                el = dt.select("a").first();
                if (el == null) {
                    continue;
                }
                profileUrl = el.attr("href");
                profileUrl = "http://www.bej48.com" + profileUrl;

                el = dt.select("img").first();
                thumbnailUrl = el.attr("src");
                thumbnailUrl = "http://www.bej48.com" + thumbnailUrl;

                el = dl.select("dd.name").first();
                name = el.text();

                el = dl.select("dd.name_a").first();
                nameEn = el.text();

                id = Util.urlToId(profileUrl);

                //Log.e(mTag, id + " / " + teamName + " / " + nameJa + " / " + nameEn + " / " + thumbnailUrl + " / " + profileUrl);

                MemberData memberData = new MemberData();
                memberData.setId(id);
                memberData.setGroupId(groupData.getId());
                memberData.setGroupName(groupData.getName());
                memberData.setTeamName(teamName);
                memberData.setName(name);
                memberData.setNameEn(nameEn);
                memberData.setNoSpaceName(Util.removeSpace(name));
                memberData.setThumbnailUrl(thumbnailUrl);
                memberData.setProfileUrl(profileUrl);

                groupMemberList.add(memberData);
            }
        }

        if (teamDataList != null) {
            super.findTeamMemberOne(groupMemberList, teamDataList);
        }
    }

    public HashMap<String, String> parseProfile(String response) {
        /*
        <div class="member_third_a">
        <h2>青钰雯 Qing YuWen   |   BEJ48  B队(TEAM B)</h2>
        <div class="member_third_b">
          <div class="member_third_c">
            <img src="/images/b/bej48_b_13.jpg" width="240" height="320" class="biank_e left">
            <div class="member_third_d right">
              <img src="/images/B/duibiaob.png" width="180" height="115" class="duibiaob">
              <div class="member_third_e">
                <dl class="left member_third_ea">
                  <dd><img src="/images/bic/1.png">昵称：鱼丸</dd>
                  <dd><img src="/images/bic/3.png">血型：B</dd>
                  <dd><img src="/images/bic/5.png">星座：摩羯座</dd>
                  <dd><img src="/images/bic/7.png">身高：167cm</dd>
                </dl>
                <dl class="right member_third_eb">
                  <dd><img src="/images/bic/2.png">生日：1月18日</dd>
                  <dd><img src="/images/bic/4.png">出生地：中国 四川南充</dd>
                  <dd><img src="/images/bic/6.png">个人特长：唱歌  耍杂技 羽毛球  走路 煲鸡汤</dd>
                  <dd><img src="/images/bic/8.png">兴趣爱好：健身 唱k 旅游 养生 游乐园</dd>
                </dl>
              </div>
              <!--member_third_e end-->
              <div class="member_third_f">
                <p><a>加入时间：2016-01-18</a><span>最终所属：BEJ48  B队(TEAM B)</span></p>
                <p><a>加入所属：SNH48 六期生</a><span>所属公司：北京丝芭文化传媒有限公司</span></p>
                <p>经历备注：</p>
                <p><p>
	            2016.1.18 加入SNH48六期生&nbsp;
                </p>
                <p>
	              2016.04.20 移籍至BEJ48&nbsp;
                </p>
                <p>
	            2016.04.20 加入BEJ48TeamB
                </p></p>
              </div>
            </div>
            <!--member_third_d end-->
          </div>
          <!--member_third_c end-->
        */
        response = clean(response);
        //Log.i("##### response", response);

        HashMap<String, String> hashMap = new HashMap<>();

        Document doc = Jsoup.parse(response); // http://jsoup.org/

        Element root = doc.select(".member_third_a").first();
        if (root == null) {
            return hashMap;
        }

        Element el;

        el = root.select(".member_third_c").first();
        if (el == null) {
            return hashMap;
        }
        String imageUrl = "";
        Element img = el.select("img").first();
        if (img == null) {
            return hashMap;
        }
        imageUrl = "http://www.bej48.com" + img.attr("src");
        hashMap.put("imageUrl", imageUrl);

        Element e = root.select(".member_third_e").first();
        if (e == null) {
            return hashMap;
        }
        int count = 0;
        String html = "";
        for (Element dd : e.select("dd")) {
            if (count > 0) {
                html += "<br>";
            }
            html += dd.text();
            count++;
        }

        el = root.select(".member_third_f").first();
        if (el != null) {
            for (Element p : el.select("p")) {
                String text = "";
                Element a = p.select("a").first();
                if (a != null) {
                    text = a.text().trim();
                    Element span = p.select("span").first();
                    if (span != null) {
                        text += "<br>" + span.text().trim();
                    }
                } else {
                    text = p.text().trim();
                }
                if (!text.isEmpty()) {
                    html += "<br>" + text;
                }
            }
        }

        hashMap.put("html", html);

        hashMap.put("isOk", "ok");

        return hashMap;
    }
}
