package com.summertaker.sakamichiguide.data;

import java.io.Serializable;

public class YahooImageData  implements Serializable {

    private static final long serialVersionUID = 1L;

    String thumbnailUrl;
    String imageUrl;

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
