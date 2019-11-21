package com.nontivi.nonton.data.model;

import java.io.Serializable;

public class Schedule implements Serializable {
    private int id;
    private String title;
    private String subtitle;
    private long timestamp;
    private boolean isSelected = false;

    public Schedule(int id, String title, String subtitle, long timestamp){
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.timestamp = timestamp;
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

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
