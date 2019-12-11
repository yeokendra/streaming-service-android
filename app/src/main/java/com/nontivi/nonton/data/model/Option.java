package com.nontivi.nonton.data.model;

import androidx.annotation.DrawableRes;

import io.realm.RealmModel;

public class Option {

    private int id;
    private String name;
    private int drawableId;

    public Option(int id, String name, @DrawableRes int drawableId) {
        setId(id);
        setName(name);
        setDrawableId(drawableId);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }
}
