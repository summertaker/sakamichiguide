package com.summertaker.sakamichiguide.parser;

import com.summertaker.sakamichiguide.data.VoteData;
import com.summertaker.sakamichiguide.util.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

public class OriconParser extends BaseParser {

    public void parse7List(String response, int electionCount, ArrayList<VoteData> voteDataList) {
        /*
        <section class="box-rank-review">
            <div class="inner">
                <div class="wrap-text">
                    <p class="list-review" style="margin-top:-4px;margin-bottom:2px;font-size:12px;color:#666;">HKT48チームH</p>
                    <p class="list-review" style="margin-top:5px;margin-bottom:0px;font-size:12px;color:#3171bd;">さしはら りの</p>
                    <p class="title"><a href="http://www.oricon.co.jp/prof/artist/478709/" style="font-size:21px;">指原莉乃</a></p>
                    <p class="list-review" style="margin-top:-4px;margin-bottom:6px;font-size:18px;font-weight:600;">194,049票</p>
                    <p class="list-review" style="margin-bottom:5px;font-size:12px;color:#666;">速報順位：1位(38,151票)</p>
                    <p class="list-review" style="margin-bottom:5px;margin-top:0px;font-size:12px;color:#666;">前回順位：2位(141,954票)</p>
                    <p class="list-review" style="margin-bottom:5px;margin-top:0px;font-size:12px;color:#666;">前々回順位：1位（150,570票）</p>
                    <p class="list-review" style="margin-bottom:10px;margin-top:10px;line-height:16px;">
                        こんなに素敵な景色をもう一度見ることができるとは思っていませんでした。ありがとうございます。
                        普段はHKT48メンバーにお礼をいう機会が少なくて、いつも恥ずかしくて言えませんが、みんなは大事な宝物です。
                        ブスで貧乳でいいところは本当に少ないですが、いいところのない私は、開き直って2年前に1位になることができました。
                        その1位はスキャンダルからの大逆転劇だとか言われましたが、いつか指原莉乃として評価されたいと思っていました。
                        今年は、1年がんばってきたことを評価されての1位だと思っています。全国の落ちこぼれの皆さん、私の1位をどうか自信に変えてください。
                    </p>
                </div>
                <div class="photo-summary">
                    <p class="image">
                        <a href="http://www.oricon.co.jp/photo/1390/105861/" target="_blank">
                            <img src="http://contents.oricon.co.jp/photo/img/1000/1390/detail/img280/1432642705063.jpg" alt="指原莉乃">
                        </a>
                    </p>
                </div>
                <div class="ribbon"></div>
                <p class="num">1</p>
                <div class="crown crown01"></div>
            </div>
        </section>
        ....
        */
        response = clean(response);
        Document doc = Jsoup.parse(response);

        for (Element row : doc.select(".box-rank-review")) {

            String id;
            String team = null;
            String concurrentTeam = null;
            String name = null;
            String noSpaceName = null;
            String furigana = null;
            String rank = null;
            String vote = null;
            String thumbnailUrl = null;

            Element wrapText = row.select(".wrap-text").first();
            int count = 0;
            for (Element p : wrapText.select("p")) {
                switch (count) {
                    case 0:
                        team = p.text().trim();
                        String sep1 = "（";
                        String sep2 = "）";
                        if (!team.contains(sep1)) {
                            sep1 = "\\(";
                            sep2 = ")";
                        }
                        String[] array = team.split(sep1);
                        if (array.length == 2) {
                            team = array[0].trim();
                            concurrentTeam = array[1].replace(sep1, "").replace(sep2, "").replace(")", "").trim();
                            //Log.e(mTag, sep1 + " / " + team + " / " + concurrentTeam);
                        }
                        break;
                    case 1:
                        furigana = p.text().trim();
                        break;
                    case 2:
                        name = p.text().trim();
                        break;
                    case 3:
                        vote = p.text().trim();
                        break;
                    /*case 4:
                        breakingRank = p.text().trim();
                        break;
                    case 5:
                        preRank = p.text().trim();
                        break;
                    case 6:
                        prePreRank = p.text().trim();
                        break;*/
                }
                count++;
            }
            noSpaceName = Util.removeSpace(name);

            if (vote != null) {
                vote = vote.replace("票", "");
            }

            Element img = row.select(".photo-summary").first().select("img").first();
            thumbnailUrl = img.attr("src");

            Element num = row.select(".num").first();
            rank = num.text().trim();
            if (rank.length() == 1) {
                rank = "0" + rank;
            }

            //Log.e(mTag, name + " / " + team + " / " + concurrentTeam + "|");

            VoteData voteData = new VoteData();
            voteData.setElectionCount(electionCount);
            voteData.setTeam(team);
            voteData.setConcurrentTeam(concurrentTeam);
            voteData.setName(name);
            voteData.setFurigana(furigana);
            voteData.setNoSpaceName(noSpaceName);
            voteData.setRank(rank);
            voteData.setVote(vote);
            voteData.setThumbnailUrl(thumbnailUrl);

            voteDataList.add(voteData);
        }
    }

