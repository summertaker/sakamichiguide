package com.summertaker.sakamichiguide.data;

import java.io.Serializable;

public class ElectionData implements Serializable, Comparable<ElectionData> {

    private static final long serialVersionUID = 1L;

    private int count;
    private String countText;
    private String countSuffix;
    int singleNumber;
    private String title;
    private String catchPhrase;
    private String date;
    private String place;
    private String url;
    private String description;
    private int drawable;
    private String youtubeUrl;

    public ElectionData() {

    }

    public ElectionData(int count, int singleNumber, int drawable, String catchPhrase, String date, String place, String youtubeUrl) {
        this.count = count;
        this.countText = (count < 10) ? "0" + count : count + "";
        this.singleNumber = singleNumber;
        this.drawable = drawable;
        this.catchPhrase = catchPhrase;
        this.date = date;
        this.place = place;
        this.youtubeUrl = youtubeUrl;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getCountText() {
        return countText;
    }

    public void setCountText(String countText) {
        this.countText = countText;
    }

    public String getCountSuffix() {
        return countSuffix;
    }

    public void setCountSuffix(String countSuffix) {
        this.countSuffix = countSuffix;
    }

    public int getSingleNumber() {
        return singleNumber;
    }

    public void setSingleNumber(int singleNumber) {
        this.singleNumber = singleNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCatchPhrase() {
        return catchPhrase;
    }

    public void setCatchPhrase(String catchPhrase) {
        this.catchPhrase = catchPhrase;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    @Override
    public int compareTo(ElectionData another) {
        //return countText.compareTo(another.countText);

        return this.count - another.getCount(); //ascending order
        //return another.getCount() - this.count; //descending order
    }
}
