package com.summertaker.sakamichiguide.parser;

import com.summertaker.sakamichiguide.data.ElectionData;
import com.summertaker.sakamichiguide.data.RankingData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class ElectionParser extends BaseParser {

    private String mTag = "##### ElectionParser";

    private String mBaseUrl = "http://48pedia.org";

    public void parseList(String response, ArrayList<ElectionData> dataList) {
        //Log.e(mTag, response);

        response = clean(response);
        Document doc = Jsoup.parse(response);
        Element root = doc.getElementById("AKB48");

        if (root != null) {
            Element dl = root.parent().nextElementSibling();
            if (dl != null) {
                for (Element dt : dl.select("dt")) {
                    //Log.e(mTag, "" + dt.html());
                    String title = dt.text();

                    Element a = dt.select("a").first();
                    String pedia48Url = mBaseUrl + a.attr("href").trim();

                    String description = "";
                    Element dd = dt.nextElementSibling();
                    while (dd != null) {
                        //Log.e(mTag, "" + dd.text());
                        if (!dd.tagName().equals("dd")) {
                            break;
                        }
                        description += dd.text();
                        dd = dd.nextElementSibling();
                    }

                    //Log.e(mTag, title + " / " + pedia48Url + " / " + description);

                    ElectionData electionData = new ElectionData();
                    electionData.setTitle(title);
                    electionData.setDescription(description);
                    //electionData.setPedia48Url(pedia48Url);

                    dataList.add(electionData);
                }
            }
        }
    }

    public void parseRanking(String response, ElectionData electionData, ArrayList<RankingData> dataList) {
        response = clean(response);
        Document doc = Jsoup.parse(response);

        for (Element h2 : doc.select("h2")) {
            if (h2.text().equals("結果表")) {
                Element table = h2.nextElementSibling();
                while (!table.tagName().equals("table")) {
                    table = table.nextElementSibling();
                }

                int rankingIndex = 0;
                int breakingVoteIndex = 0;
                int finalVoteIndex = 0;
                int nameIndex = 0;
                int teamIndex = 0;
                int generationIndex = 0;
                int imageIndex = 0;
                boolean isFirstRow = true;

                for (Element rows : table.select("tr")) {
                    Elements cols = rows.children();
                    if (isFirstRow) {
                        for (int i = 0; i < cols.size(); i++) {
                            Element th = cols.get(i);
                            String thTitle = th.text().trim();
                            //Log.e(mTag, thTitle);
                            if (thTitle.equals("順位")) {
                                rankingIndex = i;
                            } else if (thTitle.equals("名前")) {
                                nameIndex = i;
                            } else if (thTitle.equals("期")) {
                                generationIndex = i;
                            } else if (thTitle.equals("速報順位")) {
                                breakingVoteIndex = i;
                            } else if (thTitle.equals("最終順位")) {
                                finalVoteIndex = i;
                            }
                        }
                        imageIndex = 1;
                        teamIndex = 2;
                    } else {
                        String ranking = cols.get(rankingIndex).text().trim();
                        String breakingVote = cols.get(breakingVoteIndex).text().trim();
                        String finalVote = cols.get(finalVoteIndex).text().trim();
                        String name = cols.get(nameIndex).text().trim();
                        String team = cols.get(teamIndex).text().trim();
                        String generation = cols.get(generationIndex).text().trim();
                        String imageUrl = "";

                        String src = cols.get(imageIndex).select("img").first().attr("src").trim();
                        //Log.e(mTag, "imageUrl: " + imageUrl);
                        if (src.contains("/50px-")) {
                            src = src.split("/50px-")[0].replace("/thumb", "");
                            imageUrl = mBaseUrl + src;
                        }

                        //Log.e(mTag, ranking + " / " + imageUrl + " / " + team + " / " + name + " / " + generation + " / " + breakingRanking + " / " + finalRanking);

                        RankingData rankingData = new RankingData();
                        rankingData.setElectionTitle(electionData.getTitle());
                        rankingData.setRanking(ranking);
                        rankingData.setBreakingVote(breakingVote);
                        rankingData.setFinalVote(finalVote);
                        rankingData.setName(name);
                        rankingData.setTeam(team);
                        rankingData.setGeneration(generation);
                        rankingData.setImageUrl(imageUrl);

                        dataList.add(rankingData);
                    }

                    isFirstRow = false;
                }
            }
        }
    }
}
