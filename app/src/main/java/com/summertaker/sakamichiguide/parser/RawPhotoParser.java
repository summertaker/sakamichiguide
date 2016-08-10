package com.summertaker.sakamichiguide.parser;

import android.util.Log;

import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.data.WebData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

public class RawPhotoParser extends BaseParser {

    public void parseSuzukazedayoriMember(String response, String groupId, ArrayList<WebData> dataList) {
        response = clean(response);
        //Log.e(mTag, response.substring(0, 100));

        Document doc = Jsoup.parse(response);
        Element root = null;
        Element nav = null;

        int match = groupId.equals(Config.GROUP_ID_NOGIZAKA46) ? 1 : 2;
        int count = 0;
        boolean found = false;

        for (Element li : doc.select("li")) {
            for (Element div : li.select(".menber-nav")) {
                nav = div;
                found = true;
                break;
            }
            if (found) {
                root = li;
                break;
            }
        }

        if (root != null) {
            if (groupId.equals(Config.GROUP_ID_KEYAKIZAKA46)) {
                root = root.nextElementSibling();
            }

            Element el = root.getElementById("PlagClose" + match);
            if (el != null) {
                for (Element div : el.select(".menber-nav")) {
                    Element ul = div.select(".nav").first();
                    getSuzukazedayoriData(ul, dataList);
                }
            }
        }
    }

    private void getSuzukazedayoriData(Element ul, ArrayList<WebData> dataList) {
        for (Element li : ul.select("li")) {

            String name;
            String url;

            Element a = li.select("a").first();
            url = "http://suzukazedayori.com" + a.attr("href");

            Element el = a.select(".kanji").first();
            name = el.text();

            //Log.e(mTag, name + " / " + url);

            WebData data = new WebData();
            data.setName(name);
            data.setUrl(url);
            dataList.add(data);
        }
    }

    public void parseShopproMember(String response, String groupId, ArrayList<WebData> dataList) {
        /*
         <ul class="mega-navi">
            <li class="mega-navi__unit">
                <a href="http://nogisen.shop-pro.jp/?mode=cate&cbid=2048195&csid=0" class="mega-navi__link">秋元真夏</a>
            </li>
            ...
        */
        response = clean(response);
        //Log.e(mTag, response.substring(0, 100));

        Document doc = Jsoup.parse(response);

        Element root = doc.select(".mega-navi").first();
        if (root == null) {
            return;
        }

        for (Element row : root.select("li")) {

            String name;
            String url;

            Element a = row.select("a").first();
            url = a.attr("href");
            name = a.text();

            //Log.e(mTag, name + " " + url);

            WebData data = new WebData();
            data.setName(name);
            data.setUrl(url);
            dataList.add(data);
        }
    }

    public int parseShopproList(String response, String groupId, ArrayList<WebData> dataList) {
        /*
        <ul class="product-list productlist-list">
            <li class="product-list__unit product-list__unit-lg">
                <a href="?pid=104992169" class="product-list__link">
                    <img src="//img08.shop-pro.jp/PA01204/943/product/104992169_th.jpg?cmsp_timestamp=20160720203936" alt="乃木坂46 WebShop限定生写真 2016.July タンクトップ 生田絵梨花 コンプ" class="product-list__image" />
                </a>
                <a href="?pid=104992169" class="product-list__name product-list__text txt--lg">
                    乃木坂46 WebShop限定生写真 2016.July タンクトップ 生田絵梨花 コンプ
                </a>
                <p class="product-list__prices">
                    <span class="product-list__price-soldout product-list__text">SOLD OUT</span>
                </p>
            </li>
            ...
        */
        response = clean(response);
        //Log.e(mTag, response.substring(0, 100));

        Document doc = Jsoup.parse(response);

        Element root = doc.select(".productlist-list").first();
        if (root == null) {
            return 0;
        }

        String baseUrl = groupId.equals(Config.GROUP_ID_NOGIZAKA46) ? "http://nogisen.shop-pro.jp/" : "http://keyaking.shop-pro.jp/";

        for (Element row : root.select("li")) {

            String name;
            String url;
            String imageUrl;

            Element a = row.select("a").first();
            url = baseUrl + a.attr("href");

            Element img = a.select("img").first();

            // http://img08.shop-pro.jp/PA01204/943/product/104992260_th.jpg?cmsp_timestamp=20160720204058
            // http://img08.shop-pro.jp/PA01204/943/product/104992260.jpg?cmsp_timestamp=20160720204058
            imageUrl = "http:" + img.attr("src");
            imageUrl = imageUrl.replace("_th.jpg", ".jpg");

            name = img.attr("alt");

            //Log.e(mTag, name + " / " + url + " / " + imageUrl);

            WebData data = new WebData();
            data.setName(name);
            data.setUrl(url);
            data.setImageUrl(imageUrl);
            dataList.add(data);
        }

        /*
        <div class="pagenation">
            <div class="pagenation-prev">
                <a href="?mode=cate&cbid=2048197&csid=0&page=1" class="">&lt; 前のページ</a>
            </div>
            <div class="pagenation-next">
                <a href="?mode=cate&cbid=2048197&csid=0&page=3" class="">次のページ &gt;</a>
            </div>
            <div class="pagenation-pos">
                全<span class="pagenation-pos__number">296</span>件&nbsp;[&nbsp;25-48&nbsp;]
            </div>
        </div>
        */
        Element page = doc.select(".pagenation-pos__number").first();
        return (page == null) ? 0 : Integer.parseInt(page.text());
    }
}
