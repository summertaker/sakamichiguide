package com.summertaker.sakamichiguide.data;

import java.io.Serializable;

public class RankingData implements Serializable {

    private static final long serialVersionUID = 1L;

    String electionTitle;
    String ranking;
    String rankingNumber;
    String rankingNumberSuffix;
    String breakingVote;
    String finalVote;
    String name;
    String team;
    String generation;
    String imageUrl;

    public String getElectionTitle() {
        return electionTitle;
    }

    public void setElectionTitle(String electionTitle) {
        this.electionTitle = electionTitle;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public String getRankingNumber() {
        return rankingNumber;
    }

    public void setRankingNumber(String rankingNumber) {
        this.rankingNumber = rankingNumber;
    }

    public String getRankingNumberSuffix() {
        return rankingNumberSuffix;
    }

    public void setRankingNumberSuffix(String rankingNumberSuffix) {
        this.rankingNumberSuffix = rankingNumberSuffix;
    }

    public String getBreakingVote() {
        return breakingVote;
    }

    public void setBreakingVote(String breakingVote) {
        this.breakingVote = breakingVote;
    }

    public String getFinalVote() {
        return finalVote;
    }

    public void setFinalVote(String finalVote) {
        this.finalVote = finalVote;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getGeneration() {
        return generation;
    }

    public void setGeneration(String generation) {
        this.generation = generation;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
