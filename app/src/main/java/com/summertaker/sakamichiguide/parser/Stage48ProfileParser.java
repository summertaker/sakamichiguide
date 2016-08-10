package com.summertaker.sakamichiguide.parser;

import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.data.WebData;
import com.summertaker.sakamichiguide.util.Util;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Stage48ProfileParser extends BaseParser {

    String mBaseUrl = "http://stage48.net";

    public void parseProfile(Document doc, MemberData memberData) {
        //Log.e(mTag, "parseStage48Profile()..........");

        /*
        <table class="toccolours itwiki_template_toc" style="text-align: right; width: 100%; float: right; clear: right; width:20em; margin-top: 0.5em; text-align:left">
        <tr style="width:100%; background:#f576a3">
            <td colspan="2"></td>
        </tr>
        <tr style="width:100%">
            <th style="text-align: center; font-size: larger;" colspan="2"> <b>Kojima Haruna (小嶋陽菜)</b></th>
            <td colspan="2"></td>
        </tr>
        <tr style="width:100%">
            <th style="text-align: center;" colspan="2"> <b>Harunyan (はるにゃん), Kojiharu (こじはる)</b></th>
        </tr>
        <tr style="width:100%">
            <td style="padding: 1em 0; text-align: center;" colspan="2">
                <div class="thumb tright">
                    <div class="thumbinner" style="width:282px;">
                        <a href="/wiki/index.php/File:KojimaHarunaA2014.jpg" class="image">
                            <img alt="" src="/wiki/images/thumb/0/0f/KojimaHarunaA2014.jpg/280px-KojimaHarunaA2014.jpg" width="280" height="340" class="thumbimage" />
                        </a>
                        <div class="thumbcaption">
                            <div class="magnify">
                                <a href="/wiki/index.php/File:KojimaHarunaA2014.jpg" class="internal" title="Enlarge">
                                    <img src="/wiki/skins/common/images/magnify-clip.png" width="15" height="11" alt="" />
                                </a>
                            </div>
                            Kojima Haruna (2014)
                        </div>
                    </div>
                </div>
            </td>
        </tr>
        <tr style="vertical-align: top; text-align: left;">
            <td style="width:50%"><b>Social Networks</b></td>
            <td>
                <a rel="nofollow" class="external text" href="http://blog.oricon.co.jp/no3b/category_2/">Ameblo</a> /
                <a rel="nofollow" class="external text" href="https://plus.google.com/114739367195523292316">Google+</a> /
                <a rel="nofollow" class="external text" href="http://instagram.com/nyanchan22#">Instagram</a> /
                <a rel="nofollow" class="external text" href="https://twitter.com/kojiharunyan">Twitter</a> /
                <a rel="nofollow" class="external text" href="http://7gogo.jp/lp/vY_r5fWR7IlWkVIvojdMdG==">7gogo</a>
            </td>
        </tr>
        */
        Element root = doc.select("table.itwiki_template_toc").first();
        if (root != null) {
            //Log.e(mTag, root.text());
            for (Element tr : root.select("tr")) {
                Element td1 = tr.select("td").first();
                if (td1 != null) {
                    Element img = td1.select("img.thumbimage").first();
                    if (img != null) {
                        String src = img.attr("src"); // /wiki/images/thumb/0/0f/KojimaHarunaA2014.jpg/280px-KojimaHarunaA2014.jpg;
                        //Log.e(mTag, "src: " + src);
                        src = mBaseUrl + src;
                        //memberData.setWikipediaImageUrl(src);

                        //Element a = td1.select("a.image").first();
                        //if (a != null) {
                        //    //Log.e(mTag, "a.href: " + a.attr("href"));
                        //    //mWikipediaProfileImageHref = mBaseUrl + a.attr("href"); // /wiki/index.php/File:KojimaHarunaA2014.jpg
                        //}
                        continue;
                    }

                    if (td1.text().equals("Social Networks")) {
                        Element td2 = tr.select("td").last();
                        if (td2 != null) {
                            for (Element a : td2.select("a")) {
                                if (a != null) {
                                    String href = a.attr("href");
                                    String text = a.text();
                                    //Log.e(mTag, "title: " + title + ", href: " + href);

                                    if (href.contains("plus.google.com")) {
                                        String[] info = Util.getSnsInfo(href);
                                        memberData.setGooglePlusId(info[0]);
                                        memberData.setGooglePlusUrl(info[1]);
                                    } else if (href.contains("twitter.com")) {
                                        String[] info = Util.getSnsInfo(href);
                                        memberData.setTwitterId(info[0]);
                                        memberData.setTwitterUrl(info[1]);
                                    } else if (href.contains("facebook.com")) {
                                        String[] info = Util.getSnsInfo(href);
                                        memberData.setFacebookId(info[0]);
                                        memberData.setFacebookUrl(info[1]);
                                    } else if (href.contains("instagram.com")) {
                                        String[] info = Util.getSnsInfo(href);
                                        memberData.setInstagramId(info[0]);
                                        memberData.setInstagramUrl(info[1]);
                                    } else if (href.contains("7gogo.jp")) {
                                        memberData.setNanagogoUrl(href);
                                    } else if (href.contains("ameblo.jp")) {
                                        memberData.setBlogUrl(href);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        //showProfileImage();
    }

    public void parseProfileImage(Document doc, ArrayList<WebData> dataList) {
        //Log.e(mTag, "parseStage48Gallery()............");
        /*
        <h2> <span class="mw-headline" id="Gallery">Gallery</span></h2>
        <div style="text-align:left; font-size:0.7em; color:black; margin:0.7em 0;">
            <span style="display:inline-block; vertical-align:top; width:197px; margin-right:10px; margin-bottom:10px;">
                <table style="width:197px; height:232px; border:1px solid #3366CC; padding:10px; border-spacing:0;">
                    <td style="text-align:center; vertical-align:middle; padding:0px;">
                        <a href="/wiki/index.php/File:B-kojima_haruna.jpg" class="image">
                            <img alt="B-kojima haruna.jpg" src="/wiki/images/thumb/9/94/B-kojima_haruna.jpg/168px-B-kojima_haruna.jpg" width="168" height="210" />
                        </a>
                    </td>
                </table>
                <div style="text-align:center;">Kojima Haruna (2013)</div>
            </span>
            <span style="display:inline-block; vertical-align:top; width:197px; margin-right:10px; margin-bottom:10px;">
         */
        Element root = doc.getElementById("Gallery");
        if (root != null) {
            root = root.parent(); // h2
            if (root != null) {
                root = root.nextElementSibling(); // div
                if (root != null) {
                    //Log.e(mTag, root.html());
                    String regex = ".*(\\d\\d\\d\\d).*"; //".*\\((.*)\\)";
                    Pattern pattern = Pattern.compile(regex);

                    //LinearLayout loImages = (LinearLayout) findViewById(R.id.loImages);
                    for (Element span : root.select("span")) {
                        Element img = span.select("img").first();
                        if (img == null) {
                            continue;
                        }
                        //Log.e(mTag, img.attr("src"));
                        String src = mBaseUrl + img.attr("src");

                        Element a = span.select("a").first();
                        String href = mBaseUrl + a.attr("href");

                        String caption = "";
                        Element div = span.select("div").first();
                        if (div != null) {
                            caption = div.text();
                            Matcher matcher = pattern.matcher(caption);
                            while (matcher.find()) {
                                //Log.e(mTag, "matcher.groupCount(): " + matcher.groupCount());
                                if (matcher.groupCount() != 1) {
                                    continue;
                                }
                                caption = matcher.group(1).trim();
                            }
                        }
                        WebData webData = new WebData();
                        webData.setTitle(caption);
                        webData.setUrl(href);
                        webData.setThumbnailUrl(src);

                        dataList.add(webData);
                    }
                }
            }
        }
    }
}
