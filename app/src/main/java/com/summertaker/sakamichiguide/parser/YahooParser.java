package com.summertaker.sakamichiguide.parser;

import com.summertaker.sakamichiguide.data.WebData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

public class YahooParser extends BaseParser {

    private String mTag = "YahooParser";

    public void parseImage(String response, ArrayList<WebData> dataList) {
        response = clean(response);
        Document doc = Jsoup.parse(response);

        Element root = doc.getElementById("gridlist");

        /*
        <div class="SeR">
            <p class="tb">
                <a href="http://ord.yahoo.co.jp/o/image/_ypr.jpg" target="imagewin">
                    <img src="http://msp.c.yimg.jp/yjimage?q=SxGf47">
                    <span class="imgsize">376 x 456</span>
                </a>
            </p>
            ...
        </div>
        */

        if (root == null) {
            return;
        }

        for (Element row : root.select(".gridmodule")) {
            //Log.e(mTag, a.html());

            String siteId = "yahooimage";
            String title;
            String imageUrl;
            String thumbnailUrl;

            Element el = row.select("a").first();
            imageUrl = el.attr("href");
            //Log.e(mTag, "imageUrl: " + imageUrl);
            if (imageUrl.contains("**")) {
                int idx = imageUrl.indexOf("**");
                imageUrl = imageUrl.substring(idx + 2, imageUrl.length());
            }
            //href = href.split("\\?imgurl=")[1];
            //Log.e(mTag, "imageUrl: " + imageUrl);

            el = el.select("img").first();
            thumbnailUrl = el.attr("src");
            if (thumbnailUrl == null || thumbnailUrl.isEmpty()) {
                continue;
            }
            //imageUrl = urlDecode(imageUrl);
            //Log.e(mTag, "thumbnailUrl: " + thumbnailUrl);

            //thumbnailUrl = urlDecode(thumbnailUrl);
            //Log.e(mTag, "- thumbnailUrl: " + thumbnailUrl);

            title = row.select("h3").first().text();

            WebData webData = new WebData();
            webData.setSiteId(siteId);
            webData.setTitle(title);
            webData.setImageUrl(imageUrl);
            webData.setThumbnailUrl(thumbnailUrl);

            dataList.add(webData);
        }
    }

    private String urlDecode(String url) {
        String newUrl = url;

        if (newUrl.contains("%")) {
            newUrl = newUrl.split("%")[0];
        }

        return newUrl;
    }
}

