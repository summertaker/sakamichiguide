package com.summertaker.sakamichiguide.parser;

import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.data.GroupData;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.util.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

public class WikipediaEnParser extends BaseParser {

    public String getUrl(String groupId) {
        return "https://en.wikipedia.org/wiki/" + Util.ucfirst(groupId.toLowerCase());
    }

    public void parse48List(String response, GroupData groupData, ArrayList<MemberData> memberList) {
        response = clean(response);
        Document doc = Jsoup.parse(response);

        for (Element table : doc.select(".wikitable")) {

            Element tr1 = table.select("tr").first();
            Element th1 = tr1.select("th").first();
            if (th1 == null || th1.text() == null) {
                continue;
            }
            if (!th1.text().equals("Name")) {
                continue;
            }

            int count = 0;
            for (Element tr : table.select("tr")) {

                String nameJa;
                String nameEn;

                count++;
                if (count < 2) {
                    continue;
                }

                Element td = tr.select("td").first();
                if (td == null) {
                    continue;
                }
                Element a = td.select("a").first();
                if (a != null) {
                    nameEn = a.text().trim();
                } else {
                    String[] array = td.html().split("<span ");
                    nameEn = array[0].trim();
                }

                Element span = td.select("span").first();
                if (span == null) {
                    continue;
                }
                Element kanji = span.select(".t_nihongo_kanji").first();
                if (kanji == null) {
                    continue;
                }
                nameJa = kanji.text().trim();

                //Log.e(mTag, nameEn + " / " + nameJa);

                MemberData memberData = new MemberData();
                memberData.setNameJa(nameJa);
                memberData.setNameEn(nameEn);

                memberList.add(memberData);
            }
        }
    }

    public void parse46List(String response, GroupData groupData, ArrayList<MemberData> memberList) {
        /*
        <table class="wikitable sortable" style="text-align:left; font-size:small;">
        <tr>
            <th>Name</th>
            <th>Birth date (age)</th>
            <th>Native</th>
            <th>Height</th>
            <th>Generation</th>
            <th>Notes</th>
        </tr>
        <tr>
            <td>
                Manatsu Akimoto
                <span style="font-weight: normal">
                (
                    <span class="t_nihongo_kanji" lang="ja">秋元真夏</span>
                    <sup class="t_nihongo_help noprint">
                        <a href="/wiki/Help:Installing_Japanese_character_sets" title="Help:Installing Japanese character sets">
                            <span class="t_nihongo_icon" style="color: #00e; font: bold 80% sans-serif; text-decoration: none; padding: 0 .1em;">?</span>
                        </a>
                    </sup>
                )
               </span>
            </td>
            <td><span style="display:none">(<span class="bday">1993-08-20</span>)</span> August 20, 1993 <span class="noprint ForceAgeToShow">(age&#160;22)</span></td>
            <td>Saitama</td>
            <td>156&#160;cm</td>
            <td>1</td>
            <td></td>
        </tr>
        */
        //Log.e(mTag, response);

        response = clean(response);
        Document doc = Jsoup.parse(response);

        Element el;

        for (Element table : doc.select(".wikitable")) {
            //Log.e(mTag, table.html());

            Element tr1 = table.select("tr").first();
            Element th1 = tr1.select("th").first();
            if (th1 == null || th1.text() == null) {
                continue;
            }
            if (!th1.text().trim().equals("Name")) {
                continue;
            }

            int count = 0;
            for (Element tr : table.select("tr")) {
                //Log.e(mTag, tr.html());

                String nameEn;
                String nameJa;

                count++;
                //Log.e(mTag, "count: " + count);

                if (count < 2) {
                    continue;
                }

                Element td = tr.select("td").first();
                if (td == null) {
                    continue;
                }
                String[] array = td.html().split("<span ");
                if (array.length < 2) {
                    continue;
                }
                nameEn = array[0].trim();

                Element span = td.select("span").first();
                Element kanji = span.select(".t_nihongo_kanji").first();
                if (kanji == null) {
                    continue;
                }
                nameJa = kanji.text().trim();

                //Log.e(mTag, nameEn + "/" + nameJa);

                MemberData memberData = new MemberData();
                memberData.setNameEn(nameEn);
                memberData.setNameJa(nameJa);
                memberList.add(memberData);
            }
        }
    }
}


