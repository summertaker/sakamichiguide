package com.summertaker.sakamichiguide.parser;

import com.summertaker.sakamichiguide.data.SiteData;
import com.summertaker.sakamichiguide.data.WebData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class YoutubeParser extends BaseParser {

    public void parseList(String response, SiteData siteData, ArrayList<WebData> webDataList) {
        /*
        <ul id="channels-browse-content-grid" class="channels-browse-content-grid branded-page-gutter-padding grid-lockups-container">
            <li class="channels-content-item yt-shelf-grid-item">
                <div class="yt-lockup clearfix  yt-lockup-video yt-lockup-grid vve-check" data-context-item-id="c47UO4ryEjs" data-visibility-tracking="CCwQlDUiEwjf8v3z5cnMAhUGgVgKHeSvAScomxxAu6TI17iHtcdz">
                    <div class="yt-lockup-dismissable">
                        <div class="yt-lockup-thumbnail">
                            <span class=" spf-link  ux-thumb-wrap contains-addto">
                                <a href="/watch?v=c47UO4ryEjs" class="yt-uix-sessionlink" aria-hidden="true"  data-sessionlink="ei=UdUuV5-GMYaC4gLk34a4Ag&amp;feature=c4-videos-u&amp;ved=CDEQwBsiEwjf8v3z5cnMAhUGgVgKHeSvAScomxw" >
                                    <span class="video-thumb  yt-thumb yt-thumb-196">
                                        <span class="yt-thumb-default">
                                            <span class="yt-thumb-clip">
                                                <img src="https://i.ytimg.com/vi/c47UO4ryEjs/hqdefault.jpg?custom=true&amp;w=196&amp;h=110&amp;stc=true&amp;jpg444=true&amp;jpgq=90&amp;sp=68&amp;sigh=h7CqgQI7tTTvd4fhV4qvHwTZps8" alt="" aria-hidden="true" width="196" >
                                                <span class="vertical-align"></span>
                                            </span>
                                        </span>
                                    </span>
                                </a>
                                <span class="video-time" aria-hidden="true"><span aria-label="3 分、1 秒">3:01</span>
                            </span>
                            <span class="thumb-menu dark-overflow-action-menu video-actions">
                                <button type="button" onclick=";return false;" aria-expanded="false" aria-haspopup="true" class="yt-uix-button-reverse flip addto-watch-queue-menu spf-nolink hide-until-delayloaded yt-uix-button yt-uix-button-dark-overflow-action-menu yt-uix-button-size-default yt-uix-button-has-icon no-icon-markup yt-uix-button-empty" >
                                    <span class="yt-uix-button-arrow yt-sprite"></span>
                                    <ul class="watch-queue-thumb-menu yt-uix-button-menu yt-uix-button-menu-dark-overflow-action-menu hid">
                                        <li role="menuitem" class="overflow-menu-choice addto-watch-queue-menu-choice addto-watch-queue-play-next yt-uix-button-menu-item" data-action="play-next" onclick=";return false;"  data-video-ids="c47UO4ryEjs">
                                            <span class="addto-watch-queue-menu-text">次に再生</span>
                                        </li>
                                        <li role="menuitem" class="overflow-menu-choice addto-watch-queue-menu-choice addto-watch-queue-play-now yt-uix-button-menu-item" data-action="play-now" onclick=";return false;"  data-video-ids="c47UO4ryEjs">
                                            <span class="addto-watch-queue-menu-text">今すぐ再生</span>
                                        </li>
                                    </ul>
                                </button>
                            </span>
                            <button class="yt-uix-button yt-uix-button-size-small yt-uix-button-default yt-uix-button-empty yt-uix-button-has-icon no-icon-markup addto-button video-actions spf-nolink hide-until-delayloaded addto-watch-later-button yt-uix-tooltip" type="button" onclick=";return false;" title="後で見る" role="button" data-video-ids="c47UO4ryEjs"></button>
                            <button class="yt-uix-button yt-uix-button-size-small yt-uix-button-default yt-uix-button-empty yt-uix-button-has-icon no-icon-markup addto-button addto-queue-button video-actions spf-nolink hide-until-delayloaded addto-tv-queue-button yt-uix-tooltip" type="button" onclick=";return false;" title="キュー" data-video-ids="c47UO4ryEjs" data-style="tv-queue"></button>
                        </span>
                    </div>
                    <div class="yt-lockup-content">
                        <h3 class="yt-lockup-title ">
                            <a class="yt-uix-sessionlink yt-uix-tile-link  spf-link  yt-ui-ellipsis yt-ui-ellipsis-2" dir="ltr" title="2016.7.8公開 「DOCUMENTARY of AKB48」（仮題）特報 / AKB48[公式]"  aria-describedby="description-id-615375" data-sessionlink="ei=UdUuV5-GMYaC4gLk34a4Ag&amp;feature=c4-videos-u&amp;ved=CDIQvxsiEwjf8v3z5cnMAhUGgVgKHeSvAScomxw" href="/watch?v=c47UO4ryEjs">
                                2016.7.8公開 「DOCUMENTARY of AKB48」（仮題）特報 / AKB48[公式]
                            </a>
                            <span class="accessible-description" id="description-id-615375"> - 長さ: 3 分、1 秒。</span>
                        </h3>
                        <div class="yt-lockup-meta">
                            <ul class="yt-lockup-meta-info"><li>視聴回数 119,939 回</li><li>3 日前</li></ul>
                        </div>
                    </div>
                </div>
                <div class="yt-lockup-notifications-container hid" style="height:110px"></div>
            </div>
        </li>
        ....
        */
        response = clean(response);
        Document doc = Jsoup.parse(response);

        Element root = doc.getElementById("channels-browse-content-grid");

        Element el;

        for (Element li : root.select("li")) {

            String id;
            String title;
            String hit = null;
            String date = null;
            String time = null;
            String url;
            String thumbnailUrl;

            el = li.select(".yt-uix-sessionlink").first();
            if (el == null) {
                continue;
            }
            url = el.attr("href").trim();
            id = url.replace("/watch?v=", "");
            url = "https://www.youtube.com" + url;

            // http://stackoverflow.com/questions/2068344/how-do-i-get-a-youtube-video-thumbnail-from-the-youtube-api
            //http://img.youtube.com/vi/<insert-youtube-video-id-here>/0.jpg
            //http://img.youtube.com/vi/<insert-youtube-video-id-here>/1.jpg
            //http://img.youtube.com/vi/<insert-youtube-video-id-here>/2.jpg
            //http://img.youtube.com/vi/<insert-youtube-video-id-here>/3.jpg
            thumbnailUrl = "http://img.youtube.com/vi/" + id + "/mqdefault.jpg";
            //thumbnailUrl = "http://img.youtube.com/vi/" + id + "/hqdefault.jpg";

            /*el = li.select(".yt-thumb-default").first();
            if (el == null) {
                continue;
            }
            el = el.select("img").first();
            if (el == null) {
                continue;
            }
            thumbnailUrl = el.attr("src").trim();*/

            el = li.select(".yt-lockup-title").first();
            if (el == null) {
                continue;
            }
            //title = el.text().trim();
            el = el.select("a").first();
            title = el.attr("title").trim();

            el = li.select(".video-time").first();
            if (el == null) {
                continue;
            }
            time = el.text().trim();

            Elements ul = li.select(".yt-lockup-meta-info");
            if (ul == null) {
                continue;
            }
            int count = 0;
            for (Element meta : ul.select("li")) {
                String text = meta.text().trim();

                switch (count) {
                    case 0:
                        hit = text;
                        break;
                    case 1:
                        date = text;
                        break;
                }
                count++;
            }

            //namuwikiInfo = namuwikiInfo.replaceAll("\\[[\\d+]\\]", "").trim();

            //Log.e(mTag, title + " / " + thumbnailUrl);

            WebData webData = new WebData();
            webData.setTitle(title);
            webData.setHit(hit);
            webData.setDate(date);
            webData.setTime(time);
            webData.setUrl(url);
            webData.setThumbnailUrl(thumbnailUrl);

            webDataList.add(webData);
        }
    }
}
