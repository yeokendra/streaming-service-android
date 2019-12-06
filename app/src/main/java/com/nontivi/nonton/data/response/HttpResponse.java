package com.nontivi.nonton.data.response;

import com.nontivi.nonton.data.model.Setting;

import java.util.List;

public class HttpResponse<T> {

    private MetaResponse<T> meta;

    public MetaResponse<T> getMeta() {
        return meta;
    }

    public void setMeta(MetaResponse<T> meta) {
        this.meta = meta;
    }
}


