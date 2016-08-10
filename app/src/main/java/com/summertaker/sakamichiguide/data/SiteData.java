package com.summertaker.sakamichiguide.data;

import java.io.Serializable;

public class SiteData implements Serializable {

    private static final long serialVersionUID = 1L;

    String id;
    String groupId;
    String serial;
    String name;
    String localeName;
    int image;
    String url;
    String mobileUrl;
    String rssUrl;
    String imageUrl;
    String blogUrl;
    String updateDate;
    String updateCheckDate;
    boolean isUpdated;
    boolean checked;

    public SiteData() {

    }

    public SiteData(String id, String groupId, String name, int image, String url, String mobileUrl, String rssUrl) {
        super();
        this.id = id;
        this.groupId = groupId;
        this.name = name;
        this.image = image;
        this.url = url;
        this.mobileUrl = mobileUrl;
        this.rssUrl = rssUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocaleName() {
        return localeName;
    }

    public void setLocaleName(String localeName) {
        this.localeName = localeName;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMobileUrl() {
        return mobileUrl;
    }

    public void setMobileUrl(String mobileUrl) {
        this.mobileUrl = mobileUrl;
    }

    public String getBlogUrl() {
        return blogUrl;
    }

    public void setBlogUrl(String blogUrl) {
        this.blogUrl = blogUrl;
    }

    public String getRssUrl() {
        return rssUrl;
    }

    public void setRssUrl(String rssUrl) {
        this.rssUrl = rssUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateCheckDate() {
        return updateCheckDate;
    }

    public void setUpdateCheckDate(String updateCheckDate) {
        this.updateCheckDate = updateCheckDate;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getString() {
        return this.groupId + " / " + this.name + " / " + this.image + " / " + this.url + " / " + this.rssUrl;
    }
}
