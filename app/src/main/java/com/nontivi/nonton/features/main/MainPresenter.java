package com.nontivi.nonton.features.main;

import javax.inject.Inject;

import com.nontivi.nonton.data.DataManager;
import com.nontivi.nonton.features.base.BasePresenter;
import com.nontivi.nonton.injection.ConfigPersistent;
import com.nontivi.nonton.util.rx.scheduler.SchedulerUtils;

@ConfigPersistent
public class MainPresenter extends BasePresenter<MainMvpView> {

    private final DataManager dataManager;

    @Inject
    public MainPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(MainMvpView mvpView) {
        super.attachView(mvpView);
    }

    public void getPokemon(int limit) {
        checkViewAttached();
        getView().showProgress(true);
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
