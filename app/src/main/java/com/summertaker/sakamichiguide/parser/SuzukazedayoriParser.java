package com.summertaker.sakamichiguide.parser;

import com.summertaker.sakamichiguide.data.WebData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

public class SuzukazedayoriParser extends BaseParser {

    public void parseList(String response, ArrayList<WebData> webDataList) {
        /*
        <div class="box" id="box_recommend">
            <h3>オススメ生写真</h3>
            <div class="item_box02">
                <div class="item_photo">
                    <a href="?pid=102643487">
                        <img src="http://xn--akb48-gy5h153scmd.com/photo/031-20160523-NO1-673s.jpg" onerror="this.src='http://xn--akb48-gy5h153scmd.com/photo/now_printing.png';" class="item" width="150px"/>
                    </a><br />
                </div>
                <div class="item_detail">
                    <p class="item_name">
                        <a href="?pid=102643487">
                            【渡辺みり愛】 乃木坂46 公式生写真 [乃木選 2016 MAY file.005] HUSTLEPRESS WEB SHOP 乃木選封入 直筆サイン入り C
                        </a><br />
                    </p>
                    <p class="item_price">
                        9,980円(税込)<br />
                    </p>
                </div>
            </div>
            ....
        */
        response = clean(response);
        Document doc = Jsoup.parse(response);

        Element root = doc.getElementById("box_recommend");
        if (root == null) {
            return;
        }

        Element el;

        for (Element row : root.select(".item_box02")) {

            String title = null;
            String price = null;
            String url = null;
            String thumbnailUrl = null;

            //Log.e(mTag, title + " / " + thumbnailUrl);

            Element itemPhoto = row.select(".item_photo").first();
            if (itemPhoto == null) {
                continue;
            }

            el = itemPhoto.select("a").first();
            if (el == null) {
                continue;
            }
            url = el.attr("href");
            url = "http://suzukazedayori.com/" + url;

            el = el.select("img").first();
            if (el == null) {
                continue;
            }
            thumbnailUrl = el.attr("src");

            Element itemDetail = row.select(".item_detail").first();
            if (itemDetail == null) {
                continue;
            }
            el = itemDetail.select(".item_name").first();
            title = el.text().trim();

            el = itemDetail.select(".item_price").first();
            price = el.text().trim();

            //Log.e(mTag, title + " / " + thumbnailUrl);

            WebData webData = new WebData();
            webData.setTitle(title);
            webData.setPrice(price);
            webData.setUrl(url);
            webData.setThumbnailUrl(thumbnailUrl);

            webDataList.add(webData);
        }
    }
}

