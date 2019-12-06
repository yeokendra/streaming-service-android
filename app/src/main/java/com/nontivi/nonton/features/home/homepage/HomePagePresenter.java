package com.nontivi.nonton.features.home.homepage;

import com.nontivi.nonton.data.DataManager;
import com.nontivi.nonton.features.base.BasePresenter;
import com.nontivi.nonton.features.home.HomeMvpView;
import com.nontivi.nonton.injection.ConfigPersistent;
import com.nontivi.nonton.util.rx.scheduler.SchedulerUtils;

import javax.inject.Inject;

@ConfigPersistent
public class HomePagePresenter extends BasePresenter<HomePageMvpView> {

    private final DataManager dataManager;

    @Inject
    public HomePagePresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(HomePageMvpView mvpView) {
        super.attachView(mvpView);
    }

    public void getChannels() {
        checkViewAttached();
        getView().showProgress(true);
        dataManager
                .getChannelList()
                .compose(SchedulerUtils.ioToMain())
                .subscribe(
                        channels -> {
                            getView().showProgress(false);
                            getView().showChannelList(channels);
                        },
                        throwable -> {
                            getView().showProgress(false);
                            getView().showError(throwable);
                        });
    }

    public void getGenres() {
        checkViewAttached();
        getView().showProgress(true);
        dataManager
                .getGenreList()
                .compose(SchedulerUtils.ioToMain())
                .subscribe(
                        genres -> {
                            getView().showProgress(false);
                            getView().showGenreList(genres);
                        },
                        throwable -> {
                            getView().showProgress(false);
                            getView().showError(throwable);
                        });
    }
}
