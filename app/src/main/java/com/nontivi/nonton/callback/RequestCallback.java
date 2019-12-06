package com.nontivi.nonton.callback;


import com.nontivi.nonton.data.response.HttpResponse;

/**
 * Created by mac on 10/5/17.
 */

public interface RequestCallback<T> {
    void beforeRequest();

    void requestError(HttpResponse response);

    void requestComplete();

    void requestSuccess(T data);
}
