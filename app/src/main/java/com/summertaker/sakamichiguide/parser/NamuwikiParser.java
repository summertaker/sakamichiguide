package com.summertaker.sakamichiguide.parser;

import com.summertaker.sakamichiguide.common.Config;
import com.summertaker.sakamichiguide.data.GroupData;
import com.summertaker.sakamichiguide.data.MemberData;
import com.summertaker.sakamichiguide.util.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

public class NamuwikiParser extends BaseParser {

    public String getUrl(String groupId) {
        String url = "";
        switch (groupId) {
            case Config.GROUP_ID_NOGIZAKA46:
                url = "https://namu.wiki/w/%EB%85%B8%EA%B8%B0%EC%9E%90%EC%B9%B446/%EB%A9%A4%EB%B2%84";
                break;
            case Config.GROUP_ID_KEYAKIZAKA46:
                url = "https://namu.wiki/w/%EC%BC%80%EC%95%BC%ED%82%A4%EC%9E%90%EC%B9%B446/%EB%A9%A4%EB%B2%84";
                break;
            default:
                url = "https://namu.wiki/w/" + groupId + "/%EB%A9%A4%EB%B2%84%20%EC%9D%BC%EB%9E%8C";
                break;
        }
        return url;
    }

    public void parse48List(String response, GroupData groupData, ArrayList<MemberData> memberList) {
        /*
        <h3>
            <a id='s-2.1' href='#toc'>2.1.</a>
            요코야마 팀A (22명)
            <span class='wiki-edit-section'>
                <a rel='nofollow' href='/edit/AKB48/%EB%A9%A4%EB%B2%84%20%EC%9D%BC%EB%9E%8C?section=3'>&#91;편집&#93;</a>
            </span>
        </h3>
        <div class='wiki-table-wrap'>
            <table class='wiki-table'>
            <tr>
                <td>이름(한글)</td>
                <td>이름(원문)</td>
                <td>생년월일</td>
                <td>닉네임</td> // JKT48의 경우
                <td>기수</td>
                <td>비고</td>
            </tr>
            <tr>
                <td>
                    <a class='wiki-link-internal' href='/w/%EC%9D%B4%EB%A6%AC%EC%95%BC%EB%A7%88%20%EC%95%88%EB%82%98' title='이리야마 안나'>
                        이리야마 안나
                    </a>
                </td>
                <td>入山杏奈</td>
                <td>1995년 12월 3일</td>
                <td>10기</td>
                <td> </td>
            </tr>
            ...
        */
        response = clean(response);
        Document doc = Jsoup.parse(response);

        Element el;

        for (Element table : doc.select(".wiki-table")) {

            Element tr1 = table.select("tr").first();
            Element td1 = tr1.select("td").first();
            if (td1 == null || td1.text() == null) {
                continue;
            }
            if (!td1.text().trim().equals("이름(한글)")) {
                continue;
            }

            int count = 0;
            for (Element tr : table.select("tr")) {

                String nameKo;
                String localName;
                String noSpaceName;
                String birthday = null;
                String nickNameKo = null;
                String hometown = null;
                String generation = null;
                String namuwikiUrl = null;
                String namuwikiInfo = null;

                count++;
                if (count < 2) {
                    continue;
                }

                el = tr.select("td").first();
                nameKo = el.text().trim();

                Element a = el.select("a").first();
                if (a != null) {
                    namuwikiUrl = a.attr("href");
                    namuwikiUrl = "https://namu.wiki" + namuwikiUrl;
                }

                el = el.nextElementSibling();
                localName = el.text().trim();
                if (localName.isEmpty()) {
                    continue;
                }
                noSpaceName = Util.removeSpace(localName);

                el = el.nextElementSibling();
                if (el != null) {
                    birthday = el.text().trim();

                    el = el.nextElementSibling();
                    if (el != null) {
                        generation = el.text().trim();
                        el = el.nextElementSibling();
                        if (el != null) {
                            namuwikiInfo = el.text().trim();
                            namuwikiInfo = namuwikiInfo.replaceAll("\\[[\\d+]\\]", "").trim();
                        }
                    }
                }

                //Log.e(mTag, nameKo + " / " + localName + " / " + birthday + " / " + generation + " / " + namuwikiInfo + " / " + namuwikiUrl);

                MemberData memberData = new MemberData();
                memberData.setNameKo(nameKo);
                memberData.setLocalName(localName);
                memberData.setNoSpaceName(noSpaceName);
                memberData.setNicknameKo(nickNameKo);
                memberData.setBirth(birthday);
                memberData.setHometown(hometown);
                memberData.setGeneration(generation);
                memberData.setNamuwikiUrl(namuwikiUrl);
                memberData.setNamuwikiInfo(namuwikiInfo);

                //Log.e(mTag, "namuwikiInfo: " + namuwikiInfo);

                if (namuwikiInfo != null) {
                    if (namuwikiInfo.contains("부캡틴")) {
                        memberData.setViceCaptain(true);
                    } else if (namuwikiInfo.contains("캡틴")) {
                        memberData.setCaptain(true); // 팀 캡틴
                    } else if (namuwikiInfo.contains("부리더")) {
                        memberData.setViceCaptain(true);
                    } else if (namuwikiInfo.contains("리더")) {
                        memberData.setCaptain(true);
                    } else if (namuwikiInfo.contains("지배인")) {
                        //Log.e(mTag, namuwikiInfo);
                        memberData.setManager(true);
                    }

                    if (namuwikiInfo.contains("총감독")) {
                        memberData.setGeneralManager(true);
                    }
                    if (namuwikiInfo.contains("겸임")) {
                        memberData.setConcurrentPosition(true);
                    }
                }

                memberList.add(memberData);
            }
        }
    }

