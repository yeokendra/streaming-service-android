package com.nontivi.nonton.injection.component;

import dagger.Subcomponent;
import com.nontivi.nonton.features.detail.DetailActivity;
import com.nontivi.nonton.features.genre.GenreActivity;
import com.nontivi.nonton.features.home.HomeActivity;
import com.nontivi.nonton.features.main.MainActivity;
import com.nontivi.nonton.features.search.SearchActivity;
import com.nontivi.nonton.features.streaming.StreamActivity;
import com.nontivi.nonton.injection.PerActivity;
import com.nontivi.nonton.injection.module.ActivityModule;

@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity mainActivity);

    void inject(DetailActivity detailActivity);

    void inject(HomeActivity homeActivity);

    void inject(StreamActivity streamActivity);

    void inject(SearchActivity searchActivity);

    void inject(GenreActivity genreActivity);
}
