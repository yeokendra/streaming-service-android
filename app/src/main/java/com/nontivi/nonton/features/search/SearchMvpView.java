package com.nontivi.nonton.features.search;

import com.nontivi.nonton.features.base.MvpView;

import java.util.List;

public interface SearchMvpView extends MvpView {

    void showProgress(boolean show);

    void showError(Throwable error);
}