    public void parse46List(String response, GroupData groupData, ArrayList<MemberData> memberList) {
        /*
        <table class='wiki-table'>
            <tr>
                <td colspan='99' style='text-align:center'> 1기 </td>
            </tr>
            <tr>
                <td colspan='2'> 이름 </td>
                <td> 생년월일 </td>
                <td> 신장 </td>
                <td> 비고 </td>
            </tr>
            <tr>
                <td>
                    <a class='wiki-link-internal' href='/w/%EC%8B%9C%EB%9D%BC%EC%9D%B4%EC%8B%9C%20%EB%A7%88%EC%9D%B4'
                        title='시라이시 마이'>시라이시 마이
                    </a>
                </td>
                <td> 白石麻衣 </td>
                <td> 1992년 8월 20일 </td>
                <td> 161cm </td>
                <td> </td>
            </tr>
            ....
        */
        //Log.e(mTag, response);

        response = clean(response);
        Document doc = Jsoup.parse(response);

        Element el;

        for (Element table : doc.select(".wiki-table")) {
            //Log.e(mTag, table.html());

            Element tr1 = table.select("tr").first();
            Element tr2 = tr1.nextElementSibling();
            if (tr2 == null) {
                continue;
            }
            Element td1 = tr2.select("td").first();
            if (td1 == null || td1.text() == null) {
                continue;
            }
            if (!td1.text().trim().equals("이름")) {
                continue;
            }

            String generation = tr1.select("td").first().text().trim();

            int count = 0;
            for (Element tr : table.select("tr")) {
                //Log.e(mTag, tr.html());

                String nameKo;
                String localName;
                String noSpaceName;
                String birthday;
                String namuwikiUrl;
                String namuwikiInfo;

                count++;
                //Log.e(mTag, "count: " + count);

                if (count < 3) {
                    continue;
                }

                el = tr.select("td").first();
                nameKo = el.text().trim();

                Element a = el.select("a").first();
                if (a == null) {
                    namuwikiUrl = null;
                } else {
                    namuwikiUrl = a.attr("href");
                    namuwikiUrl = "https://namu.wiki" + namuwikiUrl;
                }

                el = el.nextElementSibling();
                localName = el.text().trim();
                if (localName.isEmpty()) {
                    continue;
                }
                noSpaceName = Util.removeSpace(localName);

                el = el.nextElementSibling();
                birthday = el.text().trim();

                el = el.nextElementSibling();

                el = el.nextElementSibling();
                namuwikiInfo = el.text().trim();
                namuwikiInfo = namuwikiInfo.replaceAll("\\[[\\d+]\\]", "").trim();

                //Log.e(mTag, nameKo + " / " + nameJa + " / " + birthday + " / " + generation + " / " + etc);

                MemberData memberData = new MemberData();
                memberData.setNameKo(nameKo);
                memberData.setLocalName(localName);
                memberData.setNoSpaceName(noSpaceName);
                memberData.setBirth(birthday);
                memberData.setGeneration(generation);
                memberData.setNamuwikiUrl(namuwikiUrl);
                memberData.setNamuwikiInfo(namuwikiInfo);
                if (namuwikiInfo.contains("캡틴")) {
                    memberData.setCaptain(true);
                }
                memberList.add(memberData);
            }
        }
    }