    public void parse6List(String response, int electionCount, ArrayList<VoteData> voteDataList) {
        /*
        <div id="akbmain615">
            <!-- 1 AREA START -->
            <div class="left100">
                <div class="rankakb">
                    <p class="rankakbtext">1位</p>
                </div>
                <p class="nameakbtext">
                    <a href="http://www.oricon.co.jp/prof/artist/478717/">渡辺麻友</a>
                </p>
                <p class="numberakbtext">AKB48チームB<br>159,854票</p>
                <p class="numberakbtext">
                    HKT48チーム H<br>AKB48 チームK兼任）<br>33,545票
                </p>
            </div>
            <div class="akbph">
                <a href="http://www.oricon.co.jp/prof/artist/478717/">
                    <img src="http://contents.oricon.co.jp/music/special/2014/akb48_0607/image/01.jpg" width="82" height="82" alt="" border="0">
                </a>
            </div>
            <div class="commentarea">
                <p class="commenttext">
                    こんなに素晴らしい景色を見たことありません。諦めないでやってきて本当に良かったです。これからは、後輩たちがついて行きたいと思えるような先輩に。そして、先輩の背中を見上げるのではなく、AKB48グループの未来を見上げながら初心を忘れずに、感謝の気持ちを胸に前へと進んでいきたいと思います。
                </p>
                <span class="line332"></span>
                <p class="exrankingtext">
                    <span class="exranking140">
                        第5回順位：3位（101,210票）<br>第4回順位：2位（72,574票）<br>第3回順位：5位（59,118票）
                    </span>
                    <span class="exranking140">
                        第2回順位：5位（20,088票）<br>第1回順位：4位（2,625票）
                    </span>
                    <span class="reset"></span>
                </p>
            </div>
            <div class="eachbtnarea">
                <a href="http://www.oricon.co.jp/prof/artist/478717/"><img src="http://contents.oricon.co.jp/music/special/2014/akb48_0607/image/btn_prof.png" width="62" height="30" alt="Profileページ" border="0" style="margin:9px 0px 0px 4px;"></a><br>
                <a href="http://www.oricon.co.jp/photo/akb48/971/66/"><img src="http://contents.oricon.co.jp/music/special/2014/akb48_0607/image/btn_photo.png" width="62" height="30" alt="写真ページ" border="0" style="margin:5px 0px 0px 4px;"></a><br>
            </div>
            <span class="reset"></span>
            <!-- 1 AREA END -->

            <img src="http://contents.oricon.co.jp/music/special/2014/akb48_0607/image/bline615.gif" width="615" height="15" alt="" border="0"><br>
        ....
        */
        response = clean(response);
        Document doc = Jsoup.parse(response);

        Element root = doc.getElementById("akbmain615");
        if (root == null) {
            return;
        }

        for (Element row : root.select(".left100")) {

            String id;
            String team = null;
            String concurrentTeam = null;
            String name = null;
            String noSpaceName = null;
            String rank = null;
            String vote = null;
            String thumbnailUrl = null;

            Element el;

            el = row.select(".rankakbtext").first();
            rank = el.text().trim();
            rank = rank.replace("位", "");
            if (rank.length() == 1) {
                rank = "0" + rank;
            }

            el = row.select(".nameakbtext").first();
            el = el.select("a").first();
            name = el.text();

            noSpaceName = Util.removeSpace(name);

            el = row.select(".numberakbtext").first();
            String[] array = el.html().split("<br>");
            int len = array.length;
            if (len == 1) {
                vote = array[0];
            } else if (len == 2) {
                team = array[0];
                vote = array[1];
            } else {
                vote = array[len - 1];
                team = el.text();
                team = team.replace(vote, "").trim();
            }
            vote = vote.replace("票", "");

            if (team != null) {
                String sep1 = "（";
                ;
                String sep2 = "）";
                if (!team.contains(sep1)) {
                    sep1 = "\\(";
                    sep2 = ")";
                }
                array = team.split(sep1);
                if (array.length == 2) {
                    team = array[0].trim();
                    concurrentTeam = array[1].replace(sep1, "").replace(sep2, "").trim();
                }
            }

            el = row.nextElementSibling();
            el = el.select("img").first();
            thumbnailUrl = el.attr("src");

            //Log.e(mTag, rank + " / " + name + " / " + vote + " / " + thumbnailUrl);

            VoteData voteData = new VoteData();
            voteData.setElectionCount(electionCount);
            voteData.setTeam(team);
            voteData.setConcurrentTeam(concurrentTeam);
            voteData.setName(name);
            voteData.setNoSpaceName(noSpaceName);
            voteData.setRank(rank);
            voteData.setVote(vote);
            voteData.setThumbnailUrl(thumbnailUrl);

            voteDataList.add(voteData);
        }
    }

