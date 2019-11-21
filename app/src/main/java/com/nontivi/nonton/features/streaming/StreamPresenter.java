package com.nontivi.nonton.features.streaming;

import com.nontivi.nonton.data.DataManager;
import com.nontivi.nonton.features.base.BasePresenter;
import com.nontivi.nonton.features.home.HomeMvpView;
import com.nontivi.nonton.injection.ConfigPersistent;
import com.nontivi.nonton.util.rx.scheduler.SchedulerUtils;

import javax.inject.Inject;

@ConfigPersistent
public class StreamPresenter extends BasePresenter<StreamMvpView> {

    private final DataManager dataManager;

    @Inject
    public StreamPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(StreamMvpView mvpView) {
        super.attachView(mvpView);
    }

    public void getData(int limit) {
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