    public HashMap<String, String> parseProfile(String response) {
        /*
        <div class='wiki-table-wrap table-right'>
            <table class='wiki-table'>
                ...
                <tr>
                    <td colspan='2' style='text-align:center;background-color:#F6F6F6'>
                        <span class='wiki-color' style='color:#000000'><strong>이리야마 안나</strong> (入山 杏奈) </span>
                    </td>
                </tr>
                <tr>
                    <td colspan='2' style='text-align:center'>
                        <img class='wiki-image wiki-lazy-image wiki-lazy-loading' data-original='http://stage48.net/wiki/images/9/99/IriyamaAnna2015.jpg'><noscript><img class='wiki-image' src='http://stage48.net/wiki/images/9/99/IriyamaAnna2015.jpg'></noscript>
                    </td>
                </tr>
                ...
                <tr>
                    <td>
                        <strong>링크</strong>
                    </td>
                    <td>
                        <a class='wiki-link-external' href='http://www.akb48.co.jp/about/members/detail.php?mid=51' target='_blank' title='http://www.akb48.co.jp/about/members/detail.php?mid=51'>akb48 공식</a>
                        <a class='wiki-link-external' href='https://plus.google.com/101748015513264110691/posts' target='_blank' title='https://plus.google.com/101748015513264110691/posts'>구글플러스</a>
                        <strong><a class='wiki-link-external' href='https://twitter.com/iriyamaanna1203' target='_blank' title='https://twitter.com/iriyamaanna1203'>트위터</a></strong>
                     </td>
                </tr>
            </table>
        </div>
        */
        response = clean(response);
        //Log.i("##### response", response);

        HashMap<String, String> hashMap = new HashMap<>();

        Document doc = Jsoup.parse(response); // http://jsoup.org/

        Elements rows = doc.select(".wiki-table-wrap");
        if (rows == null) {
            return hashMap;
        }
        //Log.e(mTag, "root: " + root.html());

        for (Element table : rows.select("table")) {

            for (Element tr : table.select("tr")) {

                Element td1 = tr.select("td").first();
                if (!td1.text().equals("링크")) {
                    continue;
                }

                Element td2 = td1.nextElementSibling();
                if (td2 == null) {
                    continue;
                }
                for (Element a : td2.select("a")) {
                    String url = a.attr("href");
                    //Log.e(mTag, "url: " + url);

                    if (url.contains("plus.google.com")) {
                        hashMap.put(Config.SITE_ID_GOOGLE_PLUS, url);
                    } else if (url.contains("twitter.com")) {
                        hashMap.put(Config.SITE_ID_TWITTER, url);
                    } else if (url.contains("instagram.com")) {
                        //Log.e(mTag, "url: " + url);
                        hashMap.put(Config.SITE_ID_INSTAGRAM, url);
                    } else if (url.contains("amblo.jp")) {
                        hashMap.put(Config.SITE_ID_BLOG, url);
                    } else if (url.contains("7gogo.jp")) {
                        //http://7gogo.jp/lp/JitPAdGLEXaWkVIvojdMdG== // 잘못된 URL이 들어있는 경우가 있다.
                        if (!url.contains("/lp/")) {
                            hashMap.put(Config.SITE_ID_NANAGOGO, url);
                        }
                    } else {
                        if (url.substring(0, 4).equals("http")) {
                            //Log.e(mTag, a.text() + ": " + url);
                            //hashMap.put(Config.SITE_ID_BLOG, url);
                        }
                    }
                }
            }
        }

        return hashMap;
    }
}
