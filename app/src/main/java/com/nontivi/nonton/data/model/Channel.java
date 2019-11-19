package com.nontivi.nonton.data.model;

import java.io.Serializable;
import java.util.List;

public class Channel implements Serializable {
    private int id;
    private String title;
    private String image_url;

    public Channel(int id, String title, String image_url){
        this.id = id;
        this.title = title;
        this.image_url = image_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
