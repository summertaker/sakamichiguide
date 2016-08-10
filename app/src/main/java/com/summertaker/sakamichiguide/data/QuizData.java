package com.summertaker.sakamichiguide.data;

import java.io.Serializable;

public class QuizData implements Serializable {

    private static final long serialVersionUID = 1L;

    String id;
    String title;
    int image;

    public QuizData(String id, String title, int image) {
        this.id = id;
        this.title = title;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