    public void parse4List(String response, int electionCount, ArrayList<VoteData> voteDataList) {
        /*
        <div class="tbl" id="t1">
            <table class="nmain" width=100% cellspacing=0 cellpadding=0>
                <tr class="color99">
                    <td class="m" width="15%">順位</td>
                    <td class="m" width="15%">CLICK！<br>写真一覧</td>
                    <td class="m" width="15%">得票数</td>
                    <td class="m" width="25%">名前／プロフィール</td>
                    <td class="m" width="30%">前回</td>
                </tr>
                <tr class="#a9a9a9">
                    <td colspan="5">
                        <p style="padding:6px;text-align:left;font-weight:bold;">
                            <span style="color:#ff4500">↓選抜</span>
                        </p>
                    </td>
                </tr>
                <tr class="color1">
                    <td class="t1">
                        <b><span style="color:#ff4500">第1位</span></b>
                    </td>
                    <td class="t2">
                        <a href="http://www.oricon.co.jp/photo/akb48/230/1/" target="_blank">
                            <img src="http://contents.oricon.co.jp/music/special/2013/akb48_0522/yonkaisenkyo/01.jpg">
                        </a>
                    </td>
                    <td class="t3">
                        108837票
                    </td>
                    <td class="t4">
                        <b><a href="http://www.oricon.co.jp/prof/artist/416562/" target="_blank" style="color:#3366ff">大島優子</a></b>
                    </td>
                    <td class="t5">↑2位</td>
                </tr>
        */
        response = clean(response);
        Document doc = Jsoup.parse(response);

        Element root = doc.getElementById("t1");
        if (root == null) {
            return;
        }
        Element table = root.select(".nmain").first();
        if (table == null) {
            return;
        }

        for (Element row : table.select("tr")) {

            String name = null;
            String noSpaceName = null;
            String rank = null;
            String vote = null;
            String thumbnailUrl = null;

            Element el;

            el = row.select(".t1").first();
            if (el == null) {
                continue;
            }

            rank = el.text();
            rank = rank.replace("第", "");
            rank = rank.replace("位", "");
            if (rank.length() == 1) {
                rank = "0" + rank;
            }

            el = row.select(".t2").first();
            el = el.select("img").first();
            thumbnailUrl = el.attr("src");

            el = row.select(".t3").first();
            vote = el.text();

            el = row.select(".t4").first();
            name = el.text();

            noSpaceName = Util.removeSpace(name);
            vote = vote.replace("票", "");

            //Log.e(mTag, rank + " / " + name + " / " + vote + " / " + thumbnailUrl);

            VoteData voteData = new VoteData();
            voteData.setElectionCount(electionCount);
            voteData.setName(name);
            voteData.setNoSpaceName(noSpaceName);
            voteData.setRank(rank);
            voteData.setVote(vote);
            voteData.setThumbnailUrl(thumbnailUrl);

            voteDataList.add(voteData);
        }
    }
}

