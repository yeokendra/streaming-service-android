package com.nontivi.nonton.features.genre;

import com.nontivi.nonton.data.DataManager;
import com.nontivi.nonton.features.base.BasePresenter;
import com.nontivi.nonton.features.search.SearchMvpView;
import com.nontivi.nonton.injection.ConfigPersistent;

import javax.inject.Inject;

@ConfigPersistent
public class GenrePresenter extends BasePresenter<GenreMvpView> {

    private final DataManager dataManager;

    @Inject
    public GenrePresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(GenreMvpView mvpView) {
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
