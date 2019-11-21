package com.nontivi.nonton.features.streaming;

import com.nontivi.nonton.features.base.MvpView;

import java.util.List;

public interface StreamMvpView extends MvpView {

    //void showPokemon(List<String> pokemon);

    void showProgress(boolean show);

    void showError(Throwable error);
}
