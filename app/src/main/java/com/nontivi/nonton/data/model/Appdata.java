package com.nontivi.nonton.data.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Appdata extends RealmObject {

    public static final long ID_DATA_WARNING = 0;
    public static final long ID_WALKTHROUGH = 1;

    private long id;
    private int value;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
