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

public class Gnz48Parser extends BaseParser {

    public void parseMemberList(String response, GroupData groupData, ArrayList<MemberData> groupMemberList, ArrayList<TeamData> teamDataList) {
        /*
        <div class="team-bt clearfix">
          <img src="/statics/images/gz/g-team.png" height="74" width="85" alt="" />
          <span class="g-team">GNZ48 TEAM G</span>
        </div>
        <div class="team-main clearfix">
          <ul>
              <li>
                  <div class="cy"><img src="/images/member/1/1.jpg" width="160" alt=""></div>
                  <div class="kuang"><a href="member_detail.php?sid=1"><img src="/statics/images/gz/kuang_g.png" width="174" alt=""></a></div>
                  <p class="p01">Chen Ke</p>
                  <h1>陈 珂</h1>
                  <p class="p02">CK、珂导</p>
                  <p class="p03"><i><img src="/statics/images/gz/icon/birthday.png" alt=""></i>&nbsp;&nbsp;&nbsp;08.09</p>
              </li>
        */
        if (response == null || response.isEmpty()) {
            return;
        }
        response = clean(response);
        response = Util.getChineseString(response, null);
        Document doc = Jsoup.parse(response);

        for (Element row : doc.select(".team-main")) {

            Element bt = row.previousElementSibling();
            if (bt == null) {
                continue;
            }
            Element span = bt.select("span").first();
            if (span == null) {
                continue;
            }
            String teamName = span.text();
            teamName = teamName.replace(groupData.getName(), "");
            teamName = teamName.trim();

            Element ul = row.select("ul").first();
            if (ul == null) {
                continue;
            }
            for (Element li : ul.select("li")) {
                String id;
                String name = null;
                String nameEn = null;
                String thumbnailUrl;
                String profileUrl;

                Element el;

                el = li.select(".cy").first();
                if (el == null) {
                    continue;
                }
                el = el.select("img").first();
                if (el == null) {
                    continue;
                }
                thumbnailUrl = el.attr("src");
                thumbnailUrl = "http://www.gnz48.com" + thumbnailUrl;

                el = li.select(".kuang").first();
                if (el == null) {
                    continue;
                }
                el = el.select("a").first();
                if (el == null) {
                    continue;
                }
                profileUrl = el.attr("href");
                profileUrl = "http://www.gnz48.com/member/" + profileUrl;

                el = li.select(".p01").first();
                if (el != null) {
                    nameEn = el.text().trim();
                }

                el = li.select("h1").first();
                if (el != null) {
                    name = el.text().trim();
                }

                id = Util.urlToId(profileUrl);

                //Log.e(mTag, id + " / " + teamName + " / " + nameCn + " / " + nameEn + " / " + thumbnailUrl + " / " + profileUrl);

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
        <div class="list-r-xinxi team-g">
            <img src="/images/member/1/1.jpg"  alt="" class="chenyuan" />
            <div class="bshare-custom" style="position:absolute; top:430px; left: 33px;">
                分享到：
                <a title="分享到QQ空间" class="bshare-qzone"></a>
                <a title="分享到新浪微博" class="bshare-sinaminiblog"></a>
                <a title="分享到人人网" class="bshare-renren"></a>
                <a title="分享到腾讯微博" class="bshare-qqmb"></a>
                <a title="分享到网易微博" class="bshare-neteasemb"></a>
                <a title="更多平台" class="bshare-more bshare-more-icon more-style-addthis"></a>
                <span class="BSHARE_COUNT bshare-share-count">0</span>
            </div>
            <script type="text/javascript" charset="utf-8" src="http://static.bshare.cn/b/buttonLite.js#style=-1&amp;uuid=&amp;pophcol=2&amp;lang=zh"></script>
            <script type="text/javascript" charset="utf-8" src="http://static.bshare.cn/b/bshareC0.js"></script>
            <div class="xinxi">
                <p class="mz">Chen Ke</p>
                <h1 style="color: #595757;">陈 珂</h1>
                <p class="green">CK、珂导</p>
                <p><span>所属公司：</span>广州丝芭文化传媒有限公司</p>
                <p><span>生日：</span>08.09</p>
                <p><span>星座：</span>狮子座</p>
                <p><span>出生地：</span>中国 湖南</p>
                <p><span>身高：</span>166cm</p>
                <p><span>个人特长：</span>舞蹈、尤克里里、运动、跆拳道</p>
                <p><span>兴趣爱好：</span>养生、动漫、运动、收集奇怪的东西</p>
                <p><span>加入所属：</span>SNH48 五期生</p>
                <p><span>最终所属：</span>GNZ48 TEAM G</p>
            </div>
            <img src="/statics/images/gz/g-team.png" alt="" class="lg" />
        </div>
        <div class="list-r-shiji">
            <div class="btn-l"><img src="/statics/images/gz/cy/btn-l.gif" height="31" width="36" alt="" /></div>
            <div class="shiji">
                <ul class="content">
                    <li>
                       <div class="content-main" style="height: 36px;">
                       <p>2015.07.25<br/>加入SNH48五期生</p>
                       <p><i></i></p>
                       </div>
                       <div class="content-xian"></div>
                    </li>
                    <li>
                       <div class="content-main" style="height: 36px;">
                       <p>2015.12.04<br/>加入SNH48TeamXII队（TeamXII）</p>
                       <p><i></i></p>
                       </div>
                       <div class="content-xian"></div>
                    </li>
                    <li>
                       <div class="content-main" style="height: 36px;">
                       <p>2016.04.20<br/>移籍GNZ48TeamG队（TeamG)</p>
                       <p><i></i></p>
                       </div>
                       <div class="content-xian"></div>
                    </li>
                    <li>
                       <div class="content-main" style="height: 36px;">
                       <p><br/></p>
                       <p><i></i></p>
                       </div>
                       <div class="content-xian"></div>
                    </li>
                </ul>
            </div>
            <div class="btn-r"><img src="/statics/images/gz/cy/btn-r.gif" height="31" width="37" alt="" /></div>
        </div>
    </div>
        */
        response = clean(response);
        response = Util.getChineseString(response, null);
        //Log.i("##### response", response);

        HashMap<String, String> hashMap = new HashMap<>();

        Document doc = Jsoup.parse(response); // http://jsoup.org/

        Element root = doc.select(".list-r-xinxi").first();
        if (root == null) {
            return hashMap;
        }

        Element el;

        el = root.select("img").first();
        if (el == null) {
            return hashMap;
        }
        String imageUrl = "http://www.gnz48.com" + el.attr("src");
        hashMap.put("imageUrl", imageUrl);

        Element xinxi = root.select(".xinxi").first();
        if (xinxi == null) {
            return hashMap;
        }

        int count = 0;
        boolean isFirst = true;
        String html = "";
        for (Element p : xinxi.select("p")) {
            if (count > 3) {
                String text = p.text().trim();
                if (!isFirst) {
                    html += "<br>";
                }
                html += text;
                isFirst = false;
            }
            count++;
        }

        root = doc.select(".list-r-shiji").first();
        Element ul = root.select("ul").first();
        if (ul != null) {
            for (Element li : ul.select("li")) {
                Element p = li.select("p").first();
                if (p != null && !p.text().trim().isEmpty()) {
                    String text = p.html();
                    //Log.e(mTag, text);
                    text = text.replace("<br>", "：");
                    html += "<br>" + text;
                }
            }
        }

        hashMap.put("html", html);

        hashMap.put("isOk", "ok");

        return hashMap;
    }
}
