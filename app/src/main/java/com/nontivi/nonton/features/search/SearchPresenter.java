package com.nontivi.nonton.features.search;

import com.nontivi.nonton.data.DataManager;
import com.nontivi.nonton.features.base.BasePresenter;
import com.nontivi.nonton.features.main.MainMvpView;
import com.nontivi.nonton.injection.ConfigPersistent;
import com.nontivi.nonton.util.rx.scheduler.SchedulerUtils;

import javax.inject.Inject;

@ConfigPersistent
public class SearchPresenter extends BasePresenter<SearchMvpView> {

    private final DataManager dataManager;

    @Inject
    public SearchPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(SearchMvpView mvpView) {
        super.attachView(mvpView);
    }

    public void getPokemon(int limit) {
        checkViewAttached();
//        getView().showProgress(true);
//        dataManager
//                .getPokemonList(limit)
//                .compose(SchedulerUtils.ioToMain())
//                .subscribe(
//                        pokemons -> {
//                            getView().showProgress(false);
//                            getView().showPokemon(pokemons);
//                        },
//                        throwable -> {
//                            getView().showProgress(false);
//                            getView().showError(throwable);
//                        });
    }
}
