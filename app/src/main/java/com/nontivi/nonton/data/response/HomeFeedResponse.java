package com.nontivi.nonton.data.response;

import java.io.Serializable;

public class HomeFeedResponse implements Serializable {
    public int type;
    public Object object;
    public String title;
    public String desc;

    public HomeFeedResponse(int type) {
        this.type = type;
    }

    public HomeFeedResponse(int type, Object object) {
        this.type = type;
        this.object = object;
    }

    public HomeFeedResponse(int type, Object object, String title, String desc) {
        this.type = type;
        this.object = object;
        this.title = title;
        this.desc = desc;
    }

    public HomeFeedResponse() {
    }
}
