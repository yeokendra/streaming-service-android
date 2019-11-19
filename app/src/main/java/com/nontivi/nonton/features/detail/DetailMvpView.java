package com.nontivi.nonton.features.detail;

import com.nontivi.nonton.data.model.Pokemon;
import com.nontivi.nonton.data.model.Statistic;
import com.nontivi.nonton.features.base.MvpView;

public interface DetailMvpView extends MvpView {

    void showPokemon(Pokemon pokemon);

    void showStat(Statistic statistic);

    void showProgress(boolean show);

    void showError(Throwable error);
}
