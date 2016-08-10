package com.summertaker.sakamichiguide.parser;

import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.data.WebData;
import com.summertaker.sakamichiguide.util.Util;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pedia48ProfileParser extends BaseParser {

    String mTag = "### Pedia48ProfileParser";

    String mBaseUrl = "http://48pedia.org";

    public void parseProfile(Document doc, MemberData memberData) {
        Element toc = doc.select("table.infobox").first();
        if (toc != null) {
            //Log.e(mTag, "toc.html(): " + toc.html());
            Element img = toc.select("img").first();
            if (img != null) {
                String src = img.attr("src");
                //Log.e(mTag, "img.src: " + src);

                // /images/thumb/3/32/2014年NMB48プロフィール_小谷里歩.jpg/250px-2014年NMB48プロフィール_小谷里歩.jpg
                src = src.split(".jpg/")[0].replace("/thumb/", "/") + ".jpg";
                src = mBaseUrl + src;

                memberData.setPedia48ImageUrl(src);
            }
        }

        for (Element h2 : doc.select("h2")) {
            //Log.e(mTag, "h2.text(): " + h2.text());

            if (h2.text().trim().equals("外部リンク")) {
                Element ul = h2.nextElementSibling();
                //Log.e(mTag, "ul.html(): " + ul.html());

                if (ul != null) {
                    for (Element li : ul.select("li")) {
                        Element del = li.select("del").first();
                        if (del != null) {
                            continue;
                        }
                        Element s = li.select("s").first();
                        if (s != null) {
                            continue;
                        }

                        Element a = li.select("a").first();
                        if (a == null) {
                            continue;
                        }

                        String text = a.text().toLowerCase().trim();
                        String href = a.attr("href").trim();

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
                        } else {
                            //if (text.contains("ブログ")) {
                            //    memberData.setBlogUrl(href);
                            //}
                        }
                    }
                }
            }
        }
    }

    public void parseProfileImage(Document doc, ArrayList<WebData> dataList) {
        Element ul = doc.select("ul.gallery").first();

        if (ul != null) {
            String regex = ".*(\\d\\d\\d\\d年).*";
            Pattern pattern = Pattern.compile(regex);

            ArrayList<WebData> imageDatas = new ArrayList<>();

            for (Element li : ul.select("li.gallerybox")) {
                //Log.e(mTag, li.html());
                Element a = li.select("a").first();
                if (a == null) {
                    continue;
                }
                String href = mBaseUrl + a.attr("href");

                Element img = li.select("img").first();
                if (img == null) {
                    continue;
                }
                //Log.e(mTag, "img.src: " + img.attr("data-cfsrc"));
                String src = img.attr("src");

                // /images/thumb/2/26/2014年AKB48プロフィール_小嶋陽菜.jpg/99px-2014年AKB48プロフィール_小嶋陽菜.jpg
                // /images/2/26/2014年AKB48プロフィール_小嶋陽菜.jpg
                src = src.split(".jpg/")[0].replace("/thumb/", "/") + ".jpg";
                src = mBaseUrl + src;

                String caption = "";
                Element p = li.select("div.gallerytext > p").first();
                if (p != null) {
                    caption = p.text().trim();
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
                imageDatas.add(webData);
            }

            // 역순으로 정렬
            if (imageDatas.size() > 0) {
                for (int i = imageDatas.size() - 1; i >= 0; i--) {
                    dataList.add(imageDatas.get(i));
                }
            }
        }
    }
}
