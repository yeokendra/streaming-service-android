package com.nontivi.nonton.data.model;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Favorite extends RealmObject {

    private RealmList<Channel> channels;

    public RealmList<Channel> getChannels() {
        return channels;
    }

    public void setChannels(RealmList<Channel> channels) {
        this.channels = channels;
    }
}
