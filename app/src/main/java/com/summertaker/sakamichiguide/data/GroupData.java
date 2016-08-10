package com.summertaker.sakamichiguide.data;

import java.io.Serializable;

public class GroupData implements Serializable {

    private static final long serialVersionUID = 1L;

    String id;
    String name;
    int image;
    String url;
    String mobileUrl;
    String rawPhotoUrl;

    public GroupData() {

    }

    public GroupData(String id, String name, int image, String url, String mobileUrl, String rawPhotoUrl) {
        super();
        this.id = id;
        this.name = name;
        this.image = image;
        this.url = url;
        this.mobileUrl = mobileUrl;
        this.rawPhotoUrl = rawPhotoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getRawPhotoUrl() {
        return rawPhotoUrl;
    }

    public void setRawPhotoUrl(String rawPhotoUrl) {
        this.rawPhotoUrl = rawPhotoUrl;
    }

    public String getString() {
        return this.id + " / " + this.name + " / " + this.image + " / " + this.url + " / " + this.mobileUrl;
    }
}
