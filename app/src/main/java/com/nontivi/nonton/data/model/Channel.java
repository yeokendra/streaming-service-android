package com.nontivi.nonton.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;

public class Channel extends RealmObject implements Serializable{

    @PrimaryKey
    private int id;
    private String title;
    private String description;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("streaming_url")
    private String streamingUrl;
    @SerializedName("current_viewer")
    private int currentViewer;
    @SerializedName("is_trending")
    private boolean isTrending;
    @SerializedName("is_published")
    private boolean isPublished;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;

    @LinkingObjects("channels")
    private final RealmResults<ChannelContainer> channelContainer = null;

    public Channel(){
        id = -1;
        title = "";
        description = "";
        imageUrl = "";
        streamingUrl = "";
        currentViewer = -1;
        isTrending = false;
        isPublished = false;
        createdAt = "";
        updatedAt = "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStreamingUrl() {
        return streamingUrl;
    }

    public void setStreamingUrl(String streamingUrl) {
        this.streamingUrl = streamingUrl;
    }

    public int getCurrentViewer() {
        return currentViewer;
    }

    public void setCurrentViewer(int currentViewer) {
        this.currentViewer = currentViewer;
    }

    public boolean isTrending() {
        return isTrending;
    }

    public void setTrending(boolean trending) {
        isTrending = trending;
    }

    public boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(boolean isPublished) {
        this.isPublished = isPublished;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
