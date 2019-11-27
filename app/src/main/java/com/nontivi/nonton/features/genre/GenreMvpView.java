package com.nontivi.nonton.features.genre;

import com.nontivi.nonton.features.base.MvpView;

public interface GenreMvpView extends MvpView {

    void showProgress(boolean show);

    void showError(Throwable error);
}
