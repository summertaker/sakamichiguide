package com.summertaker.sakamichiguide.data;

import java.io.Serializable;

public class MenuData implements Serializable {

    private static final long serialVersionUID = 1L;

    String id;
    String title;
    String url;
    int drawable;

    public MenuData() {

    }

    public MenuData(String key, String title, int drawable) {
        this.id = key;
        this.title = title;
        this.drawable = drawable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
