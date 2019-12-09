package com.nontivi.nonton.data.model;

import java.io.Serializable;
import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;

public class ChannelContainer extends RealmObject {

    public static final int ID_CHANNEL_ALL = 0;
    public static final int ID_CHANNEL_FAVORITES = 1;

    private int id;
    private RealmList<Channel> channels;

    public RealmList<Channel> getChannels() {
        return channels;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setChannels(RealmList<Channel> channels) {
        this.channels = channels;
    }

    public void setChannels(ArrayList<Channel> channels) {
        this.channels.clear();
        this.channels.addAll(channels);
    }

    public void addChannel(Channel channel){
        this.channels.add(channel);
    }

    public void removeChannelByPosition(int position){
        if(channels != null && channels.size() > position) {
            this.channels.remove(position);
        }
    }

    public int findChannelPositionById(int id){
        if(channels != null) {
            for (int i = 0; i < this.channels.size(); i++) {
                if (this.channels.get(i).getId() == id) {
                    return i;
                }
            }
            return -1;
        }else {
            return -1;
        }
    }
}
