package com.summertaker.sakamichiguide.parser;

import android.util.Log;

import com.summertaker.sakamichiguide.data.WebData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

public class GoogleImageParser extends BaseParser {

    private String mTag = "GoogleImageParser";

    public void parse(String response, ArrayList<WebData> dataList) {
        /*
        <div id="rg_s" style="visibility:hidden">
            <!--m-->
            <div class="rg_di rg_el ivg-i" data-ved="0ahUKEwifouXwqODLAhVlKKYKHRbeDIIQMwgbKAAwAA">
                <a href="/imgres?imgurl=http://cfile8.uf.tistory.com/image/23656A4D552A42F20D9723
                    &amp;imgrefurl=http://topsy.tistory.com/3550
                    &amp;h=750
                    &amp;w=600
                    &amp;tbnid=sNJg5S-4LnjQ9M:
                    &amp;docid=vJAKpYDcpoqRYM&amp;hl=ko
                    &amp;ei=eIj3Vp-GAeXQmAWWvLOQCA&amp;tbm=isch" target="_blank"
                    jsaction="fire.ivg_o;mouseover:str.hmov;mouseout:str.hmou" class="rg_l">

                    <img data-sz="f" name="sNJg5S-4LnjQ9M:" class="rg_i" alt="akb48 에토미사에 대한 이미지 검색결과"
                     jsaction="load:str.tbn" onload="google.aft&&google.aft(this)">

                    <div class="_aOd rg_ilm">
                        <div class="rg_ilmbg">
                            <span class="rg_ilmn"> 600&nbsp;&#215;&nbsp;750 - topsy.tistory.com </span>
                        </div>
                    </div>
                </a>
                <div class="rg_meta">
                    {"cb":21,"cl":15,"cr":15,"ct":9,"id":"sNJg5S-4LnjQ9M:","isu":"topsy.tistory.com","ity":"","oh":750,"ou":"http://cfile8.uf.tistory.com/image/23656A4D552A42F20D9723","ow":600,"pt":"노기자카46의 극한 비주얼, 에토 미사 - 팔만대잡담","rid":"vJAKpYDcpoqRYM","ru":"http://topsy.tistory.com/3550","s":"아무래도 다른 맴버들보다 경력이 길다 보니 연예인 티를 일찍 내고 있었죠. 그런데도 선발에 못들어서 일본에서도 말이 많았는데, 이젠 선발에 꾸준히 들고 있어서 ...","sc":1,"th":251,"tu":"https://encrypted-tbn0.gstatic.com/images?q\u003dtbn:ANd9GcSiHpWdEH8gZ-MlUxzhzGCHw1yumToLkDGA-jNltZdfDPHEEPXa","tw":201}
                </div>
            </div>
            <!--n-->

            <!--m-->
            <div class="rg_di rg_el ivg-i" data-ved="0ahUKEwifouXwqODLAhVlKKYKHRbeDIIQMwgcKAEwAQ">
            ...
        </div>
        */
        response = clean(response);
        Document doc = Jsoup.parse(response);

        Element root = doc.getElementById("rg_s");

        if (root == null) {
            Log.e(mTag, "============== root is null ==================");
            return;
        }
        //Log.e(mTag, root.html());

        for (Element row : root.select(".rg_di")) {
            //Log.e(mTag, row.html());

            String siteId = "googleimage";
            String title;
            String thumbnailUrl;

            Element el = row.select("a").first();
            String href = el.attr("href");
            href = urlDecode(href);
            //Log.e(mTag, "href: " + href);

            String suffix = ".jpg";
            String[] array = href.split(".jpg");
            if (array.length == 1) {
                array = href.split(".jpeg");
                suffix = ".jpeg";
            }
            if (array.length < 2) {
                continue;
            }
            thumbnailUrl = array[0];
            thumbnailUrl = thumbnailUrl.replace("/imgres?imgurl=", "");
            thumbnailUrl = thumbnailUrl + suffix;
            Log.e(mTag, "thumbnailUrl: " + thumbnailUrl);

            title = el.attr("alt");

            WebData webData = new WebData();
            webData.setSiteId(siteId);
            webData.setTitle(title);
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

