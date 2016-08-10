package com.summertaker.sakamichiguide.data;

import java.io.Serializable;
import java.util.Comparator;

public class VoteData implements Serializable, Comparable<VoteData> {

    private static final long serialVersionUID = 1L;

    int electionCount;
    int singleNumber;
    String team;
    String concurrentTeam;
    String name;
    String noSpaceName;
    String localeName;
    String furigana;
    String rank;
    String vote;
    String thumbnailUrl;
    MemberData memberData;

    public VoteData() {

    }

    public int getElectionCount() {
        return electionCount;
    }

    public void setElectionCount(int electionCount) {
        this.electionCount = electionCount;
    }

    public int getSingleNumber() {
        return singleNumber;
    }

    public void setSingleNumber(int singleNumber) {
        this.singleNumber = singleNumber;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getConcurrentTeam() {
        return concurrentTeam;
    }

    public void setConcurrentTeam(String concurrentTeam) {
        this.concurrentTeam = concurrentTeam;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getNoSpaceName() {
        return noSpaceName;
    }

    public void setNoSpaceName(String noSpaceName) {
        this.noSpaceName = noSpaceName;
    }

    public String getLocaleName() {
        return localeName;
    }

    public void setLocaleName(String localeName) {
        this.localeName = localeName;
    }

    public String getFurigana() {
        return furigana;
    }

    public void setFurigana(String furigana) {
        this.furigana = furigana;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public MemberData getMemberData() {
        return memberData;
    }

    public void setMemberData(MemberData memberData) {
        this.memberData = memberData;
    }

    @Override
    public int compareTo(VoteData another) {
        return rank.compareTo(another.rank);
    }

    public static Comparator<VoteData> compareToElectionCount = new Comparator<VoteData>() {

        public int compare(VoteData voteData1, VoteData voteData2) {
            int value1 = voteData1.getElectionCount();
            int value2 = voteData2.getElectionCount();

            //return value1 - value2; //ascending order
            return value2 - value1; //descending order
        }

    };
}
